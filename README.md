# SakuraApp - Visor y Dashboard de Base de Datos SAKILA

---

## Tecnologías utilizadas

- **Java 11+**
- **Spring Boot 3**
- **Gradle**
- **Thymeleaf** (plantillas HTML)
- **Bootstrap 5** (estilos)
- **Chart.js** (gráficos)
- **MariaDB** (base de datos SAKILA)

---

## Requisitos previos

1. **Java 11.0.28 o superior**
    - [Descargar Java](https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html)
2. **MariaDB** con la base de datos SAKILA importada
    - [Descargar Sakila Database scripts](https://dev.mysql.com/doc/sakila/en/)
3. **Gradle 8.14.3** (wrapper incluido, no necesitas instalación)
4. **Navegador web**

---

## Instalación y guía de ejecución

### 1. Clona el proyecto

```bash
git clone https://github.com/nataliagamezbarea/SakuraApp
cd SakuraApp
```

### 2. Configura la conexión a la base de datos

Edita el archivo `src/main/resources/application.properties` y pon tus credenciales y el nombre de la base de datos SAKILA. Ejemplo:

```
spring.datasource.url=jdbc:mariadb://localhost:3306/sakila
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
```

### 3. Compila y ejecuta la aplicación

Ahora deberás de ejecutar la aplicación desde IntelliJ en src/main/java/com/santjosepobrero/basededatos/ProyectoBdSakuraApplication 

La app arrancará por defecto en: [http://localhost:8080/](http://localhost:8080/)

---

## Estructura de carpetas

```
src/
 └── main/
     ├── java/
     │     └── com/santjosepobrero/basededatos/
     │           ├── controller/
     │           ├── service/
     │           └── ProyectoBdSakuraApplication.java
     └── resources/
           ├── application.properties
           ├── sql/            # Consultas/estadísticas custom por archivos SQL
           ├── static/
           │     ├── css/
           │     └── js/
           └── templates/      # Todas las vistas .html Thymeleaf
```

---

## Funcionalidades principales

- **Listado de Tablas**: Navega todas las tablas presentes en tu base de datos Sakila.
- **Visualización de datos**: Cada tabla puede verse con paginación, ordenación y filtrado de columnas.
- **Ejecuta archivos SQL**: Sube archivos `.sql` custom y ve los resultados inmediatamente.
- **Dashboard**: Gráficas interactivas de clientes, películas, países, alquileres, categorías, rating y más.
- **Panel de columnas y ordenación**: Oculta/muestra columnas y ordena cualquier campo clicando el encabezado.

---

## Librerías externas

- [Bootstrap](https://getbootstrap.com/) para estilos (CDN embebido en HTML)
- [Chart.js](https://www.chartjs.org/) para gráficos (CDN embebido en HTML)
- [Thymeleaf](https://www.thymeleaf.org/) para plantillas dinámicas Java
- [Spring Boot](https://spring.io/projects/spring-boot) para backend web
- [MariaDB Connector/J](https://mariadb.com/kb/en/mariadb-connector-j/)
---

## Base de datos

- Para el desarrollo del proyecto se ha utilizado la base de datos **Sakila**.  
  Puedes descargarla y consultar su documentación oficial [aquí](https://dev.mysql.com/doc/sakila/en/).
---

## Uso básico

- Accede a [http://localhost:8080/](http://localhost:8080/)
- Haz clic en **Ver Tablas** para explorar la base de datos.
- Haz clic en **Ir al Dashboard** para ver las estadísticas gráficas.
- Puedes hacer paginación, ordenar ( si le das click a una columna) y ocultar columnas en todas las tablas y resultados.
- Puedes subir archivos `.sql` para ejecutar tus propias consultas ( y también importarlas) , si el sql obtiene múltiples consultas se mostraran varios botones.
---


## Solución a problemas comunes

- **No conecta a la base de datos**: revisa las credenciales en `application.properties`, asegúrate de tener el driver JDBC en el classpath.
- **No aparecen gráficas/dashboard**: asegúrate de tener los archivos `.sql` necesarios en `src/main/resources/sql/` y que devuelvan resultados correctos.
- **Error al iniciar Spring Boot**: revisa que la base de datos esté corriendo y accesible en el puerto indicado.
---

# Utilización inteligencia artificial

Este proyecto se ha beneficiado de herramientas de **inteligencia artificial** para:

- **Revisión y refactorización de código**: sugerencias de buenas prácticas y estructura de métodos.

> Nota: La IA se ha utilizado únicamente como **herramienta de apoyo** para ampliar la funcionalidad del gestor de base de datos.


Más información sobre el uso de Chat: [https://chatgpt.com/c/692219a1-45c8-8329-a994-34177a8a7121](https://chatgpt.com/c/692219a1-45c8-8329-a994-34177a8a7121)
