package nl.devoorkant.sbdr.cir.data.repository;

import java.util.List;

import nl.devoorkant.sbdr.cir.data.model.CirAdrestype;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CirAdrestypeRepositoryCustom {
	
	@Query("FROM CirCirAdrestype WHERE code = :code")
	CirAdrestype findByCode(@Param("code") String code);

	@Query("FROM CirCirAdrestype WHERE actief= 1")
	List<CirAdrestype> findAllActief();
	
}
