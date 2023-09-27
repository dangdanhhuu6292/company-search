package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.CIKvKCurator;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CIKvKCuratorRepositoryCustom {

	@Query("FROM CIKvKCurator WHERE naam = :naam AND CIKvKDossier.cikvKdossierId = :dossierId")
	CIKvKCurator findByNaamAndKvKDossierId(@Param("naam") String naam, @Param("dossierId") Integer dossierId);
}