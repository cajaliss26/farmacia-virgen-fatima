package com.farmacia.fatima.model;

import jakarta.persistence.*;
import lombok.*;

@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StockSucursal {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false) private Sucursal sucursal;
    @ManyToOne(optional=false) private Producto producto;

    private Integer stockActual = 0;
    private Integer stockMinimo = 0;
}
