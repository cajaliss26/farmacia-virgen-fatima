package com.farmacia.fatima.repository;

import com.farmacia.fatima.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repositorio para operaciones de base de datos con Reservas
 */
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    
    // Buscar todas las reservas de un usuario específico
    List<Reserva> findByUsuarioId(Long usuarioId);
    
    // Buscar reservas por sucursal
    List<Reserva> findBySucursalId(Long sucursalId);
    
    // Buscar reservas de un usuario en una sucursal específica
    List<Reserva> findByUsuarioIdAndSucursalId(Long usuarioId, Long sucursalId);
    
    // Buscar reservas por estado
    List<Reserva> findByEstado(Reserva.EstadoReserva estado);
    
    // Query personalizada: obtener reservas pendientes de un usuario
    @Query("SELECT r FROM Reserva r WHERE r.usuario.id = :usuarioId AND r.estado = 'PENDIENTE' ORDER BY r.fechaReserva DESC")
    List<Reserva> findReservasPendientesByUsuario(@Param("usuarioId") Long usuarioId);

    boolean existsBySucursalId(Long sucursalId);
}