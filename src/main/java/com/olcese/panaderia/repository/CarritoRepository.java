package com.olcese.panaderia.repository;

import com.olcese.panaderia.model.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarritoRepository extends JpaRepository<Carrito, Long> {


    Optional<Carrito> findFirstByUsuario_IdAndActivoTrueOrderByIdDesc(Long usuarioId);

}