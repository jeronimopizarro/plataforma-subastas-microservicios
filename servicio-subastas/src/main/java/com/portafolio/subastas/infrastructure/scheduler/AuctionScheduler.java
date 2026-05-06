package com.portafolio.subastas.infrastructure.scheduler;

import com.portafolio.subastas.application.usecase.FinishAuctionsUseCase;
import com.portafolio.subastas.application.usecase.StartAuctionsUseCase;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AuctionScheduler {

    private final StartAuctionsUseCase startAuctionsUseCase;
    private final FinishAuctionsUseCase finishAuctionsUseCase;

    public AuctionScheduler(StartAuctionsUseCase startAuctionsUseCase,
                            FinishAuctionsUseCase finishAuctionsUseCase) {
        this.startAuctionsUseCase = startAuctionsUseCase;
        this.finishAuctionsUseCase = finishAuctionsUseCase;
    }

    // Cada 10 seg.
    @Scheduled(fixedRate = 10000)
    public void checkAndStartAuctions() {
        startAuctionsUseCase.execute();

        finishAuctionsUseCase.execute();
    }
}
