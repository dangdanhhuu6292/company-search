package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.Alert;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Extension for BedrijfRepository bean.
 * <p/>
 * SBDR - Stichting Bedrijfskrediet Data Registratie
 * <p/>
 * Copyright:       Copyright (c) 2015. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author Coen Hasselaar
 * @version %I%
 */
public interface AlertRepositoryCustom {

	@Query("FROM Alert a WHERE a.bedrijfByBedrijfId.bedrijfId = :bId " + "OR a.bedrijfByAlertOverBedrijfId.bedrijfId = :bId")
	List<Alert> findAlertByBedrijfId(@Param("bId") Integer bId);

	//Gets an alert object based on a support ID
	@Query("FROM Alert a WHERE a.support.supportId = :sId")
	List<Alert> findAlertBySupportId(@Param("sId") Integer sId);

	@Query("FROM Alert a WHERE a.support.supportId = :sId AND a.bedrijfByBedrijfId.bedrijfId = :bId")
	Alert findAlertBySupportIdAndBedrijfId(@Param("sId") Integer sId, @Param("bId") Integer bId);

	//@Query("FROM Alert a WHERE a.gebruiker.gebruikerId = :gId AND a.gebruiker.actief = TRUE")
	// MBR 7-12-2017 BedrijfManaged users added
	@Query("FROM Alert a LEFT OUTER JOIN a.gebruiker g LEFT OUTER JOIN g.bedrijvenManagedDoorGebruikerId bm with bm.bedrijf.bedrijfId = :bId AND bm.actief = TRUE WHERE g.gebruikerId = :gId AND g.bedrijf.bedrijfId = :bId AND g.actief = TRUE")
	List<Alert> findAlertsMeantForGebruiker(@Param("gId") Integer gId, @Param("bId") Integer bId);
}
