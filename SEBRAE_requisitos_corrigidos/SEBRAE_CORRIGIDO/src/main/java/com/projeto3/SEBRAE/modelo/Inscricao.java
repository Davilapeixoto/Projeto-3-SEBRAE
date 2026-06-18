package com.projeto3.SEBRAE.modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
	name = "inscricoes",
	uniqueConstraints = @UniqueConstraint(
		name = "uk_inscricao_usuario_curso",
		columnNames = {"usuario_id", "curso_id"}
	)
)
public class Inscricao {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "usuario_id", nullable = false)
	private Usuario usuario;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "curso_id", nullable = false)
	private Curso curso;

	@Column(name = "data_inscricao", nullable = false)
	private LocalDateTime dataInscricao;

	public Inscricao() {
	}

	@PrePersist
	public void preencherDataInscricao() {
		if (dataInscricao == null) {
			dataInscricao = LocalDateTime.now();
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public LocalDateTime getDataInscricao() {
		return dataInscricao;
	}

	public void setDataInscricao(LocalDateTime dataInscricao) {
		this.dataInscricao = dataInscricao;
	}

	public String getDataInscricaoFormatada() {
		return dataInscricao == null ? "" : dataInscricao.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
	}
}
