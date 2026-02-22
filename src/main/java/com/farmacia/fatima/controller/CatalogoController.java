package com.farmacia.fatima.controller;

import com.farmacia.fatima.model.dto.CatalogoProductoResponse;
import com.farmacia.fatima.model.dto.SucursalResponse;
import com.farmacia.fatima.service.CatalogoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/catalogo")
@CrossOrigin(origins = "*")
public class CatalogoController {
    
    private final CatalogoService catalogoService;
    
    public CatalogoController(CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }
    

    @GetMapping("/productos")
    public ResponseEntity<List<CatalogoProductoResponse>> listarProductos() {
        List<CatalogoProductoResponse> productos = catalogoService.listarProductosActivos();
        return ResponseEntity.ok(productos);
    }
    

    @GetMapping("/productos/sucursal/{sucursalId}")
    public ResponseEntity<List<CatalogoProductoResponse>> listarProductosPorSucursal(
            @PathVariable Long sucursalId) {
        List<CatalogoProductoResponse> productos = 
                catalogoService.listarProductosPorSucursal(sucursalId);
        return ResponseEntity.ok(productos);
    }
    

    @GetMapping("/sucursales")
    public ResponseEntity<List<SucursalResponse>> listarSucursales() {
        List<SucursalResponse> sucursales = catalogoService.listarSucursales();
        return ResponseEntity.ok(sucursales);
    }
}