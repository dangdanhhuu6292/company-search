package nl.devoorkant.sbdr.data.service;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.Alert;

import java.util.List;

public interface AlertDataService {

	//Checks if any alerts exist with the given support ID
	Boolean checkIfAlertWithSupportIdExists(Integer sId) throws DataServiceException;

	//Deletes an alert
	void delete(Alert alert) throws DataServiceException;

	//Deletes an alert by its support ID
	void deleteAlertBySupportId(Integer sId, Integer bId) throws DataServiceException;

	//Finds an alert by its ID
	Alert findAlertByAlertId(Integer alertId) throws DataServiceException;

	Alert findAlertBySupportIdAndBedrijfId(Integer sId, Integer bId) throws DataServiceException;

	List<Alert> findAlertsMeantForGebruiker(Integer gId, Integer bedrijfId) throws DataServiceException;

	List<Alert> findAlertsOfBedrijfByBedrijfId(Integer bId) throws DataServiceException;

	//Saves an alert
	Alert save(Alert alert) throws DataServiceException;
}