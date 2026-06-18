package com.projeto3.SEBRAE.servicos;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projeto3.SEBRAE.modelo.Curso;
import com.projeto3.SEBRAE.modelo.RegistroTempoPagina;
import com.projeto3.SEBRAE.modelo.TempoPagina;
import com.projeto3.SEBRAE.repositorios.RepositorioCurso;
import com.projeto3.SEBRAE.repositorios.RepositorioTempoPagina;
import com.projeto3.SEBRAE.repositorios.ResumoTempoCurso;

@Service
public class ServicoTempoPagina {

	private static final Pattern VISITA_ID_SEGURA = Pattern.compile("[a-zA-Z0-9_-]{8,64}");
	private static final Pattern PAGINA_CURSO = Pattern.compile("^/cursos/(\\d+)/?$");
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

		Long cursoId = extrairCursoDaPagina(registro.pagina());
		if (registro.cursoId() == null || !cursoId.equals(registro.cursoId())) {
			throw new IllegalArgumentException("Curso inválido");
		}

		Curso curso = repositorioCurso.findById(cursoId)
				.orElseThrow(() -> new IllegalArgumentException("Curso não encontrado"));

		long segundos = Math.min(Math.max(registro.segundos(), 0), LIMITE_SEGUNDOS);
		LocalDateTime agora = LocalDateTime.now();

		TempoPagina tempo = repositorioTempoPagina.findByVisitaId(registro.visitaId())
				.orElseGet(() -> {
					TempoPagina novo = new TempoPagina();
					novo.setVisitaId(registro.visitaId());
					novo.setPagina("/cursos/" + cursoId);
					novo.setCurso(curso);
					novo.setUsuarioId(usuarioId);
					novo.setCriadoEm(agora);
					return novo;
				});

		if (tempo.getCurso() == null || !tempo.getCurso().getId().equals(cursoId)) {
			throw new IllegalArgumentException("Visita associada a outro curso");
		}

		// O navegador envia o tempo acumulado. Manter o maior valor evita duplicação.
		tempo.setSegundos(Math.max(tempo.getSegundos(), segundos));
		tempo.setFinalizado(tempo.isFinalizado() || registro.finalizado());
		tempo.setAtualizadoEm(agora);
		repositorioTempoPagina.save(tempo);
	}

	@Transactional(readOnly = true)
	public List<ResumoTempoCurso> resumirTempoMedioPorCurso() {
		return repositorioTempoPagina.resumirTempoMedioPorCurso();
	}

	private Long extrairCursoDaPagina(String pagina) {
		if (pagina == null || pagina.isBlank()) {
			throw new IllegalArgumentException("Página de curso inválida");
		}

		Matcher matcher = PAGINA_CURSO.matcher(pagina.trim());
		if (!matcher.matches()) {
			throw new IllegalArgumentException("O tempo só pode ser registrado em páginas de cursos");
		}

		try {
			return Long.valueOf(matcher.group(1));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Curso inválido", e);
		}
	}
}
