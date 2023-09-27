package nl.devoorkant.sbdr.data.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.Alert;
import nl.devoorkant.sbdr.data.model.ContactMoment;
import nl.devoorkant.sbdr.data.model.Monitoring;
import nl.devoorkant.sbdr.data.repository.ContactMomentRepository;

@Service("contactMomentDataService")
@Transactional(readOnly = true)
public class ContactMomentDataServiceImpl implements ContactMomentDataService {
	private static Logger LOGGER = LoggerFactory.getLogger(ContactMomentDataService.class);
	
	@Autowired
	private ContactMomentRepository contactMomentRepository;	
	
	@Override
	public ContactMoment findById(Integer contactMomentId) throws DataServiceException {
		try {
			Optional<ContactMoment> contactMoment = contactMomentRepository.findById(contactMomentId);
			return contactMoment != null ? contactMoment.get() : null;
		} catch(Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}	
	
    @Override
    @Transactional	
	public ContactMoment save(ContactMoment contactMoment) throws DataServiceException {
		ContactMoment result = null;

		if (contactMoment != null) {
			try {
				result = contactMomentRepository.save(contactMoment);
			} catch (DataServiceException e) {
				throw new DataServiceException("Error in ContactMoment database transaction: " + e.getMessage());
			} catch (Exception e) {
				LOGGER.error("Error saving contactMoment: " + e.getMessage());
				throw new DataServiceException("Error saving ContactMoment");
			}

			return result;
		} else throw new DataServiceException("Cannot save ContactMoment as null");

	}
    
    @Override
    public List<Object[]> findAllOfNotification(Integer meldingId) throws DataServiceException {
    	try {
    		return contactMomentRepository.findAllOfNotication(meldingId);
    	} catch(Exception e) {
    		throw new DataServiceException("Cannot find contactMoment of notification");
    	}
    }
    
	@Override
	@Transactional
	public void delete(Integer contactMomentId) throws DataServiceException {
		if (contactMomentId != null) {
			try {
				contactMomentRepository.deleteById(contactMomentId);
			} catch (Exception e) {
				throw new DataServiceException(e.getMessage());
			}
		}
	}    
}
