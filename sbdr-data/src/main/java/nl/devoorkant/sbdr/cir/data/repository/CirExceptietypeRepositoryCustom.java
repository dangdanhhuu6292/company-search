package nl.devoorkant.sbdr.cir.data.repository;

import java.util.List;

import nl.devoorkant.sbdr.cir.data.model.CirExceptietype;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CirExceptietypeRepositoryCustom {
	
	@Query("FROM CirCirExceptietype WHERE code = :code")
	CirExceptietype findByCode(@Param("code") String code);

	@Query("FROM CirCirExceptietype WHERE actief= 1")
	List<CirExceptietype> findAllActief();
	
}
