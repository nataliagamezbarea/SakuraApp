package com.santjosepobrero.basededatos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de la aplicación.
 * Aquí es donde arranca todo el proyecto de Spring Boot.
 * Básicamente, es el punto de entrada de nuestra app.
 */
@SpringBootApplication
public class ProyectoBdSakuraApplication {

    /**
     * Método main que lanza la aplicación.
     * Spring Boot se encarga de inicializar todo lo necesario
     * para que nuestra app funcione, como el servidor web embebido.
     */
    public static void main(String[] args) {
        SpringApplication.run(ProyectoBdSakuraApplication.class, args);
    }
}
