package com.projeto3.SEBRAE.controle;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.projeto3.SEBRAE.modelo.RegistroTempoPagina;
import com.projeto3.SEBRAE.modelo.Usuario;
import com.projeto3.SEBRAE.servicos.ServicoTempoPagina;

import jakarta.servlet.http.HttpSession;

@RestController
public class ControleTempoPagina {

	private final ServicoTempoPagina servicoTempoPagina;

	public ControleTempoPagina(ServicoTempoPagina servicoTempoPagina) {
		this.servicoTempoPagina = servicoTempoPagina;
	}

	@PostMapping("/api/tempo-pagina")
	public ResponseEntity<Void> registrar(@RequestBody RegistroTempoPagina registro, HttpSession session) {
		Object usuarioSessao = session.getAttribute("usuarioLogado");
		Long usuarioId = usuarioSessao instanceof Usuario usuario ? usuario.getId() : null;

		try {
			servicoTempoPagina.registrar(registro, usuarioId);
			return ResponseEntity.noContent().build();
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().build();
		}
	}
}
