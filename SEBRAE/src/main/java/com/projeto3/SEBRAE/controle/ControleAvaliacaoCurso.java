package com.projeto3.SEBRAE.controle;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.projeto3.SEBRAE.modelo.Usuario;
import com.projeto3.SEBRAE.servicos.ServicoAvaliacaoCurso;

import jakarta.servlet.http.HttpSession;

@Controller
public class ControleAvaliacaoCurso {

    private final ServicoAvaliacaoCurso servicoAvaliacao;

    public ControleAvaliacaoCurso(ServicoAvaliacaoCurso servicoAvaliacao) {
        this.servicoAvaliacao = servicoAvaliacao;
    }

    @PostMapping("/cursos/{id:\\d+}/avaliar")
    public String avaliar(
            @PathVariable Long id,
            @RequestParam int nota,
            @RequestParam(required = false) String comentario,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        Object sessao = session.getAttribute("usuarioLogado");
        if (!(sessao instanceof Usuario usuario)) {
            return "redirect:/login";
        }

        try {
            servicoAvaliacao.avaliar(usuario.getId(), id, nota, comentario);
            return "redirect:/cursos/" + id + "?avaliacao=sucesso#avaliacoes";
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("erroAvaliacao", e.getMessage());
            return "redirect:/cursos/" + id + "#avaliacoes";
        }
    }
}
