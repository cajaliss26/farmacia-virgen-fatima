package com.farmacia.fatima.model.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalTime;

public record ReservaRequest(

        @NotNull(message = "Debe especificar la sucursal")
        Long sucursalId,

        @NotNull(message = "Debe ingresar la fecha de la reserva")
        LocalDate fechaReserva,

        @NotNull(message = "Debe ingresar la hora de inicio")
        LocalTime horaInicio,

        @NotNull(message = "Debe ingresar la hora de fin")
        LocalTime horaFin,

        @Min(value = 1, message = "Debe haber al menos 1 persona")
        Integer numeroPersonas
) {
    public ReservaRequest {
        if (numeroPersonas == null) {
            numeroPersonas = 1;
        }
    }
}
