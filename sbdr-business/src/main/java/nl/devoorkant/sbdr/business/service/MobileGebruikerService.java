package nl.devoorkant.sbdr.business.service;

import nl.devoorkant.sbdr.data.model.MobileGebruiker;

import java.util.Date;
import java.util.List;

public interface MobileGebruikerService {
	MobileGebruiker addMobileGebruikerRecord(Integer gId, String key) throws ServiceException;

	List<MobileGebruiker> findByGebruikerId(Integer gId) throws ServiceException;

	MobileGebruiker findByGebruikerIdAndKey(Integer gId, String key) throws ServiceException;

	MobileGebruiker findByKey(String key) throws ServiceException;

	void removeMobileGebruikerRecord(String key) throws ServiceException;

	void updateMobileGebruikerRecord(String key, Date updatedDate) throws ServiceException;

	void removeAllMobileGebruikerRecordsByBedrijfId(Integer bId) throws ServiceException;
}
