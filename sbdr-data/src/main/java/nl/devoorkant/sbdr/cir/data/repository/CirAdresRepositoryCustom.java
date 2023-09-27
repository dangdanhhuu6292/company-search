package nl.devoorkant.sbdr.cir.data.repository;

import java.util.List;

import nl.devoorkant.sbdr.cir.data.model.CirAdres;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CirAdresRepositoryCustom {
	
	@Query("SELECT a FROM CirCirAdres a JOIN a.cirInsolventies c WHERE c.id = :cirInsolventieId")
	List<CirAdres> findByInsolventieId(@Param("cirInsolventieId") Integer cirInsolventieId);

	@Query("SELECT a FROM CirCirAdres a JOIN a.cirHandelsnaams h WHERE h.id = :cirHandelsnaamId")
	List<CirAdres> findByHandelsnaamId(@Param("cirHandelsnaamId") Integer cirHandelsnaamId);
	
}
