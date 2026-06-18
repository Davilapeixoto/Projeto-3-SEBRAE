(function () {
    "use strict";

    const CHAVE_TEMA = "sebrae-tema";

    function temaEscuroAtivo() {
        return document.documentElement.classList.contains("tema-escuro");
    }

    function salvarTema(valor) {
        try {
            localStorage.setItem(CHAVE_TEMA, valor);
        } catch (erro) {
            // O modo escuro continua funcionando mesmo quando o armazenamento é bloqueado.
        }
    }

    function lerTemaSalvo() {
        try {
            return localStorage.getItem(CHAVE_TEMA);
        } catch (erro) {
            return null;
        }
    }

    function aplicarTemaInicial() {
        const temaSalvo = lerTemaSalvo();
        const prefereEscuro = window.matchMedia
            && window.matchMedia("(prefers-color-scheme: dark)").matches;

        if (temaSalvo === "escuro" || (!temaSalvo && prefereEscuro)) {
            document.documentElement.classList.add("tema-escuro");
        }
    }

    function atualizarBotoesTema() {
        const escuro = temaEscuroAtivo();
        document.querySelectorAll("[data-tema-toggle]").forEach(function (botao) {
            botao.textContent = escuro ? "☀" : "☾";
            botao.setAttribute("aria-label", escuro ? "Ativar modo claro" : "Ativar modo escuro");
            botao.title = escuro ? "Modo claro" : "Modo escuro";
        });
    }

    function alternarTema() {
        const escuro = document.documentElement.classList.toggle("tema-escuro");
        salvarTema(escuro ? "escuro" : "claro");
        atualizarBotoesTema();
    }

    function configurarTema() {
        let botoes = document.querySelectorAll("[data-tema-toggle]");

        // Segurança para alguma tela futura que seja criada sem o botão no HTML.
        if (botoes.length === 0) {
            const botao = document.createElement("button");
            botao.type = "button";
            botao.className = "tema-toggle tema-toggle-flutuante";
            botao.setAttribute("data-tema-toggle", "");
            document.body.appendChild(botao);
            botoes = document.querySelectorAll("[data-tema-toggle]");
        }

        botoes.forEach(function (botao) {
            botao.addEventListener("click", alternarTema);
        });
        atualizarBotoesTema();
    }

    function criarIdVisita() {
        if (window.crypto && typeof window.crypto.randomUUID === "function") {
            return window.crypto.randomUUID();
        }
        return "visita_" + Date.now().toString(36) + "_" + Math.random().toString(36).slice(2, 14);
    }

    function configurarTempoPagina() {
        if (!window.fetch || !document.body) {
            return;
        }

        const pagina = window.location.pathname;
        const cursoEncontrado = pagina.match(/^\/cursos\/(\d+)\/?$/);

        // O tempo é registrado somente na página de detalhes de um curso cadastrado.
        if (!cursoEncontrado) {
            return;
        }

        const visitaId = criarIdVisita();
        const cursoId = Number(cursoEncontrado[1]);
        let segundos = 0;
        let ultimoEnviado = 0;
        let finalizado = false;

        function payload(encerrou) {
            return JSON.stringify({
                visitaId: visitaId,
                pagina: pagina,
                cursoId: cursoId,
                segundos: segundos,
                finalizado: Boolean(encerrou)
            });
        }

        function enviar(encerrou) {
            if (segundos < 1 || (!encerrou && segundos === ultimoEnviado)) {
                return;
            }

            const corpo = payload(encerrou);
            ultimoEnviado = segundos;

            if (encerrou && navigator.sendBeacon) {
                const blob = new Blob([corpo], { type: "application/json; charset=UTF-8" });
                navigator.sendBeacon("/api/tempo-pagina", blob);
                return;
            }

            fetch("/api/tempo-pagina", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: corpo,
                credentials: "same-origin",
                keepalive: true
            }).catch(function () {
                // Métrica não deve bloquear a navegação nem exibir erro ao usuário.
            });
        }

        const intervalo = window.setInterval(function () {
            if (document.visibilityState === "visible") {
                segundos += 1;
                if (segundos % 5 === 0) {
                    enviar(false);
                }
            }
        }, 1000);

        function encerrar() {
            if (finalizado) {
                return;
            }
            finalizado = true;
            window.clearInterval(intervalo);
            enviar(true);
        }

        window.addEventListener("pagehide", encerrar, { once: true });
        window.addEventListener("beforeunload", encerrar, { once: true });
    }

    aplicarTemaInicial();

    document.addEventListener("DOMContentLoaded", function () {
        configurarTema();
        configurarTempoPagina();
    });
})();
