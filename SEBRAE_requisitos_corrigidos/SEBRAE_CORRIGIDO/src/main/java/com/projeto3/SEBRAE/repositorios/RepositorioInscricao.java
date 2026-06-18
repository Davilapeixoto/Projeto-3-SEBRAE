package com.projeto3.SEBRAE.repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projeto3.SEBRAE.modelo.Inscricao;

@Repository
public interface RepositorioInscricao extends JpaRepository<Inscricao, Long> {

	boolean existsByUsuario_IdAndCurso_Id(Long usuarioId, Long cursoId);

	Optional<Inscricao> findByUsuario_IdAndCurso_Id(Long usuarioId, Long cursoId);

	List<Inscricao> findAllByUsuario_IdOrderByDataInscricaoDesc(Long usuarioId);

	long countByCurso_Id(Long cursoId);

	void deleteAllByCurso_Id(Long cursoId);
}
