package com.projeto3.SEBRAE.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "usuarios")
public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "O nome é obrigatório")
	@Size(max = 120, message = "O nome deve possuir no máximo 120 caracteres")
	@Column(nullable = false, length = 120)
	private String nome;

	@NotBlank(message = "O e-mail é obrigatório")
	@Email(message = "Informe um e-mail válido")
	@Size(max = 160, message = "O e-mail deve possuir no máximo 160 caracteres")
	@Column(unique = true, nullable = false, length = 160)
	private String email;

	@NotBlank(message = "A senha é obrigatória")
	@Size(min = 6, max = 100, message = "A senha deve possuir entre 6 e 100 caracteres")
	@Column(nullable = false, length = 100)
	private String senha;

	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	private PerfilUsuario perfil = PerfilUsuario.ALUNO;

	public Usuario() {
	}

	public Usuario(Long id, String nome, String email, String senha) {
		this.id = id;
		this.nome = nome;
		this.email = email;
		this.senha = senha;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public PerfilUsuario getPerfil() {
		return perfil == null ? PerfilUsuario.ALUNO : perfil;
	}

	public void setPerfil(PerfilUsuario perfil) {
		this.perfil = perfil;
	}

	@Transient
	public boolean isAdministrador() {
		return getPerfil() == PerfilUsuario.ADMINISTRADOR;
	}
}
