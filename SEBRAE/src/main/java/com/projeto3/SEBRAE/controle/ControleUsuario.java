package com.projeto3.SEBRAE.controle;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ControleUsuario {
	
	@GetMapping("/")
	public String paginaInicial() {
		return "usuario/home";
	}

}
