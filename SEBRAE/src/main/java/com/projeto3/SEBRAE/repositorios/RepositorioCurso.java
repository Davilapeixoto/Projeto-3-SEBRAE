package com.projeto3.SEBRAE.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.projeto3.SEBRAE.modelo.Curso;

@Repository
public interface RepositorioCurso extends JpaRepository<Curso, Long> {

	List<Curso> findAllByOrderByIdDesc();

	List<Curso> findTop6ByOrderByVisualizacoesDescIdDesc();

	boolean existsByArea_Id(Long areaId);

	boolean existsByTags_Id(Long tagId);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("update Curso c set c.visualizacoes = c.visualizacoes + 1 where c.id = :id")
	int incrementarVisualizacoes(@Param("id") Long id);

	@Query("select coalesce(sum(c.visualizacoes), 0) from Curso c")
	long somarVisualizacoes();
}
