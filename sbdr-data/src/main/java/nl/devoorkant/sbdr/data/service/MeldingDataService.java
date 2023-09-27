package nl.devoorkant.sbdr.data.service;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.Melding;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

public interface MeldingDataService {

	/**
	 * Get all Melding objects of bedrijf.
	 *
	 * @return page collection of Melding objects
	 */
	Page<Melding> findActiveMeldingenOfBedrijf(Integer BedrijfId, Pageable pageable) throws DataServiceException;

	/**
	 * Get all Non Active Melding objects of bedrijf.
	 *
	 * @return list collection of Melding objects
	 */
	List<Melding> findAllMeldingenOfBedrijf(Integer BedrijfId) throws DataServiceException;

	List<Melding> findAllFromDate(Date fromDate) throws DataServiceException;
	
	/**
	 * Get Melding with the associated primary key
	 *
	 * @param meldingId - melding identifier
	 *
	 * @return Melding object
	 */
	Melding findById(Integer meldingId) throws DataServiceException;

	/**
	 * @param bedrijfId  bedrijfId which reported the notification
	 * @param referentie
	 *
	 * @return
	 *
	 * @throws DataServiceException
	 */
	List<Melding> findByReferentieNummer(Integer meldingId, Integer bedrijfId, String referentie) throws DataServiceException;

	/**
	 * @param referentie
	 *
	 * @return
	 *
	 * @throws DataServiceException
	 */
	Melding findByReferentieNummerIntern(String referentie) throws DataServiceException;

	/**
	 * Finds all notifications of a company based on the userId of a company
	 *
	 * @param gebruikerId the ID of the user who is connected to a company, whose notifications will be retrieved
	 *
	 * @return list of meldingen of a company
	 *
	 * @throws DataServiceException
	 */
	List<Melding> findBygebruikerIdOfCompany(Integer gebruikerId) throws DataServiceException;

	List<Melding> findMeldingenBetweenCompaniesByMeldingId(Integer meldingId) throws DataServiceException;

	List<Melding> findMeldingenOfDocumentByMeldingId(Integer meldingId) throws DataServiceException;

	List<Melding> findNewMeldingenBetweenDates(Date startDate, Date endDate) throws DataServiceException;

	List<Melding> findNewMeldingenOfBedrijfAboutBedrijf(Integer vanBedrijfId, Integer overBedrijfId) throws DataServiceException;

	/**
	 * Find all Melding records which are INBEHANDELING and entry date is beyond daysoverdue date
	 *
	 * @param daysoverdue
	 *
	 * @return
	 *
	 * @throws DataServiceException
	 */
	List<Melding> findOverdueNewMeldingen(int daysoverdue) throws DataServiceException;

	List<Melding> findOverdueNewMeldingenOfBedrijfAboutBedrijf(Integer bedrijfIdDoor, Integer bedrijfIdOver, int daysOverdue) throws DataServiceException;

	/**
	 * Saves the passed Melding Entity.<br/>
	 * <p/>
	 * For this type of entity only updates are allowed. Creation of entities is not allowed.<br/>
	 *
	 * @param melding the {@link Melding} object to save.
	 *
	 * @return the saved {@link Melding} object.
	 *
	 * @throws DataServiceException
	 */
	Melding save(Melding melding) throws DataServiceException;
}
