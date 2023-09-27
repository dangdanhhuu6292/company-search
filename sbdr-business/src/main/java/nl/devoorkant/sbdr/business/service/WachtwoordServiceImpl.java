package nl.devoorkant.sbdr.business.service;

import nl.devoorkant.exception.DVKException;
import nl.devoorkant.sbdr.business.job.AssignExpiredObjectionsToAdminJob;
import nl.devoorkant.sbdr.business.job.SendPasswordResetReminderEmailsJob;
import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.Gebruiker;
import nl.devoorkant.sbdr.data.model.Wachtwoord;
import nl.devoorkant.sbdr.data.model.WachtwoordStatus;
import nl.devoorkant.sbdr.data.service.GebruikerDataService;
import nl.devoorkant.sbdr.data.service.WachtwoordDataService;
import nl.devoorkant.sbdr.data.service.WachtwoordStatusDataService;
import nl.devoorkant.security.Encryption;
import nl.devoorkant.util.DateUtil;
import nl.devoorkant.util.StringUtil;
import nl.devoorkant.validation.Result;
import nl.devoorkant.validation.ValidationConstants;
import nl.devoorkant.validation.ValidationObject;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

/**
 * Service bean with functionality for Wachtwoorden.
 * <p/>
 * EDO - Applicatie voor het verwerken van Export Documenten
 * <p/>
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author Jan Meekel
 * @version %I%
 */

@Service("wachtwoordService")
@Transactional(readOnly = true)
public class WachtwoordServiceImpl implements WachtwoordService {
	private static final Logger LOGGER = LoggerFactory.getLogger(WachtwoordService.class);
	private final static String REGEX_DIGIT = "^\\w*[0-9]+\\w*$";
	private final static String REGEX_LENGTH = "^([a-zA-Z0-9]){8,25}$";
	private final static String REGEX_UPPERCASE = "^\\w*[A-Z]+\\w*$";
	
	@Autowired
	Scheduler scheduler;
	
	@Value("${job.cron.send_password_reset_reminder_emails}")
	String cronExpressionSendPasswordResetReminderEmails;
	
	@Autowired
	GebruikerDataService gebruikerDataService;
	@Autowired
	WachtwoordDataService wachtwoordDataService;
	@Autowired
	WachtwoordStatusDataService wachtwoordStatusDataService;
	@Autowired
	EmailService emailService;

	@Value("${daysToPasswordReset}")
	private int daysToPasswordReset;


	@PostConstruct
	public void createSchedule() {        
        JobDetail jobDetail = buildJobDetail();
        Trigger trigger = buildJobTrigger(jobDetail);
        try {
			scheduler.scheduleJob(jobDetail, trigger);
		} catch (SchedulerException e) {
			LOGGER.error("Cannot start wachtwoord schedule SendPasswordResetReminderEmails", e);
		}        
	}
	
	private JobDetail buildJobDetail() {

        return JobBuilder.newJob(SendPasswordResetReminderEmailsJob.class)
                .withIdentity(UUID.randomUUID().toString(), "wachtwoord-jobs")
                .withDescription("Send password reset reminder emails")
                .storeDurably()
                .build();
    }	

    private Trigger buildJobTrigger(JobDetail jobDetail) {
    	return TriggerBuilder.newTrigger()
    			  .forJob(jobDetail)
    			  .withIdentity(jobDetail.getKey().getName(), "companyaccount")
    			  .withSchedule(CronScheduleBuilder.cronSchedule(cronExpressionSendPasswordResetReminderEmails))
    			  .build();    	
    }
    
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Wachtwoord getNewWachtwoord() throws ServiceException {
		Wachtwoord result = new Wachtwoord();

		result.setWachtwoordStatusCode(WachtwoordStatusDataService.WACHTWOORD_STATUS_CODE_INITIEEL);
		result.setDatumLaatsteWijziging(DateUtil.getCurrentSQLDate());

		return result;
	}

