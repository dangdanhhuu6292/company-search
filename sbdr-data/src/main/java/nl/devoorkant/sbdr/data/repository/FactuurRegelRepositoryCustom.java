package nl.devoorkant.sbdr.data.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface FactuurRegelRepositoryCustom {

	@Query("SELECT SUM(bedrag) FROM FactuurRegel WHERE factuur.factuurId = :factuurId")
	List<BigDecimal> findTotalOfFactuurById(@Param("factuurId") Integer factuurId);
}
