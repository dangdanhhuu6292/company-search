package nl.devoorkant.creditsafe.client;

//import nl.devoorkant.creditsafe.CSCommunicationException;
//import nl.devoorkant.creditsafe.CSSOAPFaultException;
//import nl.devoorkant.creditsafe.converter.CSConverter;
//import nl.devoorkant.creditsafe.cxf.ArrayOfCountryCode;
//import nl.devoorkant.creditsafe.cxf.ArrayOfunsignedInt;
//import nl.devoorkant.creditsafe.cxf.CompaniesList;
//import nl.devoorkant.creditsafe.cxf.CompanyReportSet;
//import nl.devoorkant.creditsafe.cxf.CompanyReportType;
//import nl.devoorkant.creditsafe.cxf.CountryCode;
//import nl.devoorkant.creditsafe.cxf.CustomData;
//import nl.devoorkant.creditsafe.cxf.GlobalDataService;
//import nl.devoorkant.creditsafe.cxf.Language;
//import nl.devoorkant.creditsafe.cxf.MainServiceBasic;
//import nl.devoorkant.creditsafe.cxf.QueryMatchType;
//import nl.devoorkant.creditsafe.cxf.QueryString;
//import nl.devoorkant.creditsafe.cxf.SearchCriteria;

import com.creditsafe.globaldata.datatypes.*;
import com.creditsafe.globaldata.datatypes.reports.CompanyReportSet;
import com.microsoft.schemas._2003._10.serialization.arrays.ArrayOfunsignedInt;
import nl.devoorkant.creditsafe.CSCommunicationException;
import nl.devoorkant.creditsafe.CSSOAPFaultException;
import nl.devoorkant.creditsafe.converter.CSConverter;
import nl.devoorkant.creditsafe.cxf.GlobalDataService;
import nl.devoorkant.creditsafe.cxf.MainServiceBasic;
import nl.devoorkant.exception.DVKException;
import nl.devoorkant.sbdr.data.model.CIKvKDossier;
import nl.devoorkant.sbdr.data.model.CompanyInfo;
import nl.devoorkant.util.StringUtil;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.frontend.ClientProxy;
import org.datacontract.schemas._2004._07.creditsafe.ArrayOfCountryCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.ws.BindingProvider;
import java.util.Date;
import java.util.List;

// CXF 2.7.4 not working in 2.5.1
//import org.apache.cxf.ws.addressing.impl.AddressingPropertiesImpl;
//import org.apache.cxf.ws.addressing.AddressingBuilderImpl;

/**
 * WebServiceClient for creating requests to Company Info.
 * <p/>
 * SBDR - Stichting Bedrijfskrediet Data Registratie
 * <p/>
 * Copyright:       Copyright (c) 2015. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author Martijn Bruinenberg
 * @version %I%
 */

