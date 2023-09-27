package nl.devoorkant.sbdr.cir.data.repository;

import java.util.List;

import nl.devoorkant.sbdr.cir.data.model.CirHandelsnaam;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CirHandelsnaamRepositoryCustom {
	
	@Query("SELECT h FROM CirCirHandelsnaam h JOIN h.cirInsolventies c WHERE c.id = :cirInsolventieId")
	List<CirHandelsnaam> findByInsolventieId(@Param("cirInsolventieId") Integer cirInsolventieId);

}
