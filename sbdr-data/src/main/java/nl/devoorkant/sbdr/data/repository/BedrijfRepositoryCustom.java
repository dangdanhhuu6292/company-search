package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Extension for BedrijfRepository bean.
 * <p/>
 * SBDR - Stichting Bedrijfskrediet Data Registratie
 * <p/>
 * Copyright:    Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:     De Voorkant B.V.
 *
 * @author Jan Meekel
 * @version %I%
 */

public interface BedrijfRepositoryCustom {

	// ('ACT', 'BLK', 'PRO', 'REG', ...)
	@Query("SELECT k.actief, " +
			"k.klantStatus.code, " +
			"COUNT(k.klantStatus) AS aantal " +
			"FROM Klant k " +
			"GROUP BY k.actief, k.klantStatus")
	List<Object[]> findAantalKlantenPerActiveKlantStatus();

	//'INI', 'INB', 'ACT', ...
	@Query("SELECT m.meldingStatus.code, " +
			"COUNT(m.meldingStatus) AS aantal " +
			"FROM Melding m " +
			"GROUP BY m.meldingStatus")
	List<Object[]> findAantalMeldingenPerStatus();

	//'ACT', ...
	@Query("SELECT m.monitoringStatus.code, " +
			"COUNT(m.monitoringStatus) AS aantal " +
			"FROM Monitoring m GROUP BY m.monitoringStatus")
	List<Object[]> findAantalMonitoringPerStatus();

	@Query("SELECT COUNT(m.monitoringId) AS aantal " +
			"FROM Monitoring m " +
			"WHERE m.monitoringStatus.code = 'ACT' " +
			"AND m.datumStart > :fromDate")
	List<Long> findAantalNieuweMonitoring(@Param("fromDate") Date fromDate);

	@Query("SELECT COUNT(d.documentId) AS aantal " +
			"FROM Document d " +
			"WHERE (d.documentType.code = 'REP' " +
			"OR d.documentType = 'MOC') " +
			"AND d.datumAangemaakt > :fromDate")
	List<Long> findAantalNieuweRapporten(@Param("fromDate") Date fromDate);

	@Query("SELECT COUNT(d.documentId) AS aantal " +
			"FROM Document d " +
			"WHERE d.documentType.code = 'REP' " +
			"OR d.documentType = 'MOC'")
	List<Long> findAantalRapporten();

	@Query("SELECT aV.alertId, " +
			"aV.datum, " +
			"aV.meldingId, " +
			"aV.referentieNummer, " +
			"s " +
			"FROM AlertView AS aV, Support AS s " +
			"WHERE aV.supportId = s.supportId " +
			"AND aV.type = 'SUP' " +
			"AND aV.bestemdVoorBedrijfId =:bId " +
			"AND ((s.supportType.code != 'BZW' " +
			"AND s.supportStatus.code = 'OPN') " +
			"OR (s.supportType.code = 'BZW' " +
			"AND s.supportStatus.code = 'GST')) " + 
			"ORDER BY aV.datum ASC")
	Page<Object[]> findActiveSupportAlertsForSbdrAdmin(@Param("bId") Integer bId, Pageable p);

	@Query("SELECT aV.alertId, " +
			"aV.datum, " +
			"aV.meldingId, " +
			"aV.referentieNummer, " +
			"s " +
			"FROM AlertView AS aV, Support AS s " +
			"WHERE aV.supportId = s.supportId " +
			"AND aV.type = 'SUP' " +
			"AND aV.bestemdVoorBedrijfId =:bId " +
			"AND (s.supportType.code != 'BZW' " +
			"AND s.supportStatus.code = 'OPN') " + 
			"ORDER BY aV.datum ASC")
	Page<Object[]> findActiveSupportAlertsNoObjectionsForSbdrAdmin(@Param("bId")Integer bId, Pageable p);

	@Query("SELECT aV.alertId, " +
			"aV.datum, " +
			"aV.meldingId, " +
			"aV.referentieNummer, " +
			"s " +
			"FROM AlertView AS aV, Support AS s " +
			"WHERE aV.supportId = s.supportId " +
			"AND aV.type = 'SUP' " +
			"AND aV.bestemdVoorBedrijfId =:bId " +
			"AND (s.supportType.code = 'BZW' " +
			"AND s.supportStatus.code = 'GST') " + 
			"ORDER BY aV.datum ASC")
	Page<Object[]> findActiveObjectionsForSbdrAdmin(@Param("bId") Integer bId, Pageable p);

	@Query("SELECT aV.alertId, " +
			"aV.datum, " +
			"aV.meldingId, " +
			"aV.referentieNummer, " +
			"s " +
			"FROM AlertView AS aV, Support AS s " +
			"WHERE aV.supportId = s.supportId " +
			"AND aV.type = 'SUP' " +
			"AND aV.bestemdVoorBedrijfId =:bId " +
			"AND (s.supportType.code != 'BZW' " +
			"AND s.supportStatus.code = 'OPN') "	+ 
			"ORDER BY aV.datum ASC")
	Page<Object[]> findActiveSupportTicketsForSbdrAdmin(@Param("bId") Integer bId, Pageable p);
	
	//	MBR change
	//	to hoofd
	//	+neven
	//	@Query("SELECT b, mon.bedrijfByMonitoringVanBedrijfId.bedrijfId, m.referentieNummerIntern, m.meldingStatus.code, m.datumGeaccordeerd, b.CIKvKDossier.aanvangFaillissement, b.CIKvKDossier.indicatieFaillissement, b.CIKvKDossier.aanvangSurseance, b.CIKvKDossier.indicatieSurseance, CASE b.bedrijfId WHEN :bedrijfId THEN 1 ELSE 0 END AS indEigenBedrijf, CASE mon.bedrijfByMonitoringVanBedrijfId.bedrijfId WHEN b.bedrijfId THEN 1 ELSE 0 END AS indMonitoringBedrijf " +
	//			"FROM Bedrijf b " +
	//			"JOIN b.meldingsForMeldingOverBedrijfId m, Bedrijf ba " +
	//			"JOIN ba.monitoringsForMonitoringDoorBedrijfId mon " +
	//
	//			"WHERE mon.bedrijfByMonitoringDoorBedrijfId.bedrijfId = :bedrijfId " +
	//			"AND m.bedrijfByMeldingOverBedrijfId.kvKnummer = mon.bedrijfByMonitoringVanBedrijfId.kvKnummer " +
	//			"AND m.meldingStatus.code ='ACT' " + // 'INI', 'INB',
	//			"AND m.gebruikerByMeldingDoorGebruikerId.gebruikerId != :userId " +
	//			"AND mon.monitoringStatus.code = 'ACT' " +
	//			"AND (mon.datumLaatsteCheck IS NULL " +
	//			"OR mon.datumLaatsteCheck <= m.datumGeaccordeerd) " +
	//
	//			"ORDER BY m.datumGeaccordeerd DESC")
	@Query("SELECT aV.alertId, " +
			"aV.type, " +
			"aV.bedrijf, " +
			"aV.meldingId, " +
			"aV.monitoringId, " +
			"aV.supportId, " +
			"aV.referentieNummer, " +
			"aV.supportType, " +
			"aV.meldingStatus, " +
			"aV.datum, " +
			"aV.aanvangFaillissement, " +
			"aV.indicatieFaillissement, " +
			"aV.aanvangSurseance, " +
			"aV.indicatieSurseance, " +
			"aV.indEigenBedrijf, " +
			"aV.indMonitoringBedrijf " +
			"FROM AlertView AS aV " +
			"WHERE (((aV.type = 'SUP' " +
			"AND aV.bestemdVoorBedrijfId = :bedrijfId) " +
			"OR (aV.type = 'VER' " +
			"AND aV.bestemdVoorBedrijfId = :bedrijfId) " +
			"OR (aV.type = 'VVM' " +
			"AND aV.bestemdVoorBedrijfId = :bedrijfId) " +
			"OR (aV.type = 'MON' " +
			"AND aV.monitoringDoorBedrijfId = :bedrijfId)) " + 
			"AND (aV.gebruikerId is null " +
			"OR aV.gebruikerId = :userId))")
	Page<Object[]> findActiveAlertsOfBedrijf(@Param("bedrijfId") Integer bedrijfId, @Param("userId") Integer userId, Pageable pageable);

	@Query("SELECT aV.alertId, " +
			"aV.type, " +
			"aV.bedrijf, " +
			"aV.meldingId, " +
			"aV.monitoringId, " +
			"aV.supportId, " +
			"aV.referentieNummer, " +
			"aV.supportType, " +
			"aV.meldingStatus, " +
			"aV.datum, " +
			"aV.aanvangFaillissement, " +
			"aV.indicatieFaillissement, " +
			"aV.aanvangSurseance, " +
			"aV.indicatieSurseance, " +
			"aV.indEigenBedrijf, " +
			"aV.indMonitoringBedrijf " +
			"FROM AlertView AS aV " +
			"WHERE (((aV.type = 'SUP' " +
			"AND aV.bestemdVoorBedrijfId = :bedrijfId) " +
			"OR (aV.type = 'VER' " +
			"AND aV.bestemdVoorBedrijfId = :bedrijfId) " +
			"OR (aV.type = 'VVM' " +
			"AND aV.bestemdVoorBedrijfId = :bedrijfId)) " +
			"AND (aV.gebruikerId is null " +
			"OR aV.gebruikerId = :userId))")
	Page<Object[]> findActiveAlertsNoMonitoringOfBedrijf(@Param("bedrijfId") Integer bedrijfId, @Param("userId") Integer userId, Pageable pageable);

