package com.farmacia.fatima.model;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "pedidos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Pedido {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Usuario usuario;

    @OneToOne(fetch = FetchType.LAZY)
    private Carrito carritoOrigen;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPedido estado = EstadoPedido.CREADO;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal total;

    @Column(name = "creado_en", updatable = false)
    @CreationTimestamp
    private LocalDateTime creadoEn = LocalDateTime.now();

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PedidoItem> items = new ArrayList<>();

    public enum EstadoPedido { CREADO, PAGADO, PREPARANDO, DESPACHADO, ENTREGADO, CANCELADO }
}
