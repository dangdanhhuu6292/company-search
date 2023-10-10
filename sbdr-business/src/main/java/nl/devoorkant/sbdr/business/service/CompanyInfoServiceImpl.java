package nl.devoorkant.sbdr.business.service;

//import nl.devoorkant.companyinfo.CICommunicationException;
//import nl.devoorkant.companyinfo.CISOAPFaultException;
//import nl.devoorkant.companyinfo.client.WebServiceClient;
//import nl.devoorkant.companyinfo.converter.CIConverter;

import nl.devoorkant.creditsafe.CSCommunicationException;
import nl.devoorkant.creditsafe.CSSOAPFaultException;
import nl.devoorkant.creditsafe.client.WebServiceClient;
import nl.devoorkant.creditsafe.converter.CSConverter;
import nl.devoorkant.exception.DVKException;
import nl.devoorkant.kvk.client.RestKVK;
import nl.devoorkant.kvk.client.impl.RestKVKImpl;
import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.*;
import nl.devoorkant.sbdr.data.service.BedrijfDataService;
import nl.devoorkant.sbdr.data.service.CompanyInfoDataService;
import nl.devoorkant.sbdr.data.util.SearchUtil;
import nl.devoorkant.util.DateUtil;
import nl.devoorkant.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.JAXBException;
import java.util.*;

/**
 * Stateless service bean with functionality for Companies.
 * <p/>
 * SBDR - Stichting Bedrijfskrediet Data Registratie
 * <p/>
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author Jan Meekel
 * @version %I%
 */

