package com.projeto3.SEBRAE.servicos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projeto3.SEBRAE.modelo.Area;
import com.projeto3.SEBRAE.modelo.Curso;
import com.projeto3.SEBRAE.modelo.ResumoCategoria;
import com.projeto3.SEBRAE.repositorios.RepositorioArea;
import com.projeto3.SEBRAE.repositorios.RepositorioCurso;

@Service
public class ServicoArea {

	private final RepositorioArea repositorioArea;
	private final RepositorioCurso repositorioCurso;

	public ServicoArea(RepositorioArea repositorioArea, RepositorioCurso repositorioCurso) {
		this.repositorioArea = repositorioArea;
		this.repositorioCurso = repositorioCurso;
	}

	@Transactional(readOnly = true)
	public List<Area> listar() {
		return repositorioArea.findAllByOrderByNomeAsc();
	}

	@Transactional(readOnly = true)
	public Area buscarPorId(Long id) {
		return repositorioArea.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Área não encontrada"));
	}

	@Transactional
	public Area salvar(Area area) {
		String nome = area.getNome().trim();
		repositorioArea.findByNomeIgnoreCase(nome)
				.filter(existente -> !existente.getId().equals(area.getId()))
				.ifPresent(existente -> {
					throw new IllegalArgumentException("Já existe uma área com esse nome");
				});
		area.setNome(nome);
		return repositorioArea.save(area);
	}

	@Transactional
	public void excluir(Long id) {
		if (repositorioCurso.existsByArea_Id(id)) {
			throw new IllegalStateException("A área está sendo utilizada por um curso");
		}
		repositorioArea.delete(buscarPorId(id));
	}

	@Transactional(readOnly = true)
	public List<ResumoCategoria> listarComEstatisticas() {
		List<Area> areas = repositorioArea.findAllByOrderByNomeAsc();
		Map<Long, ResumoCategoriaMutavel> resumos = new LinkedHashMap<>();

		for (Area area : areas) {
			resumos.put(area.getId(), new ResumoCategoriaMutavel(area));
		}

		for (Curso curso : repositorioCurso.findAll()) {
			ResumoCategoriaMutavel resumo = resumos.get(curso.getArea().getId());
			if (resumo != null) {
				resumo.totalCursos++;
				resumo.totalAcessos += curso.getVisualizacoes();
			}
		}

		List<ResumoCategoria> resultado = new ArrayList<>();
		for (ResumoCategoriaMutavel resumo : resumos.values()) {
			resultado.add(new ResumoCategoria(
					resumo.area.getId(),
					resumo.area.getNome(),
					resumo.totalCursos,
					resumo.totalAcessos));
		}
		return resultado;
	}

	@Transactional(readOnly = true)
	public List<ResumoCategoria> listarMaisAcessadas() {
		return listarComEstatisticas().stream()
				.sorted(Comparator.comparingLong(ResumoCategoria::getTotalAcessos).reversed()
						.thenComparing(ResumoCategoria::getNome, String.CASE_INSENSITIVE_ORDER))
				.toList();
	}

	private static class ResumoCategoriaMutavel {
		private final Area area;
		private long totalCursos;
		private long totalAcessos;

		private ResumoCategoriaMutavel(Area area) {
			this.area = area;
		}
	}

	@Transactional(readOnly = true)
	public long contar() {
		return repositorioArea.count();
	}
}
