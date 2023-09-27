package nl.devoorkant.sbdr.data.service.cir;

import nl.devoorkant.sbdr.cir.data.model.*;
import nl.devoorkant.sbdr.cir.data.repository.CirAdresRepository;
import nl.devoorkant.sbdr.cir.data.repository.CirInsolventieRepository;
import nl.devoorkant.sbdr.cir.data.repository.CirPersoonRepository;
import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.util.StringUtil;
import nl.devoorkant.validation.Result;
import nl.devoorkant.validation.ValidationObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import javax.persistence.*;
import java.util.*;

import static nl.devoorkant.validation.ValidationConstants.MessageType;

/**
 * Stateless service bean with functionality for CIR_Insolventies.
 * <p/>
 * CIR - The module for connecting with the "Centraal Insolventie Register"
 * <p/>
 * Copyright:       2015 De Voorkant B.V.<br/>
 * Company:         De Voorkant B.V.
 *
 * @author Martijn Bruinenberg (mbruinenberg@devoorkant.nl)
 * @version 1.0 (26-03-2015).<br/>
 */

@Service("cirInsolventieDataService")
@Transactional(readOnly = true, propagation=Propagation.SUPPORTS)
public class CirInsolventieDataServiceImpl implements CirInsolventieDataService {

	@Autowired
	private CirAdresDataService cirAdresDataService;

	@Autowired
	private CirAdresRepository cirAdresRepository;

	@Autowired
	private CirCbvDataService cirCbvDataService;

	@Autowired
	private CirHandelsNaamDataService cirHandelsnaamDataService;

	@Autowired
	private CirInsolventieRepository cirInsolventieRepository;

	@Autowired
	private CirPersoonRepository cirPersoonRepository;

	@Autowired
	private CirPublicatieDataService cirPublicatieDataService;

	private static final Logger LOGGER = LoggerFactory.getLogger(CirCbvDataServiceImpl.class);

	/**
	 * Adds the passed CIR_CBVs to the passed CIR_Insolventie Object.<br/>
	 * <p/>
	 * The CIR_Insolventie Object passed as argument must already exist and has to be valid. The passed CIR_CBVs are saved first and than added
	 * to the CIR_Insolventie Object. Finally the CIR_Insolventie Object is persisted.
	 *
	 * @param poCIR_Insolventie the CIR_Insolventie Object to which the CIR_CBV objects are added.
	 * @param poCIR_CBVs        the CIR_CBV Objecten to add.
	 * @return a Result Object, containing the adjusted CIR_Insolventie Object.
	 */
	public Result addCIR_CBVs(CirInsolventie poCIR_Insolventie, Set<CirCbv> poCIR_CBVs) {
		LOGGER.info("Method addCIR_CBVs.");
		Result loResult = new Result();

		try {
			if(poCIR_Insolventie != null && poCIR_Insolventie.getId() != null && validateCIR_Insolventie(poCIR_Insolventie).isValid()) {

				if(poCIR_CBVs != null && poCIR_CBVs.size() > 0) {
					Result loSaveResult;
					Set<CirCbv> loSavedCIR_CBVs = new HashSet<CirCbv>(poCIR_CBVs.size());
					LOGGER.info("Method addCIR_CBVs. The Insolventie is {}", poCIR_Insolventie.toString());

					for(CirCbv loCIR_CBV : poCIR_CBVs) {
						loSaveResult = cirCbvDataService.saveCIR_CBV(loCIR_CBV);
						if(loSaveResult.isSuccessful()) {
							loSavedCIR_CBVs.add((CirCbv) loSaveResult.getResultObject());
						} else {
							throw new DataServiceException("Problem saving CIR_CBV");
						}
					}

					if(loSavedCIR_CBVs.size() > 0) {
						LOGGER.info("Method addCIR_CBVs. Adding them to the CIR_Insolventie.");
						poCIR_Insolventie.setCirCbvs(loSavedCIR_CBVs);

						LOGGER.info("Method addCIR_CBVs. Updating the Insolventie {}", poCIR_Insolventie.toString());
						poCIR_Insolventie = cirInsolventieRepository.save(poCIR_Insolventie);
					}

				} else {
					LOGGER.info("Method addCIR_CBVs. No CIR_CBVs to add were passed.");
				}
			} else {
				loResult.getValidationObject().addMessage("Geen geldige CIR_Insolventie ontvangen.", MessageType.ERROR);
				LOGGER.error("Method validateCIR_Insolventie. No valid CIR_Insolventie received.");
			}

		} catch(DataServiceException loEx) {
			LOGGER.error("Method addCIR_CBVs. Adding the CIR_CBVs failed.", loEx);
			loResult.setResult(false);
		}

		return loResult;
	}

