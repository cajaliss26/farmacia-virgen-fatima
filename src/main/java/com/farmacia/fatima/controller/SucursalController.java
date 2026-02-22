package com.farmacia.fatima.controller;

import com.farmacia.fatima.model.Sucursal;
import com.farmacia.fatima.model.dto.SucursalRequest;
import com.farmacia.fatima.model.dto.SucursalResponse;
import com.farmacia.fatima.service.SucursalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sucursales")
@CrossOrigin(origins = "*")
public class SucursalController {

    private final SucursalService service;

    public SucursalController(SucursalService service) {
        this.service = service;
    }

    // GET
    @GetMapping
    public ResponseEntity<List<SucursalResponse>> listar() {
        List<SucursalResponse> lista = service.listar().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    // GET
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            Sucursal sucursal = service.buscarPorId(id);
            return ResponseEntity.ok(toResponse(sucursal));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // POST
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody SucursalRequest req, Authentication auth) {
        if (!tieneRolAdmin(auth)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado");
        }

        try {
            Sucursal sucursal = service.crear(req);
            return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(sucursal));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // PUT
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody SucursalRequest req, Authentication auth) {
        if (!tieneRolAdmin(auth)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado");
        }

        try {
            Sucursal sucursal = service.actualizar(id, req);
            return ResponseEntity.ok(toResponse(sucursal));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id, Authentication auth) {
        if (!tieneRolAdmin(auth)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado");
        }

        try {
            service.eliminar(id);
            return ResponseEntity.ok("Sucursal eliminada correctamente");
        } catch (Exception e) {
            String mensaje = e.getMessage() != null ? e.getMessage() : "Error al eliminar la sucursal";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensaje);
        }
    }

    private boolean tieneRolAdmin(Authentication auth) {
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equalsIgnoreCase("ROLE_ADMIN"));
    }

    // Conversión a DTO
    private SucursalResponse toResponse(Sucursal s) {
        return new SucursalResponse(
                s.getId(),
                s.getNombre(),
                s.getDistrito(),
                s.getDireccion(),
                s.getTelefono()
        );
    }
}
