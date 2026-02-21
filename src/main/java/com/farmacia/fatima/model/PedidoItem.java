package com.farmacia.fatima.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity @Table(name = "pedido_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PedidoItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Pedido pedido;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Producto producto;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal precioUnitario;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal subtotal;
}
