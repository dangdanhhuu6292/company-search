package nl.devoorkant.sbdr.business.service;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.Klant;
import nl.devoorkant.sbdr.data.model.KortingsCode;
import nl.devoorkant.sbdr.data.service.KlantDataService;
import nl.devoorkant.sbdr.data.service.KortingsCodeDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service("kortingsCodeService")
@Transactional(readOnly = true)
public class KortingsCodeServiceImpl implements KortingsCodeService {

	@Autowired
	private KortingsCodeDataService kortingsCodeDataService;

	@Autowired
	private KlantDataService klantDataService;

	@Override
	public boolean checkIfCodeIsExpired(String code, Date currentDate) throws ServiceException {
		if(code!=null && currentDate!=null){
			try{
				return kortingsCodeDataService.checkIfCodeIsExpired(code, currentDate);
			} catch(DataServiceException e){
				throw new ServiceException("Method checkIfCodeIsExpired: " + e.getMessage());
			}
		} else throw new ServiceException("Method checkIfCodeIsExpired: one or more parameters are null");
	}

	@Override
	public boolean checkIfCodeIsValid(String code, Date currentDate) throws ServiceException {
		if(code!=null && currentDate!=null){
			try{
				return kortingsCodeDataService.checkIfCodeIsValid(code, currentDate);
			} catch(DataServiceException e){
				throw new ServiceException("Method checkIfCodeIsValid: " + e.getMessage());
			}
		} else throw new ServiceException("Method checkIfCodeIsValid: one or more parameters are null");
	}

	@Override
	public KortingsCode findKortingsCodeOfKlant(Integer gebruikerId, Integer bedrijfId) throws ServiceException{
		if(gebruikerId!=null){
			try{
				Klant k = klantDataService.findKlantOfGebruikerByGebruikerId(gebruikerId, bedrijfId);
				return k.getKortingsCode();
			} catch(DataServiceException e){
				throw new ServiceException("Method findKortingsCodeOfKlant: " + e.getMessage());
			}
		} else throw new ServiceException("Method findKortingsCodeOfKlant: one or more parameters are null");
	}
}
