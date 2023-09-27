package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.Sbi;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Extension for KlantStatusRepository bean.
 * <p/>
 * SBDR - Stichting Bedrijfskrediet Data Registratie
 * <p/>
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author          Jan Meekel
 * @version         %I%
 */

public interface SbiRepositoryCustom {

    /**
     * Returns all Sbi with the requested code value
     *
     * @param actief    a String containing the requested value for code
     * @return          a List of Sbi objects with the requested code value, or null if not available
     */
    @Query("FROM Sbi WHERE code = :code")
    List<Sbi> findByCode(@Param("code") String code);
}
