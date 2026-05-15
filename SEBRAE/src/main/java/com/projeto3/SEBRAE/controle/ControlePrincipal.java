package com.projeto3.SEBRAE.controle;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ControlePrincipal {
	
	@GetMapping("admin/home")
	public String acessarAdmin() {
		return "admin/home";
	}

}
