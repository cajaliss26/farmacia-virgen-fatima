package com.farmacia.fatima.model.dto;

import java.math.BigDecimal;

public record ReporteResumen(
    BigDecimal totalVentas,
    long pedidos,
    BigDecimal ticketPromedio
) {}
