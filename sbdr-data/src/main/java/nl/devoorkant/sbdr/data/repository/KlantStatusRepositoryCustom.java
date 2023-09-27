package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.KlantStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

public interface KlantStatusRepositoryCustom {

    /**
     * Returns all Bevoegdheden with the requested actief value
     *
     * @param actief    a Boolean containing the requested value for actief
     * @return          a List of KlantStatus objects with the requested actief value, or null if not available
     */
    @Query("FROM KlantStatus WHERE actief = :actief ORDER BY omschrijving ASC")
    List<KlantStatus> findByActief(@Param("actief") Boolean actief);

    /**
     * Returns a Page containing KlantStatus objects with the requested omschrijving.<br/>
     *
     * @param omschrijving  a String containing the requested value for omschrijving
     * @param pageable      a Pageable, containing the parameters for creating the requested Page
     * @return 		        a Page with KlantStatus objects, or NULL when no KlantStatus objects could be retrieved.
     */
    @Query("SELECT ks FROM KlantStatus ks WHERE ks.omschrijving = :omschrijving")
    Page<KlantStatus> findByOmschrijving(@Param("omschrijving") String omschrijving, Pageable pageable);

    /**
     * Returns a Page containing KlantStatus objects where the omschrijving contains the requested value.<br/>
     *
     * @param omschrijving  a String containing the requested value for omschrijving
     * @param pageable      a Pageable, containing the parameters for creating the requested Page
     * @return 		        a Page with KlantStatus objects, or NULL when no KlantStatus objects could be retrieved.
     */
    @Query("SELECT ks FROM KlantStatus ks WHERE ks.omschrijving like :omschrijving")
    Page<KlantStatus> findByOmschrijvingLike(@Param("omschrijving") String omschrijving, Pageable pageable);
}
