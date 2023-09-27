package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.Eigenschap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Extension for EigenschapRepository bean.
 * <p/>
 * SBDR - Stichting Bedrijfskrediet Data Registratie
 * <p/>
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author          Jan Meekel
 * @version         %I%
 */

public interface EigenschapRepositoryCustom {

    /**
     * Returns all Eigenschappen with the requested actief value
     *
     * @param actief    a Boolean containing the requested value for actief
     * @return          a List of Eigenschap objects with the requested actief value, or null if not available
     */
    @Query("FROM Eigenschap WHERE actief = :actief ORDER BY omschrijving ASC")
    List<Eigenschap> findByActief(@Param("actief") Boolean actief);

    /**
     * Returns a Page containing Eigenschappen with the requested omschrijving.<br/>
     *
     * @param omschrijving  a String containing the requested value for omschrijving
     * @param pageable      a Pageable, containing the parameters for creating the requested Page
     * @return 		        a Page with Eigenschappen, or NULL when the Eigenschappen could not be retrieved..
     */
    @Query("SELECT e FROM Eigenschap e WHERE e.omschrijving = :omschrijving")
    Page<Eigenschap> findByOmschrijving(@Param("omschrijving") String omschrijving, Pageable pageable);

    /**
     * Returns a Page containing Eigenschappen where the omschrijving contains the requested value.<br/>
     *
     * @param omschrijving  a String containing the requested value for omschrijving
     * @param pageable      a Pageable, containing the parameters for creating the requested Page
     * @return 		        a Page with Eigenschappen, or NULL when the Eigenschappen could not be retrieved..
     */
    @Query("SELECT e FROM Eigenschap e WHERE e.omschrijving like :omschrijving")
    Page<Eigenschap> findByOmschrijvingLike(@Param("omschrijving") String omschrijving, Pageable pageable);
}
