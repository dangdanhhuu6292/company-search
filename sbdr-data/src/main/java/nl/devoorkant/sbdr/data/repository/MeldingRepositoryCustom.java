package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.Melding;
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

public interface MeldingRepositoryCustom {

	/**
	 * Returns a Page containing active meldingen for a specific company.<br/>
	 *
	 * @param bedrijfId a String containing the requested value for bedrijfId
	 *
	 * @return a List with Meldingen, or NULL when the Meldingen could not be retrieved..
	 */
	@Query("select m from Melding m where m.bedrijfByMeldingDoorBedrijfId.bedrijfId = :bedrijfid and m.meldingStatus.code = 'ACT'")
	Page<Melding> findActiveMeldingenOfBedrijf(@Param("bedrijfid") Integer bedrijfId, Pageable pageable);

	@Query("select m from Melding m where m.bedrijfByMeldingDoorBedrijfId.bedrijfId = :bedrijfid and m.meldingStatus.code NOT IN ('AFW', 'BLK', 'DEL')")
	List<Melding> findAllMeldingenOfBedrijf(@Param("bedrijfid") Integer bedrijfId);

	@Query("FROM Melding m WHERE m.bedrijfByMeldingDoorBedrijfId.bedrijfId = :bedrijfId AND m.meldingStatus.code not in ('AFW', 'DEL') AND alphanum(m.referentieNummer) = alphanum(:referentie)")
	List<Melding> findByReferentieNummer(@Param("bedrijfId") Integer bedrijfId, @Param("referentie") String referentie);

	@Query("FROM Melding WHERE referentieNummerIntern = :referentie")
	List<Melding> findByReferentieNummerIntern(@Param("referentie") String referentie);

	@Query("FROM Melding m WHERE m.bedrijfByMeldingDoorBedrijfId.bedrijfId = :bedrijfId AND m.meldingId <> :meldingId AND m.meldingStatus.code not in ('AFW', 'DEL') AND alphanum(m.referentieNummer) = alphanum(:referentie)")
	List<Melding> findByReferentieNummerNotMeldingId(@Param("meldingId") Integer meldingId, @Param("bedrijfId") Integer bedrijfId, @Param("referentie") String referentie);

	@Query("SELECT m FROM Melding m" +
			" LEFT OUTER JOIN m.supports s" +
			" JOIN FETCH m.bedrijfByMeldingOverBedrijfId b" +
			" JOIN b.gebruikers g" +
			" WHERE g.gebruikerId=:gebruikerId" +
			" AND m.meldingStatus.code NOT IN('AFW', 'NOK', 'DEL')" + // MBR added DEL
			" AND s.supportId IS NULL")
	List<Melding> findBygebruikerIdOfCompany(@Param("gebruikerId") Integer gebruikerId);

	@Query("FROM Melding m " +
			"WHERE m.bedrijfByMeldingDoorBedrijfId.bedrijfId IN (" +
			"SELECT m2.bedrijfByMeldingDoorBedrijfId.bedrijfId " +
			"FROM Melding m2 " +
			"WHERE m2.meldingId = :meldingId)" +
			"AND m.bedrijfByMeldingOverBedrijfId.bedrijfId IN (" +
			"SELECT m3.bedrijfByMeldingOverBedrijfId.bedrijfId " +
			"FROM Melding m3 " +
			"WHERE m3.meldingId = :meldingId)")
	List<Melding> findMeldingenBetweenCompaniesByMeldingId(@Param("meldingId") Integer meldingId);

	@Query("FROM Melding m " +
			"WHERE m.document.documentId = " +
			"(SELECT m2.document.documentId " +
			"FROM Melding m2 " +
			"WHERE m2.meldingId = :meldingId)")
	List<Melding> findMeldingenOfDocumentByMeldingId(@Param("meldingId") Integer meldingId);

	@Query("FROM Melding  WHERE datumIngediend >= :startDate AND datumIngediend <= :endDate")
	List<Melding> findNewMeldingenBetweenDates(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

	@Query("FROM Melding m " +
			"WHERE m.bedrijfByMeldingDoorBedrijfId.bedrijfId = :vanBedrijfId " +
			"AND m.bedrijfByMeldingOverBedrijfId.bedrijfId = :overBedrijfId " +
			"AND m.briefStatus.code = 'NVW' " +
			"AND m.meldingStatus.code IN ('INI', 'INB')")
	List<Melding> findNewMeldingenOfBedrijfAboutBedrijf(@Param("vanBedrijfId") Integer vanBedrijfId, @Param("overBedrijfId") Integer overBedrijfId);

	@Query("SELECT m " +
			"FROM Melding m, Klant k " +
			"WHERE m.bedrijfByMeldingDoorBedrijfId = k.bedrijf " +
			"AND k.klantStatus.code = 'ACT' " +
			"AND m.meldingStatus.code = 'INB' " +
			"AND m.datumIngediend < :overdueDate")
	List<Melding> findOverdueNewMeldingen(@Param("overdueDate") Date overdueDate);

	@Query("SELECT m " +
			"FROM Melding m, Klant k " +
			"WHERE m.bedrijfByMeldingDoorBedrijfId = k.bedrijf " +
			"AND k.klantStatus.code = 'ACT' " +
			"AND m.meldingStatus.code = 'INB' " +
			"AND m.datumIngediend < :overdueDate " +
			"AND m.bedrijfByMeldingDoorBedrijfId.bedrijfId = :bedrijfIdDoor " +
			"AND m.bedrijfByMeldingOverBedrijfId.bedrijfId = :bedrijfIdOver")
	List<Melding> findOverdueNewMeldingenOfBedrijf(@Param("bedrijfIdDoor") Integer bedrijfIdDoor, @Param("bedrijfIdOver") Integer bedrijfIdOver, @Param("overdueDate") Date overdueDate);

	@Query("select m from Melding m where m.datumIngediend > :fromDate OR m.datumGeaccordeerd > :fromDate OR m.datumVerwijderd > :fromDate OR m.datumLaatsteMutatie > :fromDate")
	List<Melding> findAllFromDate(@Param("fromDate") Date fromDate);

}
