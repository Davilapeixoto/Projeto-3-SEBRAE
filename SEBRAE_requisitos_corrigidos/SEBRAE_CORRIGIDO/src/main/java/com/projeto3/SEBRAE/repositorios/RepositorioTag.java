package com.projeto3.SEBRAE.repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projeto3.SEBRAE.modelo.Tag;

@Repository
public interface RepositorioTag extends JpaRepository<Tag, Long> {

	List<Tag> findAllByOrderByNomeAsc();

	Optional<Tag> findByNomeIgnoreCase(String nome);
}
