package com.projeto3.SEBRAE.modelo;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
	name = "tempos_pagina",
	uniqueConstraints = @UniqueConstraint(name = "uk_tempo_visita", columnNames = "visita_id"),
	indexes = {
		@Index(name = "idx_tempo_pagina", columnList = "pagina"),
		@Index(name = "idx_tempo_curso", columnList = "curso_id")
	}
)
public class TempoPagina {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "visita_id", nullable = false, length = 64)
	private String visitaId;

	@Column(nullable = false, length = 255)
	private String pagina;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "curso_id")
	private Curso curso;

	@Column(name = "usuario_id")
	private Long usuarioId;

	@Column(nullable = false)
	private long segundos;

	@Column(nullable = false)
	private boolean finalizado;

	@Column(name = "criado_em", nullable = false)
	private LocalDateTime criadoEm;

	@Column(name = "atualizado_em", nullable = false)
	private LocalDateTime atualizadoEm;

	public Long getId() {
		return id;
	}

	public String getVisitaId() {
		return visitaId;
	}

	public void setVisitaId(String visitaId) {
		this.visitaId = visitaId;
	}

	public String getPagina() {
		return pagina;
	}

	public void setPagina(String pagina) {
		this.pagina = pagina;
	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public Long getUsuarioId() {
		return usuarioId;
	}

	public void setUsuarioId(Long usuarioId) {
		this.usuarioId = usuarioId;
	}

	public long getSegundos() {
		return segundos;
	}

	public void setSegundos(long segundos) {
		this.segundos = Math.max(0, segundos);
	}

	public boolean isFinalizado() {
		return finalizado;
	}

	public void setFinalizado(boolean finalizado) {
		this.finalizado = finalizado;
	}

	public LocalDateTime getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(LocalDateTime criadoEm) {
		this.criadoEm = criadoEm;
	}

	public LocalDateTime getAtualizadoEm() {
		return atualizadoEm;
	}

	public void setAtualizadoEm(LocalDateTime atualizadoEm) {
		this.atualizadoEm = atualizadoEm;
	}
}
