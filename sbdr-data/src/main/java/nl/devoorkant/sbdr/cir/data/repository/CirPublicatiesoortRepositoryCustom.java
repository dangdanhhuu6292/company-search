package nl.devoorkant.sbdr.cir.data.repository;

import java.util.List;

import nl.devoorkant.sbdr.cir.data.model.CirPublicatiesoort;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CirPublicatiesoortRepositoryCustom {
	
	@Query("FROM CirCirPublicatiesoort WHERE code = :code")
	CirPublicatiesoort findByCode(@Param("code") String code);

	@Query("FROM CirCirPublicatiesoort WHERE actief= 1")
	List<CirPublicatiesoort> findAllActief();
	
}
