package com.farmacia.fatima.model.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class SucursalRequest {

    @NotBlank
    private String nombre;

    private String distrito;
    private String direccion;
    private String telefono;
    
    public String getNombre() { return nombre; }
    public String getDistrito() { return distrito; }
    public String getDireccion() { return direccion; }
    public String getTelefono() { return telefono; }

    
}
