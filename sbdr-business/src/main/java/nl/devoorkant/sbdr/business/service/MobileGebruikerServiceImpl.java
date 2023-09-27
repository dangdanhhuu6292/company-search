package nl.devoorkant.sbdr.business.service;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.Gebruiker;
import nl.devoorkant.sbdr.data.model.MobileGebruiker;
import nl.devoorkant.sbdr.data.service.GebruikerDataService;
import nl.devoorkant.sbdr.data.service.MobileGebruikerDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service("mobileGebruikerService")
@Transactional(readOnly = true)
public class MobileGebruikerServiceImpl implements MobileGebruikerService {

	@Autowired
	private GebruikerDataService gebruikerDataService;
	@Value("${maxMCKPerUser}")
	private int maxMCKPerUser;
	@Autowired
	private MobileGebruikerDataService mobileGebruikerDataService;
	private static Logger LOGGER = LoggerFactory.getLogger(MobileGebruikerService.class);

	@Override
	@Transactional
	public MobileGebruiker addMobileGebruikerRecord(Integer gId, String key) throws ServiceException {
		MobileGebruiker newMG = new MobileGebruiker();

		newMG.setActief(true);
		newMG.setDateCreated(new Date());
		newMG.setGebruikerGebruikerId(gId);
		newMG.setMobileClientKey(key);

		/*
		Alle bestaande MCKs ophalen
		Als het aantal groter is dan het max
			Oudste verwijderen
		 */

		try {
			List<MobileGebruiker> savedMCKs = mobileGebruikerDataService.findByGebruikerId(gId);

			if(savedMCKs.size() >= maxMCKPerUser) {
				MobileGebruiker oldestMCK = null;

				for(MobileGebruiker mG : savedMCKs) {
					if(oldestMCK == null) {
						oldestMCK = mG;
					} else {
						if(mG.getDateCreated().before(oldestMCK.getDateCreated())) {
							oldestMCK = mG;
						}
					}
				}

				mobileGebruikerDataService.delete(oldestMCK);
			}

			return mobileGebruikerDataService.save(newMG);
		} catch(DataServiceException e) {
			LOGGER.error("addMobileGebruikerRecord, error: " + e.getMessage());
			throw new ServiceException(e);
		}
	}

	@Override
	public List<MobileGebruiker> findByGebruikerId(Integer gId) throws ServiceException {
		try {
			return mobileGebruikerDataService.findByGebruikerId(gId);
		} catch(DataServiceException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public MobileGebruiker findByGebruikerIdAndKey(Integer gId, String key) throws ServiceException {
		try {
			return mobileGebruikerDataService.findByGebruikerIdAndKey(gId, key);
		} catch(DataServiceException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public MobileGebruiker findByKey(String key) throws ServiceException {
		try {
			return mobileGebruikerDataService.findByKey(key);
		} catch(DataServiceException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional
	public void removeMobileGebruikerRecord(String key) throws ServiceException {
		try {
			MobileGebruiker mG = mobileGebruikerDataService.findByKey(key);
			if(mG != null) {
				mobileGebruikerDataService.delete(mG);
			}
		} catch(DataServiceException e) {
			LOGGER.error("removeMobileGebruikerRecord, error: " + e.getMessage());
			throw new ServiceException(e);
		}
	}

	@Override
	@Transactional
	public void updateMobileGebruikerRecord(String key, Date updatedDate) throws ServiceException {
		try {
			MobileGebruiker mG = mobileGebruikerDataService.findByKey(key);
			if(mG != null) {
				mG.setDateCreated(updatedDate);
				mobileGebruikerDataService.save(mG);
			}
		} catch(DataServiceException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional
	public void removeAllMobileGebruikerRecordsByBedrijfId(Integer bId) throws ServiceException {
		try {
			List<Gebruiker> gebruikers = gebruikerDataService.findAllGebruikersOfBedrijf(bId);

			if (gebruikers != null) {
				for(Gebruiker g : gebruikers) {
					List<MobileGebruiker> MCKRecords = findByGebruikerId(g.getGebruikerId());
	
					for(MobileGebruiker MCKRecord : MCKRecords) {
						removeMobileGebruikerRecord(MCKRecord.getMobileClientKey());
					}
				}
			}
		} catch(DataServiceException e) {
			throw new ServiceException(e);
		}
	}
}
