package nl.devoorkant.sbdr.data.service;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.Support;
import nl.devoorkant.sbdr.data.model.SupportBestand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Interface exposing data services for Support Objects.
 * <p/>
 * SBDR - Applicatie voor Bedrijfskrediet Data Registratie
 * <p/>
 * <p/>
 * Defines the functionality that the SupportDataService must implement to support interaction between a Business Object and
 * the SupportRepository
 * <p/>
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author Coen Hasselaar
 * @version %I%
 */
public interface SupportDataService {
	//Checks if a supportBestand with the given reference number exists
	Boolean checkIfBestandReferentieNummerExists(String ref) throws DataServiceException;

	//Checks if a reference exists
	Boolean checkIfReferentieNummerExists(String ref) throws DataServiceException;

	//Checks if any supportBestand objects exists with the given support ID
	Boolean checkIfSupportBestandWithSupportIdExists(Integer sId) throws DataServiceException;

	//Checks if a support ticket exists
	Boolean checkIfSupportExists(Integer sId) throws DataServiceException;

	//Deletes a support ticket by its ID
	void deleteSupportTicket(Integer sId) throws DataServiceException;

	//Deletes SupportBestand objecten based on a support ID
	void deleteSupportTicketBestandenBySupportId(Integer sId) throws DataServiceException;

	List<Support> findExpiredObjectionSupportTickets(int eID) throws DataServiceException;

	//Gets the start and end date of a support chain, based on the meldingId of the first support ticket
	List<Object[]> findStartAndEndOfSupportTicketChainByMeldingId(Integer mId) throws DataServiceException;

	//Gets a support file by its ID
	SupportBestand findSupportTicketBestandBySupportBestandId(Integer sBId) throws DataServiceException;

	//Gets a support ticket by its id
	Support findSupportTicketBySupportId(Integer sId) throws DataServiceException;

	// Gets all support tickets of gebruiker
	List<Support> findAllSupportTicketsByGebruikerId(Integer gId) throws DataServiceException;
	
	//Gets all support tickets of a company
	Page<Support> findSupportTicketsAboutBedrijfByBedrijfId(Integer bId, Pageable p) throws DataServiceException;

	//Gets all non-archived support tickets of a company, including tickets of a chain where at least one ticket is made by the given company
	List<Support> findAllSupportTicketsByBedrijfId(Integer bId) throws DataServiceException;
	
	//Gets all support tickets from a user
	Page<Object[]> findSupportTicketsByGebruikerId(Integer gId, Pageable p) throws DataServiceException;

	//Gets all support tickets in a conversation/chain
	List<Support> findSupportTicketsByReferentieNummer(String ref) throws DataServiceException;

	//Gets all support tickets of a type
	Page<Support> findSupportTicketsBySupportType(String type, Pageable p) throws DataServiceException;

	//Saves a support ticket
	Support saveSupportTicket(Support s) throws DataServiceException;

	//Saves a support file
	SupportBestand saveSupportTicketBestand(SupportBestand sb) throws DataServiceException;
}