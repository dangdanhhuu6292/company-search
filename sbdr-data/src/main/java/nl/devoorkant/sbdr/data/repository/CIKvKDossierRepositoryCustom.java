package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.Bedrijf;
import nl.devoorkant.sbdr.data.model.CIKvKDossier;
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

public interface CIKvKDossierRepositoryCustom {

    @Query("FROM CIKvKDossier d WHERE d.kvKnummer = :kvKNummer " +
	"AND ((:subDossier IS NULL " +
	"AND d.subDossier IS NULL) OR d.subDossier = :subDossier)")
    List<CIKvKDossier> findByKvKNummer(@Param("kvKNummer") String kvKNummer, @Param("subDossier") String subDossier);    

    @Query("FROM CIKvKDossier d WHERE d.cikvKdossierId = :ciKvKDossierId")
    CIKvKDossier findByCIKvKDossier(@Param("ciKvKDossierId") Integer ciKvKDossierId);
    
    /**
     * Returns a Page containing {@link nl.devoorkant.sbdr.data.model.CompanyInfo} Objects with the requested bedrijfsNaam.<br/>
     *
     * @param bedrijfsNaam  a String containing the requested value for bedrijfsNaam
     * @param pageable      a Pageable, containing the parameters for creating the requested Page
     * @return 		        a Page with Bedrijven, or NULL when the Bedrijven could not be retrieved..
     */
    @Query("SELECT d FROM CIKvKDossier d WHERE d.handelsNaam = :bedrijfsNaam")
    Page<CIKvKDossier> findByBedrijfsNaam(@Param("bedrijfsNaam") String bedrijfsNaam, Pageable pageable);

    /**
     * Returns a Page containing {@link nl.devoorkant.sbdr.data.model.CompanyInfo} Objects where the bedrijfsNaam contains the requested value.<br/>
     *
     * @param bedrijfsNaam  a String containing the requested value for bedrijfsNaam
     * @param pageable      a Pageable, containing the parameters for creating the requested Page
     * @return 		        a Page with Bedrijven, or NULL when the Bedrijven could not be retrieved..
     */
    @Query("SELECT d FROM CIKvKDossier d WHERE d.handelsNaam like :bedrijfsNaam")
    Page<CIKvKDossier> findByBedrijfsNaamLike(@Param("bedrijfsNaam") String bedrijfsNaam, Pageable pageable);
}
