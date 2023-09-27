package nl.devoorkant.sbdr.business.service;

import nl.devoorkant.sbdr.business.transfer.*;
import nl.devoorkant.sbdr.business.util.EBevoegdheid;
import nl.devoorkant.sbdr.business.util.EBriefStatus;
import nl.devoorkant.sbdr.business.wrapper.ErrorService;
import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.Bedrijf;
import nl.devoorkant.sbdr.data.model.ContactMoment;
import nl.devoorkant.sbdr.data.model.Melding;
import nl.devoorkant.sbdr.data.model.Monitoring;
import nl.devoorkant.sbdr.data.model.Notitie;
import nl.devoorkant.sbdr.data.util.EMeldingStatus;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Interface exposing functionality for Bedrijf.
 * <p/>
 * SBDR - Stichting Bedrijfskrediet Data Registratie
 * <p/>
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author Jan Meekel
 * @version %I%
 */

public interface BedrijfService {
	String KVK_SBDR = "0000000";	
	
	static final int RATINGMESSAGE_INDICATOR = 0;
	static final int RATINGMESSAGE_OPMERKINGLIST = 1;
	
	static final String[] ratingBetalingsachterstandMessage = new String[]{"De kans op wanbetaling is aanwezig", "Er zijn actieve betalingsachterstanden in het register bekend. Hou hier rekening mee bij het verrichten van werkzaamheden."};

	static final NavigableMap<Integer, String[]> ratingScoreMessage = new TreeMap<Integer, String[]>() {{
		put(-1, new String[]{"De kans op wanbetaling is aanwezig", "Er zijn geen actieve betalingsachterstanden in het register bekend. Desalniettemin adviseren wij u toch voorzichtig te zijn bij het verrichten van werkzaamheden voor dit bedrijf."}); // 0..30
		put(31, new String[]{"De kans op wanbetaling is gemiddeld", "Er zijn geen actieve  betalingsachterstanden bij in het register bekend. "}); // 31..45
		put(46, new String[]{"Lage kans op wanbetaling", "Er zijn geen actieve betalingsachterstanden in het register bekend. De kans op wanbetaling is laag."}); // 45..79
		put(80, new String[]{"Zeer Lage kans op wanbetaling", "Er zijn geen actieve  betalingsachterstanden in het register bekend. De kans op wanbetaling is zeer laag."}); // 80..
	}};
	
//	// To do a lookup for some value in 'key'
//	if (key < 0 || key > 100) {
//	    // out of range
//	} else {
//	   return map.floorEntry(key).getValue();
//	}
	
	ErrorService activateMelding(Integer meldingId, Integer gebruikerId) throws ServiceException;

	/**
	 * Activates all Meldingen which are INBEHANDELING and date has passed overdue date
	 *
	 * @return ErrorService on error
	 * @throws ServiceException
	 */
	ErrorService activateOverdueMeldingen() throws ServiceException;

	List<BedrijfTransfer> addBekendBijSbdrProperty(List<BedrijfTransfer> bedrijfTransfers) throws ServiceException;

	List<BedrijfTransfer> addHeeftMeldingProperty(Integer bedrijfId, List<BedrijfTransfer> bedrijfTransfers) throws ServiceException;

	List<BedrijfTransfer> addHeeftMonitoringProperty(Integer bedrijfId, List<BedrijfTransfer> bedrijfTransfers) throws ServiceException;

	List<ExceptionBedrijfOverviewTransfer> addKlantProperty(List<ExceptionBedrijfOverviewTransfer> exceptionCompanies) throws ServiceException;

	List<BedrijfTransfer> addRapportTodayCreated(Integer bedrijfId, List<BedrijfTransfer> bedrijfTransfers) throws ServiceException;

	List<BedrijfTransferNs> addSbdrNummerProperty(List<BedrijfTransferNs> bedrijfTransfers) throws ServiceException;

	AdminOverviewTransfer adminOverviewTransfer() throws ServiceException;

	List<Melding> findAllActiveMeldingenOfBedrijf(Integer bedrijfId) throws ServiceException;

