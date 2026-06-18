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
		select t.pagina as pagina,
		       count(t.id) as visitas,
		       sum(t.segundos) as tempoTotalSegundos,
		       avg(t.segundos) as tempoMedioSegundos
		from TempoPagina t
		group by t.pagina
		order by sum(t.segundos) desc
		""")
	List<ResumoTempoPagina> resumirPorPagina();
}
