package com.farmacia.fatima.service.Impl;

import com.farmacia.fatima.model.Reserva;
import com.farmacia.fatima.model.Sucursal;
import com.farmacia.fatima.model.Usuario;
import com.farmacia.fatima.model.dto.ReservaRequest;
import com.farmacia.fatima.model.dto.ReservaResponse;
import com.farmacia.fatima.repository.ReservaRepository;
import com.farmacia.fatima.repository.SucursalRepository;
import com.farmacia.fatima.repository.UsuarioRepository;
import com.farmacia.fatima.service.ReservaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReservaServiceImpl implements ReservaService {

    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final SucursalRepository sucursalRepository;

    public ReservaServiceImpl(ReservaRepository reservaRepository,
                              UsuarioRepository usuarioRepository,
                              SucursalRepository sucursalRepository) {
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
        this.sucursalRepository = sucursalRepository;
    }

    @Override
    public ReservaResponse crearReserva(Long usuarioId, ReservaRequest request) {
        // 1️⃣ Validar usuario
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2️⃣ Validar sucursal
        Sucursal sucursal = sucursalRepository.findById(request.sucursalId())
                .orElseThrow(() -> new RuntimeException("Sucursal no encontrada con ID: " + request.sucursalId()));

        // 3️⃣ Validar coherencia de horarios
        if (request.horaFin().isBefore(request.horaInicio()) || request.horaFin().equals(request.horaInicio())) {
            throw new RuntimeException("La hora de fin debe ser posterior a la hora de inicio");
        }

        // 4️⃣ Calcular total automático (ejemplo: 4 soles por persona)
        BigDecimal precioPorPersona = new BigDecimal("4.00");
        BigDecimal total = precioPorPersona.multiply(BigDecimal.valueOf(request.numeroPersonas()));

        // 5️⃣ Crear la entidad Reserva con los nuevos campos
        Reserva reserva = Reserva.builder()
                .usuario(usuario)
                .sucursal(sucursal)
                .fechaReserva(request.fechaReserva())
                .horaInicio(request.horaInicio())
                .horaFin(request.horaFin())
                .numeroPersonas(request.numeroPersonas())
                .estado(Reserva.EstadoReserva.PENDIENTE)
                .total(total)
                .build();

        // 6️⃣ Guardar en base de datos
        Reserva reservaGuardada = reservaRepository.save(reserva);

        // 7️⃣ Retornar la respuesta DTO
        return convertirAResponse(reservaGuardada);
    }

    @Override
    public List<ReservaResponse> obtenerReservasUsuario(Long usuarioId) {
        return reservaRepository.findByUsuarioId(usuarioId).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ReservaResponse buscarReservaPorId(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + id));
        return convertirAResponse(reserva);
    }

    @Override
    public ReservaResponse cancelarReserva(Long id, Long usuarioId) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        if (!reserva.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("No tienes permiso para cancelar esta reserva");
        }

        if (reserva.getEstado() == Reserva.EstadoReserva.ENTREGADA) {
            throw new RuntimeException("No se puede cancelar una reserva ya entregada");
        }

        reserva.setEstado(Reserva.EstadoReserva.CANCELADA);
        Reserva actualizada = reservaRepository.save(reserva);
        return convertirAResponse(actualizada);
    }

    @Override
    public List<ReservaResponse> listarTodasLasReservas() {
        return reservaRepository.findAll().stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    // 🔹 Conversión a DTO
    private ReservaResponse convertirAResponse(Reserva r) {
        return new ReservaResponse(
                r.getId(),
                r.getUsuario().getId(),
                r.getUsuario().getNombre() + " " + r.getUsuario().getApellido(),
                r.getSucursal().getId(),
                r.getSucursal().getNombre(),
                r.getSucursal().getDireccion(),
                r.getFechaReserva(),
                r.getHoraInicio(),
                r.getHoraFin(),
                r.getNumeroPersonas(),
                r.getEstado().name(),
                r.getTotal()
        );
    }
}
