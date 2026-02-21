package com.farmacia.fatima.service;

import com.farmacia.fatima.model.dto.ReservaRequest;
import com.farmacia.fatima.model.dto.ReservaResponse;

import java.util.List;

/**
 * Interfaz del servicio de reservas
 * Define los métodos para gestionar reservas de clientes
 */
public interface ReservaService {
    
    // Crear una nueva reserva
    ReservaResponse crearReserva(Long usuarioId, ReservaRequest request);
    
    // Obtener todas las reservas de un usuario
    List<ReservaResponse> obtenerReservasUsuario(Long usuarioId);
    
    // Buscar una reserva específica por ID
    ReservaResponse buscarReservaPorId(Long id);
    
    // Cancelar una reserva (cambiar estado a CANCELADA)
    ReservaResponse cancelarReserva(Long id, Long usuarioId);
    
    // Listar todas las reservas (para admin)
    List<ReservaResponse> listarTodasLasReservas();
}