@Service("companyInfoService")
@Transactional(readOnly = true)
public class CompanyInfoServiceImpl implements CompanyInfoService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConfiguratieService.class);
	RestKVK restKVK = new RestKVKImpl();
	@Autowired
	BedrijfDataService bedrijfDataService;
	@Autowired
	CompanyInfoDataService companyInfoDataService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<CompanyInfo> retrieveFromCompanyInfo(String searchValue, String plaats, int maxResults) throws ServiceException, ThirdPartyServiceException {
		LOGGER.debug("Method retrieveFromCompanyInfo.");
		List<CompanyInfo> result = null;

		try {
			/**
			 *	is leeg?
			 *	nee:
			 *		is een nummer?
			 *		ja:
			 *			is een geldig nummer?
			 *			ja:
			 *
			 *		nee:
			 *
			 *	ja:
			 *
			 */
			if(StringUtil.isNotEmptyOrNull(searchValue)) {
				if(SearchUtil.isKvKNumber(searchValue)) {
					//if(SearchUtil.isFullKvKNumber(searchValue)) {
					//	WebServiceClient client = new WebServiceClient();
					//	result = new ArrayList<>();
					//	CompanyInfo ciresult = client.getDossierWithFullNumber(searchValue);
					//	if (ciresult != null)
					//		result.add(ciresult);
					//} else {
						//WebServiceClient webServiceClient = new WebServiceClient();
						//result = webServiceClient.searchByKvKNummer(searchValue, maxResults);
						result = restKVK.searchCompany(searchValue,true,false);
						
					//}
				} else {
					//This is to catch incomplete KvK numbers
					//if(!SearchUtil.onlyHasNumbers(searchValue)) {
					//	WebServiceClient webServiceClient = new WebServiceClient();
					//	result = webServiceClient.searchByBedrijfsNaam(searchValue, maxResults);
					//}
					result = restKVK.searchCompany(searchValue,false,true);
				}
			} else {
				LOGGER.debug("Method retrieveFromCompanyInfo. Kan geen Companies ophalen als er geen zoekcriteria zijn opgegeven.");
			}
		} catch(Exception e) {
			throw new ServiceException(e);
		}
		//} catch(JAXBException e) {
		//	throw new ServiceException(e);
		//} catch(CSCommunicationException e) {
		//	throw new ThirdPartyServiceException(e);
		//} catch(CSSOAPFaultException e) {
		//	throw new ServiceException(e);
		//} catch (DVKException e) {
		//	throw new ServiceException(e);
		//}

		return result;
	}

	@Override
	public CompanyInfo retrieveHqFromCompanyInfo(String kvknummer) throws ServiceException, ThirdPartyServiceException {
		LOGGER.debug("Method retrieveHqFromCompanyInfo.");
		List<CompanyInfo> companylist = null;
		CompanyInfo result = null;

		try {
			/**
			 *	is leeg?
			 *	nee:
			 *		is een nummer?
			 *		ja:
			 *			is een geldig nummer?
			 *			ja:
			 *
			 *		nee:
			 *
			 *	ja:
			 *
			 */
			if(StringUtil.isNotEmptyOrNull(kvknummer)) {
				if(SearchUtil.isKvKNumber(kvknummer)) {
					if(SearchUtil.isFullKvKNumber(kvknummer)) {
						WebServiceClient client = new WebServiceClient();
						companylist = new ArrayList<>();
						CompanyInfo ciresult = client.getDossierWithFullNumber(kvknummer);
						if (ciresult != null)
							companylist.add(ciresult);
					} else {
						WebServiceClient webServiceClient = new WebServiceClient();
						companylist = webServiceClient.searchByKvKNummer(kvknummer, 20);
					}
				} 
			} else {
				LOGGER.debug("Method retrieveFromCompanyInfo. Kan geen Companies ophalen als er geen zoekcriteria zijn opgegeven.");
			}
			
			if (companylist != null && companylist.size() > 0) {
				for (CompanyInfo company : companylist) {
					if (company.getCreditSafeHeadQuarters() != null && company.getCreditSafeHeadQuarters().equals("H")) {
						result = company;
						break;
					}
				}
			}

		} catch(JAXBException e) {
			throw new ServiceException(e);
		} catch(CSCommunicationException e) {
			throw new ThirdPartyServiceException(e);
		} catch(CSSOAPFaultException e) {
			throw new ServiceException(e);
		} catch(DVKException e) {
			throw new ServiceException(e);
		}

		return result;
	}	
	/**
	 * {@inheritDoc}
	 */
	//    @Override
	//    public CIKvKDossier getCIKvKDossierFromCompanyInfo(String kvKNummer) throws ServiceException {
	//        LOGGER.debug("Start.");
	//        CIKvKDossier result = null;
	//
	//        try {
	//            if(StringUtil.isNotEmptyOrNull(kvKNummer)) {
	//                WebServiceClient webServiceClient = new WebServiceClient();
	//                result = webServiceClient.getDossier(kvKNummer);
	//
	//                // try something else if result is null....
	//                if (result == null) {
	//                	// try new search with no trailing 0000, or add 0000 if there is no trailing 0000.
	//                	String otherKvKNummer = kvKNummer.substring(0, 8);
	//                	if (kvKNummer.length() == 8)
	//                		otherKvKNummer+= "0000";
	//
	//                	result = webServiceClient.getDossier(otherKvKNummer);
	//                }
	//
	//            } else {
	//                LOGGER.debug("Cannot retrieve a KvKDossier without a KvKNummer.");
	//            }
	//
	//        }catch (JAXBException e) {
	//            throw new ServiceException(e);
	//        } catch (CICommunicationException e) {
	//            throw new ServiceException(e);
	//        } catch (CISOAPFaultException e) {
	//            throw new ServiceException(e);
	//        }
	//
	//        return result;
	//    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CIKvKDossier getCIKvKDossierFromCompanyInfo(String kvKNummer, String subdossier, boolean isHoofdVestiging) throws ServiceException, ThirdPartyServiceException {
		LOGGER.debug("Start.");
		CIKvKDossier result = null;

		try {
			String hoofdNeven = "H";
			if(!isHoofdVestiging) hoofdNeven = "N";

			//ToDo CIKvKDossier subdossier nr must be null when '0000';
			String kvkNummerCS = kvKNummer;
			// MBR subdossier issue 16-2-2016
			//if(subdossier == null) kvkNummerCS += "0000";
			//else kvkNummerCS += subdossier;
			if (subdossier != null) {
				kvkNummerCS += subdossier;
			}
				
			
			if(StringUtil.isNotEmptyOrNull(kvkNummerCS)) {
				WebServiceClient webServiceClient = new WebServiceClient();
				//result = webServiceClient.getDossier(kvkNummerCS, hoofdNeven);
				if(kvKNummer.trim().length() > 8) {
					result = restKVK.getDossier(kvKNummer.substring(0, 8),kvKNummer.substring(8, 20), isHoofdVestiging);
				}else {
					result = restKVK.getDossier(kvKNummer.substring(0, 8),"", isHoofdVestiging);
				}
				
				// try something else if result is null....
				if(result == null) {
					// try new search with no trailing 0000, or add 0000 if there is no trailing 0000.
					String otherKvKNummer = kvKNummer.substring(0, 8);
					if(kvKNummer.length() == 8) otherKvKNummer += "0000";

					result = webServiceClient.getDossier(otherKvKNummer, hoofdNeven);
				}
				
				// workaround, creditsafe legal form is by description. Convert it to RV code
				if(result != null && result.getRv() != null) {
					try {
						Rechtsvorm rechtsvorm = bedrijfDataService.findByRvOmschrijving(result.getRv());
						if(rechtsvorm != null) result.setRv(rechtsvorm.getCode());
					} catch(DataServiceException e) {
						LOGGER.warn("Cannot find RV code for: " + result.getRv());
					}
				}				

			} else {
				LOGGER.debug("Cannot retrieve a KvKDossier without a KvKNummer.");
			}

		} catch(JAXBException e) {
			throw new ServiceException(e);
		} catch(CSCommunicationException e) {
			throw new ThirdPartyServiceException(e);
		} catch(CSSOAPFaultException e) {
			throw new ServiceException(e);
		} catch(DVKException e) {
			throw new ServiceException(e);
		}

		return result;
	}

	@Override
	public List<CompanyInfo> findByKvKNummer(String kvKNummer) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public CompanyInfo saveCompanyInfo(CompanyInfo companyInfo) throws ServiceException {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void deleteCompanyInfo(Integer companyInfo_ID) throws ServiceException {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	@Transactional
	public CIKvKDossier saveCIKvkDossier(CIKvKDossier newCiKvKDossier) throws ServiceException {
		CIKvKDossier result = null;

		if(newCiKvKDossier != null) {
			try {
				CIKvKDossier existingDossier = companyInfoDataService.findCIKvkDossierByKvkNumber(newCiKvKDossier.getKvKnummer(), newCiKvKDossier.getSubDossier());

				if(existingDossier != null) {
					if(CSConverter.compareCIKvKDossier(existingDossier, newCiKvKDossier)) {
						// data is equal, so only update DatumLaatsteUpdate
						existingDossier.setDatumLaatsteUpdate(DateUtil.getCurrentTimestamp());
						result = companyInfoDataService.saveCIKvkDossier(existingDossier);
					} else {
						// copy current dossier to history 
						CIKvKDossierHistorie kvkDossierHistorie = CSConverter.transformToCIKvKDossierHistorie(existingDossier);
						// set referentie to current dossier
						kvkDossierHistorie.setCIKvKDossier(existingDossier);
						companyInfoDataService.saveCIKvkDossierHistorie(kvkDossierHistorie);

						//Set the CIKvKDossier ID
						newCiKvKDossier.setCikvKdossierId(existingDossier.getCikvKdossierId());

						for(CIKvKBestuurder best : newCiKvKDossier.getCIKvKBestuurders())
							best.setCIKvKDossierCikvKdossierId(newCiKvKDossier.getCikvKdossierId());

						//Determine what Bestuurder objects should be added or deleted
						Set<CIKvKBestuurder> bestuurdersToDelete = new HashSet<>();
						Set<CIKvKBestuurder> bestuurdersToAdd = new HashSet<>();

						for(CIKvKBestuurder bestToDelete : existingDossier.getCIKvKBestuurders()) {
							boolean bestExists = false;
							for(CIKvKBestuurder newDossierBest : newCiKvKDossier.getCIKvKBestuurders()) {
								if(newDossierBest.getNaam().equals(bestToDelete.getNaam())){
									if(newDossierBest.getCIKvKDossier() != null && bestToDelete.getCIKvKDossier() != null){
										if(newDossierBest.getCIKvKDossierCikvKdossierId().equals(bestToDelete.getCIKvKDossierCikvKdossierId())){
											bestExists = true;
										}
									}
								}
							}

							if(!bestExists) bestuurdersToDelete.add(bestToDelete);
						}

						for(CIKvKBestuurder bestToAdd : newCiKvKDossier.getCIKvKBestuurders()) {
							boolean bestExists = false;
							for(CIKvKBestuurder existingDossierBest : existingDossier.getCIKvKBestuurders()) {
								if(existingDossierBest.getNaam().equals(bestToAdd.getNaam())){
									if(existingDossierBest.getCIKvKDossier() != null && bestToAdd.getCIKvKDossier() != null){
										if(existingDossierBest.getCIKvKDossierCikvKdossierId().equals(bestToAdd.getCIKvKDossierCikvKdossierId())){
											bestExists = true;
										}
									}
								}
							}

							if(!bestExists) bestuurdersToAdd.add(bestToAdd);
						}
						
						boolean deleteSetDone = bestuurdersToDelete.removeAll(newCiKvKDossier.getCIKvKBestuurders());
						boolean addSetDone = bestuurdersToAdd.removeAll(existingDossier.getCIKvKBestuurders());

						existingDossier.setCIKvKBestuurders(bestuurdersToAdd);						
						
						//Set the CIKvKDossier ID
						for(CIKvKAandeelhouder aandh : newCiKvKDossier.getCIKvKAandeelhouders())
							aandh.setCIKvKDossierCikvKdossierId(newCiKvKDossier.getCikvKdossierId());
						
						//Determine what Aandeelhouder objects should be added or deleted
						Set<CIKvKAandeelhouder> aandeelhoudersToDelete = new HashSet<>();
						Set<CIKvKAandeelhouder> aandeelhoudersToAdd = new HashSet<>();

						for(CIKvKAandeelhouder aandhToDelete : existingDossier.getCIKvKAandeelhouders()) {
							boolean aandhExists = false;
							for(CIKvKAandeelhouder newDossierAandh : newCiKvKDossier.getCIKvKAandeelhouders()) {
								if(newDossierAandh.getNaam().equals(aandhToDelete.getNaam())){
									if(newDossierAandh.getCIKvKDossier() != null && aandhToDelete.getCIKvKDossier() != null){
										if(newDossierAandh.getCIKvKDossierCikvKdossierId().equals(aandhToDelete.getCIKvKDossierCikvKdossierId())){
											aandhExists = true;
										}
									}
								}
							}

							if(!aandhExists) aandeelhoudersToDelete.add(aandhToDelete);
						}

						for(CIKvKAandeelhouder aandhToAdd : newCiKvKDossier.getCIKvKAandeelhouders()) {
							boolean aandhExists = false;
							for(CIKvKAandeelhouder existingDossierAandh : existingDossier.getCIKvKAandeelhouders()) {
								if(existingDossierAandh.getNaam().equals(aandhToAdd.getNaam())){
									if(existingDossierAandh.getCIKvKDossier() != null && aandhToAdd.getCIKvKDossier() != null){
										if(existingDossierAandh.getCIKvKDossierCikvKdossierId().equals(aandhToAdd.getCIKvKDossierCikvKdossierId())){
											aandhExists = true;
										}
									}
								}
							}

							if(!aandhExists) aandeelhoudersToAdd.add(aandhToAdd);
						}						

						deleteSetDone = aandeelhoudersToDelete.removeAll(newCiKvKDossier.getCIKvKAandeelhouders());
						addSetDone = aandeelhoudersToAdd.removeAll(existingDossier.getCIKvKAandeelhouders());

						existingDossier.setCIKvKAandeelhouders(aandeelhoudersToAdd);

						//Set the CIKvKDossier ID
						for(CIKvKCurator curs : newCiKvKDossier.getCIKvKCurators())
							curs.setCIKvKDossierCikvKdossierId(newCiKvKDossier.getCikvKdossierId());
						
						//Determine what Curator objects should be added or deleted
						Set<CIKvKCurator> curatorsToDelete = new HashSet<>();
						Set<CIKvKCurator> curatorsToAdd = new HashSet<>();

						for(CIKvKCurator curToDelete : existingDossier.getCIKvKCurators()) {
							boolean curExists = false;
							for(CIKvKCurator newDossierCur : newCiKvKDossier.getCIKvKCurators()) {
								if(newDossierCur.getNaam().equals(curToDelete.getNaam())){
									if(newDossierCur.getCIKvKDossier() != null && curToDelete.getCIKvKDossier() != null){
										if(newDossierCur.getCIKvKDossierCikvKdossierId().equals(curToDelete.getCIKvKDossierCikvKdossierId())){
											curExists = true;
										}
									}
								}
							}

							if(!curExists) curatorsToDelete.add(curToDelete);
						}

						for(CIKvKCurator curToAdd : newCiKvKDossier.getCIKvKCurators()) {
							boolean curExists = false;
							for(CIKvKCurator existingDossierCur : existingDossier.getCIKvKCurators()) {
								if(existingDossierCur.getNaam().equals(curToAdd.getNaam())){
									if(existingDossierCur.getCIKvKDossier() != null && curToAdd.getCIKvKDossier() != null){
										if(existingDossierCur.getCIKvKDossierCikvKdossierId().equals(curToAdd.getCIKvKDossierCikvKdossierId())){
											curExists = true;
										}
									}
								}
							}

							if(!curExists) curatorsToAdd.add(curToAdd);
						}						

						deleteSetDone = curatorsToDelete.removeAll(newCiKvKDossier.getCIKvKCurators());
						addSetDone = curatorsToAdd.removeAll(existingDossier.getCIKvKCurators());

						existingDossier.setCIKvKCurators(curatorsToAdd);
						
						// update current dossier
						// copy identifier for copy all props
						existingDossier = CSConverter.copyCIKvKDossier(newCiKvKDossier, existingDossier);
						result = companyInfoDataService.saveCIKvkDossier(existingDossier);

						//Finally remove the Bestuurder objects in the bestuurdersToDelete set
						for(CIKvKBestuurder bestuurderToDelete : bestuurdersToDelete) {
							companyInfoDataService.deleteCIKvkBestuurder(bestuurderToDelete);
						}
						//Finally remove the Aandeelhouder objects in the aandeelhoudersToDelete set
						for(CIKvKAandeelhouder aandeelhouderToDelete : aandeelhoudersToDelete) {
							companyInfoDataService.deleteCIKvkAandeelhouder(aandeelhouderToDelete);
						}						
						//Finally remove the Curator objects in the curatorsToDelete set
						for(CIKvKCurator curatorToDelete : curatorsToDelete) {
							companyInfoDataService.deleteCIKvkCurator(curatorToDelete);
						}					}
				} else {
					// create new dossier
					result = companyInfoDataService.saveCIKvkDossier(newCiKvKDossier);
				}

			} catch(DataServiceException e) {
				throw new ServiceException("Error in CIKvKDossier database transaction: " + e.getMessage());
			} catch(Exception e) {
				throw new ServiceException("Error saving CIKvKDossier");
			}

			return result;
		} else throw new ServiceException("Cannot save CIKvKDossier as null");

	}
}