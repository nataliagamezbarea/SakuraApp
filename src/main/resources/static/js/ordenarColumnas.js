function initOrdenarColumnas(tablaId) {
    const tabla = typeof tablaId === 'string' ? document.getElementById(tablaId) : tablaId;
    if (!tabla) return;

    const encabezados = tabla.querySelectorAll('thead th');
    let direccionOrden = Array(encabezados.length).fill(null);

    // Orden inicial: primera columna ascendente
    direccionOrden[0] = 'asc';
    ordenarTablaPorColumna(tabla, 0, 'asc');
    encabezados[0].classList.add('sort-asc');

    encabezados.forEach((th, i) => {
        th.style.cursor = 'pointer';
        th.onclick = () => {
            let direccion = direccionOrden[i] === 'asc' ? 'desc' : 'asc';
            direccionOrden.fill(null);
            direccionOrden[i] = direccion;

            ordenarTablaPorColumna(tabla, i, direccion);

            encabezados.forEach((h, j) => {
                h.classList.remove('sort-asc', 'sort-desc');
                if (i === j) h.classList.add(direccion === 'asc' ? 'sort-asc' : 'sort-desc');
            });
        };
    });
}

function ordenarTablaPorColumna(tabla, indice, direccion) {
    const cuerpo = tabla.querySelector('tbody');
    const filasArray = Array.from(cuerpo.querySelectorAll('tr'));

    filasArray.sort((a, b) => {
        const celdaA = a.cells[indice] ? a.cells[indice].textContent.trim() : '';
        const celdaB = b.cells[indice] ? b.cells[indice].textContent.trim() : '';

        const numA = parseFloat(celdaA.replace(',', '.'));
        const numB = parseFloat(celdaB.replace(',', '.'));

        if (!isNaN(numA) && !isNaN(numB)) return direccion === 'asc' ? numA - numB : numB - numA;

        const aDate = Date.parse(celdaA);
        const bDate = Date.parse(celdaB);
        if (!isNaN(aDate) && !isNaN(bDate)) return direccion === 'asc' ? aDate - bDate : bDate - aDate;

        return direccion === 'asc' ? celdaA.localeCompare(celdaB) : celdaB.localeCompare(celdaA);
    });

    filasArray.forEach(fila => cuerpo.appendChild(fila));
}

document.addEventListener('DOMContentLoaded', () => {
    let i = 0;
    while (document.getElementById('dataTable' + i)) {
        initOrdenarColumnas('dataTable' + i);
        i++;
    }
    if (document.getElementById('dataTable')) {
        initOrdenarColumnas('dataTable');
    }
});
