package nl.devoorkant.sbdr.data.service;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.MeldingBatch;
import org.springframework.transaction.annotation.Transactional;

/**
 * Interface exposing data services for MeldingBatch Objects.
 * <p/>
 * SBDR - Applicatie voor Bedrijfskrediet Data Registratie
 * <p/>
 *
 * Defines the functionality that the WachtwoordDataService must implement to support interaction between a Business Object and
 * the WachtwoordRepository
 *
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author          Bas Dekker
 * @version         %I%
 */

public interface MeldingBatchDataService {
    /**
     * Retrieves a MeldingBatch object, based on the passed meldingBatch_ID.
     *
     * @param meldingBatch_ID an Integer representing a MeldingBatch
     * @return              a MeldingBatch object or null when the MeldingBatch object could not be retrieved.
     * @throws DataServiceException
     */
	MeldingBatch findByPrimaryKey(Integer meldingBatch_ID) throws DataServiceException;

    /**
     * Saves the presented {@link nl.devoorkant.sbdr.data.model.MeldingBatch} Object.
     *
     * @param wachtwoord     the {@link nl.devoorkant.sbdr.data.model.MeldingBatch} Object to save
     * @throws DataServiceException
     */
    @Transactional
    MeldingBatch save(MeldingBatch meldingBatch) throws DataServiceException;

    /**
     * Deletes the passed MeldingBatch Object.
     *
     * @param wachtwoord     the MeldingBatch Object to delete
     * @throws DataServiceException
     */
    @Transactional
    void delete(MeldingBatch meldingBatch) throws DataServiceException;
}
