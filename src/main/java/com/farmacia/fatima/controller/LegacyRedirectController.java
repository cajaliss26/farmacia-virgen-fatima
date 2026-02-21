package com.farmacia.fatima.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// Redirecciones suaves desde rutas de Servlets/JSP antiguas a las nuevas vistas
@Controller
public class LegacyRedirectController {

    @GetMapping({"/ProductoServlet","/inicio.jsp","/index.jsp"})
    public String redirectInicio(){
        return "redirect:/inicio";
    }
}
