package nl.devoorkant.sbdr.data.service;

import nl.devoorkant.sbdr.data.DataServiceException;

import java.util.Date;

public interface KortingsCodeDataService {

	boolean checkIfCodeIsExpired(String code, Date currentDate) throws DataServiceException;

	boolean checkIfCodeIsValid(String code, Date currentDate) throws DataServiceException;
}
