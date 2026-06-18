package com.projeto3.SEBRAE.controle;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.projeto3.SEBRAE.modelo.Tag;
import com.projeto3.SEBRAE.modelo.Usuario;
import com.projeto3.SEBRAE.servicos.ServicoTag;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class ControleTag {

	private final ServicoTag servicoTag;

	public ControleTag(ServicoTag servicoTag) {
		this.servicoTag = servicoTag;
	}

	@GetMapping("/admin/tags")
	public String listar(HttpSession session, Model model) {
		String redirecionamento = validarAdministrador(session);
		if (redirecionamento != null) {
			return redirecionamento;
		}

		prepararPagina(model, new Tag());
		return "admin/tags/lista";
	}

	@GetMapping("/admin/tags/editar/{id}")
	public String editar(@PathVariable Long id, HttpSession session, Model model) {
		String redirecionamento = validarAdministrador(session);
		if (redirecionamento != null) {
			return redirecionamento;
		}

		prepararPagina(model, servicoTag.buscarPorId(id));
		return "admin/tags/lista";
	}

	@PostMapping("/admin/tags/salvar")
	public String salvar(
			@Valid @ModelAttribute("tag") Tag tag,
			BindingResult bindingResult,
			HttpSession session,
			Model model) {

		String redirecionamento = validarAdministrador(session);
		if (redirecionamento != null) {
			return redirecionamento;
		}

		if (bindingResult.hasErrors()) {
			prepararPagina(model, tag);
			return "admin/tags/lista";
		}

		try {
			servicoTag.salvar(tag);
			return "redirect:/admin/tags?salvo=sucesso";
		} catch (IllegalArgumentException e) {
			model.addAttribute("erro", e.getMessage());
			prepararPagina(model, tag);
			return "admin/tags/lista";
		}
	}

	@PostMapping("/admin/tags/excluir/{id}")
	public String excluir(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
		String redirecionamento = validarAdministrador(session);
		if (redirecionamento != null) {
			return redirecionamento;
		}

		try {
			servicoTag.excluir(id);
			return "redirect:/admin/tags?excluido=sucesso";
		} catch (IllegalStateException e) {
			redirectAttributes.addFlashAttribute("erro", e.getMessage());
			return "redirect:/admin/tags";
		}
	}

	private void prepararPagina(Model model, Tag tag) {
		model.addAttribute("tag", tag);
		model.addAttribute("tags", servicoTag.listar());
	}

	private String validarAdministrador(HttpSession session) {
		Object objeto = session.getAttribute("usuarioLogado");
		if (!(objeto instanceof Usuario usuario)) {
			return "redirect:/login";
		}
		return usuario.isAdministrador() ? null : "redirect:/";
	}
}
