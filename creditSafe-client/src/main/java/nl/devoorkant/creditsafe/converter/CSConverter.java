package nl.devoorkant.creditsafe.converter;

//import nl.devoorkant.creditsafe.cxf.AddressData;
//import nl.devoorkant.creditsafe.cxf.CompaniesList;
//import nl.devoorkant.creditsafe.cxf.Company;
//import nl.devoorkant.creditsafe.cxf.CompanyActivity;
//import nl.devoorkant.creditsafe.cxf.CompanyReportSet;
//import nl.devoorkant.creditsafe.cxf.LtdCompanyFullReport;
//import nl.devoorkant.creditsafe.cxf.LtdFinancialStatement;
//import nl.devoorkant.creditsafe.cxf.OfficeType;
//import nl.devoorkant.creditsafe.cxf.StreetAddressWithTelephone;
//import nl.devoorkant.creditsafe.cxf.LtdCompanyFullReport.AdditionalInformation;

import com.creditsafe.globaldata.datatypes.Company;
import com.creditsafe.globaldata.datatypes.reports.*;
import nl.devoorkant.sbdr.data.model.*;
import nl.devoorkant.sbdr.data.util.SearchUtil;
import nl.devoorkant.util.DateUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.beanutils.BeanMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

import com.creditsafe.globaldata.datatypes.AddressData;
import com.creditsafe.globaldata.datatypes.CompaniesList.Companies;
import com.creditsafe.globaldata.datatypes.OfficeType;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.xpath.XPathExpressionException;

/**
 * CIConverter, used for converting CompanyInfo Objects to SBDR Objects.
 * <p/>
 * SBDR - Stichting Bedrijfskrediet Data Registratie
 * <p/>
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author Jan Meekel
 * @version %I%
 */

public class CSConverter {
	private static String regexpcommentary_employees = "De onderneming heeft ([0-9]+) werknemers?\\."; // De onderneming heeft 4 werknemers.
	private static String regexpcsid = "NL[0-9]{3}\\/[A-Z]\\/(\\w+)"; // NL007/X/380247390000
	private static String regexphuisnummer = "([\\d]+)([\\s]*)([\\W]*)(.*)"; // 4 groups: housenr, spacing opt., other chars opt., suffix opt.
	private static final Logger ioLogger = LoggerFactory.getLogger(CSConverter.class);

	/**
	 * Constructs a CSConverter.
	 */
	public CSConverter() {
	}

	public static boolean compareCIKvKDossier(CIKvKDossier one, CIKvKDossier two) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		boolean areEqual = false;
		BeanMap map = new BeanMap(one);

		PropertyUtilsBean propUtils = new PropertyUtilsBean();

		for(Object propNameObject : map.keySet()) {
			String propertyName = (String) propNameObject;
			//ioLogger.info("check  " + propertyName);

			if(!propertyName.equals("cikvKdossierId") && !propertyName.equals("datumLaatsteUpdate") && !propertyName.equals("CIKvKDossierHistories")) {
				Object property1 = propUtils.getProperty(one, propertyName);
				Object property2 = propUtils.getProperty(two, propertyName);

				if((property1 == null && property2 == null) || (property1 != null && property1.equals(property2)) || (property1 != null && property1.toString().equals("0") && property2 == null)) {
					ioLogger.debug("  " + propertyName + " is equal");
					areEqual = true;
				} else {
					ioLogger.debug("> " + propertyName + " is different (oldValue=\"" + property1 + "\", newValue=\"" + property2 + "\")");
					areEqual = false;
					break;
				}
			}
		}