	@Query("SELECT aV.alertId, " +
			"aV.type, " +
			"aV.bedrijf, " +
			"aV.meldingId, " +
			"aV.monitoringId, " +
			"aV.supportId, " +
			"aV.referentieNummer, " +
			"aV.supportType, " +
			"aV.meldingStatus, " +
			"aV.datum, " +
			"aV.aanvangFaillissement, " +
			"aV.indicatieFaillissement, " +
			"aV.aanvangSurseance, " +
			"aV.indicatieSurseance, " +
			"aV.indEigenBedrijf, " +
			"aV.indMonitoringBedrijf " +
			"FROM AlertView AS aV " +
			"WHERE ((aV.type = 'MON' " +
			"AND aV.monitoringDoorBedrijfId = :bedrijfId) " + 
			"AND (aV.gebruikerId is null " +
			"OR aV.gebruikerId = :userId))")
	Page<Object[]> findActiveMonitoringAlertsOfBedrijf(@Param("bedrijfId") Integer bedrijfId, @Param("userId") Integer userId, Pageable pageable);
	
	
	// For Mobile monitoring tab
	// Shows all monitoring records with active notification
	// In 'fake' alertview notation
	@Query("SELECT 0, " +
			"'MON', " +
			"b, " +
			"m.meldingId, " +
			"mon.monitoringId, " +
			"0, " +
			"m.referentieNummerIntern, " +
			"'', " +
			"m.meldingStatus.code, " +
			"m.datumGeaccordeerd, " + // is actually date start of alert!
			"b.CIKvKDossier.aanvangFaillissement, " +
			"b.CIKvKDossier.indicatieFaillissement, " +
			"b.CIKvKDossier.aanvangSurseance, " +
			"b.CIKvKDossier.indicatieSurseance, " +
			"CASE b.bedrijfId WHEN :bedrijfId THEN 1 ELSE 0 END AS indEigenBedrijf, " +
			"CASE mon.bedrijfByMonitoringVanBedrijfId.bedrijfId WHEN b.bedrijfId THEN 1 ELSE 0 END AS indMonitoringBedrijf " +			
			"FROM Bedrijf b " +
			"JOIN b.meldingsForMeldingOverBedrijfId m, Bedrijf ba " +
			"JOIN ba.monitoringsForMonitoringDoorBedrijfId mon " +
			"WHERE mon.bedrijfByMonitoringDoorBedrijfId.bedrijfId = :bedrijfId " +
			"AND m.bedrijfByMeldingOverBedrijfId.kvKnummer = mon.bedrijfByMonitoringVanBedrijfId.kvKnummer " +
			"AND m.meldingStatus.code = 'ACT' " + // 'INI', 'INB',
			"AND mon.monitoringStatus.code = 'ACT' " +	
			// Conditions under are for 'removal' of monitoring records in the view after user has 'checked' status
			//"AND ((mon.datumLaatsteCheck IS NULL " +
			//"AND mon.datumStart <= m.datumGeaccordeerd) " +
			//"OR mon.datumLaatsteCheck <= m.datumGeaccordeerd) " +
			
			"ORDER BY m.bedrijfByMeldingOverBedrijfId.bedrijfId, m.datumGeaccordeerd DESC")
	Page<Object[]> findActiveMonitoringNotificationsOfBedrijf(@Param("bedrijfId") Integer bedrijfId, Pageable pageable);	
	
	// MBR change to hoofd + neven
	@Query("SELECT b, " +
			"mon.bedrijfByMonitoringVanBedrijfId.bedrijfId, " +
			"m.referentieNummerIntern, " +
			"m.meldingStatus.code, " +
			"m.datumGeaccordeerd, " +
			"b.CIKvKDossier.aanvangFaillissement, " +
			"b.CIKvKDossier.indicatieFaillissement, " +
			"b.CIKvKDossier.aanvangSurseance, " +
			"b.CIKvKDossier.indicatieSurseance, " +
			"CASE b.bedrijfId WHEN :bedrijfId THEN 1 ELSE 0 END AS indEigenBedrijf, " +
			"CASE mon.bedrijfByMonitoringVanBedrijfId.bedrijfId WHEN b.bedrijfId THEN 1 ELSE 0 END AS indMonitoringBedrijf " +
			"FROM Bedrijf b " +
			"JOIN b.meldingsForMeldingOverBedrijfId m, Bedrijf ba " +
			"JOIN ba.monitoringsForMonitoringDoorBedrijfId mon " +
			"WHERE mon.bedrijfByMonitoringDoorBedrijfId.bedrijfId = :bedrijfId " +
			//"AND m.bedrijfByMeldingOverBedrijfId.bedrijfId = mon.bedrijfByMonitoringVanBedrijfId.bedrijfId " +
			"AND m.bedrijfByMeldingOverBedrijfId.kvKnummer = mon.bedrijfByMonitoringVanBedrijfId.kvKnummer " +
			"AND m.bedrijfByMeldingOverBedrijfId.kvKnummer LIKE :kvKnummer " +
			"AND m.meldingStatus.code = 'ACT' " + // 'INI', 'INB',
			"AND m.gebruikerByMeldingDoorGebruikerId.gebruikerId != :userId " +
			"AND mon.monitoringStatus.code = 'ACT' " +
			"AND ((mon.datumLaatsteCheck IS NULL " +
			"AND mon.datumStart <= m.datumGeaccordeerd) " +
			"OR mon.datumLaatsteCheck <= m.datumGeaccordeerd) " +
			"ORDER BY m.datumGeaccordeerd DESC")
	Page<Object[]> findActiveAlertsOfBedrijf_SearchKvkNummer(@Param("bedrijfId") Integer bedrijfId, @Param("userId") Integer userId, @Param("kvKnummer") String kvkNummer, Pageable pageable);

	// MBR change to hoofd + neven
	@Query("SELECT b, " +
			"mon.bedrijfByMonitoringVanBedrijfId.bedrijfId, " +
			"m.referentieNummerIntern, " +
			"m.meldingStatus.code, " +
			"m.datumGeaccordeerd, " +
			"b.CIKvKDossier.aanvangFaillissement, " +
			"b.CIKvKDossier.indicatieFaillissement, " +
			"b.CIKvKDossier.aanvangSurseance, " +
			"b.CIKvKDossier.indicatieSurseance, " +
			"CASE b.bedrijfId WHEN :bedrijfId THEN 1 ELSE 0 END AS indEigenBedrijf, " +
			"CASE mon.bedrijfByMonitoringVanBedrijfId.bedrijfId WHEN b.bedrijfId THEN 1 ELSE 0 END AS indMonitoringBedrijf " +
			"FROM Bedrijf b " +
			"JOIN b.meldingsForMeldingOverBedrijfId m, Bedrijf ba " +
			"JOIN ba.monitoringsForMonitoringDoorBedrijfId mon " +
			"WHERE mon.bedrijfByMonitoringDoorBedrijfId.bedrijfId = :bedrijfId " +
			//"AND m.bedrijfByMeldingOverBedrijfId.bedrijfId = mon.bedrijfByMonitoringVanBedrijfId.bedrijfId " +
			"AND m.bedrijfByMeldingOverBedrijfId.kvKnummer = mon.bedrijfByMonitoringVanBedrijfId.kvKnummer " +
			"AND m.bedrijfByMeldingOverBedrijfId.bedrijfsNaam LIKE :bedrijfsNaam " +
			"AND m.meldingStatus.code = 'ACT' " + // 'INI', 'INB',
			"AND m.gebruikerByMeldingDoorGebruikerId.gebruikerId != :userId " +
			"AND mon.monitoringStatus.code = 'ACT' " +
			"AND ((mon.datumLaatsteCheck IS NULL " +
			"AND mon.datumStart <= m.datumGeaccordeerd) " +
			"OR mon.datumLaatsteCheck <= m.datumGeaccordeerd) " +
			"ORDER BY m.datumGeaccordeerd DESC")
	Page<Object[]> findActiveAlertsOfBedrijf_SearchName(@Param("bedrijfId") Integer bedrijfId, @Param("userId") Integer userId, @Param("bedrijfsNaam") String bedrijfsNaam, Pageable pageable);

