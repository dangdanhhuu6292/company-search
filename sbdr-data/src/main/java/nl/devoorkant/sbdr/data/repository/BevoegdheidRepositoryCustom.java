package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.Bevoegdheid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Extension for BevoegdheidRepository bean.
 * <p/>
 * SBDR - Stichting Bedrijfskrediet Data Registratie
 * <p/>
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author          Jan Meekel
 * @version         %I%
 */

public interface BevoegdheidRepositoryCustom {

    /**
     * Returns all Bevoegdheden with the requested actief value
     *
     * @param actief    a Boolean containing the requested value for actief
     * @return          a List of Bevoegdheid objects with the requested actief value, or null if not available
     */
    @Query("FROM Bevoegdheid WHERE actief = :actief ORDER BY omschrijving ASC")
    List<Bevoegdheid> findByActief(@Param("actief") Boolean actief);

    /**
     * Returns a Page containing Bevoegdheden with the requested omschrijving.<br/>
     *
     * @param omschrijving  a String containing the requested value for omschrijving
     * @param pageable      a Pageable, containing the parameters for creating the requested Page
     * @return 		        a Page with Bevoegdheden, or NULL when the Bevoegdheden could not be retrieved..
     */
    @Query("SELECT b FROM Bevoegdheid b WHERE b.omschrijving = :omschrijving")
    Page<Bevoegdheid> findByOmschrijving(@Param("omschrijving") String omschrijving, Pageable pageable);

    /**
     * Returns a Page containing Bevoegdheden where the omschrijving contains the requested value.<br/>
     *
     * @param omschrijving  a String containing the requested value for omschrijving
     * @param pageable      a Pageable, containing the parameters for creating the requested Page
     * @return 		        a Page with Bevoegdheden, or NULL when the Bevoegdheden could not be retrieved..
     */
    @Query("SELECT b FROM Bevoegdheid b WHERE b.omschrijving like :omschrijving")
    Page<Bevoegdheid> findByOmschrijvingLike(@Param("omschrijving") String omschrijving, Pageable pageable);
}
