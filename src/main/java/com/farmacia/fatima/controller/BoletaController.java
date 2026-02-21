package com.farmacia.fatima.controller;

import com.farmacia.fatima.model.Usuario; // ajusta el paquete si difiere
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

@Controller
public class BoletaController {

    @GetMapping("/boleta/{pedidoId}")
    public String boleta(@PathVariable Long pedidoId, Model model) {
        // para que el JS pueda leerlo (opcional si lo tomas de la URL)
        model.addAttribute("pedidoId", pedidoId);

        // ---- valores dummy para que Thymeleaf no falle ----
        model.addAttribute("cliente", new Usuario()); // nombre/apellido/email = null
        model.addAttribute("items", Collections.emptyList());
        model.addAttribute("total", BigDecimal.ZERO);
        model.addAttribute("numero", "BOL-000000");
        model.addAttribute("fecha", LocalDateTime.now());

        return "boleta";
    }
}
