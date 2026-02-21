package com.farmacia.fatima.controller;

import com.farmacia.fatima.model.Pedido;
import com.farmacia.fatima.model.dto.*;
import com.farmacia.fatima.repository.PedidoRepository;
import com.farmacia.fatima.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {

    private final PedidoRepository pedidoRepo;
    private final UsuarioRepository usuarioRepo;

    public PedidoController(PedidoRepository pedidoRepo, UsuarioRepository usuarioRepo) {
        this.pedidoRepo = pedidoRepo;
        this.usuarioRepo = usuarioRepo;
    }

    private Long userId(Authentication auth) {
        String email = ((UserDetails) auth.getPrincipal()).getUsername();
        return usuarioRepo.findByEmail(email).orElseThrow().getId();
    }

    // Lista de pedidos del usuario (si quieres también podrías mapear a DTO)
    @GetMapping("/mis")
    public ResponseEntity<List<Pedido>> misPedidos(Authentication auth) {
        Long uid = userId(auth);
        return ResponseEntity.ok(pedidoRepo.findByUsuarioIdOrderByCreadoEnDesc(uid));
    }

    // Un pedido por id -> como DTO para evitar ciclos e infinito anidamiento JSON
    @GetMapping("/{id}")
    public ResponseEntity<PedidoDTO> obtenerUno(@PathVariable Long id, Authentication auth) {
        Long uid = userId(auth);

        Pedido p = pedidoRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado"));

        if (!p.getUsuario().getId().equals(uid)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No autorizado para ver este pedido");
        }

        UsuarioDTO u = new UsuarioDTO(
                p.getUsuario().getId(),
                p.getUsuario().getNombre(),
                p.getUsuario().getApellido(),
                p.getUsuario().getEmail()
        );

        List<ItemDTO> items = p.getItems().stream().map(it -> {
            var prod = it.getProducto();
            var suc = (prod.getSucursal() != null)
                    ? new SucursalDTO(prod.getSucursal().getId(), prod.getSucursal().getNombre())
                    : null;

            var prodDTO = new ProductoDTO(prod.getId(), prod.getNombre(), suc);

            return new ItemDTO(
                    it.getId(),
                    it.getCantidad(),
                    it.getPrecioUnitario(),
                    it.getSubtotal(),
                    prodDTO
            );
        }).toList();

        PedidoDTO dto = new PedidoDTO(
                p.getId(),
                p.getCreadoEn(),
                p.getTotal(),
                u,
                items
        );

        return ResponseEntity.ok(dto);
    }
}
