package com.projeto3.SEBRAE.modelo;

public class ResumoCategoria {

    private final Long id;
    private final String nome;
    private final long totalCursos;
    private final long totalAcessos;

    public ResumoCategoria(Long id, String nome, long totalCursos, long totalAcessos) {
        this.id = id;
        this.nome = nome;
        this.totalCursos = totalCursos;
        this.totalAcessos = totalAcessos;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public long getTotalCursos() {
        return totalCursos;
    }

    public long getTotalAcessos() {
        return totalAcessos;
    }
}
