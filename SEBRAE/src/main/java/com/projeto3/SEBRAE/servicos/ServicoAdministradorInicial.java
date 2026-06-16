package com.projeto3.SEBRAE.servicos;

import org.springframework.stereotype.Service;

import com.projeto3.SEBRAE.modelo.PerfilUsuario;
import com.projeto3.SEBRAE.repositorios.RepositorioUsuario;

import jakarta.annotation.PostConstruct;

@Service
public class ServicoAdministradorInicial {

	private final RepositorioUsuario repositorioUsuario;

	public ServicoAdministradorInicial(RepositorioUsuario repositorioUsuario) {
		this.repositorioUsuario = repositorioUsuario;
	}

	@PostConstruct
	public void definirAdministradorInicial() {
		if (repositorioUsuario.existsByPerfil(PerfilUsuario.ADMINISTRADOR)) {
			return;
		}

		repositorioUsuario.findFirstByOrderByIdAsc().ifPresent(usuario -> {
			usuario.setPerfil(PerfilUsuario.ADMINISTRADOR);
			repositorioUsuario.save(usuario);
		});
	}
}
