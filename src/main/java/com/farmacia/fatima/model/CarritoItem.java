package com.farmacia.fatima.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity @Table(name = "carrito_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CarritoItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Carrito carrito;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Producto producto;

    // snapshot de precio al momento de agregar
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal precioUnitario;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal subtotal;
}
