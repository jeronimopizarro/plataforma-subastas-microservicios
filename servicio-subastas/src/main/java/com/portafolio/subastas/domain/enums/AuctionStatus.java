package com.portafolio.subastas.domain.enums;

public enum AuctionStatus {
    DRAFT,      // Creada pero aún no comenzó el periodo de pujas
    ACTIVE,     // Periodo de pujas abierto
    FINISHED,   // El tiempo terminó, hay un ganador (o quedó desierta)
    CANCELLED   // El administrador la bajó manualmente
}
