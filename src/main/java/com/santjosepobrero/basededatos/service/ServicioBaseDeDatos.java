package com.santjosepobrero.basededatos.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Servicio para manejar operaciones de base de datos de manera general.
 * <p>
 * Permite:
 * <ul>
 *     <li>Normalización de nombres de tablas y archivos SQL.</li>
 *     <li>Listar tablas de la base de datos.</li>
 *     <li>Contar filas de tablas.</li>
 *     <li>Obtener datos de tablas con paginación.</li>
 *     <li>Ejecutar sentencias SQL directas o desde archivos.</li>
 * </ul>
 */
@Service
public class ServicioBaseDeDatos {

    private final DataSource origenDeDatos;
    private final ResourceLoader cargadorRecursos;
    private final Pattern patronNombreTabla = Pattern.compile("^[a-zA-Z0-9_]+$");
    private final Pattern patronNombreArchivo = Pattern.compile("^[a-zA-Z0-9_\\.\\-]+\\.sql$");

    @Value("${spring.datasource.url}")
    private String urlJdbc;

    /**
     * Constructor que permite la inyección de dependencias.
     *
     * @param origenDeDatos  Fuente de datos JDBC
     * @param cargadorRecursos Cargador de recursos de Spring
     */
    public ServicioBaseDeDatos(DataSource origenDeDatos, ResourceLoader cargadorRecursos) {
        this.origenDeDatos = origenDeDatos;
        this.cargadorRecursos = cargadorRecursos;
    }

    /**
     * Normaliza un nombre: elimina espacios, convierte a minúsculas y reemplaza caracteres inválidos por '_'.
     *
     * @param nombre Nombre a normalizar
     * @return Nombre normalizado
     */
    public String normalizarNombre(String nombre) {
        if (nombre == null) return "";
        String normalizado = nombre.trim().toLowerCase();
        normalizado = normalizado.replaceAll("[^a-z0-9_\\.\\-]", "_");
        return normalizado;
    }

    /**
     * Obtiene el esquema de la base de datos a partir de la URL JDBC.
     *
     * @return Nombre del esquema
     * @throws SQLException si la URL es inválida
     */
    private String obtenerEsquema() throws SQLException {
        String[] partes = urlJdbc.split("/");
        if (partes.length == 0)
            throw new SQLException("URL de base de datos inválida.");
        return partes[partes.length - 1].split("\\?")[0];
    }

