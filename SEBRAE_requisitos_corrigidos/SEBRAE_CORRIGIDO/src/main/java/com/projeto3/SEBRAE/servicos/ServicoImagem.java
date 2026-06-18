package com.projeto3.SEBRAE.servicos;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ServicoImagem {

	private static final Set<String> TIPOS_PERMITIDOS = Set.of("image/jpeg", "image/png", "image/webp");
	private final Path diretorio;

	public ServicoImagem(@Value("${aplicacao.upload.cursos:uploads/cursos}") String diretorio) {
		this.diretorio = Paths.get(diretorio).toAbsolutePath().normalize();
	}

	public String salvar(MultipartFile arquivo) {
		if (arquivo == null || arquivo.isEmpty()) {
			throw new IllegalArgumentException("A imagem do curso é obrigatória");
		}

		String tipo = arquivo.getContentType();
		if (tipo == null || !TIPOS_PERMITIDOS.contains(tipo.toLowerCase(Locale.ROOT))) {
			throw new IllegalArgumentException("A imagem deve estar em JPG, PNG ou WEBP");
		}

		String extensao = extensaoDoTipo(tipo);
		String nome = UUID.randomUUID() + extensao;

		try {
			Files.createDirectories(diretorio);
			try (InputStream entrada = arquivo.getInputStream()) {
				Files.copy(entrada, diretorio.resolve(nome), StandardCopyOption.REPLACE_EXISTING);
			}
			return nome;
		} catch (IOException e) {
			throw new IllegalStateException("Não foi possível salvar a imagem", e);
		}
	}

	public void excluir(String nome) {
		if (nome == null || nome.isBlank()) {
			return;
		}

		try {
			Files.deleteIfExists(diretorio.resolve(nome).normalize());
		} catch (IOException e) {
			throw new IllegalStateException("Não foi possível excluir a imagem", e);
		}
	}

	private String extensaoDoTipo(String tipo) {
		return switch (tipo.toLowerCase(Locale.ROOT)) {
			case "image/png" -> ".png";
			case "image/webp" -> ".webp";
			default -> ".jpg";
		};
	}
}
