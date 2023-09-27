package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.Gebruiker;
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

public interface GebruikerRepositoryCustom {

	// users logged in at the moment...from database. Better use WebTokenService
	@Query("SELECT count(g.gebruikerId) as aantal FROM Gebruiker g WHERE g.datumLaatsteAanmelding > g.datumLaatsteLogout OR (g.datumLaatsteAanmelding is not null and g.datumLaatsteLogout is null)")
	List<Integer> findAantalGebruikersIngelogd();

	/**
	 * Returns a Page containing gebruikers for a specific company.<br/>
	 *
	 * @param gebruikersNaam a String containing the requested value for gebruikersNaam
	 * @return a List with Gebruikers, or NULL when the Gebruikers could not be retrieved..
	 */
	// MBR 7-12-2017 BedrijfManaged users added
	//@Query("select g from Gebruiker g where g.bedrijf.bedrijfId = :bedrijfid and g.actief = TRUE")
	@Query("select g from Gebruiker g LEFT OUTER JOIN g.bedrijvenManagedDoorGebruikerId b WITH b.actief = TRUE AND b.bedrijf.bedrijfId = :bedrijfId where (g.bedrijf.bedrijfId = :bedrijfId OR b.bedrijf.bedrijfId = :bedrijfId) and g.actief = TRUE")
	Page<Gebruiker> findActiveGebruikersOfBedrijf(@Param("bedrijfId") Integer bedrijfId, Pageable pageable);

	// MBR 7-12-2017 BedrijfManaged users added
	@Query("FROM Gebruiker g LEFT OUTER JOIN g.bedrijvenManagedDoorGebruikerId b WITH b.actief = TRUE AND b.bedrijf.bedrijfId = :bedrijfId WHERE (g.bedrijf.bedrijfId = :bedrijfId OR b.bedrijf.bedrijfId = :bedrijfId) and g.actief = TRUE")
	List<Gebruiker> findAllGebruikersOfBedrijfByBedrijfId(@Param("bedrijfId") Integer bedrijfId);

	// MBR 7-12-2017 BedrijfManaged users added
	@Query("SELECT DISTINCT g FROM Gebruiker g LEFT OUTER JOIN g.bedrijvenManagedDoorGebruikerId b WITH b.actief = TRUE AND b.bedrijf.bedrijfId = :bedrijfId, AlertView aV " +
			"WHERE (g.bedrijf.bedrijfId = :bedrijfId OR b.bedrijf.bedrijfId = :bedrijfId) AND ((aV.type IN ('VVM', 'VER', 'SUP') AND aV.bestemdVoorBedrijfId = :bedrijfId) OR (aV.type = 'MON' AND aV.monitoringDoorBedrijfId = :bedrijfId)) " +
			"AND g.actief = TRUE " +
			"AND aV.gebruikerId IS NOT NULL")
	List<Gebruiker> findAllGebruikersWithAlertsByBedrijfId(@Param("bedrijfId")Integer bedrijfId);

	@Query("SELECT CASE WHEN (HOUR(NOW()) < h.hourNr) THEN (HOUR(NOW()) -24 + (HOUR(NOW())- h.hourNr)) ELSE h.hourNr END AS HourOrder, " +
			"h.hourNr AS hourNr, " +
			"CONCAT(h.hourNr, ':00-', h.hourNr+1, ':00') AS HourDesc, " +
			"COUNT(d.datumAangemaakt) AS aantal " +
			"FROM Document d, " +
			"Hours h " +
			"WHERE d.documentType = 'REP' " +
			"AND HOUR(d.datumAangemaakt) = h.hourNr " +
			"AND d.datumAangemaakt BETWEEN :oneDayFromNow AND :now " +
			"OR d.datumAangemaakt IS NULL " +
			"GROUP BY h.hourNr " +
			"ORDER BY 1")
	List<Object[]> findAllRequestedReportsLast24h(@Param("oneDayFromNow") Date oneDayFromNow, @Param("now") Date now);

	/**
	 * Returns all Gebruikers with the requested activity indication
	 *
	 * @param actief a Boolean containing the activity indication of the Gebruiker
	 * @return a List of Gebruiker objects with the requested activity indication, or null if not available
	 */
	@Query("FROM Gebruiker WHERE actief = :actief ORDER BY gebruikersNaam ASC")
	List<Gebruiker> findByActief(@Param("actief") Boolean actief);

	/**
	 * Returns all Gebruikers with the requested activity indication
	 *
	 * @param actief a Boolean containing the activity indication of the Gebruiker
	 * @return a List of Gebruiker objects with the requested activity indication, or null if not available
	 */
	@Query("SELECT g FROM Gebruiker g, Wachtwoord w WHERE g.actief = 1 and w.wachtwoordStatus = 'INI' AND w.activatieCode = :activatieCode AND g.wachtwoord = w")
	List<Gebruiker> findByActivatieCode(@Param("activatieCode") String activatieCode);

	/**
	 * Returns all Gebruikers with the requested activity indication
	 *
	 * @param actief a Boolean containing the activity indication of the Gebruiker
	 * @return a List of Gebruiker objects with the requested activity indication, or null if not available
	 */
	@Query("SELECT m.gebruiker FROM BedrijfManaged m WHERE m.actief = 1 and m.gebruikerStatus = 'REG' AND m.activatieCode = :activatieCode")
	List<Gebruiker> findByActivatieCodeBedrijfManaged(@Param("activatieCode") String activatieCode);
	
