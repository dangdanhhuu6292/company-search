package nl.devoorkant.sbdr.data.service;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.MobileGebruiker;
import nl.devoorkant.sbdr.data.repository.MobileGebruikerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service("mobileGebruikerDataService")
@Transactional(readOnly = true)
public class MobileGebruikerDataServiceImpl implements MobileGebruikerDataService {

	@Autowired
	MobileGebruikerRepository mobileGebruikerRepository;

	@Override
	@Transactional
	public void delete(MobileGebruiker mG) throws DataServiceException {
		try {
			mobileGebruikerRepository.delete(mG);
		} catch(Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public List<MobileGebruiker> findByGebruikerId(Integer gId) throws DataServiceException {
		try {
			return mobileGebruikerRepository.findByGebruikerId(gId);
		} catch(Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public MobileGebruiker findByGebruikerIdAndKey(Integer gId, String key) throws DataServiceException {
		try {
			return mobileGebruikerRepository.findByGebruikerIdAndKey(gId, key);
		} catch(Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public MobileGebruiker findByKey(String key) throws DataServiceException {
		try {
			return mobileGebruikerRepository.findByKey(key);
		} catch(Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional
	public MobileGebruiker save(MobileGebruiker mG) throws DataServiceException {
		try {
			return mobileGebruikerRepository.save(mG);
		} catch(Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}
}
