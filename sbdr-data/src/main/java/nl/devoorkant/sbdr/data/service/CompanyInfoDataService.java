package nl.devoorkant.sbdr.data.service;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.CIKvKAandeelhouder;
import nl.devoorkant.sbdr.data.model.CIKvKBestuurder;
import nl.devoorkant.sbdr.data.model.CIKvKCurator;
import nl.devoorkant.sbdr.data.model.CIKvKDossier;
import nl.devoorkant.sbdr.data.model.CIKvKDossierHistorie;

public interface CompanyInfoDataService {
	void deleteCIKvkBestuurder(CIKvKBestuurder bestuurder) throws DataServiceException;
	void deleteCIKvkAandeelhouder(CIKvKAandeelhouder aandeelhouder) throws DataServiceException;
	void deleteCIKvkCurator(CIKvKCurator curator) throws DataServiceException;
	
	/**
	 * Delete the associated CIKvKDossier
	 * record from the data repository.
	 *
	 * @param kvkDossier
	 * @return CIKvKDossier object
	 */
	void deleteCIKvkDossier(CIKvKDossier kvkDossier) throws DataServiceException;

	/**
	 * Get CIKvKDossier with the associated kvkNumber
	 *
	 * @param kvkNumber - KvK Number
	 * @param subDossier - Sub Dossier Number
	 * @return CIKvKDossier object
	 */
	CIKvKDossier findCIKvkDossierByKvkNumber(String kvkNumber, String subDossier) throws DataServiceException;

	CIKvKDossier findByCIKvKDossierId(Integer ciKvKDossierId) throws DataServiceException;
	
	CIKvKDossier findById(Integer ciKvkDossierId) throws DataServiceException;

	/**
	 * Save the state of the provided
	 * CIKvKDossier object into the data
	 * repository.
	 *
	 * @param kvkDossier
	 * @return
	 */
	CIKvKDossier saveCIKvkDossier(CIKvKDossier kvkDossier) throws DataServiceException;

	/**
	 * Save the state of the provided
	 * CIKvKDossierHisorie object into the data
	 * repository.
	 *
	 * @param kvkDossier
	 * @return CIKvKDossierHistorie object
	 */
	CIKvKDossierHistorie saveCIKvkDossierHistorie(CIKvKDossierHistorie kvkDossier) throws DataServiceException;
}