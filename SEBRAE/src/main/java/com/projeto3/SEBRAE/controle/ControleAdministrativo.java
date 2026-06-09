package com.projeto3.SEBRAE.controle;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ControleAdministrativo {

	@GetMapping("/admin/home")
	public String acessarAdmin(HttpSession session) {
		if (session.getAttribute("usuarioLogado") == null) {
			return "redirect:/login";
		}
		return "admin/home";
	}

}