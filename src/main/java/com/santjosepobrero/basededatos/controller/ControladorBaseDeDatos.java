package com.santjosepobrero.basededatos.controller;

import com.santjosepobrero.basededatos.service.ServicioBaseDeDatos;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Controlador Spring MVC para manejar operaciones de base de datos a través de la interfaz web.
 * Permite listar tablas, ver contenido de tablas y ejecutar archivos SQL de manera segura.
 */
@Controller
public class ControladorBaseDeDatos {

    /** Servicio que maneja la lógica de acceso a la base de datos. */
    private final ServicioBaseDeDatos servicioBaseDeDatos;

    /**
     * Constructor que permite la inyección del servicio de base de datos por Spring.
     *
     * @param servicioBaseDeDatos Servicio que gestiona operaciones con la base de datos
     */
    public ControladorBaseDeDatos(ServicioBaseDeDatos servicioBaseDeDatos) {
        this.servicioBaseDeDatos = servicioBaseDeDatos;
    }

    /**
     * Muestra la lista de todas las tablas de la base de datos.
     * La información se pasa a la vista 'tablas.html' usando el modelo.
     *
     * @param modelo Modelo de datos para la vista
     * @return Nombre de la plantilla Thymeleaf a renderizar
     */
    @GetMapping("/tablas")
    public String mostrarTablas(Model modelo) {
        try {
            modelo.addAttribute("tablas", servicioBaseDeDatos.listarTablas());
        } catch (SQLException ex) {
            modelo.addAttribute("error", "Error de la base de datos: " + ex.getMessage());
        } catch (Exception ex) {
            modelo.addAttribute("error", "Error inesperado al listar las tablas: " + ex.getMessage());
        }
        return "tablas";
    }

    /**
     * Muestra los datos de una tabla específica.
     * Permite paginación usando los parámetros 'pagina' y 'tamanio'.
     *
     * @param nombre Nombre de la tabla a mostrar
     * @param pagina Número de página para paginación (por defecto 0)
     * @param tamanio Tamaño de página para paginación (por defecto 20)
     * @param modelo Modelo de datos para la vista
     * @return Nombre de la plantilla Thymeleaf a renderizar
     */
    @GetMapping("/tabla/{nombre}")
    public String mostrarTabla(@PathVariable String nombre,
                               @RequestParam(defaultValue = "0") int pagina,
                               @RequestParam(defaultValue = "20") int tamanio,
                               Model modelo) {
        try {
            String nombreNormalizado = servicioBaseDeDatos.normalizarNombreTabla(nombre);
            modelo.addAttribute("datos", servicioBaseDeDatos.obtenerDatosTabla(nombre, pagina, tamanio));
            modelo.addAttribute("nombreTabla", nombreNormalizado);
            modelo.addAttribute("pagina", pagina);
            modelo.addAttribute("tamanio", tamanio);
            modelo.addAttribute("total", servicioBaseDeDatos.contarFilasTabla(nombre));
        } catch (SQLException ex) {
            modelo.addAttribute("error", "No se pudo conectar a la base de datos o la tabla no existe: " + ex.getMessage());
        } catch (Exception ex) {
            modelo.addAttribute("error", "Error inesperado al mostrar la tabla: " + ex.getMessage());
        }
        return "tabla";
    }

    /**
     * Permite subir un archivo SQL y ejecutarlo de manera segura.
     * Se filtran instrucciones peligrosas como DROP, DELETE o ALTER.
     * Los resultados de sentencias SELECT se muestran en la vista.
     *
     * @param archivo Archivo SQL subido por el usuario
     * @param modelo Modelo de datos para la vista
     * @return Nombre de la plantilla Thymeleaf a renderizar
     */
    @PostMapping("/ejecutar-sql")
    public String ejecutarSQLdeArchivo(@RequestParam("archivo") MultipartFile archivo, Model modelo) {
        List<Map<String, Object>> resultadosSelect = new ArrayList<>();

        if (archivo.isEmpty()) {
            modelo.addAttribute("error", "Selecciona un archivo SQL válido para subir.");
            return "tablas";
        }

        try {
            String nombreNormalizado = servicioBaseDeDatos.normalizarNombreArchivoSQL(archivo.getOriginalFilename());
            String contenidoSQL = new String(archivo.getBytes(), StandardCharsets.UTF_8);

            // Eliminación de comentarios SQL
            contenidoSQL = contenidoSQL.replaceAll("(?m)^\\s*--.*$", "");
            contenidoSQL = contenidoSQL.replaceAll("/\\*.*?\\*/", "");
            contenidoSQL = contenidoSQL.trim();

            // Instrucciones SQL prohibidas
            Pattern instruccionesProhibidas = Pattern.compile("\\b(DROP|ALTER|DELETE|UPDATE|TRUNCATE|RENAME|MODIFY\\s+COLUMN)\\b",
                    Pattern.CASE_INSENSITIVE);

            String[] sentencias = contenidoSQL.split(";");
            int contador = 0;

            for (String sentencia : sentencias) {
                String s = sentencia.trim();
                if (s.isEmpty()) continue;
                contador++;

                if (instruccionesProhibidas.matcher(s).find()) {
                    modelo.addAttribute("error", "El archivo contiene una instrucción prohibida: " + s);
                    return "tablas";
                }

                try {
                    Map<String, Object> resultado = servicioBaseDeDatos.ejecutarSQLDirecta(s);
                    if (resultado.containsKey("columnas") && resultado.containsKey("filas")) {
                        resultado.put("indice", contador);
                        resultado.put("sql", s);
                        resultadosSelect.add(resultado);
                    }
                } catch (SQLException ex) {
                    modelo.addAttribute("error", "Error en sentencia #" + contador + ": " + ex.getMessage());
                    return "tablas";
                } catch (Exception ex) {
                    modelo.addAttribute("error", "Error inesperado en sentencia #" + contador + ": " + ex.getMessage());
                    return "tablas";
                }
            }

            modelo.addAttribute("mensaje", "Archivo SQL ejecutado correctamente.");
            if (!resultadosSelect.isEmpty()) {
                modelo.addAttribute("resultadosSelect", resultadosSelect);
            }
            return "tabla";
        } catch (Exception ex) {
            modelo.addAttribute("error", "Error inesperado al procesar el archivo SQL: " + ex.getMessage());
        }
        return "tablas";
    }
}
