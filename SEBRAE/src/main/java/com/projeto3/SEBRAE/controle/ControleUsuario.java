package com.projeto3.SEBRAE.controle;

import com.projeto3.SEBRAE.modelo.Usuario;
import com.projeto3.SEBRAE.repositorios.RepositorioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@Controller
public class ControleUsuario {

	@Autowired
	private RepositorioUsuario repositorioUsuario;

	@GetMapping("/")
	public String paginaInicial() {
		return "usuario/home";
	}

	@GetMapping("/cadastro")
	public String exibirFormularioCadastro(Model model) {
		model.addAttribute("usuario", new Usuario());
		return "usuario/cadastro";
	}

	@PostMapping("/cadastro")
	public String processarCadastro(Usuario usuario, Model model) {
		if (repositorioUsuario.findByEmail(usuario.getEmail()).isPresent()) {
			model.addAttribute("erro", "Este e-mail já se encontra registado na plataforma.");
			return "usuario/cadastro";
		}
		repositorioUsuario.save(usuario);
		return "redirect:/login";
	}

	@GetMapping("/login")
	public String exibirFormularioLogin() {
		return "usuario/login";
	}

	@PostMapping("/login")
	public String processarLogin(@RequestParam String email, @RequestParam String senha, HttpSession session, Model model) {
		Optional<Usuario> usuarioOpt = repositorioUsuario.findByEmail(email);

		if (usuarioOpt.isPresent() && usuarioOpt.get().getSenha().equals(senha)) {
			session.setAttribute("usuarioLogado", usuarioOpt.get());
			return "redirect:/admin/home";
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