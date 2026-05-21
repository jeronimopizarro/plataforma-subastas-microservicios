package com.portafolio.wallet.web.mapper;

import com.portafolio.wallet.domain.entity.Wallet;
import com.portafolio.wallet.web.dto.WalletResponse;
import org.springframework.stereotype.Component;

@Component
public class WalletResponseMapper {

    public WalletResponse toResponse(Wallet wallet) {
        if (wallet == null) {
            return null;
        }

        return new WalletResponse(
                wallet.getId(),
                wallet.getUserId(),
                wallet.getAvailableBalance(),
                wallet.getHeldFunds()
        );
    }
}