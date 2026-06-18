package com.projeto3.SEBRAE.modelo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CursoFormulario {

	private Long id;

	@NotBlank(message = "O nome é obrigatório")
	@Size(max = 150, message = "O nome deve possuir no máximo 150 caracteres")
	private String nome;

	@NotBlank(message = "A descrição é obrigatória")
	@Size(max = 2000, message = "A descrição deve possuir no máximo 2000 caracteres")
	private String descricao;

	@NotNull(message = "A área é obrigatória")
	private Long areaId;

	@NotNull(message = "O nível é obrigatório")
	private Nivel nivel;

	@NotEmpty(message = "Selecione pelo menos uma tag")
	private List<Long> tagIds = new ArrayList<>();

	private MultipartFile imagem;
	private String imagemAtual;

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

	public Long getAreaId() {
		return areaId;
	}

	public void setAreaId(Long areaId) {
		this.areaId = areaId;
	}

	public Nivel getNivel() {
		return nivel;
	}

	public void setNivel(Nivel nivel) {
		this.nivel = nivel;
	}

	public List<Long> getTagIds() {
		return tagIds;
	}

	public void setTagIds(List<Long> tagIds) {
		this.tagIds = tagIds == null ? new ArrayList<>() : tagIds;
	}

	public MultipartFile getImagem() {
		return imagem;
	}

	public void setImagem(MultipartFile imagem) {
		this.imagem = imagem;
	}

	public String getImagemAtual() {
		return imagemAtual;
	}

	public void setImagemAtual(String imagemAtual) {
		this.imagemAtual = imagemAtual;
	}
}
