package nl.devoorkant.sbdr.business.service;

import nl.devoorkant.sbdr.data.model.KortingsCode;

import java.util.Date;

public interface KortingsCodeService {

	boolean checkIfCodeIsExpired(String code, Date currentDate) throws ServiceException;

	boolean checkIfCodeIsValid(String code, Date currentDate) throws ServiceException;

	KortingsCode findKortingsCodeOfKlant(Integer gebruikerId, Integer bedrijfId) throws ServiceException;
}
