// src/main/java/com/olcese/panaderia/controller/HealthController.java
package com.farmacia.fatima.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    @GetMapping("/ping")
    public String ping() { return "pong"; }
}