	boolean bedrijfHasMeldingenByGebruikerId(Integer gId) throws ServiceException;

	void blokkeerMelding(Integer meldingId, Integer gebruikerId, boolean onHold) throws ServiceException;

	ErrorService createCustomMelding(Integer gebruikerId, CustomMeldingTransfer customMeldingTransfer) throws ServiceException;

	void createInitialMeldingenLetter(Integer meldingId, Integer gebruikerId) throws ServiceException;

	ErrorService createMeldingBatch(Integer gebruikerId, NotificationsBatchTransfer notificationBatch) throws ServiceException;

	void createMeldingenQuartzJob() throws ServiceException;

	/**
	 * Create a monitoring object of a company the {@link nl.devoorkant.sbdr.data.model.Monitoring} Object describes the content.
	 *
	 * @param monitoring a Monitoring object representing the {@link nl.devoorkant.sbdr.data.model.Monitoring} Object to create
	 * @throws nl.devoorkant.sbdr.business.service.ServiceException as a reaction to all errors thrown by the persistence layer
	 */
	ErrorService createMonitoring(Monitoring monitoring) throws ServiceException;

	void createNewMeldingenLetter(Integer meldingId, Integer gebruikerId, boolean generateForAllMeldingen) throws ServiceException;

	void createOldNotificationAlertsQuartzJob() throws ServiceException;

	/**
	 * Deletes the {@link nl.devoorkant.sbdr.data.model.Bedrijf} Object identified by the presented id.
	 *
	 * @param bedrijf_ID an Integer representing the {@link nl.devoorkant.sbdr.data.model.Bedrijf} Object to delete
	 * @throws nl.devoorkant.sbdr.business.service.ServiceException as a reaction to all errors thrown by the persistence layer
	 */
	void deleteBedrijf(Integer bedrijf_ID) throws ServiceException;

	/**
	 * Fetch website param values
	 *
	 * @return WebsiteparamTransfer which contains all values
	 * @throws ServiceException
	 */
	WebsiteparamTransfer fetchWebsiteparam() throws ServiceException;

	//PageTransfer<AdminAlertTransfer> findActiveAlertsForSbdrAdmin(Integer bedrijfId, boolean includeObjections, Pageable p) throws ServiceException;

	/**
	 * Finds page list of alerts {@link AlertOverviewTransfer} by its company.
	 *
	 * @param bedrijfId the primary key of {@link Bedrijf}
	 * @param userId    the user id of Gebruiker used for not to find own notifications
	 * @param search    String searches on KvKNummer or company name
	 * @param pageable  params for paging
	 * @return PageTransfer {@link PageTransfer} of {@link AlertOverviewTransfer} instance or null in case the record could not be found.
	 */
	PageTransfer<AlertOverviewTransfer> findActiveAlertsOfBedrijf(Integer bedrijfId, Integer userId, String search, Pageable pageable) throws ServiceException;

	PageTransfer<AlertOverviewTransfer> findActiveAlertsNoMonitoringOfBedrijf(Integer bedrijfId, Integer userId, String search, Pageable pageable) throws ServiceException;
	PageTransfer<AlertOverviewTransfer> findActiveMonitoringAlertsOfBedrijf(Integer bedrijfId, Integer userId, String search, Pageable pageable) throws ServiceException;
	PageTransfer<AlertOverviewTransfer> findActiveMonitoringNotificationsOfBedrijf(Integer bedrijfId, Pageable pageable) throws DataServiceException;
	
	PageTransfer<AdminAlertTransfer> findActiveObjectionsForSbdrAdmin(Integer bedrijfId, Pageable p) throws ServiceException;
	PageTransfer<AdminAlertTransfer> findActiveSupportTicketsForSbdrAdmin(Integer bedrijfId, Pageable p) throws ServiceException;
	
