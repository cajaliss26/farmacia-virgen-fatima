package com.farmacia.fatima.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "sucursales")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sucursal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String nombre;

    @Column(length = 60)
    private String distrito;

    @Column(length = 120)
    private String direccion;

    @Column(length = 20)
    private String telefono;

    @OneToMany(mappedBy = "sucursal", cascade = CascadeType.ALL, orphanRemoval = false)
    @ToString.Exclude
    @Builder.Default
    private List<Producto> productos = List.of();
}
