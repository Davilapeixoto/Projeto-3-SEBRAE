package com.projeto3.SEBRAE.repositorios;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projeto3.SEBRAE.modelo.PerfilUsuario;
import com.projeto3.SEBRAE.modelo.Usuario;

@Repository
public interface RepositorioUsuario extends JpaRepository<Usuario, Long> {

	Optional<Usuario> findByEmail(String email);

	Optional<Usuario> findFirstByOrderByIdAsc();

	boolean existsByPerfil(PerfilUsuario perfil);
}