	/**
	 * Admin notification lists
	 *
	 * @param pageable         params for paging
	 * @param search           kvk number or company name
	 * @param klantStatusCodes list of customer status codes
	 * @param meldingStatusCodes list of melding status codes
	 * @return a PageTransfer {@link PageTransfer} of (@link MeldingTransfer} instance
	 * @throws ServiceException
	 */
	PageTransfer<MeldingTransfer> findMeldingenOfAllBedrijven(String search, List<String> klantStatusCodes, List<String> meldingStatusCodes, Pageable pageable) throws ServiceException;

	/**
	 * Finds page list of notifications {@link MeldingOverviewTransfer} by its company.
	 *
	 * @param bedrijfId the primary key of {@link Bedrijf}
	 * @param search    String searches on KvKNummer or company name
	 * @param pageable  params for paging
	 * @return a PageTransfer {@link PageTransfer} of {@link MeldingOverviewTransfer} instance or null in case the record could not be found.
	 */
	PageTransfer<MeldingOverviewTransfer> findActiveMeldingenOfBedrijf(Integer bedrijfId, String search, Pageable pageable) throws ServiceException;

	/**
	 * Finds page list of notifications {@link MonitoringOverviewTransfer} by its company.
	 *
	 * @param bedrijfId the primary key of {@link Bedrijf}
	 * @param search    String searches on KvKNummer or company name
	 * @param pageable  params for paging
	 * @return a PageTransfer {@link PageTransfer} of {@link MonitoringOverviewTransfer} instance or null in case the record could not be found.
	 */
	PageTransfer<MonitoringOverviewTransfer> findActiveMonitoringOfBedrijf(Integer bedrijfId, String search, Pageable pageable) throws ServiceException;

	PageTransfer<ExceptionBedrijfOverviewTransfer> findAllExceptionBedrijven(String search, List<String> statusCodes, Pageable pageable) throws ServiceException;

	/**
	 * @param pageable    params for paging
	 * @param search      kvk number or company name
	 * @param statusCodes statusCode list of klant account to search for
	 * @return a PageTransfer {@link PageTransfer} of {@link KlantBedrijfOverviewTransfer} instance
	 * @throws ServiceException
	 */
	PageTransfer<KlantBedrijfOverviewTransfer> findAllKlantBedrijvenOnActiveKlantStatus(String search, List<String> statusCodes, Pageable pageable) throws ServiceException;

	/**
	 * Returns the {@link nl.devoorkant.sbdr.data.model.Bedrijf} Object by klantgebruikerId.<br/>
	 *
	 * @param gebruikerId an Integer representing the {@link nl.devoorkant.sbdr.data.model.Gebruiker} of Bedrijf {@link nl.devoorkant.sbdr.data.model.Bedrijf} Object to retrieve
	 * @return the {@link nl.devoorkant.sbdr.data.model.Bedrijf} Object
	 * @throws ServiceException
	 */
	Bedrijf findByKlantGebruikersId(Integer gebruikerId) throws ServiceException;

	/**
	 * Finds all facturen of a company based on the ID of a company
	 *
	 * @param bId the ID of the company
	 * @return list of facturen of a company
	 * @throws ServiceException
	 */
	PageTransfer<FactuurTransfer> findFacturenOfBedrijf(Integer bId, Pageable p) throws ServiceException;

	/**
	 * Finds all notifications of a company based on the userId of a company
	 *
	 * @param gebruikerId the ID of the user who is connected to a company, whose notifications will be retrieved
	 * @return list of meldingen of a company
	 * @throws ServiceException
	 */
	List<MeldingTransfer> findMeldingenAboutBedrijfByGebruikerId(Integer gebruikerId) throws ServiceException;

	/**
	 * Finds page list of removed company notifications and monitoring {@link RemovedBedrijfOverviewTransfer} by its company.
	 *
	 * @param bedrijfId the primary key of {@link Bedrijf}
	 * @param view      the subselection: 'monitoring' || 'melding' || 'all'
	 * @param search    String searches on KvKNummer or company name
	 * @param pageable  params for paging
	 * @return a PageTransfer {@link PageTransfer} of {@link RemovedBedrijfOverviewTransfer} instance or null in case the record could not be found.
	 */
	PageTransfer<RemovedBedrijfOverviewTransfer> findRemovedBedrijvenOfBedrijf(Integer bedrijfId, String view, String search, Pageable pageable) throws ServiceException;

