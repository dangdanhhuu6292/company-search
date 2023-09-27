package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.BedrijfStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Extension for BedrijfStatusRepository bean.
 * <p/>
 * SBDR - Stichting Bedrijfskrediet Data Registratie
 * <p/>
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author          Bas Dekker
 * @version         %I%
 */

public interface BedrijfStatusRepositoryCustom {
    /**
     * Returns all BedrijfStatussen with the requested actief value
     *
     * @param actief    a Boolean containing the requested value for actief
     * @return          a List of BedrijfStatus objects with the requested actief value, or null if not available
     */
    @Query("FROM BedrijfStatus WHERE actief = :actief ORDER BY omschrijving ASC")
    List<BedrijfStatus> findByActief(@Param("actief") Boolean actief);

    /**
     * Returns a Page containing BedrijfStatus objects with the requested omschrijving.<br/>
     *
     * @param omschrijving  a String containing the requested value for omschrijving
     * @param pageable      a Pageable, containing the parameters for creating the requested Page
     * @return 		        a Page with BedrijfStatus objects, or NULL when no BedrijfStatus objects could be retrieved.
     */
    @Query("SELECT bs FROM BedrijfStatus bs WHERE bs.omschrijving = :omschrijving")
    Page<BedrijfStatus> findByOmschrijving(@Param("omschrijving") String omschrijving, Pageable pageable);

    /**
     * Returns a Page containing BedrijfStatus objects where the omschrijving contains the requested value.<br/>
     *
     * @param omschrijving  a String containing the requested value for omschrijving
     * @param pageable      a Pageable, containing the parameters for creating the requested Page
     * @return 		        a Page with BedrijfStatus objects, or NULL when no BedrijfStatus objects could be retrieved.
     */
    @Query("SELECT bs FROM BedrijfStatus bs WHERE bs.omschrijving like :omschrijving")
    Page<BedrijfStatus> findByOmschrijvingLike(@Param("omschrijving") String omschrijving, Pageable pageable);
}
