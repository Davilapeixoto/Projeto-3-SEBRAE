package com.projeto3.SEBRAE.modelo;

public enum Nivel {
	INICIANTE("Iniciante"),
	INTERMEDIARIO("Intermediário"),
	AVANCADO("Avançado");

	private final String descricao;

	Nivel(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}
}
