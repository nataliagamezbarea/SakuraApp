function toggleSelectResult(index) {
    const resultados = document.getElementsByClassName('select-result');
    const buttons = document.querySelectorAll('[id^="selectBtn"]');
    for (let i = 0; i < resultados.length; i++) {
        resultados[i].style.display = 'none';
        if (buttons[i]) {
            buttons[i].classList.remove('btn-primary');
            buttons[i].classList.add('btn-outline-primary');
        }
    }
    const el = document.getElementById('selectResult' + index);
    const btn = document.getElementById('selectBtn' + index);
    if (el) el.style.display = 'block';
    if (btn) {
        btn.classList.remove('btn-outline-primary');
        btn.classList.add('btn-primary');
    }
    initMostrarOcultarColumnas('dataTable' + index);
    initOrdenarColumnas('dataTable' + index);
}

document.addEventListener('DOMContentLoaded', () => {
    if (document.getElementById('dataTable')) {
        initMostrarOcultarColumnas('dataTable');
        initOrdenarColumnas('dataTable');
    }
    let i = 0;
    while (document.getElementById('dataTable' + i)) {
        initMostrarOcultarColumnas('dataTable' + i);
        initOrdenarColumnas('dataTable' + i);
        i++;
    }
});