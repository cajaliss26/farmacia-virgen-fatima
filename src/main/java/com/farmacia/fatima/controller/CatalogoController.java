package com.farmacia.fatima.controller;

import com.farmacia.fatima.model.dto.CatalogoProductoResponse;
import com.farmacia.fatima.model.dto.SucursalResponse;
import com.farmacia.fatima.service.CatalogoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para el catálogo público de productos
 * Accesible para todos los usuarios (no requiere autenticación)
 */
@RestController
@RequestMapping("/api/catalogo")
@CrossOrigin(origins = "*")
public class CatalogoController {
    
    private final CatalogoService catalogoService;
    
    public CatalogoController(CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }
    
    /**
     * GET /api/catalogo/productos
     * Lista todos los productos activos del catálogo
     * Acceso: Público (no requiere autenticación)
     */
    @GetMapping("/productos")
    public ResponseEntity<List<CatalogoProductoResponse>> listarProductos() {
        List<CatalogoProductoResponse> productos = catalogoService.listarProductosActivos();
        return ResponseEntity.ok(productos);
    }
    
    /**
     * GET /api/catalogo/productos/sucursal/{sucursalId}
     * Lista productos de una sucursal específica
     * Acceso: Público
     */
    @GetMapping("/productos/sucursal/{sucursalId}")
    public ResponseEntity<List<CatalogoProductoResponse>> listarProductosPorSucursal(
            @PathVariable Long sucursalId) {
        List<CatalogoProductoResponse> productos = 
                catalogoService.listarProductosPorSucursal(sucursalId);
        return ResponseEntity.ok(productos);
    }
    
    /**
     * GET /api/catalogo/sucursales
     * Lista todas las sucursales disponibles
     * Acceso: Público
     */
    @GetMapping("/sucursales")
    public ResponseEntity<List<SucursalResponse>> listarSucursales() {
        List<SucursalResponse> sucursales = catalogoService.listarSucursales();
        return ResponseEntity.ok(sucursales);
    }
}