	// MBR change to hoofd + neven
	@Query("SELECT b, " +
			"mon.bedrijfByMonitoringVanBedrijfId.bedrijfId, " +
			"m.referentieNummerIntern, " +
			"m.meldingStatus.code, " +
			"m.datumGeaccordeerd, " +
			"b.CIKvKDossier.aanvangFaillissement, " +
			"b.CIKvKDossier.indicatieFaillissement, " +
			"b.CIKvKDossier.aanvangSurseance, " +
			"b.CIKvKDossier.indicatieSurseance, " +
			"CASE b.bedrijfId WHEN :bedrijfId THEN 1 ELSE 0 END AS indEigenBedrijf, " +
			"CASE mon.bedrijfByMonitoringVanBedrijfId.bedrijfId WHEN b.bedrijfId THEN 1 ELSE 0 END AS indMonitoringBedrijf " +
			"FROM Bedrijf b " +
			"JOIN b.meldingsForMeldingOverBedrijfId m, Bedrijf ba " +
			"JOIN ba.monitoringsForMonitoringDoorBedrijfId mon " +
			"WHERE mon.bedrijfByMonitoringDoorBedrijfId.bedrijfId = :bedrijfId " +
			//"AND m.bedrijfByMeldingOverBedrijfId.bedrijfId = mon.bedrijfByMonitoringVanBedrijfId.bedrijfId " +
			"AND m.bedrijfByMeldingOverBedrijfId.kvKnummer = mon.bedrijfByMonitoringVanBedrijfId.kvKnummer " +
			"AND (m.bedrijfByMeldingOverBedrijfId.kvKnummer LIKE :nummer " +
			"OR m.bedrijfByMeldingOverBedrijfId.sbdrNummer LIKE :nummer " +
			"OR m.referentieNummerIntern LIKE :nummer) " +
			"AND m.meldingStatus.code = 'ACT' " + // 'INI', 'INB',
			"AND m.gebruikerByMeldingDoorGebruikerId.gebruikerId != :userId " +
			"AND mon.monitoringStatus.code = 'ACT' " +
			"AND ((mon.datumLaatsteCheck IS NULL " +
			"AND mon.datumStart <= m.datumGeaccordeerd) " +
			"OR mon.datumLaatsteCheck <= m.datumGeaccordeerd) " +
			"ORDER BY m.datumGeaccordeerd DESC")
	Page<Object[]> findActiveAlertsOfBedrijf_SearchNummer(@Param("bedrijfId") Integer bedrijfId, @Param("userId") Integer userId, @Param("nummer") String nummer, Pageable pageable);

	@Query("SELECT b, " +
			"mon.bedrijfByMonitoringVanBedrijfId.bedrijfId, " +
			"m.referentieNummerIntern, " +
			"m.meldingStatus.code, " +
			"m.datumGeaccordeerd, " +
			"b.CIKvKDossier.aanvangFaillissement, " +
			"b.CIKvKDossier.indicatieFaillissement, " +
			"b.CIKvKDossier.aanvangSurseance, " +
			"b.CIKvKDossier.indicatieSurseance, " +
			"CASE b.bedrijfId WHEN :bedrijfId THEN 1 ELSE 0 END AS indEigenBedrijf, " +
			"CASE mon.bedrijfByMonitoringVanBedrijfId.bedrijfId WHEN b.bedrijfId THEN 1 ELSE 0 END AS indMonitoringBedrijf " +
			"FROM Bedrijf b " +
			"JOIN b.meldingsForMeldingOverBedrijfId m, Bedrijf ba " +
			"JOIN ba.monitoringsForMonitoringDoorBedrijfId mon " +
			"WHERE mon.bedrijfByMonitoringDoorBedrijfId.bedrijfId = :bedrijfId " +
			//"AND m.bedrijfByMeldingOverBedrijfId.bedrijfId = mon.bedrijfByMonitoringVanBedrijfId.bedrijfId " +
			"AND m.bedrijfByMeldingOverBedrijfId.kvKnummer = mon.bedrijfByMonitoringVanBedrijfId.kvKnummer " +
			"AND m.bedrijfByMeldingOverBedrijfId.sbdrNummer LIKE :nummer " +
			"AND m.meldingStatus.code = 'ACT' " + // 'INI', 'INB',
			"AND m.gebruikerByMeldingDoorGebruikerId.gebruikerId != :userId " +
			"AND mon.monitoringStatus.code = 'ACT' " +
			"AND ((mon.datumLaatsteCheck IS NULL " +
			"AND mon.datumStart <= m.datumGeaccordeerd) " +
			"OR mon.datumLaatsteCheck <= m.datumGeaccordeerd) " +
			"ORDER BY m.datumGeaccordeerd DESC")
	Page<Object[]> findActiveAlertsOfBedrijf_SearchSbdrNummer(@Param("bedrijfId") Integer bedrijfId, @Param("userId") Integer userId, @Param("nummer") String nummer, Pageable pageable);

	// MBR change to hoofd + neven
	@Query("SELECT b, " +
			"mon.bedrijfByMonitoringVanBedrijfId.bedrijfId, " +
			"m.referentieNummerIntern, " +
			"m.meldingStatus.code, " +
			"m.datumGeaccordeerd, " +
			"b.CIKvKDossier.aanvangFaillissement, " +
			"b.CIKvKDossier.indicatieFaillissement, " +
			"b.CIKvKDossier.aanvangSurseance, " +
			"b.CIKvKDossier.indicatieSurseance, " +
			"CASE b.bedrijfId WHEN :bedrijfId THEN 1 ELSE 0 END AS indEigenBedrijf, " +
			"CASE mon.bedrijfByMonitoringVanBedrijfId.bedrijfId WHEN b.bedrijfId THEN 1 ELSE 0 END AS indMonitoringBedrijf " +
			"FROM Bedrijf b " +
			"JOIN b.meldingsForMeldingOverBedrijfId m, Bedrijf ba " +
			"JOIN ba.monitoringsForMonitoringDoorBedrijfId mon " +
			"WHERE mon.bedrijfByMonitoringDoorBedrijfId.bedrijfId = :bedrijfId " +
			//"AND m.bedrijfByMeldingOverBedrijfId.bedrijfId = mon.bedrijfByMonitoringVanBedrijfId.bedrijfId " +
			"AND m.bedrijfByMeldingOverBedrijfId.kvKnummer = mon.bedrijfByMonitoringVanBedrijfId.kvKnummer " +
			"AND m.referentieNummerIntern LIKE :nummer " +
			"AND m.meldingStatus.code = 'ACT' " + // 'INI', 'INB',
			"AND m.gebruikerByMeldingDoorGebruikerId.gebruikerId != :userId " +
			"AND mon.monitoringStatus.code = 'ACT' " +
			"AND ((mon.datumLaatsteCheck IS NULL " +
			"AND mon.datumStart <= m.datumGeaccordeerd) " +
			"OR mon.datumLaatsteCheck <= m.datumGeaccordeerd) " +
			"ORDER BY m.datumGeaccordeerd DESC")
	Page<Object[]> findActiveAlertsOfBedrijf_SearchVeNummer(@Param("bedrijfId") Integer bedrijfId, @Param("userId") Integer userId, @Param("nummer") String nummer, Pageable pageable);

	@Query("SELECT g " +
			"FROM Gebruiker g " +
			"WHERE g.bedrijf.bedrijfId = :bedrijfid " +
			"AND g.actief = TRUE")
	Page<Gebruiker> findActiveGebruikersOfBedrijf(@Param("bedrijfid") Integer bedrijfId, Pageable pageable);

	//admin related used for search of NON-active notifications of ACT customers OR search of NON-active notifications of PRO customers
	@Query("SELECT m " +
			"FROM Bedrijf b " +
			"JOIN b.meldingsForMeldingOverBedrijfId m, Klant k " +
			"WHERE k.bedrijf = m.bedrijfByMeldingDoorBedrijfId AND k.klantStatus.code IN (:klantStatusCodes)" +
			"AND m.meldingStatus.code IN (:meldingStatusCodes) " + //, 'ACT'
			"ORDER BY m.datumIngediend DESC, m.meldingStatus.code ASC")
	Page<Melding> findMeldingenOfAllBedrijven(@Param("klantStatusCodes") Collection<String> klantStatusCodes, @Param("meldingStatusCodes") Collection<String> meldingStatusCodes, Pageable pageable);

	@Query("SELECT m " +
			"FROM Bedrijf b " +
			"JOIN b.meldingsForMeldingOverBedrijfId m, Klant k " +
			"WHERE k.bedrijf = m.bedrijfByMeldingDoorBedrijfId AND k.klantStatus.code IN (:klantStatusCodes)" +
			"AND m.meldingStatus.code IN (:meldingStatusCodes) " + //, 'ACT'
			"AND m.bedrijfByMeldingOverBedrijfId.kvKnummer LIKE :kvKnummer " +
			"ORDER BY m.datumIngediend DESC, m.meldingStatus.code ASC")
	Page<Melding> findMeldingenOfAllBedrijven_SearchKvkNummer(@Param("kvKnummer") String kvkNummer, @Param("klantStatusCodes") Collection<String> klantStatusCodes, @Param("meldingStatusCodes") Collection<String> meldingStatusCodes, Pageable pageable);

	@Query("SELECT m " +
			"FROM Bedrijf b " +
			"JOIN b.meldingsForMeldingOverBedrijfId m, Klant k " +
			"WHERE k.bedrijf = m.bedrijfByMeldingDoorBedrijfId AND k.klantStatus.code IN (:klantStatusCodes)" +
			"AND m.meldingStatus.code IN (:meldingStatusCodes) " + //, 'ACT'
			"AND (m.bedrijfByMeldingOverBedrijfId.bedrijfsNaam LIKE :bedrijfsNaam " +
			"OR alphanum(m.referentieNummer) LIKE alphanum(:bedrijfsNaam)) " +
			"ORDER BY m.datumIngediend DESC, m.meldingStatus.code ASC")
	Page<Melding> findMeldingenOfAllBedrijven_SearchName(@Param("bedrijfsNaam") String bedrijfsNaam, @Param("klantStatusCodes") Collection<String> klantStatusCodes, @Param("meldingStatusCodes") Collection<String> meldingStatusCodes, Pageable pageable);

