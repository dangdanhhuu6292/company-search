package nl.devoorkant.sbdr.data.service;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.Monitoring;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MonitoringDataService {
	/**
	 * Get all Monitoring objects of bedrijf.
	 *
	 * @return page collection of Monitoring objects
	 */
	Page<Monitoring> findActiveMonitoringOfBedrijf(Integer BedrijfId, Pageable pageable) throws DataServiceException;

	/**
	 * Returns active monitoring of Bedrijf by Bedrijf
	 *
	 * @param doorBedrijfId
	 * @param vanBedrijfId
	 * @return
	 * @throws DataServiceException
	 */
	Monitoring findActiveMonitoringOfBedrijfByBedrijf(Integer doorBedrijfId, Integer vanBedrijfId) throws DataServiceException;

	/**
	 * Get Melding with the associated primary key
	 *
	 * @param meldingId - melding identifier
	 * @return Melding object
	 */
	Monitoring findById(Integer monitoringId) throws DataServiceException;

	/**
	 * @param referentie
	 * @return
	 * @throws DataServiceException
	 */
	Monitoring findByReferentieNummerIntern(String referentie) throws DataServiceException;

	List<Monitoring> findMonitorsOfBedrijf(Integer bedrId) throws DataServiceException;

	/**
	 * Saves the passed Monitoring Entity.<br/>
	 * <p/>
	 * For this type of entity only updates are allowed. Creation of entities is not allowed.<br/>
	 *
	 * @param melding the {@link Monitoring} object to save.
	 * @return the saved {@link Monitoring} object.
	 * @throws DataServiceException
	 */
	Monitoring save(Monitoring monitoring) throws DataServiceException;
}
