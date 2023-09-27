package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.MobileGebruiker;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MobileGebruikerRepositoryCustom {

	@Query("FROM MobileGebruiker mg WHERE mg.gebruiker.gebruikerId = :gId AND mg.actief = TRUE")
	List<MobileGebruiker> findByGebruikerId(@Param("gId") Integer gId);

	@Query("FROM MobileGebruiker mg WHERE mg.gebruiker.gebruikerId = :gId AND mobileClientKey = :key AND mg.actief = TRUE")
	MobileGebruiker findByGebruikerIdAndKey(@Param("gId") Integer gId, @Param("key") String key);

	@Query("FROM MobileGebruiker mg WHERE mg.mobileClientKey = :key AND mg.actief = TRUE")
	MobileGebruiker findByKey(@Param("key") String key);
}
