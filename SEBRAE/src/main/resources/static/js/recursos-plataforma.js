(function () {
    "use strict";

    const CHAVE_TEMA = "sebrae-tema";

    function temaEscuroAtivo() {
        return document.documentElement.classList.contains("tema-escuro");
    }

    function salvarTema(valor) {
        try { localStorage.setItem(CHAVE_TEMA, valor); } catch (erro) { /* armazenamento opcional */ }
    }

    function lerTemaSalvo() {
        try { return localStorage.getItem(CHAVE_TEMA); } catch (erro) { return null; }
    }

    function aplicarTemaInicial() {
        const salvo = lerTemaSalvo();
        const prefereEscuro = window.matchMedia && window.matchMedia("(prefers-color-scheme: dark)").matches;
        if (salvo === "escuro" || (!salvo && prefereEscuro)) {
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
        if (botoes.length === 0) {
            const botao = document.createElement("button");
            botao.type = "button";
            botao.className = "tema-toggle";
            botao.setAttribute("data-tema-toggle", "");
            botao.style.position = "fixed";
            botao.style.right = "18px";
            botao.style.bottom = "18px";
            botao.style.zIndex = "1100";
            document.body.appendChild(botao);
            botoes = document.querySelectorAll("[data-tema-toggle]");
        }
        botoes.forEach(function (botao) { botao.addEventListener("click", alternarTema); });
        atualizarBotoesTema();
    }

    function configurarToasts() {
        document.querySelectorAll("[data-toast]").forEach(function (toast) {
            const fechar = toast.querySelector("[data-toast-close]");
            let temporizador;

            function remover() {
                window.clearTimeout(temporizador);
                toast.classList.remove("toast-visible");
                window.setTimeout(function () { toast.remove(); }, 260);
            }

            if (fechar) { fechar.addEventListener("click", remover); }
            window.requestAnimationFrame(function () { toast.classList.add("toast-visible"); });

            const parametro = toast.getAttribute("data-toast-clear-param");
            if (parametro && window.history && typeof window.history.replaceState === "function") {
                const url = new URL(window.location.href);
                url.searchParams.delete(parametro);
                window.history.replaceState({}, document.title, url.pathname + url.search + url.hash);
            }

            temporizador = window.setTimeout(remover, 5200);
        });
    }

    function configurarConfirmacaoSenha() {
        document.querySelectorAll("[data-password-confirm-form]").forEach(function (formulario) {
            const senha = formulario.querySelector("[data-password]");
            const confirmacao = formulario.querySelector("[data-password-confirm]");
            if (!senha || !confirmacao) return;

            function validar() {
                if (confirmacao.value && senha.value !== confirmacao.value) {
                    confirmacao.setCustomValidity("As senhas não coincidem.");
                } else {
                    confirmacao.setCustomValidity("");
                }
            }

            senha.addEventListener("input", validar);
            confirmacao.addEventListener("input", validar);
            formulario.addEventListener("submit", validar);
        });
    }

    function configurarExibicaoSenha() {
        document.querySelectorAll("[data-password-toggle]").forEach(function (botao) {
            const seletor = botao.getAttribute("data-password-toggle");
            const campo = document.querySelector(seletor);
            if (!campo) return;
            botao.addEventListener("click", function () {
                const mostrar = campo.type === "password";
                campo.type = mostrar ? "text" : "password";
                botao.textContent = mostrar ? "Ocultar" : "Ver";
                botao.setAttribute("aria-label", mostrar ? "Ocultar senha" : "Mostrar senha");
            });
        });
    }

    function criarIdVisita() {
        if (window.crypto && typeof window.crypto.randomUUID === "function") return window.crypto.randomUUID();
        return "visita_" + Date.now().toString(36) + "_" + Math.random().toString(36).slice(2, 14);
    }

    function configurarTempoPagina() {
        if (!window.fetch || !document.body) return;
        const pagina = window.location.pathname;
        const cursoEncontrado = pagina.match(/^\/cursos\/(\d+)\/?$/);
        if (!cursoEncontrado) return;

        const visitaId = criarIdVisita();
        const cursoId = Number(cursoEncontrado[1]);
        let segundos = 0;
        let ultimoEnviado = 0;
        let finalizado = false;

        function corpo(encerrou) {
            return JSON.stringify({ visitaId: visitaId, pagina: pagina, cursoId: cursoId, segundos: segundos, finalizado: Boolean(encerrou) });
        }

        function enviar(encerrou) {
            if (segundos < 1 || (!encerrou && segundos === ultimoEnviado)) return;
            const payload = corpo(encerrou);
            ultimoEnviado = segundos;
            if (encerrou && navigator.sendBeacon) {
                navigator.sendBeacon("/api/tempo-pagina", new Blob([payload], { type: "application/json; charset=UTF-8" }));
                return;
            }
            fetch("/api/tempo-pagina", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: payload,
                credentials: "same-origin",
                keepalive: true
            }).catch(function () { /* métrica não bloqueia a navegação */ });
        }

        const intervalo = window.setInterval(function () {
            if (document.visibilityState === "visible") {
                segundos += 1;
                if (segundos % 5 === 0) enviar(false);
            }
        }, 1000);

        function encerrar() {
            if (finalizado) return;
            finalizado = true;
            window.clearInterval(intervalo);
            enviar(true);
        }

        window.addEventListener("pagehide", encerrar, { once: true });
        window.addEventListener("beforeunload", encerrar, { once: true });
    }



    function configurarModoCatalogo() {
        const grade = document.querySelector("[data-course-grid]");
        const botoes = document.querySelectorAll("[data-course-view-button]");
        if (!grade || botoes.length === 0) return;

        const chave = "sebrae-modo-catalogo";
        let modo = grade.getAttribute("data-default-view") || "vitrine";
        try {
            const salvo = localStorage.getItem(chave);
            if (salvo === "lista" || salvo === "vitrine") modo = salvo;
        } catch (erro) { /* preferência opcional */ }

        if (window.location.pathname === "/loja") modo = "vitrine";

        function aplicar(novoModo) {
            modo = novoModo === "lista" ? "lista" : "vitrine";
            grade.classList.toggle("course-grid-list", modo === "lista");
            botoes.forEach(function (botao) {
                const ativo = botao.getAttribute("data-course-view-button") === modo;
                botao.classList.toggle("active", ativo);
                botao.setAttribute("aria-pressed", String(ativo));
            });
            try { localStorage.setItem(chave, modo); } catch (erro) { /* preferência opcional */ }
        }

        botoes.forEach(function (botao) {
            botao.addEventListener("click", function () {
                aplicar(botao.getAttribute("data-course-view-button"));
            });
        });
        aplicar(modo);
    }

    function configurarJornadaNovoUsuario() {
        const jornada = document.querySelector("[data-onboarding]");
        if (!jornada) return;

        const chave = "sebrae-jornada-novo-usuario";
        const passos = Array.from(jornada.querySelectorAll("[data-onboarding-step]"));
        const progresso = jornada.querySelector("[data-onboarding-progress]");
        const rotulo = jornada.querySelector("[data-onboarding-label]");
        let concluidos = [];

        try {
            const valor = JSON.parse(localStorage.getItem(chave) || "[]");
            if (Array.isArray(valor)) concluidos = valor;
        } catch (erro) { concluidos = []; }

        function atualizar() {
            passos.forEach(function (passo) {
                const id = passo.getAttribute("data-onboarding-step");
                const completo = concluidos.includes(id);
                passo.classList.toggle("completed", completo);
                const botao = passo.querySelector("[data-onboarding-toggle]");
                if (botao) {
                    botao.setAttribute("aria-pressed", String(completo));
                    botao.title = completo ? "Marcar como pendente" : "Marcar como concluída";
                }
            });
            const total = passos.length || 1;
            const quantidade = passos.filter(function (passo) {
                return concluidos.includes(passo.getAttribute("data-onboarding-step"));
            }).length;
            if (progresso) progresso.style.width = ((quantidade / total) * 100) + "%";
            if (rotulo) rotulo.textContent = quantidade + " de " + total + " etapas concluídas";
            try { localStorage.setItem(chave, JSON.stringify(concluidos)); } catch (erro) { /* opcional */ }
        }

        jornada.querySelectorAll("[data-onboarding-toggle]").forEach(function (botao) {
            botao.addEventListener("click", function () {
                const id = botao.getAttribute("data-onboarding-toggle");
                concluidos = concluidos.includes(id)
                    ? concluidos.filter(function (item) { return item !== id; })
                    : concluidos.concat(id);
                atualizar();
            });
        });
        atualizar();
    }

    aplicarTemaInicial();

    document.addEventListener("DOMContentLoaded", function () {
        configurarTema();
        configurarToasts();
        configurarConfirmacaoSenha();
        configurarExibicaoSenha();
        configurarModoCatalogo();
        configurarJornadaNovoUsuario();
        configurarTempoPagina();
    });
})();
