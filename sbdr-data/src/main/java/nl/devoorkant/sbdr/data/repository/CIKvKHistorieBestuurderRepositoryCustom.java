package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.CIKvKHistorieBestuurder;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CIKvKHistorieBestuurderRepositoryCustom {

	@Query("FROM CIKvKHistorieBestuurder WHERE naam = :naam AND CIKvKDossierHistorie.cikvKdossierHistorieId = :historieDossierId")
	CIKvKHistorieBestuurder findByNaamAndKvKDossierHistorieId(@Param("naam") String naam, @Param("historieDossierId") Integer historieDossierId);
}
