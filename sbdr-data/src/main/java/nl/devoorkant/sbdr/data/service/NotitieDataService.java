package nl.devoorkant.sbdr.data.service;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.Notitie;

public interface NotitieDataService {

	public Notitie findById(Integer notitieId) throws DataServiceException;
	
	public Notitie findByNotitieMeldingGebruiker(Integer meldingId, Integer gebruikerId, String notitieType) throws DataServiceException;
	
	/**
	 * Save the state of the provided
	 * Notitie object into the data
	 * repository.
	 *
	 * @param bedrijf notitie to save
	 *
	 * @return Notitie
	 */
	Notitie save(Notitie notitie) throws DataServiceException;

}
