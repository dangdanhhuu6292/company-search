package nl.devoorkant.sbdr.data.service;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.KortingsCode;
import nl.devoorkant.sbdr.data.repository.KortingsCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import java.util.Date;

@Service("kortingsCodeDataService")
@Transactional(readOnly = true, propagation=Propagation.SUPPORTS)
public class KortingsCodeDataServiceImpl implements KortingsCodeDataService {
	@Autowired
	private KortingsCodeRepository kortingsCodeRepository;

	@Override
	public boolean checkIfCodeIsExpired(String code, Date currentDate) throws DataServiceException {
		if(code != null && currentDate != null) {
			try {
				KortingsCode result = kortingsCodeRepository.checkIfCodeIsExpired(code, currentDate);

				return result != null;
			} catch(Exception e) {
				throw new DataServiceException(e.getMessage());
			}
		} else throw new DataServiceException("Method checkIfCodeIsExpired: one or more parameters are null");
	}

	@Override
	public boolean checkIfCodeIsValid(String code, Date currentDate) throws DataServiceException {
		if(code != null && currentDate != null) {
			try {
				KortingsCode result = kortingsCodeRepository.checkIfCodeIsValid(code, currentDate);

				return result != null;
			} catch(Exception e) {
				throw new DataServiceException(e.getMessage());
			}
		} else throw new DataServiceException("Method checkIfCodeIsValid: one or more parameters are null");
	}
}
