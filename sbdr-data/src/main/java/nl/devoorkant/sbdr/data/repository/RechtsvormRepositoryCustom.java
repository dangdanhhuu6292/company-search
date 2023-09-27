package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.Rechtsvorm;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Extension 
 * <p/>
 * SBDR - Stichting Bedrijfskrediet Data Registratie
 * <p/>
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author          Jan Meekel
 * @version         %I%
 */

public interface RechtsvormRepositoryCustom {

    /**
     * Returns all Sbi with the requested code value
     *
     * @param actief    a String containing the requested value for code
     * @return          a List of Sbi objects with the requested code value, or null if not available
     */
    @Query("FROM Rechtsvorm WHERE code = :code")
    List<Rechtsvorm> findByCode(@Param("code") String code);
    
    @Query("FROM Rechtsvorm WHERE omschrijving = :omschrijving")
    List<Rechtsvorm> findByOmschrijving(@Param("omschrijving") String omschrijving);
    
}
