package com.farmacia.fatima.model.dto;

import jakarta.validation.constraints.*;

public record ProductoRequest(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 120)
        String nombre,

        @NotNull(message = "Debe especificar el ID de la categoría")
        Long categoriaId,

        @NotNull(message = "Debe especificar el ID de la sucursal")
        Long sucursalId,

        @NotNull(message = "Debe indicar el precio")
        @Positive(message = "El precio debe ser mayor que cero")
        Double precio,

        @NotNull(message = "Debe indicar si el producto está activo")
        Boolean activo
) {}
