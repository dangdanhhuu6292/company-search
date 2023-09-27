package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.CIKvKAandeelhouder;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CIKvKAandeelhouderRepositoryCustom {

	@Query("FROM CIKvKAandeelhouder WHERE naam = :naam AND CIKvKDossier.cikvKdossierId = :dossierId")
	CIKvKAandeelhouder findByNaamAndKvKDossierId(@Param("naam") String naam, @Param("dossierId") Integer dossierId);
}