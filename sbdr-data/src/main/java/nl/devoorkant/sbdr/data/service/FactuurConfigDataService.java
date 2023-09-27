package nl.devoorkant.sbdr.data.service;

import java.util.List;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.FactuurConfig;

public interface FactuurConfigDataService {

	FactuurConfig save(FactuurConfig factuurConfig) throws DataServiceException;

	FactuurConfig findByProductCode(String productCode)
			throws DataServiceException;

	List<FactuurConfig> findAllActive() throws DataServiceException;
	
	List<FactuurConfig> findAllOfFrequency(String freq) throws DataServiceException;

}
