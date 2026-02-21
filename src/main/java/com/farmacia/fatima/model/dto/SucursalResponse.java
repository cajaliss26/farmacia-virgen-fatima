package com.farmacia.fatima.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SucursalResponse {

    private Long id;
    private String nombre;
    private String distrito;
    private String direccion;
    private String telefono;
}
