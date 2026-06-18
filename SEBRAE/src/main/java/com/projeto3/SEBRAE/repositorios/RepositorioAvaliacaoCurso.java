package com.projeto3.SEBRAE.repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.projeto3.SEBRAE.modelo.AvaliacaoCurso;

@Repository
public interface RepositorioAvaliacaoCurso extends JpaRepository<AvaliacaoCurso, Long> {

    Optional<AvaliacaoCurso> findByCurso_IdAndUsuario_Id(Long cursoId, Long usuarioId);

    List<AvaliacaoCurso> findAllByCurso_IdOrderByAtualizadoEmDesc(Long cursoId);

    long countByCurso_Id(Long cursoId);

    void deleteAllByCurso_Id(Long cursoId);

    @Query("select coalesce(avg(a.nota), 0.0) from AvaliacaoCurso a where a.curso.id = :cursoId")
    double calcularMediaPorCurso(@Param("cursoId") Long cursoId);
}
