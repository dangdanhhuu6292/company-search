package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.Gebruiker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Extension for GebruikerRepository bean.
 * <p/>
 * SBDR - Stichting Bedrijfskrediet Data Registratie
 * <p/>
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author          Jan Meekel
 * @version         %I%
 */

public interface GebruikerRolRepositoryCustom {

    /**
     * Returns all Gebruikers with the requested activity indication
     *
     * @param actief    a Boolean containing the activity indication of the Gebruiker
     * @return          a List of Gebruiker objects with the requested activity indication, or null if not available
     */
    @Query("FROM Gebruiker WHERE actief = :actief ORDER BY gebruikersNaam ASC")
    List<Gebruiker> findByActief(@Param("actief") Boolean actief);

    /**
     * Returns a Page containing gebruikers with the requested gebruikersNaam.<br/>
     *
     * @param gebruikersNaam  a String containing the requested value for gebruikersNaam
     * @param pageable      a Pageable, containing the parameters for creating the requested Page
     * @return 		        a Page with Gebruikers, or NULL when the Gebruikers could not be retrieved..
     */
    @Query("SELECT g FROM Gebruiker g WHERE g.gebruikersNaam = :gebruikersNaam")
    Page<Gebruiker> findByGebruikersNaam(@Param("gebruikersNaam") String gebruikersNaam, Pageable pageable);

    /**
     * Returns a Page containing gebruikers with the requested gebruikersNaam.<br/>
     *
     * @param gebruikersNaam  a String containing the requested value for gebruikersNaam
     * @return 		        a List with Gebruikers, or NULL when the Gebruikers could not be retrieved..
     */
    List<Gebruiker> findByGebruikersNaam(@Param("gebruikersNaam") String gebruikersNaam);

    
    /**
     * Returns a Page containing gebruikers where the gebruikersNaam contains the requested value.<br/>
     *
     * @param gebruikersNaam  a String containing the requested value for gebruikersNaam
     * @param pageable      a Pageable, containing the parameters for creating the requested Page
     * @return 		        a Page with Bedrijven, or NULL when the Bedrijven could not be retrieved..
     */
    @Query("SELECT g FROM Gebruiker g WHERE g.gebruikersNaam like :gebruikersNaam")
    Page<Gebruiker> findByGebruikersNaamLike(@Param("gebruikersNaam") String gebruikersNaam, Pageable pageable);
}
