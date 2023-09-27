package nl.devoorkant.sbdr.data.service;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.CustomMelding;

public interface CustomMeldingDataService {

	/**
	 * 
	 * @param customMeldingId
	 * @return
	 * @throws DataServiceException
	 */
	CustomMelding findById(Integer customMeldingId) throws DataServiceException;

	/**
	 * 
	 * @param customMelding
	 * @return
	 * @throws DataServiceException
	 */
	CustomMelding save(CustomMelding customMelding) throws DataServiceException;

	/**
	 * 
	 * @param customMeldingId
	 * @param bedrijfId
	 * @return
	 * @throws DataServiceException
	 */
	CustomMelding findByBedrijfId(Integer customMeldingId, Integer bedrijfId)
			throws DataServiceException;
	

}