	PageTransfer<SearchResultsOverviewTransfer> findSearchResults(String search, Boolean vermelder, Pageable pageable) throws ServiceException;

	/**
	 * Returns the {@link nl.devoorkant.sbdr.data.model.Bedrijf} Object by primary key.<br/>
	 *
	 * @param bedrijf_ID an Integer representing the {@link nl.devoorkant.sbdr.data.model.Bedrijf} Object to retrieve
	 * @return the {@link nl.devoorkant.sbdr.data.model.Bedrijf} Object
	 * @throws ServiceException
	 */
	Bedrijf getBedrijf(Integer bedrijf_ID) throws ServiceException;
	
	/**
	 * Find Bedrijf by sbdrNummer
	 * 
	 * @param sbdrNummer
	 * @return
	 * @throws ServiceException
	 */
	Bedrijf findBySbdrNummer(String sbdrNummer) throws ServiceException;

	/**
	 * Get BedrijfTransfer data of a company the bedrijfId references the {@link nl.devoorkant.sbdr.data.model.Bedrijf} Object.
	 *
	 * @param bedrijfId an identifier representing the {@link nl.devoorkant.sbdr.data.model.Bedrijf} Object to get report data from
	 * @throws nl.devoorkant.sbdr.business.service.ServiceException as a reaction to all errors thrown by the persistence layer
	 */
	BedrijfTransfer getBedrijfData(Integer bedrijfId) throws ServiceException;
	
	BedrijfTransferExtra getBedrijfDataExtra(Integer bedrijfId) throws ServiceException;

	/**
	 * Get BedrijfReport data of a company the bedrijfId references the {@link nl.devoorkant.sbdr.data.model.Bedrijf} Object.
	 *
	 * @param bedrijfId an identifier representing the {@link nl.devoorkant.sbdr.data.model.Bedrijf} Object to get report data from
	 * @throws nl.devoorkant.sbdr.business.service.ServiceException as a reaction to all errors thrown by the persistence layer
	 */
	MeldingTransfer getMeldingData(Integer meldingId, Integer doorBedrijfId, Integer bedrijfId, Integer gebruikerId) throws ServiceException;

	/**
	 * Get BedrijfReport data of a company the bedrijfId references the {@link nl.devoorkant.sbdr.data.model.Bedrijf} Object.
	 *
	 * @param bedrijfAanvragerId an identifier representing the {@link nl.devoorkant.sbdr.data.model.Bedrijf} Object which requested the report
	 * @param bedrijfId          an identifier representing the {@link nl.devoorkant.sbdr.data.model.Bedrijf} Object to get report data from
	 * @param referentieNummer   unique reference for specific report
	 * @throws nl.devoorkant.sbdr.business.service.ServiceException as a reaction to all errors thrown by the persistence layer
	 */
	BedrijfReportTransfer getReportData(Integer bedrijfAanvragerId, Integer bedrijfId, String referentieNummer) throws ServiceException, ThirdPartyServiceException;

	EMeldingStatus maxMeldingStatus(String kvkNumber) ;
	 
	boolean hasRightToDo(Integer bedrijfId, EBevoegdheid checkBevoegdheid) throws ServiceException;

	void ignoreException(Integer customMeldingId, Integer bedrijfId) throws ServiceException;

	boolean isBedrijfOfKlant(Integer bedrijfId) throws ServiceException;

	boolean isIbanCheckOk(String ibanNumber) throws ServiceException;

	boolean isViesCheckOk(String vatNumber) throws ServiceException;

	MonitoringDetailsTransfer monitoringDetailTransfer(Integer doorBedrijfId, Integer monitoringId) throws ServiceException;

	/**
	 * Creates or update Rapport counter and returns new rapport count
	 *
	 * @throws ServiceException
	 */
	Integer newRapportCounter(Integer bedrijfId) throws ServiceException;

	ErrorService rejectMelding(Integer meldingId, Integer bedrijfId, Integer gebruikerId) throws ServiceException;

