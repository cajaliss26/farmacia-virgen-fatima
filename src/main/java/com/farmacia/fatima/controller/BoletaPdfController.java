package com.farmacia.fatima.controller;

import com.farmacia.fatima.model.Pedido;
import com.farmacia.fatima.repository.PedidoRepository;
import com.farmacia.fatima.repository.UsuarioRepository;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;

@Controller
@RequestMapping("/api/pedidos")
public class BoletaPdfController {

    private final PedidoRepository pedidoRepo;
    private final UsuarioRepository usuarioRepo;
    private final SpringTemplateEngine thymeleaf;

    public BoletaPdfController(PedidoRepository pedidoRepo,
                               UsuarioRepository usuarioRepo,
                               SpringTemplateEngine thymeleaf) {
        this.pedidoRepo = pedidoRepo;
        this.usuarioRepo = usuarioRepo;
        this.thymeleaf = thymeleaf;
    }

    private Long userId(Authentication auth){
        String email = ((UserDetails) auth.getPrincipal()).getUsername();
        return usuarioRepo.findByEmail(email).orElseThrow().getId();
    }

    @GetMapping("/{id}/boleta.pdf")
    public void boletaPdf(@PathVariable Long id, Authentication auth, HttpServletResponse resp) throws Exception {
        Long uid = userId(auth);
        Pedido p = pedidoRepo.findById(id).orElseThrow();
        if (!p.getUsuario().getId().equals(uid)) throw new RuntimeException("No autorizado");

        // Reutilizamos la vista 'boleta.html'
        Context ctx = new Context();
        ctx.setVariable("pedido", p);
        ctx.setVariable("cliente", p.getUsuario());
        ctx.setVariable("items", p.getItems());
        ctx.setVariable("total", p.getTotal());
        ctx.setVariable("numero", String.format("BOL-%06d", p.getId()));
        ctx.setVariable("fecha", p.getCreadoEn());

        String html = thymeleaf.process("boleta", ctx);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.useFastMode();
        builder.withHtmlContent(html, null);
        builder.toStream(baos);
        builder.run();

        byte[] pdf = baos.toByteArray();
        resp.setContentType("application/pdf");
        resp.setHeader("Content-Disposition", "inline; filename=boleta-"+id+".pdf");
        resp.setContentLength(pdf.length);
        resp.getOutputStream().write(pdf);
        resp.flushBuffer();
    }
}
