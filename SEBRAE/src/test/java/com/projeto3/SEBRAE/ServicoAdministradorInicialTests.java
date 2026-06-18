package com.projeto3.SEBRAE;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.projeto3.SEBRAE.modelo.PerfilUsuario;
import com.projeto3.SEBRAE.modelo.Usuario;
import com.projeto3.SEBRAE.repositorios.RepositorioUsuario;
import com.projeto3.SEBRAE.servicos.ServicoAdministradorInicial;

class ServicoAdministradorInicialTests {

    @Test
    void criaAdministradorQuandoContaNaoExiste() {
        RepositorioUsuario repositorio = Mockito.mock(RepositorioUsuario.class);
        when(repositorio.findByEmail("admin@gmail.com")).thenReturn(Optional.empty());

        ServicoAdministradorInicial servico = new ServicoAdministradorInicial(
                repositorio,
                "Administrador",
                "ADMIN@gmail.com",
                "admin123");

        servico.run(null);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(repositorio).save(captor.capture());

        Usuario usuarioCriado = captor.getValue();
        assertEquals("Administrador", usuarioCriado.getNome());
        assertEquals("admin@gmail.com", usuarioCriado.getEmail());
        assertEquals("admin123", usuarioCriado.getSenha());
        assertEquals(PerfilUsuario.ADMINISTRADOR, usuarioCriado.getPerfil());
    }

    @Test
    void promoveContaExistenteESincronizaSuaSenha() {
        RepositorioUsuario repositorio = Mockito.mock(RepositorioUsuario.class);
        Usuario existente = new Usuario(null, "Davi", "admin@gmail.com", "senha-antiga");
        existente.setPerfil(PerfilUsuario.ALUNO);
        when(repositorio.findByEmail("admin@gmail.com")).thenReturn(Optional.of(existente));

        ServicoAdministradorInicial servico = new ServicoAdministradorInicial(
                repositorio,
                "Administrador",
                "admin@gmail.com",
                "nova-senha");

        servico.run(null);

        assertEquals(PerfilUsuario.ADMINISTRADOR, existente.getPerfil());
        assertEquals("nova-senha", existente.getSenha());
        verify(repositorio).save(existente);
    }

    @Test
    void naoSalvaNovamenteAdministradorExistente() {
        RepositorioUsuario repositorio = Mockito.mock(RepositorioUsuario.class);
        Usuario existente = new Usuario(null, "Administrador", "admin@gmail.com", "admin123");
        existente.setPerfil(PerfilUsuario.ADMINISTRADOR);
        when(repositorio.findByEmail("admin@gmail.com")).thenReturn(Optional.of(existente));

        ServicoAdministradorInicial servico = new ServicoAdministradorInicial(
                repositorio,
                "Administrador",
                "admin@gmail.com",
                "admin123");

        servico.run(null);

        verify(repositorio, never()).save(any(Usuario.class));
    }
}
