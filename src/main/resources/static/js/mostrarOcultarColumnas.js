// Mostrar/Ocultar columnas para tabla normal y SELECTs
function initMostrarOcultarColumnas(tablaId = null) {
    // Para la tabla principal ('dataTable') el panel es 'columnTogglePanel'
    // Para cada resultado SELECT ('dataTable0', ...) el panel está cerca de la tabla

    let tabla;
    let casillas;
    if (tablaId === null || tablaId === 'dataTable') {
        tabla = document.getElementById('dataTable');
        // Busca panel exclusivo de columnas de tabla normal
        casillas = document.querySelectorAll('#columnTogglePanel .column-toggle');
    } else {
        tabla = document.getElementById(tablaId);
        // Busca panel de columnas en el div padre de la tabla select (si hay más de un SELECT)
        casillas = tabla?.parentElement?.parentElement?.querySelectorAll('.column-toggle') || [];
    }

    if (!tabla || !casillas.length) return;

    casillas.forEach(casilla => {
        casilla.onchange = function() {
            const colIndex = parseInt(casilla.dataset.colIndex);

            for (let fila of tabla.rows) {
                if (fila.cells.length > colIndex) {
                    fila.cells[colIndex].style.display = casilla.checked ? '' : 'none';
                }
            }
        };

        // Al cargar, asegura el estado de visibilidad
        setTimeout(() => {
            const colIndex = parseInt(casilla.dataset.colIndex);
            for (let fila of tabla.rows) {
                if (fila.cells.length > colIndex) {
                    fila.cells[colIndex].style.display = casilla.checked ? '' : 'none';
                }
            }
        }, 0);
    });
}

// Al cargar la página o tras una recarga AJAX/paginación
document.addEventListener('DOMContentLoaded', () => {
    // Tabla normal (actor, customer, etc)
    if (document.getElementById('dataTable')) {
        initMostrarOcultarColumnas('dataTable');
    }
    // SELECT results (por archivos SQL)
    let i = 0;
    while (document.getElementById('dataTable' + i)) {
        initMostrarOcultarColumnas('dataTable' + i);
        i++;
    }
});