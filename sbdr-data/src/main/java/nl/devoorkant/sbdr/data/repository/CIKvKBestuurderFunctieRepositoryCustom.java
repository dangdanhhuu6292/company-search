package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.CIKvKBestuurderFunctie;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CIKvKBestuurderFunctieRepositoryCustom {

	@Query("FROM CIKvKBestuurderFunctie WHERE functie = :functie AND CIKvKBestuurder.cikvKbestuurderId = :bestuurderId")
	CIKvKBestuurderFunctie findByFunctieAndBestuurderId(@Param("functie") String functie, @Param("bestuurderId") Integer bestuurderId);
}
