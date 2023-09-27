package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.CIKvKHistorieCurator;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CIKvKHistorieCuratorRepositoryCustom {

	@Query("FROM CIKvKHistorieCurator WHERE naam = :naam AND CIKvKDossierHistorie.cikvKdossierHistorieId = :historieDossierId")
	CIKvKHistorieCurator findByNaamAndKvKDossierHistorieId(@Param("naam") String naam, @Param("historieDossierId") Integer historieDossierId);
}
