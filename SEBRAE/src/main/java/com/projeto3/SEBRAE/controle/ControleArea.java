package com.projeto3.SEBRAE.controle;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.projeto3.SEBRAE.modelo.Area;
import com.projeto3.SEBRAE.modelo.Usuario;
import com.projeto3.SEBRAE.servicos.ServicoArea;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class ControleArea {

	private final ServicoArea servicoArea;

	public ControleArea(ServicoArea servicoArea) {
		this.servicoArea = servicoArea;
	}

	@GetMapping("/admin/areas")
	public String listar(HttpSession session, Model model) {
		String redirecionamento = validarAdministrador(session);
		if (redirecionamento != null) {
			return redirecionamento;
		}

		prepararPagina(model, new Area());
		return "admin/areas/lista";
	}

	@GetMapping("/admin/areas/editar/{id}")
	public String editar(@PathVariable Long id, HttpSession session, Model model) {
		String redirecionamento = validarAdministrador(session);
		if (redirecionamento != null) {
			return redirecionamento;
		}

		prepararPagina(model, servicoArea.buscarPorId(id));
		return "admin/areas/lista";
	}

	@PostMapping("/admin/areas/salvar")
	public String salvar(
			@Valid @ModelAttribute("area") Area area,
			BindingResult bindingResult,
			HttpSession session,
			Model model) {

		String redirecionamento = validarAdministrador(session);
		if (redirecionamento != null) {
			return redirecionamento;
		}

		if (bindingResult.hasErrors()) {
			prepararPagina(model, area);
			return "admin/areas/lista";
		}

		try {
			servicoArea.salvar(area);
			return "redirect:/admin/areas?salvo=sucesso";
		} catch (IllegalArgumentException e) {
			model.addAttribute("erro", e.getMessage());
			prepararPagina(model, area);
			return "admin/areas/lista";
		}
	}

	@PostMapping("/admin/areas/excluir/{id}")
	public String excluir(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
		String redirecionamento = validarAdministrador(session);
		if (redirecionamento != null) {
			return redirecionamento;
		}

		try {
			servicoArea.excluir(id);
			return "redirect:/admin/areas?excluido=sucesso";
		} catch (IllegalStateException e) {
			redirectAttributes.addFlashAttribute("erro", e.getMessage());
			return "redirect:/admin/areas";
		}
	}

	private void prepararPagina(Model model, Area area) {
		model.addAttribute("area", area);
		model.addAttribute("areas", servicoArea.listar());
	}

	private String validarAdministrador(HttpSession session) {
		Object objeto = session.getAttribute("usuarioLogado");
		if (!(objeto instanceof Usuario usuario)) {
			return "redirect:/login";
		}
		return usuario.isAdministrador() ? null : "redirect:/";
	}
}
