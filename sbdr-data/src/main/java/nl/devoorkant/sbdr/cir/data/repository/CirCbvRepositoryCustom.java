package nl.devoorkant.sbdr.cir.data.repository;

import java.util.List;

import nl.devoorkant.sbdr.cir.data.model.CirCbv;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CirCbvRepositoryCustom {
	@Query("SELECT p FROM CirCirCbv p JOIN p.cirInsolventies c WHERE c.id = :cirInsolventieId")
	List<CirCbv> findByInsolventieId(@Param("cirInsolventieId") Integer cirInsolventieId);

}
