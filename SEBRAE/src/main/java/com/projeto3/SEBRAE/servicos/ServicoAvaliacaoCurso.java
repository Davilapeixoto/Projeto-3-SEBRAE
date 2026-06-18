package com.projeto3.SEBRAE.servicos;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projeto3.SEBRAE.modelo.AvaliacaoCurso;
import com.projeto3.SEBRAE.modelo.Curso;
import com.projeto3.SEBRAE.modelo.Usuario;
import com.projeto3.SEBRAE.repositorios.RepositorioAvaliacaoCurso;
import com.projeto3.SEBRAE.repositorios.RepositorioCurso;
import com.projeto3.SEBRAE.repositorios.RepositorioUsuario;

@Service
public class ServicoAvaliacaoCurso {

    private final RepositorioAvaliacaoCurso repositorioAvaliacao;
    private final RepositorioUsuario repositorioUsuario;
    private final RepositorioCurso repositorioCurso;

    public ServicoAvaliacaoCurso(
            RepositorioAvaliacaoCurso repositorioAvaliacao,
            RepositorioUsuario repositorioUsuario,
            RepositorioCurso repositorioCurso) {
        this.repositorioAvaliacao = repositorioAvaliacao;
        this.repositorioUsuario = repositorioUsuario;
        this.repositorioCurso = repositorioCurso;
    }

    @Transactional
    public AvaliacaoCurso avaliar(Long usuarioId, Long cursoId, int nota, String comentario) {
        if (nota < 1 || nota > 5) {
            throw new IllegalArgumentException("Escolha uma nota entre 1 e 5 estrelas");
        }

        Usuario usuario = repositorioUsuario.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        Curso curso = repositorioCurso.findById(cursoId)
                .orElseThrow(() -> new IllegalArgumentException("Curso não encontrado"));

        AvaliacaoCurso avaliacao = repositorioAvaliacao
                .findByCurso_IdAndUsuario_Id(cursoId, usuarioId)
                .orElseGet(AvaliacaoCurso::new);
        avaliacao.setUsuario(usuario);
        avaliacao.setCurso(curso);
        avaliacao.setNota(nota);
        avaliacao.setComentario(limparComentario(comentario));

        try {
            return repositorioAvaliacao.saveAndFlush(avaliacao);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("Não foi possível salvar a avaliação. Tente novamente.");
        }
    }

    @Transactional(readOnly = true)
    public List<AvaliacaoCurso> listarPorCurso(Long cursoId) {
        return repositorioAvaliacao.findAllByCurso_IdOrderByAtualizadoEmDesc(cursoId);
    }

    @Transactional(readOnly = true)
    public Optional<AvaliacaoCurso> buscarDoUsuario(Long cursoId, Long usuarioId) {
        return repositorioAvaliacao.findByCurso_IdAndUsuario_Id(cursoId, usuarioId);
    }

    @Transactional(readOnly = true)
    public double mediaDoCurso(Long cursoId) {
        double media = repositorioAvaliacao.calcularMediaPorCurso(cursoId);
        return Math.round(media * 10.0) / 10.0;
    }

    @Transactional(readOnly = true)
    public long totalDoCurso(Long cursoId) {
        return repositorioAvaliacao.countByCurso_Id(cursoId);
    }

    private String limparComentario(String comentario) {
        if (comentario == null) {
            return null;
        }
        String limpo = comentario.trim();
        if (limpo.length() > 1000) {
            throw new IllegalArgumentException("O comentário deve possuir no máximo 1000 caracteres");
        }
        return limpo.isEmpty() ? null : limpo;
    }
}
