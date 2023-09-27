package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.CIKvKBestuurder;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CIKvKBestuurderRepositoryCustom {

	@Query("FROM CIKvKBestuurder WHERE naam = :naam AND CIKvKDossier.cikvKdossierId = :dossierId")
	CIKvKBestuurder findByNaamAndKvKDossierId(@Param("naam") String naam, @Param("dossierId") Integer dossierId);
}