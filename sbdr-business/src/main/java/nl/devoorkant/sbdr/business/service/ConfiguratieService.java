package nl.devoorkant.sbdr.business.service;

import nl.devoorkant.sbdr.data.model.Configuratie;
import nl.devoorkant.validation.Result;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;

/**
 * Interface exposing functionality for Configuraties.
 * <p/>
 * SBDR - Stichting Bedrijfskrediet Data Registratie
 * <p/>
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author          Jan Meekel
 * @version         %I%
 */

public interface ConfiguratieService {

    /**
     * Returns the Configuratie Entity by primary key.<br/>
     *
     * @param pstrCode	a String representing the ConfiguratieService to retrieve
     * @return 			the Configuratie Entity, or NULL when the Configuratie could not be retrieved.
     */
    Configuratie getConfiguratie(String pstrCode);

    /**
     * Returns all the displayable Configuraties.<br/>
     *
     * @return 		a Collection of displayable Configuraties.
     */
    Collection<Configuratie> getDisplayableConfiguraties();

    /**
     * Returns a Page containing configurations with the requested omschrijving.<br/>
     *
     * @param omschrijving  a String containing the requested value for omschrijving
     * @param pageable      a Pageable, containing the parameters for creating the requested Page
     * @return 		        a Page with Configuraties, or NULL when the Configuraties could not be retrieved..
     */
    Page<Configuratie> getConfiguratiePageByOmschrijving(String omschrijving, Pageable pageable);

    /**
     * Returns a Page containing configurations the omschrijving contains the requested value.<br/>
     *
     * @param omschrijving  a String containing the requested value for omschrijving
     * @param pageable      a Pageable, containing the parameters for creating the requested Page
     * @return 		        a Page with Configuraties, or NULL when the Configuraties could not be retrieved..
     */
    Page<Configuratie> getConfiguratiePageByOmschrijvingLike(String omschrijving, Pageable pageable);

    /**
     * Saves the passed Configuratie Entity.<br/>
     *
     * @param poConfiguratie 	the Configuratie Entity to save.
     * @return 			        a Result Object, containing the result of the save action.
     */
    Result saveConfiguratie(Configuratie poConfiguratie);
}