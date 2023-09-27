package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.KortingsCode;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;

public interface KortingsCodeRepositoryCustom {

	@Query("FROM KortingsCode WHERE code = :code AND actief = TRUE AND (geldigVan > :date OR geldigTot < :date)")
	KortingsCode checkIfCodeIsExpired(@Param("code") String code, @Param("date") Date currentDate);

	@Query("FROM KortingsCode WHERE code = :code AND actief = TRUE AND geldigVan <= :date AND geldigTot >= :date")
	KortingsCode checkIfCodeIsValid(@Param("code") String code, @Param("date") Date currentDate);
}