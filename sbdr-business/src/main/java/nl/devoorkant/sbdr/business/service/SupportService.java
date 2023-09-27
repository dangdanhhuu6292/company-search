package nl.devoorkant.sbdr.business.service;

import nl.devoorkant.sbdr.business.transfer.PageTransfer;
import nl.devoorkant.sbdr.business.transfer.SupportBestandTransfer;
import nl.devoorkant.sbdr.business.transfer.SupportTransfer;
import nl.devoorkant.sbdr.business.wrapper.ErrorService;
import nl.devoorkant.sbdr.data.model.Gebruiker;
import nl.devoorkant.sbdr.data.model.Klant;
import nl.devoorkant.sbdr.data.model.Support;
import nl.devoorkant.sbdr.data.model.SupportBestand;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Interface exposing functionality for Support.
 * <p/>
 * SBDR - Stichting Bedrijfskrediet Data Registratie
 * <p/>
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author Coen Hasselaar
 * @version %I%
 */

public interface SupportService {

	ErrorService archiveAllSupportTicketsOfBedrijf(Integer bedrijfId) throws ServiceException;

	ErrorService assignAllSupportTicketsOfGebruikerToKlant(Integer gebruikerId, Integer bedrijfId) throws ServiceException;
	
	void assignExpiredObjectionsToAdmin() throws ServiceException;

	//Deletes a support ticket and attachments, from the database and from disk
	ErrorService deleteSupportTicketAndBestanden(Integer sId) throws ServiceException;

	//Finds a SupportBestand object based on its unique ID
	SupportBestand findSupportTicketBestandBySupportBestandId(Integer sBId) throws ServiceException;

	//Finds all support tickets about a company
	PageTransfer<SupportTransfer> findSupportTicketsAboutBedrijfByBedrijfId(Integer bId, Pageable p) throws ServiceException;

	//Finds support tickets made by a user and tickets picked up by a user and returns the result as a page
	PageTransfer<SupportTransfer> findSupportTicketsByGebruikerId(Integer gId, Pageable p) throws ServiceException;

	//Finds support tickets by a reference number
	List<SupportTransfer> findSupportTicketsByReferentieNummer(String ref) throws ServiceException;

	//Finds support tickets based on the type and returns the result as a page
	PageTransfer<SupportTransfer> findSupportTicketsBySupportType(String type, Pageable p) throws ServiceException;

	//Assigns a support ticket to a user
	void pickUpSupportTicket(String refNo, Integer gebruikerId) throws ServiceException;

	//Saves a support ticket to the database, and activates or discards a notification if necessary
	SupportTransfer saveSupportTicket(Support s, Boolean activateNotification) throws ServiceException;

	//Saves a file to disk, and adds a reference to it in the database
	SupportBestandTransfer saveSupportTicketBestand(SupportBestand sB, byte[] document) throws ServiceException;
}
