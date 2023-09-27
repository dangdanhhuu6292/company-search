package nl.devoorkant.sbdr.data.service;

import java.util.List;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.FactuurConfig;
import nl.devoorkant.sbdr.data.repository.FactuurConfigRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

@Service("factuurConfigDataService")
@Transactional(readOnly = true, propagation=Propagation.SUPPORTS)
public class FactuurConfigDataServiceImpl implements FactuurConfigDataService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FactuurConfigDataServiceImpl.class);

    @Autowired
    FactuurConfigRepository factuurConfigRepository;
    
    @Override
    public FactuurConfig findByProductCode(String productCode) throws DataServiceException {
    	try {
    		return factuurConfigRepository.findByProductCode(productCode);
    	} catch (Exception e) {
    		throw new DataServiceException(e.getMessage());
    	}
    }
    
    @Override
    public List<FactuurConfig> findAllActive() throws DataServiceException {
    	try {
    		return factuurConfigRepository.findAllActive();
    	} catch(Exception e) {
    		throw new DataServiceException(e.getMessage());
    	}
    }
    
    @Override
    public List<FactuurConfig> findAllOfFrequency(String freq) throws DataServiceException {
    	try {
    		return factuurConfigRepository.findAllOfFrequency(freq);
    	} catch (Exception e) {
    		throw new DataServiceException(e.getMessage());
    	}
    }
    
    @Override
    @Transactional
    public FactuurConfig save(FactuurConfig factuurConfig) throws DataServiceException {
    	try {
    		return factuurConfigRepository.save(factuurConfig);
    	} catch (Exception e) {
    		throw new DataServiceException(e.getMessage());
    	}
    }
}
