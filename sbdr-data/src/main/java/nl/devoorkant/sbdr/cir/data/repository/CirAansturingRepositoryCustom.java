package nl.devoorkant.sbdr.cir.data.repository;

import nl.devoorkant.sbdr.cir.data.model.CirAansturing;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CirAansturingRepositoryCustom {
	
	@Query("FROM CirCirAansturing WHERE code = :code")
	CirAansturing findByCode(@Param("code") String code);
	
}
