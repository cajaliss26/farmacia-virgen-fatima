package com.farmacia.fatima.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SerieTiempo(
    LocalDate fecha,
    BigDecimal total
) {}