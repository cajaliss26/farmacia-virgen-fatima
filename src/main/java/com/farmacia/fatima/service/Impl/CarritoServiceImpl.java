package com.farmacia.fatima.service.Impl;

import com.farmacia.fatima.model.*;
import com.farmacia.fatima.repository.CarritoRepository;
import com.farmacia.fatima.repository.PedidoRepository;
import com.farmacia.fatima.repository.ProductoRepository;
import com.farmacia.fatima.repository.UsuarioRepository;
import com.farmacia.fatima.model.dto.*;
import com.farmacia.fatima.repository.*;
import com.farmacia.fatima.service.CarritoService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
 
import com.farmacia.fatima.model.dto.CarritoAddRequest;
import com.farmacia.fatima.model.dto.CarritoUpdateQtyRequest;
import com.farmacia.fatima.model.dto.CarritoResponse;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Service
@Transactional
public class CarritoServiceImpl implements CarritoService {

    private final CarritoRepository carritoRepo;
    private final ProductoRepository productoRepo;
    private final UsuarioRepository usuarioRepo;
    private final PedidoRepository pedidoRepo;

    public CarritoServiceImpl(CarritoRepository carritoRepo,
                              ProductoRepository productoRepo,
                              UsuarioRepository usuarioRepo,
                              PedidoRepository pedidoRepo) {
        this.carritoRepo = carritoRepo;
        this.productoRepo = productoRepo;
        this.usuarioRepo = usuarioRepo;
        this.pedidoRepo = pedidoRepo;
    }

    @Override
    public CarritoResponse obtenerCarritoActual(Long usuarioId) {
        Carrito c = carritoRepo.findFirstByUsuario_IdAndActivoTrueOrderByIdDesc(usuarioId)
                .orElseGet(() -> crearCarrito(usuarioId));
        recalcularTotal(c);
        return toResponse(c);
    }

    @Override
    public CarritoResponse agregarProducto(Long usuarioId, CarritoAddRequest req) {
        if (req.cantidad() == null || req.cantidad() <= 0) {
            throw new RuntimeException("Cantidad inválida");
        }
        Carrito c = carritoRepo.findFirstByUsuario_IdAndActivoTrueOrderByIdDesc(usuarioId)
                .orElseGet(() -> crearCarrito(usuarioId));

        Producto p = productoRepo.findById(req.productoId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // (Opcional) Enforce una sola sucursal por carrito:
        // if (!c.getItems().isEmpty()) {
        //   Long sucCarrito = c.getItems().get(0).getProducto().getSucursal().getId();
        //   if (!p.getSucursal().getId().equals(sucCarrito)) {
        //       throw new RuntimeException("El carrito solo permite productos de la sucursal " + sucCarrito);
        //   }
        // }

        // buscar si ya existe el item para ese producto
        CarritoItem existente = c.getItems().stream()
                .filter(it -> it.getProducto().getId().equals(p.getId()))
                .findFirst()
                .orElse(null);

        if (existente == null) {
            CarritoItem it = CarritoItem.builder()
                    .carrito(c)
                    .producto(p)
                    .precioUnitario(p.getPrecio())
                    .cantidad(req.cantidad())
                    .subtotal(p.getPrecio().multiply(BigDecimal.valueOf(req.cantidad())))
                    .build();
            c.getItems().add(it);
        } else {
            int nuevaCant = existente.getCantidad() + req.cantidad();
            existente.setCantidad(nuevaCant);
            existente.setSubtotal(existente.getPrecioUnitario().multiply(BigDecimal.valueOf(nuevaCant)));
        }
        recalcularTotal(c);
        carritoRepo.save(c);
        return toResponse(c);
    }

    @Override
    public CarritoResponse actualizarCantidad(Long usuarioId, CarritoUpdateQtyRequest req) {
        if (req.cantidad() == null || req.cantidad() <= 0) {
            throw new RuntimeException("Cantidad inválida");
        }
        Carrito c = carritoRepo.findFirstByUsuario_IdAndActivoTrueOrderByIdDesc(usuarioId)
                .orElseThrow(() -> new RuntimeException("No hay carrito activo"));

        CarritoItem it = c.getItems().stream()
                .filter(x -> x.getId().equals(req.itemId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item no encontrado"));

        it.setCantidad(req.cantidad());
        it.setSubtotal(it.getPrecioUnitario().multiply(BigDecimal.valueOf(req.cantidad())));
        recalcularTotal(c);
        carritoRepo.save(c);
        return toResponse(c);
    }

    @Override
    public CarritoResponse quitarItem(Long usuarioId, Long itemId) {
        Carrito c = carritoRepo.findFirstByUsuario_IdAndActivoTrueOrderByIdDesc(usuarioId)
                .orElseThrow(() -> new RuntimeException("No hay carrito activo"));
        boolean removed = c.getItems().removeIf(it -> it.getId().equals(itemId));
        if (!removed) throw new RuntimeException("Item no encontrado");
        recalcularTotal(c);
        carritoRepo.save(c);
        return toResponse(c);
    }

    @Override
    public void vaciar(Long usuarioId) {
        Carrito c = carritoRepo.findFirstByUsuario_IdAndActivoTrueOrderByIdDesc(usuarioId)
                .orElseThrow(() -> new RuntimeException("No hay carrito activo"));
        c.getItems().clear();
        c.setTotal(BigDecimal.ZERO);
        carritoRepo.save(c);
    }

    @Override
    public Long checkout(Long usuarioId) {
        Carrito c = carritoRepo.findFirstByUsuario_IdAndActivoTrueOrderByIdDesc(usuarioId)
                .orElseThrow(() -> new RuntimeException("No hay carrito activo"));

        if (c.getItems().isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }

        recalcularTotal(c);

        Usuario u = usuarioRepo.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Pedido pedido = Pedido.builder()
                .usuario(u)
                .carritoOrigen(c)
                .estado(Pedido.EstadoPedido.CREADO)
                .total(c.getTotal())
                .build();

        // copiar items
        for (CarritoItem it : c.getItems()) {
            PedidoItem pi = PedidoItem.builder()
                    .pedido(pedido)
                    .producto(it.getProducto())
                    .precioUnitario(it.getPrecioUnitario())
                    .cantidad(it.getCantidad())
                    .subtotal(it.getSubtotal())
                    .build();
            pedido.getItems().add(pi);
        }

        pedidoRepo.save(pedido);

        // cerrar carrito
        c.setActivo(false);
        carritoRepo.save(c);

        return pedido.getId();
    }

    // helpers
    private Carrito crearCarrito(Long usuarioId) {
        Usuario u = usuarioRepo.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Carrito c = Carrito.builder()
                .usuario(u)
                .activo(true)
                .total(BigDecimal.ZERO)
                .build();
        return carritoRepo.save(c);
    }

    private void recalcularTotal(Carrito c) {
        BigDecimal total = c.getItems().stream()
                .map(CarritoItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        c.setTotal(total);
    }

    private CarritoResponse toResponse(Carrito c) {
        return new CarritoResponse(
                c.getId(),
                c.getActivo(),
                c.getTotal(),
                c.getItems().stream().map(it ->
                        new CarritoResponse.Item(
                                it.getId(),
                                it.getProducto().getId(),
                                it.getProducto().getNombre(),
                                it.getProducto().getSucursal() != null ? it.getProducto().getSucursal().getNombre() : null,
                                it.getCantidad(),
                                it.getPrecioUnitario(),
                                it.getSubtotal()
                        )
                ).collect(Collectors.toList())
        );
    }
}
