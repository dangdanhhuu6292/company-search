package nl.devoorkant.sbdr.data.service;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.MeldingBatch;
import nl.devoorkant.sbdr.data.repository.MeldingBatchRepository;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

/**
 * Data Service bean with implemented functionality for MeldingBatch.
 * <p/>
 * SBDR - Applicatie voor Bedrijfskrediet Data Registratie
 * <p/>
 * Implements the functionality exposed by the MeldingBatchDataService interface.
 *
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author          Martijn Bruinenberg
 * @version         %I%
 */

@Service("meldingBatchDataService")
@Transactional(readOnly = true, propagation=Propagation.SUPPORTS)
public class MeldingBatchDataServiceImpl implements MeldingBatchDataService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MeldingBatchDataServiceImpl.class);

    @Autowired
    private MeldingBatchRepository meldingBatchRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public MeldingBatch findByPrimaryKey(Integer meldingBatch_ID) throws DataServiceException {

        try {
            if (meldingBatch_ID != null) {
                Optional<MeldingBatch> meldingBatch = meldingBatchRepository.findById(meldingBatch_ID);
                return meldingBatch != null ? meldingBatch.get() : null;
            } else {
                LOGGER.debug("Cannot retrieve MeldingBatch without a key.");
                return null;
            }
        } catch (Exception e) {
            throw new DataServiceException(e.getMessage());
        }
    }

    /**
     * Saves the presented {@link nl.devoorkant.sbdr.data.model.MeldingBatch} Object.
     *
     * @param wachtwoord     the {@link nl.devoorkant.sbdr.data.model.MeldingBatch} Object to save
     * @throws              nl.devoorkant.sbdr.data.DataServiceException when saving of the MeldingBatch Object fails
     */
    @Override
    @Transactional
    public MeldingBatch save(MeldingBatch meldingBatch) throws DataServiceException {
        LOGGER.info("Start.");
        MeldingBatch result = null;

        try {
            result = meldingBatchRepository.save(meldingBatch);
        } catch (Exception e) {
            throw new DataServiceException(e.getMessage());
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void delete(MeldingBatch meldingBatch) throws DataServiceException {
        try {
        	meldingBatchRepository.delete(meldingBatch);
        } catch (Exception e) {
            throw new DataServiceException(e.getMessage());
        }
    }

}
