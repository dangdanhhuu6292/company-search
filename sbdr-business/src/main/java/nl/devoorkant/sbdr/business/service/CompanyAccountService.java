package nl.devoorkant.sbdr.business.service;

import nl.devoorkant.sbdr.business.wrapper.ErrorService;
import nl.devoorkant.sbdr.data.model.Bedrijf;
import nl.devoorkant.sbdr.data.model.CIKvKDossier;
import nl.devoorkant.sbdr.data.model.Klant;

public interface CompanyAccountService {

	/**
	 * @param activatieCode  code in email provided by SBDR
	 * @param gebruikersNaam user name of klant
	 * @return ErrorService on error
	 * @throws ServiceException
	 */
	ErrorService activateKlant(String activatieCode, String gebruikersNaam) throws ServiceException;

	/**
	 * @param activatieCode letter code provided by SBDR
	 * @param gebruikerId   klantgebruiker identifier
	 * @return ErrorService on error
	 * @throws ServiceException
	 */
	ErrorService activateKlantTwoFactorCode(String activatieCode, Integer gebruikerId) throws ServiceException;

	boolean checkIfUserCanPay(Integer gId, Integer bId) throws ServiceException;

	ErrorService createAccount(Klant klant, Bedrijf bedrijf, Boolean adresOk, String opmerkingenAdres) throws ServiceException;

	Bedrijf createBedrijfFromCIKvkDossier(CIKvKDossier kvkDossier, boolean createNewIfNotExists, boolean activateBedrijf) throws ServiceException, ThirdPartyServiceException;

	/**
	 * @param bedrijfId
	 * @return
	 * @throws ServiceException
	 */
	void createNewAccountLetterForKlant(Integer bedrijfId) throws ServiceException;

	/**
	 * Deletes klant pro-active. By SBDR user for example.
	 *
	 * @param klantId identifier of Klant record
	 * @param gebruikerId gebruiker who performs deletion
	 * @return ErrorService on error
	 * @throws ServiceException
	 */
	ErrorService deleteKlant(Integer klantId, Integer gebruikerId) throws ServiceException;

	ErrorService deleteKlantOfBedrijf(Integer bedrijfId, Integer gebruikerId) throws ServiceException;

	/**
	 * Set Klant Status to VERVALLEN  for all Klant records with status REGISTRATIE or PROSPECT
	 * which are not activated within overdue period
	 *
	 * @return ErrorService on error
	 * @throws ServiceException
	 */
	ErrorService deleteOverdueKlanten() throws ServiceException;

	void emailResult(String result);

	Bedrijf findBedrijf(String kvkNumber, String subDossier) throws ServiceException;

	boolean hasBedrijfAccount(String kvkNumber, String subDossier, boolean onlyActive) throws ServiceException;

	boolean isRecaptchaOk(String ipaddress, String response, String challenge) throws ServiceException;

	/**
	 * Send email reminder after few days kicked in by Mule
	 *
	 * @return
	 * @throws ServiceException
	 */
	ErrorService sendEmailReminderNewAccountKlanten() throws ServiceException;

	void setEmailResult(String result);

	ErrorService updateAccountData(Klant klant, Bedrijf b) throws ServiceException;

	/**
	 * @param bedrijf
	 * @return
	 * @throws ServiceException
	 */
	ErrorService updateBedrijfData(Bedrijf bedrijf) throws ServiceException;

	/**
	 * FOr manual update klant/bedrijf data.
	 *
	 * @param klant
	 * @param bedrijf
	 * @param gebruikerId
	 * @return
	 * @throws ServiceException
	 */
	ErrorService updateKlantBedrijfAccountData(Klant klant, Bedrijf bedrijf, Integer gebruikerId) throws ServiceException;

}