	/**
	 * Adds the passed CIR_HandelsNamen to the passed CIR_Insolventie Object.<br/>
	 * <p/>
	 * The CIR_Insolventie Object passed as argument must already exist and has to be valid. The passed CIR_HandelsNamen are saved first and than added
	 * to the CIR_Insolventie Object. Finally the CIR_Insolventie Object is persisted.
	 *
	 * @param poCIR_Insolventie  the CIR_Insolventie Object to which the CIR_HandelsNaam objects are added.
	 * @param poCIR_HandelsNamen the CIR_HandelsNaam Objecten to add.
	 * @return a Result Object, containing the adjusted CIR_InsolventieObject.
	 */
	public Result addCIR_HandelsNamen(CirInsolventie poCIR_Insolventie, Set<CirHandelsnaam> poCIR_HandelsNamen) {
		LOGGER.debug("Method addCIR_HandelsNamen.");
		Result loResult = new Result();

		try {
			if(poCIR_Insolventie != null && poCIR_Insolventie.getId() != null && validateCIR_Insolventie(poCIR_Insolventie).isValid()) {

				if(poCIR_HandelsNamen != null && poCIR_HandelsNamen.size() > 0) {
					Result loSaveResult;
					Set<CirHandelsnaam> loSavedCIR_HandelsNamen = new HashSet<CirHandelsnaam>(poCIR_HandelsNamen.size());
					LOGGER.info("Method addpoCIR_HandelsNamen. The Insolventie is {}", poCIR_Insolventie.toString());

					for(CirHandelsnaam loCIR_HandelsNaam : poCIR_HandelsNamen) {
						loSaveResult = cirHandelsnaamDataService.saveCIR_HandelsNaam(loCIR_HandelsNaam);
						if(loSaveResult.isSuccessful()) {
							loSavedCIR_HandelsNamen.add((CirHandelsnaam) loSaveResult.getResultObject());
						} else {
							throw new DataServiceException("Problem saving CIR_HandelsNaam");
						}
					}

					if(loSavedCIR_HandelsNamen.size() > 0) {
						LOGGER.info("Method addCIR_HandelsNamen. Adding them to the CIR_Insolventie.");
						poCIR_Insolventie.setCirHandelsnaams(loSavedCIR_HandelsNamen);

						LOGGER.info("Method addCIR_HandelsNamen. Updating the Insolventie {}", poCIR_Insolventie.toString());
						poCIR_Insolventie = cirInsolventieRepository.save(poCIR_Insolventie);
					}

				} else {
					LOGGER.info("Method addCIR_HandelsNamen. No CIR_HandelsNamen to add were passed.");
				}
			} else {
				loResult.getValidationObject().addMessage("Geen geldige CIR_Insolventie ontvangen.", MessageType.ERROR);
				LOGGER.error("Method validateCIR_Insolventie. No valid CIR_Insolventie received.");
			}

		} catch(DataServiceException loEx) {
			LOGGER.error("Method addCIR_HandelsNamen. Adding the CIR_HandelsNamen failed.", loEx);
			loResult.setResult(false);
		}

		return loResult;
	}

