package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.KlantType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Extension for KlantTypeRepository bean.
 * <p/>
 * SBDR - Stichting Bedrijfskrediet Data Registratie
 * <p/>
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author          Bas Dekker
 * @version         %I%
 */

public interface KlantTypeRepositoryCustom {
    /**
     * Returns all KlantTypes with the requested actief value
     *
     * @param actief    a Boolean containing the requested value for actief
     * @return          a List of KlantType objects with the requested actief value, or null if not available
     */
    @Query("FROM KlantType WHERE actief = :actief ORDER BY omschrijving ASC")
    List<KlantType> findByActief(@Param("actief") Boolean actief);

    /**
     * Returns a Page containing KlantType objects with the requested omschrijving.<br/>
     *
     * @param omschrijving  a String containing the requested value for omschrijving
     * @param pageable      a Pageable, containing the parameters for creating the requested Page
     * @return 		        a Page with KlantType objects, or NULL when no KlantType objects could be retrieved.
     */
    @Query("SELECT kt FROM KlantType kt WHERE kt.omschrijving = :omschrijving")
    Page<KlantType> findByOmschrijving(@Param("omschrijving") String omschrijving, Pageable pageable);

    /**
     * Returns a Page containing KlantStatus objects where the omschrijving contains the requested value.<br/>
     *
     * @param omschrijving  a String containing the requested value for omschrijving
     * @param pageable      a Pageable, containing the parameters for creating the requested Page
     * @return 		        a Page with KlantType objects, or NULL when no KlantType objects could be retrieved.
     */
    @Query("SELECT kt FROM KlantType kt WHERE kt.omschrijving like :omschrijving")
    Page<KlantType> findByOmschrijvingLike(@Param("omschrijving") String omschrijving, Pageable pageable);
}
