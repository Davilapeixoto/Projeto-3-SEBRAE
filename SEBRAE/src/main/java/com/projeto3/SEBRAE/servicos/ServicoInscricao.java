package com.projeto3.SEBRAE.servicos;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projeto3.SEBRAE.modelo.Curso;
import com.projeto3.SEBRAE.modelo.Inscricao;
import com.projeto3.SEBRAE.modelo.Usuario;
import com.projeto3.SEBRAE.repositorios.RepositorioCurso;
import com.projeto3.SEBRAE.repositorios.RepositorioInscricao;
import com.projeto3.SEBRAE.repositorios.RepositorioUsuario;

@Service
public class ServicoInscricao {

	private final RepositorioInscricao repositorioInscricao;
	private final RepositorioUsuario repositorioUsuario;
	private final RepositorioCurso repositorioCurso;

	public ServicoInscricao(
			RepositorioInscricao repositorioInscricao,
			RepositorioUsuario repositorioUsuario,
			RepositorioCurso repositorioCurso) {
		this.repositorioInscricao = repositorioInscricao;
		this.repositorioUsuario = repositorioUsuario;
		this.repositorioCurso = repositorioCurso;
	}

	@Transactional
	public void inscrever(Long usuarioId, Long cursoId) {
		if (repositorioInscricao.existsByUsuario_IdAndCurso_Id(usuarioId, cursoId)) {
			throw new IllegalStateException("Você já está inscrito neste curso");
		}

		Usuario usuario = repositorioUsuario.findById(usuarioId)
				.orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
		Curso curso = repositorioCurso.findById(cursoId)
				.orElseThrow(() -> new IllegalArgumentException("Curso não encontrado"));

		Inscricao inscricao = new Inscricao();
		inscricao.setUsuario(usuario);
		inscricao.setCurso(curso);

		try {
			repositorioInscricao.saveAndFlush(inscricao);
		} catch (DataIntegrityViolationException e) {
			throw new IllegalStateException("Você já está inscrito neste curso");
		}
	}

	@Transactional
	public void cancelar(Long usuarioId, Long cursoId) {
		Inscricao inscricao = repositorioInscricao.findByUsuario_IdAndCurso_Id(usuarioId, cursoId)
				.orElseThrow(() -> new IllegalStateException("Inscrição não encontrada"));
		repositorioInscricao.delete(inscricao);
	}

	@Transactional(readOnly = true)
	public boolean estaInscrito(Long usuarioId, Long cursoId) {
		return repositorioInscricao.existsByUsuario_IdAndCurso_Id(usuarioId, cursoId);
	}

	@Transactional(readOnly = true)
	public List<Inscricao> listarPorUsuario(Long usuarioId) {
		return repositorioInscricao.findAllByUsuario_IdOrderByDataInscricaoDesc(usuarioId);
	}

	@Transactional(readOnly = true)
	public long contarPorCurso(Long cursoId) {
		return repositorioInscricao.countByCurso_Id(cursoId);
	}
}
