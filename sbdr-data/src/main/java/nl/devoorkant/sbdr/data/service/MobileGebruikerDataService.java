package nl.devoorkant.sbdr.data.service;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.MobileGebruiker;

import java.util.List;

public interface MobileGebruikerDataService {

	void delete(MobileGebruiker mG) throws DataServiceException;

	List<MobileGebruiker> findByGebruikerId(Integer gId) throws DataServiceException;

	MobileGebruiker findByGebruikerIdAndKey(Integer gId, String key) throws DataServiceException;

	MobileGebruiker findByKey(String key) throws DataServiceException;

	MobileGebruiker save(MobileGebruiker mG) throws DataServiceException;
}