	@Query("SELECT m " +
			"FROM Bedrijf b " +
			"JOIN b.meldingsForMeldingOverBedrijfId m, Klant k " +
			"WHERE k.bedrijf = m.bedrijfByMeldingDoorBedrijfId AND k.klantStatus.code IN (:klantStatusCodes)" +
			"AND m.meldingStatus.code IN (:meldingStatusCodes) " + //, 'ACT'
			"AND (m.bedrijfByMeldingOverBedrijfId.bedrijfsNaam LIKE :nummer " +
			"OR m.bedrijfByMeldingOverBedrijfId.kvKnummer LIKE :nummer " +
			"OR m.bedrijfByMeldingOverBedrijfId.sbdrNummer LIKE :nummer " +
			"OR m.referentieNummerIntern LIKE :nummer) " +
			"ORDER BY m.datumIngediend DESC, m.meldingStatus.code ASC")
	Page<Melding> findMeldingenOfAllBedrijven_SearchNummer(@Param("nummer") String nummer, @Param("klantStatusCodes") Collection<String> klantStatusCodes, @Param("meldingStatusCodes") Collection<String> meldingStatusCodes, Pageable pageable);

	@Query("SELECT m " +
			"FROM Bedrijf b " +
			"JOIN b.meldingsForMeldingOverBedrijfId m, Klant k " +
			"WHERE k.bedrijf = m.bedrijfByMeldingDoorBedrijfId AND k.klantStatus.code IN (:klantStatusCodes)" +
			"AND m.meldingStatus.code IN (:meldingStatusCodes) " + //, 'ACT'
			"AND m.bedrijfByMeldingOverBedrijfId.sbdrNummer LIKE :nummer " +
			"ORDER BY m.datumIngediend DESC, m.meldingStatus.code ASC")
	Page<Melding> findMeldingenOfAllBedrijven_SearchSbdrNummer(@Param("nummer") String nummer, @Param("klantStatusCodes") Collection<String> klantStatusCodes, @Param("meldingStatusCodes") Collection<String> meldingStatusCodes, Pageable pageable);

	@Query("SELECT m " +
			"FROM Bedrijf b " +
			"JOIN b.meldingsForMeldingOverBedrijfId m, Klant k " +
			"WHERE k.bedrijf = m.bedrijfByMeldingDoorBedrijfId AND k.klantStatus.code IN (:klantStatusCodes)" +
			"AND m.meldingStatus.code IN (:meldingStatusCodes) " + //, 'ACT'
			"AND m.referentieNummerIntern LIKE :nummer " +
			"ORDER BY m.datumIngediend DESC, m.meldingStatus.code ASC")
	Page<Melding> findMeldingenOfAllBedrijven_SearchVeNummer(@Param("nummer") String nummer, @Param("klantStatusCodes") Collection<String> klantStatusCodes, @Param("meldingStatusCodes") Collection<String> meldingStatusCodes, Pageable pageable);

	// company owned notifications
	@Query("SELECT m " +
			"FROM Bedrijf b " +
			"JOIN b.meldingsForMeldingOverBedrijfId m " +
			"LEFT OUTER JOIN m.gebruikerByVerwijderdDoorGebruikerId g " +
			"LEFT OUTER JOIN g.rollen r " +
			"WHERE m.bedrijfByMeldingDoorBedrijfId.bedrijfId = :bedrijfId " +
			"AND (m.meldingStatus.code IN ('NOK', 'INI', 'INB', 'ACT', 'AFW', 'BLK') " +
			"OR (m.meldingStatus.code = 'DEL' " +
			"AND (r.code = 'SBDR' OR r.code = 'SBDRHOOFD'))) " +
			"ORDER BY m.datumIngediend DESC")
	Page<Melding> findActiveMeldingenOfBedrijf(@Param("bedrijfId") Integer bedrijfId, Pageable pageable);

	//	@Query("SELECT b " +
	//			"FROM Bedrijf b, Klant k " +
	//			"WHERE k.bedrijf.bedrijfId = b.bedrijfId " +
	//			"AND b.bedrijfStatus.code IN ('ACT', 'DIS')")
	//	List<Klant> findKlantBedrijvenActiveInactive();

	@Query("SELECT m " +
			"FROM Bedrijf b " +
			"JOIN b.meldingsForMeldingOverBedrijfId m " +
			"LEFT OUTER JOIN m.gebruikerByVerwijderdDoorGebruikerId g " +
			"LEFT OUTER JOIN g.rollen r " +
			"WHERE m.bedrijfByMeldingDoorBedrijfId.bedrijfId = :bedrijfId " +
			"AND m.bedrijfByMeldingOverBedrijfId.kvKnummer LIKE :kvKnummer " +
			"AND (m.meldingStatus.code IN ('NOK', 'INI', 'INB', 'ACT', 'AFW', 'BLK') " +
			"OR (m.meldingStatus.code = 'DEL' " +
			"AND (r.code = 'SBDR' OR r.code = 'SBDRHOOFD'))) " +
			"ORDER BY m.datumIngediend DESC")
	Page<Melding> findActiveMeldingenOfBedrijf_SearchKvkNummer(@Param("bedrijfId") Integer bedrijfId, @Param("kvKnummer") String kvkNummer, Pageable pageable);

	@Query("SELECT m " +
			"FROM Bedrijf b " +
			"JOIN b.meldingsForMeldingOverBedrijfId m " +
			"LEFT OUTER JOIN m.gebruikerByVerwijderdDoorGebruikerId g " +
			"LEFT OUTER JOIN g.rollen r " +
			"WHERE m.bedrijfByMeldingDoorBedrijfId.bedrijfId = :bedrijfId " +
			"AND (m.bedrijfByMeldingOverBedrijfId.bedrijfsNaam LIKE :bedrijfsNaam " +
			"OR alphanum(m.referentieNummer) LIKE alphanum(:bedrijfsNaam)) " +
			"AND (m.meldingStatus.code IN ('NOK', 'INI', 'INB', 'ACT', 'AFW', 'BLK') " +
			"OR (m.meldingStatus.code = 'DEL' " +
			"AND (r.code = 'SBDR' OR r.code = 'SBDRHOOFD'))) " +
			"ORDER BY m.datumIngediend DESC")
	Page<Melding> findActiveMeldingenOfBedrijf_SearchName(@Param("bedrijfId") Integer bedrijfId, @Param("bedrijfsNaam") String bedrijfsNaam, Pageable pageable);

	@Query("SELECT m " +
			"FROM Bedrijf b " +
			"JOIN b.meldingsForMeldingOverBedrijfId m " +
			"LEFT OUTER JOIN m.gebruikerByVerwijderdDoorGebruikerId g " +
			"LEFT OUTER JOIN g.rollen r " +
			"WHERE m.bedrijfByMeldingDoorBedrijfId.bedrijfId = :bedrijfId " +
			"AND (m.bedrijfByMeldingOverBedrijfId.bedrijfsNaam LIKE :nummer " +
			"OR m.bedrijfByMeldingOverBedrijfId.kvKnummer LIKE :nummer " +
			"OR m.bedrijfByMeldingOverBedrijfId.sbdrNummer LIKE :nummer " +
			"OR m.referentieNummerIntern LIKE :nummer) " +
			"AND (m.meldingStatus.code IN ('NOK', 'INI', 'INB', 'ACT', 'AFW', 'BLK') " +
			"OR (m.meldingStatus.code = 'DEL' " +
			"AND (r.code = 'SBDR' OR r.code = 'SBDRHOOFD'))) " +
			"ORDER BY m.datumIngediend DESC")
	Page<Melding> findActiveMeldingenOfBedrijf_SearchNummer(@Param("bedrijfId") Integer bedrijfId, @Param("nummer") String nummer, Pageable pageable);

	@Query("SELECT m " +
			"FROM Bedrijf b " +
			"JOIN b.meldingsForMeldingOverBedrijfId m " +
			"LEFT OUTER JOIN m.gebruikerByVerwijderdDoorGebruikerId g " +
			"LEFT OUTER JOIN g.rollen r " +
			"WHERE m.bedrijfByMeldingDoorBedrijfId.bedrijfId = :bedrijfId " +
			"AND m.bedrijfByMeldingOverBedrijfId.sbdrNummer LIKE :nummer " +
			"AND (m.meldingStatus.code IN ('NOK', 'INI', 'INB', 'ACT', 'AFW', 'BLK') " +
			"OR (m.meldingStatus.code = 'DEL' " +
			"AND (r.code = 'SBDR' OR r.code = 'SBDRHOOFD'))) " +
			"ORDER BY m.datumIngediend DESC")
	Page<Melding> findActiveMeldingenOfBedrijf_SearchSbdrNummer(@Param("bedrijfId") Integer bedrijfId, @Param("nummer") String nummer, Pageable pageable);

