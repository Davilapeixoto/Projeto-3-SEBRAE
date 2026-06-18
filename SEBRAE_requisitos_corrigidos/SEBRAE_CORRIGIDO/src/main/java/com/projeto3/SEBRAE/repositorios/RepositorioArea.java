package com.projeto3.SEBRAE.repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projeto3.SEBRAE.modelo.Area;

@Repository
public interface RepositorioArea extends JpaRepository<Area, Long> {

	List<Area> findAllByOrderByNomeAsc();

	Optional<Area> findByNomeIgnoreCase(String nome);
}
