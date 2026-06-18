package com.projeto3.SEBRAE.servicos;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projeto3.SEBRAE.modelo.Tag;
import com.projeto3.SEBRAE.repositorios.RepositorioCurso;
import com.projeto3.SEBRAE.repositorios.RepositorioTag;

@Service
public class ServicoTag {

	private final RepositorioTag repositorioTag;
	private final RepositorioCurso repositorioCurso;

	public ServicoTag(RepositorioTag repositorioTag, RepositorioCurso repositorioCurso) {
		this.repositorioTag = repositorioTag;
		this.repositorioCurso = repositorioCurso;
	}

	@Transactional(readOnly = true)
	public List<Tag> listar() {
		return repositorioTag.findAllByOrderByNomeAsc();
	}

	@Transactional(readOnly = true)
	public Tag buscarPorId(Long id) {
		return repositorioTag.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Tag não encontrada"));
	}

	@Transactional
	public Tag salvar(Tag tag) {
		String nome = tag.getNome().trim();
		repositorioTag.findByNomeIgnoreCase(nome)
				.filter(existente -> !existente.getId().equals(tag.getId()))
				.ifPresent(existente -> {
					throw new IllegalArgumentException("Já existe uma tag com esse nome");
				});
		tag.setNome(nome);
		return repositorioTag.save(tag);
	}

	@Transactional
	public void excluir(Long id) {
		if (repositorioCurso.existsByTags_Id(id)) {
			throw new IllegalStateException("A tag está sendo utilizada por um curso");
		}
		repositorioTag.delete(buscarPorId(id));
	}

	@Transactional(readOnly = true)
	public long contar() {
		return repositorioTag.count();
	}
}