	@Query("SELECT m " +
			"FROM Bedrijf b " +
			"JOIN b.meldingsForMeldingOverBedrijfId m " +
			"LEFT OUTER JOIN m.gebruikerByVerwijderdDoorGebruikerId g " +
			"LEFT OUTER JOIN g.rollen r " +
			"WHERE m.bedrijfByMeldingDoorBedrijfId.bedrijfId = :bedrijfId " +
			"AND m.referentieNummerIntern LIKE :nummer " +
			"AND (m.meldingStatus.code IN ('NOK', 'INI', 'INB', 'ACT', 'AFW', 'BLK') " +
			"OR (m.meldingStatus.code = 'DEL' " +
			"AND (r.code = 'SBDR' OR r.code = 'SBDRHOOFD'))) " +
			"ORDER BY m.datumIngediend DESC")
	Page<Melding> findActiveMeldingenOfBedrijf_SearchVeNummer(@Param("bedrijfId") Integer bedrijfId, @Param("nummer") String nummer, Pageable pageable);

	// Active monitoring of specific company by any bedrijf
	@Query("SELECT m " +
			"FROM Bedrijf b " +
			"JOIN b.monitoringsForMonitoringVanBedrijfId m " +
			"WHERE m.bedrijfByMonitoringVanBedrijfId.bedrijfId = :bedrijfId " +
			"AND m.monitoringStatus.code = 'ACT' " +
			"ORDER BY m.datumStart DESC")
	List<Monitoring> findActiveMonitoringByAllBedrijven(@Param("bedrijfId") Integer bedrijfId);

	@Query("FROM AlertView AS aV " +
			"WHERE ((aV.type = 'SUP' " +
			"AND aV.bestemdVoorBedrijfId = :bedrijfId) " +
			"OR (aV.type = 'VVM' " +
			"AND aV.bestemdVoorBedrijfId = :bedrijfId) " +
			"OR (aV.type = 'VER' " +
			"AND aV.bestemdVoorBedrijfId = :bedrijfId) " +
			"OR (aV.type = 'MON' " +
			"AND aV.monitoringDoorBedrijfId = :bedrijfId))")
	List<AlertView> findAllActiveAlertsOfBedrijf(@Param("bedrijfId") Integer bedrijfId);

	@Query("SELECT (CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END) " +
			"FROM AlertView " +
			"WHERE ((type IN ('SUP', 'VVM', 'VER') AND bestemdVoorBedrijfId = :bedrijfId) OR (type = 'MON' AND monitoringDoorBedrijfId = :bedrijfId)) " +
			"AND gebruikerId IS NULL")
	boolean bedrijfOnlyHasBedrijfLevelAlerts(@Param("bedrijfId")Integer bedrijfId);

	// old: CASE WHEN (HOUR(NOW()) < h.hourNr) THEN (HOUR(NOW()) -24 + (HOUR(NOW())- h.hourNr)) ELSE h.hourNr END AS HourOrder, h.hourNr AS hourNr, CONCAT(h.hourNr, ':00-', h.hourNr+1, ':00') AS HourDesc
	@Query("SELECT CASE WHEN (HOUR(NOW()) < h.hourNr) THEN (h.hourNr -24) ELSE h.hourNr END AS HourOrder, " +
			"h.hourNr AS hourNr, " +
			"CONCAT(h.hourNr, ':00-', h.hourNr+1, ':00') AS HourDesc " +
			", (SELECT COUNT(k.gebruikerId) " +
			"FROM Klant k " +
			"WHERE k.klantStatus.code = 'ACT' " +
			"AND HOUR(k.datumAangemaakt) = h.hourNr " +
			"AND (k.datumAangemaakt BETWEEN :oneDayFromNow " +
			"AND :now " +
			"OR k.datumAangemaakt IS NULL)) AS aantal " +
			"FROM Hours h " +
			"ORDER BY 1")
	List<Object[]> findAllActiveKlantenLast24h(@Param("oneDayFromNow") Date oneDayFromNow, @Param("now") Date now);

	@Query("SELECT CASE WHEN (HOUR(NOW()) < h.hourNr) THEN (h.hourNr -24) ELSE h.hourNr END AS HourOrder, " +
			"h.hourNr AS hourNr, " +
			"CONCAT(h.hourNr, ':00-', h.hourNr+1, ':00') AS HourDesc " +
			", (SELECT COUNT(m.meldingId) " +
			"FROM Melding m " +
			"WHERE m.meldingStatus.code IN ('NOK', 'INI', 'INB', 'ACT') " +
			"AND HOUR(m.datumIngediend) = h.hourNr " +
			"AND (m.datumIngediend BETWEEN :oneDayFromNow " +
			"AND :now " +
			"OR m.datumIngediend IS NULL)) AS aantal " +
			"FROM Hours h " +
			"ORDER BY 1")
	List<Object[]> findAllActiveMeldingenLast24h(@Param("oneDayFromNow") Date oneDayFromNow, @Param("now") Date now);

	@Query("SELECT CASE WHEN (HOUR(NOW()) < h.hourNr) THEN (h.hourNr -24) ELSE h.hourNr END AS HourOrder, " +
			"h.hourNr AS hourNr, " +
			"CONCAT(h.hourNr, ':00-', h.hourNr+1, ':00') AS HourDesc " +
			", (SELECT COUNT(m.monitoringId) " +
			"FROM Monitoring m " +
			"WHERE m.monitoringStatus.code IN ('ACT') " +
			"AND HOUR(m.datumStart) = h.hourNr " +
			"AND (m.datumStart BETWEEN :oneDayFromNow " +
			"AND :now " +
			"OR m.datumStart IS NULL)) AS aantal " +
			"FROM Hours h " +
			"ORDER BY 1")
	List<Object[]> findAllActiveMonitoringLast24h(@Param("oneDayFromNow") Date oneDayFromNow, @Param("now") Date now);

	@Query("SELECT b " +
			"FROM Melding m JOIN m.bedrijfByMeldingDoorBedrijfId b " +
			"WHERE m.meldingStatus.code in ('INI', 'INB') " +
			"AND b.bedrijfStatus.code = 'ACT' " +
			"AND m.datumIngediend BETWEEN :startDate AND :endDate")
	List<Bedrijf> findAllBedrijvenWithNewMeldingen(@Param("startDate") Date startDate, @Param("endDate")Date endDate);

	// bedrijven with exceptions for admin to handle
	// MBR 7-12-2017 BedrijfManaged users added
	@Query("SELECT b AS bedrijf, " +
			"c AS customMelding, " +
			"b2 AS bedrijfDoor " +
			"FROM Bedrijf b, CustomMelding c, Bedrijf b2, Gebruiker g LEFT OUTER JOIN g.bedrijvenManagedDoorGebruikerId bm " +
			"WHERE g = c.gebruiker AND ((g.bedrijf.bedrijfId = b2.bedrijfId AND (bm is null OR bm.bedrijf.bedrijfId <> b2.bedrijfId)) OR (g.bedrijf.bedrijfId <> b2.bedrijfId AND bm.bedrijf.bedrijfId = b2.bedrijfId)) " +
			"AND c.bedrijf.bedrijfId = b.bedrijfId " +
			"AND c.customMeldingStatus.code IN (:statusCodes)")
	Page<Object[]> findAllExceptionBedrijven(@Param("statusCodes") Collection<String> statusCodes, Pageable pageable);

	// MBR 7-12-2017 BedrijfManaged users added
	@Query("SELECT b AS bedrijf, " +
			"c AS customMelding, " +
			"b2 AS bedrijfDoor " +
			"FROM Bedrijf b, CustomMelding c, Bedrijf b2, Gebruiker g LEFT OUTER JOIN g.bedrijvenManagedDoorGebruikerId bm " +
			"WHERE g = c.gebruiker AND ((g.bedrijf.bedrijfId = b2.bedrijfId AND (bm is null OR bm.bedrijf.bedrijfId <> b2.bedrijfId)) OR (g.bedrijf.bedrijfId <> b2.bedrijfId AND bm.bedrijf.bedrijfId = b2.bedrijfId)) " +
			"AND c.bedrijf.bedrijfId = b.bedrijfId " +
			"AND b.kvKnummer LIKE :kvKnummer " +
			"AND c.customMeldingStatus.code IN (:statusCodes)")
	Page<Object[]> findAllExceptionBedrijven_SearchKvkNummer(@Param("kvKnummer") String kvkNummer, @Param("statusCodes") Collection<String> statusCodes, Pageable pageable);

	// MBR 7-12-2017 BedrijfManaged users added
	@Query("SELECT b AS bedrijf, " +
			"c AS customMelding, " +
			"b2 AS bedrijfDoor " +
			"FROM Bedrijf b, CustomMelding c, Bedrijf b2, Gebruiker g LEFT OUTER JOIN g.bedrijvenManagedDoorGebruikerId bm " +
			"WHERE g = c.gebruiker AND ((g.bedrijf.bedrijfId = b2.bedrijfId AND (bm is null OR bm.bedrijf.bedrijfId <> b2.bedrijfId)) OR (g.bedrijf.bedrijfId <> b2.bedrijfId AND bm.bedrijf.bedrijfId = b2.bedrijfId)) " +
			"AND c.bedrijf.bedrijfId = b.bedrijfId " +
			"AND b.bedrijfsNaam LIKE :bedrijfsNaam " +
			"AND c.customMeldingStatus.code IN (:statusCodes)")
	Page<Object[]> findAllExceptionBedrijven_SearchName(@Param("bedrijfsNaam") String bedrijfsNaam, @Param("statusCodes") Collection<String> statusCodes, Pageable pageable);

