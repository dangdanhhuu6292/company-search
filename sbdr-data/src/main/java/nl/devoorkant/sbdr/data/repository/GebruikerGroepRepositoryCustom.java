package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.GebruikerGroep;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Extension for GebruikerGroepRepository bean.
 * <p/>
 * SBDR - Stichting Bedrijfskrediet Data Registratie
 * <p/>
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author          Jan Meekel
 * @version         %I%
 */

public interface GebruikerGroepRepositoryCustom {

    /**
     * Returns all GebruikerGroepen with the requested activity indication
     *
     * @param actief    a Boolean containing the activity indication of the GebruikerGroep
     * @return          a List of GebruikerGroep objects with the requested activity indication, or null if not available
     */
    @Query("FROM GebruikerGroep WHERE actief = :actief ORDER BY naam ASC")
    List<GebruikerGroep> findByActief(@Param("actief") Boolean actief);

    /**
     * Returns a Page containing GebruikerGroepen with the requested naam.<br/>
     *
     * @param naam          a String containing the requested value for naam
     * @param pageable      a Pageable, containing the parameters for creating the requested Page
     * @return 		        a Page with Gebruikergroepen, or NULL when the GebruikerGroepen could not be retrieved.
     */
    @Query("SELECT gg FROM GebruikerGroep gg WHERE gg.naam = :naam")
    Page<GebruikerGroep> findByNaam(@Param("naam") String naam, Pageable pageable);

    /**
     * Returns a Page containing GebruikerGroepen where the naam contains the requested value.<br/>
     *
     * @param naam          a String containing the requested value for naam
     * @param pageable      a Pageable, containing the parameters for creating the requested Page
     * @return 		        a Page with Gebruikergroepen, or NULL when the GebruikerGroepen could not be retrieved.
     */
    @Query("SELECT gg FROM GebruikerGroep gg WHERE gg.naam like :naam")
    Page<GebruikerGroep> findByNaamLike(@Param("naam") String naam, Pageable pageable);
}
