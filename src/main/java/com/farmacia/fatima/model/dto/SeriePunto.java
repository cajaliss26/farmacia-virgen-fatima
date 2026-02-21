package com.farmacia.fatima.model.dto;

import java.math.BigDecimal;

public record SeriePunto(
    String label,
    BigDecimal valor
) {}