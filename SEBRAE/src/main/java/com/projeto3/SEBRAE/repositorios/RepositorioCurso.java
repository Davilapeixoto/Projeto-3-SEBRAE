package com.projeto3.SEBRAE.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projeto3.SEBRAE.modelo.Curso;

@Repository
public interface RepositorioCurso extends JpaRepository<Curso, Long> {

	List<Curso> findAllByOrderByIdDesc();

	boolean existsByArea_Id(Long areaId);

	boolean existsByTags_Id(Long tagId);
}