	// MBR 7-12-2017 BedrijfManaged users added
	@Query("SELECT b AS bedrijf, " +
			"c AS customMelding, " +
			"b2 AS bedrijfDoor " +
			"FROM Bedrijf b, CustomMelding c, Bedrijf b2, Gebruiker g LEFT OUTER JOIN g.bedrijvenManagedDoorGebruikerId bm " +
			"WHERE g = c.gebruiker AND ((g.bedrijf.bedrijfId = b2.bedrijfId AND (bm is null OR bm.bedrijf.bedrijfId <> b2.bedrijfId)) OR (g.bedrijf.bedrijfId <> b2.bedrijfId AND bm.bedrijf.bedrijfId = b2.bedrijfId)) " +
			"AND c.bedrijf.bedrijfId = b.bedrijfId " +
			"AND (b.kvKnummer LIKE :nummer " +
			"OR b.sbdrNummer like :nummer) " +
			"AND c.customMeldingStatus.code IN (:statusCodes)")
	Page<Object[]> findAllExceptionBedrijven_SearchNummer(@Param("nummer") String nummer, @Param("statusCodes") Collection<String> statusCodes, Pageable pageable);

	@Query("SELECT b AS bedrijf, " +
			"c AS customMelding, " +
			"b2 AS bedrijfDoor " +
			"FROM Bedrijf b, CustomMelding c, Bedrijf b2 " +
			"WHERE c.gebruiker.bedrijf.bedrijfId = b2.bedrijfId " +
			"AND c.bedrijf.bedrijfId = b.bedrijfId " +
			"AND b.sbdrNummer like :nummer " +
			"AND c.customMeldingStatus.code IN (:statusCodes)")
	Page<Object[]> findAllExceptionBedrijven_SearchSbdrNummer(@Param("nummer") String nummer, @Param("statusCodes") Collection<String> statusCodes, Pageable pageable);

	@Query("SELECT b, " +
			"k " +
			"FROM Bedrijf b, Klant k " +
			"WHERE k.actief = :actief " +
			"AND k.bedrijf.bedrijfId = b.bedrijfId " +
			"AND k.klantStatus.code IN (:statusCodes)" + 
			"ORDER BY k.datumAangemaakt DESC")
	Page<Object[]> findAllKlantBedrijvenOnActiveKlantStatus(@Param("actief") boolean actief, @Param("statusCodes") Collection<String> statusCodes, Pageable pageable);

	@Query("SELECT b, " +
			"k " +
			"FROM Bedrijf b, Klant k " +
			"WHERE k.actief = :actief " +
			"AND k.bedrijf.bedrijfId = b.bedrijfId " +
			"AND b.kvKnummer LIKE :kvKnummer " +
			"AND k.klantStatus.code IN (:statusCodes)" + 
			"ORDER BY k.datumAangemaakt DESC")
	Page<Object[]> findAllKlantBedrijvenOnActiveKlantStatus_SearchKvkNummer(@Param("actief") boolean actief, @Param("kvKnummer") String kvkNummer, @Param("statusCodes") Collection<String> statusCodes, Pageable pageable);

	@Query("SELECT b, " +
			"k " +
			"FROM Bedrijf b, Klant k " +
			"WHERE k.actief = :actief " +
			"AND k.bedrijf.bedrijfId = b.bedrijfId " +
			"AND b.bedrijfsNaam LIKE :bedrijfsNaam " +
			"AND k.klantStatus.code IN (:statusCodes)" + 
			"ORDER BY k.datumAangemaakt DESC")
	Page<Object[]> findAllKlantBedrijvenOnActiveKlantStatus_SearchName(@Param("actief") boolean actief, @Param("bedrijfsNaam") String bedrijfsNaam, @Param("statusCodes") Collection<String> statusCodes, Pageable pageable);

	@Query("SELECT b, " +
			"k " +
			"FROM Bedrijf b, Klant k " +
			"WHERE k.actief = :actief " +
			"AND k.bedrijf.bedrijfId = b.bedrijfId " +
			"AND (b.kvKnummer LIKE :nummer " +
			"OR b.sbdrNummer like :nummer) " +
			"AND k.klantStatus.code IN (:statusCodes)" + 
			"ORDER BY k.datumAangemaakt DESC")
	Page<Object[]> findAllKlantBedrijvenOnActiveKlantStatus_SearchNummer(@Param("actief") boolean actief, @Param("nummer") String nummer, @Param("statusCodes") Collection<String> statusCodes, Pageable pageable);

	@Query("SELECT b, " +
			"k " +
			"FROM Bedrijf b, Klant k " +
			"WHERE k.actief = :actief " +
			"AND k.bedrijf.bedrijfId = b.bedrijfId " +
			"AND b.sbdrNummer like :nummer " +
			"AND k.klantStatus.code IN (:statusCodes)" + 
			"ORDER BY k.datumAangemaakt DESC")
	Page<Object[]> findAllKlantBedrijvenOnActiveKlantStatus_SearchSbdrNummer(@Param("actief") boolean actief, @Param("nummer") String nummer, @Param("statusCodes") Collection<String> statusCodes, Pageable pageable);

	@Query("SELECT m " +
			"FROM Melding m, Bedrijf b " +
			"WHERE m.bedrijfByMeldingOverBedrijfId.kvKnummer = b.kvKnummer " +
			"AND b.bedrijfId = :bedrijfId " +
			"AND m.meldingStatus.code IN (:statusCodes)")
	List<Melding> findAllMeldingenOfBedrijf(@Param("bedrijfId") Integer bedrijfId, @Param("statusCodes") Collection<String> statusCodes);

//	@Query("SELECT DISTINCT b1 " +
//			"FROM Bedrijf b1 " +
//			"WHERE b1.bedrijfStatus.code = 'ACT' AND NOT EXISTS " +
//			"(SELECT 1 " +
//			"FROM Gebruiker g2 " +
//			"WHERE g2.datumLaatsteLogout >= :datum " +
//			"AND g2.actief = 1 AND g2.bedrijf.bedrijfId = b1.bedrijfId) " +
//			"AND b1.bedrijfStatus.code = 'ACT' AND b1.kvKnummer != '0000000'")
	// MBR 7-12-2017 BedrijfManaged users added
	@Query("FROM Bedrijf b " +
			"WHERE (" +
				"SELECT MAX(g.datumLaatsteLogout) " +
				"FROM Gebruiker g LEFT OUTER JOIN g.bedrijvenManagedDoorGebruikerId bm WITH bm.actief = TRUE " +
				"WHERE ((g.bedrijf.bedrijfId = b.bedrijfId AND (bm is null OR bm.bedrijf.bedrijfId <> b.bedrijfId)) OR (g.bedrijf.bedrijfId <> b.bedrijfId AND bm.bedrijf.bedrijfId = b.bedrijfId)) " +
			") <= :datum " +
			"AND b.bedrijfStatus = 'ACT' " +
			"AND b.kvKnummer != '00000000'")
	List<Bedrijf> findAllNonRecentBedrijven(@Param("datum") Date datum);

	@Query("SELECT CASE WHEN (HOUR(NOW()) < h.hourNr) THEN (h.hourNr -24) ELSE h.hourNr END AS HourOrder, " +
			"h.hourNr AS hourNr, " +
			"CONCAT(h.hourNr, ':00-', h.hourNr+1, ':00') AS HourDesc " +
			", (SELECT COUNT(d.documentId) " +
			"FROM Document d " +
			"WHERE (d.documentType.code = 'REP' OR d.documentType = 'MOC') " +
			"AND HOUR(d.datumAangemaakt) = h.hourNr " +
			"AND (d.datumAangemaakt BETWEEN :oneDayFromNow " +
			"AND :now " +
			"OR d.datumAangemaakt IS NULL)) AS aantal " +
			"FROM Hours h " +
			"ORDER BY 1")
	List<Object[]> findAllRapportenLast24h(@Param("oneDayFromNow") Date oneDayFromNow, @Param("now") Date now);

	@Query("SELECT b " +
			"FROM Bedrijf b " +
			"JOIN b.meldingsForMeldingOverBedrijfId m " +
			"WHERE m.bedrijfByMeldingDoorBedrijfId.bedrijfId = :bedrijfId " +
			"AND m.meldingStatus.code IN ('ACT') " + // 'INI', 'INB',
			"AND m.bedrijfByMeldingOverBedrijfId.bedrijfId IN (:bedrijvenIds)")
	List<Bedrijf> findBedrijvenMetMeldingByBedrijf_SearchListOfBedrijven(@Param("bedrijfId") Integer bedrijfId, @Param("bedrijvenIds") Collection<Integer> bedrijvenIds);

