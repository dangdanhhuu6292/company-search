package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.CustomMelding;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Extension for CusomMeldingRepository bean.
 * <p/>
 * SBDR - Stichting Bedrijfskrediet Data Registratie
 * <p/>
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author          Jan Meekel
 * @version         %I%
 */

public interface CustomMeldingRepositoryCustom {
    
    @Query("FROM CustomMelding c WHERE c.customMeldingId = :customMeldingId AND c.bedrijf.bedrijfId = :bedrijfId")
    CustomMelding findByCustomMeldingIdBedrijfId(@Param("customMeldingId") Integer customMeldingId, @Param("bedrijfId") Integer bedrijfId);

}
