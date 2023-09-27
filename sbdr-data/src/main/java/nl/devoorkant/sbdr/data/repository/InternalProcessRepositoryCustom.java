package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.InternalProcess;
import nl.devoorkant.sbdr.data.model.Klant;
import nl.devoorkant.sbdr.data.model.Melding;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface InternalProcessRepositoryCustom {
	@Query("SELECT i FROM InternalProcess i " +
			"WHERE i.internalProcessStatus.code NOT IN('BVD', 'BVW')")
	Page<InternalProcess> findPageOfNewProcessRows(Pageable pageable);

	@Query("SELECT k " +
			"FROM InternalProcess iP JOIN iP.klant k " +
			"WHERE iP.internalProcessId = :processId")
	Klant findKlantOfInternalProcess(@Param("processId")Integer internalProcessId);

	@Query("SELECT m " +
			"FROM InternalProcess iP JOIN iP.melding m " +
			"WHERE iP.internalProcessId = :processId")
	Melding findMeldingOfInternalProcess(@Param("processId")Integer internalProcessId);
}
