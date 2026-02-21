package com.farmacia.fatima.controller;

import com.farmacia.fatima.model.dto.ProductoRequest;
import com.farmacia.fatima.model.dto.ProductoResponse;
import com.farmacia.fatima.model.Producto;
import com.farmacia.fatima.service.ProductoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService service;

    public ProductoController(ProductoService service) {
        this.service = service;
    }

    // GET /api/productos - Listar todos
    @GetMapping
    public ResponseEntity<List<ProductoResponse>> listar() {
        var lista = service.listar().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    // GET /api/productos/{id} - Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            var producto = service.buscarPorId(id);
            return ResponseEntity.ok(toResponse(producto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // POST /api/productos - Crear nuevo (solo admin)
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody ProductoRequest req, Authentication auth) {
        if (!tieneRolAdmin(auth)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado");
        }

        try {
            var producto = service.crear(req);
            return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(producto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // PUT /api/productos/{id} - Actualizar (solo admin)
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody ProductoRequest req, Authentication auth) {
        if (!tieneRolAdmin(auth)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado");
        }

        try {
            var producto = service.actualizar(id, req);
            return ResponseEntity.ok(toResponse(producto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // DELETE /api/productos/{id} - Eliminar (solo admin)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id, Authentication auth) {
        if (!tieneRolAdmin(auth)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado");
        }

        try {
            service.eliminar(id);
            return ResponseEntity.ok("Producto eliminado correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // PATCH /api/productos/{id}/estado - Cambiar estado activo/inactivo (solo admin)
    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable Long id, Authentication auth) {
        if (!tieneRolAdmin(auth)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado");
        }

        try {
            var producto = service.cambiarEstado(id);
            return ResponseEntity.ok(toResponse(producto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Método auxiliar para validar rol admin
    private boolean tieneRolAdmin(Authentication auth) {
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equalsIgnoreCase("ROLE_ADMIN"));
    }

    // Conversión a DTO de respuesta
    private ProductoResponse toResponse(Producto p) {
        return new ProductoResponse(
                p.getId(),
                p.getNombre(),
                p.getCategoria() != null ? p.getCategoria().getNombre() : null,
                p.getSucursal() != null ? p.getSucursal().getNombre() : null,
                p.getPrecio().doubleValue(),
                p.getActivo()
        );
    }
}
