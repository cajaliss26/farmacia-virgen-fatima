package com.farmacia.fatima.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.access.prepost.PreAuthorize; // <-- importa

@Controller
public class ViewController {

    @GetMapping({"/", "/inicio"})
    public String home() { return "dashboard-cliente"; }

    @GetMapping("/login")     public String loginPage()    { return "login"; }
    @GetMapping("/register")  public String registerPage() { return "register"; }

    // ====== Vistas ADMIN (solo ADMIN) ======
    @GetMapping("/dashboard-admin")
    public String adminDashboard() { return "dashboard-admin"; }

    @GetMapping("/dashboard-admin/productos")
    public String adminProductos() { return "admin-productos"; }

    @GetMapping("/dashboard-admin/sucursales")
    public String adminSucursales() { return "admin-sucursales"; }

    @GetMapping("/dashboard-admin/reportes")
    public String adminReportes() { return "reportes-admin"; }



    // ====== Vistas cliente ======
    @GetMapping("/dashboard-cliente")
    public String clienteDashboard() { return "dashboard-cliente"; }

    @GetMapping("/dashboard-cliente/productos")
    public String catalogoProductos() { return "catalogo-productos"; }

    @GetMapping("/dashboard-cliente/reservas")
    public String vistaReservasCliente() { return "reservas-cliente"; }

    @GetMapping("/dashboard-cliente/carrito")
    public String carrito() { return "carrito"; }

    @GetMapping("/catalogo")    public String aliasCatalogo()  { return "catalogo-productos"; }
    @GetMapping("/reservas")    public String aliasReservas()  { return "reservas-cliente"; }
    @GetMapping("/boleta-demo") public String boletaDemo()     { return "boleta"; }
}
