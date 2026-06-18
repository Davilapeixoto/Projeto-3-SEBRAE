package com.projeto3.SEBRAE.controle;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.projeto3.SEBRAE.modelo.Usuario;
import com.projeto3.SEBRAE.servicos.ServicoArea;
import com.projeto3.SEBRAE.servicos.ServicoCurso;
import com.projeto3.SEBRAE.servicos.ServicoTag;
import com.projeto3.SEBRAE.servicos.ServicoTempoPagina;

import jakarta.servlet.http.HttpSession;

@Controller
public class ControleAdministrativo {

	private final ServicoCurso servicoCurso;
	private final ServicoArea servicoArea;
	private final ServicoTag servicoTag;
	private final ServicoTempoPagina servicoTempoPagina;

	public ControleAdministrativo(ServicoCurso servicoCurso, ServicoArea servicoArea, ServicoTag servicoTag,
			ServicoTempoPagina servicoTempoPagina) {
		this.servicoCurso = servicoCurso;
		this.servicoArea = servicoArea;
		this.servicoTag = servicoTag;
		this.servicoTempoPagina = servicoTempoPagina;
	}

	@GetMapping("/admin/home")
	public String acessarAdmin(HttpSession session, Model model) {
		Usuario usuario = usuarioLogado(session);

		if (usuario == null) {
			return "redirect:/login";
		}

		if (!usuario.isAdministrador()) {
			return "redirect:/";
		}

		model.addAttribute("totalCursos", servicoCurso.contar());
		model.addAttribute("totalAreas", servicoArea.contar());
		model.addAttribute("totalTags", servicoTag.contar());
		model.addAttribute("totalVisualizacoes", servicoCurso.totalVisualizacoes());
		model.addAttribute("usuarioLogado", usuario);
		return "admin/home";
	}

	@GetMapping("/admin/estatisticas")
	public String estatisticas(HttpSession session, Model model) {
		Usuario usuario = usuarioLogado(session);

		if (usuario == null) {
			return "redirect:/login";
		}
		if (!usuario.isAdministrador()) {
			return "redirect:/";
		}

		model.addAttribute("usuarioLogado", usuario);
		model.addAttribute("tempos", servicoTempoPagina.resumirTempoMedioPorCurso());
		model.addAttribute("cursosMaisVisitados", servicoCurso.listarMaisVisitados());
		model.addAttribute("totalVisualizacoes", servicoCurso.totalVisualizacoes());
		return "admin/estatisticas";
	}

	private Usuario usuarioLogado(HttpSession session) {
		Object usuario = session.getAttribute("usuarioLogado");
		return usuario instanceof Usuario ? (Usuario) usuario : null;
	}
}
