package nl.devoorkant.sbdr.data.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.Alert;
import nl.devoorkant.sbdr.data.model.ContactMoment;

public interface ContactMomentDataService {

	ContactMoment findById(Integer contactMomentId) throws DataServiceException;
	
	/**
	 * Save the state of the provided
	 * ContactMoment object into the data
	 * repository.
	 *
	 * @param bedrijf contactMoment to save
	 *
	 * @return ContactMoment
	 */
	ContactMoment save(ContactMoment contactMoment) throws DataServiceException;
	
	void delete(Integer contactMomentId) throws DataServiceException;

	List<Object[]> findAllOfNotification(Integer meldingId) throws DataServiceException;

}
