package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.SupportBestand;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
public interface SupportBestandRepositoryCustom {

	//Deletes a support ticket file by the support ID
	@Query("DELETE SupportBestand sb WHERE sb.support.supportId = :sId")
	void deleteSupportTicketBestandBySupportId(@Param("sId") Integer sId);

	//Gets the support ticket file by its reference number
	@Query("FROM SupportBestand sb WHERE sb.referentieNummer = :ref")
	List<SupportBestand> findSupportTicketBestandenByReferentieNummer(@Param("ref") String ref);

	//Gets a support ticket file by its ID
	@Query("FROM SupportBestand sb WHERE sb.supportBestandId = :sBId")
	SupportBestand findSupportTicketBestandBySupportBestandId(@Param("sBId") Integer sBId);

	//Gets all support ticket files by the given support ID
	@Query("FROM SupportBestand sb WHERE sb.support.supportId = :sId")
	List<SupportBestand> findSupportTicketBestandenBySupportId(@Param("sId") Integer sId);
}
