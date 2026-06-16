package com.projeto3.SEBRAE.servicos;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projeto3.SEBRAE.modelo.Area;
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
	public long contar() {
		return repositorioArea.count();
	}
}
