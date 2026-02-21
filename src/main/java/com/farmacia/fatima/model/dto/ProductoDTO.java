package com.farmacia.fatima.model.dto;

public record ProductoDTO(
        Long id,
        String nombre,
        SucursalDTO sucursal
) {}