package com.projeto3.SEBRAE.controle;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.projeto3.SEBRAE.modelo.PerfilUsuario;
import com.projeto3.SEBRAE.modelo.Usuario;
import com.projeto3.SEBRAE.repositorios.RepositorioUsuario;
import com.projeto3.SEBRAE.servicos.ServicoCurso;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class ControleUsuario {

	private final RepositorioUsuario repositorioUsuario;
	private final ServicoCurso servicoCurso;

	public ControleUsuario(RepositorioUsuario repositorioUsuario, ServicoCurso servicoCurso) {
		this.repositorioUsuario = repositorioUsuario;
		this.servicoCurso = servicoCurso;
	}

	@GetMapping("/")
	public String paginaInicial(Model model, HttpSession session) {
		model.addAttribute("cursos", servicoCurso.listar().stream().limit(6).toList());
		model.addAttribute("usuarioLogado", session.getAttribute("usuarioLogado"));
		return "usuario/home";
	}

	@GetMapping("/cadastro")
	public String exibirFormularioCadastro(Model model) {
		model.addAttribute("usuario", new Usuario());
		return "usuario/cadastro";
	}

	@PostMapping("/cadastro")
	public String processarCadastro(
			@Valid @ModelAttribute("usuario") Usuario usuario,
			BindingResult bindingResult,
			Model model) {

		if (bindingResult.hasErrors()) {
			return "usuario/cadastro";
		}

		usuario.setId(null);
		usuario.setNome(usuario.getNome().trim());
		usuario.setEmail(usuario.getEmail().trim().toLowerCase());

		if (repositorioUsuario.findByEmail(usuario.getEmail()).isPresent()) {
			model.addAttribute("erro", "Este e-mail já está cadastrado na plataforma.");
			return "usuario/cadastro";
		}

		usuario.setPerfil(repositorioUsuario.count() == 0 ? PerfilUsuario.ADMINISTRADOR : PerfilUsuario.ALUNO);
		repositorioUsuario.save(usuario);
		return "redirect:/login?cadastro=sucesso";
	}

	@GetMapping("/login")
	public String exibirFormularioLogin() {
		return "usuario/login";
	}

	@PostMapping("/login")
	public String processarLogin(
			@RequestParam String email,
			@RequestParam String senha,
			HttpSession session,
			Model model) {

		Optional<Usuario> usuarioOpt = repositorioUsuario.findByEmail(email.trim().toLowerCase());

		if (usuarioOpt.isPresent() && usuarioOpt.get().getSenha().equals(senha)) {
			session.setAttribute("usuarioLogado", usuarioOpt.get());
			return "redirect:/";
		}

		model.addAttribute("erro", "Credenciais inválidas. Por favor, tente novamente.");
		return "usuario/login";
	}

	@GetMapping("/logout")
	public String terminarSessao(HttpSession session) {
		session.invalidate();
		return "redirect:/";
	}
}
