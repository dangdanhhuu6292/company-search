package nl.devoorkant.sbdr.business.service;

import nl.devoorkant.sbdr.business.wrapper.ErrorService;
import nl.devoorkant.sbdr.data.model.Alert;
import nl.devoorkant.sbdr.data.model.Bedrijf;
import nl.devoorkant.sbdr.data.model.Melding;
import nl.devoorkant.sbdr.data.model.Support;
import nl.devoorkant.sbdr.data.util.EAlertType;

public interface AlertService {

	ErrorService createMeldingAlert(Integer meldingId, Integer overBedrId, Integer voorBedrId, boolean forUsers, EAlertType aType) throws ServiceException;

	ErrorService createMeldingAlert(Melding melding) throws ServiceException;

	ErrorService createOverdueNotificationAlert(Bedrijf bedrijf) throws ServiceException;

	ErrorService createSupportAlert(Support s) throws ServiceException;

	ErrorService createSupportAlert(Support s, Integer bVBId) throws ServiceException;
	
	ErrorService createContactMomentNotificationAlert(Integer bedrijfId) throws ServiceException;

	ErrorService assignAllAlertsOfGebruikerToKlant(Integer gebruikerID, Integer bedrijfId) throws ServiceException;

	ErrorService createSupportAlert(Support s, Integer bVBId, Integer bVGId) throws ServiceException;

	ErrorService deleteAlert(Alert alert) throws ServiceException;

	ErrorService deleteAlertByAlertId(Integer alertId) throws ServiceException;

	ErrorService deleteAlertsByBedrijfId(Integer bId) throws ServiceException;

	//Finds an alert by its ID
	Alert findAlertById(Integer alertId) throws ServiceException;

	//Saves an alert
	ErrorService saveAlert(Alert alert) throws ServiceException;

}