	/**
	 * Returns a List containing gebruikers with the requested gebruikersNaam.<br/>
	 *
	 * @param gebruikersNaam a String containing the requested value for gebruikersNaam
	 * @return a List with Gebruikers, or NULL when the Gebruikers could not be retrieved..
	 */
	@Query("select g from Gebruiker g where g.gebruikersNaam = :gebruikersNaam and g.actief = 1")
	List<Gebruiker> findByGebruikersNaam(@Param("gebruikersNaam") String gebruikersNaam);

	/**
	 * Returns a List containing gebruikers with the requested gebruikersNaam + bedrijf.<br/>
	 *
	 * @param gebruikersNaam a String containing the requested value for gebruikersNaam
	 * @param bedrijfId a ID containing the requested value for bedrijfId of gebruiker
	 * @return a List with Gebruikers, or NULL when the Gebruikers could not be retrieved..
	 */
	// MBR 7-12-2017 BedrijfManaged users added
	@Query("select g from Gebruiker g  LEFT OUTER JOIN g.bedrijvenManagedDoorGebruikerId b with b.actief = TRUE AND b.bedrijf.bedrijfId = :bedrijfId where (g.bedrijf.bedrijfId = :bedrijfId OR b.bedrijf.bedrijfId = :bedrijfId) and g.actief = TRUE AND g.gebruikersNaam = :gebruikersNaam")
	List<Gebruiker> findByGebruikersNaamOfBedrijf(@Param("gebruikersNaam") String gebruikersNaam, @Param("bedrijfId") Integer bedrijfId);
	
	
	/**
	 * Returns a Page containing gebruikers with the requested gebruikersNaam.<br/>
	 *
	 * @param gebruikersNaam a String containing the requested value for gebruikersNaam
	 * @param pageable       a Pageable, containing the parameters for creating the requested Page
	 * @return a Page with Gebruikers, or NULL when the Gebruikers could not be retrieved..
	 */
	@Query("SELECT g FROM Gebruiker g WHERE g.gebruikersNaam = :gebruikersNaam and g.actief = 1")
	Page<Gebruiker> findByGebruikersNaam(@Param("gebruikersNaam") String gebruikersNaam, Pageable pageable);

	/**
	 * Returns a Page containing gebruikers where the gebruikersNaam contains the requested value.<br/>
	 *
	 * @param gebruikersNaam a String containing the requested value for gebruikersNaam
	 * @param pageable       a Pageable, containing the parameters for creating the requested Page
	 * @return a Page with Bedrijven, or NULL when the Bedrijven could not be retrieved..
	 */
	@Query("SELECT g FROM Gebruiker g WHERE g.gebruikersNaam like :gebruikersNaam and g.actief = 1")
	Page<Gebruiker> findByGebruikersNaamLike(@Param("gebruikersNaam") String gebruikersNaam, Pageable pageable);

	/**
	 * Returns count of Gebruikers with the requested activity indication
	 *
	 * @return a List of count of Gebruiker objects
	 */
	@Query("SELECT count(g) FROM Gebruiker g, Wachtwoord w WHERE g.actief = 1 and w.wachtwoordStatus = 'ACT'AND g.wachtwoord = w")
	List<Long> findCountActivatedUsers();

	/**
	 * Returns count of Gebruikers with the requested activity indication
	 *
	 * @return a List of count of Gebruiker objects
	 */
	@Query("SELECT count(g) FROM Gebruiker g")
	List<Long> findCountAllUsers();

	@Query("SELECT g " +
			"FROM Gebruiker g " +
			"JOIN g.rollen r " +
			"WHERE r.code = 'AUTO'")
	Gebruiker findSysteemGebruiker();

	@Query("SELECT g " +
			"FROM Gebruiker as g " +
			"JOIN g.rollen r " +
			"WHERE g.bedrijf.bedrijfId = :bId " +
			"AND r.code IN ('HOOFD', 'KLANT')")
	List<Gebruiker> findAllHoofdAndKlantGebruikersOfBedrijf(@Param("bId")Integer bId);
	/**
	 * Returns count of Gebruikers with the requested activity indication
	 *
	 * @return a List of count of Gebruiker objects
	 */
	@Query("SELECT count(g) FROM Gebruiker g WHERE g.actief = 0")
	List<Long> findCountDeactivatedUsers();

	/**
	 * Returns count of Gebruikers with the requested activity indication
	 *
	 * @return a List of count of Gebruiker objects
	 */
	@Query("SELECT count(g) FROM Gebruiker g, Wachtwoord w WHERE g.actief = 1 and w.wachtwoordStatus = 'INI'AND g.wachtwoord = w")
	List<Long> findCountNonActivatedUsers();

	/**
	 * Returns all Gebruikers with the requested activity indication
	 *
	 * @param actief a Boolean containing the activity indication of the Gebruiker
	 * @return a List of Gebruiker objects with the requested activity indication, or null if not available
	 */
	@Query("SELECT g FROM Gebruiker g JOIN g.rollen r WHERE r.code = :rolcode")
	List<Gebruiker> findGebruikersByRolCode(@Param("rolcode") String rolcode);

	// MBR 7-12-2017 BedrijfManaged users added
	@Query("SELECT g FROM Gebruiker g LEFT OUTER JOIN g.bedrijvenManagedDoorGebruikerId bm with bm.actief = TRUE AND bm.bedrijf.bedrijfId = :bedrijfid WHERE (g.bedrijf.bedrijfId = :bedrijfid OR bm.bedrijf.bedrijfId = :bedrijfid) and g.actief = TRUE")
	Page<Gebruiker> findGebruikersOfBedrijf(@Param("bedrijfid") Integer bedrijfId, Pageable pageable);

	@Query("SELECT g FROM Gebruiker AS g JOIN g.rollen r WHERE r.code = 'AUTO'")
	Gebruiker findSbdrGebruiker();
}
