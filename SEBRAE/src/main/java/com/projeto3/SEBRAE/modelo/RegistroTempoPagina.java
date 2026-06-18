package com.projeto3.SEBRAE.modelo;

public record RegistroTempoPagina(
		String visitaId,
		String pagina,
		Long cursoId,
		long segundos,
		boolean finalizado) {
}
