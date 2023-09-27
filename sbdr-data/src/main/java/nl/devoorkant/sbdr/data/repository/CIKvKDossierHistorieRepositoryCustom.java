package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.CIKvKDossierHistorie;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Extension for CompanyInfoRepository bean.
 * <p/>
 * SBDR - Stichting Bedrijfskrediet Data Registratie
 * <p/>
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author          Jan Meekel
 * @version         %I%
 */

public interface CIKvKDossierHistorieRepositoryCustom {

    List<CIKvKDossierHistorie> findByKvKnummer(@Param("kvKnummer") String kvKnummer);

    /**
     * Returns a Page containing {@link nl.devoorkant.sbdr.data.model.CompanyInfo} Objects with the requested bedrijfsNaam.<br/>
     *
     * @param bedrijfsNaam  a String containing the requested value for bedrijfsNaam
     * @param pageable      a Pageable, containing the parameters for creating the requested Page
     * @return 		        a Page with CIKvKDossierHistorie, or NULL when the CIKvKDossierHistorie could not be retrieved..
     */
    @Query("SELECT dh FROM CIKvKDossierHistorie dh WHERE dh.handelsNaam = :bedrijfsNaam")
    Page<CIKvKDossierHistorie> findByBedrijfsNaam(@Param("bedrijfsNaam") String bedrijfsNaam, Pageable pageable);

    /**
     * Returns a Page containing {@link nl.devoorkant.sbdr.data.model.CompanyInfo} Objects where the bedrijfsNaam contains the requested value.<br/>
     *
     * @param bedrijfsNaam  a String containing the requested value for bedrijfsNaam
     * @param pageable      a Pageable, containing the parameters for creating the requested Page
     * @return 		        a Page with CIKvKDossierHistorie, or NULL when the CIKvKDossierHistorie could not be retrieved..
     */
    @Query("SELECT dh FROM CIKvKDossierHistorie dh WHERE dh.handelsNaam like :bedrijfsNaam")
    Page<CIKvKDossierHistorie> findByBedrijfsNaamLike(@Param("bedrijfsNaam") String bedrijfsNaam, Pageable pageable);
}
