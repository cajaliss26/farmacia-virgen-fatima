package com.farmacia.fatima.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "reservas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sucursal_id", nullable = false)
    private Sucursal sucursal;

    @Column(name = "fecha_reserva", nullable = false)
    private LocalDate fechaReserva; // 📅 fecha elegida por el cliente

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio; // ⏰ hora de inicio

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin; // ⏰ hora de fin

    @Column(name = "numero_personas", nullable = false)
    private Integer numeroPersonas;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoReserva estado = EstadoReserva.PENDIENTE;

    @Column(precision = 10, scale = 2)
    private BigDecimal total;

    public enum EstadoReserva {
        PENDIENTE,
        CONFIRMADA,
        ENTREGADA,
        CANCELADA
    }
}
