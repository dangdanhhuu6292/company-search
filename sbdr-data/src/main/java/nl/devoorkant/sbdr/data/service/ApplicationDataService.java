package nl.devoorkant.sbdr.data.service;

import nl.devoorkant.sbdr.data.DataServiceException;


public interface ApplicationDataService {

	nl.devoorkant.sbdr.cir.data.model.Applicatie findByApplicationId(Integer applicationId)
			throws DataServiceException;

	nl.devoorkant.sbdr.data.model.Applicatie findByApplicationId2(Integer applicationId)
			throws DataServiceException;

}
