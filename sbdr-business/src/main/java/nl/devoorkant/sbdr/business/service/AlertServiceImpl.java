package nl.devoorkant.sbdr.business.service;

import nl.devoorkant.sbdr.business.util.ESupportStatus;
import nl.devoorkant.sbdr.business.wrapper.ErrorService;
import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.*;
import nl.devoorkant.sbdr.data.service.*;
import nl.devoorkant.sbdr.data.util.EAlertType;
import nl.devoorkant.sbdr.data.util.EKlantStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service("alertService")
@Transactional(readOnly = true)
public class AlertServiceImpl implements AlertService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AlertServiceImpl.class);

	@Autowired
	AlertDataService alertDataService;

	@Autowired
	BedrijfDataService bedrijfDataService;

	@Autowired
	GebruikerDataService gebruikerDataService;

	@Autowired
	KlantDataService klantDataService;

	@Autowired
	MeldingDataService meldingDataService;

	@Autowired
	SupportDataService supportDataService;

	@Override
	@Transactional
	public ErrorService createMeldingAlert(Integer meldingId, Integer overBedrId, Integer voorBedrId, boolean forUsers, EAlertType aType) throws ServiceException {
		if (meldingId != null && overBedrId != null && voorBedrId != null) {
			try {
				Klant k = klantDataService.findKlantOfBedrijfByBedrijfId(voorBedrId);
				if (k != null && (k.getKlantStatus().getCode().equals(EKlantStatus.ACTIEF.getCode()) || k.getKlantStatus().getCode().equals(EKlantStatus.PROSPECT.getCode()))) {
					if (forUsers) {
						List<Gebruiker> gebrList = gebruikerDataService.findAllGebruikersOfBedrijf(voorBedrId);

						for (Gebruiker g : gebrList) {
							Alert a = new Alert();

							a.setAlertTypeCode(aType.getCode());
							a.setBedrijfByAlertOverBedrijfIdBedrijfId(overBedrId);
							a.setBedrijfByBedrijfIdBedrijfId(voorBedrId);
							a.setDatumAlert(new Date());
							a.setMeldingMeldingId(meldingId);
							a.setGebruikerGebruikerId(g.getGebruikerId());

							alertDataService.save(a);
						}
					} else {
						Alert a = new Alert();

						a.setAlertTypeCode(aType.getCode());
						a.setBedrijfByAlertOverBedrijfIdBedrijfId(overBedrId);
						a.setBedrijfByBedrijfIdBedrijfId(voorBedrId);
						a.setDatumAlert(new Date());
						a.setMeldingMeldingId(meldingId);

						alertDataService.save(a);
					}
				}

				return null;
			} catch (DataServiceException e) {
				throw new ServiceException("Method createMeldingAlert: " + e.getMessage());
			}
		} else return new ErrorService(ErrorService.CANNOT_CREATE_ALERT);
	}

	@Override
	@Transactional
	public ErrorService createMeldingAlert(Melding melding) throws ServiceException {
		if (melding != null) {
			try {
				if (melding.getMeldingId() != null && melding.getBedrijfByMeldingDoorBedrijfIdBedrijfId() != null) {
					// create alert for each gebruiker
					List<Gebruiker> gebruikers = gebruikerDataService.findAllGebruikersOfBedrijf(melding.getBedrijfByMeldingDoorBedrijfIdBedrijfId());
					for (Gebruiker gebruiker : gebruikers) {
						Alert alert = new Alert();

						alert.setAlertTypeCode(EAlertType.MELDING.getCode());
						alert.setBedrijfByAlertOverBedrijfIdBedrijfId(melding.getBedrijfByMeldingOverBedrijfIdBedrijfId());
						alert.setBedrijfByBedrijfIdBedrijfId(melding.getBedrijfByMeldingDoorBedrijfIdBedrijfId());
						alert.setDatumAlert(new Date());
						alert.setMeldingMeldingId(melding.getMeldingId());
						alert.setGebruikerGebruikerId(gebruiker.getGebruikerId());

						alertDataService.save(alert);
					}

				} else {
					// melding not saved
					return new ErrorService(ErrorService.GENERAL_FAILURE);
				}

				return null;
			} catch (DataServiceException e) {
				throw new ServiceException("Cannot saveAlert: " + e.getMessage());
			}
		} else return new ErrorService(ErrorService.GENERAL_FAILURE);
	}

	@Override
	public ErrorService createOverdueNotificationAlert(Bedrijf bedrijf) throws ServiceException {
		if (bedrijf != null) {
			Alert a = new Alert();

			a.setAlertTypeCode(EAlertType.VERLOPEN_MELDING.getCode());
			a.setBedrijfByBedrijfIdBedrijfId(bedrijf.getBedrijfId());
			a.setDatumAlert(new Date());

			try {
				alertDataService.save(a);

				return null;
			} catch (DataServiceException e) {
				throw new ServiceException("Method createOverdueNotificationAlert: " + e.getMessage());
			}
		} else return new ErrorService(ErrorService.CANNOT_CREATE_ALERT);
	}
	
	@Override
	public ErrorService createContactMomentNotificationAlert(Integer bedrijfId) throws ServiceException {
		if (bedrijfId != null) {
			Alert a = new Alert();

			a.setAlertTypeCode(EAlertType.CONTACT_MOMENT.getCode());
			a.setBedrijfByBedrijfIdBedrijfId(bedrijfId);
			a.setDatumAlert(new Date());

			try {
				alertDataService.save(a);

				return null;
			} catch (DataServiceException e) {
				throw new ServiceException("Method createContactMomentNotificationAlert: " + e.getMessage());
			}
		} else return new ErrorService(ErrorService.CANNOT_CREATE_ALERT);
	}	

	@Override
	@Transactional
	public ErrorService createSupportAlert(Support s) throws ServiceException {
		try {
			return createSupportAlert(s, null, null);
		} catch (ServiceException e) {
			throw new ServiceException("Error in createSupportAlert: " + e.getMessage());
		}
	}

	@Override
	@Transactional
	public ErrorService createSupportAlert(Support s, Integer idVoorBedrijf) throws ServiceException {
		try {
			return createSupportAlert(s, idVoorBedrijf, null);
		} catch (Exception e) {
			throw new ServiceException("Error in createSupportAlert: " + e.getMessage());
		}
	}

	@Override
	@Transactional
	public ErrorService assignAllAlertsOfGebruikerToKlant(Integer gebruikerID, Integer bedrijfId) throws ServiceException{
		if(gebruikerID!=null){
			try{
				Gebruiker gebruiker= gebruikerDataService.findById(gebruikerID);
				Klant klant = null;

				if(gebruiker!=null){
					klant = klantDataService.findKlantOfGebruikerByGebruikerId(gebruikerID, bedrijfId);
					if (klant == null)
						klant = klantDataService.findKlantOfBedrijfByBedrijfId(bedrijfId);
				}

				if(klant!=null){
					// MBR 7-12-2017 Find alerts of Gebruiker but only from company bedrijfId
					List<Alert> alertsForGebruiker = alertDataService.findAlertsMeantForGebruiker(gebruikerID, bedrijfId);

					for(Alert a : alertsForGebruiker){
						a.setGebruikerGebruikerId(klant.getGebruikerId());
						alertDataService.save(a);
					}
				} else {
					LOGGER.error("Method assignAllAlertsOfGebruikerToKlant: Klant is null");
					return new ErrorService(ErrorService.GENERAL_FAILURE);
				}

				return null;
			}catch(DataServiceException e) {
				LOGGER.error("Method assignAllAlertsOfGebruikerToKlant: " + e.getMessage());
				throw new ServiceException(e);
			}
		}  else {
			LOGGER.error("Method assignAllAlertsOfGebruikerToKlant: Parameter is null");
			return new ErrorService(ErrorService.GENERAL_FAILURE);
		}
	}

	@Override
	@Transactional
	public ErrorService createSupportAlert(Support s, Integer idVoorBedrijf, Integer idVoorGebr) throws ServiceException {
		if (s != null) {
			LOGGER.debug("createSupportAlert, printing parameters. Support s: " + s.toString() + ", Integer idVoorBedrijf: " + idVoorBedrijf + ", Integer idVoorGebr: " + idVoorGebr);
			try {
				boolean supportExists;
				if (idVoorBedrijf != null) {
					supportExists = alertDataService.findAlertBySupportIdAndBedrijfId(s.getSupportId(), idVoorBedrijf) != null;
				} else {
					supportExists = alertDataService.findAlertBySupportIdAndBedrijfId(s.getSupportId(), s.getBedrijfBedrijfId()) != null;
				}

				if (!supportExists) {
					Alert a = new Alert();

					LOGGER.debug("createSupportAlert, printing variables. s.bedrijfId: " + s.getBedrijfBedrijfId());
					if (idVoorBedrijf != null) a.setBedrijfByBedrijfIdBedrijfId(idVoorBedrijf);
					else a.setBedrijfByBedrijfIdBedrijfId(s.getBedrijfBedrijfId());

					if (idVoorGebr != null) a.setGebruikerGebruikerId(idVoorGebr);

					LOGGER.debug("createSupportAlert, printing variables. s.datumAangemaakt: " + s.getDatumAangemaakt());
					LOGGER.debug("createSupportAlert, printing variables. s.supportId: " + s.getSupportId());

					a.setAlertTypeCode(EAlertType.SUPPORT.getCode());
					a.setDatumAlert(s.getDatumAangemaakt());
					a.setSupportSupportId(s.getSupportId());

					LOGGER.debug("createSupportAlert, printing variables. s.gebruikerId: " + s.getGebruikerByGebruikerIdGebruikerId());

					Integer bId = bedrijfDataService.findByKlantGebruikersId(s.getGebruikerByGebruikerIdGebruikerId()).getBedrijfId();

					if (bId != null) a.setBedrijfByAlertOverBedrijfIdBedrijfId(bId);
					else throw new ServiceException("Error in createSupportAlert: could not determine bedrijfId");

					alertDataService.save(a);
				}

				return null;
			} catch (DataServiceException e) {
				LOGGER.error("createSupportAlert, error msg: " + e.getMessage());
				LOGGER.error("createSupportAlert, local error msg: " + e.getLocalizedMessage());
				LOGGER.error("createSupportAlert, stack trace: " + e.getStackTrace());
				throw new ServiceException("Error in createSupportAlert: " + e.getMessage());
			}
		} else return new ErrorService(ErrorService.GENERAL_FAILURE);
	}

	@Override
	@Transactional
	public ErrorService deleteAlert(Alert alert) throws ServiceException {
		if (alert != null && alert.getAlertId() != null) {
			try {
				if (allowedToDeleteSupportAlert(alert)) alertDataService.delete(alert);

				return null;
			} catch (DataServiceException e) {
				throw new ServiceException("Cannot deleteAlert: " + e.getMessage());
			}
		} else return new ErrorService(ErrorService.CANNOT_FIND_ALERT);
	}

	@Override
	@Transactional
	public ErrorService deleteAlertByAlertId(Integer alertId) throws ServiceException {
		if (alertId != null) {
			try {
				Alert alert = alertDataService.findAlertByAlertId(alertId);

				if (alert != null) return deleteAlert(alert);
				else return new ErrorService(ErrorService.CANNOT_FIND_ALERT);
			} catch (DataServiceException e) {
				throw new ServiceException("Cannot deleteAlert: " + e.getMessage());
			}
		} else return new ErrorService(ErrorService.GENERAL_FAILURE);
	}

	@Override
	@Transactional
	public ErrorService deleteAlertsByBedrijfId(Integer bId) throws ServiceException {
		if (bId != null) {
			try {
				List<Alert> alerts = alertDataService.findAlertsOfBedrijfByBedrijfId(bId);

				for (Alert a : alerts) {
					deleteAlert(a);
				}
			} catch (DataServiceException e) {
				LOGGER.error("deleteAlertsByBedrijfId: " + e.getMessage());
				throw new ServiceException(e);
			}
		} else {
			return new ErrorService(ErrorService.PARAMETER_IS_EMPTY);
		}

		return null;
	}

	@Override
	public Alert findAlertById(Integer alertId) throws ServiceException {
		try {
			return alertDataService.findAlertByAlertId(alertId);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	@Override
	@Transactional
	public ErrorService saveAlert(Alert alert) throws ServiceException {
		if (alert != null) {
			try {
				alertDataService.save(alert);

				return null;
			} catch (DataServiceException e) {
				throw new ServiceException("Cannot saveAlert: " + e.getMessage());
			}
		} else return new ErrorService(ErrorService.GENERAL_FAILURE);
	}

	private boolean allowedToDeleteSupportAlert(Alert a) {
		if (a.getAlertTypeCode().equals(EAlertType.SUPPORT.getCode())) {
			Support s;
			try {
				s = supportDataService.findSupportTicketBySupportId(a.getSupportSupportId());
			} catch (DataServiceException e) {
				LOGGER.error("Method allowedToDeleteSupportAlert: " + e.getMessage());
				return false;
			}

			return (s.getSupportStatusCode().equals(ESupportStatus.GESLOTEN.getCode()) || s.getSupportStatusCode().equals(ESupportStatus.GEARCHIVEERD.getCode()));
		} else return true;
	}
}