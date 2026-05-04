package com.portafolio.subastas.domain.exception;

public enum ErrorCode {
    NOT_FOUND,       // Para cuando no encontramos algo en la BD
    BAD_REQUEST,     // Para reglas de negocio rotas (ej: precio inválido)
    CONFLICT         // Para reglas que chocan (ej: la subasta ya terminó)
}
