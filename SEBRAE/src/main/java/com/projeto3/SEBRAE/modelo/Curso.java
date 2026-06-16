package com.projeto3.SEBRAE.modelo;

import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "cursos")
public class Curso {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 150)
	private String nome;

	@Column(nullable = false, length = 2000)
	private String descricao;

	@Column(nullable = false, length = 255)
	private String imagem;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "area_id", nullable = false)
	private Area area;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private Nivel nivel;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
		name = "cursos_tags",
		joinColumns = @JoinColumn(name = "curso_id"),
		inverseJoinColumns = @JoinColumn(name = "tag_id")
	)
	private Set<Tag> tags = new LinkedHashSet<>();

	public Curso() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getImagem() {
		return imagem;
	}

	public void setImagem(String imagem) {
		this.imagem = imagem;
	}

	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}

	public Nivel getNivel() {
		return nivel;
	}

	public void setNivel(Nivel nivel) {
		this.nivel = nivel;
	}

	public Set<Tag> getTags() {
		return tags;
	}

	public void setTags(Set<Tag> tags) {
		this.tags = tags == null ? new LinkedHashSet<>() : tags;
	}

	@Transient
	public String getImagemUrl() {
		return "/uploads/cursos/" + imagem;
	}
}
