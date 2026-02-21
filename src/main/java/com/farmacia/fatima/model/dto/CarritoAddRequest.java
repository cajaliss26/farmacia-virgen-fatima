package com.farmacia.fatima.model.dto;

public record CarritoAddRequest(
        Long productoId,
        Integer cantidad
) {}
