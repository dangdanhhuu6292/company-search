package nl.devoorkant.sbdr.data.service;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.ExactOnlineAccess;

/**
 * Data Service bean with implemented functionality for ExactOnlineAccess.
 * <p/>
 * SBDR - Applicatie voor Bedrijfskrediet Data Registratie
 * <p/>
 *
 * Defines the functionality that the ExactOnlineAccessDataService must implement to support interaction between a Business Object and
 * the ConfiguratieRepository
 *
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author          Martijn Bruinenberg
 * @version         %I%
 */

public interface ExactOnlineAccessDataService {

	ExactOnlineAccess find() throws DataServiceException;

	ExactOnlineAccess save(ExactOnlineAccess exactOnlineAccess)
			throws DataServiceException;

	ExactOnlineAccess reset() throws DataServiceException;

	void delete(ExactOnlineAccess exactOnlineAccess)
			throws DataServiceException;

}
