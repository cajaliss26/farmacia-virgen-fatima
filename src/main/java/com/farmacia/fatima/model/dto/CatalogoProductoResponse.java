package com.farmacia.fatima.model.dto;

/**
 * DTO simplificado para mostrar productos en el catálogo público
 * No incluye información sensible de gestión
 */
public record CatalogoProductoResponse(
    Long id,
    String nombre,
    String categoria,
    String sucursal,
    String distrito,         // Distrito de la sucursal
    Double precio,
    Boolean disponible       // Indica si está activo
) {}