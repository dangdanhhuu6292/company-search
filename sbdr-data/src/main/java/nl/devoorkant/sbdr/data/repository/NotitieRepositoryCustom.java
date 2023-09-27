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
 * @author Martijn Bruinenberg
 * @version %I%
 */

public interface NotitieRepositoryCustom {

	@Query("FROM Notitie n WHERE n.meldingOfNotitieGebruiker.meldingId = :meldingId AND n.notitieType = :notitieType AND n.gebruiker.gebruikerId = :gebruikerId")
    Notitie findByNotitieMeldingGebruikerWithGebruikerId(@Param("meldingId") Integer meldingId, @Param("gebruikerId") Integer gebruikerId, @Param("notitieType") String notitieType);

	@Query("FROM Notitie n WHERE n.meldingOfNotitieGebruiker.meldingId = :meldingId AND n.notitieType = :notitieType")
    Notitie findByNotitieMeldingGebruiker(@Param("meldingId") Integer meldingId, @Param("notitieType") String notitieType);
	
	@Query("FROM Notitie n WHERE n.meldingOfNotitieAdmin.meldingId = :meldingId AND n.notitieType = :notitieType")
    Notitie findByNotitieMeldingAdmin(@Param("meldingId") Integer meldingId, @Param("notitieType") String notitieType);
		
}