public class WebServiceClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(WebServiceClient.class);

	public WebServiceClient() {

	}

	/**
	 *
	 */
	public CIKvKDossier getDossier(String kvkNummer, String hoofdNeven) throws CSCommunicationException, JAXBException, CSSOAPFaultException, DVKException {
		LOGGER.info("Start.");
		CIKvKDossier result = null;

		if (StringUtil.isNotEmptyOrNull(kvkNummer)) {
			// create format CreditSafe

			String companyId = "NL007/X/" + kvkNummer;

			GlobalDataService servicePort = initializeServicePort();
			LOGGER.info("Port initialized");

			try {
				//KvkDossier kvkDossier = servicePort.getDossier(kvKNummer);
				CompanyReportType reportType = CompanyReportType.FULL;
				CustomData customData = null;
				String chargeReference = null;
				Boolean storeInReportbox = false;
				ArrayOfunsignedInt portfolioIds = null;
				String monitoringReference = null;

				CompanyReportSet companyReportSet = servicePort.retrieveCompanyOnlineReport(companyId, reportType, Language.NL, customData, chargeReference, storeInReportbox, portfolioIds, monitoringReference);
				LOGGER.info("Na call");

				if (companyReportSet != null) {
					try {
						result = CSConverter.transformToCIKvKDossier(companyReportSet, hoofdNeven);
					} catch (Exception e) {
						// error in CS conversion
						LOGGER.error("Error converting CS company to dossier.", e);
						result = null;
						throw new DVKException("No result due CS convert error.");
					}
				} else {
					// Nothing received from web service
					LOGGER.error("No result received");
					throw new CSCommunicationException("No response receved: " + new Date());
				}

			} catch (Exception faultMsg) {
				new CSCommunicationException(faultMsg.getMessage());
			}

		} else {
			throw new DVKException("getDossier. No kvknr in request: " + new Date());
		}

		return result;
	}

	public CompanyInfo getDossierWithFullNumber(String KvKNo) throws CSCommunicationException, JAXBException, CSSOAPFaultException, DVKException {
		CompanyInfo result = null;

		if (StringUtil.isNotEmptyOrNull(KvKNo)) {
			String FullId = "NL007/X/" + KvKNo;
			GlobalDataService port = initializeServicePort();
			try {
				ArrayOfCountryCode countries = new ArrayOfCountryCode();
				countries.getCountryCode().add(CountryCode.NL);

				SearchCriteria criteria = new SearchCriteria();
				criteria.setId(FullId);
				CompaniesList resultList = port.findCompanies(countries, criteria, null, null);

				if (resultList != null) {
					CSConverter converter = new CSConverter();
					try {
						List<CompanyInfo> results = converter.transformToCompanyInfoList(resultList.getCompanies());
						if (results != null && results.size() > 0)
							result = results.get(0);
					} catch (Exception e) {
						// error in CS convert
						LOGGER.error("Error converting companies list from CS.", e);
						result = null;
						throw new DVKException("No result due CS convert error.");
					}
				} else {
					LOGGER.error("Method getDossierWithFullNumber. No result received");
					throw new CSCommunicationException("No response receved: " + new Date());
				}
			} catch (Exception faultMsg) {
				throw new CSCommunicationException(faultMsg.getMessage());
			}
		} else {
			throw new DVKException("getDossierWithFullNumber. No kvknr in request: " + new Date());
		}

		return result;
	}

	/**
	 *
	 */
	public List<CompanyInfo> searchByBedrijfsNaam(String bedrijfsNaam, int maxResults) throws CSCommunicationException, JAXBException, CSSOAPFaultException, DVKException {
		LOGGER.info("Method searchByBedrijfsNaam.");
		List<CompanyInfo> result = null;

		if (StringUtil.isNotEmptyOrNull(bedrijfsNaam)) {
			GlobalDataService servicePort = initializeServicePort();
			LOGGER.info("Method searchByBedrijfsNaam. Port initialized");

			try {
				ArrayOfCountryCode countries = new ArrayOfCountryCode();
				countries.getCountryCode().add(CountryCode.NL);

				SearchCriteria searchCriteria = new SearchCriteria();
				QueryString naam = new QueryString();
				naam.setMatchType(QueryMatchType.MATCH_BEGINNING);
				naam.setValue(bedrijfsNaam);
				searchCriteria.setName(naam);

				CustomData customData = null;

				String chargeReference = null;

				CompaniesList companiesList = servicePort.findCompanies(countries, searchCriteria, customData, chargeReference);

				//ResultEnumeration resultEnumeration = servicePort.searchByKvkNumber(kvKNummer, "", "", maxResults);
				LOGGER.info("Method searchByKvKNummer. Na call");

				if (companiesList != null) {
					CSConverter converter = new CSConverter();
					try {
						result = converter.transformToCompanyInfoList(companiesList.getCompanies());
					} catch (Exception e) {
						// error converting CS response
						LOGGER.error("Error converting companies list from CS.", e);
						result = null;
						throw new DVKException("No result due CS convert error.");
					}
				} else {
					// Nothing received from web service
					LOGGER.error("Method searchByKvKNummer. No result received");
					throw new CSCommunicationException("No response receved: " + new Date());
				}

			} catch (Throwable faultMsg) {
				LOGGER.error("Error in searchByBedrijfsNaam: " + faultMsg.getMessage(), faultMsg);

				throw new CSCommunicationException(faultMsg.getMessage());
			}

		} else {
			throw new DVKException("searchByBedrijfsNaam. No bedrijfsNaam in request: " + new Date());
		}

//        for (CompanyInfo companyInfo : result) {
//            LOGGER.info("Result = " + companyInfo.getKvKnummer());
//            LOGGER.info("Result = " + companyInfo.getBedrijfsNaam());
//            LOGGER.info("Result = " + companyInfo.getPlaats());
//        }

		return result;
	}

	/**
	 *
	 */
	public List<CompanyInfo> searchByKvKNummer(String kvKNummer, int maxResults) throws CSCommunicationException, JAXBException, CSSOAPFaultException, DVKException {
		LOGGER.info("Method searchByKvKNummer.");
		List<CompanyInfo> result = null;

		if (StringUtil.isNotEmptyOrNull(kvKNummer)) {
			GlobalDataService servicePort = initializeServicePort();
			LOGGER.info("Method searchByKvKNummer. Port initialized");

			try {
				ArrayOfCountryCode countries = new ArrayOfCountryCode();
				countries.getCountryCode().add(CountryCode.NL);

				SearchCriteria searchCriteria = new SearchCriteria();
				searchCriteria.setRegistrationNumber(kvKNummer);

				CustomData customData = null;

				String chargeReference = null;

				CompaniesList companiesList = servicePort.findCompanies(countries, searchCriteria, customData, chargeReference);

				//ResultEnumeration resultEnumeration = servicePort.searchByKvkNumber(kvKNummer, "", "", maxResults);
				LOGGER.info("Method searchByKvKNummer. Na call");

				if (companiesList != null) {
					CSConverter converter = new CSConverter();
					try {
						result = converter.transformToCompanyInfoList(companiesList.getCompanies());
					} catch (Exception e) {
						// error converting answer
						LOGGER.error("Error converting companies list from CS.", e);
						result = null;
						throw new DVKException("No result due CS convert error.");
					}
				} else {
					// Nothing received from web service
					LOGGER.error("Method searchByKvKNummer. No result received");
					throw new CSCommunicationException("No response receved: " + new Date());
				}

			} catch (Exception faultMsg) {
				throw new CSCommunicationException(faultMsg.getMessage());
			}

		} else {
			throw new DVKException("searchByKvKNummer. No kvknr in request: " + new Date());
		}

//        if (result != null)
//	        for (CompanyInfo companyInfo : result) {
//	            LOGGER.info("Result = " + companyInfo.getBedrijfsNaam());
//	        }

		return result;
	}

	private GlobalDataService initializeServicePort() throws CSSOAPFaultException {
		LOGGER.info("Method initializeServicePort.");

		MainServiceBasic csr = new MainServiceBasic();
		GlobalDataService loServicePort = csr.getBasicHttpBindingGlobalDataService();

		if (loServicePort != null) {

			// AdjustHeader
			Client loClient = ClientProxy.getClient(loServicePort);
			Endpoint endpoint = loClient.getEndpoint();

			BindingProvider bindingProvider = (BindingProvider) loServicePort;
			// testomgeving
			//bindingProvider.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, "sdbrLive");
			//bindingProvider.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, "sdbr1357");
			// live omgeving update credentials 7-5-2020
			bindingProvider.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, "adorsaLIVE"); // "horizonbouwLive" "westklusLive" "finexLive" "finexLive"
			bindingProvider.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, "adorsaLIVE21836"); // "horizonbouw335691" "westklus148925" "finex784521" "finexLive5671"

		} else {
			throw new CSSOAPFaultException("Error initializing serviceport");
		}

		return loServicePort;
	}

	private <T> T nodeToJAXB(Node node, Class<T> type) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(type);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		return type.cast(unmarshaller.unmarshal(node));
	}

}


