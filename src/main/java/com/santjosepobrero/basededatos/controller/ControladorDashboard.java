package com.santjosepobrero.basededatos.controller;

import com.santjosepobrero.basededatos.service.ServicioBaseDeDatos;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controlador Spring MVC para manejar la vista del Dashboard de la aplicación.
 * Carga estadísticas y datos resumidos de la base de datos para mostrarlos en la interfaz web.
 */
@Controller
public class ControladorDashboard {

    /** Servicio que maneja la lógica de acceso a la base de datos. */
    private final ServicioBaseDeDatos servicioBaseDeDatos;

    /** Logger para registrar errores y eventos importantes. */
    private static final Logger logger = Logger.getLogger(ControladorDashboard.class.getName());

    /**
     * Constructor que permite la inyección del servicio de base de datos por Spring.
     *
     * @param servicioBaseDeDatos Servicio que gestiona operaciones con la base de datos
     */
    public ControladorDashboard(ServicioBaseDeDatos servicioBaseDeDatos) {
        this.servicioBaseDeDatos = servicioBaseDeDatos;
    }

    /**
     * Maneja la ruta "/dashboard" y prepara los datos estadísticos para la vista.
     * <p>
     * Carga información como:
     * <ul>
     *     <li>Películas por rating</li>
     *     <li>Actores por inicial</li>
     *     <li>Películas por categoría</li>
     *     <li>Alquileres por mes</li>
     *     <li>Clientes por país</li>
     *     <li>Total de clientes, países y películas</li>
     * </ul>
     *
     * @param modelo Modelo de datos para pasar información a la vista Thymeleaf
     * @return Nombre de la plantilla Thymeleaf a renderizar ("dashboard")
     */
    @GetMapping("/dashboard")
    public String mostrarDashboard(Model modelo) {
        try {
            modelo.addAttribute("filmsPorRating", servicioBaseDeDatos.ejecutarSQLDesdeArchivo("stat_peliculas_por_rating.sql"));
            modelo.addAttribute("actoresInicial", servicioBaseDeDatos.ejecutarSQLDesdeArchivo("stat_actores_por_letra.sql"));
            modelo.addAttribute("filmsPorCategoria", servicioBaseDeDatos.ejecutarSQLDesdeArchivo("stat_peliculas_por_categoria.sql"));
            modelo.addAttribute("alquileresMes", servicioBaseDeDatos.ejecutarSQLDesdeArchivo("stat_alquileres_por_mes.sql"));
            modelo.addAttribute("clientesPais", servicioBaseDeDatos.ejecutarSQLDesdeArchivo("stat_clientes_por_pais.sql"));

            modelo.addAttribute("numClientes", ServicioBaseDeDatos.obtenerTotalGenerico(servicioBaseDeDatos.ejecutarSQLDesdeArchivo("stat_clientes.sql")));
            modelo.addAttribute("numPaises", ServicioBaseDeDatos.obtenerTotalGenerico(servicioBaseDeDatos.ejecutarSQLDesdeArchivo("stat_paises.sql")));
            modelo.addAttribute("numPeliculas", ServicioBaseDeDatos.obtenerTotalGenerico(servicioBaseDeDatos.ejecutarSQLDesdeArchivo("stat_peliculas.sql")));

        } catch (SQLException ex) {
            modelo.addAttribute("error", "Error de base de datos: " + ex.getMessage());
            logger.log(Level.SEVERE, "Error de SQL", ex);
        } catch (Exception ex) {
            modelo.addAttribute("error", "Error inesperado al cargar el dashboard: " + ex.getMessage());
            logger.log(Level.SEVERE, "Error General", ex);
        }
        return "dashboard";
    }
}
