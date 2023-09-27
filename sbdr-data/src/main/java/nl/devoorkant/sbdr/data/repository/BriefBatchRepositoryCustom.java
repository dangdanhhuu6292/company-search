package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.BriefBatch;
import nl.devoorkant.sbdr.data.model.InternalProcess;
import nl.devoorkant.sbdr.data.model.Klant;
import nl.devoorkant.sbdr.data.model.Melding;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface BriefBatchRepositoryCustom {

	@Query("SELECT iP " +
			"FROM InternalProcess iP " +
			"WHERE iP.briefBatch.briefBatchId = :batchId")
	InternalProcess findByBriefBatchId(@Param("batchId") Integer batchId);

	@Query("SELECT k " +
			"FROM Klant k " +
			"WHERE k.briefBatch.briefBatchId = :batchId")
	List<Klant> findCustomersOfBatch(@Param("batchId") Integer batchId);

	@Query("SELECT m " +
			"FROM Melding m " +
			"WHERE m.briefBatch.briefBatchId = :batchId")
	List<Melding> findNotificationsOfBatch(@Param("batchId") Integer batchId);

	@Query("SELECT b FROM BriefBatch b " + "WHERE b.briefBatchStatus.code = 'VTD' ")
	Page<BriefBatch> findPageOfProcessedBriefBatches(Pageable pageable);

	@Query("SELECT k " +
			"FROM Bedrijf b, Klant k " +
			"WHERE k.actief = true " +
			"AND k.bedrijf.bedrijfId = b.bedrijfId " +
			"AND k.klantStatus.code IN ('PRO') " +
			"AND k.briefStatus.code = 'NVW' " +
			"AND (k.datumAangemaakt BETWEEN :batchStartDate AND :batchEndDate)")
	List<Klant> getNewCustomerLetterBatch(@Param("batchStartDate")Date batchStartDate, @Param("batchEndDate")Date batchEndDate);

	@Query("SELECT m " +
			"FROM Bedrijf b " +
			"JOIN b.meldingsForMeldingOverBedrijfId m, Klant k " +
			"WHERE k.bedrijf = m.bedrijfByMeldingDoorBedrijfId  AND k.klantStatus.code = 'ACT'" +
			"AND m.meldingStatus.code IN ('INI', 'INB') " +
			"AND m.briefStatus.code = 'KDL' " +
			"AND (m.datumIngediend BETWEEN :batchStartDate AND :batchEndDate)")
	List<Melding> getNewNotificationLetterBatch(@Param("batchStartDate")Date batchStartDate, @Param("batchEndDate")Date batchEndDate);
}
