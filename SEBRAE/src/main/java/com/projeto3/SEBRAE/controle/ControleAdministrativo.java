package com.projeto3.SEBRAE.controle;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.projeto3.SEBRAE.modelo.Usuario;
import com.projeto3.SEBRAE.servicos.ServicoArea;
import com.projeto3.SEBRAE.servicos.ServicoCurso;
import com.projeto3.SEBRAE.servicos.ServicoTag;

import jakarta.servlet.http.HttpSession;

@Controller
public class ControleAdministrativo {

	private final ServicoCurso servicoCurso;
	private final ServicoArea servicoArea;
	private final ServicoTag servicoTag;

	public ControleAdministrativo(ServicoCurso servicoCurso, ServicoArea servicoArea, ServicoTag servicoTag) {
		this.servicoCurso = servicoCurso;
		this.servicoArea = servicoArea;
		this.servicoTag = servicoTag;
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
		model.addAttribute("usuarioLogado", usuario);
		return "admin/home";
	}

	private Usuario usuarioLogado(HttpSession session) {
		Object usuario = session.getAttribute("usuarioLogado");
		return usuario instanceof Usuario ? (Usuario) usuario : null;
	}
}
