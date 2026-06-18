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
import com.projeto3.SEBRAE.servicos.ServicoArea;
import com.projeto3.SEBRAE.servicos.ServicoCurso;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class ControleUsuario {

	private final RepositorioUsuario repositorioUsuario;
	private final ServicoCurso servicoCurso;
	private final ServicoArea servicoArea;

	public ControleUsuario(RepositorioUsuario repositorioUsuario, ServicoCurso servicoCurso, ServicoArea servicoArea) {
		this.repositorioUsuario = repositorioUsuario;
		this.servicoCurso = servicoCurso;
		this.servicoArea = servicoArea;
	}

	@GetMapping("/")
	public String paginaInicial(Model model, HttpSession session) {
		Usuario usuario = usuarioLogado(session);
		model.addAttribute("cursos", servicoCurso.listar().stream().limit(6).toList());
		model.addAttribute("maisVisitados", servicoCurso.listarMaisVisitados());
		model.addAttribute("selecionados", servicoCurso.recomendarParaUsuario(usuario == null ? null : usuario.getId())
				.stream().limit(6).toList());
		model.addAttribute("categorias", servicoArea.listarComEstatisticas().stream().limit(8).toList());
		model.addAttribute("categoriasMaisAcessadas", servicoArea.listarMaisAcessadas().stream().limit(5).toList());
		model.addAttribute("usuarioLogado", usuario);
		return "usuario/home";
	}

	@GetMapping("/feed")
	public String feed(Model model, HttpSession session) {
		Usuario usuario = usuarioLogado(session);
		model.addAttribute("usuarioLogado", usuario);
		model.addAttribute("recomendados", servicoCurso.recomendarParaUsuario(usuario == null ? null : usuario.getId()));
		model.addAttribute("recentes", servicoCurso.listar().stream().limit(8).toList());
		model.addAttribute("maisVisitados", servicoCurso.listarMaisVisitados());
		model.addAttribute("categorias", servicoArea.listarMaisAcessadas().stream().limit(6).toList());
		return "usuario/feed";
	}

	@GetMapping("/categorias")
	public String categorias(Model model, HttpSession session) {
		model.addAttribute("usuarioLogado", usuarioLogado(session));
		model.addAttribute("categorias", servicoArea.listarComEstatisticas());
		model.addAttribute("maisAcessadas", servicoArea.listarMaisAcessadas().stream().limit(5).toList());
		return "usuario/categorias";
	}

	@GetMapping({"/novo-usuario", "/new-user"})
	public String novoUsuario(Model model, HttpSession session) {
		model.addAttribute("usuarioLogado", usuarioLogado(session));
		model.addAttribute("categorias", servicoArea.listarMaisAcessadas().stream().limit(4).toList());
		return "usuario/novo-usuario";
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
			@RequestParam(defaultValue = "") String confirmarSenha,
			HttpSession session,
			Model model) {

		if (bindingResult.hasErrors()) {
			return "usuario/cadastro";
		}

		if (!confirmarSenha.equals(usuario.getSenha())) {
			model.addAttribute("erroConfirmarSenha", "As senhas não coincidem.");
			return "usuario/cadastro";
		}

		usuario.setId(null);
		usuario.setNome(usuario.getNome().trim());
		usuario.setEmail(usuario.getEmail().trim().toLowerCase());

		if (repositorioUsuario.findByEmail(usuario.getEmail()).isPresent()) {
			model.addAttribute("erro", "Este e-mail já está cadastrado na plataforma.");
			return "usuario/cadastro";
		}

		usuario.setPerfil(PerfilUsuario.ALUNO);
		Usuario usuarioSalvo = repositorioUsuario.save(usuario);
		session.setAttribute("usuarioLogado", usuarioSalvo);
		return "redirect:/?cadastro=sucesso";
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

	private Usuario usuarioLogado(HttpSession session) {
		Object usuario = session.getAttribute("usuarioLogado");
		return usuario instanceof Usuario ? (Usuario) usuario : null;
	}

	@GetMapping("/logout")
	public String terminarSessao(HttpSession session) {
		session.invalidate();
		return "redirect:/";
	}
}
