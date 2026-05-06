package com.portafolio.subastas.infrastructure.scheduler;

import com.portafolio.subastas.application.usecase.StartAuctionsUseCase;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AuctionScheduler {

    private final StartAuctionsUseCase startAuctionsUseCase;

    public AuctionScheduler(StartAuctionsUseCase startAuctionsUseCase) {
        this.startAuctionsUseCase = startAuctionsUseCase;
    }

    // Cada 10 seg.
    @Scheduled(fixedRate = 10000)
    public void checkAndStartAuctions() {
        startAuctionsUseCase.execute();
    }
}
