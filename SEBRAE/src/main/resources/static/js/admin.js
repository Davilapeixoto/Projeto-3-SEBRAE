(function () {
    "use strict";

    function configurarGraficosDeAcesso() {
        document.querySelectorAll("[data-grafico-acessos]").forEach(function (grafico) {
            const linhas = Array.from(grafico.querySelectorAll("[data-acessos]"));
            if (linhas.length === 0) {
                return;
            }

            const valores = linhas.map(function (linha) {
                const valor = Number(linha.getAttribute("data-acessos"));
                return Number.isFinite(valor) && valor > 0 ? valor : 0;
            });
            const maiorValor = Math.max.apply(null, valores);

            linhas.forEach(function (linha, indice) {
                const valor = valores[indice];
                const barra = linha.querySelector("[data-barra-acessos]");
                if (!barra) {
                    return;
                }

                const percentual = maiorValor > 0 ? (valor / maiorValor) * 100 : 0;
                barra.style.width = percentual.toFixed(2) + "%";
                barra.setAttribute("role", "progressbar");
                barra.setAttribute("aria-valuemin", "0");
                barra.setAttribute("aria-valuemax", String(maiorValor));
                barra.setAttribute("aria-valuenow", String(valor));
                barra.setAttribute("aria-label", valor + (valor === 1 ? " acesso" : " acessos"));
            });

            const limite = grafico.querySelector("[data-grafico-limite]");
            if (limite) {
                limite.textContent = String(maiorValor);
            }
        });
    }

    document.addEventListener("DOMContentLoaded", configurarGraficosDeAcesso);
})();
