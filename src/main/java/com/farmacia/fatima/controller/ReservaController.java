package com.farmacia.fatima.controller;

import com.farmacia.fatima.repository.UsuarioRepository;
import com.farmacia.fatima.model.dto.ReservaRequest;
import com.farmacia.fatima.model.dto.ReservaResponse;
import com.farmacia.fatima.service.ReservaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@CrossOrigin(origins = "*")
public class ReservaController {
    
    private final ReservaService reservaService;
    private final UsuarioRepository usuarioRepository;
    
    public ReservaController(ReservaService reservaService,
                             UsuarioRepository usuarioRepository) {
        this.reservaService = reservaService;
        this.usuarioRepository = usuarioRepository;
    }
    

    @PostMapping
    public ResponseEntity<?> crearReserva(
            @Valid @RequestBody ReservaRequest request,
            Authentication authentication) {
        
        try {
            // Obtener el ID del usuario desde el JWT
            Long usuarioId = obtenerUsuarioIdDesdeAuth(authentication);
            
            // Crear la reserva
            ReservaResponse reserva = reservaService.crearReserva(usuarioId, request);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(reserva);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al crear reserva: " + e.getMessage());
        }
    }

    @GetMapping("/mis-reservas")
    public ResponseEntity<List<ReservaResponse>> obtenerMisReservas(Authentication authentication) {
        Long usuarioId = obtenerUsuarioIdDesdeAuth(authentication);
        List<ReservaResponse> reservas = reservaService.obtenerReservasUsuario(usuarioId);
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerReserva(
            @PathVariable Long id,
            Authentication authentication) {
        
        try {
            Long usuarioId = obtenerUsuarioIdDesdeAuth(authentication);
            ReservaResponse reserva = reservaService.buscarReservaPorId(id);
            
            // Validar que la reserva pertenece al usuario (o es admin)
            if (!reserva.usuarioId().equals(usuarioId) && !esAdmin(authentication)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("No tienes permiso para ver esta reserva");
            }
            
            return ResponseEntity.ok(reserva);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Reserva no encontrada: " + e.getMessage());
        }
    }
    

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarReserva(
            @PathVariable Long id,
            Authentication authentication) {
        
        try {
            Long usuarioId = obtenerUsuarioIdDesdeAuth(authentication);
            ReservaResponse reserva = reservaService.cancelarReserva(id, usuarioId);
            
            return ResponseEntity.ok(reserva);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al cancelar reserva: " + e.getMessage());
        }
    }

    @GetMapping("/admin/todas")
    public ResponseEntity<?> listarTodasLasReservas(Authentication authentication) {
        // Validar que es admin
        if (!esAdmin(authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acceso denegado: solo administradores");
        }
        
        List<ReservaResponse> reservas = reservaService.listarTodasLasReservas();
        return ResponseEntity.ok(reservas);
    }
    
    // ========== MÉTODOS AUXILIARES ==========

    private Long obtenerUsuarioIdDesdeAuth(Authentication authentication) {
        // El email está subject del JWT
        String email = ((UserDetails) authentication.getPrincipal()).getUsername();

        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"))
                .getId();
    }

    private boolean esAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equalsIgnoreCase("ROLE_ADMIN"));
    }
}