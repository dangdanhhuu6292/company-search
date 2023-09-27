package nl.devoorkant.sbdr.cir.data.repository;

import java.util.List;

import nl.devoorkant.sbdr.cir.data.model.CirPublicatiekenmerk;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CirPublicatiekenmerkRepositoryCustom {
	
	@Query("FROM CirCirPublicatiekenmerk WHERE publicatieKenmerk = :publicatieKenmerk")
	List<CirPublicatiekenmerk> findByPublicatieKenmerk(@Param("publicatieKenmerk") String publicatieKenmerk);

	@Query("FROM CirCirPublicatiekenmerk WHERE tijdVerwerkt IS NULL ORDER BY tijdExtractie")
	List<CirPublicatiekenmerk> findByTijdverwerktIsNull();
}
