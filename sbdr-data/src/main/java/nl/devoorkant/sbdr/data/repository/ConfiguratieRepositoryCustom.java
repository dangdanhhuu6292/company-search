package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.Configuratie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Extension for ConfiguratieRepository bean.
 * <p/>
 * SBDR - Stichting Bedrijfskrediet Data Registratie
 * <p/>
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author          Jan Meekel
 * @version         %I%
 */

public interface ConfiguratieRepositoryCustom {

    /**
     * Returns all Configuraties with the requested indTonen value
     *
     * @param indTonen  a Boolean containing the requested value for indTonen
     * @return          a List of Configuratie objects with the requested indTonen value, or null if not available
     */
    @Query("FROM Configuratie WHERE indTonen = :indTonen ORDER BY omschrijving ASC")
    List<Configuratie> findByIndTonen(@Param("indTonen") Boolean indTonen);

    /**
     * Returns a Page containing configurations with the requested omschrijving.<br/>
     *
     * @param omschrijving  a String containing the requested value for omschrijving
     * @param pageable      a Pageable, containing the parameters for creating the requested Page
     * @return 		        a Page with Configuraties, or NULL when the Configuraties could not be retrieved..
     */
    @Query("SELECT c FROM Configuratie c WHERE c.omschrijving = :omschrijving")
    Page<Configuratie> findByOmschrijving(@Param("omschrijving") String omschrijving, Pageable pageable);

    /**
     * Returns a Page containing configurations the omschrijving contains the requested value.<br/>
     *
     * @param omschrijving  a String containing the requested value for omschrijving
     * @param pageable      a Pageable, containing the parameters for creating the requested Page
     * @return 		        a Page with Configuraties, or NULL when the Configuraties could not be retrieved..
     */
    @Query("SELECT c FROM Configuratie c WHERE c.omschrijving like :omschrijving")
    Page<Configuratie> findByOmschrijvingLike(@Param("omschrijving") String omschrijving, Pageable pageable);
}
