package com.portafolio.subastas.application.usecase;

import com.portafolio.subastas.application.util.AuctionStateProcessor;
import com.portafolio.subastas.domain.entity.Auction;
import com.portafolio.subastas.domain.entity.Product;
import com.portafolio.subastas.domain.repository.AuctionRepository;
import com.portafolio.subastas.domain.repository.ProductRepository;
import com.portafolio.subastas.infrastructure.client.WalletFeignClient;
import com.portafolio.subastas.infrastructure.client.dto.TransactionRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FinishAuctionsUseCase {

    private final AuctionRepository auctionRepository;
    private final ProductRepository productRepository;
    private final WalletFeignClient walletClient;
    private final AuctionStateProcessor stateProcessor;
    private static final Logger logger = LoggerFactory.getLogger(FinishAuctionsUseCase.class);

    // Inyectamos ProductRepository y WalletFeignClient
    public FinishAuctionsUseCase(AuctionRepository auctionRepository,
                                 ProductRepository productRepository,
                                 WalletFeignClient walletClient,
                                 AuctionStateProcessor stateProcessor) {
        this.auctionRepository = auctionRepository;
        this.productRepository = productRepository;
        this.walletClient = walletClient;
        this.stateProcessor = stateProcessor;
    }

    public void execute() {
        // 1. Buscar las que superaron la fecha de fin
        List<Auction> pendingToFinish = auctionRepository.findAuctionsToFinish(LocalDateTime.now());

        // 2. Procesarlas en lote
        stateProcessor.processBatch(
                pendingToFinish,
                auction -> {
                    // Cambiamos el estado de la subasta a FINALIZADA
                    auction.finish(auction.getWinnerId(), auction.getCurrentHighestBid());

                    // --- LÓGICA DE TRANSFERENCIA DE FONDOS ---
                    if (auction.getWinnerId() != null) {
                        try {
                            // A. Buscamos el producto para saber quién fue el vendedor
                            Product product = productRepository.findById(auction.getProductId())
                                    .orElseThrow(() -> new RuntimeException("Producto no encontrado para la subasta: " + auction.getId()));

                            Long sellerId = product.getSellerId();

                            // B. Cobrar al ganador: Confirmamos la extracción de los fondos retenidos
                            walletClient.commitFunds(
                                    auction.getWinnerId(),
                                    new TransactionRequest(auction.getCurrentHighestBid(), "Pago por subasta ganada: #" + auction.getId())
                            );

                            // C. Pagar al vendedor: Añadimos saldo disponible a su cuenta
                            walletClient.depositFunds(
                                    String.valueOf(sellerId),
                                    new TransactionRequest(auction.getCurrentHighestBid(), "Cobro por venta en subasta: #" + auction.getId())
                            );

                            logger.info("Transferencia exitosa: Subasta {}, Ganador {}, Vendedor {}, Monto ${}",
                                    auction.getId(), auction.getWinnerId(), sellerId, auction.getCurrentHighestBid());
                        } catch (Exception e) {
                            logger.error("Error crítico al transferir fondos para la subasta {}: {}", auction.getId(), e.getMessage());
                            throw new RuntimeException("Error en la transferencia de fondos", e);
                        }
                    }
                },
                "finalizada"
        );
    }
}