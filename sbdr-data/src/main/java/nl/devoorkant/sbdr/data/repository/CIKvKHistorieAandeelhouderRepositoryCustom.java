package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.CIKvKHistorieAandeelhouder;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CIKvKHistorieAandeelhouderRepositoryCustom {

	@Query("FROM CIKvKHistorieAandeelhouder WHERE naam = :naam AND CIKvKDossierHistorie.cikvKdossierHistorieId = :historieDossierId")
	CIKvKHistorieAandeelhouder findByNaamAndKvKDossierHistorieId(@Param("naam") String naam, @Param("historieDossierId") Integer historieDossierId);
}