	/**
	 * Returns a new initialised Wachtwoord Object.<br/>
	 *
	 * @param pstrUnencryptedWachtwoord a String containing the password to encrypt.
	 * @return the Wachtwoord Object
	 */
	public Wachtwoord getNewWachtwoord(String pstrUnencryptedWachtwoord) throws ServiceException {
		Wachtwoord result = null;

		try {
			result = getNewWachtwoord();
			result.setWachtwoord(Encryption.encrypt(pstrUnencryptedWachtwoord));

		} catch(DVKException e) {
			LOGGER.error("Encrypting the wachtwoord failed.");
			throw new ServiceException(e.getMessage());
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Wachtwoord getWachtwoord(Integer wachtwoord_ID) throws ServiceException {

		try {
			if(wachtwoord_ID != null) {
				return wachtwoordDataService.findByPrimaryKey(wachtwoord_ID);
			} else {
				LOGGER.debug("Cannot retrieve Wachtwoord without a key.");
				return null;
			}

		} catch(Exception e) {
			throw new ServiceException(e.getMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public boolean blockWachtwoord(Integer poWachtwoord_ID) throws ServiceException {
		Result result = new Result();

		try {
			Wachtwoord wachtwoord = getWachtwoord(poWachtwoord_ID);

			if(wachtwoord != null) {
				wachtwoord.setWachtwoordStatusCode(WachtwoordStatusDataService.WACHTWOORD_STATUS_CODE_BLOCKED);
				wachtwoord.setDatumLaatsteWijziging(DateUtil.getCurrentSQLDate());

				result.setResultObject(wachtwoordDataService.save(wachtwoord));
			} else {
				LOGGER.error("Wachtwoord cannot be blocked without passing the Wachtwoord.");
				ValidationObject loValidation = new ValidationObject();
				loValidation.addMessage("Wachtwoord kan niet worden geblokkeerd als het niet wordt meegegeven.", ValidationConstants.MessageType.INVALID);
				result.addValidationObject(loValidation);
			}

		} catch(DataServiceException e) {
			LOGGER.error("Blocking the Wachtwoord failed");
			throw new ServiceException(e.getMessage());
		}

		LOGGER.debug("Result = {}", result.isSuccessful());
		return result.isSuccessful();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Gebruiker releaseWachtwoord(Gebruiker gebruiker) throws ServiceException {

		try {
			if(gebruiker != null) {

				gebruiker.getWachtwoord().setWachtwoordStatusCode(WachtwoordStatusDataService.WACHTWOORD_STATUS_CODE_ACTIVE);
				gebruiker.getWachtwoord().setDatumLaatsteWijziging(DateUtil.getCurrentSQLDate());
			}

		} catch(Exception e) {
			LOGGER.error("Releasing the Wachtwoord failed.");
			throw new ServiceException(e.getMessage());
		}

		return gebruiker;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean changeWachtwoord(Gebruiker gebruiker, String newWachtwoord) throws ServiceException {
		Result result = new Result();

		try {
			ValidationObject loValidation = new ValidationObject();
			if(gebruiker != null && gebruiker.getWachtwoord() != null && newWachtwoord != null) {

				Wachtwoord wachtwoord = gebruiker.getWachtwoord();
				wachtwoord.setWachtwoordStatusCode(WachtwoordStatusDataService.WACHTWOORD_STATUS_CODE_ACTIVE);
				wachtwoord.setWachtwoordOud4(wachtwoord.getWachtwoordOud3());
				wachtwoord.setWachtwoordOud3(wachtwoord.getWachtwoordOud2());
				wachtwoord.setWachtwoordOud2(wachtwoord.getWachtwoordOud1());
				wachtwoord.setWachtwoordOud1(wachtwoord.getWachtwoord());
				wachtwoord.setWachtwoord(newWachtwoord);

				result.addValidationObject(validateWachtwoord(gebruiker));

				if(loValidation.isValid()) {
					result.setResultObject(saveWachtwoord(gebruiker));
				}

			} else {
				LOGGER.error("Not all necessary information is available.");
				loValidation.addMessage("Niet alle noodzakelijke informatie is beschikbaar.", ValidationConstants.MessageType.ERROR);
			}

			result.addValidationObject(loValidation);

		} catch(Exception e) {
			LOGGER.error("Changing the Wachtwoord failed.");
			throw new ServiceException(e.getMessage());
		}

		return result.isSuccessful();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean resetWachtwoord(Gebruiker gebruiker) throws ServiceException {
		Result result = new Result();

		try {
			ValidationObject loValidation = new ValidationObject();
			if(gebruiker != null) {

				gebruiker.getWachtwoord().setWachtwoord(Encryption.encrypt(gebruiker.getGebruikersNaam()));
				gebruiker.getWachtwoord().setWachtwoordStatusCode(WachtwoordStatusDataService.WACHTWOORD_STATUS_CODE_RESET);
				gebruiker.getWachtwoord().setDatumLaatsteWijziging(DateUtil.getCurrentSQLDate());

				result.addValidationObject(validateWachtwoord(gebruiker));

				if(loValidation.isValid()) {
					result.setResultObject(saveWachtwoord(gebruiker));
				}

			} else {
				LOGGER.error("Cannot reset the Wachtwoord when the user is not passed.");
				loValidation.addMessage("Niet alle noodzakelijke informatie is beschikbaar.", ValidationConstants.MessageType.ERROR);
			}

			result.addValidationObject(loValidation);

		} catch(Exception e) {
			LOGGER.error("Resetting the Wachtwoord failed.");
			throw new ServiceException(e.getMessage());
		}

		LOGGER.debug("Result = {}", result.isSuccessful());
		return result.isSuccessful();
	}

	/**
	 * Checks the validity of a Wachtwoord Object, passed as part of an Persoon Object.<br/>
	 * <p/>
	 * A Wachtwoord Object must apply to the following rules:<br/>
	 * <ol>
	 * <li>The Wachtwoord must have a WachtwoordStatus.</li>
	 * <li>The field Wachtwoord must contain a value.</li>
	 * <ul>
	 * <li>The value of Wachtwoord may not be equal to the value of UserName (part of login).</li>
	 * <li>The Wachtwoord must be constructed the right way {@link #validateWachtwoordPattern(String)} zijn.</li>
	 * <li>The Wachtwoord may not be equal to one of the former passwords.</li>
	 * </ul>
	 * </ol>
	 *
	 * @param gebruiker an Persoon Object, also containing the Wachtwoord Object to validate.
	 * @return a {@link nl.devoorkant.validation.ValidationObject} containing the validation results.
	 */
	public ValidationObject validateWachtwoord(Gebruiker gebruiker) {
		ValidationObject loValidation = new ValidationObject();

		try {
			if(gebruiker != null && gebruiker.getWachtwoord() != null) {
				Wachtwoord wachtwoord = gebruiker.getWachtwoord();

				// The Wachtwoord must have a WachtwoordStatus.
				if(wachtwoord.getWachtwoordStatus() == null) {
					loValidation.addMessage(wachtwoord.getWachtwoordStatus(), "The field passwordstate must contain a value.", ValidationConstants.MessageType.INVALID);
					LOGGER.debug("WachtwoordStatus must contain a value.");
				}

				// The field Wachtwoord must contain a value,
				// unless it is an initial password.
				if(!wachtwoord.getWachtwoordStatus().getCode().equalsIgnoreCase(WachtwoordStatusDataService.WACHTWOORD_STATUS_CODE_INITIEEL)) {
					String lstrWachtwoord = wachtwoord.getWachtwoord();
					if(lstrWachtwoord != null && lstrWachtwoord.trim().length() > 0) {

						// The value of Wachtwoord may not be equal to the value of UserName (part of signIn),
						// unless it is a resetted password
						if(StringUtil.isNotEmptyOrNull(gebruiker.getGebruikersNaam())) {

							if(!wachtwoord.getWachtwoordStatus().getCode().equalsIgnoreCase(WachtwoordStatusDataService.WACHTWOORD_STATUS_CODE_RESET)) {

								if(lstrWachtwoord.equals(gebruiker.getGebruikersNaam())) {
									loValidation.addMessage("Wachtwoord is gelijk aan de gebruikersnaam.", ValidationConstants.MessageType.INVALID);
									LOGGER.debug("Wachtwoord is equal to UserName.");
								}
							}

						} else {
							loValidation.addMessage("Geen gebruikersnaam gespecificeerd.", ValidationConstants.MessageType.INVALID);
							LOGGER.debug("There is no Username specified.");
						}

						// The Wachtwoord must be constructed the right way
						if(WachtwoordStatusDataService.WACHTWOORD_STATUS_CODE_INITIEEL.equalsIgnoreCase(wachtwoord.getWachtwoordStatus().getCode())) {
							loValidation.addMessages(validateWachtwoordPattern(lstrWachtwoord).getValidationMessages());
						}

						// The Wachtwoord may not be equal to one of the former passwords.
						String lstrEncryptedWachtwoord = Encryption.encrypt(lstrWachtwoord);
						if(lstrEncryptedWachtwoord.equals(wachtwoord.getWachtwoordOud1()) ||
								lstrEncryptedWachtwoord.equalsIgnoreCase(wachtwoord.getWachtwoordOud2()) ||
								lstrEncryptedWachtwoord.equalsIgnoreCase(wachtwoord.getWachtwoordOud3()) ||
								lstrEncryptedWachtwoord.equalsIgnoreCase(wachtwoord.getWachtwoordOud4())) {
							loValidation.addMessage("Hewt nieuwe Wachtwoord is recentelijk nog gebruikt!", ValidationConstants.MessageType.INVALID);
							LOGGER.debug("Wachtwoord is recently used before.");
						}

					} else {
						loValidation.addMessage("Het wachtwoord is niet gevuld.", ValidationConstants.MessageType.INVALID);
						LOGGER.info("Wachtwoord must contain a value.");
					}
				}

			} else {
				loValidation.addMessage("Wachtwoord kan niet worden gecontroleerd.", ValidationConstants.MessageType.INVALID);
				LOGGER.info("Wachtwoord cannot be validated.");
			}
			LOGGER.info("Number of messages = " + loValidation.getValidationMessages().size());

		} catch(Exception loEx) {
			loValidation.addMessage("Validatie mislukt.", ValidationConstants.MessageType.ERROR);
			LOGGER.error("Validation failed.");
		}

		return loValidation;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public Gebruiker saveWachtwoord(Gebruiker gebruiker) {
		Gebruiker result = null;

		try {
			/** Before saving the Wachtwoord, it must be validated */
			if(validateWachtwoord(gebruiker).isValid()) {
				Wachtwoord wachtwoord = gebruiker.getWachtwoord();
				result = gebruiker;

				//wachtwoord.setWachtwoordStatus(getWachtwoordStatusByCode(EWachtwoordStatusCode.INITIEEL.getCode()));
				//wachtwoord.setWachtwoord(Encryption.encrypt(wachtwoord.getWachtwoord()));

				Wachtwoord savedWachtwoord = wachtwoordDataService.save(wachtwoord);

				result.setWachtwoord(savedWachtwoord);
			}
		} catch(DataServiceException loEx) {
			LOGGER.error("Saving Wachtwoord failed.", loEx);
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public WachtwoordStatus getWachtwoordStatus(String code) throws ServiceException {

		try {
			if(StringUtil.isNotEmptyOrNull(code)) {
				return wachtwoordStatusDataService.findByPrimaryKey(code);
			} else {
				LOGGER.debug("Cannot retrieve WachtwoordStatus without a key.");
				return null;
			}

		} catch(Exception e) {
			throw new ServiceException(e.getMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<WachtwoordStatus> getActiveWachtwoordStatussen() throws ServiceException {

		try {
			return wachtwoordStatusDataService.findByActief(true);

		} catch(Exception e) {
			throw new ServiceException(e.getMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public WachtwoordStatus saveWachtwoordStatus(WachtwoordStatus poWachtwoordStatus) {
		LOGGER.debug("Method saveWachtwoordStatus.");
		WachtwoordStatus result = null;

		try {
			// Before saving the WachtwoordStatus, it must be validated
			result = wachtwoordStatusDataService.save(poWachtwoordStatus);

		} catch(Exception loEx) {
			LOGGER.error("Method saveWachtwoordStatus. Method failed.", loEx);
		}
		return result;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public String generateWachtwoord() {
		StringBuffer wachtwoord = new StringBuffer();
		String[] lstraFirst = new String[]{"A", "B", "C", "D", "E", "F", "H", "K", "M", "N", "P", "Q", "R", "T", "W"};
		String[] lstraSecond = new String[]{"2", "3", "4", "6", "7", "8"};
		String[] lstraThird = new String[]{"a", "b", "c", "d", "e", "f", "h", "k", "m", "n", "p", "q", "r", "t", "w"};

		ArrayList<String> loCharacterList = new ArrayList<String>();
		loCharacterList.addAll(Arrays.asList(lstraFirst));
		loCharacterList.addAll(Arrays.asList(lstraSecond));
		loCharacterList.addAll(Arrays.asList(lstraThird));

		wachtwoord.append(lstraFirst[(int) (Math.random() * (lstraFirst.length - 1))]);
		wachtwoord.append(lstraSecond[(int) (Math.random() * (lstraSecond.length - 1))]);
		wachtwoord.append(lstraThird[(int) (Math.random() * (lstraThird.length - 1))]);
		wachtwoord.append(loCharacterList.get((int) (Math.random() * (loCharacterList.size() - 1))));
		wachtwoord.append(loCharacterList.get((int) (Math.random() * (loCharacterList.size() - 1))));
		wachtwoord.append(loCharacterList.get((int) (Math.random() * (loCharacterList.size() - 1))));
		wachtwoord.append(loCharacterList.get((int) (Math.random() * (loCharacterList.size() - 1))));
		wachtwoord.append(loCharacterList.get((int) (Math.random() * (loCharacterList.size() - 1))));
		wachtwoord.append(loCharacterList.get((int) (Math.random() * (loCharacterList.size() - 1))));
		wachtwoord.append(loCharacterList.get((int) (Math.random() * (loCharacterList.size() - 1))));

		return wachtwoord.toString();
	}

	@Override
	@Transactional
	public void sendPasswordResetReminderEmailsQuartzJob() throws ServiceException {
		try{
			//Find all passwords with status RST(reset)
			List<Wachtwoord> passwordsWithStatusRst = wachtwoordDataService.findByStatusReset();

			//Construct a Date instance of the previous day
			Calendar previousDayCalendar = Calendar.getInstance();
			previousDayCalendar.setTime(new Date());
			previousDayCalendar.add(Calendar.DAY_OF_YEAR, -1);
			Date previousDate = previousDayCalendar.getTime();

			//Construct a Date instance of three days prior
			Calendar resetProcessReset = Calendar.getInstance();
			resetProcessReset.setTime(new Date());
			resetProcessReset.add(Calendar.DAY_OF_YEAR, -daysToPasswordReset);
			Date threeDaysPrior = resetProcessReset.getTime();

			for(Wachtwoord p : passwordsWithStatusRst){
				if(p.getDatumLaatsteWijziging().before(threeDaysPrior)){
					//If the last change was more than 3 days ago, empty the ActivatieCode column and update the DatumLaatsteWijziging column
					p.setDatumLaatsteWijziging(new Date());
					p.setWachtwoordStatusCode(WachtwoordStatusDataService.WACHTWOORD_STATUS_CODE_ACTIVE);
					p.setActivatieCode(null);

					wachtwoordDataService.save(p);
				} else {
					//If less than 3 days ago...
					if(p.getDatumLaatsteWijziging().before(previousDate)){
						//But more than 1 day ago, re-send the email, don't update the DatumLaatsteWijziging column

						//Check if an user exists for the password
						if(p.getGebruikers().isEmpty()){
							LOGGER.error("No users found for password " + p.getWachtwoordId());
							throw new ServiceException("sendPasswordResetReminderEmailsQuartzJob: no users found for password " + p.getWachtwoordId());
						}else {
							//Find the user of the password and send the email
							Gebruiker g = p.getGebruikers().iterator().next();

							//If the forgotPasswordEmailSent field isn't set, set it and send the email
							if(!p.isForgotPasswordEmailSent()){
								emailService.sendPasswordForgottenEmail(g);

								p.setForgotPasswordEmailSent(true);
								wachtwoordDataService.save(p);
							}
						}
					}
				}
			}
		}catch(DataServiceException e){
			LOGGER.error("sendPasswordResetReminderEmailsQuartzJob: " + e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * Get WachtwoordStatus by code.<br/>
	 *
	 * @param code a WachtwoordStatus code.
	 * @return WachtwoordStatus.
	 */
	private WachtwoordStatus getWachtwoordStatusByCode(String code) throws ServiceException {

		if(StringUtil.isNotEmptyOrNull(code)) {
			return getWachtwoordStatus(code);
		} else {
			LOGGER.debug("Cannot retrieve WachtwoordStatus without a key.");
			return null;
		}
	}


	/**
	 * Validate Wachtwoord pattern.<br/>
	 *
	 * @param pstrWachtwoord a String containing the Wachtwoord to validate.
	 * @return a {@link nl.devoorkant.validation.ValidationObject} containing the validation results.
	 */
	private ValidationObject validateWachtwoordPattern(String pstrWachtwoord) {
		LOGGER.info("Method validateWachtwoordPattern. Wachtwoord = {}", pstrWachtwoord);

		ValidationObject loValidation = new ValidationObject();

		if(pstrWachtwoord != null) {
			// Het wachtwoord moet minimaal een hoofdletter bevatten.
			Pattern loUppercase = Pattern.compile(REGEX_UPPERCASE);
			if(!loUppercase.matcher(pstrWachtwoord).matches()) {
				loValidation.addMessage("Het wachtwoord bevat niet het minimale aantal hoofdletters!", ValidationConstants.MessageType.INVALID);
			}

			// Het wachtwoord moet minimaal een cijfer bevatten.
			Pattern loDigit = Pattern.compile(REGEX_DIGIT);
			if(!loDigit.matcher(pstrWachtwoord).matches()) {
				loValidation.addMessage("Het wachtwoord bevat niet het minimale aantal cijfers!", ValidationConstants.MessageType.INVALID);
			}

			// Het wachtwoord moet minimaal 8 posities lang zijn.
			Pattern loLength = Pattern.compile(REGEX_LENGTH);
			if(!loLength.matcher(pstrWachtwoord).matches()) {
				loValidation.addMessage("Het wachtwoord bevat niet het minimale aantal tekens!", ValidationConstants.MessageType.INVALID);
			}
		}
		return loValidation;
	}

}
