package nl.devoorkant.sbdr.cir.data.repository;

import java.util.List;

import nl.devoorkant.sbdr.cir.data.model.CirPublicatie;
import nl.devoorkant.sbdr.cir.data.model.CirZittingslocatie;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CirPublicatieRepositoryCustom {
	
	@Query("SELECT p FROM CirCirPublicatie p WHERE p.cirInsolventie.id = :cirInsolventieId")
	List<CirPublicatie> findByInsolventieId(@Param("cirInsolventieId") Integer cirInsolventieId);
	
	@Query("SELECT z FROM CirCirPublicatie p JOIN p.cirZittingslocatie z WHERE p.id = :cirPublicatieId")
	CirZittingslocatie findZittingslocatieByPublicatieId(@Param("cirPublicatieId") Integer cirPublicatieId);
	
		

}
