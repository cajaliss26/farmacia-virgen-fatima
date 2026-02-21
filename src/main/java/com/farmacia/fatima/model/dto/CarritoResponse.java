package com.farmacia.fatima.model.dto;


import java.math.BigDecimal;
import java.util.List;

public record CarritoResponse(
        Long id,
        Boolean activo,
        BigDecimal total,
        List<Item> items
) {
    public record Item(
            Long itemId,
            Long productoId,
            String nombreProducto,
            String sucursal,
            Integer cantidad,
            BigDecimal precioUnitario,
            BigDecimal subtotal
    ) {}
}