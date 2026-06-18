package com.projeto3.SEBRAE.controle;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.projeto3.SEBRAE.modelo.Curso;
import com.projeto3.SEBRAE.modelo.CursoFormulario;
import com.projeto3.SEBRAE.modelo.Nivel;
import com.projeto3.SEBRAE.modelo.Usuario;
import com.projeto3.SEBRAE.servicos.ServicoArea;
import com.projeto3.SEBRAE.servicos.ServicoAvaliacaoCurso;
import com.projeto3.SEBRAE.servicos.ServicoCurso;
import com.projeto3.SEBRAE.servicos.ServicoInscricao;
import com.projeto3.SEBRAE.servicos.ServicoTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class ControleCurso {

	private final ServicoCurso servicoCurso;
	private final ServicoArea servicoArea;
	private final ServicoTag servicoTag;
	private final ServicoInscricao servicoInscricao;
	private final ServicoAvaliacaoCurso servicoAvaliacao;

	public ControleCurso(
			ServicoCurso servicoCurso,
			ServicoArea servicoArea,
			ServicoTag servicoTag,
			ServicoInscricao servicoInscricao,
			ServicoAvaliacaoCurso servicoAvaliacao) {
		this.servicoCurso = servicoCurso;
		this.servicoArea = servicoArea;
		this.servicoTag = servicoTag;
		this.servicoInscricao = servicoInscricao;
		this.servicoAvaliacao = servicoAvaliacao;
	}

	@GetMapping({"/cursos", "/explorar", "/loja"})
	public String explorarCursos(
			@RequestParam(required = false) String q,
			@RequestParam(required = false) Long area,
			@RequestParam(required = false) Nivel nivel,
			@RequestParam(defaultValue = "recentes") String ordem,
			Model model,
			HttpSession session,
			HttpServletRequest request) {
		model.addAttribute("cursos", servicoCurso.explorar(q, area, nivel, ordem));
		model.addAttribute("maisVisitados", servicoCurso.listarMaisVisitados());
		model.addAttribute("areas", servicoArea.listar());
		model.addAttribute("niveis", Nivel.values());
		model.addAttribute("q", q == null ? "" : q);
		model.addAttribute("areaSelecionada", area);
		model.addAttribute("nivelSelecionado", nivel);
		model.addAttribute("ordem", ordem);
		model.addAttribute("modoLoja", request.getRequestURI().startsWith("/loja"));
		model.addAttribute("usuarioLogado", usuarioLogado(session));
		return "usuario/cursos";
	}

	@GetMapping("/cursos/{id:\\d+}")
	public String detalhesCurso(@PathVariable Long id, Model model, HttpSession session) {
		Curso curso;

		try {
			servicoCurso.registrarVisualizacao(id);
			curso = servicoCurso.buscarPorId(id);
		} catch (IllegalArgumentException e) {
			return "redirect:/cursos?naoEncontrado=curso";
		}

		Usuario usuario = usuarioLogado(session);
		boolean inscrito = usuario != null && servicoInscricao.estaInscrito(usuario.getId(), id);

		model.addAttribute("curso", curso);
		model.addAttribute("usuarioLogado", usuario);
		model.addAttribute("inscrito", inscrito);
		model.addAttribute("totalInscritos", servicoInscricao.contarPorCurso(id));
		model.addAttribute("avaliacoes", servicoAvaliacao.listarPorCurso(id));
		model.addAttribute("mediaAvaliacoes", servicoAvaliacao.mediaDoCurso(id));
		model.addAttribute("totalAvaliacoes", servicoAvaliacao.totalDoCurso(id));
		model.addAttribute("avaliacaoUsuario", usuario == null ? null : servicoAvaliacao.buscarDoUsuario(id, usuario.getId()).orElse(null));
		return "usuario/detalhes-curso";
	}

	@GetMapping("/cursos/gerenciar")
	public String gerenciarCursos(HttpSession session, Model model) {
		String redirecionamento = validarAdministrador(session);
		if (redirecionamento != null) {
			return redirecionamento;
		}

		model.addAttribute("cursos", servicoCurso.listar());
		model.addAttribute("usuarioLogado", usuarioLogado(session));
		return "cursos/lista";
	}

	@GetMapping("/cursos/novo")
	public String novoCurso(HttpSession session, Model model) {
		String redirecionamento = validarAdministrador(session);
		if (redirecionamento != null) {
			return redirecionamento;
		}

		prepararFormulario(model, new CursoFormulario());
		return "cursos/formulario";
	}

	@GetMapping("/cursos/cadastrar")
	public String redirecionarCadastroAntigo() {
		return "redirect:/cursos/novo";
	}

	@GetMapping("/cursos/editar/{id}")
	public String editarCurso(@PathVariable Long id, HttpSession session, Model model) {
		String redirecionamento = validarAdministrador(session);
		if (redirecionamento != null) {
			return redirecionamento;
		}

		try {
			prepararFormulario(model, servicoCurso.criarFormulario(id));
			return "cursos/formulario";
		} catch (IllegalArgumentException e) {
			return "redirect:/cursos/gerenciar?naoEncontrado=curso";
		}
	}

	@PostMapping("/cursos/salvar")
	public String salvarCurso(
			@Valid @ModelAttribute("cursoFormulario") CursoFormulario formulario,
			BindingResult bindingResult,
			HttpSession session,
			Model model) {

		String redirecionamento = validarAdministrador(session);
		if (redirecionamento != null) {
			return redirecionamento;
		}

		if (formulario.getId() == null && (formulario.getImagem() == null || formulario.getImagem().isEmpty())) {
			bindingResult.rejectValue("imagem", "imagem.obrigatoria", "A imagem do curso é obrigatória");
		}

		if (bindingResult.hasErrors()) {
			prepararFormulario(model, formulario);
			return "cursos/formulario";
		}

		try {
			Curso curso = servicoCurso.salvar(formulario);
			return "redirect:/cursos/" + curso.getId() + "?salvo=sucesso";
		} catch (IllegalArgumentException | IllegalStateException e) {
			model.addAttribute("erro", e.getMessage());
			prepararFormulario(model, formulario);
			return "cursos/formulario";
		}
	}

	@PostMapping("/cursos/excluir/{id}")
	public String excluirCurso(
			@PathVariable Long id,
			HttpSession session,
			RedirectAttributes redirectAttributes) {
		String redirecionamento = validarAdministrador(session);
		if (redirecionamento != null) {
			return redirecionamento;
		}

		try {
			servicoCurso.excluir(id);
			return "redirect:/cursos/gerenciar?excluido=sucesso";
		} catch (IllegalArgumentException | IllegalStateException e) {
			redirectAttributes.addFlashAttribute("erro", e.getMessage());
			return "redirect:/cursos/gerenciar";
		}
	}

	@PostMapping("/cursos/{id:\\d+}/inscrever")
	public String inscrever(
			@PathVariable Long id,
			HttpSession session,
			RedirectAttributes redirectAttributes) {
		Usuario usuario = usuarioLogado(session);

		if (usuario == null) {
			return "redirect:/login";
		}

		try {
			servicoInscricao.inscrever(usuario.getId(), id);
			return "redirect:/cursos/" + id + "?inscricao=sucesso";
		} catch (IllegalArgumentException | IllegalStateException e) {
			redirectAttributes.addFlashAttribute("erro", e.getMessage());
			return "redirect:/cursos/" + id;
		}
	}

	@PostMapping("/cursos/{id:\\d+}/cancelar-inscricao")
	public String cancelarInscricao(
			@PathVariable Long id,
			HttpSession session,
			RedirectAttributes redirectAttributes) {
		Usuario usuario = usuarioLogado(session);

		if (usuario == null) {
			return "redirect:/login";
		}

		try {
			servicoInscricao.cancelar(usuario.getId(), id);
			return "redirect:/cursos/" + id + "?cancelada=sucesso";
		} catch (IllegalArgumentException | IllegalStateException e) {
			redirectAttributes.addFlashAttribute("erro", e.getMessage());
			return "redirect:/cursos/" + id;
		}
	}

	@GetMapping("/minhas-inscricoes")
	public String minhasInscricoes(HttpSession session, Model model) {
		Usuario usuario = usuarioLogado(session);

		if (usuario == null) {
			return "redirect:/login";
		}

		model.addAttribute("usuarioLogado", usuario);
		model.addAttribute("inscricoes", servicoInscricao.listarPorUsuario(usuario.getId()));
		return "usuario/minhas-inscricoes";
	}

	private String validarAdministrador(HttpSession session) {
		Usuario usuario = usuarioLogado(session);
		if (usuario == null) {
			return "redirect:/login";
		}
		return usuario.isAdministrador() ? null : "redirect:/";
	}

	private Usuario usuarioLogado(HttpSession session) {
		Object usuario = session.getAttribute("usuarioLogado");
		return usuario instanceof Usuario ? (Usuario) usuario : null;
	}

	private void prepararFormulario(Model model, CursoFormulario formulario) {
		model.addAttribute("cursoFormulario", formulario);
		model.addAttribute("niveis", Nivel.values());
		model.addAttribute("areas", servicoArea.listar());
		model.addAttribute("tags", servicoTag.listar());
	}
}