    /**
     * Lista todas las tablas del esquema actual de la base de datos.
     *
     * @return Lista de nombres de tablas
     * @throws SQLException si ocurre un error de SQL o permisos
     */
    public List<String> listarTablas() throws SQLException {
        try (Connection conexion = origenDeDatos.getConnection()) {
            String esquema = obtenerEsquema();
            List<String> tablas = new ArrayList<>();
            ResultSet rs = conexion.getMetaData().getTables(esquema, null, "%", new String[]{"TABLE"});
            while (rs.next())
                tablas.add(rs.getString("TABLE_NAME"));
            return tablas;
        } catch (SQLSyntaxErrorException ex) {
            throw new SQLException("El esquema o las tablas no existen: " + ex.getMessage(), ex);
        } catch (SQLException ex) {
            if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("denied")) {
                throw new SQLException("No tienes permisos para listar las tablas.", ex);
            }
            throw new SQLException("No se pudo listar las tablas: " + ex.getMessage(), ex);
        }
    }

    /**
     * Normaliza un nombre de tabla y verifica que sea válido.
     *
     * @param nombreTabla Nombre de la tabla
     * @return Nombre normalizado o "tabla_invalida" si no es válido
     */
    public String normalizarNombreTabla(String nombreTabla) {
        String normalizado = normalizarNombre(nombreTabla);
        if (!patronNombreTabla.matcher(normalizado).matches()) {
            return "tabla_invalida";
        }
        return normalizado;
    }

    /**
     * Normaliza un nombre de archivo SQL y verifica que sea válido.
     *
     * @param nombreArchivo Nombre del archivo
     * @return Nombre normalizado o "archivo_invalido.sql" si no es válido
     */
    public String normalizarNombreArchivoSQL(String nombreArchivo) {
        String normalizado = normalizarNombre(nombreArchivo);
        if (!normalizado.endsWith(".sql")) {
            normalizado = normalizado + ".sql";
        }
        if (!patronNombreArchivo.matcher(normalizado).matches()) {
            return "archivo_invalido.sql";
        }
        return normalizado;
    }

    /**
     * Cuenta el número de filas de una tabla.
     *
     * @param nombreTabla Nombre de la tabla
     * @return Número de filas
     * @throws SQLException si ocurre un error de SQL o permisos
     */
    public int contarFilasTabla(String nombreTabla) throws SQLException {
        String nombreTablaNormalizado = normalizarNombreTabla(nombreTabla);
        try (Connection conexion = origenDeDatos.getConnection();
             Statement sentencia = conexion.createStatement()) {
            ResultSet rs = sentencia.executeQuery("SELECT COUNT(*) as total FROM " + nombreTablaNormalizado);
            return rs.next() ? rs.getInt("total") : 0;
        } catch (SQLSyntaxErrorException ex) {
            throw new SQLException("Tabla '" + nombreTablaNormalizado + "' no existe: " + ex.getMessage(), ex);
        } catch (SQLException ex) {
            if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("denied")) {
                throw new SQLException("No tienes permisos para ver los registros de la tabla.", ex);
            }
            throw new SQLException("No se pudo contar las filas de la tabla: " + ex.getMessage(), ex);
        }
    }

    /**
     * Obtiene datos de una tabla con paginación.
     *
     * @param nombreTabla Nombre de la tabla
     * @param pagina      Número de página (0-based)
     * @param tamanio     Tamaño de página
     * @return Lista de mapas, cada uno representando una fila (columna -> valor)
     * @throws SQLException si ocurre un error de SQL o permisos
     */
    public List<Map<String, Object>> obtenerDatosTabla(String nombreTabla, int pagina, int tamanio) throws SQLException {
        String nombreTablaNormalizado = normalizarNombreTabla(nombreTabla);
        String consulta = "SELECT * FROM " + nombreTablaNormalizado + " LIMIT ? OFFSET ?";
        try (Connection conexion = origenDeDatos.getConnection();
             PreparedStatement ps = conexion.prepareStatement(consulta)) {
            ps.setInt(1, tamanio);
            ps.setInt(2, pagina * tamanio);

            ResultSet rs = ps.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();
            int columnas = meta.getColumnCount();

            List<Map<String, Object>> datos = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> fila = new LinkedHashMap<>();
                for (int i = 1; i <= columnas; i++) {
                    fila.put(meta.getColumnName(i), rs.getObject(i));
                }
                datos.add(fila);
            }
            return datos;
        } catch (SQLSyntaxErrorException ex) {
            throw new SQLException("La tabla no existe: " + ex.getMessage(), ex);
        } catch (SQLException ex) {
            if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("denied")) {
                throw new SQLException("No tienes permisos para ver los datos de la tabla.", ex);
            }
            throw new SQLException("No se pudo obtener los datos: " + ex.getMessage(), ex);
        }
    }

    /**
     * Ejecuta una sentencia SQL directa.
     * <p>
     * Si es SELECT, devuelve columnas y filas.
     * Si es otra instrucción, devuelve un mensaje de éxito.
     *
     * @param sql Sentencia SQL
     * @return Mapa con los resultados (columnas/filas o mensaje)
     * @throws SQLException si ocurre un error de SQL o permisos
     */
    public Map<String, Object> ejecutarSQLDirecta(String sql) throws SQLException {
        try (Connection conexion = origenDeDatos.getConnection();
             Statement sentencia = conexion.createStatement()) {
            String sqlNormalizada = sql.trim();
            Map<String, Object> resultado = new HashMap<>();

            if (sqlNormalizada.toUpperCase().startsWith("SELECT")) {
                ResultSet rs = sentencia.executeQuery(sqlNormalizada);
                ResultSetMetaData meta = rs.getMetaData();

                List<String> columnas = new ArrayList<>();
                for (int i = 1; i <= meta.getColumnCount(); i++)
                    columnas.add(meta.getColumnName(i));

                List<List<Object>> filas = new ArrayList<>();
                while (rs.next()) {
                    List<Object> fila = new ArrayList<>();
                    for (int i = 1; i <= meta.getColumnCount(); i++)
                        fila.add(rs.getObject(i));
                    filas.add(fila);
                }

                resultado.put("columnas", columnas);
                resultado.put("filas", filas);

            } else {
                sentencia.execute(sqlNormalizada);
                resultado.put("mensaje", "Sentencia ejecutada correctamente.");
            }

            return resultado;

        } catch (SQLSyntaxErrorException ex) {
            throw new SQLException("La sentencia SQL no es válida: " + ex.getMessage(), ex);
        } catch (SQLException ex) {
            if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("denied")) {
                throw new SQLException("No tienes permisos para ejecutar la sentencia.", ex);
            }
            throw new SQLException("Error ejecutando la sentencia: " + ex.getMessage(), ex);
        }
    }

    /**
     * Ejecuta un archivo SQL ubicado en la carpeta "classpath:sql/".
     *
     * @param archivoSql Nombre del archivo SQL
     * @return Mapa con los resultados de la ejecución
     * @throws SQLException si no se puede cargar, leer o ejecutar el archivo
     */
    public Map<String, Object> ejecutarSQLDesdeArchivo(String archivoSql) throws SQLException {
        try {
            String archivoSqlNormalizado = normalizarNombreArchivoSQL(archivoSql);
            InputStream inputStream = cargadorRecursos.getResource("classpath:sql/" + archivoSqlNormalizado).getInputStream();
            String sql = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8).trim();
            return ejecutarSQLDirecta(sql);
        } catch (Exception ex) {
            throw new SQLException("No se pudo cargar, leer o ejecutar el archivo SQL.", ex);
        }
    }

    /**
     * Extrae el número total de cualquier consulta COUNT(*) AS xxxx desde el resultado de ejecutarSQLDesdeArchivo.
     *
     * @param resultadoSql Resultado de ejecutarSQLDirecta o ejecutarSQLDesdeArchivo
     * @return Total numérico encontrado, o 0 si no se puede extraer
     */
    public static int obtenerTotalGenerico(Map<String, Object> resultadoSql) {
        if (resultadoSql == null) return 0;
        var columnas = (java.util.List<String>) resultadoSql.get("columnas");
        var filas = (java.util.List<java.util.List<Object>>) resultadoSql.get("filas");
        if (columnas != null && filas != null && !filas.isEmpty()) {
            var fila = filas.get(0);
            for (Object valor : fila) {
                if (valor instanceof Number) return ((Number) valor).intValue();
                try { return Integer.parseInt(valor.toString()); } catch (Exception e) {}
            }
        }
        return 0;
    }
}
