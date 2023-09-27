package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.WachtwoordStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Extension for WachtwoordStatusRepository bean.
 * <p/>
 * SBDR - Stichting Bedrijfskrediet Data Registratie
 * <p/>
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author          Jan Meekel
 * @version         %I%
 */

public interface WachtwoordStatusRepositoryCustom {

    /**
     * Returns all Bevoegdheden with the requested actief value
     *
     * @param actief    a Boolean containing the requested value for actief
     * @return          a List of WachtwoordStatus objects with the requested actief value, or null if not available
     */
    @Query("FROM WachtwoordStatus WHERE actief = :actief ORDER BY omschrijving ASC")
    List<WachtwoordStatus> findByActief(@Param("actief") Boolean actief);

    /**
     * Returns a Page containing WachtwoordStatus objects with the requested omschrijving.<br/>
     *
     * @param omschrijving  a String containing the requested value for omschrijving
     * @param pageable      a Pageable, containing the parameters for creating the requested Page
     * @return 		        a Page with WachtwoordStatus objects, or NULL when no WachtwoordStatus objects could be retrieved.
     */
    @Query("SELECT ws FROM WachtwoordStatus ws WHERE ws.omschrijving = :omschrijving")
    Page<WachtwoordStatus> findByOmschrijving(@Param("omschrijving") String omschrijving, Pageable pageable);

    /**
     * Returns a Page containing WachtwoordStatus objects where the omschrijving contains the requested value.<br/>
     *
     * @param omschrijving  a String containing the requested value for omschrijving
     * @param pageable      a Pageable, containing the parameters for creating the requested Page
     * @return 		        a Page with WachtwoordStatus objects, or NULL when no WachtwoordStatus objects could be retrieved.
     */
    @Query("SELECT ws FROM WachtwoordStatus ws WHERE ws.omschrijving like :omschrijving")
    Page<WachtwoordStatus> findByOmschrijvingLike(@Param("omschrijving") String omschrijving, Pageable pageable);
}
