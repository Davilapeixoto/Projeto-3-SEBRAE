package com.projeto3.SEBRAE.servicos;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.projeto3.SEBRAE.modelo.PerfilUsuario;
import com.projeto3.SEBRAE.repositorios.RepositorioUsuario;

import jakarta.annotation.PostConstruct;

@Service
public class ServicoAdministradorInicial {

    private final RepositorioUsuario repositorioUsuario;
    private final String emailAdministrador;

    public ServicoAdministradorInicial(
            RepositorioUsuario repositorioUsuario,
            @Value("${aplicacao.admin.email:}") String emailAdministrador) {
        this.repositorioUsuario = repositorioUsuario;
        this.emailAdministrador = emailAdministrador;
    }

    @PostConstruct
    public void definirAdministradorInicial() {
        String emailConfigurado = emailAdministrador == null
                ? ""
                : emailAdministrador.trim().toLowerCase();

        // Permite recuperar o acesso administrativo sem mudar a regra do cadastro.
        // Defina ADMIN_EMAIL ou aplicacao.admin.email e reinicie a aplicação.
        if (!emailConfigurado.isBlank()) {
            var usuarioConfigurado = repositorioUsuario.findByEmail(emailConfigurado);
            if (usuarioConfigurado.isPresent()) {
                var usuario = usuarioConfigurado.get();
                if (!usuario.isAdministrador()) {
                    usuario.setPerfil(PerfilUsuario.ADMINISTRADOR);
                    repositorioUsuario.save(usuario);
                }
                return;
            }
        }

        if (repositorioUsuario.existsByPerfil(PerfilUsuario.ADMINISTRADOR)) {
            return;
        }

        repositorioUsuario.findFirstByOrderByIdAsc().ifPresent(usuario -> {
            usuario.setPerfil(PerfilUsuario.ADMINISTRADOR);
            repositorioUsuario.save(usuario);
        });
    }
}
