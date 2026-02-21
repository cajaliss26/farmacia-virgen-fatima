package com.farmacia.fatima.controller;

import com.farmacia.fatima.model.dto.SeriePunto;
import com.farmacia.fatima.model.dto.SerieTiempo;
import com.farmacia.fatima.repository.PedidoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class ReporteController {

    private final PedidoRepository pedidoRepo;

    public ReporteController(PedidoRepository pedidoRepo) {
        this.pedidoRepo = pedidoRepo;
    }

    @GetMapping("/ventas-por-dia")
    public List<SerieTiempo> ventasPorDia(@RequestParam String desde, @RequestParam String hasta) {
        LocalDateTime ini = LocalDate.parse(desde).atStartOfDay();
        LocalDateTime fin = LocalDate.parse(hasta).atTime(23, 59, 59);

        return pedidoRepo.ventasPorDia(ini, fin).stream()
                .map(r -> {
                    java.sql.Date fechaSql = (java.sql.Date) r[0];
                    LocalDate fecha = fechaSql.toLocalDate();
                    BigDecimal total = (r[1] instanceof BigDecimal bd)
                            ? bd
                            : BigDecimal.valueOf(((Number) r[1]).doubleValue());
                    return new SerieTiempo(fecha, total);
                })
                .toList();
    }


    @GetMapping("/top-productos")
    public List<SeriePunto> topProductos(@RequestParam String desde,
                                         @RequestParam String hasta,
                                         @RequestParam(defaultValue = "5") int limite) {
        LocalDateTime ini = LocalDate.parse(desde).atStartOfDay();
        LocalDateTime fin = LocalDate.parse(hasta).atTime(23, 59, 59);

        return pedidoRepo.topProductos(ini, fin).stream()
                .limit(limite)
                .map(r -> new SeriePunto(
                        (String) r[0],
                        (r[1] instanceof Long l) ? BigDecimal.valueOf(l) : (BigDecimal) r[1]
                ))
                .toList();
    }

    @GetMapping("/ventas-por-sucursal")
    public List<SeriePunto> ventasPorSucursal(@RequestParam String desde, @RequestParam String hasta) {
        LocalDateTime ini = LocalDate.parse(desde).atStartOfDay();
        LocalDateTime fin = LocalDate.parse(hasta).atTime(23, 59, 59);

        return pedidoRepo.ventasPorSucursal(ini, fin).stream()
                .map(r -> {
                    String nombreSucursal = (String) r[0];
                    Object valor = r[1];
                    BigDecimal total;

                    if (valor instanceof BigDecimal bd) {
                        total = bd;
                    } else if (valor instanceof Number n) {
                        total = BigDecimal.valueOf(n.doubleValue());
                    } else {
                        total = BigDecimal.ZERO;
                    }

                    return new SeriePunto(nombreSucursal, total);
                })
                .toList();
    }


    @GetMapping("/resumen")
    public ResponseEntity<?> resumen(
            @RequestParam String desde,
            @RequestParam String hasta) {

        try {
            // 🔹 Convertir las fechas del query param a LocalDateTime
            LocalDateTime ini = LocalDate.parse(desde).atStartOfDay();
            LocalDateTime fin = LocalDate.parse(hasta).atTime(23, 59, 59);

            // 🔹 Ejecutar la consulta en el repositorio
            List<Object[]> resultados = pedidoRepo.resumen(ini, fin);

            BigDecimal totalVentas = BigDecimal.ZERO;
            Long totalPedidos = 0L;

            // 🔹 Validar y mapear resultados
            if (!resultados.isEmpty()) {
                Object[] fila = resultados.get(0);

                if (fila[0] instanceof BigDecimal bd) {
                    totalVentas = bd;
                } else if (fila[0] instanceof Number n) {
                    totalVentas = BigDecimal.valueOf(n.doubleValue());
                }

                if (fila[1] instanceof Number n) {
                    totalPedidos = n.longValue();
                }
            }

            // 🔹 Calcular ticket promedio
            BigDecimal ticketPromedio = totalPedidos > 0
                    ? totalVentas.divide(BigDecimal.valueOf(totalPedidos), 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            // 🔹 Construir respuesta JSON
            Map<String, Object> respuesta = Map.of(
                    "totalVentas", totalVentas,
                    "totalPedidos", totalPedidos,
                    "ticketPromedio", ticketPromedio
            );

            return ResponseEntity.ok(respuesta);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }


}

