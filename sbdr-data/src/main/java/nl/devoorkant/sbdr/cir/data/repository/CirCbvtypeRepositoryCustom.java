package nl.devoorkant.sbdr.cir.data.repository;

import java.util.List;

import nl.devoorkant.sbdr.cir.data.model.CirCbvtype;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CirCbvtypeRepositoryCustom {
	
	@Query("FROM CirCirCbvtype WHERE code = :code")
	CirCbvtype findByCode(@Param("code") String code);

	@Query("FROM CirCirCbvtype WHERE actief= 1")
	List<CirCbvtype> findAllActief();
	
}
