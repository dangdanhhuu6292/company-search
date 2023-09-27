package nl.devoorkant.sbdr.data.service;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.ExactOnlineAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;

import java.util.Date;
import java.util.List;

/**
 * Data Service bean with implemented functionality for Configuratie.
 * <p/>
 * SBDR - Applicatie voor Bedrijfskrediet Data Registratie
 * <p/>
 *
 * Defines the functionality that the ConfiguratieDataService must implement to support interaction between a Business Object and
 * the ConfiguratieRepository
 *
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author          Bas Dekker
 * @version         %I%
 */

@Service("exactOnlineDataService")
@Transactional(readOnly = true, propagation=Propagation.SUPPORTS)
public class ExactOnlineAccessDataServiceImpl implements ExactOnlineAccessDataService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExactOnlineAccessDataServiceImpl.class);

    @Autowired
    private nl.devoorkant.sbdr.data.repository.ExactOnlineAccessRepository exactOnlineAccessRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public ExactOnlineAccess find() throws DataServiceException {

        try {
        	List<ExactOnlineAccess> exactOnlineAccess = exactOnlineAccessRepository.findAll();
        	
        	if (exactOnlineAccess != null && exactOnlineAccess.size() > 0)
        		return exactOnlineAccess.get(0);
        	else
        		return null;
        } catch (Exception e) {
            throw new DataServiceException(e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional()
    public ExactOnlineAccess save(ExactOnlineAccess exactOnlineAccess) throws DataServiceException {
        ExactOnlineAccess result = null;

        try {
        	if (exactOnlineAccess.getDatumAangemaakt() == null)
        		exactOnlineAccess.setDatumAangemaakt(new Date());
        	else
        		exactOnlineAccess.setDatumGewijzigd(new Date());
            result = exactOnlineAccessRepository.save(exactOnlineAccess);

            return result;
        } catch (Exception e) {
            throw new DataServiceException(e.getMessage());
        }

    }
    
    @Override
    @Transactional
    public ExactOnlineAccess reset() throws DataServiceException {
    	ExactOnlineAccess result = null;
    	try {
    		ExactOnlineAccess exactOnlineAccess = find();
    		
    		if (exactOnlineAccess != null) {
    			LOGGER.info("ExactOnlineAccess will be reset.");
    			exactOnlineAccess.setAccessToken(null);
    			exactOnlineAccess.setRefreshToken(null);
    			exactOnlineAccess.setDatumAccessTokenExpire(null);    			
    			exactOnlineAccess.setDatumGewijzigd(new Date());
    			
    			result = save(exactOnlineAccess);
    		}
    		
    		return result;
    		
    	} catch (Exception e) {
    		throw new DataServiceException(e.getMessage());
    	}
    }
    
    /**
     * Removes the ExactOnlineAccess Object.<br/>
     * <p/>
     * @param exactOnlineAccess object 		an object to remove.
     */
    @Override
    @Transactional
    public void delete(ExactOnlineAccess exactOnlineAccess) throws DataServiceException{
    	try {
	        if (exactOnlineAccess != null) {
	        	exactOnlineAccessRepository.delete(exactOnlineAccess);
	        } else {
	            LOGGER.error("Cannot remove ExactOnlineAccess record: null object");
	        }
    	} catch (Exception e) {
    		throw new DataServiceException("Cannot remove ExactOnlineAccess record: " + e.getMessage());
    	}
    }    

}
