package nl.devoorkant.sbdr.data.service;

import nl.devoorkant.sbdr.data.DataLast24h;
import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.DataStatusAantal;
import nl.devoorkant.sbdr.data.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

/**
 * Interface exposing data services for Bedrijf Objects.
 * <p/>
 * SBDR - Applicatie voor Bedrijfskrediet Data Registratie
 * <p/>
 * <p/>
 * Defines the functionality that the GebruikerDataService must implement to support interaction between a Business Object and
 * the GebruikerRepository
 * <p/>
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author Bas Dekker
 * @version %I%
 */

public interface BedrijfDataService {

	boolean bedrijfHasAlerts(Integer bedrijfId) throws DataServiceException;

	boolean bedrijfOnlyHasBedrijfLevelAlerts(Integer bedrijfId) throws DataServiceException;

	/**
	 * Remove the associated Bedrijf
	 * record from the data repository.
	 *
	 * @param bedrijf bedrijf to remove
	 */
	void delete(Bedrijf bedrijf) throws DataServiceException;

	List<DataStatusAantal> findAantalKlantenPerActiveKlantStatus() throws DataServiceException;

	List<DataStatusAantal> findAantalMeldingenPerStatus() throws DataServiceException;

	List<DataStatusAantal> findAantalMonitoringPerStatus() throws DataServiceException;

	Long findAantalNieuweMonitoring() throws DataServiceException;

	Long findAantalNieuweRapporten() throws DataServiceException;

	Long findAantalRapporten() throws DataServiceException;

	//Page<Object[]> findActiveAlertsForSbdrAdmin(Integer bId, boolean includeObjections, Pageable p) throws DataServiceException;

	Page<Object[]> findActiveAlertsNoMonitoringOfBedrijf(Integer bedrijfId, Integer userId, String search, Pageable pageable) throws DataServiceException;
	
	Page<Object[]> findActiveAlertsOfBedrijf(Integer bedrijfId, Integer userId, String search, Pageable pageable) throws DataServiceException;
	
	Page<Melding> findMeldingenOfAllBedrijven(String search, List<String> klantStatusCodes, List<String> meldingStatusCodes, Pageable pageable) throws DataServiceException;

	Page<Melding> findActiveMeldingenOfBedrijf(Integer bedrijfId, String search, Pageable pageable) throws DataServiceException;

	Page<Object[]> findActiveMonitoringAlertsOfBedrijf(Integer bedrijfId, Integer userId, String search, Pageable pageable) throws DataServiceException;

	Page<Object[]> findActiveMonitoringNotificationsOfBedrijf(Integer bedrijfId, Pageable pageable) throws DataServiceException;
	
	Page<Object[]> findActiveMonitoringOfBedrijf(Integer bedrijfId, String search, Pageable pageable) throws DataServiceException;

	Page<Object[]> findActiveObjectionsForSbdrAdmin(Integer bId, Pageable p) throws DataServiceException;

	Page<Object[]> findActiveSupportTicketsForSbdrAdmin(Integer bId, Pageable p) throws DataServiceException;

	List<DataLast24h> findAllActiveKlantenLast24h() throws DataServiceException;

	List<DataLast24h> findAllActiveMeldingenLast24h() throws DataServiceException;

	List<DataLast24h> findAllActiveMonitoringLast24h() throws DataServiceException;

	List<Bedrijf> findAllBedrijvenWithNewMeldingenOfDay(Date date) throws DataServiceException;

	Page<Object[]> findAllExceptionBedrijven(String search, List<String> statusCodes, Pageable pageable) throws DataServiceException;

	Page<Object[]> findAllKlantBedrijvenOnKlantStatus(boolean klantStatus, String search, List<String> statusCodes, Pageable pageable) throws DataServiceException;

	List<Bedrijf> findAllNonRecentBedrijven(Date datum) throws DataServiceException;

	List<DataLast24h> findAllRapportenLast24h() throws DataServiceException;

	Klant findAnyKlantRecordOfBedrijf(Integer bedrijfId) throws DataServiceException;

	List<Bedrijf> findBedrijvenMetMonitoringOpBedrijfByBedrijfId(Integer bedrijfId) throws DataServiceException;

	List<Bedrijf> findBestaandeBedrijvenFromBedrijvenList(List bedrijvenIds) throws DataServiceException;

	Bedrijf findByBedrijfId(Integer bedrijfId) throws DataServiceException;

	Sbi findByCode(String code) throws DataServiceException;

	Bedrijf findByKlantGebruikersId(Integer gebruikerId) throws DataServiceException;

	/**
	 * Get BedrijfData with the associated kvk number
	 *
	 * @param kvkNumber  nummer - kvkNumber
	 * @param subDossier dossier - subDossier
	 *
	 * @return Bedrijf object
	 */
	Bedrijf findByKvkNummer(String kvkNumber, String subDossier) throws DataServiceException;

	/**
	 * Get BedrijfData with the associated kvk numbers
	 *
	 * @param kvkNumbers nummers - kvkNumbers
	 *
	 * @return List of Bedrijf objects
	 */
	List<Bedrijf> findByKvkNummers(List<String> kvkNumbers) throws DataServiceException;

	Rechtsvorm findByRvCode(String code) throws DataServiceException;

	Rechtsvorm findByRvOmschrijving(String omschrijving) throws DataServiceException;

	Bedrijf findBySbdrNummer(String sbdrNummer) throws DataServiceException;

	Page<Factuur> findFacturenOfBedrijf(Integer bId, Pageable p) throws DataServiceException;

	Klant findKlantOfBedrijf(Integer bedrijfId) throws DataServiceException;

	List<Klant> findKlantOfBedrijfFromBedrijvenList(List bedrijvenIds) throws DataServiceException;

	List<Bedrijf> findMatchingBedrijvenMetMeldingByBedrijfFromBedrijvenList(Integer bedrijfId, List bedrijvenIds) throws DataServiceException;

	List<Bedrijf> findMatchingBedrijvenMetMonitoringOfBedrijfFromBedrijvenList(Integer bedrijfId, List bedrijvenIds) throws DataServiceException;

	List<Bedrijf> findMatchingBedrijvenMetRapportCreatedTodayFromBedrijvenList(Integer bedrijfId, List bedrijvenIds) throws DataServiceException;

	List<Melding> findMeldingenOfBedrijf(Integer bedrijfId, List<String> statusCodes) throws DataServiceException;

	// all monitoring records of specific company by others
	List<Monitoring> findMonitoringOfBedrijf(Integer bedrijfId) throws DataServiceException;

	Page<Object[]> findRemovedBedrijvenOfBedrijf(Integer bedrijfId, String view, String search, Pageable pageable) throws DataServiceException;

	/**
	 * find SBDR as bedrijf object
	 */
	Bedrijf findSbdr() throws DataServiceException;

	String findSbiOmschrijvingOfBedrijf(Integer bedrijfId) throws DataServiceException;

	Page<Object[]> findSearchResults(String search, Boolean vermelder, Pageable pageable) throws DataServiceException;

	List<Bedrijf> findVermeldeBedrijvenOfBedrijfWithOverdueMeldingen(Integer bedrijfId, int overdueDays) throws DataServiceException;

	List<Bedrijf> findnewNotifiedCompaniesOfCompany(Integer bedrijfId) throws DataServiceException;

	/**
	 * Save the state of the provided
	 * Bedrijf object into the data
	 * repository.
	 *
	 * @param bedrijf bedrijf to save
	 *
	 * @return Bedrijf
	 */
	Bedrijf save(Bedrijf bedrijf) throws DataServiceException;

}
