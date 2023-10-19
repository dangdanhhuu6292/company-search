package nl.devoorkant.sbdr.business.service;

import nl.devoorkant.sbdr.data.model.CIKvKDossier;
import nl.devoorkant.sbdr.data.model.CompanyInfo;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Interface exposing functionality for CompanyInfo.
 * <p/>
 * SBDR - Stichting Bedrijfskrediet Data Registratie
 * <p/>
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author          Jan Meekel
 * @version         %I%
 */

public interface CompanyInfoService {

    /**
     * Returns a List containing CompanyInfo objects returned bij the CompanyInfo webservice, based on the passed search criteria.<br/>     *
     * @param searchValue  a String containing the requested value for bedrijfsNaam or KvK number
     * @param plaats        a String containing the requested value for plaats
     * @param maxResults    an int indicating the maximum number of companies to retrieve.
     * @return 		        a List of {@link nl.devoorkant.sbdr.data.model.CompanyInfo} objects, or NULL when the CompanyInfos could not be retrieved.
     */
    List<CompanyInfo> retrieveFromCompanyInfo(String searchValue, String plaats, int maxResults) throws ServiceException;

    /**
     * Find HQ company of kvknummer
     * 
     * @param kvknummer
     * @return
     * @throws ServiceException
     */
	CompanyInfo retrieveHqFromCompanyInfo(String kvknummer) throws ServiceException;

    /**
     * Returns a {@link nl.devoorkant.sbdr.data.model.CIKvKDossier} returned bij the CompanyInfo webservice, based on the passed kvKNummer.<br/>     *
     * @param kvKNummer     a String containing the requested value for kvkNummer
     * @param subdossier	CS subdossier number
     * @param isHoofdVestiging	is head office
     * @return 		        a {@link nl.devoorkant.sbdr.data.model.CIKvKDossier} object, or NULL when the CIKvKDossier could not be retrieved.
     */
    //CIKvKDossier getCIKvKDossierFromCompanyInfo(String kvKNummer)  throws ServiceException;
    CIKvKDossier getCIKvKDossierFromCompanyInfo(String kvKNummer, String subdossier, boolean isHoofdVestiging) throws ServiceException;
    /**
     * Returns the {@link nl.devoorkant.sbdr.data.model.CompanyInfo} Object by primary key.<br/>
     *
     * @param kvKNummer     a String containing the requested value for kvkNummer
     * @return 			    the {@link nl.devoorkant.sbdr.data.model.CompanyInfo} Object
     */
    List<CompanyInfo> findByKvKNummer(String kvKNummer);

    /**
     * Saves the presented {@link nl.devoorkant.sbdr.data.model.CompanyInfo} Object.
     *
     * @param companyInfo	    the {@link nl.devoorkant.sbdr.data.model.CompanyInfo} Object to save
     * @throws ServiceException as a reaction to all errors thrown by the persistence layer
     */
    @Transactional
    CompanyInfo saveCompanyInfo(CompanyInfo companyInfo) throws ServiceException;

    /**
     * Deletes the {@link nl.devoorkant.sbdr.data.model.CompanyInfo} Object identified by the presented id.
     *
     * @param companyInfo_ID	an Integer representing the {@link nl.devoorkant.sbdr.data.model.CompanyInfo} Object to delete
     * @throws ServiceException as a reaction to all errors thrown by the persistence layer
     */
    @Transactional
    void deleteCompanyInfo(Integer companyInfo_ID) throws ServiceException;

    @Transactional
	CIKvKDossier saveCIKvkDossier(CIKvKDossier ciKvkDossier)
			throws ServiceException;

    List<CIKvKDossier> retrieveCIKvKDossierInfos(String searchValue);
}