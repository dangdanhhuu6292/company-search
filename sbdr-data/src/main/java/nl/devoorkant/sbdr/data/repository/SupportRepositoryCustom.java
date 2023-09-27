package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.Support;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * Extension for SupportRepository bean.
 * <p/>
 * SBDR - Stichting Bedrijfskrediet Data Registratie
 * <p/>
 * Copyright:       Copyright (c) 2015. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author Coen Hasselaar
 * @version %I%
 */

public interface SupportRepositoryCustom {

	@Query("FROM Support s WHERE s.gebruikerByGebruikerId.gebruikerId = :gId")
	List<Support> findAllSupportTicketsByGebruikerId(@Param("gId") Integer gId);
	
	// MBR 7-12-2017 BedrijfManaged users added
	@Query("FROM Support s, Gebruiker g LEFT OUTER JOIN g.bedrijvenManagedDoorGebruikerId bm WITH bm.bedrijf.bedrijfId = :bId " +
			"WHERE (s.gebruikerByGebruikerId = g AND g.bedrijf.bedrijfId = :bId " +
			"OR s.referentieNummer IN (" +
				"SELECT subS.referentieNummer " +
				"FROM Support subS, Gebruiker subG LEFT OUTER JOIN subG.bedrijvenManagedDoorGebruikerId subBm WITH subBm.bedrijf.bedrijfId = :bId " +
				"WHERE subS.gebruikerByGebruikerId = subG AND (subG.bedrijf.bedrijfId = :bId OR subBm.bedrijf.bedrijfId = :bId) ) ) " +
			"AND s.supportStatus.code <> 'ARC'")
	List<Support> findAllSupportTicketsByBedrijfId(@Param("bId") Integer bId);

	//Deletes a support ticket by its ID
	@Query("DELETE SupportBestand sb " +
			"WHERE sb.support.supportId = :sId")
	void deleteSupportTicket(@Param("sId") Integer sId);

	//Gets the start and end date of a support chain, based on the meldingId of the first support ticket
	@Query("SELECT MIN(s.datumAangemaakt), " +
			"MAX(s.datumAangemaakt) " +
			"FROM Support s " +
			"WHERE s.supportStatus.code <> 'ARC' " +
			"AND s.referentieNummer = (" +
				"SELECT subS.referentieNummer " +
				"FROM Support subS " +
				"WHERE subS.melding.meldingId=:mid )")
	List<Object[]> findStartAndEndOfSupportTicketChainByMeldingId(@Param("mid") Integer mId);

	//Gets a support ticket by its id
	@Query("FROM Support s FETCH ALL PROPERTIES " +
			"WHERE s.supportId = :sid")
	Support findSupportTicketBySupportId(@Param("sid") Integer sId);

	//Gets all support tickets of a company
	@Query("SELECT s " +
			"FROM Support s " +
			"JOIN s.melding m " +
			"WHERE m.bedrijfByMeldingOverBedrijfId.bedrijfId = :bid " +
			"AND s.supportStatus <> 'GST'")
	Page<Support> findSupportTicketsAboutBedrijfByBedrijfId(@Param("bid") Integer bId, Pageable p);

	//Gets all support tickets from a user
	@Query("SELECT s2.supportId, " +
			"s.supportType.code, " +
			"m, " +
			"s.supportReden.code, " +
			"s2.bericht, " +
			"s2.referentieNummer, " +
			"s2.datumAangemaakt, " +
			"s2.datumUpdate, " +
			"s2.supportStatus.code, " +
			"s2.support.supportId, " +
			"g, " +
			"gGeslotenDoor, " +
			"s2.betwistBezwaar, " +
			"s2.bedrijf.bedrijfId, " +
			"m.referentieNummer, " +
			"b.bedrijfsNaam " +
			"FROM Support s " +
			"LEFT OUTER JOIN s.melding m " +
			"LEFT OUTER JOIN m.bedrijfByMeldingOverBedrijfId b, " +
			"Support s2 " +
			"LEFT OUTER JOIN s2.gebruikerByGeslotenDoorGebruikerId gGeslotenDoor, " +
			"Gebruiker g " +
			"WHERE s.referentieNummer = s2.referentieNummer " +
			"AND " +
			"(" +
				"(" +
					"s.referentieNummer IN " +
					"(" +
						"SELECT referentieNummer " +
						"FROM Support " +
						"WHERE gebruikerByGebruikerId.gebruikerId = :gid " +
						"OR " +
						"(" +
							"gebruikerByGeslotenDoorGebruikerId.gebruikerId = :gid " +
							"AND supportStatus.code IN ('IBH')" +
						")" +
					") " +
					"AND s.support.supportId IS NULL " +
					"AND s2.gebruikerByGebruikerId.gebruikerId = :gid " +
					"OR " +
					"(" +
						"s2.gebruikerByGeslotenDoorGebruikerId.gebruikerId = :gid " +
						"AND s2.supportStatus.code IN ('IBH')" +
					")" +
				")" +
				"OR " +
				"(" +
					"s.referentieNummer IN " +
					"(" +
						"SELECT subS.referentieNummer " +
						"FROM Support subS " +
						"INNER JOIN subS.melding subM " +
						"WHERE subM.bedrijfByMeldingDoorBedrijfId.bedrijfId = " +
						"(" +
							"SELECT subG.bedrijf.bedrijfId " +
							"FROM Gebruiker subG " +
							"WHERE subG.gebruikerId = :gid" +
						") " +
						"AND subS.supportStatus.code in ('OPN', 'GST') " +
						"AND s.melding.meldingId IS NOT NULL " +
						"AND s2.support.supportId IS NOT NULL " +
					")" +
				")" +
			") " +
			"AND g.gebruikerId = s2.gebruikerByGebruikerId.gebruikerId " +
			"ORDER BY s2.datumAangemaakt DESC")
	Page<Object[]> findSupportTicketsByGebruikerId(@Param("gid") Integer gId, Pageable p);

	//Gets all support tickets in a chain
	@Query("FROM Support s " +
			"WHERE s.referentieNummer = :ref")
	List<Support> findSupportTicketsByReferentieNummer(@Param("ref") String ref);

	//Gets all support tickets of a type
	@Query("SELECT s FROM Support s " +
			"WHERE s.supportType = :type " +
			"AND s.supportStatus <> 'GST'")
	Page<Support> findSupportTicketsBySupportType(@Param("type") String type, Pageable p);

	@Query("FROM Support s " +
			"WHERE s.supportStatus.code IN ('OPN', 'IBH') " +
			"AND s.supportType.code = 'BZW' " +
			"AND s.datumAangemaakt < :date")
	List<Support> findSupportTicketsMadeBeforeDate(@Param("date") Date threshold);
}