		return areEqual;
	}

	public static Map<String, Object> convertXmlToHashMap(Element e) {
		Map<String, Object> hMap = new HashMap<>();

		String nodeName;
		Object nodeContent;

		if(e.hasChildNodes()) {
			NodeList childs = e.getChildNodes();

			for(int i = 0; i < childs.getLength(); i++) {

				Node n = childs.item(i);
				nodeName = n.getNodeName() + "0";

				while(hMap.get(nodeName) != null) {
					int j = Integer.parseInt(nodeName.replaceAll("[a-zA-Z]*", ""));
					nodeName = nodeName.replaceAll("[0-9]*$", "") + (j + 1);
				}

				if(n.hasChildNodes() && (n.getFirstChild().hasChildNodes()))
					nodeContent = convertXmlToHashMap((Element) n);
				else nodeContent = childs.item(i).getTextContent();

				hMap.put(nodeName, nodeContent);
			}
		}

		return hMap;
	}

	public static CIKvKDossier copyCIKvKDossier(CIKvKDossier existingDossier, CIKvKDossier newDossier) throws IllegalAccessException, InvocationTargetException {
		ioLogger.debug("Method copyCIKvKDossier.");
		CIKvKDossier result = null;

		if(existingDossier == null) {
			result = new CIKvKDossier();
		} else {
			result = newDossier;
		}

		if(existingDossier.getCIKvKBestuurders() != null && newDossier.getCIKvKBestuurders() != null) {
			Set<CIKvKBestuurder> huidigeBests = new HashSet<>(existingDossier.getCIKvKBestuurders());
			Set<CIKvKBestuurder> newBests = new HashSet<>(newDossier.getCIKvKBestuurders());
		}

		BeanUtils.copyProperties(newDossier, existingDossier);

		return result;
	}

	private static CIKvKBestuurder transformToCIKvKBestuurder(Director d){
		CIKvKBestuurder newBest = new CIKvKBestuurder();

		newBest.setGeslacht((byte) d.getGender());
		newBest.setNaam(d.getName().replaceAll("\\s+", " "));

		if(d.getPosition() != null && d.getPosition().size() > 0) {
			Set<CIKvKBestuurderFunctie> bestFuncties = new HashSet<>();
			for(CorporatePosition p : d.getPosition()) {
				CIKvKBestuurderFunctie bestFunctie = new CIKvKBestuurderFunctie();
				bestFunctie.setFunctie(p.getValue());

				bestFuncties.add(bestFunctie);
			}

			newBest.setCIKvKBestuurderFuncties(bestFuncties);
		}

		return newBest;
	}

	/**
	 * @param companyReportSet
	 * @param hoofdNeven
	 * @return
	 */
	public static CIKvKDossier transformToCIKvKDossier(CompanyReportSet companyReportSet, String hoofdNeven) {
		ioLogger.debug("Method transformTotransformToCIKvKDossier.");
		CIKvKDossier result = null;

		try {
			if(companyReportSet != null && companyReportSet.getReports() != null &&
					companyReportSet.getReports().getReport() != null && companyReportSet.getReports().getReport().size() == 1) {
				LtdCompanyFullReport companyReport = (LtdCompanyFullReport) companyReportSet.getReports().getReport().get(0);

				if(companyReport != null) {

					result = new CIKvKDossier();
					result.setDatumLaatsteUpdate(DateUtil.getCurrentTimestamp());

					result.setCreditSafeId(companyReport.getCompanyId());
					String kvknr12 = findKvkNummer12(result.getCreditSafeId());
					ECreditSafeStatusType creditSafeStatus = null;

					if(kvknr12 != null) {
						result.setKvKnummer(companyReport.getCompanySummary().getCompanyRegistrationNumber());
						if(kvknr12.length() == 12) result.setSubDossier(kvknr12.substring(8));
					}

					if(companyReport.getCompanySummary() != null) {
						result.setDossierNr(companyReport.getCompanySummary().getCompanyRegistrationNumber()); // kvknr without 0000

						if(companyReport.getCompanySummary().getCompanyStatus() != null) {
							try {
								creditSafeStatus = ECreditSafeStatusType.findBySearchCode(companyReport.getCompanySummary().getCompanyStatus().getCode());
							} catch(Exception e) {
								ioLogger.warn("Cannot classify CreditSafeStatus: '" + companyReport.getCompanySummary().getCompanyStatus().getCode() + "' of kvknr: " + result.getKvKnummer());
								creditSafeStatus = ECreditSafeStatusType.NIETACTIEF;
							}
						}
						
						if (companyReport.getCompanySummary().getCreditRating() != null) {
							if (companyReport.getCompanySummary().getCreditRating().getProviderValue() != null && companyReport.getCompanySummary().getCreditRating().getProviderValue().getValue() != null) {
								try {
									result.setCsCreditRating(new Integer(companyReport.getCompanySummary().getCreditRating().getProviderValue().getValue()));
								} catch (Exception e) {
									ioLogger.error("Cannot convert creditrating field to number: '" + companyReport.getCompanySummary().getCreditRating().getProviderValue().getValue() + "'");
								}
								result.setCsCommonDescription(companyReport.getCompanySummary().getCreditRating().getCommonDescription());
								if (companyReport.getCompanySummary().getCreditRating().getCommonValue() != null && companyReport.getCompanySummary().getCreditRating().getCommonValue().value() != null)
									result.setCsCommonValue(companyReport.getCompanySummary().getCreditRating().getCommonValue().value().toUpperCase());
								result.setCsProviderDescription(companyReport.getCompanySummary().getCreditRating().getProviderDescription());
							}
						}
					}
					result.setHoofdNeven(hoofdNeven);

					if(companyReport.getDirectors() != null && companyReport.getDirectors().getCurrentDirectors() != null && companyReport.getDirectors().getCurrentDirectors().getDirector().size() > 0) {
						/**
						 * 	if there is 1 element:
						 *		add it, no questions asked
						 *	if there are > 1 elements
						 *		if all elements have no position:
						 *			add everything
						 *		if there are elements with a position:
						 *			don't take in the elements without a position
						 */

						Set<CIKvKBestuurder> bestuurders = new HashSet<>();
						if(companyReport.getDirectors().getCurrentDirectors().getDirector().size() == 1) {
							//add the element
							bestuurders.add(transformToCIKvKBestuurder(companyReport.getDirectors().getCurrentDirectors().getDirector().get(0)));
						} else {
							boolean anyElementHasPosition = false;

							for(Director d : companyReport.getDirectors().getCurrentDirectors().getDirector()) {
								if(d.getPosition() != null && d.getPosition().size() > 0) {
									anyElementHasPosition = true;
									break;
								}
							}

							for(Director d : companyReport.getDirectors().getCurrentDirectors().getDirector()){
								if(anyElementHasPosition){
									//only add the element if the director has *a* position
									if(d.getPosition() != null && d.getPosition().size() > 0) {
										//check if the name already exists
										//if true, only add the position
										//if false, add a new Bestuurder CikvKbstuurder element
										boolean bestuurderExists = false;

										CIKvKBestuurder existingKvKBestuurder = null;
										for(CIKvKBestuurder existingBestuurder : bestuurders){
											String newBestuurderNaam = d.getName().replaceAll("\\s+", " ");
											if(newBestuurderNaam.equals(existingBestuurder.getNaam())) {
												bestuurderExists = true;
												existingKvKBestuurder = existingBestuurder;
												break;
											}
										}

										if(bestuurderExists){
											Set<CIKvKBestuurderFunctie> functies = existingKvKBestuurder.getCIKvKBestuurderFuncties();

											for(CorporatePosition pos : d.getPosition()){
												CIKvKBestuurderFunctie newFunctie = new CIKvKBestuurderFunctie();
												newFunctie.setFunctie(pos.getValue());
												functies.add(newFunctie);
											}

											existingKvKBestuurder.setCIKvKBestuurderFuncties(functies);
										} else {
											existingKvKBestuurder = transformToCIKvKBestuurder(d);
										}

										bestuurders.add(existingKvKBestuurder);
									}
								} else {
									//no element has any position, add everything
									bestuurders.add(transformToCIKvKBestuurder(d));
								}
							}
						}

						result.setCIKvKBestuurders(bestuurders);
					}
					//result.setBeherendKn(kvkDossier.getBEHERENDKN());
					//resul t.setContinent(kvkDossier.getCONTINENT());
					//result.setContinentCode(kvkDossier.getCONTINENTCODE());

					Set<CIKvKAandeelhouder> aandeelhouders = new HashSet<CIKvKAandeelhouder>();
					if(companyReport.getShareCapitalStructure()!=null){
						if(companyReport.getShareCapitalStructure().getIssuedShareCapital()!=null)
							result.setGestortKapitaal(companyReport.getShareCapitalStructure().getIssuedShareCapital().getValue().longValue());

						if(companyReport.getShareCapitalStructure().getNominalShareCapital()!=null)
							result.setNominaalAandelenKapitaal(companyReport.getShareCapitalStructure().getNominalShareCapital().getValue().longValue());
						
						if (companyReport.getShareCapitalStructure().getShareHolders() != null) {
							for (ShareHolder shareholder : companyReport.getShareCapitalStructure().getShareHolders().getShareHolder()) {
								CIKvKAandeelhouder aandeelhouder = new CIKvKAandeelhouder();
								if (shareholder.getName() != null)
									aandeelhouder.setNaam(shareholder.getName());
								else
									aandeelhouder.setNaam("onbekend");
								if (shareholder.getAddress() != null) {
									// Pattern match regexp for suffix
									AddressData address = shareholder.getAddress();
									String houseNumberTotal = address.getHouseNumber();
									if (houseNumberTotal != null && houseNumberTotal.length() > 0) {								
								    	Pattern pattern = Pattern.compile(regexphuisnummer);
										Matcher matcher = pattern.matcher(houseNumberTotal.trim());
			
										String houseNr = null;
										String houseNrSuffix = null;
										String houseNrOther = null;
										
										if(matcher.find()) {
											houseNr = matcher.group(1);
											//houseNrSpacing = matcher.group(2);
											houseNrOther = matcher.group(3);
											houseNrSuffix = matcher.group(4);
											if (houseNrOther != null)
												houseNrOther = houseNrOther.trim();
											if (houseNrSuffix != null)
												houseNrSuffix = houseNrSuffix.trim();
											ioLogger.debug("CSConverter housenr: " + houseNr + " other: " + houseNrOther + " suffix: " + houseNrSuffix);
											
											if (houseNr != null && !houseNr.isEmpty())
												aandeelhouder.setHuisnummer(houseNr);
											if (houseNrOther != null && !houseNrOther.isEmpty() && houseNrSuffix != null && !houseNrSuffix.isEmpty())
												aandeelhouder.setHuisnummerToevoeging(houseNrOther + houseNrSuffix);
											else if (houseNrSuffix != null && !houseNrSuffix.isEmpty())
												aandeelhouder.setHuisnummerToevoeging(houseNrSuffix);										
										}    									
									}			
									
									aandeelhouder.setStraat(address.getStreet());
									aandeelhouder.setPostcode(address.getPostalCode());
									aandeelhouder.setPlaats(address.getCity());									
								}
								
								if (shareholder.getSharePercent() != null)
									aandeelhouder.setAandelenPercentage(shareholder.getSharePercent());
								
								aandeelhouders.add(aandeelhouder);
							}
						}
					}
					result.setCIKvKAandeelhouders(aandeelhouders);

					if(companyReport.getCompanySummary() != null) {
						if(companyReport.getCompanySummary().getMainActivity() != null)
							result.setHoofdActiviteit(companyReport.getCompanySummary().getMainActivity().getActivityCode());

						//result.setLand(kvkDossier.getLAND());
						result.setLandCode(companyReport.getCompanySummary().getCountry().value());
					}

					if(companyReport.getGroupStructure() != null) {
						if(companyReport.getGroupStructure().getImmediateParent() != null)
							result.setParent(companyReport.getGroupStructure().getImmediateParent().getRegistrationNumber());

						if(companyReport.getGroupStructure().getUltimateParent() != null)
							result.setUltimateParent(companyReport.getGroupStructure().getUltimateParent().getRegistrationNumber());
					}

					if(companyReport.getCompanyIdentification() != null) {
						if(companyReport.getCompanyIdentification().getBasicInformation() != null) {
							// Datum inschrijving
							if(companyReport.getCompanyIdentification().getBasicInformation().getDateofCompanyRegistration() != null)
								result.setDatumInschrijving(xmlGregorianCalendarToDate(companyReport.getCompanyIdentification().getBasicInformation().getDateofCompanyRegistration()));
							// Datum voortzetting
							if(companyReport.getCompanyIdentification().getBasicInformation().getDateofStartingOperations() != null)
								result.setDatumVoortzetting(xmlGregorianCalendarToDate(companyReport.getCompanyIdentification().getBasicInformation().getDateofStartingOperations()));
							// Legal form description
							if(companyReport.getCompanyIdentification().getBasicInformation().getLegalForm() != null)
								result.setRv(companyReport.getCompanyIdentification().getBasicInformation().getLegalForm().getValue());
							// Registered company name
							if(companyReport.getCompanyIdentification().getBasicInformation().getRegisteredCompanyName() != null)
								result.setVenNaam(companyReport.getCompanyIdentification().getBasicInformation().getRegisteredCompanyName());
							else
								ioLogger.warn("transformToCIKvKDossier: Company kvknr:" + result.getKvKnummer() + " has no RegisteredCompanyName!");
							// Business name
							if(companyReport.getCompanyIdentification().getBasicInformation().getBusinessName() != null)
								result.setHandelsNaam(companyReport.getCompanyIdentification().getBasicInformation().getBusinessName());
							else result.setHandelsNaam(result.getVenNaam());
						}


						if(companyReport.getCompanyIdentification().getActivities() != null) {
							if(companyReport.getCompanyIdentification().getActivities().getActivity() != null && companyReport.getCompanyIdentification().getActivities().getActivity().size() > 0) {
								int nevenact = 0;
								for(CompanyActivity activity : companyReport.getCompanyIdentification().getActivities().getActivity()) {
									if(result.getHoofdActiviteit() != null && activity.getActivityCode().equals(result.getHoofdActiviteit())) {
										result.setSbihoofdAct(activity.getActivityCode());

									} else if(nevenact < 2) {
										if(nevenact == 0) result.setSbinevenActiviteit1(activity.getActivityCode());
										else result.setSbinevenActiviteit2(activity.getActivityCode());
										nevenact++;
									}

									//result.setNevenActiviteit1(kvkDossier.getNEVENACT1());
									//result.setNevenActiviteit2(kvkDossier.getNEVENACT2());


								}
							}
						}
					}

					if(companyReport.getFinancialStatements() != null) {
						if(companyReport.getFinancialStatements().getFinancialStatement() != null && companyReport.getFinancialStatements().getFinancialStatement().size() > 0) {
							LtdFinancialStatement financialStatement = companyReport.getFinancialStatements().getFinancialStatement().get(0);
							result.setDeponeringJaarstukken(financialStatement.getYearEndDate().getYear());
						}
					}

					if(companyReport.getContactInformation() != null) {
						//result.setHuisnummerToevoeging(kvkDossier.getTOEVHSNR());
						//result.setHuisnummerToevoegingCa(kvkDossier.getTOEVHNCA());
						//result.setStraatHuisnummer(kvkDossier.getSTRHSNR());
						//result.setStraatHuisnummerCa(kvkDossier.getSTRHSCA());
						//result.setTelefoonNummerMobiel(kvkDossier.getMOBIELNR());
						//result.setTelefoonNetNummer(kvkDossier.getTELNETNR());

						if(companyReport.getContactInformation().getMainAddress() != null) {
							AddressData address = companyReport.getContactInformation().getMainAddress().getAddress();
							
							// Pattern match regexp for suffix
							String houseNumberTotal = address.getHouseNumber();
							if (houseNumberTotal != null && houseNumberTotal.length() > 0) {								
						    	Pattern pattern = Pattern.compile(regexphuisnummer);
								Matcher matcher = pattern.matcher(houseNumberTotal.trim());
	
								String houseNr = null;
								String houseNrSuffix = null;
								String houseNrOther = null;
								
								if(matcher.find()) {
									houseNr = matcher.group(1);
									//houseNrSpacing = matcher.group(2);
									houseNrOther = matcher.group(3);
									houseNrSuffix = matcher.group(4);
									if (houseNrOther != null)
										houseNrOther = houseNrOther.trim();
									if (houseNrSuffix != null)
										houseNrSuffix = houseNrSuffix.trim();
									
									ioLogger.debug("CSConverter housenr: " + houseNr + " other: " + houseNrOther + " suffix: " + houseNrSuffix);
									
									if (houseNr != null && !houseNr.isEmpty())
										result.setHuisnummer(houseNr);
									if (houseNrOther != null && !houseNrOther.isEmpty() && houseNrSuffix != null && !houseNrSuffix.isEmpty())
										result.setHuisnummerToevoeging(houseNrOther + houseNrSuffix);
									else if (houseNrSuffix != null && !houseNrSuffix.isEmpty())
										result.setHuisnummerToevoeging(houseNrSuffix);										
								}    									
							}			
							
							result.setStraat(address.getStreet());
							result.setPostcode(address.getPostalCode());
							result.setPlaats(address.getCity());
						}
						if(companyReport.getContactInformation().getOtherAddresses() != null && companyReport.getContactInformation().getOtherAddresses().getOtherAddress().size() > 0) {
							

							// postadres
							
							StreetAddressWithTelephone addresswithphone = null;
							
							for (StreetAddressWithTelephone otheraddress : companyReport.getContactInformation().getOtherAddresses().getOtherAddress()) {
								if (otheraddress.getAddress() != null && otheraddress.getAddress().getStreet() != null && otheraddress.getAddress().getStreet().equalsIgnoreCase("Postbus"))
									addresswithphone = otheraddress;
							}
							
							if (addresswithphone != null) {
								String houseNumberTotal = addresswithphone.getAddress().getHouseNumber();
								// Pattern match regexp for suffix
								if (houseNumberTotal != null && houseNumberTotal.length() > 0) {								
							    	Pattern pattern = Pattern.compile(regexphuisnummer);
									Matcher matcher = pattern.matcher(houseNumberTotal.trim());
		
									String houseNr = null;
									String houseNrSuffix = null;
									String houseNrOther = null;
									
									if(matcher.find()) {
										houseNr = matcher.group(1);
										//houseNrSpacing = matcher.group(2);
										houseNrOther = matcher.group(3);
										houseNrSuffix = matcher.group(4);
										ioLogger.debug("CSConverter housenr: " + houseNr + " other: " + houseNrOther + " suffix: " + houseNrSuffix);
										
										if (houseNr != null && !houseNr.isEmpty())
											result.setHuisnummerCa(houseNr);
										if (houseNrOther != null && !houseNrOther.isEmpty() && houseNrSuffix != null && !houseNrSuffix.isEmpty())
											result.setHuisnummerToevoegingCa(houseNrOther + houseNrSuffix);
										else if (houseNrSuffix != null && !houseNrSuffix.isEmpty())
											result.setHuisnummerToevoegingCa(houseNrSuffix);										
									}    									
								}	
								
								result.setStraatCa(addresswithphone.getAddress().getStreet());
								result.setStraatHuisnummerCa(addresswithphone.getAddress().getHouseNumber());
								result.setPostcodeCa(addresswithphone.getAddress().getPostalCode());
								result.setPlaatsCa(addresswithphone.getAddress().getCity());
							}							
						}


						if(companyReport.getContactInformation().getWebsites() != null && companyReport.getContactInformation().getWebsites().getWebsite().size() > 0)
							result.setDomeinNaam(companyReport.getContactInformation().getWebsites().getWebsite().get(0));
					}
					result.setTelefoonAbonneeNummer(companyReport.getCompanyIdentification().getBasicInformation().getContactTelephoneNumber());

					//result.setEcoAct(kvkDossier.getECOACT());

					Date ceasedTradingDate = null;
					Date bankruptcyDate = null;
					String courtAction = null;
					boolean hasCourtData = false;
					boolean bankruptcy = false;
					CIKvKCurator curator = null;
					Set<CIKvKCurator> curators = new HashSet<CIKvKCurator>();
					if(companyReport.getAdditionalInformation() != null) {
						for(Element element : companyReport.getAdditionalInformation().getAny()) {
							//ioLogger.info("" + element.getNodeName() + " type: " + element.getNodeValue());

							printXml(element);

							if(element.getNodeName().equals("CompanyHistory")) {
								//Map<String, Object> testMap = convertXmlToHashMap(element);

								Map<Date, EEventType> eventOccurrences = new HashMap<>();

								NodeList events = element.getChildNodes();
								for(int i = 0; i < events.getLength(); i++) {
									Element event = (Element) events.item(i);

									Date timestamp = getDateFromElement(event);

									String desc = null;
									Element eDesc = getElementFromSubNode(event, "Description");
									if(eDesc != null) desc = eDesc.getTextContent().replaceAll("\\s+", " ");

									if(EEventType.get(desc) != null) {
										eventOccurrences.put(timestamp, EEventType.get(desc));
									} else {
										ioLogger.info("Processing events: an unknown event was found: " + desc + "\n\nIf possible, add this to the enumeration, this event will be noted as other/anders/overig.");
										eventOccurrences.put(timestamp, EEventType.ANDERS);
									}
								}

								Date latestMove = null;

								if(eventOccurrences.size() > 0) {
									for(Map.Entry kv : eventOccurrences.entrySet()) {
										//Only go in here if the event was about an address change
										if(kv.getValue() == EEventType.PLAATS || kv.getValue() == EEventType.STRAAT || kv.getValue() == EEventType.HUISNR || kv.getValue() == EEventType.HUISNNR_TOEVOEGING || kv.getValue() == EEventType.POSTCODE) {
											if(latestMove != null) {
												if(((Date) kv.getKey()).after(latestMove)) {
													latestMove = (Date) kv.getKey();
												}
											} else latestMove = (Date) kv.getKey();
										}
									}
								}

								if(latestMove != null) {
									result.setDatumHuidigeVestiging(latestMove);
								} else {
									if(companyReport.getCompanyIdentification() != null) {
										if(companyReport.getCompanyIdentification().getBasicInformation() != null) {
											if(companyReport.getCompanyIdentification().getBasicInformation().getDateofStartingOperations() != null) {
												result.setDatumHuidigeVestiging(companyReport.getCompanyIdentification().getBasicInformation().getDateofStartingOperations().toGregorianCalendar().getTime());
											} else {
												if(companyReport.getCompanyIdentification().getBasicInformation().getDateofCompanyRegistration() != null)
													result.setDatumHuidigeVestiging(companyReport.getCompanyIdentification().getBasicInformation().getDateofCompanyRegistration().toGregorianCalendar().getTime());
											}
										}
									}
								}
							}

							if(element.getNodeName().equals("Misc")) {
								Element ceasedTradingElement = getElementFromSubNode(element, "CeasedTradingDate");
								if(ceasedTradingElement != null) {
									ceasedTradingDate = getDateFromElement(ceasedTradingElement);
								}

								String exporter = getStringFromElement(getElementFromSubNode(element, "Exporter"));
								if(exporter != null && exporter.equals("Ja")) result.setExport("J");
								else result.setExport("N");

								String importer = getStringFromElement(getElementFromSubNode(element, "Importer"));
								if(importer != null && importer.equals("Ja")) result.setImport_("J");
								else result.setImport_("N");

								String rsin = getStringFromElement(getElementFromSubNode(element, "RSINNumber"));
								if(rsin != null) result.setRsin(rsin);

							}

							if(element.getNodeName().equals("Commentaries")) {
								NodeList commentaries = element.getChildNodes();
								for(int i = 0; i < commentaries.getLength(); i++) {
									Element commentary = (Element) commentaries.item(i);
									Element text = getElementFromSubNode(commentary, "CommentaryText");
									// nr of employees
									if(text != null && text.getTextContent().contains("werknemer")) {
										Integer nr = findNrOfEmployees(text.getTextContent());
										if(nr != null) result.setNrEmployees(nr);
									}
								}
							}

							//							/**
							//							 <NegativeInformation xmlns="http://www.creditsafe.com/globaldata/datatypes/reports">
							//
							//							 <CourtData>
							//
							//							 <Facts>
							//
							//							 <CourtAction>declaration of bankruptcy</CourtAction>
							//
							//							 <DateOfBankruptcy>2014-10-21T00:00:00</DateOfBankruptcy>
							//
							//							 <Details>Uitspraak faillissement op 21 oktober 2014</Details>
							//
							//							 </Facts>
							//
							//							 <ReceiverDetails>
							//
							//							 <Name>mr. J.S. van Burg</Name>
							//
							//							 <Address>Postbus 538, Assen 9400AM</Address>
							//
							//							 </ReceiverDetails>
							//
							//							 </CourtData>
							//
							//							 </NegativeInformation>
							//
							//							 */

							if(element.getNodeName().equals("NegativeInformation")) {
								NodeList courtData = element.getChildNodes();
								for(int i = 0; i < courtData.getLength(); i++) {
									Element courtItem = (Element) courtData.item(i);
									if(courtItem != null && courtItem.getNodeName().equals("CourtData")) {
										// in surseance or bankruptcy
										hasCourtData = true;

										Element facts = getElementFromSubNode(courtItem, "Facts");
										if(facts != null) {
											NodeList factItems = facts.getChildNodes();
											for(int j = 0; j < factItems.getLength(); j++) {
												Element factItem = (Element) factItems.item(j);
												if(factItem.getNodeName().equals("CourtAction")) {
													// surseance or
													courtAction = getStringFromElement(factItem);
												} else if(factItem.getNodeName().equals("DateOfBankruptcy")) {
													// bankruptcy
													bankruptcy = true;
													bankruptcyDate = getDateFromElement(factItem);

												} else if(factItem.getNodeName().equals("Details")) {
													// do nothing
												}
											}
										}
										
										// curator
										Element receiverdetails = getElementFromSubNode(courtItem, "ReceiverDetails");
										if (receiverdetails != null) {
											curator = new CIKvKCurator();
											NodeList receiverdetailsItems = receiverdetails.getChildNodes();
											for(int j = 0; j < receiverdetailsItems.getLength(); j++) {
												Element receiverdetailsItem = (Element) receiverdetailsItems.item(j);
												if(receiverdetailsItem.getNodeName().equals("Name")) {
													curator.setNaam(getStringFromElement(receiverdetailsItem));
												} else if(receiverdetailsItem.getNodeName().equals("Address")) {
													curator.setAdres(getStringFromElement(receiverdetailsItem));
												}
											}		
											if (curator.getNaam() == null) 
												curator.setNaam("onbekend");
											curators.add(curator);
										}
									}
								}

								//result.setAanvangFaillissement(kvkDossier.getAANVFAILL());
								//result.setAanvangSurseance(kvkDossier.getAANVSURS());
								//result.setEindeFaillissement(kvkDossier.getEINDEFAIL());
								//result.setEindeSurseance(kvkDossier.getEINDESURS());

							}

							//if (true) {
							//	NegativeInformation neginfo;
							//}
						}
					}
					
					// Actually there may be only one
					result.setCIKvKCurators(curators);

					result.setIndicatieFaillissement("N");
					result.setIndicatieSurseance("N");
					if(hasCourtData && creditSafeStatus.getOmschrijving().equals(ECreditSafeStatusType.RECHTZAKEN.getOmschrijving())) {
						SimpleDateFormat formatDate = new SimpleDateFormat("yyyyMMdd");

						if(courtAction != null) {
							String ceasedOrBankruptcyDate = null;
							if(bankruptcyDate != null) ceasedOrBankruptcyDate = formatDate.format(bankruptcyDate);
							else if(ceasedTradingDate != null)
								ceasedOrBankruptcyDate = formatDate.format(ceasedTradingDate);
							else ceasedOrBankruptcyDate = "0";

							//ECourtAction eCourtAction = ECourtAction.get(courtAction);
							
							//if(eCourtAction.equals(ECourtAction.IN_FAILLISSEMENT)) {
								result.setIndicatieFaillissement("J");
								result.setAanvangFaillissement(ceasedOrBankruptcyDate);
							//} else if(eCourtAction.equals(ECourtAction.IN_SURSEANCE)) {
								result.setIndicatieSurseance("J");
								result.setAanvangSurseance(ceasedOrBankruptcyDate);
							//} else if (eCourtAction.equals(ECourtAction.FAILLIET)) {
								result.setIndicatieFaillissement("J");
								result.setAanvangFaillissement(ceasedOrBankruptcyDate);
							//} else
								ioLogger.warn("Company has courtdata + isPending but no valid courAction: " + result.getKvKnummer());
						} else
							ioLogger.warn("Company has courtdata + isPending but no courtaction: " + result.getKvKnummer());


					} else if(ceasedTradingDate != null) {
						// if there is court data + court action but status is not pending check for bankruptcy
						
						if(hasCourtData && courtAction != null) {
							//ECourtAction eCourtAction = ECourtAction.get(courtAction);

							//if(eCourtAction.equals(ECourtAction.FAILLIET)) {
								result.setIndicatieFaillissement("J");
							//}
						}

						//result.setDatumOntbinding(ceasedTradingDate);
						result.setDatumOpheffing(ceasedTradingDate); // is this correct?
						//result.setDatumUitschrijving(ceasedTradingDate);
					} else if (creditSafeStatus != null && creditSafeStatus.getOmschrijving() != null && creditSafeStatus.getOmschrijving().equals(ECreditSafeStatusType.NIETACTIEF.getOmschrijving()) &&
							result.getDatumInschrijving() != null) {
						result.setDatumOpheffing(result.getDatumInschrijving()); // work around. No ceasedTrading date but NOT ACTIVE. So set ceasedTradingDate on Start date.
					}


					if(companyReport.getGroupStructure() != null) {
						Integer nrOfDochters = new Integer(0);

						if(companyReport.getGroupStructure().getAffiliatedCompanies() != null && companyReport.getGroupStructure().getAffiliatedCompanies().getAffiliatedCompany() != null) {
							nrOfDochters = companyReport.getGroupStructure().getAffiliatedCompanies().getAffiliatedCompany().size();
						}

						if(companyReport.getGroupStructure().getSubsidiaryCompanies() != null && companyReport.getGroupStructure().getSubsidiaryCompanies().getSubsidiary() != null) {
							nrOfDochters += companyReport.getGroupStructure().getSubsidiaryCompanies().getSubsidiary().size();
						}

						if(nrOfDochters != null) result.setNrDochters(nrOfDochters);

						//if(companyReport.getGroupStructure().getUltimateParent() != null) {
						//	if(companyReport.getGroupStructure().getUltimateParent().getRegistrationNumber() != null)
						//		result.setUltimateParent(companyReport.getGroupStructure().getUltimateParent().getRegistrationNumber());
						//}
					}
					//result.setFc(kvkDossier.getFC());
					//result.setGemeente(kvkDossier.getGEMEENTE());

					//result.setHn1x2x30(kvkDossier.getHN1X2X30());
					//result.setHn1x30(kvkDossier.getHN1X30());
					//result.setHn1x45(kvkDossier.getHN1X45());
					//result.setHn2x2x30(kvkDossier.getHN2X2X30());

					//result.setProvincie(kvkDossier.getPROVINCIE());
					//result.setRedenOpheffing(kvkDossier.getREDOPHEFF());
					//result.setRedenUitschrijving(kvkDossier.getREDUITSCH());
					//result.setCodeSexe(kvkDossier.getSEXECODE());
					//result.setSoortOntbinding(kvkDossier.getSRTONTBIND());
					//result.setVenNaam(kvkDossier.getVENNAAM());
					//result.setVestigingsNummer(kvkDossier.getVESTIGINGSNR());
					//result.setVoorletters(kvkDossier.getVRLETTERS());
					//result.setVoorvoegsel(kvkDossier.getVRVOEGSEL());
					//result.setVsi(kvkDossier.getVSI());
					//result.setWkpTot(kvkDossier.getWKPTOT());
					//result.setTurnover(kvkDossier.getTURNOVER());
					//result.setProfit(kvkDossier.getPROFIT());
					//result.setCiSbi1(kvkDossier.getCISBI1());
					//result.setCiSbi2(kvkDossier.getCISBI2());
					//result.setCiSbi3(kvkDossier.getCISBI3());
					//result.setParent(kvkDossier.getPARENT());
				}
			}
		} catch(Exception e) {
			ioLogger.error("Error in resolving CS data to CIKvKDossier: " + e.getMessage(), e);
		}
		return result;
	}

	public static CIKvKDossierHistorie transformToCIKvKDossierHistorie(CIKvKDossier dossier) throws IllegalAccessException, InvocationTargetException {
		ioLogger.debug("Method transformTotransformToCIKvKDossierHistorie.");
		CIKvKDossierHistorie historieDossier = null;

		if(dossier != null) {
			historieDossier = new CIKvKDossierHistorie();

			BeanUtilsBean.getInstance().getConvertUtils().register(false, false, 0);
			BeanUtils.copyProperties(historieDossier, dossier);

			Set<CIKvKHistorieBestuurder> historieBestuurders = new HashSet<>();
			Set<CIKvKHistorieCurator> historieCurators = new HashSet<>();			
			Set<CIKvKHistorieAandeelhouder> historieAandeelhouders = new HashSet<>();
			
			for(CIKvKBestuurder b : dossier.getCIKvKBestuurders()) {
				historieBestuurders.add(transfromToCIKvKHistorieBestuurder(b));
			}
			
			for (CIKvKCurator c : dossier.getCIKvKCurators()) {
				historieCurators.add(transformToCIKvKHistorieCurator(c));
			}
			
			for (CIKvKAandeelhouder a : dossier.getCIKvKAandeelhouders()) {
				historieAandeelhouders.add(transformToCIKvKHistorieAandeelhouders(a));
			}

			historieDossier.setCIKvKHistorieBestuurders(historieBestuurders);
			historieDossier.setCIKvKHistorieCurators(historieCurators);
			historieDossier.setCIKvKHistorieAandeelhouders(historieAandeelhouders);
		}

		return historieDossier;
	}

	/**
	 * Transforms a List of n InspubWebserviceInsolvente.Insolvente Object to a CompanyInfo Object.
	 * <p/>
	 * The List of CompanyInfo recs created
	 *
	 * @return A List of CompanyInfo Objects or null when the transformation fails.
	 */
	public static List<CompanyInfo> transformToCompanyInfoList(Companies searchResults) {

		ioLogger.debug("Method transformToCompanyInfoList.");
		List<CompanyInfo> result = null;


		if(searchResults != null && searchResults.getCompany() != null && searchResults.getCompany() != null) {
			result = new ArrayList<CompanyInfo>(searchResults.getCompany().size());

			List<Company> companiesResults = searchResults.getCompany();
			CompanyInfo companyInfo;

			for(Company searchResult : companiesResults) {
				companyInfo = new CompanyInfo();
				companyInfo.setBedrijfsNaam(searchResult.getName()); //searchResult.getHANDELSNAAM());
				if(searchResult.getAddress() != null) {
					AddressData address = searchResult.getAddress();
					companyInfo.setHuisNummer(address.getHouseNumber()); //searchResult.getHUISNR());
					companyInfo.setStraat(address.getStreet()); //searchResult.getSTRAATNAAM());
					companyInfo.setPlaats(address.getCity()); //searchResult.getWOONPLAATS());
					companyInfo.setPostcode(address.getPostalCode());
				}

				String kvknumber = searchResult.getRegistrationNumber();
				String kvknumberlong = findKvkNummer12(searchResult.getId());
				String sub = null;
				if(kvknumberlong != null) {
					if(kvknumberlong.length() > 8)
						sub = kvknumberlong.substring(8).trim().equals("") ? null : kvknumberlong.substring(8);
						// MBR subdossier issue 16-2-2016 sub = kvknumberlong.substring(8).replace('0', ' ').trim().equals("") ? null : kvknumberlong.substring(8); //kvknumberlong.substring(8);
				}

				//companyInfo.setKvKnummer(kvknumber);
				companyInfo.setKvKnummer(kvknumber);
				//companyInfo.setKvKnummerVerkort(kvknumber);
				companyInfo.setSub(sub);
				//companyInfo.setVestiging(searchResult.getVESTIGINGSNR());

				// ID for fetch dossier results
				companyInfo.setCreditSafeId(searchResult.getId());
				if(searchResult.getOfficeType() != null && searchResult.getOfficeType().equals(OfficeType.HEAD_OFFICE))
					companyInfo.setCreditSafeHeadQuarters("H");
				else companyInfo.setCreditSafeHeadQuarters("N");

				if(searchResult.getStatus() != null) {
					try {
						companyInfo.setCreditSafeStatus(ECreditSafeStatusType.findBySearchCode(searchResult.getStatus()).getOmschrijving()); // SearchCode
					} catch(Exception e) {
						ioLogger.warn("Cannot classify CreditSafeStatus: '" + searchResult.getStatus() + "' of kvknr: " + kvknumber);
						companyInfo.setCreditSafeStatus(ECreditSafeStatusType.NIETACTIEF.getOmschrijving());
					}
				}

				//ioLogger.debug("CreditSafeHq: " + companyInfo.getCreditSafeHeadQuarters());
				// Only companies with valid KvK number may be added 8 or 12 long + numbers only
				if(SearchUtil.isKvKNumber(searchResult.getRegistrationNumber())) result.add(companyInfo);
			}

		}

		return result;
	}

	private static CIKvKHistorieBestuurder transfromToCIKvKHistorieBestuurder(CIKvKBestuurder bestuurder) throws IllegalAccessException, InvocationTargetException {
		ioLogger.debug("Method transfromToCIKvKHistorieBestuurder");
		CIKvKHistorieBestuurder historieBestuurder = null;

		if(bestuurder != null) {
			historieBestuurder = new CIKvKHistorieBestuurder();
			BeanUtilsBean.getInstance().getConvertUtils().register(false, false, 0);
			BeanUtils.copyProperties(historieBestuurder, bestuurder);

			Set<CIKvKHistorieBestuurderFunctie> historieBestuurderFuncties = new HashSet<>();

			for(CIKvKBestuurderFunctie bestuurderFunctie : bestuurder.getCIKvKBestuurderFuncties()){
				historieBestuurderFuncties.add(transformToCIKvKHistorieBestuurderFunctie(bestuurderFunctie));
			}

			historieBestuurder.setCIKvKHistorieBestuurderFuncties(historieBestuurderFuncties);
		}

		return historieBestuurder;
	}
	
	private static CIKvKHistorieBestuurderFunctie transformToCIKvKHistorieBestuurderFunctie(CIKvKBestuurderFunctie bestuurderFunctie) throws IllegalAccessException, InvocationTargetException{
		ioLogger.debug("Method transformToCIKvKHistorieBestuurderFunctie");
		CIKvKHistorieBestuurderFunctie historieBestuurderFunctie = null;

		if(bestuurderFunctie!=null){
			historieBestuurderFunctie = new CIKvKHistorieBestuurderFunctie();
			BeanUtilsBean.getInstance().getConvertUtils().register(false, false, 0);
			BeanUtils.copyProperties(historieBestuurderFunctie, bestuurderFunctie);
		}

		return historieBestuurderFunctie;
	}
	
	private static CIKvKHistorieCurator transformToCIKvKHistorieCurator(CIKvKCurator curator) throws IllegalAccessException, InvocationTargetException {
		ioLogger.debug("Method transfromToCIKvKHistorieCurator");
		CIKvKHistorieCurator historieCurator = null;

		if(curator != null) {
			historieCurator = new CIKvKHistorieCurator();
			BeanUtilsBean.getInstance().getConvertUtils().register(false, false, 0);
			BeanUtils.copyProperties(historieCurator, curator);
		}

		return historieCurator;
	}	
	
	
	private static CIKvKHistorieAandeelhouder transformToCIKvKHistorieAandeelhouders(CIKvKAandeelhouder aandeelhouder) throws IllegalAccessException, InvocationTargetException {
		ioLogger.debug("Method transfromToCIKvKHistorieAandeelhouder");
		CIKvKHistorieAandeelhouder historieAandeelhouder = null;

		if(aandeelhouder != null) {
			historieAandeelhouder = new CIKvKHistorieAandeelhouder();
			BeanUtilsBean.getInstance().getConvertUtils().register(false, false, 0);
			BeanUtils.copyProperties(historieAandeelhouder, aandeelhouder);
		}

		return historieAandeelhouder;
		
	}

	private static String findKvkNummer12(String creditSafeId) {
		String kvknr12 = null;

		if(creditSafeId != null) {
			Pattern pattern = Pattern.compile(regexpcsid);
			Matcher matcher = pattern.matcher(creditSafeId.toUpperCase());

			if(matcher.find()) {
				kvknr12 = matcher.group(1);
			}
		} else ioLogger.error("Cannot extract kvknummer12 from empty creditSafeId");


		return kvknr12;
	}

	private static Integer findNrOfEmployees(String nrOfEmployees) {
		Integer result = null;

		if(nrOfEmployees != null) {
			Pattern pattern = Pattern.compile(regexpcommentary_employees);
			Matcher matcher = pattern.matcher(nrOfEmployees);

			if(matcher.find()) {
				try {
					result = Integer.parseInt(matcher.group(1));
				} catch(NumberFormatException e) {
					ioLogger.warn("findNrOfEmployees: Cannot find integer in: '" + nrOfEmployees + "'.");
				}
			}
		} else ioLogger.error("Cannot extract kvknummer12 from empty creditSafeId");


		return result;
	}

	private static Date getDateFromElement(Element element) {
		Date result = null;
		// the format: 2008-08-15T00:00:00Z
		String pattern = "yyyy-MM-dd'T'hh:mm:ss";

		if(element != null && element.getTextContent() != null) {
			try {
				result = new SimpleDateFormat(pattern).parse(element.getTextContent());
			} catch(Exception e) {
				ioLogger.warn("getDateFromElement: Cannot parse string: " + element.getTextContent() + " to date.");
			}

			return result;
		} else {
			ioLogger.warn("getDateFromElement: Element is null or value is null.");
			return null;
		}
	}

	private static Element getElementFromSubNode(Node rootnode, String subnodename) throws XPathExpressionException {
		Element result = null;

		Node node = getNodeListFromSubNode(rootnode, subnodename);

		if(node != null) {
			result = (Element) node;
		}

		return result;
	}

	private static Node getNodeListFromSubNode(Node rootnode, String subnodename) throws XPathExpressionException {
		Node node = null;

		//    	XPath xpath = XPathFactory.newInstance().newXPath();
		//String expression = "/widgets/widget";

		//    	node = (Node) xpath.evaluate(expression, rootnode, XPathConstants.NODE);

		if(rootnode != null) {
			if(rootnode.getChildNodes() != null && rootnode.getChildNodes().getLength() > 0) {
				for(int i = 0; i < rootnode.getChildNodes().getLength(); i++)
					if(rootnode.getChildNodes().item(i).getNodeName().equals(subnodename)) {
						node = rootnode.getChildNodes().item(i);
						break;
					}

			}
		}

		return node;
	}

	private static String getStringFromElement(Element element) {
		if(element != null && element.getTextContent() != null) return element.getTextContent();
		else {
			ioLogger.warn("getDateFromElement: Element is null or value is null.");
			return null;
		}
	}

	private static void printXml(Element element) {
		Document document = element.getOwnerDocument();
		DOMImplementationLS domImplLS = (DOMImplementationLS) document.getImplementation();
		LSSerializer serializer = domImplLS.createLSSerializer();
		String str = serializer.writeToString(element);

		//ioLogger.info("XML Element: " + str);
	}

	private static Date xmlGregorianCalendarToDate(XMLGregorianCalendar xmlcal) {
		Date result = null;

		if(xmlcal != null) {
			try {
				result = xmlcal.toGregorianCalendar().getTime();

				return result;
			} catch(Exception e) {
				ioLogger.warn("XMLGregorianCalendarToDate cannot convert: " + xmlcal.toString() + " to date");
			}

			return result;
		} else return null;
	}

}
