package nl.devoorkant.sbdr.cir.data.repository;

import java.util.List;

import nl.devoorkant.sbdr.cir.data.model.Configuratie;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CirConfiguratieRepositoryCustom {
	
	@Query("FROM CirConfiguratie WHERE code = :code")
	Configuratie findByCode(@Param("code") String code);

	
	@Query("FROM CirConfiguratie WHERE indTonen = :indTonen")
	List<Configuratie> findByIndTonen(@Param("indTonen") boolean indTonen);
}
