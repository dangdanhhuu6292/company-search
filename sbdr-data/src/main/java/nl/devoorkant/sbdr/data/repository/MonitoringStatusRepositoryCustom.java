package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.MonitoringStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Extension for MonitoringStatusRepository bean.
 * <p/>
 * SBDR - Stichting Bedrijfskrediet Data Registratie
 * <p/>
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author          Bas Dekker
 * @version         %I%
 */

public interface MonitoringStatusRepositoryCustom {
    /**
     * Returns all Bevoegdheden with the requested actief value
     *
     * @param actief    a Boolean containing the requested value for actief
     * @return          a List of MonitoringStatus objects with the requested actief value, or null if not available
     */
    @Query("FROM MonitoringStatus WHERE actief = :actief ORDER BY omschrijving ASC")
    List<MonitoringStatus> findByActief(@Param("actief") Boolean actief);

    /**
     * Returns a Page containing MonitoringStatus objects with the requested omschrijving.<br/>
     *
     * @param omschrijving  a String containing the requested value for omschrijving
     * @param pageable      a Pageable, containing the parameters for creating the requested Page
     * @return 		        a Page with MonitoringStatus objects, or NULL when no MonitoringStatus objects could be retrieved.
     */
    @Query("SELECT ms FROM MonitoringStatus ms WHERE ms.omschrijving = :omschrijving")
    Page<MonitoringStatus> findByOmschrijving(@Param("omschrijving") String omschrijving, Pageable pageable);

    /**
     * Returns a Page containing MonitoringStatus objects where the omschrijving contains the requested value.<br/>
     *
     * @param omschrijving  a String containing the requested value for omschrijving
     * @param pageable      a Pageable, containing the parameters for creating the requested Page
     * @return 		        a Page with MonitoringStatus objects, or NULL when no MonitoringStatus objects could be retrieved.
     */
    @Query("SELECT ms FROM MonitoringStatus ms WHERE ms.omschrijving like :omschrijving")
    Page<MonitoringStatus> findByOmschrijvingLike(@Param("omschrijving") String omschrijving, Pageable pageable);    
}
