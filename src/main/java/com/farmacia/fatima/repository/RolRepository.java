package com.farmacia.fatima.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.farmacia.fatima.model.Rol;

import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByNombre(String nombre);
}
