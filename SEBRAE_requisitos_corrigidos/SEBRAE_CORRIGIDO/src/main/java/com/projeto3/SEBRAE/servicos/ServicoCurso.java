package com.projeto3.SEBRAE.servicos;

import java.text.Normalizer;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projeto3.SEBRAE.modelo.Area;
import com.projeto3.SEBRAE.modelo.Curso;
import com.projeto3.SEBRAE.modelo.CursoFormulario;
import com.projeto3.SEBRAE.modelo.Nivel;
import com.projeto3.SEBRAE.modelo.Tag;
import com.projeto3.SEBRAE.repositorios.RepositorioArea;
import com.projeto3.SEBRAE.repositorios.RepositorioCurso;
import com.projeto3.SEBRAE.repositorios.RepositorioInscricao;
import com.projeto3.SEBRAE.repositorios.RepositorioTag;

@Service
public class ServicoCurso {

	private final RepositorioCurso repositorioCurso;
	private final RepositorioArea repositorioArea;
	private final RepositorioTag repositorioTag;
	private final RepositorioInscricao repositorioInscricao;
	private final ServicoImagem servicoImagem;

	public ServicoCurso(
			RepositorioCurso repositorioCurso,
			RepositorioArea repositorioArea,
			RepositorioTag repositorioTag,
			RepositorioInscricao repositorioInscricao,
			ServicoImagem servicoImagem) {
		this.repositorioCurso = repositorioCurso;
		this.repositorioArea = repositorioArea;
		this.repositorioTag = repositorioTag;
		this.repositorioInscricao = repositorioInscricao;
		this.servicoImagem = servicoImagem;
	}

	@Transactional(readOnly = true)
	public List<Curso> listar() {
		return repositorioCurso.findAllByOrderByIdDesc();
	}

	@Transactional(readOnly = true)
	public List<Curso> listarMaisVisitados() {
		return repositorioCurso.findTop6ByOrderByVisualizacoesDescIdDesc();
	}

	@Transactional(readOnly = true)
	public List<Curso> explorar(String termo, Long areaId, Nivel nivel, String ordem) {
		String termoNormalizado = normalizar(termo);

		Comparator<Curso> comparador = switch (ordem == null ? "recentes" : ordem) {
			case "visitados" -> Comparator.comparingLong(Curso::getVisualizacoes).reversed()
					.thenComparing(Curso::getId, Comparator.reverseOrder());
			case "nome" -> Comparator.comparing(Curso::getNome, String.CASE_INSENSITIVE_ORDER);
			default -> Comparator.comparing(Curso::getId, Comparator.reverseOrder());
		};

		return repositorioCurso.findAll().stream()
				.filter(curso -> areaId == null || curso.getArea().getId().equals(areaId))
				.filter(curso -> nivel == null || curso.getNivel() == nivel)
				.filter(curso -> termoNormalizado.isBlank() || correspondeAoTermo(curso, termoNormalizado))
				.sorted(comparador)
				.toList();
	}

	@Transactional
	public void registrarVisualizacao(Long id) {
		if (repositorioCurso.incrementarVisualizacoes(id) == 0) {
			throw new IllegalArgumentException("Curso não encontrado");
		}
	}

	@Transactional(readOnly = true)
	public Curso buscarPorId(Long id) {
		return repositorioCurso.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Curso não encontrado"));
	}

	@Transactional(readOnly = true)
	public CursoFormulario criarFormulario(Long id) {
		Curso curso = buscarPorId(id);
		CursoFormulario formulario = new CursoFormulario();
		formulario.setId(curso.getId());
		formulario.setNome(curso.getNome());
		formulario.setDescricao(curso.getDescricao());
		formulario.setAreaId(curso.getArea().getId());
		formulario.setNivel(curso.getNivel());
		formulario.setTagIds(curso.getTags().stream().map(Tag::getId).toList());
		formulario.setImagemAtual(curso.getImagem());
		return formulario;
	}

	@Transactional
	public Curso salvar(CursoFormulario formulario) {
		Curso curso = formulario.getId() == null ? new Curso() : buscarPorId(formulario.getId());
		Area area = repositorioArea.findById(formulario.getAreaId())
				.orElseThrow(() -> new IllegalArgumentException("A área selecionada não existe"));
		Set<Long> idsSolicitados = new LinkedHashSet<>(formulario.getTagIds());
		List<Tag> tagsEncontradas = repositorioTag.findAllById(idsSolicitados);

		if (tagsEncontradas.size() != idsSolicitados.size()) {
			throw new IllegalArgumentException("Uma ou mais tags selecionadas não existem");
		}

		String imagemAnterior = curso.getImagem();
		String novaImagem = imagemAnterior;

		if (formulario.getImagem() != null && !formulario.getImagem().isEmpty()) {
			novaImagem = servicoImagem.salvar(formulario.getImagem());
		}

		if (novaImagem == null || novaImagem.isBlank()) {
			throw new IllegalArgumentException("A imagem do curso é obrigatória");
		}

		curso.setNome(formulario.getNome().trim());
		curso.setDescricao(formulario.getDescricao().trim());
		curso.setArea(area);
		curso.setNivel(formulario.getNivel());
		curso.setTags(new LinkedHashSet<>(tagsEncontradas));
		curso.setImagem(novaImagem);

		Curso salvo = repositorioCurso.saveAndFlush(curso);

		if (imagemAnterior != null && !imagemAnterior.equals(novaImagem)) {
			servicoImagem.excluir(imagemAnterior);
		}

		return salvo;
	}

	@Transactional
	public void excluir(Long id) {
		Curso curso = buscarPorId(id);
		String imagem = curso.getImagem();
		repositorioInscricao.deleteAllByCurso_Id(id);
		repositorioInscricao.flush();
		repositorioCurso.delete(curso);
		repositorioCurso.flush();
		servicoImagem.excluir(imagem);
	}

	@Transactional(readOnly = true)
	public long contar() {
		return repositorioCurso.count();
	}

	@Transactional(readOnly = true)
	public long totalVisualizacoes() {
		return repositorioCurso.somarVisualizacoes();
	}

	private boolean correspondeAoTermo(Curso curso, String termo) {
		return normalizar(curso.getNome()).contains(termo)
				|| normalizar(curso.getDescricao()).contains(termo)
				|| normalizar(curso.getArea().getNome()).contains(termo)
				|| curso.getTags().stream().anyMatch(tag -> normalizar(tag.getNome()).contains(termo));
	}

	private String normalizar(String valor) {
		if (valor == null) {
			return "";
		}
		String semAcentos = Normalizer.normalize(valor, Normalizer.Form.NFD)
				.replaceAll("\\p{M}", "");
		return semAcentos.trim().toLowerCase(Locale.ROOT);
	}
}
