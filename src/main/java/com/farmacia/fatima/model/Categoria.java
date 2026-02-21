package com.farmacia.fatima.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "categorias")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String nombre;

    @Column(length = 150)
    private String descripcion;

    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, orphanRemoval = false)
    @ToString.Exclude
    @Builder.Default
    private List<Producto> productos = List.of();
}
