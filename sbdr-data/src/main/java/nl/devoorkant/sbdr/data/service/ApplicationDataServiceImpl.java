package nl.devoorkant.sbdr.data.service;

import nl.devoorkant.sbdr.data.DataServiceException;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

@Service("applicationDataService")
@Transactional(readOnly = true, propagation=Propagation.SUPPORTS)
public class ApplicationDataServiceImpl implements ApplicationDataService {
	private static Logger LOGGER = LoggerFactory.getLogger(BedrijfDataService.class);
		
	@Autowired
	@Qualifier("cirApplicatieRepository")
	private nl.devoorkant.sbdr.cir.data.repository.CirApplicatieRepository applicationRepository;
	
	@Autowired
	@Qualifier("applicatieRepository")
	private nl.devoorkant.sbdr.data.repository.ApplicatieRepository applicationRepository2;
		
	@Override
	public nl.devoorkant.sbdr.cir.data.model.Applicatie findByApplicationId(Integer applicationId) throws DataServiceException {
		try {
			nl.devoorkant.sbdr.cir.data.model.Applicatie result = applicationRepository.getOne(applicationId);

			return result;
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}
	
	@Override
	public nl.devoorkant.sbdr.data.model.Applicatie findByApplicationId2(Integer applicationId) throws DataServiceException {
		try {
			Optional<nl.devoorkant.sbdr.data.model.Applicatie> result = applicationRepository2.findById(applicationId);

			return result != null ? result.get() : null;
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}	
}
