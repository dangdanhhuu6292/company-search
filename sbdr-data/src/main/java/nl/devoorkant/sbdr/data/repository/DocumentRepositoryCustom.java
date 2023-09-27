package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * Extension for GebruikerRepository bean.
 * <p/>
 * SBDR - Stichting Bedrijfskrediet Data Registratie
 * <p/>
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author Jan Meekel
 * @version %I%
 */

public interface DocumentRepositoryCustom {

	// 'ABR', 'MBR', 'REP', ...
	@Query("SELECT d.documentType, " +
			"COUNT(d.documentType) AS aantal " +
			"FROM Document d " +
			"WHERE d.actief = TRUE " +
			"GROUP BY d.documentType")
	List<Object[]> findAantalDocumentenPerType();

	@Query("SELECT CASE WHEN (HOUR(NOW()) < h.hourNr) THEN (h.hourNr -24) ELSE h.hourNr END AS HourOrder, " +
			"h.hourNr AS hourNr, " +
			"CONCAT(h.hourNr, ':00-', h.hourNr+1, ':00') AS HourDesc, " +
			"(SELECT COUNT(d.datumAangemaakt) " +
			"FROM Document d " +
			"WHERE d.documentType IN ('REP', 'MOC') " +
			"AND d.actief = TRUE " +
			"AND HOUR(d.datumAangemaakt) = h.hourNr " +
			"AND (d.datumAangemaakt BETWEEN :oneDayFromNow AND :now " +
			"OR d.datumAangemaakt IS NULL)) AS aantal " +
			"FROM Hours h\n" +
			"ORDER BY 1")
	List<Object[]> findAllRequestedReportsLast24h(@Param("oneDayFromNow") Date oneDayFromNow, @Param("now") Date now);

	@Query("FROM Document d WHERE d.bedrijfByBedrijfId.bedrijfId = :bId AND d.actief = TRUE")
	List<Document> findDocumentsByBedrijfId(@Param("bId") Integer bedrijfId);

	@Query("FROM Document d " + "WHERE d.referentieNummer = :referentie AND d.actief = TRUE")
	List<Document> findDocumentByReference(@Param("referentie") String referentie);

	@Query("FROM Document d " +
			"WHERE d.bedrijfByBedrijfId.bedrijfId = :bedrijfId " +
			"AND d.documentType = 'ABR' " +
			"AND d.actief = TRUE")
	List<Document> findNewAccountLetterByBedrijfId(@Param("bedrijfId") Integer bedrijfId);

	@Query("SELECT d " +
			"FROM Melding m " +
			"JOIN m.document d " +
			"WHERE m.meldingId = :meldingId " +
			"AND d.documentType.code = 'MBR' " +
			"AND d.actief = TRUE")
	Document findNotificationLetterByMeldingId(@Param("meldingId") Integer meldingId);

	// Reports of all Bedrijven

	@Query("SELECT d, d.documentType.code, " +
			"d.gebruiker " +
			"FROM Document d " +
			"WHERE d.bedrijfByBedrijfId.bedrijfId = :bedrijfId " +
			"AND d.documentType.code IN ('REP', 'MOC') " +
			"AND d.actief = TRUE " +
			"ORDER BY d.datumAangemaakt DESC")
	Page<Object[]> findRequestedReportsByGebruikerIdBedrijfId(@Param("bedrijfId") Integer bedrijfId, Pageable pageable);

	@Query("SELECT d, d.documentType.code, " +
			"d.gebruiker " +
			"FROM Document d " +
			"WHERE d.bedrijfByBedrijfId.bedrijfId = :bedrijfId " +
			"AND d.bedrijfByRapportBedrijfId.kvKnummer LIKE :kvkNummer " +
			"AND d.documentType.code IN ('REP', 'MOC') " +
			"AND d.actief = TRUE " +
			"ORDER BY d.datumAangemaakt DESC")
	Page<Object[]> findRequestedReportsByGebruikerIdBedrijfId_SearchKvkNummer(@Param("bedrijfId") Integer bedrijfId, @Param("kvkNummer") String kvkNummer, Pageable pageable);

	@Query("SELECT d, d.documentType.code, " +
			"d.gebruiker " +
			"FROM Document d " +
			"WHERE d.bedrijfByBedrijfId.bedrijfId = :bedrijfId " +
			"AND d.bedrijfByRapportBedrijfId.bedrijfsNaam LIKE :bedrijfsNaam " +
			"AND d.documentType.code IN ('REP', 'MOC') " +
			"AND d.actief = TRUE")
	Page<Object[]> findRequestedReportsByGebruikerIdBedrijfId_SearchName(@Param("bedrijfId") Integer bedrijfId, @Param("bedrijfsNaam") String bedrijfsNaam, Pageable pageable);

	@Query("SELECT d, d.documentType.code, " +
			"d.gebruiker " +
			"FROM Document d " +
			"WHERE d.bedrijfByBedrijfId.bedrijfId = :bedrijfId " +
			"AND (d.bedrijfByRapportBedrijfId.kvKnummer LIKE :nummer " +
			"OR d.bedrijfByRapportBedrijfId.sbdrNummer LIKE :nummer " +
			"OR d.referentieNummer LIKE :repNummer) " +
			"AND d.documentType.code IN ('REP', 'MOC') " +
			"AND d.actief = TRUE")
	Page<Object[]> findRequestedReportsByGebruikerIdBedrijfId_SearchNummer(@Param("bedrijfId") Integer bedrijfId, @Param("nummer") String nummer, @Param("repNummer") String repNummer, Pageable pageable);

	@Query("SELECT d, d.documentType.code, " +
			"d.gebruiker " +
			"FROM Document d " +
			"WHERE d.bedrijfByBedrijfId.bedrijfId = :bedrijfId " +
			"AND d.referentieNummer LIKE :nummer " +
			"AND d.documentType.code IN ('REP', 'MOC') " +
			"AND d.actief = TRUE")
	Page<Object[]> findRequestedReportsByGebruikerIdBedrijfId_SearchRepNummer(@Param("bedrijfId") Integer bedrijfId, @Param("nummer") String nummer, Pageable pageable);


	// Reports of specific Bedrijf

	@Query("SELECT d, d.documentType.code, " +
			"d.gebruiker " +
			"FROM Document d " +
			"WHERE d.bedrijfByBedrijfId.bedrijfId = :bedrijfId " +
			"AND d.bedrijfByRapportBedrijfId.sbdrNummer LIKE :nummer " +
			"AND d.documentType.code IN ('REP', 'MOC') " +
			"AND d.actief = TRUE " +
			"ORDER BY d.datumAangemaakt DESC")
	Page<Object[]> findRequestedReportsByGebruikerIdBedrijfId_SearchSbdrNummer(@Param("bedrijfId") Integer bedrijfId, @Param("nummer") String nummer, Pageable pageable);

	@Query("FROM Document d " +
			"WHERE d.bedrijfByRapportBedrijfId.bedrijfId = :bedrijfId " +
			"AND d.documentType.code IN ('REP', 'MOC') " +
			"AND d.actief = TRUE " +
			"ORDER BY d.datumAangemaakt DESC")
	List<Document> findRequestedReportsOfBedrijfId(@Param("bedrijfId") Integer bedrijfId);

}