	/**
	 * Removes a notification object of a company the {@link nl.devoorkant.sbdr.data.model.Melding} Object describes the content of the notification.
	 *
	 * @param meldingId    a Melding representing the {@link nl.devoorkant.sbdr.data.model.Melding} Object to remove
	 * @param bedrijfId    identifier of company subject of the Melding object
	 * @param gebruikerId  identifier of gebruiker of making the change gerapporteerd
	 * @param reden        reason of removal (frontend code)
	 * @param sbdrUser     if sbdr user removes notification
	 * @param createAlerts if createAlerts create alerts for company
	 * @throws nl.devoorkant.sbdr.business.service.ServiceException as a reaction to all errors thrown by the persistence layer
	 */
	ErrorService removeMelding(Integer meldingId, Integer bedrijfId, Integer bedrijfIdGerapporteerd, Integer gebruikerId, String reden, boolean sbdrUser, boolean createAlerts) throws ServiceException;

	/**
	 * Removes/invalidates a monitoring object of a company the {@link nl.devoorkant.sbdr.data.model.Monitoring} Object describes the content.
	 *
	 * @param monitoringId identifier of a Monitoring object representing the {@link nl.devoorkant.sbdr.data.model.Monitoring} Object to remove
	 * @param bedrijfId    identifier of company subject of the Monitoring object
	 * @param gebruikerId  identifier of user which removes the Monitoring object
	 * @throws nl.devoorkant.sbdr.business.service.ServiceException as a reaction to all errors thrown by the persistence layer
	 */
	ErrorService removeMonitoring(Integer monitoringId, Integer bedrijfId, Integer gebruikerId) throws ServiceException;

	void removeMonitorsOfBedrijf(Integer bedrijfId) throws ServiceException;

	void resolveException(Integer customMeldingId, Integer bedrijfId) throws ServiceException;

	/**
	 * Saves the presented {@link nl.devoorkant.sbdr.data.model.Bedrijf} Object.
	 *
	 * @param bedrijf the {@link nl.devoorkant.sbdr.data.model.Bedrijf} Object to save
	 * @throws nl.devoorkant.sbdr.business.service.ServiceException as a reaction to all errors thrown by the persistence layer
	 */
	Bedrijf saveBedrijf(Bedrijf bedrijf) throws ServiceException;

	/**
	 * Create a notification object of a company the {@link nl.devoorkant.sbdr.data.model.Melding} Object describes the content of the notification.
	 *
	 * @param melding a Melding representing the {@link nl.devoorkant.sbdr.data.model.Melding} Object to create
	 * @throws nl.devoorkant.sbdr.business.service.ServiceException as a reaction to all errors thrown by the persistence layer
	 */
	Melding saveMelding(Melding melding) throws ServiceException;

	void saveWebsiteparam(WebsiteparamTransfer websiteparam) throws ServiceException;

	void updateMeldingenBriefStatus(List<Melding> meldingen, EBriefStatus status) throws ServiceException;

	ErrorService updateMonitoring(Integer vanBedrijfId, Integer doorBedrijfId, Integer gebruikerId) throws ServiceException;

	/**
	 * Check if referentie already exists for specific company
	 *
	 * @throws ServiceException
	 */
	ErrorService validateMeldingData(MeldingTransfer meldingTransfer) throws ServiceException;
	
	Notitie saveNotitie(Notitie notitie, Integer meldingId) throws ServiceException;
	
	ContactMoment saveContactMomentNotification(ContactMomentTransfer contactMoment, Notitie notitie) throws ServiceException;

	ContactMomentTransfer getContactMomentNotificationData(Integer contactMomentId) throws ServiceException;

	NotitieTransfer getNotitieAdminData(Integer notitieId) throws ServiceException;

	List<ContactMomentTransfer> findContactMomentsOfNotification(Integer meldingId, Integer userId) throws ServiceException;

	ErrorService removeContactMomentNotification(Integer contactMomentId, Integer bedrijfId, Integer userId) throws ServiceException;
}