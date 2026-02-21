package com.farmacia.fatima.model.dto;

public record CarritoUpdateQtyRequest(
        Long itemId,
        Integer cantidad
) {}