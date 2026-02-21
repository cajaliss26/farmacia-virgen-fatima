package com.farmacia.fatima.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoDTO(
        Long id,
        LocalDateTime creadoEn,
        BigDecimal total,
        UsuarioDTO usuario,
        List<ItemDTO> items
) {}

