package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.Klant;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Extension for KlantRepository bean.
 * <p/>
 * SBDR - Stichting Bedrijfskrediet Data Registratie
 * <p/>
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author Jan Meekel
 * @version %I%
 */

public interface KlantRepositoryCustom {

	/**
	 * Returns all Klanten with the requested activity indication
	 *
	 * @param actief a Boolean containing the activity indication of the Klant
	 * @return a List of Klant objects with the requested activity indication, or null if not available
	 */
	@Query("FROM Klant " +
			"WHERE actief = :actief " +
			"ORDER BY naam ASC")
	List<Klant> findByActief(@Param("actief") Boolean actief);

	@Query("FROM Klant " +
			"WHERE actief = TRUE " +
			"AND activatieCode = :activatieCode " +
			"AND klantStatus.code in ('NOK', 'REG')")
	List<Klant> findByActivatieCode(@Param("activatieCode") String activatieCode);

	List<Klant> findByGebruikerId(@Param("gebruikerId") Integer gebruikerId);

	// Get active Klant if Klant == active Gebruiker
	@Query("FROM Klant k " +
			"WHERE k.gebruikersNaam = :gebruikersNaam " +
			"AND k.actief = TRUE")
	List<Klant> findByGebruikersNaam(@Param("gebruikersNaam") String gebruikersNaam);

	// MBR 17-12-2015 REG entries may be double existing
	@Query("FROM Klant k " +
			"WHERE k.bedrijf.bedrijfId = :bedrijfId AND k.klantStatus.code IN ('PRO', 'ACT')" +
			"AND k.actief = TRUE")
	List<Klant> findKlantOfBedrijfByBedrijfId(@Param("bedrijfId") Integer bedrijfId);

	@Query("FROM Klant k " +
			"WHERE k.bedrijf.bedrijfId = :bId AND k.klantStatus.code IN (:statusCodes) " +
			"AND k.actief = TRUE")
	List<Klant> findKlantOfBedrijfByBedrijfIdAndStatus(@Param("bId")Integer bId, @Param("statusCodes")Collection<String> statusCodes);

	@Query("SELECT k " +
			"FROM Klant k, " +
			"Bedrijf b, " +
			"Gebruiker g " +
			"WHERE g.gebruikerId = :gebruikerId " +
			"AND g.actief = TRUE " +
			"AND b.bedrijfId = g.bedrijf.bedrijfId " +
			"AND k.actief = TRUE " +
			"AND k.bedrijf = b")
	List<Klant> findKlantOfGebruikerByGebruikerId(@Param("gebruikerId") Integer gebruikerId);
	
	@Query("SELECT distinct k " +
			"FROM Klant k, " +
			"Bedrijf b, " +
			"Gebruiker g LEFT OUTER JOIN g.bedrijvenManagedDoorGebruikerId bm with bm.actief = TRUE " +
			"WHERE g.gebruikerId = :gebruikerId " +
			"AND g.actief = TRUE " +
			// MBR 7-12-2017 Bedrijf now via Gebruiker of BedrijfManaged
			// "AND b = g.bedrijf " +
			"AND b.bedrijfId = :bedrijfId " +
			"AND ((g.bedrijf = b AND (bm is null OR bm.bedrijf <> b)) OR (g.bedrijf <> b AND bm.bedrijf = b)) " + 
			"AND k.actief = TRUE " +
			"AND k.bedrijf = b")
	List<Klant> findKlantOfGebruikerByGebruikerIdBedrijfId(@Param("gebruikerId") Integer gebruikerId, @Param("bedrijfId") Integer bedrijfId);

	// trick with addDays to create date-without-time-part
	@Query("SELECT k, " +
			"b " +
			"FROM Klant k JOIN k.bedrijf b " +
			"WHERE " +
			"((k.actief = TRUE AND k.klantStatus.code in ('ACT')) OR (EXISTS (SELECT 1 FROM b.facturen f WHERE f.datumAangemaakt is null)))" + // 'REG', 'PRO', 
			"AND (k.bedrijf.datumVerwerktExactOnline is null OR (k.bedrijf.datumWijziging is not null AND k.bedrijf.datumVerwerktExactOnline is not null AND addDays(k.bedrijf.datumWijziging,0) >= k.bedrijf.datumVerwerktExactOnline))")
	List<Object[]> findKlantenToProcessExactOnline();

	@Query("FROM Klant " +
			"WHERE actief = TRUE " +
			"AND klantStatus.code IN (:klantStatusCodes) " +
			"AND datumAangemaakt < :overduedate")
	List<Klant> findKlantAccountByOverdueDateAndStatus(@Param("overduedate") Date overduedate, @Param("klantStatusCodes") Collection<String> klantStatusCodes);

	@Query("FROM Klant " +
			"WHERE gebruikerId = :gebruikerId " +
			"AND actief = TRUE " +
			"AND klantStatus.code IN (:klantStatusCodes)")
	Klant findKlantByIdAndStatus(@Param("gebruikerId") Integer gebruikerId, @Param("klantStatusCodes") Collection<String> klantStatusCodes);

	@Query("FROM Klant " +
			"WHERE actief = TRUE " +
			"AND klantStatus.code IN (:klantStatusCodes)")
	List<Klant> findKlantenByStatus(@Param("klantStatusCodes") Collection<String> klantStatusCodes);
	
	/**
	 * This method changes database content with a native SQL query.
	 * Therefor 'clearAutomatically = true' to flush all unsynchronized Entities in Hibernate's second level cache.
	 * Watch out in use of this method especially for already fetched objects in the transaction.
	 * 
	 * @param gebruikerId
	 */
	@Modifying(clearAutomatically = true)
	@Query(value = "INSERT INTO klant(gebruiker_id, KlantStatus, BTWNummer, ActivatieReminderSent) VALUES (:gebruikerId,'ACT','', 0)", nativeQuery = true)	
	void createNewKlantRecord(@Param("gebruikerId") Integer gebruikerId);
	
}
