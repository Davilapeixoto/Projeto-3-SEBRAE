package com.projeto3.SEBRAE.servicos;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projeto3.SEBRAE.modelo.PerfilUsuario;
import com.projeto3.SEBRAE.modelo.Usuario;
import com.projeto3.SEBRAE.repositorios.RepositorioUsuario;

@Service
public class ServicoAdministradorInicial implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServicoAdministradorInicial.class);

    private final RepositorioUsuario repositorioUsuario;
    private final String nomeAdministrador;
    private final String emailAdministrador;
    private final String senhaAdministrador;

    public ServicoAdministradorInicial(
            RepositorioUsuario repositorioUsuario,
            @Value("${aplicacao.admin.nome:Administrador}") String nomeAdministrador,
            @Value("${aplicacao.admin.email:admin@gmail.com}") String emailAdministrador,
            @Value("${aplicacao.admin.password:admin123}") String senhaAdministrador) {
        this.repositorioUsuario = repositorioUsuario;
        this.nomeAdministrador = nomeAdministrador;
        this.emailAdministrador = emailAdministrador;
        this.senhaAdministrador = senhaAdministrador;
    }

    /**
     * Garante que exista uma conta administrativa assim que a aplicação iniciar.
     *
     * Se o e-mail ainda não estiver cadastrado, a conta é criada. Se já estiver,
     * o usuário é promovido e sua senha é sincronizada com a configuração.
     */
    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        String nome = normalizarNome(nomeAdministrador);
        String email = normalizarEmail(emailAdministrador);
        String senha = senhaAdministrador == null ? "" : senhaAdministrador;

        validarConfiguracao(nome, email, senha);

        var usuarioExistente = repositorioUsuario.findByEmail(email);
        if (usuarioExistente.isPresent()) {
            Usuario usuario = usuarioExistente.get();
            boolean alterado = false;

            if (!usuario.isAdministrador()) {
                usuario.setPerfil(PerfilUsuario.ADMINISTRADOR);
                alterado = true;
            }

            // A configuração é a fonte das credenciais da conta automática.
            // Assim, o login continua previsível mesmo se a conta já existia.
            if (!senha.equals(usuario.getSenha())) {
                usuario.setSenha(senha);
                alterado = true;
            }

            if (alterado) {
                repositorioUsuario.save(usuario);
                LOGGER.info("Conta administradora automática sincronizada: {}", email);
            } else {
                LOGGER.info("A conta administradora automática já existe: {}", email);
            }
            return;
        }

        Usuario administrador = new Usuario();
        administrador.setNome(nome);
        administrador.setEmail(email);
        administrador.setSenha(senha);
        administrador.setPerfil(PerfilUsuario.ADMINISTRADOR);
        repositorioUsuario.save(administrador);

        LOGGER.info("Conta administradora inicial criada: {}", email);
    }

    private String normalizarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            return "Administrador";
        }
        return nome.trim();
    }

    private String normalizarEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }

    private void validarConfiguracao(String nome, String email, String senha) {
        if (nome.length() > 120) {
            throw new IllegalStateException("aplicacao.admin.nome deve possuir no máximo 120 caracteres.");
        }
        if (email.isBlank() || !email.contains("@") || email.length() > 160) {
            throw new IllegalStateException("Configure um e-mail válido em aplicacao.admin.email.");
        }
        if (senha.length() < 6 || senha.length() > 100) {
            throw new IllegalStateException(
                    "aplicacao.admin.password deve possuir entre 6 e 100 caracteres.");
        }
    }
}
