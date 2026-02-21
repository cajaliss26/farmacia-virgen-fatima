package com.farmacia.fatima.service;

import com.farmacia.fatima.model.dto.ProductoRequest;
import com.farmacia.fatima.model.Categoria;
import com.farmacia.fatima.model.Producto;
import com.farmacia.fatima.model.Sucursal;
import com.farmacia.fatima.repository.CategoriaRepository;
import com.farmacia.fatima.repository.ProductoRepository;
import com.farmacia.fatima.repository.SucursalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final SucursalRepository sucursalRepository;

    public ProductoService(ProductoRepository productoRepository,
                           CategoriaRepository categoriaRepository,
                           SucursalRepository sucursalRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.sucursalRepository = sucursalRepository;
    }

    // 🔹 Listar todos
    public List<Producto> listar() {
        return productoRepository.findAll();
    }

    // 🔹 Buscar por ID
    public Producto buscarPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
    }

    // 🔹 Crear nuevo producto
    public Producto crear(ProductoRequest request) {
        if (request.categoriaId() == null || request.sucursalId() == null) {
            throw new IllegalArgumentException("Debe especificar IDs válidos de categoría y sucursal");
        }

        Categoria categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + request.categoriaId()));

        Sucursal sucursal = sucursalRepository.findById(request.sucursalId())
                .orElseThrow(() -> new RuntimeException("Sucursal no encontrada con ID: " + request.sucursalId()));

        Producto producto = Producto.builder()
                .nombre(request.nombre().trim())
                .categoria(categoria)
                .sucursal(sucursal)
                .precio(BigDecimal.valueOf(request.precio()))
                .activo(request.activo())
                .build();

        return productoRepository.save(producto);
    }

    // 🔹 Actualizar producto existente
    public Producto actualizar(Long id, ProductoRequest request) {
        Producto producto = buscarPorId(id);

        Categoria categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + request.categoriaId()));

        Sucursal sucursal = sucursalRepository.findById(request.sucursalId())
                .orElseThrow(() -> new RuntimeException("Sucursal no encontrada con ID: " + request.sucursalId()));

        producto.setNombre(request.nombre().trim());
        producto.setCategoria(categoria);
        producto.setSucursal(sucursal);
        producto.setPrecio(BigDecimal.valueOf(request.precio()));
        producto.setActivo(request.activo());

        return productoRepository.save(producto);
    }

    // 🔹 Eliminar producto
    public void eliminar(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new RuntimeException("Producto no encontrado con ID: " + id);
        }
        productoRepository.deleteById(id);
    }

    // 🔹 Cambiar estado activo/inactivo
    public Producto cambiarEstado(Long id) {
        Producto producto = buscarPorId(id);
        producto.setActivo(!producto.getActivo());
        return productoRepository.save(producto);
    }
}
