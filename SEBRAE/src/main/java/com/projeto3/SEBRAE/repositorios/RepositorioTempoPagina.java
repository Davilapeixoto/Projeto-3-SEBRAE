package com.projeto3.SEBRAE.repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.projeto3.SEBRAE.modelo.TempoPagina;

@Repository
public interface RepositorioTempoPagina extends JpaRepository<TempoPagina, Long> {

	Optional<TempoPagina> findByVisitaId(String visitaId);

	@Query("""
		select t.curso.id as cursoId,
		       t.curso.nome as cursoNome,
		       avg(t.segundos) as tempoMedioSegundos
		from TempoPagina t
		where t.curso is not null
		group by t.curso.id, t.curso.nome
		order by avg(t.segundos) desc
		""")
	List<ResumoTempoCurso> resumirTempoMedioPorCurso();
}
