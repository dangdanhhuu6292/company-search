package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.Rol;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Extension for RolRepository bean.
 * <p/>
 * SBDR - Stichting Bedrijfskrediet Data Registratie
 * <p/>
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author          Jan Meekel
 * @version         %I%
 */

public interface RolRepositoryCustom {

    /**
     * Returns all Rol objects with the requested actief value
     *
     * @param actief    a Boolean containing the requested value for actief
     * @return          a List of Rol objects with the requested actief value, or null if not available
     */
    @Query("FROM Rol WHERE actief = :actief ORDER BY omschrijving ASC")
    List<Rol> findByActief(@Param("actief") Boolean actief);

    /**
     * Returns a List containing Rol objects with the requested code.<br/>
     *
     * @param code  a String containing the requested value for omschrijving
     * @return 		        a List with Rol objects, or NULL when no Product objects could be retrieved.
     */
    @Query("FROM Rol WHERE code = :code and actief=1")
    List<Rol> findByCode(@Param("code") String code);

    
    /**
     * Returns a Page containing Rol objects with the requested omschrijving.<br/>
     *
     * @param omschrijving  a String containing the requested value for omschrijving
     * @param pageable      a Pageable, containing the parameters for creating the requested Page
     * @return 		        a Page with Rol objects, or NULL when no Product objects could be retrieved.
     */
    @Query("SELECT r FROM Rol r WHERE r.omschrijving = :omschrijving")
    Page<Rol> findByOmschrijving(@Param("omschrijving") String omschrijving, Pageable pageable);

    /**
     * Returns a Page containing Bevoegdheden where the omschrijving contains the requested value.<br/>
     *
     * @param omschrijving  a String containing the requested value for omschrijving
     * @param pageable      a Pageable, containing the parameters for creating the requested Page
     * @return 		        a Page with Rol objects, or NULL when no Product objects could be retrieved.
     */
    @Query("SELECT r FROM Rol r WHERE r.omschrijving like :omschrijving")
    Page<Rol> findByOmschrijvingLike(@Param("omschrijving") String omschrijving, Pageable pageable);
}