	@Query("SELECT b " +
			"FROM Bedrijf b " +
			"JOIN b.monitoringsForMonitoringVanBedrijfId m " +
			"WHERE m.bedrijfByMonitoringDoorBedrijfId.bedrijfId = :bedrijfId " +
			"AND m.monitoringStatus.code = 'ACT' " +
			"AND m.bedrijfByMonitoringVanBedrijfId.bedrijfId IN (:bedrijvenIds)")
	List<Bedrijf> findBedrijvenMetMonitoringOfBedrijf_SearchListOfBedrijven(@Param("bedrijfId") Integer bedrijfId, @Param("bedrijvenIds") Collection<Integer> bedrijvenIds);

	@Query("SELECT b " +
			"FROM Monitoring m " +
			"JOIN m.bedrijfByMonitoringDoorBedrijfId b " +
			"WHERE m.bedrijfByMonitoringVanBedrijfId.bedrijfId = :bedrijfId " +
			"AND m.monitoringStatus.code = 'ACT'")
	List<Bedrijf> findBedrijvenMetMonitoringOpBedrijfByBedrijfId(@Param("bedrijfId") Integer bedrijfId);

	@Query("SELECT d.bedrijfByRapportBedrijfId " +
			"FROM Document d " +
			"WHERE d.bedrijfByBedrijfId.bedrijfId = :bedrijfId " +
			"AND d.documentType.code = 'REP' " +
			"AND d.bedrijfByRapportBedrijfId.bedrijfId IN (:bedrijvenIds)" +
			"AND DATE(d.datumAangemaakt) = DATE(:today)")
	List<Bedrijf> findBedrijvenMetRapportCreatedToday_SearchListOfBedrijven(@Param("bedrijfId") Integer bedrijfId, @Param("today") Date today, @Param("bedrijvenIds") Collection<Integer> bedrijvenIds);

	@Query("SELECT b " +
			"FROM Bedrijf b " +
			"WHERE b.bedrijfId IN (:bedrijvenIds)")
	List<Bedrijf> findBestaandeBedrijven_SearchListOfBedrijven(@Param("bedrijvenIds") Collection<Integer> bedrijvenIds);

	/**
	 * Returns a Page containing bedrijven with the requested bedrijfsNaam.<br/>
	 *
	 * @param bedrijfsNaam a String containing the requested value for bedrijfsNaam
	 * @param pageable     a Pageable, containing the parameters for creating the requested Page
	 * @return a Page with Bedrijven, or NULL when the Bedrijven could not be retrieved..
	 */
	@Query("SELECT b FROM Bedrijf b " + "WHERE b.bedrijfsNaam = :bedrijfsNaam")
	Page<Bedrijf> findByBedrijfsNaam(@Param("bedrijfsNaam") String bedrijfsNaam, Pageable pageable);

	/**
	 * Returns a Page containing bedrijven where the bedrijfsNaam contains the requested value.<br/>
	 *
	 * @param bedrijfsNaam a String containing the requested value for bedrijfsNaam
	 * @param pageable     a Pageable, containing the parameters for creating the requested Page
	 * @return a Page with Bedrijven, or NULL when the Bedrijven could not be retrieved..
	 */
	@Query("SELECT b FROM Bedrijf b " + "WHERE bedrijfsNaam like :bedrijfsNaam")
	Page<Bedrijf> findByBedrijfsNaamLike(@Param("bedrijfsNaam") String bedrijfsNaam, Pageable pageable);

	
	// find klant bedrijf via gebruiker bedrijf both gebruiker + klant need to be actief
	@Query("SELECT k.bedrijf " +
			"FROM Gebruiker g, Klant k " +
			"WHERE k.bedrijf = g.bedrijf " +
			"AND k.actief = TRUE " +
			"AND g.actief = TRUE " +
			"AND g.gebruikerId = :gebruikerId")
	Bedrijf findByKlantGebruikersId(@Param("gebruikerId") Integer gebruikerId);
	
	// find klant bedrijf via gebruiker bedrijf both gebruiker + klant need to be actief
	// MBR 7-12-2017 BedrijfManaged users added
//	@Query("SELECT k.bedrijf " +
//			"FROM Gebruiker g LEFT OUTER JOIN g.bedrijvenManagedDoorGebruikerId b, Klant k " +
//			"WHERE ((k.bedrijf = g.bedrijf AND g.actief = TRUE) OR (b.bedrijf = k.bedrijf AND b.actief = TRUE))" +
//			"AND k.actief = TRUE " +
//			"AND g.gebruikerId = :gebruikerId")
//	Bedrijf findByKlantGebruikersId(@Param("gebruikerId") Integer gebruikerId);

	@Query("SELECT b FROM Bedrijf b " +
			"WHERE b.kvKnummer = :kvKNummer " +
			"AND ((:subDossier IS NULL " +
			"AND b.subDossier IS NULL) OR b.subDossier = :subDossier)")
	List<Bedrijf> findByKvKNummer(@Param("kvKNummer") String kvKNummer, @Param("subDossier") String subDossier);

	@Query("SELECT b FROM Bedrijf b " + "WHERE b.kvKnummer IN (:kvKNummers)")
	List<Bedrijf> findByKvKNummers(@Param("kvKNummers") Collection kvKNummers);

	@Query("FROM Bedrijf b " + "WHERE b.sbdrNummer = :sbdrNummer")
	List<Bedrijf> findBySbdrNummer(@Param("sbdrNummer") String sbdrNummer);

	@Query("FROM Factuur f " +
			"WHERE f.bedrijf.bedrijfId = :bId " +
			"AND f.datumAangemaakt IS NOT NULL " +
			"ORDER BY f.datumFactuur DESC")
	Page<Factuur> findFacturenOfBedrijf(@Param("bId") Integer bId, Pageable p);

	@Query("FROM Klant k " +
			"WHERE k.actief = TRUE AND k.klantStatus.code IN ('NOK', 'REG', 'PRO', 'ACT')" +
			"AND k.bedrijf.bedrijfId = :bId")
	List<Klant> findAnyKlantRecordOfBedrijf(@Param("bId") Integer bedrijfId);

	// MBR: 14-12-2015 TESTEN!!!
	@Query("FROM Klant k " +
			"WHERE k.actief = TRUE AND k.klantStatus.code IN ('PRO', 'ACT')" +
			"AND k.bedrijf.bedrijfId = :bedrijfId")
	List<Klant> findKlantOfBedrijf(@Param("bedrijfId") Integer bedrijfId);

	@Query("FROM Klant k " +
			"WHERE k.actief = TRUE " +
			"AND k.bedrijf.bedrijfId IN (:bedrijfIds)")
	List<Klant> findKlantOfBedrijven(@Param("bedrijfIds") Collection<Integer> bedrijfIds);

	@Query("SELECT b " +
			"FROM Bedrijf b " +
			"WHERE b IN (" +
			"SELECT mon.bedrijfByMonitoringVanBedrijfId " +
			"FROM Monitoring mon " +
			"WHERE mon.monitoringStatus.code = 'DEL' " +
			"AND mon.bedrijfByMonitoringDoorBedrijfId.bedrijfId = :bedrijfId) " +
			"OR b IN (" +
			"SELECT m.bedrijfByMeldingOverBedrijfId " +
			"FROM Melding m " +
			"WHERE m.meldingStatus.code IN ('DEL', 'AFW') " +
			"AND m.bedrijfByMeldingDoorBedrijfId.bedrijfId = :bedrijfId) " +
			"ORDER BY b.bedrijfsNaam")
	Page<Bedrijf> findRemovedBedrijvenOfBedrijf(@Param("bedrijfId") Integer bedrijfId, Pageable pageable);

	@Query("SELECT b " +
			"FROM Bedrijf b " +
			"WHERE (b IN (" +
			"SELECT mon.bedrijfByMonitoringVanBedrijfId " +
			"FROM Monitoring mon " +
			"WHERE mon.monitoringStatus.code = 'DEL' " +
			"AND mon.bedrijfByMonitoringDoorBedrijfId.bedrijfId = :bedrijfId) " +
			"OR b IN (" +
			"SELECT m.bedrijfByMeldingOverBedrijfId " +
			"FROM Melding m " +
			"WHERE m.meldingStatus.code IN ('DEL', 'AFW') " +
			"AND m.bedrijfByMeldingDoorBedrijfId.bedrijfId = :bedrijfId)) " +
			"AND b.kvKnummer LIKE :kvKnummer " +
			"ORDER BY b.bedrijfsNaam")
	Page<Bedrijf> findRemovedBedrijvenOfBedrijf_SearchKvkNummer(@Param("bedrijfId") Integer bedrijfId, @Param("kvKnummer") String kvkNummer, Pageable pageable);

	@Query("SELECT b " +
			"FROM Bedrijf b " +
			"WHERE b IN (" +
			"SELECT mon.bedrijfByMonitoringVanBedrijfId " +
			"FROM Monitoring mon " +
			"WHERE mon.monitoringStatus.code = 'DEL' " +
			"AND mon.bedrijfByMonitoringDoorBedrijfId.bedrijfId = :bedrijfId " +
			"AND mon.referentieNummerIntern LIKE :nummer) " +
			"AND b.kvKnummer LIKE :nummer " +
			"ORDER BY b.bedrijfsNaam")
	Page<Bedrijf> findRemovedBedrijvenOfBedrijf_SearchMoNummer(@Param("bedrijfId") Integer bedrijfId, @Param("nummer") String nummer, Pageable pageable);

