document.addEventListener('DOMContentLoaded', () => {

    const colores = ['#4e73df', '#1cc88a', '#36b9cc', '#f6c23e', '#e74a3b', '#858796', '#5a5c69', '#2e59d9', '#17a673', '#2c9faf'];

    function crearChart(canvasId, dataArray, label, tipo = 'bar') {
        if (!dataArray || !dataArray.length) return; // Si no hay datos, salir

        const ctx = document.getElementById(canvasId).getContext('2d');
        const labels = dataArray.map(row => row[0]);
        const data = dataArray.map(row => Number(row[1] || 0));

        new Chart(ctx, {
            type: tipo,
            data: {
                labels: labels,
                datasets: [{
                    label: label,
                    data: data,
                    backgroundColor: tipo === 'line' ? '#f6c23e' : colores,
                    borderColor: tipo === 'line' ? '#f6c23e' : '#fff',
                    fill: tipo !== 'line',
                    tension: tipo === 'line' ? 0.4 : 0,
                    pointRadius: tipo === 'line' ? 5 : 0
                }]
            },
            options: {
                responsive: true,
                plugins: { legend: { position: 'top' } },
                scales: tipo === 'line' ? { y: { beginAtZero: true } } : {}
            }
        });
    }

    // Crear todos los gráficos
    crearChart('chartRating', filmsPorRating['filas'], 'Películas por Rating');
    crearChart('chartActores', actoresInicial['filas'], 'Actores por letra inicial');
    crearChart('chartCategorias', filmsPorCategoria['filas'], 'Películas por Categoría');
    crearChart('chartAlquileres', alquileresMes['filas'], 'Alquileres por mes', 'line');
    crearChart('chartClientesPais', clientesPais['filas'], 'Clientes por país');
});
