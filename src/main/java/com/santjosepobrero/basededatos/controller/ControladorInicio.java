package com.santjosepobrero.basededatos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ControladorInicio {

    /**
     * Método que maneja la página de inicio de la aplicación.
     * Cuando el usuario entra a la ruta "/", se devuelve la vista 'index.html'.
     */
    @GetMapping("/")
    public String inicio() {
        // Simplemente, devolvemos la página de inicio
        return "index";
    }
}