	@Query("SELECT b " +
			"FROM Bedrijf b " +
			"WHERE ( b IN (" +
			"SELECT mon.bedrijfByMonitoringVanBedrijfId " +
			"FROM Monitoring mon " +
			"WHERE mon.monitoringStatus.code = 'DEL' " +
			"AND mon.bedrijfByMonitoringDoorBedrijfId.bedrijfId = :bedrijfId) " +
			"OR b IN (" +
			"SELECT m.bedrijfByMeldingOverBedrijfId " +
			"FROM Melding m " +
			"WHERE m.meldingStatus.code IN ('DEL', 'AFW') " +
			"AND m.bedrijfByMeldingDoorBedrijfId.bedrijfId = :bedrijfId) ) " +
			"AND b.bedrijfsNaam LIKE :bedrijfsNaam " +
			"ORDER BY b.bedrijfsNaam")
	Page<Bedrijf> findRemovedBedrijvenOfBedrijf_SearchName(@Param("bedrijfId") Integer bedrijfId, @Param("bedrijfsNaam") String bedrijfsNaam, Pageable pageable);

	@Query("SELECT b " +
			"FROM Bedrijf b " +
			"WHERE ( b IN (" +
			"SELECT mon.bedrijfByMonitoringVanBedrijfId " +
			"FROM Monitoring mon " +
			"WHERE mon.monitoringStatus.code = 'DEL' " +
			"AND mon.bedrijfByMonitoringDoorBedrijfId.bedrijfId = :bedrijfId) " +
			"OR b IN (" +
			"SELECT m.bedrijfByMeldingOverBedrijfId " +
			"FROM Melding m " +
			"WHERE m.meldingStatus.code IN ('DEL', 'AFW') " +
			"AND m.bedrijfByMeldingDoorBedrijfId.bedrijfId = :bedrijfId) ) " +
			"AND b.sbdrNummer LIKE :nummer " +
			"ORDER BY b.bedrijfsNaam")
	Page<Bedrijf> findRemovedBedrijvenOfBedrijf_SearchSbdrNummer(@Param("bedrijfId") Integer bedrijfId, @Param("nummer") String nummer, Pageable pageable);

	@Query("SELECT b " +
			"FROM Bedrijf b " +
			"WHERE b IN (" +
			"SELECT m.bedrijfByMeldingOverBedrijfId " +
			"FROM Melding m " +
			"WHERE m.meldingStatus.code IN ('DEL', 'AFW') " +
			"AND m.bedrijfByMeldingDoorBedrijfId.bedrijfId = :bedrijfId " +
			"AND m.referentieNummerIntern LIKE :nummer) " +
			"AND b.kvKnummer LIKE :nummer " +
			"ORDER BY b.bedrijfsNaam")
	Page<Bedrijf> findRemovedBedrijvenOfBedrijf_SearchVeNummer(@Param("bedrijfId") Integer bedrijfId, @Param("nummer") String nummer, Pageable pageable);

	@Query("SELECT rb, b " +
			"FROM RemovedBedrijf rb, Bedrijf b " +
			"WHERE rb.doorBedrijfId = :bedrijfId " +
			"AND b.bedrijfId = rb.bedrijfId " +
			"AND ((:view = 'monitoring' " +
			"AND rb.monitoringId is not null) " +
			"OR (:view = 'melding' " +
			"AND rb.meldingId is not null) " +
			"OR (:view = 'all'))")
	Page<Object[]> findRemovedBedrijvenViewOfBedrijf(@Param("bedrijfId") Integer bedrijfId, @Param("view") String view, Pageable pageable);

	@Query("SELECT rb, b " +
			"FROM RemovedBedrijf rb, Bedrijf b " +
			"WHERE rb.doorBedrijfId = :bedrijfId " +
			"AND rb.kvkNummer LIKE :kvkNummer " +
			"AND b.bedrijfId = rb.bedrijfId " +
			"AND ((:view = 'monitoring' " +
			"AND rb.monitoringId is not null) " +
			"OR (:view = 'melding' " +
			"AND rb.meldingId is not null) " +
			"OR (:view = 'all'))")
	Page<Object[]> findRemovedBedrijvenViewOfBedrijf_SearchKvkNummer(@Param("bedrijfId") Integer bedrijfId, @Param("view") String view, @Param("kvkNummer") String kvkNummer, Pageable pageable);

	@Query("SELECT rb, b " +
			"FROM RemovedBedrijf rb, Bedrijf b " +
			"WHERE rb.doorBedrijfId = :bedrijfId " +
			"AND rb.bedrijfsNaam LIKE :bedrijfsNaam " +
			"AND b.bedrijfId = rb.bedrijfId " +
			"AND ((:view = 'monitoring' " +
			"AND rb.monitoringId is not null) " +
			"OR (:view = 'melding' " +
			"AND rb.meldingId is not null) " +
			"OR (:view = 'all'))")
	Page<Object[]> findRemovedBedrijvenViewOfBedrijf_SearchName(@Param("bedrijfId") Integer bedrijfId, @Param("view") String view, @Param("bedrijfsNaam") String bedrijfsNaam, Pageable pageable);

	@Query("SELECT rb, b " +
			"FROM RemovedBedrijf rb, Bedrijf b " +
			"WHERE rb.doorBedrijfId = :bedrijfId " +
			"AND (rb.kvkNummer LIKE :nummer " +
			"OR rb.referentieNummerIntern LIKE :nummer) " +
			"AND b.bedrijfId = rb.bedrijfId " +
			"AND ((:view = 'monitoring' " +
			"AND rb.monitoringId is not null) " +
			"OR (:view = 'melding' " +
			"AND rb.meldingId is not null) " +
			"OR (:view = 'all'))")
	Page<Object[]> findRemovedBedrijvenViewOfBedrijf_SearchNummer(@Param("bedrijfId") Integer bedrijfId, @Param("view") String view, @Param("nummer") String nummer, Pageable pageable);

	@Query("SELECT rb, b " +
			"FROM RemovedBedrijf rb, Bedrijf b " +
			"WHERE rb.doorBedrijfId = :bedrijfId " +
			"AND rb.referentieNummerIntern LIKE :nummer " +
			"AND b.bedrijfId = rb.bedrijfId " +
			"AND ((:view = 'monitoring' " +
			"AND rb.monitoringId is not null) " +
			"OR (:view = 'melding' " +
			"AND rb.meldingId is not null) " +
			"OR (:view = 'all'))")
	Page<Object[]> findRemovedBedrijvenViewOfBedrijf_SearchSbdrNummer(@Param("bedrijfId") Integer bedrijfId, @Param("view") String view, @Param("nummer") String nummer, Pageable pageable);

	@Query("FROM Bedrijf b WHERE b.kvKnummer='0000000'")
	Bedrijf findSbdr();

	@Query("SELECT s.omschrijving " +
			"FROM Sbi s " +
			"WHERE s.code = (" +
			"SELECT b.CIKvKDossier.sbihoofdAct " +
			"FROM Bedrijf b " +
			"WHERE b.bedrijfId = :bedrijfId)")
	String findSbiOmschrijvingOfBedrijf(@Param("bedrijfId") Integer bedrijfId);

	@Query("SELECT s, b, bvan " +
			"FROM Bedrijf b, SearchResult s " +
			"LEFT OUTER JOIN s.vanBedrijf bvan " +
			"WHERE (s.bedrijfsNaam LIKE :searchstring " +
			"OR s.kvkNummer LIKE :searchstring " +
			"OR s.referentieNummerIntern LIKE :searchstring) " +
			"AND s.bedrijfId = b.bedrijfId " +
			"ORDER BY s.datumStart DESC")
	Page<Object[]> findSearchResultsVermelder(@Param("searchstring") String searchstring, Pageable pageable);

	@Query("SELECT s, bdoor, b " +
			"FROM Bedrijf b, SearchResult s " +
			"LEFT OUTER JOIN s.doorBedrijf bdoor " +
			"WHERE (b.bedrijfsNaam LIKE :searchstring " +
			"OR b.kvKnummer LIKE :searchstring) " +
			"AND s.vanBedrijf.bedrijfId = b.bedrijfId " +
			"ORDER BY s.datumStart DESC")
	Page<Object[]> findSearchResultsVermeldde(@Param("searchstring") String searchstring, Pageable pageable);
	
	@Query("SELECT b " +
			"FROM Melding m " +
			"JOIN m.bedrijfByMeldingOverBedrijfId b " +
			"WHERE m.bedrijfByMeldingDoorBedrijfId.bedrijfId = :bedrijfId " +
			"AND m.meldingStatus.code = 'INB' " +
			"AND m.datumIngediend < :overdueDate")
	List<Bedrijf> findVermeldeBedrijvenOfBedrijf(@Param("bedrijfId") Integer bedrijfId, @Param("overdueDate")Date overdueDate);

	@Query("SELECT b " +
			"FROM Melding m JOIN m.bedrijfByMeldingOverBedrijfId b " +
			"WHERE m.bedrijfByMeldingDoorBedrijfId.bedrijfId = :bedrijfId")
	List<Bedrijf> findnewNotifiedCompaniesOfCompany(@Param("bedrijfId") Integer bedrijfId);
}