	@Override
	public Page<Object[]> findAllFaillissementenLastWeek(Pageable pageable) throws DataServiceException {
		try {
			Calendar now = Calendar.getInstance();
			Date nowdate = now.getTime();
			Calendar fromdate = Calendar.getInstance();
			fromdate.setTime(nowdate);
			fromdate.add(Calendar.DAY_OF_MONTH, -7); // to get 7 days before subtract 7 days

			return cirInsolventieRepository.findAantalNieuweFaillissementen(fromdate.getTime(), pageable);
		} catch(Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	/**
	 * Returns the CIR_Insolventie Object by primary key.<br/>
	 *
	 * @param poID an Integer representing the CIR_Insolventie to retrieve
	 * @return the CIR_Insolventie Object
	 */
	public CirInsolventie getCIR_Insolventie(Integer poID) {

		if(poID != null) {
			Optional<CirInsolventie> cirInsolventie = cirInsolventieRepository.findById(poID);
			return cirInsolventie != null ? cirInsolventie.get() : null;
		} else {
			LOGGER.debug("Method getCIR_Insolventie. Cannot retrieve CIR_Insolventie without a key.");
			return null;
		}
	}

	/**
	 * Returns a CIR_Insolventie Entity based on the passed NummerInsolventie.<br/>
	 *
	 * @param pstrNummerInsolventie a String containing the NummerInsolventie
	 * @return the CIR_Insolventie Entity
	 */
	public CirInsolventie getCIR_InsolventieByNummerInsolventie(String pstrNummerInsolventie) {

		try {
			if(StringUtil.isNotEmptyOrNull(pstrNummerInsolventie)) {
				List<CirInsolventie> results = cirInsolventieRepository.findByNummerInsolventie(pstrNummerInsolventie);

				if(results != null && results.size() == 1) return results.get(0);
				else return null;
			} else {
				LOGGER.debug("Method getCIR_InsolventieByNummerInsolventie. Cannot retrieve a CIR_Insolventie without a NummerInsolventie.");
			}
		} catch(NoResultException loEx) {
			LOGGER.info("Method getCIR_InsolventieByNummerInsolventie. No CIR_Insolventie for this NummerInsolventie.");
		} catch(NonUniqueResultException loEx) {
			LOGGER.error("Method getCIR_InsolventieByNummerInsolventie. More than one CIR_Insolventie for this NummerInsolventie.");
		} catch(Exception loEx) {
			LOGGER.error("Method getCIR_InsolventieByNummerInsolventie. Retrieval of CIR_Insolventie for this NummerInsolventie failed.", loEx);
		}

		return null;
	}

	/**
	 * Returns the CIR_Persoon Entity by primary key.<br/>
	 *
	 * @param poID an Integer representing the CIR_Persoon to retrieve
	 * @return the CIR_Persoon Entity
	 */
	public CirPersoon getCIR_Persoon(Integer poID) {

		if(poID != null) {
			Optional<CirPersoon> cirPersoon = cirPersoonRepository.findById(poID);
			return cirPersoon != null ? cirPersoon.get() : null;
		} else {
			LOGGER.debug("Method getCIR_Persoon. Cannot retrieve CIR_Persoon without a key.");
			return null;
		}
	}

	public long getCountOfFaillissementenDezeWeek() throws DataServiceException {
		try {
			return cirInsolventieRepository.findFaillissementenAfterDate(getDateOfFirstDayOfThisWeek());
		} catch(Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	public long getCountOfFaillissementenDitJaar() throws DataServiceException {
		try {
			return cirInsolventieRepository.findFaillissementenAfterDate(getDateOfFirstDayOfThisYear());
		} catch(Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	public long getCountOfSurseancesDezeWeek() throws DataServiceException {
		try {
			return cirInsolventieRepository.findSurseancesAfterDate(getDateOfFirstDayOfThisWeek());
		} catch(Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	public long getCountOfSurseancesDitJaar() throws DataServiceException {
		try {
			return cirInsolventieRepository.findSurseancesAfterDate(getDateOfFirstDayOfThisYear());
		} catch(Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	/**
	 * Returns a new, initialised CIR_Insolventie Object.<br/>
	 *
	 * @return a new CIR_Insolventie Object
	 */
	public CirInsolventie getNewCIR_Insolventie() {
		return new CirInsolventie();
	}

	/**
	 * Removes all Insolventies in broader sense, consisting of a CIR_Insolventie and the related or connected CIR_Objects, based on the passed object.<br/>
	 *
	 * @return a Result Object, containing the result of the remove action.
	 */
	public Result removeAllCIR_Insolventies() {
		Result loResult = new Result();

		List<CirInsolventie> loCIR_Insolventies = getAllCIR_Insolventies();

		for(CirInsolventie loCIR_Insolventie : loCIR_Insolventies) {
			loResult.addResult(removeCIR_Insolventie(loCIR_Insolventie));
			if(!loResult.isSuccessful()) break;
		}

		return loResult;
	}

	/**
	 * Remove the Insolventie and its underlying CIR_PublicatieGeschiedenis, CIR_HandelsNamen, CIR_Adressen and CIR_CBVs.<br/>
	 *
	 * @param poCIR_Insolventie the CIR_Insolventie to remove.
	 * @return a Result Object, containing the result of the remove action.
	 */
	@Override
	@Transactional(rollbackFor=Exception.class)
	public Result removeCIR_Insolventie(CirInsolventie poCIR_Insolventie) {
		Result loResult = new Result();

		try {
			if(poCIR_Insolventie != null) {

				boolean removalOk = true;
				List<CirAdres> adressen = cirAdresRepository.findByInsolventieId(poCIR_Insolventie.getId());
				if(adressen != null) {
					for(CirAdres cirAdres : adressen) {
						removalOk = cirAdresDataService.removeCIR_Adres(cirAdres.getId());
						if(!removalOk) break;
					}
				}
				if(removalOk) {
					CirPersoon cirPersoon = poCIR_Insolventie.getCirPersoon();

					cirInsolventieRepository.delete(poCIR_Insolventie);

					if(cirPersoon != null) cirPersoonRepository.delete(cirPersoon);
				} else loResult.setResult(false);

				LOGGER.debug("Method removeCIR_Insolventie. The CIR_Insolventie is removed");

			} else {
				LOGGER.debug("Method removeCIR_Insolventie. The CIR_Insolventie to remove is not passed");
				loResult.setResult(false);
			}

		} catch(Exception loEx) {
			LOGGER.error("Method removeCIR_Insolventie. Removing the CIR_Insolventie failed.", loEx);
			loResult.setResult(false);
		}

		return loResult;
	}

	/**
	 * Saves the passed CIR_Insolventie Object.<br/>
	 * <p/>
	 * The CIR_Insolventie Object passed as argument will be validated {@link #validateCIR_Insolventie(nl.devoorkant.insolventie.data.cir.CIR_Insolventie)} first. The validation can have two possible
	 * outcomes:<br/>
	 * <ol>
	 * <li> Valid. When the passed CIR_Insolventie Object does not have an ID, a new client will be created, otherwise the client indicated by the ID will be updated. These
	 * actions can have two possible outcomes as well:<br/>
	 * <ol>
	 * <li> Successful. The saved object is stored as ResultObject in the Result Object.</li>
	 * <li> Not successful. The reasons for the failure are stored as ValidationMessages in the ValidationObject, part of the Result Object.</li>
	 * </ol>
	 * <li> Not valid. The reasons are stored as ValidationMessages in the ValidationObject, part of the Result Object, and the action is aborted.</li>
	 * </ol>
	 *
	 * @param poCIR_Insolventie the CIR_Insolventie to save.
	 * @return a Result Object, containing the result of the save action.
	 */
	@Transactional(rollbackFor=Exception.class)
	public Result saveCIR_Insolventie(CirInsolventie poCIR_Insolventie) {
		LOGGER.info("Method saveCIR_Insolventie.");
		Result loResult = new Result();

		try {
			/** Before saving the CIR_Insolventie, it must be validated */
			loResult = new Result(validateCIR_Insolventie(poCIR_Insolventie));

			if(loResult.isSuccessful()) {
				if(poCIR_Insolventie.getCirPersoon() != null)
					poCIR_Insolventie.setCirPersoon(cirPersoonRepository.save(poCIR_Insolventie.getCirPersoon()));
				if(poCIR_Insolventie.getCirAdreses() != null && poCIR_Insolventie.getCirAdreses().size() > 0) {
					Result loSaveResult;
					Set<CirAdres> loSavedCIR_Adressen = new HashSet<CirAdres>(poCIR_Insolventie.getCirAdreses().size());

					for(CirAdres loCIR_Adres : poCIR_Insolventie.getCirAdreses()) {
						if(loCIR_Adres.getId() == null) {
							loSaveResult = cirAdresDataService.saveCIR_Adres(loCIR_Adres);
							if(loSaveResult.isSuccessful()) {
								loSavedCIR_Adressen.add((CirAdres) loSaveResult.getResultObject());
							} else {
								throw new DataServiceException("Problem saving CIR_Adres");
							}
						} else {
							loSavedCIR_Adressen.add(loCIR_Adres);
						}
					}

					if(loSavedCIR_Adressen.size() > 0) {
						poCIR_Insolventie.setCirAdreses(loSavedCIR_Adressen);
					}
				}

				LOGGER.debug("Method saveCIR_Insolventie. Create {}", poCIR_Insolventie.toString());


				poCIR_Insolventie = cirInsolventieRepository.save(poCIR_Insolventie);
				loResult.setResultObject(poCIR_Insolventie);
			}
		} catch(Exception loEx) {
			LOGGER.error("Method saveCIR_Insolventie. Saving the CIR_Insolventie failed.", loEx);
			loResult.setResult(false);
		}
		return loResult;
	}

	/**
	 * Saves the passed CIR_Persoon Object.<br/>
	 * <p/>
	 * The CIR_Persoon Object passed as argument will be validated {@link #validateCIR_Persoon(nl.devoorkant.insolventie.data.cir.CIR_Persoon)} first. The validation can have two possible
	 * outcomes:<br/>
	 * <ol>
	 * <li> Valid. When the passed CIR_Persoon Object does not have an ID, a new client will be created, otherwise the client indicated by the ID will be updated. These
	 * actions can have two possible outcomes as well:<br/>
	 * <ol>
	 * <li> Successful. The saved object is stored as ResultObject in the Result Object.</li>
	 * <li> Not successful. The reasons for the failure are stored as ValidationMessages in the ValidationObject, part of the Result Object.</li>
	 * </ol>
	 * <li> Not valid. The reasons are stored as ValidationMessages in the ValidationObject, part of the Result Object, and the action is aborted.</li>
	 * </ol>
	 *
	 * @param poCIR_Persoon the CIR_Persoon to save.
	 * @return a Result Object, containing the result of the save action.
	 */
	@Transactional(rollbackFor=Exception.class)
	public Result saveCIR_Persoon(CirPersoon poCIR_Persoon) {
		LOGGER.info("Method saveCIR_Persoon.");
		Result loResult = new Result();

		try {
			/** Before saving the CIR_Persoon, it must be validated */
			loResult = new Result(validateCIR_Persoon(poCIR_Persoon));

			if(loResult.isSuccessful()) {

				LOGGER.info("Method saveCIR_Persoon.");
				poCIR_Persoon = cirPersoonRepository.save(poCIR_Persoon);
				loResult.setResultObject(poCIR_Persoon);
			}
		} catch(Exception loEx) {
			LOGGER.error("Method saveCIR_Persoon. Saving the CIR_Persoon failed.", loEx);
			loResult.setResult(false);
		}
		return loResult;
	}

	/**
	 * Checks the validity of a CIR_Insolventie Object.<br/>
	 * <p/>
	 * A CIR_Insolventie Object must apply to the following rules:<br/>
	 * <ol>
	 * <li> The field NummerInsolventie must contain a value.</li>
	 * <li> The field CodeBehandelendeInstantie must contain a value.</li>
	 * <li> The field NaamBehandelendeInstantie must contain a value.</li>
	 * <li> The field CodeBehandelendeVestiging must contain a value.</li>
	 * <li> The field NaamBehandelendeVestiging must contain a value.</li>
	 * <li> The field IndPreHGKGepubliceerd must contain a value.</li>
	 * <li> The field IndGeheimAdres must contain a value.</li>
	 * <li> There has to be a CIR_Persoon, which must be valid as well.</li>
	 * <li> If there are CIR_Adressen, they have to be valid as well</li>
	 * <li> If there are CIR_HandelsNamen, they have to be valid as well</li>
	 * <li> If there are CIR_CBVs, they have to be valid as well</li>
	 * </ol>
	 *
	 * @param poCIR_Insolventie the CIR_Insolventie that must be validated.
	 * @return a {@link nl.devoorkant.validation.ValidationObject} containing the validation results.
	 */
	public ValidationObject validateCIR_Insolventie(CirInsolventie poCIR_Insolventie) {
		ValidationObject loValidation = new ValidationObject();

		try {
			if(poCIR_Insolventie != null) {

				// 1. The field NummerInsolventie must contain a value.
				if(StringUtil.isEmptyOrNull(poCIR_Insolventie.getNummerInsolventie())) {
					loValidation.addMessage("Insolventienummer moet gevuld zijn.", MessageType.INVALID);
					LOGGER.error("Method validateCIR_Insolventie. NummerInsolventie not set.");
				}

				// 2. The field CodeBehandelendeInstantie must contain a value.
				if(StringUtil.isEmptyOrNull(poCIR_Insolventie.getCodeBehandelendeInstantie())) {
					loValidation.addMessage("Code behandelende instantie moet gevuld zijn.", MessageType.INVALID);
					LOGGER.error("Method validateCIR_Insolventie. CodeBehandelendeInstantie not set.");
				}

				// 3. The field NaamBehandelendeInstantie must contain a value.
				if(StringUtil.isEmptyOrNull(poCIR_Insolventie.getNaamBehandelendeInstantie())) {
					loValidation.addMessage("Naam behandelende instantie moet gevuld zijn.", MessageType.INVALID);
					LOGGER.error("Method validateCIR_Insolventie. NaamBehandelendeInstantie not set.");
				}

				// 4. The field CodeBehandelendeVestiging must contain a value.
				if(StringUtil.isEmptyOrNull(poCIR_Insolventie.getCodeBehandelendeVestiging())) {
					loValidation.addMessage("Code behandelende vestiging moet gevuld zijn.", MessageType.INVALID);
					LOGGER.error("Method validateCIR_Insolventie. CodeBehandelendeVestiging not set.");
				}

				// 5. The field NaamBehandelendeVestiging must contain a value.
				if(StringUtil.isEmptyOrNull(poCIR_Insolventie.getNaamBehandelendeVestiging())) {
					loValidation.addMessage("Naam behandelende vestiging moet gevuld zijn.", MessageType.INVALID);
					LOGGER.error("Method validateCIR_Insolventie. NaamBehandelendeVestiging not set.");
				}

				//                // 6. The field IndPreHGKGepubliceerd must contain a value.
				//                if (poCIR_Insolventie.getIndPreHGKGepubliceerd() == null) {
				//                    loValidation.addMessage("Indicatie pre HGK gepubliceerd moet gezet zijn.", MessageType.INVALID);
				//                    ioLogger.error("Method validateCIR_Insolventie. IndPreHGKGepubliceerd not set.");
				//                }

				//                // 6. The field IndGeheimAdres must contain a value.
				//                if (poCIR_Insolventie.getIndGeheimAdres() == null) {
				//                    loValidation.addMessage("Indicatie geheim adres moet gezet zijn.", MessageType.INVALID);
				//                    ioLogger.error("Method validateCIR_Insolventie. IndGeheimAdres not set");
				//                }

				// 7. There has to be a CIR_Persoon, which must be valid as well.
				if(poCIR_Insolventie.getCirPersoon() != null) {
					loValidation.addMessages(validateCIR_Persoon(poCIR_Insolventie.getCirPersoon()).getValidationMessages());
				} else {
					loValidation.addMessage("Persoon moet gezet zijn.", MessageType.INVALID);
					LOGGER.error("Method validateCIR_Insolventie. CIR_Persoon not set.");
				}

				// 8. If there are CIR_Adressen, they have to be valid as well
				if(poCIR_Insolventie.getCirAdreses() != null) {
					for(CirAdres loCIR_Adres : poCIR_Insolventie.getCirAdreses()) {
						loValidation.addMessages(cirAdresDataService.validateCIR_Adres(loCIR_Adres).getValidationMessages());
					}
				}

				// 9. If there are CIR_HandelsNamen, they have to be valid as well
				if(poCIR_Insolventie.getCirHandelsnaams() != null) {
					for(CirHandelsnaam loCIR_HandelsNaam : poCIR_Insolventie.getCirHandelsnaams()) {
						loValidation.addMessages(cirHandelsnaamDataService.validateCIR_HandelsNaam(loCIR_HandelsNaam).getValidationMessages());
					}
				}

				// 10. If there are CIR_CBVs, they have to be valid as well
				if(poCIR_Insolventie.getCirCbvs() != null) {
					for(CirCbv loCIR_CBV : poCIR_Insolventie.getCirCbvs()) {
						loValidation.addMessages(cirCbvDataService.validateCIR_CBV(loCIR_CBV).getValidationMessages());
					}
				}

			} else {
				loValidation.addMessage("Geen CIR_Insolventie ontvangen.", MessageType.ERROR);
				LOGGER.error("Method validateCIR_Insolventie. No CIR_Insolventie received.");
			}
			LOGGER.info("Method validateCIR_Insolventie. Number of messages = " + loValidation.getValidationMessages().size());

		} catch(Exception loEx) {
			loValidation.addMessage("Validatie mislukt", MessageType.ERROR);
			LOGGER.error("Method validateCIR_Insolventie. Validation failed.");
		}
		return loValidation;
	}

	/**
	 * Returns a CIR_Insolventie Entity.<br/>
	 *
	 * @return a CIR_Insolventie Entity
	 */
	@SuppressWarnings({"unchecked"})
	private List<CirInsolventie> getAllCIR_Insolventies() {

		try {
			return cirInsolventieRepository.findAll();

		} catch(NoResultException loEx) {
			LOGGER.info("Method getCIR_InsolventieByNummerInsolventie. No CIR_Insolventie.");
		} catch(Exception loEx) {
			LOGGER.error("Method getCIR_InsolventieByNummerInsolventie. Retrieval of CIR_Insolventie failed.", loEx);
		}

		return null;
	}

	/////
	// Protected
	/////
	//    /**
	//     * Create a new page handler.<br/>
	//     *
	//     * @param poObject   	an Object containing search criteria.
	//     * @param pnPageSize 	an int containing the Page size.
	//     * @return 				a PageHandler Object.
	//     */
	//    protected PageHandler createPageHandler(Object poObject, int pnPageSize) {
	//        ioLogger.info("Method createPageHandler");
	//
	//        try {
	//			ArrayList<Object> loQueryParams = new ArrayList<Object>();
	//			StringBuilder loWhere = new StringBuilder();
	//			StringBuilder loJoin = new StringBuilder();
	//			StringBuilder loSelectQuery = new StringBuilder();
	//			StringBuilder loCountQuery = new StringBuilder();
	//			String lstrKoppel;
	//			String lstrSelectQuery;
	//			String lstrCountQuery;
	//
	//			if (poObject instanceof CIR_Insolventie) {
	//				CIR_Insolventie loCIR_Insolventie = (CIR_Insolventie) poObject;
	//
	//                // 1. Compose the join
	//                if (loCIR_Insolventie.getCIR_Persoon() != null) loJoin.append(" JOIN ci.CIR_Persoon cp");
	//
	//                // 2. Compose the where
	//                lstrKoppel = " WHERE";
	//
	//                if (loCIR_Insolventie.getCIR_Persoon() != null) {
	//                    loQueryParams.add(loCIR_Insolventie.getCIR_Persoon().getID());
	//
	//                    loWhere.append(lstrKoppel).append(" cp.id = ?").append(loQueryParams.size());
	//                }
	//
	//				loSelectQuery.append("SELECT ci FROM CIR_Insolventie ci");
	//                loSelectQuery.append(loJoin);
	//				loSelectQuery.append(loWhere);
	//				loSelectQuery.append(" ORDER BY ci.tijd");
	//				lstrSelectQuery = loSelectQuery.toString();
	//
	//				loCountQuery.append("SELECT COUNT(ci) FROM CIR_Insolventie ci");
	//                loCountQuery.append(loJoin);
	//				loCountQuery.append(loWhere);
	//				lstrCountQuery = loCountQuery.toString();
	//
	//                ioLogger.info("Method createPageHandler. SelectQuery = {}", lstrSelectQuery);
	//
	//				return new PageHandler(lstrSelectQuery, lstrCountQuery, loQueryParams, pnPageSize);
	//
	//			} else if (poObject instanceof CIR_Persoon) {
	//				CIR_Persoon loCIR_Persoon = (CIR_Persoon) poObject;
	//
	//				// Compose the where
	//				lstrKoppel = " WHERE";
	//				if(loCIR_Persoon.getNaam() != null) {
	//					loQueryParams.add(loCIR_Persoon.getNaam());
	//
	//					// Als er een %-teken in de tekst staat, dan moet er gezocht worden m.b.v. LIKE
	//					if(!loCIR_Persoon.getNaam().contains("%")) {
	//						loWhere.append(lstrKoppel).append(" cp.omschrijving = ?").append(loQueryParams.size());
	//					} else loWhere.append(lstrKoppel).append(" cp.omschrijving LIKE ?").append(loQueryParams.size());
	//				}
	//
	//				loSelectQuery.append("SELECT cp FROM CIR_Persoon cp");
	//				loSelectQuery.append(loWhere);
	//				loSelectQuery.append(" ORDER BY cp.naam");
	//				lstrSelectQuery = loSelectQuery.toString();
	//
	//				loCountQuery.append("SELECT COUNT(cp) FROM CIR_Persoon cp");
	//				loCountQuery.append(loWhere);
	//				lstrCountQuery = loCountQuery.toString();
	//
	//				return new PageHandler(lstrSelectQuery, lstrCountQuery, loQueryParams, pnPageSize);
	//			}
	//
	//		} catch(Exception loEx) {
	//			ioLogger.error("Method createPageHandler. Method failed.", loEx);
	//		}
	//		return null;
	//	}
	//
	//    /**
	//     * Retrieves the requested page.
	//     *
	//     * @param poPageHandler 	an initialized PageHandler Object.
	//     * @param poPageAction  	an Enumeration, indicating the page to create.
	//     * @return 					a PageHandler Object, containing the requested page.
	//     */
	//	protected PageHandler getPage(PageHandler poPageHandler, PageHandler.PageAction poPageAction) {
	//		ArrayList<Object> loQueryParams = poPageHandler.getQueryParams();
	//
	//		Query loQuery = ioEntityManager.createQuery(poPageHandler.getCountQuery());
	//		for(int lnIndex = 0; lnIndex < loQueryParams.size(); lnIndex++) {
	//			loQuery.setParameter(lnIndex + 1, loQueryParams.get(lnIndex));
	//		}
	//		poPageHandler.setNrOfRecordsAvailable(((Long) loQuery.getSingleResult()).intValue());
	//
	//		poPageHandler.setStartPosition(poPageAction);
	//		loQuery = ioEntityManager.createQuery(poPageHandler.getSelectQuery());
	//		for(int lnIndex = 0; lnIndex < loQueryParams.size(); lnIndex++) {
	//			loQuery.setParameter(lnIndex + 1, loQueryParams.get(lnIndex));
	//		}
	//		loQuery.setMaxResults(poPageHandler.getMaxPageSize());
	//		loQuery.setFirstResult(poPageHandler.getStartPosition());
	//		List loList = loQuery.getResultList();
	//		poPageHandler.setPage(loList);
	//
	//		return poPageHandler;
	//	}
	//
	//    /**
	//     * Retrieves the requested page.
	//     *
	//     * @param poPageHandler     Initialized PageHandler object.
	//     * @param pnPageNr 	        an int containing the Page to retrieve.
	//     *
	//     * @return  				PageHandler object with the requested page.
	//     */
	//    protected PageHandler getPage(PageHandler poPageHandler, int pnPageNr) {
	//
	//        ArrayList<Object> loQueryParams = poPageHandler.getQueryParams();
	//
	//        Query loQuery = ioEntityManager.createQuery(poPageHandler.getCountQuery());
	//        for(int lnIndex = 0; lnIndex < loQueryParams.size(); lnIndex++) {
	//            loQuery.setParameter(lnIndex + 1, loQueryParams.get(lnIndex));
	//        }
	//        poPageHandler.setNrOfRecordsAvailable(((Long) loQuery.getSingleResult()).intValue());
	//
	//        poPageHandler.setStartPosition(pnPageNr);
	//        loQuery = ioEntityManager.createQuery(poPageHandler.getSelectQuery());
	//        for(int lnIndex = 0; lnIndex < loQueryParams.size(); lnIndex++) {
	//            loQuery.setParameter(lnIndex + 1, loQueryParams.get(lnIndex));
	//        }
	//        loQuery.setMaxResults(poPageHandler.getMaxPageSize());
	//        loQuery.setFirstResult(poPageHandler.getStartPosition());
	//        List loList = loQuery.getResultList();
	//        poPageHandler.setPage(loList);
	//
	//        return poPageHandler;
	//    }

	private Date getDateOfFirstDayOfThisWeek() {

		Calendar now = Calendar.getInstance();
		Date nowdate = now.getTime();
		Calendar fromdate = Calendar.getInstance();
		fromdate.setTime(nowdate);
		fromdate.add(Calendar.DAY_OF_MONTH, -7);

		return fromdate.getTime();
	}

	private Date getDateOfFirstDayOfThisYear() {
		int thisYear = Calendar.getInstance().get(Calendar.YEAR);

		Calendar cal = Calendar.getInstance();
		cal.set(thisYear, Calendar.JANUARY, 1);

		return cal.getTime();
	}

	/**
	 * Checks the validity of an CIR_Persoon Entity.<br/>
	 * <p/>
	 * An CIR_Persoon Object must apply to the following rules:<br/>
	 * <ol>
	 * <li> The field RechtsPersoonlijkheid must contain a value</li>
	 * </ol>
	 *
	 * @param poCIR_Persoon the CIR_Persoon Entity to validate.
	 * @return a {@link nl.devoorkant.validation.ValidationObject} containing the validation results.
	 */
	private ValidationObject validateCIR_Persoon(CirPersoon poCIR_Persoon) {
		ValidationObject loValidation = new ValidationObject();

		try {
			if(poCIR_Persoon != null) {

				// 1. The field RechtsPersoonlijkheid must contain a value.
				if(StringUtil.isEmptyOrNull(poCIR_Persoon.getRechtsPersoonlijkheid())) {
					loValidation.addMessage("Rechtspersoonlijkheid moet gevuld zijn.", MessageType.INVALID);
					LOGGER.error("Method validateCIR_Insolventie. RechtsPersoonlijkheid not set.");
				}
			} else {
				loValidation.addMessage("Geen CIR_Persoon ontvangen.", MessageType.INVALID);
				LOGGER.error("Method validateCIR_Persoon. No CIR_Persoon received.");
			}
			LOGGER.info("Method validateCIR_Persoon. Number of messages = " + loValidation.getValidationMessages().size());

		} catch(Exception loEx) {
			loValidation.addMessage("Validatie mislukt", MessageType.ERROR);
			LOGGER.error("Method validateCIR_Persoon. Validation failed.");
		}
		return loValidation;
	}
}