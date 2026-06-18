package com.projeto3.SEBRAE.servicos;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projeto3.SEBRAE.modelo.RegistroTempoPagina;
import com.projeto3.SEBRAE.modelo.TempoPagina;
import com.projeto3.SEBRAE.repositorios.RepositorioCurso;
import com.projeto3.SEBRAE.repositorios.RepositorioTempoPagina;
import com.projeto3.SEBRAE.repositorios.ResumoTempoPagina;

@Service
public class ServicoTempoPagina {

	private static final Pattern VISITA_ID_SEGURA = Pattern.compile("[a-zA-Z0-9_-]{8,64}");
	private static final long LIMITE_SEGUNDOS = 4 * 60 * 60;

	private final RepositorioTempoPagina repositorioTempoPagina;
	private final RepositorioCurso repositorioCurso;

	public ServicoTempoPagina(RepositorioTempoPagina repositorioTempoPagina, RepositorioCurso repositorioCurso) {
		this.repositorioTempoPagina = repositorioTempoPagina;
		this.repositorioCurso = repositorioCurso;
	}

	@Transactional
	public void registrar(RegistroTempoPagina registro, Long usuarioId) {
		if (registro == null || registro.visitaId() == null
				|| !VISITA_ID_SEGURA.matcher(registro.visitaId()).matches()) {
			throw new IllegalArgumentException("Identificador de visita inválido");
		}

		String pagina = limparPagina(registro.pagina());
		long segundos = Math.min(Math.max(registro.segundos(), 0), LIMITE_SEGUNDOS);
		Long cursoId = validarCurso(registro.cursoId());
		LocalDateTime agora = LocalDateTime.now();

		TempoPagina tempo = repositorioTempoPagina.findByVisitaId(registro.visitaId())
				.orElseGet(() -> {
					TempoPagina novo = new TempoPagina();
					novo.setVisitaId(registro.visitaId());
					novo.setPagina(pagina);
					novo.setCursoId(cursoId);
					novo.setUsuarioId(usuarioId);
					novo.setCriadoEm(agora);
					return novo;
				});

		// O navegador envia o tempo acumulado. Manter o maior valor evita duplicação.
		tempo.setSegundos(Math.max(tempo.getSegundos(), segundos));
		tempo.setFinalizado(tempo.isFinalizado() || registro.finalizado());
		tempo.setAtualizadoEm(agora);
		repositorioTempoPagina.save(tempo);
	}

	@Transactional(readOnly = true)
	public List<ResumoTempoPagina> resumirPorPagina() {
		return repositorioTempoPagina.resumirPorPagina();
	}

	private String limparPagina(String pagina) {
		if (pagina == null || pagina.isBlank()) {
			throw new IllegalArgumentException("Página inválida");
		}
		String limpa = pagina.trim();
		if (!limpa.startsWith("/") || limpa.length() > 255) {
			throw new IllegalArgumentException("Página inválida");
		}
		return limpa;
	}

	private Long validarCurso(Long cursoId) {
		if (cursoId == null) {
			return null;
		}
		return repositorioCurso.existsById(cursoId) ? cursoId : null;
	}
}
