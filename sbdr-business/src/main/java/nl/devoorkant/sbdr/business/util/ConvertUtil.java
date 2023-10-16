package nl.devoorkant.sbdr.business.util;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
import nl.devoorkant.sbdr.business.exception.ConverterException;
import nl.devoorkant.sbdr.business.service.BedrijfService;
import nl.devoorkant.sbdr.business.transfer.*;
import nl.devoorkant.sbdr.cir.data.model.CirPersoon;
import nl.devoorkant.sbdr.cir.data.model.CirPublicatie;
import nl.devoorkant.sbdr.data.model.*;
import nl.devoorkant.sbdr.data.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.ByteArrayInputStream;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Conversion utilities
 *
 * @author <a href="mailto:jmarsman@devoorkant.nl">Jeroen Marsman</a>
 */
public class ConvertUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConvertUtil.class);

	// <editor-fold desc="Misc methods">

	public static Integer convertStringToInteger(String src) throws NumberFormatException {
		return Integer.parseInt(src);
	}

	/**
	 * Converts XML (= an array of bytes) back to its constituent object.
	 * The input array is assumed to
	 *
	 * @param bytes the byte array to convert.
	 * @return the associated object.
	 */
	public static Object convertToObject(byte[] bytes) throws ConverterException {
		Object object = null;

		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			XStream xstream = new XStream();
			object = xstream.fromXML(bis);
		} catch(XStreamException ioe) {
			throw new ConverterException("Cannot convert from xml to object", ioe);
		}
		return object;
	}

	/**
	 * Converts any object to it's XML representation based on the XStream XML conversion framework.
	 *
	 * @param object any java object
	 * @return a XML representation of the given object as a byte array or null in case XStream returns null
	 * @throws ConverterException       in case some unknown error occurs during serialization in the XStream routine.
	 * @throws IllegalArgumentException if the given object is null
	 */
	public static byte[] convertToXML(Object object) throws ConverterException {
		if(object == null) {
			throw new IllegalArgumentException("Cannot serialize object[null] to xml");
		}

		String lstrXml;
		XStream xstream;

		try {
			xstream = new XStream();
			lstrXml = xstream.toXML(object);
		} catch(Exception e) {
			throw new ConverterException("Cannot serialize object[" + object.getClass().getName() + "] to xml", e);
		}

		if(lstrXml != null) {
			return lstrXml.getBytes();
		} else {
			return null;
		}
	}

	public static XMLGregorianCalendar dateToXMLGregorianCalendar(Date date) {
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(date);

		try {
			return DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		} catch(DatatypeConfigurationException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static Date dateWithoutTime(Date date) {
		Calendar now = Calendar.getInstance();
		if(date != null) now.setTime(date);
		now.set(Calendar.HOUR_OF_DAY, 0);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, 0);

		return now.getTime();
	}

	public static String dayMonthFromTime(Date fulldate) {
		Calendar datum = Calendar.getInstance();
		datum.setTime(fulldate);

		int day = datum.get(Calendar.DATE);
		int month = datum.get(Calendar.MONTH);
		int year = datum.get(Calendar.YEAR);

		return day + "\n" + theMonth(month);

	}

	public static boolean isHoofd(String hoofdNeven) {
		boolean result = false;

		if(hoofdNeven != null) {
			if(hoofdNeven.equals(EBedrijfType.HOOFD.getCode())) result = true;
		} else result = true; // may not occur?

		return result;

	}

	public static String monthYearFromTime(Date fulldate) {
		Calendar datum = Calendar.getInstance();
		datum.setTime(fulldate);

		String month = theMonth(datum.get(Calendar.MONTH));
		int year = datum.get(Calendar.YEAR);

		return month + "\n" + year;
	}

	private static String theMonth(int month) {
		String[] monthNames = {"jan", "feb", "mar", "apr", "mei", "jun", "jul", "aug", "sep", "okt", "nov", "dec"};
		return monthNames[month];
	}

	//</editor-fold>

	//<editor-fold desc="Object conversion eligibility">

	private static boolean meldingIsEligibleForConversion(Melding m) {
		return m.getBedrijfByMeldingOverBedrijfId() != null;
	}

	//</editor-fold>

	// <editor-fold desc="PageTransfer methods">

	public static PageTransfer<BriefBatchTransfer> convertBriefBatchPageToBriefBatchPageTransfer(Page<BriefBatch> page){
		PageTransfer<BriefBatchTransfer> result = new PageTransfer<>();
		List<BriefBatchTransfer> resultBriefBatch = new ArrayList<>();

		if(page != null) {
			result.setNumber(page.getNumber());
			result.setSize(page.getSize());
			result.setTotalPages(page.getTotalPages());
			result.setNumberOfElements(page.getNumberOfElements());
			result.setTotalElements(page.getTotalElements());
			result.setHasPreviousPage(page.hasPrevious());
			result.setFirstPage(page.isFirst());
			result.setHasNextPage(page.hasNext());
			result.setLastPage(page.isLast());
			result.setHasContent(page.hasContent());
			result.setSort(page.getSort());

			if(page.hasContent()) {
				for(BriefBatch bB : page.getContent()){
					BriefBatchTransfer bBT = createBriefBatchTransferFromBriefBatch(bB);
					resultBriefBatch.add(bBT);
				}

				result.setContent(resultBriefBatch);
			}
		}

		return result;
	}

	public static PageTransfer<InternalProcessTransfer> convertInternalProcessPageToInternalProcessPageTransfer(Page<InternalProcess> page) throws IllegalAccessException, InvocationTargetException {
		PageTransfer<InternalProcessTransfer> result = new PageTransfer<>();
		List<InternalProcessTransfer> resultInternalProcesses = new ArrayList<>();

		if(page != null) {
			result.setNumber(page.getNumber());
			result.setSize(page.getSize());
			result.setTotalPages(page.getTotalPages());
			result.setNumberOfElements(page.getNumberOfElements());
			result.setTotalElements(page.getTotalElements());
			result.setHasPreviousPage(page.hasPrevious());
			result.setFirstPage(page.isFirst());
			result.setHasNextPage(page.hasNext());
			result.setLastPage(page.isLast());
			result.setHasContent(page.hasContent());
			result.setSort(page.getSort());

			if(page.hasContent()) {
				for(InternalProcess iP : page.getContent()) {
					InternalProcessTransfer iPT = createInternalProcessTransferFromInternalProcess(iP);
					resultInternalProcesses.add((iPT));
				}

				result.setContent(resultInternalProcesses);
			}
		}

		return result;
	}

	public static PageTransfer<SupportTransfer> convertObjectPageToSupportPageTransfer(Page<Object[]> page) throws IllegalAccessException, InvocationTargetException {
		PageTransfer<SupportTransfer> result = new PageTransfer<>();
		List<SupportTransfer> resultSupports = new ArrayList<>();

		if(page != null) {
			result.setNumber(page.getNumber());
			result.setSize(page.getSize());
			result.setTotalPages(page.getTotalPages());
			result.setNumberOfElements(page.getNumberOfElements());
			result.setTotalElements(page.getTotalElements());
			result.setHasPreviousPage(page.hasPrevious());
			result.setFirstPage(page.isFirst());
			result.setHasNextPage(page.hasNext());
			result.setLastPage(page.isLast());
			result.setHasContent(page.hasContent());
			result.setSort(page.getSort());

			if(page.hasContent()) {
				for(Object[] o : page.getContent()) {
					Integer sId = (Integer) o[0];
					String sType = (String) o[1];
					Melding melding = (Melding) o[2];
					String sReden = (String) o[3];
					String bericht = (String) o[4];
					String ref = (String) o[5];
					Date dGemaakt = (Date) o[6];
					Date dUpdate = (Date) o[7];
					String sStatus = (String) o[8];
					Integer sPId = (Integer) o[9];
					Gebruiker gebruiker = (Gebruiker) o[10];
					Gebruiker geslotenDoorGebruiker = (Gebruiker) o[11];
					Boolean betwist = (Boolean) o[12];
					Integer bId = (Integer) o[13];
					String mRef = (String) o[14];
					String mBNaam = (String) o[15];

					try {
						Support s = new Support();
						s.setSupportId(sId);
						s.setSupportTypeCode(sType);
						s.setSupportStatusCode(sStatus);
						s.setGebruikerByGebruikerId(gebruiker);
						s.setBericht(bericht);
						s.setReferentieNummer(ref);
						s.setDatumAangemaakt(dGemaakt);
						s.setBetwistBezwaar(betwist);

						if(geslotenDoorGebruiker != null)
							s.setGebruikerByGeslotenDoorGebruikerId(geslotenDoorGebruiker);
						if(sPId != null) s.setSupportSupportId(sPId);
						if(melding != null) s.setMelding(melding);
						if(bId != null) s.setBedrijfBedrijfId(bId);
						if(sReden != null) s.setSupportRedenCode(sReden);
						if(dUpdate != null) s.setDatumUpdate(dUpdate);

						SupportTransfer sT = createSupportTransferFromSupport(s, mRef, mBNaam);
						resultSupports.add(sT);
					} catch(Exception e) {
						throw new IllegalAccessException(e.getMessage());
					}
				}

				result.setContent(resultSupports);
			}
		}
		return result;
	}

	public static PageTransfer<AdminAlertTransfer> convertPageToAdminAlertPageTransfer(Page<Object[]> p) throws IllegalAccessException, InvocationTargetException {
		PageTransfer<AdminAlertTransfer> r = new PageTransfer<>();
		List<AdminAlertTransfer> aAl = new ArrayList<>();

		if(p != null) {
			r.setNumber(p.getNumber());
			r.setSize(p.getSize());
			r.setTotalPages(p.getTotalPages());
			r.setNumberOfElements(p.getNumberOfElements());
			r.setTotalElements(p.getTotalElements());
			r.setHasPreviousPage(p.hasPrevious());
			r.setFirstPage(p.isFirst());
			r.setHasNextPage(p.hasNext());
			r.setLastPage(p.isLast());
			r.setHasContent(p.hasContent());
			r.setSort(p.getSort());

			if(p.hasContent()) {
				for(Object[] o : p.getContent()) {
					try {
						Integer aId = (Integer) o[0];
						Date d = (Date) o[1];
						Integer mId = (Integer) o[2];
						String refNoPfx = (String) o[3];
						Support s = (Support) o[4];

						String ref = EReferentieInternType.SUPPORT.getPrefix() + refNoPfx;

						AdminAlertTransfer aA = createAdminAlertTransferFromAdminAlert(aId, d, mId, ref, refNoPfx, s);

						aAl.add(aA);
					} catch(Exception e) {
						throw new IllegalAccessException(e.getMessage());
					}
				}

				r.setContent(aAl);
			}
		}

		return r;
	}

	// addCompanyOnce is 'true' for mobile. It then only shows a notified company once.
	public static PageTransfer<AlertOverviewTransfer> convertPageToAlertOverviewPageTransfer(Page<Object[]> page, boolean addCompanyOnce) throws IllegalAccessException, InvocationTargetException {
		PageTransfer<AlertOverviewTransfer> result = new PageTransfer<>();
		List<AlertOverviewTransfer> resultalerts = new ArrayList<>();

		if(page != null) {
			result.setNumber(page.getNumber());
			result.setSize(page.getSize());
			result.setTotalPages(page.getTotalPages());
			result.setNumberOfElements(page.getNumberOfElements());
			result.setTotalElements(page.getTotalElements());
			result.setHasPreviousPage(page.hasPrevious());
			result.setFirstPage(page.isFirst());
			result.setHasNextPage(page.hasNext());
			result.setLastPage(page.isLast());
			result.setHasContent(page.hasContent());
			result.setSort(page.getSort());

			if(page.hasContent()) {
				try {
					//BedrijfMelding x = (BedrijfMelding) page.getContent().get(0);
				} catch(Exception e) {
					LOGGER.error(e.getMessage());
				}

				List<Integer>bedrijfIds = new ArrayList<Integer>();
				
				for(Object[] alerto : page.getContent()) {
					try {
						//						0" a.type,\n" +
						//			    		1"	b, \n" +
						//			    		2"	a.melding_id,\n" +
						//			    		3"	a.monitoring_id,\n" +
						//			    		4"	a.ticket_id,\n" +
						//			    		5"	a.referentieNummer,\n" +
						//			    		6"	a.meldingStatus,\n" +
						//			    		7"	a.datum,\n" +
						//			    		8"	a.aanvangFaillissement, \n" +
						//			    		9"	a.indicatieFaillissement, \n" +
						//			    		10"	a.aanvangSurseance, \n" +
						//			    		11"	a.indicatieSurseance, \n" +
						//			    		12"	a.indEigenBedrijf, \n" +
						//			    		13"	a.indMonitoringBedrijf\n" +
						
						Integer alertId = (Integer) alerto[0];
						String type = (String) alerto[1];
						Bedrijf bedrijf = (Bedrijf) alerto[2];
						Integer meldingId = (Integer) alerto[3];
						Integer monitoringId = (Integer) alerto[4];
						Integer ticketId = (Integer) alerto[5];

						String referentieInternNoPrefix = (String) alerto[6];
						String referentieIntern = referentieInternNoPrefix;

						String supportType = (String) alerto[7];
						
						if(referentieIntern != null) {
							if(type.equals(EAlertType.VERMELDING.getCode()))
								referentieIntern = EReferentieInternType.VERMELDING.getPrefix() + referentieIntern;
							else if(type.equals(EAlertType.MONITORING.getCode()))
								referentieIntern = EReferentieInternType.MONITORING.getPrefix() + referentieIntern;
							else if(type.equals(EAlertType.SUPPORT.getCode()))
								referentieIntern = EReferentieInternType.SUPPORT.getPrefix() + referentieIntern;
							else if (type.equals(EAlertType.CONTACT_MOMENT.getCode()))
								referentieIntern = EReferentieInternType.CONTACT_MOMENT.getPrefix() + referentieIntern;
						}

						String meldingStatusCode = (String) alerto[8];
						Date datumAlert = (Timestamp) alerto[9];
						String aanvangFaillissement = (String) alerto[10];
						String indFaillissement = (String) alerto[11];

						String aanvangSurseance = (String) alerto[12];
						String indSurseance = (String) alerto[13];
						String indEigenBedrijf = "N";
						
						// @Query finder CASE result is Integer, DBView CASE result is Long
						if (alerto[14] instanceof Long) {
							if((long) alerto[14] == 1) indEigenBedrijf = "J";
						}
						else if (alerto[14] instanceof Integer) {
							if((int) alerto[14] == 1) indEigenBedrijf = "J";
						} else
							indEigenBedrijf = "N";

						// @Query finder CASE result is Integer, DBView CASE result is Long
						String indMonitoringBedrijf = "J";
						if (alerto[15] instanceof Long) {
							if((long) alerto[15] == 0) indMonitoringBedrijf = "N";
						}
						else if (alerto[14] instanceof Integer) {
							if((int) alerto[15] == 0) indMonitoringBedrijf = "N";
						} else
							indMonitoringBedrijf = "N";						

						Boolean isAdresOk = bedrijf.isAdresOk();
						Boolean handmatigGewijzigd = bedrijf.isHandmatigGewijzigd();
						String adresStatus = "Adres correct";
						if(!isAdresOk) {
							if(!handmatigGewijzigd) adresStatus = "Adres niet juist!";
							else adresStatus = "Adres niet juist + bedrijf aangepast";
						} else if(!handmatigGewijzigd) adresStatus = "Bedrijf aangepast";

						if (!bedrijfIds.contains(bedrijf.getBedrijfId()) || !addCompanyOnce) {
							AlertOverviewTransfer alertTransfer = createAlertOverviewTransferFromAlert(alertId, type, supportType, bedrijf, meldingId, monitoringId, ticketId, referentieIntern, meldingStatusCode, datumAlert, aanvangFaillissement + indFaillissement, aanvangSurseance + indSurseance, indEigenBedrijf, indMonitoringBedrijf, adresStatus, referentieInternNoPrefix);
	
							resultalerts.add(alertTransfer);
							
							bedrijfIds.add(bedrijf.getBedrijfId());
						}
						
					} catch(Exception e) {
						throw new IllegalAccessException(e.getMessage());
					}
				}

				result.setContent(resultalerts);
			}
		}

		return result;

	}

	public static PageTransfer<ExceptionBedrijfOverviewTransfer> convertPageToExceptionBedrijfOverviewPageTransfer(Page<Object[]> page) throws IllegalAccessException, InvocationTargetException {
		PageTransfer<ExceptionBedrijfOverviewTransfer> result = new PageTransfer<>();
		List<ExceptionBedrijfOverviewTransfer> resultexceptionbedrijven = new ArrayList<>();

		if(page != null) {
			result.setNumber(page.getNumber());
			result.setSize(page.getSize());
			result.setTotalPages(page.getTotalPages());
			result.setNumberOfElements(page.getNumberOfElements());
			result.setTotalElements(page.getTotalElements());
			result.setHasPreviousPage(page.hasPrevious());
			result.setFirstPage(page.isFirst());
			result.setHasNextPage(page.hasNext());
			result.setLastPage(page.isLast());
			result.setHasContent(page.hasContent());
			result.setSort(page.getSort());

			if(page.hasContent()) {
				for(Object[] object : page.getContent()) {
					Bedrijf bedrijf = (Bedrijf) object[0];
					CustomMelding customMelding = (CustomMelding) object[1];
					Bedrijf bedrijfGemeldDoor = (Bedrijf) object[2];

					boolean isKlant = false;
					boolean isProspect = false;
					boolean isAdresOk = true;

					ExceptionBedrijfOverviewTransfer exceptionBedrijfTransfer = createExceptionBedrijfOverviewTransferFromBedrijf(bedrijf, bedrijfGemeldDoor, customMelding, isKlant, isProspect, isAdresOk, null);

					resultexceptionbedrijven.add(exceptionBedrijfTransfer);
				}

				result.setContent(resultexceptionbedrijven);
			}
		}

		return result;
	}

	public static PageTransfer<FactuurTransfer> convertPageToFactuurPageTransfer(Page<Factuur> p) throws IllegalAccessException, InvocationTargetException {
		PageTransfer<FactuurTransfer> fTp = new PageTransfer<>();
		List<FactuurTransfer> fTl = new ArrayList<>();

		if(p != null) {
			fTp.setNumber(p.getNumber());
			fTp.setSize(p.getSize());
			fTp.setTotalPages(p.getTotalPages());
			fTp.setNumberOfElements(p.getNumberOfElements());
			fTp.setTotalElements(p.getTotalElements());
			fTp.setHasPreviousPage(p.hasPrevious());
			fTp.setFirstPage(p.isFirst());
			fTp.setHasNextPage(p.hasNext());
			fTp.setLastPage(p.isLast());
			fTp.setHasContent(p.hasContent());
			fTp.setSort(p.getSort());

			if(p.hasContent()) {
				for(Factuur f : p.getContent()) {
					FactuurTransfer fT = createFactuurTransferFromFactuur(f);
					fTl.add(fT);
				}

				fTp.setContent(fTl);
			}
		}

		return fTp;
	}

	public static PageTransfer<FaillissementenOverviewTransfer> convertPageToFaillissementenOverviewPageTransfer(Page<Object[]> page) throws IllegalAccessException, InvocationTargetException {
		PageTransfer<FaillissementenOverviewTransfer> result = new PageTransfer<>();
		List<FaillissementenOverviewTransfer> resultFaillissementen = new ArrayList<>();

		if(page != null) {
			result.setNumber(page.getNumber());
			result.setSize(page.getSize());
			result.setTotalPages(page.getTotalPages());
			result.setNumberOfElements(page.getNumberOfElements());
			result.setTotalElements(page.getTotalElements());
			result.setHasPreviousPage(page.hasPrevious());
			result.setFirstPage(page.isFirst());
			result.setHasNextPage(page.hasNext());
			result.setLastPage(page.isLast());
			result.setHasContent(page.hasContent());
			result.setSort(page.getSort());

			if(page.hasContent()) {
				for(Object[] object : page.getContent()) {
					Integer insolventieId = (Integer) object[0];
					CirPublicatie publicatie = (CirPublicatie) object[1];
					//CirHandelsnaam handelsnaam = (CirHandelsnaam) object[2];
					CirPersoon persoon = (CirPersoon) object[2];

					FaillissementenOverviewTransfer faillissementenOverviewTransfer = null;
					if(publicatie.getCirPublicatiesoort() != null)
						//faillissementenOverviewTransfer = new FaillissementenOverviewTransfer(insolventieId, publicatie.getId(), publicatie.getDatumPublicatie(), publicatie.getPublicatieKenmerk(), publicatie.getPublicatieOmschrijving(), handelsnaam.getId(), handelsnaam.getHandelsnaam(), handelsnaam.getNummerKvK(), publicatie.getCirPublicatiesoortCode(), publicatie.getCirPublicatiesoort().getOmschrijving());
						faillissementenOverviewTransfer = new FaillissementenOverviewTransfer(insolventieId, publicatie.getId(), publicatie.getDatumPublicatie(), publicatie.getPublicatieKenmerk(), publicatie.getPublicatieOmschrijving(), persoon.getId(), persoon.getNaam(), persoon.getNummerKvK(), publicatie.getCirPublicatiesoortCode(), publicatie.getCirPublicatiesoort().getOmschrijving());
					else
						//faillissementenOverviewTransfer = new FaillissementenOverviewTransfer(insolventieId, publicatie.getId(), publicatie.getDatumPublicatie(), publicatie.getPublicatieKenmerk(), publicatie.getPublicatieOmschrijving(), handelsnaam.getId(), handelsnaam.getHandelsnaam(), handelsnaam.getNummerKvK(), publicatie.getCirPublicatiesoortCode(), null);
						faillissementenOverviewTransfer = new FaillissementenOverviewTransfer(insolventieId, publicatie.getId(), publicatie.getDatumPublicatie(), publicatie.getPublicatieKenmerk(), publicatie.getPublicatieOmschrijving(), persoon.getId(), persoon.getNaam(), persoon.getNummerKvK(), publicatie.getCirPublicatiesoortCode(), null);
					resultFaillissementen.add(faillissementenOverviewTransfer);
				}

				result.setContent(resultFaillissementen);
			}
		}

		return result;
	}

	public static PageTransfer<GebruikerTransfer> convertPageToGebruikerPageTransfer(Page<Gebruiker> page, Bedrijf bedrijf, boolean isProspect, boolean isAdresOk, String bedrijfTelefoonNummer) throws IllegalAccessException, InvocationTargetException {
		PageTransfer<GebruikerTransfer> result = new PageTransfer<>();
		List<GebruikerTransfer> resultgebruikers = new ArrayList<>();

		if(page != null) {
			result.setNumber(page.getNumber());
			result.setSize(page.getSize());
			result.setTotalPages(page.getTotalPages());
			result.setNumberOfElements(page.getNumberOfElements());
			result.setTotalElements(page.getTotalElements());
			result.setHasPreviousPage(page.hasPrevious());
			result.setFirstPage(page.isFirst());
			result.setHasNextPage(page.hasNext());
			result.setLastPage(page.isLast());
			result.setHasContent(page.hasContent());
			result.setSort(page.getSort());

			if(page.hasContent()) {
				Rol klantrol = ERol.KLANT.getRolObject(true);
				boolean isKlant;

				for(Gebruiker gebruiker : page.getContent()) {
					isKlant = CompareUtil.containsRol(gebruiker.getRollen(), klantrol);
					GebruikerTransfer gebruikerTransfer = createGebruikerTransferFromGebruiker(gebruiker, bedrijf, bedrijfTelefoonNummer, isKlant, isProspect, isAdresOk, false, false);

					resultgebruikers.add(gebruikerTransfer);
				}

				result.setContent(resultgebruikers);
			}
		}

		return result;
	}

	public static PageTransfer<KlantBedrijfOverviewTransfer> convertPageToKlantBedrijfOverviewPageTransfer(Page<Object[]> page) throws IllegalAccessException, InvocationTargetException {
		PageTransfer<KlantBedrijfOverviewTransfer> result = new PageTransfer<>();
		List<KlantBedrijfOverviewTransfer> resultklantbedrijven = new ArrayList<>();

		if(page != null) {
			result.setNumber(page.getNumber());
			result.setSize(page.getSize());
			result.setTotalPages(page.getTotalPages());
			result.setNumberOfElements(page.getNumberOfElements());
			result.setTotalElements(page.getTotalElements());
			result.setHasPreviousPage(page.hasPrevious());
			result.setFirstPage(page.isFirst());
			result.setHasNextPage(page.hasNext());
			result.setLastPage(page.isLast());
			result.setHasContent(page.hasContent());
			result.setSort(page.getSort());

			if(page.hasContent()) {
				Rol klantrol = ERol.KLANT.getRolObject(true);
				boolean isKlant = false;

				for(Object[] object : page.getContent()) {
					Bedrijf bedrijf = (Bedrijf) object[0];
					Klant klant = (Klant) object[1];

					KlantBedrijfOverviewTransfer klantBedrijfTransfer = createKlantBedrijfOverviewTransferFromBedrijf(bedrijf, klant);

					resultklantbedrijven.add(klantBedrijfTransfer);
				}

				result.setContent(resultklantbedrijven);
			}
		}

		return result;
	}

	public static PageTransfer<MeldingOverviewTransfer> convertPageToMeldingOverviewPageTransfer(Page<Melding> page) throws IllegalAccessException, InvocationTargetException {
		PageTransfer<MeldingOverviewTransfer> result = new PageTransfer<>();
		List<MeldingOverviewTransfer> resultalerts = new ArrayList<>();

		if(page != null) {
			result.setNumber(page.getNumber());
			result.setSize(page.getSize());
			result.setTotalPages(page.getTotalPages());
			result.setNumberOfElements(page.getNumberOfElements());
			result.setTotalElements(page.getTotalElements());
			result.setHasPreviousPage(page.hasPrevious());
			result.setFirstPage(page.isFirst());
			result.setHasNextPage(page.hasNext());
			result.setLastPage(page.isLast());
			result.setHasContent(page.hasContent());
			result.setSort(page.getSort());

			if(page.hasContent()) {
				try {
					//BedrijfMelding x = (BedrijfMelding) page.getContent().get(0);
				} catch(Exception e) {
					LOGGER.error(e.getMessage());
				}

				for(Melding melding : page.getContent()) {
					try {
						MeldingOverviewTransfer meldingTransfer = createMeldingOverviewTransferFromMelding(melding);

						resultalerts.add(meldingTransfer);
					} catch(Exception e) {
						throw new IllegalAccessException(e.getMessage());
					}
				}

				result.setContent(resultalerts);
			}
		}

		return result;
	}

	public static PageTransfer<MeldingTransfer> convertPageToMeldingPageTransfer(Page<Melding> page) throws IllegalAccessException, InvocationTargetException {
		PageTransfer<MeldingTransfer> result = new PageTransfer<>();
		List<MeldingTransfer> resultalerts = new ArrayList<>();

		if(page != null) {
			result.setNumber(page.getNumber());
			result.setSize(page.getSize());
			result.setTotalPages(page.getTotalPages());
			result.setNumberOfElements(page.getNumberOfElements());
			result.setTotalElements(page.getTotalElements());
			result.setHasPreviousPage(page.hasPrevious());
			result.setFirstPage(page.isFirst());
			result.setHasNextPage(page.hasNext());
			result.setLastPage(page.isLast());
			result.setHasContent(page.hasContent());
			result.setSort(page.getSort());

			if(page.hasContent()) {
				try {
					//BedrijfMelding x = (BedrijfMelding) page.getContent().get(0);
				} catch(Exception e) {
					LOGGER.error(e.getMessage());
				}

				for(Melding melding : page.getContent()) {
					try {
						//Melding melding = (Melding) object[0];
						//Notitie notitie = (Notitie) object[1];
						Notitie notitie = (Notitie) melding.getNotitieGebruiker();
						
						MeldingTransfer meldingTransfer = createMeldingTransferFromMelding(melding, notitie);

						resultalerts.add(meldingTransfer);
					} catch(Exception e) {
						throw new IllegalAccessException(e.getMessage());
					}
				}

				result.setContent(resultalerts);
			}
		}

		return result;
	}

	public static PageTransfer<MonitoringOverviewTransfer> convertPageToMonitoringOverviewPageTransfer(Page<Object[]> page) throws IllegalAccessException, InvocationTargetException {
		PageTransfer<MonitoringOverviewTransfer> result = new PageTransfer<>();
		List<MonitoringOverviewTransfer> resultalerts = new ArrayList<>();

		if(page != null) {
			result.setNumber(page.getNumber());
			result.setSize(page.getSize());
			result.setTotalPages(page.getTotalPages());
			result.setNumberOfElements(page.getNumberOfElements());
			result.setTotalElements(page.getTotalElements());
			result.setHasPreviousPage(page.hasPrevious());
			result.setFirstPage(page.isFirst());
			result.setHasNextPage(page.hasNext());
			result.setLastPage(page.isLast());
			result.setHasContent(page.hasContent());
			result.setSort(page.getSort());

			if(page.hasContent()) {
				try {
					//BedrijfMelding x = (BedrijfMelding) page.getContent().get(0);
				} catch(Exception e) {
					LOGGER.error(e.getMessage());
				}

				for(Object[] object : page.getContent()) {
					Long countMeldingen = (Long) object[0];
					Monitoring monitoring = (Monitoring) object[1];

					try {
						MonitoringOverviewTransfer monitoringTransfer = createMonitoringOverviewTransferFromMonitoring(monitoring);

						if(countMeldingen != null && countMeldingen > 0) monitoringTransfer.setMeldingBijBedrijf(true);
						else monitoringTransfer.setMeldingBijBedrijf(false);

						resultalerts.add(monitoringTransfer);
					} catch(Exception e) {
						throw new IllegalAccessException(e.getMessage());
					}
				}

				result.setContent(resultalerts);
			}
		}

		return result;
	}

	public static PageTransfer<RemovedBedrijfOverviewTransfer> convertPageToRemovedBedrijvenOverviewPageTransfer(Page<Object[]> page, Integer bedrijfId) throws IllegalAccessException, InvocationTargetException {
		PageTransfer<RemovedBedrijfOverviewTransfer> result = new PageTransfer<>();
		List<RemovedBedrijfOverviewTransfer> resultalerts = new ArrayList<>();

		if(page != null) {
			result.setNumber(page.getNumber());
			result.setSize(page.getSize());
			result.setTotalPages(page.getTotalPages());
			result.setNumberOfElements(page.getNumberOfElements());
			result.setTotalElements(page.getTotalElements());
			result.setHasPreviousPage(page.hasPrevious());
			result.setFirstPage(page.isFirst());
			result.setHasNextPage(page.hasNext());
			result.setLastPage(page.isLast());
			result.setHasContent(page.hasContent());
			result.setSort(page.getSort());

			if(page.hasContent()) {
				try {
					//BedrijfMelding x = (BedrijfMelding) page.getContent().get(0);
				} catch(Exception e) {
					LOGGER.error(e.getMessage());
				}

				for(Object[] object : page.getContent()) {
					RemovedBedrijf removedBedrijf = (RemovedBedrijf) object[0];
					Bedrijf bedrijf = (Bedrijf) object[1];

					try {
						RemovedBedrijfOverviewTransfer monitoringTransfer = createRemovedBedrijvenOverviewTransferFromBedrijf(removedBedrijf, bedrijf, bedrijfId);

						resultalerts.add(monitoringTransfer);
					} catch(Exception e) {
						throw new IllegalAccessException(e.getMessage());
					}
				}

				result.setContent(resultalerts);
			}
		}

		return result;
	}

	public static PageTransfer<ReportRequestedTransfer> convertPageToReportRequestedPageTransfer(Page<Object[]> page, Bedrijf bedrijf, Integer gebruikerId, String bedrijfTelefoonNummer, boolean isProspect, boolean isAdresOk) throws IllegalAccessException, InvocationTargetException {
		PageTransfer<ReportRequestedTransfer> result = new PageTransfer<>();
		List<ReportRequestedTransfer> resultreports = new ArrayList<>();

		if(page != null) {
			result.setNumber(page.getNumber());
			result.setSize(page.getSize());
			result.setTotalPages(page.getTotalPages());
			result.setNumberOfElements(page.getNumberOfElements());
			result.setTotalElements(page.getTotalElements());
			result.setHasPreviousPage(page.hasPrevious());
			result.setFirstPage(page.isFirst());
			result.setHasNextPage(page.hasNext());
			result.setLastPage(page.isLast());
			result.setHasContent(page.hasContent());
			result.setSort(page.getSort());

			if(page.hasContent()) {
				try {
					//BedrijfMelding x = (BedrijfMelding) page.getContent().get(0);
				} catch(Exception e) {
					LOGGER.error(e.getMessage());
				}

				for(Object[] alerto : page.getContent()) {
					try {
						Document document = (Document) alerto[0];
						
						String documentType = (String) alerto[1];

						// GebruikerTransfer
						GebruikerTransfer gebruikerTransfer = null;
						Gebruiker gebruiker = (Gebruiker) alerto[2];
						if(gebruiker != null) {
							Rol klantrol = ERol.KLANT.getRolObject(true);
							boolean isKlant;

							isKlant = CompareUtil.containsRol(gebruiker.getRollen(), klantrol);
							gebruikerTransfer = createGebruikerTransferFromGebruiker(gebruiker, bedrijf, bedrijfTelefoonNummer, isKlant, isProspect, isAdresOk, false, false);
						}

						// BedrijfTransfer
						BedrijfTransfer bedrijfTransfer = createBedrijfTransferFromBedrijf(document.getBedrijfByRapportBedrijfId());

						String indEigenGebruiker = "N";
						if(gebruikerId != null && document.getGebruiker() != null && document.getGebruikerGebruikerId().equals(gebruikerId))
							indEigenGebruiker = "J";

						ReportRequestedTransfer reportRequestedTransfer = null;
						if (documentType.equals(EDocumentType.RAPPORT.getCode()))
							reportRequestedTransfer = createReportRequestedTransfer(document, EDocumentType.RAPPORT, gebruikerTransfer, bedrijfTransfer, indEigenGebruiker);
						else if (documentType.equals(EDocumentType.MOBILEAPPCHECK.getCode()))
							reportRequestedTransfer = createReportRequestedTransfer(document, EDocumentType.MOBILEAPPCHECK, gebruikerTransfer, bedrijfTransfer, indEigenGebruiker);

						if(reportRequestedTransfer != null) resultreports.add(reportRequestedTransfer);
					} catch(Exception e) {
						throw new IllegalAccessException(e.getMessage());
					}
				}

				result.setContent(resultreports);
			}
		}

		return result;
	}

	public static PageTransfer<SearchResultsOverviewTransfer> convertPageToSearchResultsOverviewPageTransfer(Page<Object[]> page) throws IllegalAccessException, InvocationTargetException {
		PageTransfer<SearchResultsOverviewTransfer> result = new PageTransfer<>();
		List<SearchResultsOverviewTransfer> resultalerts = new ArrayList<>();

		if(page != null) {
			result.setNumber(page.getNumber());
			result.setSize(page.getSize());
			result.setTotalPages(page.getTotalPages());
			result.setNumberOfElements(page.getNumberOfElements());
			result.setTotalElements(page.getTotalElements());
			result.setHasPreviousPage(page.hasPrevious());
			result.setFirstPage(page.isFirst());
			result.setHasNextPage(page.hasNext());
			result.setLastPage(page.isLast());
			result.setHasContent(page.hasContent());
			result.setSort(page.getSort());

			if(page.hasContent()) {
				try {
					//BedrijfMelding x = (BedrijfMelding) page.getContent().get(0);
				} catch(Exception e) {
					LOGGER.error(e.getMessage());
				}

				for(Object[] object : page.getContent()) {
					SearchResult searchResult = (SearchResult) object[0];

					Bedrijf bedrijf = (Bedrijf) object[1];
					Bedrijf bedrijfvan = null;
					if(object[2] != null) bedrijfvan = (Bedrijf) object[2];

					try {
						SearchResultsOverviewTransfer searchResultsTransfer = createSearchResultsOverviewTransfer(searchResult, bedrijf, bedrijfvan);

						resultalerts.add(searchResultsTransfer);
					} catch(Exception e) {
						throw new IllegalAccessException(e.getMessage());
					}
				}

				result.setContent(resultalerts);
			}
		}

		return result;
	}

	public static PageTransfer<SupportTransfer> convertSupportPageToSupportPageTransfer(Page<Support> page) throws IllegalAccessException, InvocationTargetException {
		PageTransfer<SupportTransfer> result = new PageTransfer<>();
		List<SupportTransfer> resultSupports = new ArrayList<>();

		if(page != null) {
			result.setNumber(page.getNumber());
			result.setSize(page.getSize());
			result.setTotalPages(page.getTotalPages());
			result.setNumberOfElements(page.getNumberOfElements());
			result.setTotalElements(page.getTotalElements());
			result.setHasPreviousPage(page.hasPrevious());
			result.setFirstPage(page.isFirst());
			result.setHasNextPage(page.hasNext());
			result.setLastPage(page.isLast());
			result.setHasContent(page.hasContent());
			result.setSort(page.getSort());

			if(page.hasContent()) {
				for(Support s : page.getContent()) {
					SupportTransfer st = createSupportTransferFromSupport(s);
					resultSupports.add(st);
				}

				result.setContent(resultSupports);
			}
		}
		return result;
	}

	//</editor-fold>

	// <editor-fold desc="Transfer methods">

	// used for creating relevant report data transfer
	public static KvkDossierTransfer convertCiKvkDossierToKvkDossierTransfer(Bedrijf bedrijf, CIKvKDossier ciKvkDossier, Klant klant) {
		KvkDossierTransfer result = null;

		// data ok?
		if(bedrijf != null && ciKvkDossier != null) {
			result = new KvkDossierTransfer();

			result.setGestortKapitaal(ciKvkDossier.getGestortKapitaal());
			result.setNominaalAandelenKapitaal(ciKvkDossier.getNominaalAandelenKapitaal());

			result.setLaatsteUpdate(ciKvkDossier.getDatumLaatsteUpdate());
			result.setSbdrNummer(bedrijf.getSbdrNummer());
			result.setKvkNummer(bedrijf.getKvKnummer());
			StringBuilder handelsNaamBuilder = new StringBuilder("");
			if(ciKvkDossier.getHandelsNaam() != null && !ciKvkDossier.getHandelsNaam().trim().isEmpty()) {
				if (handelsNaamBuilder.toString().trim().isEmpty()) {
					handelsNaamBuilder.append(ciKvkDossier.getHandelsNaam().trim());
				}else {
					handelsNaamBuilder.append(", ");
					handelsNaamBuilder.append(ciKvkDossier.getHandelsNaam().trim());
				}			
			}
			if(ciKvkDossier.getHn1x2x30() != null && !ciKvkDossier.getHn1x2x30().trim().isEmpty()) {
				if (handelsNaamBuilder.toString().trim().isEmpty()) {
					handelsNaamBuilder.append(ciKvkDossier.getHn1x2x30().trim());
				}else {
					handelsNaamBuilder.append(", ");
					handelsNaamBuilder.append(ciKvkDossier.getHn1x2x30().trim());
				}			
			}
			if(ciKvkDossier.getHn1x30() != null && !ciKvkDossier.getHn1x30().trim().isEmpty()) {
				if (handelsNaamBuilder.toString().trim().isEmpty()) {
					handelsNaamBuilder.append(ciKvkDossier.getHn1x30().trim());
				}else {
					handelsNaamBuilder.append(", ");
					handelsNaamBuilder.append(ciKvkDossier.getHn1x30().trim());
				}			
			}
			if(ciKvkDossier.getHn1x45() != null && !ciKvkDossier.getHn1x45().trim().isEmpty()) {
				if (handelsNaamBuilder.toString().trim().isEmpty()) {
					handelsNaamBuilder.append(ciKvkDossier.getHn1x45().trim());
				}else {
					handelsNaamBuilder.append(", ");
					handelsNaamBuilder.append(ciKvkDossier.getHn1x45().trim());
				}			
			}
			if(ciKvkDossier.getHn2x2x30() != null && !ciKvkDossier.getHn2x2x30().trim().isEmpty()) {
				if (handelsNaamBuilder.toString().trim().isEmpty()) {
					handelsNaamBuilder.append(ciKvkDossier.getHn2x2x30().trim());
				}else {
					handelsNaamBuilder.append(", ");
					handelsNaamBuilder.append(ciKvkDossier.getHn2x2x30().trim());
				}			
			}			
			result.setHandelsNaam(handelsNaamBuilder.toString().trim());
			if(ciKvkDossier.getVenNaam() != null) result.setVennootschapsNaam(ciKvkDossier.getVenNaam());
			else result.setVennootschapsNaam("-");
			if(ciKvkDossier.getHoofdNeven() != null) result.setHoofdNeven(ciKvkDossier.getHoofdNeven());
			else result.setHoofdNeven("H");

			result.setDatumHuidigeVestiging(ciKvkDossier.getDatumHuidigeVestiging());
			result.setParentKvKNummer(ciKvkDossier.getParent());
			result.setUltimateParentKvKNummer(ciKvkDossier.getUltimateParent());

			// Vestigingsadres
			result.setStraat(ciKvkDossier.getStraat());
			result.setHuisnummer(ciKvkDossier.getHuisnummer());
			if(ciKvkDossier.getHuisnummerToevoeging() != null)
				result.setHuisnummerToevoeging(ciKvkDossier.getHuisnummerToevoeging());
			else result.setHuisnummerToevoeging("");
			if(ciKvkDossier.getPostcode() != null) result.setPostcode(ciKvkDossier.getPostcode());
			else result.setPostcode("-");
			result.setPlaats(ciKvkDossier.getPlaats());

			// Postadres
			if(ciKvkDossier.getStraatCa() != null && ciKvkDossier.getStraatCa().equalsIgnoreCase("Postbus")) {
				if(ciKvkDossier.getStraatHuisnummerCa() != null) {
					result.setPost_postbus("Postbus " + ciKvkDossier.getStraatHuisnummerCa().trim());
				}				
				result.setPost_plaats(ciKvkDossier.getPlaatsCa());
				result.setPost_postcode(ciKvkDossier.getPostcodeCa());
			}

			result.setRsin(ciKvkDossier.getRsin());
			result.setMedewerkers(ciKvkDossier.getNrEmployees());
			if (ciKvkDossier.getNrDochters() != null)
				result.setDochters(ciKvkDossier.getNrDochters());
			else 
				result.setDochters(0);
			result.setDomeinnaam(ciKvkDossier.getDomeinNaam());
			if(ciKvkDossier.getExport() != null) result.setExportHandel(ciKvkDossier.getExport());
			else result.setExportHandel("N");
			if(ciKvkDossier.getImport_() != null) result.setImportHandel(ciKvkDossier.getImport_());
			else result.setImportHandel("N");

			SimpleDateFormat format1 = new SimpleDateFormat("yyyymmdd");

			result.setDatumInschrijving(ciKvkDossier.getDatumInschrijving());
			result.setDatumVoorzetting(ciKvkDossier.getDatumVoortzetting());
			try {
				// this is not the way it should work, no difference anymore between surseance, faillissement and failliet
				result.setIndFaillissement(ciKvkDossier.getIndicatieFaillissement());
				result.setIndSurseance(ciKvkDossier.getIndicatieSurseance());
				if(ciKvkDossier.getAanvangFaillissement() != null && !ciKvkDossier.getAanvangFaillissement().equals("") ) {
					if (!ciKvkDossier.getAanvangFaillissement().equals("0"))
						result.setDatumFaillissementStart(format1.parse(ciKvkDossier.getAanvangFaillissement()));					
					//result.setIndFaillissement("J");
				}
				if(ciKvkDossier.getAanvangSurseance() != null && !ciKvkDossier.getAanvangSurseance().equals("") && !ciKvkDossier.getAanvangSurseance().equals("0")) {
					//result.setIndSurseance("J");
					result.setDatumSurseanceStart(format1.parse(ciKvkDossier.getAanvangSurseance()));
				}
				if(ciKvkDossier.getEindeFaillissement() != null && !ciKvkDossier.getEindeFaillissement().equals("") && !ciKvkDossier.getEindeFaillissement().equals("0")) {
					result.setDatumFaillissementEinde(format1.parse(ciKvkDossier.getEindeFaillissement()));
					if(!result.getDatumFaillissementStart().after(result.getDatumFaillissementEinde()))
						result.setIndFaillissement("N");
				}
				if(ciKvkDossier.getEindeSurseance() != null && !ciKvkDossier.getEindeSurseance().equals("") && !ciKvkDossier.getEindeSurseance().equals("0")) {
					result.setDatumSurseanceEinde(format1.parse(ciKvkDossier.getEindeSurseance()));
					if(!result.getDatumSurseanceStart().after(result.getDatumSurseanceEinde()))
						result.setIndSurseance("N");
				}
			} catch(ParseException e) {
				// do nothing
			}
			result.setDatumOnbinding(ciKvkDossier.getDatumOntbinding());
			result.setDatumOpheffing(ciKvkDossier.getDatumOpheffing());
			result.setDatumUitschrijving(ciKvkDossier.getDatumUitschrijving());
			result.setDatumVoorzetting(ciKvkDossier.getDatumVoortzetting());

			// Contact KvK disable for now
			result.setKvkContact(false);
			//			if (ciKvkDossier.getCodeSexe() != null && ciKvkDossier.getCodeSexe().equals("M"))
			//				result.setAanhef("dhr.");
			//			else if (ciKvkDossier.getCodeSexe() != null && ciKvkDossier.getCodeSexe().equals("V"))
			//				result.setAanhef("mevr.");
			//			result.setVoorlettersKvk(ciKvkDossier.getVoorletters());
			//			result.setAchternaamKvk(ciKvkDossier.getAchterNaam());
			//			if (ciKvkDossier.getTelefoonNetNummer() != null && ciKvkDossier.getTelefoonAbonneeNummer() != null &&
			//				!ciKvkDossier.getTelefoonNetNummer().equalsIgnoreCase("null") && !ciKvkDossier.getTelefoonAbonneeNummer().equalsIgnoreCase("null"))
			//				result.setTelefoonKvk(ciKvkDossier.getTelefoonNetNummer() + ciKvkDossier.getTelefoonAbonneeNummer());
			//			if (ciKvkDossier.getTelefoonNummerMobiel() != null &&!ciKvkDossier.getTelefoonNummerMobiel().equalsIgnoreCase("null"))
			//				result.setMobielKvk(ciKvkDossier.getTelefoonNummerMobiel());

			result.setRedenOpheffing(ciKvkDossier.getRedenOpheffing());
			result.setRedenUitschrijving(ciKvkDossier.getRedenUitschrijving());

			if(ciKvkDossier.getDeponeringJaarstukken() != null && !ciKvkDossier.getDeponeringJaarstukken().equals(new Integer(0)))
				result.setDeponeringJaarstukken(ciKvkDossier.getDeponeringJaarstukken().toString());
			else result.setDeponeringJaarstukken("niet gedeponeerd");

			if(ciKvkDossier.getSbihoofdAct() != null) result.setHoofdactiviteitSbi(ciKvkDossier.getSbihoofdAct());

			if(ciKvkDossier.getSbinevenActiviteit1() != null)
				result.setNevenactiviteit1Sbi(ciKvkDossier.getSbinevenActiviteit1());

			if(ciKvkDossier.getSbinevenActiviteit2() != null)
				result.setNevenactiviteit2Sbi(ciKvkDossier.getSbinevenActiviteit2());

			// BTW + Contact SBDR
			if(klant != null) {
				result.setKlant(true);
				result.setBtwnummer(klant.getBtwnummer());

				if(klant.getGeslacht() != null && klant.getGeslacht().equals("M")) result.setAanhef("dhr.");
				else if(klant.getGeslacht() != null && klant.getGeslacht().equals("V")) result.setAanhef("mevr.");
				result.setVoornaam(klant.getVoornaam());
				result.setAchternaam(klant.getNaam());
				result.setTelefoon(klant.getTelefoonNummer());
				result.setEmail(klant.getEmailAdres());
				result.setFunctie(klant.getFunctie());
			} else result.setKlant(false);

			if(ciKvkDossier.getCIKvKBestuurders().size() > 0) {
				//if(ciKvkDossier.getAchterNaam() != null) {
				result.setKvkContact(true);

				HashSet<KvkBestuurderTransfer> bestuurdersTransfer = new HashSet<>();

				for(CIKvKBestuurder bestuurder : ciKvkDossier.getCIKvKBestuurders()) {
					KvkBestuurderTransfer bestuurderTransfer = new KvkBestuurderTransfer();

					String aanhef = "";
					switch(EBestuurderGeslacht.get(bestuurder.getGeslacht())) {
						case MAN: {
							aanhef = "dhr.";
							break;
						}
						case VROUW: {
							aanhef = "mevr.";
							break;
						}
						case ONBEKEND:
						default: {
							aanhef = ""; //"dhr./mevr.";
							break;
						}
					}

					bestuurderTransfer.setAanhef(aanhef);
					bestuurderTransfer.setNaam(bestuurder.getNaam().replaceAll("\\s+", " "));

					if(bestuurder.getCIKvKBestuurderFuncties() != null && bestuurder.getCIKvKBestuurderFuncties().size() > 0) {
						Set<KvkBestuurderFunctieTransfer> functiesTransfer = new HashSet<>();
						for(CIKvKBestuurderFunctie functie : bestuurder.getCIKvKBestuurderFuncties()) {
							KvkBestuurderFunctieTransfer functieTransfer = new KvkBestuurderFunctieTransfer(functie.getFunctie());
							functiesTransfer.add(functieTransfer);
						}

						bestuurderTransfer.setFuncties(functiesTransfer);
					}

					bestuurdersTransfer.add(bestuurderTransfer);
				}

				result.setKvkBestuurderTransfer(bestuurdersTransfer);

				if (ciKvkDossier.getTelefoonAbonneeNummer() != null)
					result.setTelefoonKvk(ciKvkDossier.getTelefoonAbonneeNummer());
				if(ciKvkDossier.getTelefoonNummerMobiel() != null)
					result.setMobielKvk(ciKvkDossier.getTelefoonNummerMobiel());
			}
			
			if (ciKvkDossier.getCIKvKAandeelhouders() != null) {
				HashSet<AandeelhouderTransfer> aandeelhoudersTransfer = new HashSet<>();
				
				for (CIKvKAandeelhouder aandeelhouder : ciKvkDossier.getCIKvKAandeelhouders()) {
					String adres = aandeelhouder.getStraat();
	    			if (aandeelhouder.getStraat() != null && aandeelhouder.getHuisnummer() != null && aandeelhouder.getHuisnummerToevoeging() != null)
	    				adres = aandeelhouder.getStraat() + " " + aandeelhouder.getHuisnummer() + aandeelhouder.getHuisnummerToevoeging();
	    			else if (aandeelhouder.getStraat() != null && aandeelhouder.getHuisnummer() != null)
	    				adres = aandeelhouder.getStraat() + " " + aandeelhouder.getHuisnummer();
	    			else
	    				adres = aandeelhouder.getStraat();	
	    			
	    			String postcodeplaats = null;
	    			if (aandeelhouder.getPostcode() != null && aandeelhouder.getPlaats() != null)
	    				postcodeplaats = aandeelhouder.getPostcode() + " " + aandeelhouder.getPlaats();
	    			else if (aandeelhouder.getPostcode() != null)
	    				postcodeplaats = aandeelhouder.getPostcode();
	    			
	    			AandeelhouderTransfer aandeelhouderTransfer = new AandeelhouderTransfer();
	    			aandeelhouderTransfer.setNaam(aandeelhouder.getNaam());
	    			aandeelhouderTransfer.setAdres(adres);
	    			aandeelhouderTransfer.setPostcodeplaats(postcodeplaats);
	    			
	    			aandeelhoudersTransfer.add(aandeelhouderTransfer);
				}
				
				result.setAandeelhoudersTransfer(aandeelhoudersTransfer);
			}
			
			if (ciKvkDossier.getCIKvKCurators() != null) {
				CuratorTransfer curatorTransfer = null;
				
				if (ciKvkDossier.getCIKvKCurators().iterator().hasNext()) {					
					curatorTransfer = new CuratorTransfer();
					
					CIKvKCurator curator = ciKvkDossier.getCIKvKCurators().iterator().next();
					
					curatorTransfer.setNaam(curator.getNaam());
					curatorTransfer.setAdres(curator.getAdres());
				}
				
				result.setCuratorTransfer(curatorTransfer);
			}			
		}

		return result;
	}

	// used for creating report data of notifications
	// Over and Door reversed!!!!
	public static List<MeldingOverviewTransfer> convertMeldingenToMeldingenOverviewTransfer(List<Melding> meldingen, Integer meldingOverBedrijfId) { // , Integer meldingDoorBedrijfId
		List<MeldingOverviewTransfer> result = new ArrayList<>();

		if(meldingen != null) {
			for(Melding melding : meldingen) {
				Bedrijf bedrijfDoor = melding.getBedrijfByMeldingDoorBedrijfId();
				Bedrijf bedrijfOver = melding.getBedrijfByMeldingOverBedrijfId();


				String huisnrDoor = null;
				if(bedrijfDoor.getHuisNr() != null) huisnrDoor = bedrijfDoor.getHuisNr().toString();

				String status = EMeldingStatus.get(melding.getMeldingStatusCode()).getOmschrijving();
				String statusCode = EMeldingStatus.get(melding.getMeldingStatusCode()).getCode();

				String referentieInternVE = null;
				if(melding.getReferentieNummerIntern() != null)
					referentieInternVE = EReferentieInternType.VERMELDING.getPrefix() + melding.getReferentieNummerIntern();

				String referentieInternBEDoor = null;
				if(bedrijfDoor.getSbdrNummer() != null)
					referentieInternBEDoor = EReferentieInternType.BEDRIJF.getPrefix() + bedrijfDoor.getSbdrNummer();

				boolean isViaMelding = false;
				if(!meldingOverBedrijfId.equals(bedrijfOver.getBedrijfId())) isViaMelding = true;

				// Over and Door reversed!!!!
				BedrijfTransfer bedrijfOverTransfer = createBedrijfTransferFromBedrijf(bedrijfOver);

				MeldingOverviewTransfer meldingoverview;

				BigDecimal oorspronkelijkBedrag = null;
				BigDecimal bedrag = null;
				if(melding.isBedragWeergeven()) { // || bedrijfDoor.getBedrijfId().equals(meldingDoorBedrijfId)
					oorspronkelijkBedrag = melding.getOorspronkelijkBedrag();
					bedrag = melding.getBedrag();
				}


				if(melding.isDoorBedrijfWeergeven()) // || bedrijfDoor.getBedrijfId().equals(meldingDoorBedrijfId)
					meldingoverview = new MeldingOverviewTransfer(bedrijfDoor.getBedrijfId(), bedrijfDoor.isBedrijfActief(), ConvertUtil.isHoofd(bedrijfDoor.getHoofdNeven()), bedrijfDoor.getBedrijfsNaam(), bedrijfDoor.getKvKnummer(), bedrijfDoor.getSubDossier(), referentieInternBEDoor, bedrijfDoor.getStraat(), huisnrDoor, bedrijfDoor.getHuisNrToevoeging(), bedrijfDoor.getPostcode(), bedrijfDoor.getPlaats(), melding.getMeldingId(), melding.getGebruikerByMeldingDoorGebruikerIdGebruikerId(), melding.getReferentieNummer(), referentieInternVE, statusCode, status, melding.getDatumIngediend(), melding.getDatumVerloopFactuur(), melding.getDatumGeaccordeerd(), melding.getDatumVerwijderd(), melding.getDatumLaatsteMutatie(), oorspronkelijkBedrag, bedrag, isViaMelding, bedrijfDoor.getTelefoonnummer(), null, melding.getTelefoonNummerDebiteur(), melding.getEmailAdresDebiteur());
				else
					meldingoverview = new MeldingOverviewTransfer(null, true, false, null, bedrijfDoor.getKvKnummer(), null, null, null, null, null, null, null, melding.getMeldingId(), null, melding.getReferentieNummer(), referentieInternVE, statusCode, status, melding.getDatumIngediend(), melding.getDatumVerloopFactuur(), melding.getDatumGeaccordeerd(), melding.getDatumVerwijderd(), melding.getDatumLaatsteMutatie(), oorspronkelijkBedrag, bedrag, isViaMelding, null, bedrijfDoor.getCIKvKDossier().getSbihoofdAct(), melding.getTelefoonNummerDebiteur(), melding.getEmailAdresDebiteur());
					//meldingoverview = new MeldingOverviewTransfer(null, true, false, null, bedrijfDoor.getKvKnummer(), null, referentieInternBEDoor, null, null, null, null, null, melding.getMeldingId(), melding.getGebruikerByMeldingDoorGebruikerIdGebruikerId(), melding.getReferentieNummer(), referentieInternVE, statusCode, status, melding.getDatumIngediend(), melding.getDatumVerloopFactuur(), melding.getDatumGeaccordeerd(), melding.getDatumVerwijderd(), melding.getDatumLaatsteMutatie(), oorspronkelijkBedrag, bedrag, isViaMelding, null, bedrijfDoor.getCIKvKDossier().getSbihoofdAct());

				// Set over company
				meldingoverview.setBedrijfGemeldDoor(bedrijfOverTransfer);

				result.add(meldingoverview);
			}

			//the value 0 if the argument Date is equal to this Date; a value less than 0 
			//if this Date is before the Date argument; and a value greater than 0 
			//if this Date is after the Date argument.

			// sort list desc
			if(result.size() > 1) {
				Collections.sort(result, new Comparator<MeldingOverviewTransfer>() {
					@Override
					public int compare(MeldingOverviewTransfer o1, MeldingOverviewTransfer o2) {
						return (o1.getToegevoegd().after(o2.getToegevoegd()) ? 1 : -1);
					}
				});
			}
		} else return null;

		return result;
	}

	public static List<FactuurRegelAggregate> convertToFactuurRegelAggregate(List<Object[]> factuurRegelAggregate) {
		List<FactuurRegelAggregate> result = null;

		if(factuurRegelAggregate != null) {
			result = new ArrayList<FactuurRegelAggregate>();
			for(Object[] object : factuurRegelAggregate) {
				// p.omschrijving, COUNT(fr.factuurRegelId), fr.bedrag, SUM(fr.bedrag)
				String productCode = (String) object[0];
				String productOmschrijving = (String) object[1];
				Long aantal = (Long) object[2];
				BigDecimal bedragNetto = (BigDecimal) object[3];
				BigDecimal totaalBedragNetto = (BigDecimal) object[4];
				BigDecimal bedragBruto = ConvertUtil.getBrutoFromNetto(bedragNetto);
				BigDecimal totaalBedragBruto = ConvertUtil.getBrutoFromNetto(totaalBedragNetto);

				FactuurRegelAggregate factuurRegelAggregateResult = new FactuurRegelAggregate(productCode, productOmschrijving, aantal.intValue(), bedragNetto, bedragBruto, totaalBedragNetto, totaalBedragBruto);

				result.add(factuurRegelAggregateResult);
			}

		}

		return result;
	}

	public static List<HistorieTransfer> convertToHistorieTransfer(KvkDossierTransfer kvkDossierTransfer, List<Melding> meldingen) {
		List<HistorieTransfer> result = null;

		if(kvkDossierTransfer != null) {
			result = new ArrayList<>();
			HistorieTransfer historie;

			if(kvkDossierTransfer.getDatumInschrijving() != null) {
				historie = new HistorieTransfer(kvkDossierTransfer.getDatumInschrijving(), EHistorieType.VESTIGING);
				result.add(historie);
			}

			if(kvkDossierTransfer.getDatumVoorzetting() != null) {
				historie = new HistorieTransfer(kvkDossierTransfer.getDatumVoorzetting(), EHistorieType.VOORTZETTING);
				result.add(historie);
			}

			if(kvkDossierTransfer.getDatumSurseanceStart() != null) {
				historie = new HistorieTransfer(kvkDossierTransfer.getDatumSurseanceStart(), EHistorieType.AANVANG_SURSEANCE);
				result.add(historie);
			}

			if(kvkDossierTransfer.getDatumSurseanceEinde() != null) {
				historie = new HistorieTransfer(kvkDossierTransfer.getDatumSurseanceEinde(), EHistorieType.EINDE_SURSEANCE);
				result.add(historie);
			}

			if(kvkDossierTransfer.getDatumFaillissementStart() != null) {
				historie = new HistorieTransfer(kvkDossierTransfer.getDatumFaillissementStart(), EHistorieType.AANVANG_FAILLISSEMENT);
				result.add(historie);
			}

			if(kvkDossierTransfer.getDatumFaillissementEinde() != null) {
				historie = new HistorieTransfer(kvkDossierTransfer.getDatumFaillissementEinde(), EHistorieType.EINDE_FAILLISSEMENT);
				result.add(historie);
			}

			if(kvkDossierTransfer.getDatumOnbinding() != null) {
				historie = new HistorieTransfer(kvkDossierTransfer.getDatumOnbinding(), EHistorieType.ONTBINDING);
				result.add(historie);
			}

			if(kvkDossierTransfer.getDatumOpheffing() != null) {
				historie = new HistorieTransfer(kvkDossierTransfer.getDatumOpheffing(), EHistorieType.OPHEFFING);
				result.add(historie);
			}
			// add meldingen
			if(meldingen != null && meldingen.size() > 0) {
				for(Melding melding : meldingen) {
					if(melding.getDatumIngediend() != null && melding.getDatumGeaccordeerd() == null) {
						historie = new HistorieTransfer(melding.getDatumIngediend(), EHistorieType.VERMELDING_INGEDIEND, "Er is een vermelding met ref. " + EReferentieInternType.VERMELDING.getPrefix() + melding.getReferentieNummerIntern() + " ingediend");
						result.add(historie);
					} else if(melding.getDatumGeaccordeerd() != null) {
						historie = new HistorieTransfer(melding.getDatumGeaccordeerd(), EHistorieType.VERMELDING_ACTIEF, "Vermelding met ref. " + EReferentieInternType.VERMELDING.getPrefix() + melding.getReferentieNummerIntern() + " is actief");
						result.add(historie);
					}

					//					if (melding.getDatumVerwijderd() != null) {
					//						if (melding.getMeldingStatusCode().equals(EMeldingStatus.VERWIJDERD.getCode()))
					//							historie = new HistorieTransfer(melding.getDatumVerwijderd(), "Vermelding", "Vermelding met ref. " + EReferentieInternType.VERMELDING.getPrefix() + melding.getReferentieNummerIntern() + " is verwijderd/opgelost door aanvrager");
					//						else if (melding.getMeldingStatusCode().equals(EMeldingStatus.AFGEWEZEN.getCode()))
					//							historie = new HistorieTransfer(melding.getDatumVerwijderd(), "Vermelding", "Vermelding met ref. " + EReferentieInternType.VERMELDING.getPrefix() + melding.getReferentieNummerIntern() + " is afgewezen door betalingsachterstanden.nl");
					//
					//						result.add(historie);
					//					}
				}
			}

			// sort historie
			if(result.size() > 1) {
				Collections.sort(result, new Comparator<HistorieTransfer>() {
					@Override
					public int compare(HistorieTransfer o1, HistorieTransfer o2) {
						return (o2.getDatum().after(o1.getDatum()) ? 1 : -1);
					}
				});
			}
		}

		return result;
	}

	public static List<OpmerkingenTransfer> convertToOpmerkingenTransfer(String actief, String actiefOmschrijving, List<MeldingOverviewTransfer> meldingenoverview, int[] nrOfReports, int nrOfactiveMonitoring, Date laatsteVerhuizing, Integer ratingScore, CompanyInfo parent, CompanyInfo ultParent) {
		List<OpmerkingenTransfer> result = new ArrayList<>();

		if(actief != null) {

			String type = "alert";
			if(actief.equals("Actief")) type = "info";
			result.add(new OpmerkingenTransfer(type, actiefOmschrijving));
		}

		int activemeldingen = 0;
		int aangevraagdevermeldingen = 0;
		for(MeldingOverviewTransfer melding : meldingenoverview) {
			if(melding.getStatusCode().equals(EMeldingStatus.ACTIEF.getCode())) activemeldingen++;
			else if(melding.getStatusCode().equals(EMeldingStatus.INBEHANDELING.getCode())) aangevraagdevermeldingen++;
		}
		if(activemeldingen == 1) result.add(new OpmerkingenTransfer("alert", "Er is 1 actieve betalingsachterstand"));
		else if(activemeldingen > 1)
			result.add(new OpmerkingenTransfer("alert", "Er zijn " + activemeldingen + " actieve betalingsachterstanden"));

		if(activemeldingen == 0) result.add(new OpmerkingenTransfer("info", "Er zijn geen actieve betalingsachterstanden"));

		if(aangevraagdevermeldingen == 1)
			result.add(new OpmerkingenTransfer("alert", "Er is 1 vermelding ingediend en nog niet verwerkt"));
		else if(aangevraagdevermeldingen > 1)
			result.add(new OpmerkingenTransfer("alert", "Er zijn " + aangevraagdevermeldingen + " ingediende, nog niet verwerkte, vermeldingen"));

		if(laatsteVerhuizing != null)
			result.add(new OpmerkingenTransfer("info", "Dit bedrijf is sinds " + laatsteVerhuizing + " op dit adres gevestigd"));

		if (ratingScore != null) {
			if (activemeldingen == 0 && ratingScore != -1) {
				if (ratingScore < 0 || ratingScore > 100) {
				    // out of range
				} else {
				   result.add(new OpmerkingenTransfer("alert", BedrijfService.ratingScoreMessage.floorEntry(ratingScore).getValue()[BedrijfService.RATINGMESSAGE_OPMERKINGLIST]));
				}	
			} else if (activemeldingen > 0 && ratingScore > 0)
				result.add(new OpmerkingenTransfer("alert", BedrijfService.ratingBetalingsachterstandMessage[BedrijfService.RATINGMESSAGE_OPMERKINGLIST]));
		}
		
		if(parent != null)
			result.add(new OpmerkingenTransfer("info", ("Hoofdvestiging " + parent.getBedrijfsNaam() + " - " + parent.getKvKnummer()), true, parent.getKvKnummer()));

		if(ultParent != null)
			result.add(new OpmerkingenTransfer("info", ("Hoogste moedermaatschappij " + ultParent.getBedrijfsNaam() + " - " + ultParent.getKvKnummer()), true, ultParent.getKvKnummer()));

		if(nrOfReports != null && nrOfReports.length > 0) {
			//nrOfReports[0] = nrOfReportsLaatsteJaar;
			//nrOfReports[1] = nrOfReportsLaatsteHalfjaar;
			//nrOfReports[2] = nrOfReportsLaatsteKwartaal;
			//nrOfReports[3] = nrOfReportsLaatsteMaand;
			//nrOfReports[4] = nrOfReportsLaatsteTweeWeken;
			//nrOfReports[5] = nrOfReportsLaatsteWeek;

			String reportmelding;
			if(nrOfReports[3] > 0 && nrOfReports[3] > nrOfReports[4]) {
				if(nrOfReports[3] == 1)
					reportmelding = "Afgelopen maand is er " + nrOfReports[3] + " rapport opgevraagd";
				else reportmelding = "Afgelopen maand zijn er " + nrOfReports[3] + " rapportages opgevraagd";

				if(nrOfReports[5] > 0) {
					if(nrOfReports[5] == 1) reportmelding += ", waarvan " + nrOfReports[5] + " rapport afgelopen week";
					else reportmelding += ", waarvan " + nrOfReports[5] + " rapportages afgelopen week";
				} else if(nrOfReports[4] > 0) {
					if(nrOfReports[4] == 1)
						reportmelding += ", waarvan " + nrOfReports[4] + " rapport afgelopen twee weken";
					else reportmelding += ", waarvan " + nrOfReports[4] + " rapportages afgelopen twee weken";
				}
			} else if(nrOfReports[4] > 0 && nrOfReports[4] > nrOfReports[5]) {
				if(nrOfReports[4] == 1)
					reportmelding = "Afgelopen twee weken is er " + nrOfReports[4] + " rapport opgevraagd";
				else reportmelding = "Afgelopen twee weken zijn er " + nrOfReports[4] + " rapportages opgevraagd";

				if(nrOfReports[5] > 0) {
					if(nrOfReports[5] == 1) reportmelding += ", waarvan " + nrOfReports[5] + " rapport afgelopen week";
					else reportmelding += ", waarvan " + nrOfReports[5] + " rapportages afgelopen week";
				}
			} else if(nrOfReports[5] > 0) {
				if(nrOfReports[5] == 1)
					reportmelding = "Afgelopen week is er " + nrOfReports[5] + " rapport opgevraagd";
				else reportmelding = "Afgelopen week zijn er " + nrOfReports[5] + " rapportages opgevraagd";
			} else reportmelding = "Afgelopen week zijn er geen rapportages opgevraagd";

			result.add(new OpmerkingenTransfer("alert", reportmelding));
		}

		//		if(nrOfactiveMonitoring > 0) {
		//			if(nrOfactiveMonitoring == 1)
		//				result.add(new OpmerkingenTransfer("info", "Het bedrijf wordt op dit moment door 1 bedrijf gevolgd met behulp van monitoring"));
		//			else
		//				result.add(new OpmerkingenTransfer("info", "Het bedrijf wordt op dit moment door " + nrOfactiveMonitoring + " bedrijven gevolgd met behulp van monitoring"));
		//		}

		return result;
	}

	public static AdminAlertTransfer createAdminAlertTransferFromAdminAlert(Integer aId, Date d, Integer mId, String ref, String refNoPfx, Support s) {
		SupportTransfer sT = createSupportTransferFromSupport(s);
		return new AdminAlertTransfer(aId, d, mId, ref, refNoPfx, sT);
	}

	public static AlertOverviewTransfer createAlertOverviewTransferFromAlert(Integer alertId, String alertType, String supportType, Bedrijf bedrijf, Integer meldingId, Integer monitoringId, Integer ticketId, String referentieNummer, String meldingStatusCode, Date datumAlert, String indFaillissement, String indSurseance, String indEigenBedrijf, String indMonitoringBedrijf, String adresStatus, String referentieNummerNoPrefix) {
		if(bedrijf != null) {
			String huisNr = null;
			String meldingStatus = null;

			if(bedrijf.getHuisNr() != null) huisNr = bedrijf.getHuisNr().toString();

			if(meldingStatusCode != null) meldingStatus = EMeldingStatus.get(meldingStatusCode).getOmschrijving();

			String referentieIntern = null;
			if(bedrijf.getSbdrNummer() != null)
				referentieIntern = EReferentieInternType.BEDRIJF.getPrefix() + bedrijf.getSbdrNummer();

			return new AlertOverviewTransfer(alertId, alertType, supportType, bedrijf.getBedrijfId(), bedrijf.isBedrijfActief(), ConvertUtil.isHoofd(bedrijf.getHoofdNeven()), bedrijf.getBedrijfsNaam(), bedrijf.getKvKnummer(), bedrijf.getSubDossier(), referentieIntern, bedrijf.getStraat(), huisNr, bedrijf.getHuisNrToevoeging(), bedrijf.getPostcode(), bedrijf.getPlaats(), meldingId, monitoringId, ticketId, meldingStatus, datumAlert, indFaillissement, indSurseance, indEigenBedrijf, indMonitoringBedrijf, adresStatus, referentieNummer, referentieNummerNoPrefix, bedrijf.getTelefoonnummer());
		} else return null;
	}

	public static BedrijfTransfer createBedrijfTransferFromBedrijf(Bedrijf b) {
		if(b != null) {

			String referentieInternBE = null;
			String huisnr = null;

			if(b.getSbdrNummer() != null)
				referentieInternBE = EReferentieInternType.BEDRIJF.getPrefix() + b.getSbdrNummer();

			if(b.getHuisNr() != null) huisnr = b.getHuisNr().toString();

			return new BedrijfTransfer(b.getBedrijfId(), b.isBedrijfActief(), isHoofd(b.getHoofdNeven()), b.getBedrijfsNaam(), b.getKvKnummer(), b.getSubDossier(), referentieInternBE, b.getStraat(), huisnr, b.getHuisNrToevoeging(), b.getPostcode(), b.getPlaats(), b.getTelefoonnummer(), null);
		} else return null;
	}

	public static ExceptionBedrijfOverviewTransfer createExceptionBedrijfOverviewTransferFromBedrijf(Bedrijf bedrijf, Bedrijf bedrijfGemeldDoor, CustomMelding customMelding, boolean isKlant, boolean isProspect, boolean isAdresOk, String bedrijfTelefoonNummer) {
		if(bedrijf != null && customMelding != null) {
			String huisNr = null;
			if(bedrijf.getHuisNr() != null) huisNr = bedrijf.getHuisNr().toString();

			GebruikerTransfer gebruikerTransfer = null;
			Gebruiker gebruiker = customMelding.getGebruiker();
			if(gebruiker != null) {
				Rol klantrol = ERol.KLANT.getRolObject(true);
				boolean isKlantGebruiker = CompareUtil.containsRol(gebruiker.getRollen(), klantrol);
				gebruikerTransfer = createGebruikerTransferFromGebruiker(gebruiker, bedrijf, bedrijfTelefoonNummer, isKlantGebruiker, isProspect, isAdresOk, false, false);
			}

			String referentieIntern = null;
			if(bedrijf.getSbdrNummer() != null)
				referentieIntern = EReferentieInternType.BEDRIJF.getPrefix() + bedrijf.getSbdrNummer();

			BedrijfTransfer bedrijfGemeldDoorTransfer = null;
			if(bedrijfGemeldDoor != null)
				bedrijfGemeldDoorTransfer = createBedrijfTransferFromBedrijf(bedrijfGemeldDoor);

			return new ExceptionBedrijfOverviewTransfer(customMelding.getCustomMeldingId(), bedrijf.getBedrijfId(), bedrijf.isBedrijfActief(), ConvertUtil.isHoofd(bedrijf.getHoofdNeven()), bedrijf.getBedrijfsNaam(), bedrijf.getKvKnummer(), bedrijf.getSubDossier(), referentieIntern, bedrijf.getStraat(), huisNr, bedrijf.getHuisNrToevoeging(), bedrijf.getPostcode(), bedrijf.getPlaats(), bedrijfGemeldDoorTransfer, isKlant, isProspect, customMelding.getDatumAangemaakt(), gebruikerTransfer, customMelding.isFrauduleusBedrijf(), customMelding.isDreigendFaillissement(), customMelding.isIncorrectGegeven(), customMelding.isFaillissementVraag(), customMelding.getMeldingDetails(), bedrijf.getTelefoonnummer(), customMelding.getSignTelefoonNummer());
		} else return null;
	}

	public static FactuurTransfer createFactuurTransferFromFactuur(Factuur f) {
		if(f != null) {
			BedrijfTransfer bT = null;

			if(f.getBedrijf() != null) bT = createBedrijfTransferFromBedrijf(f.getBedrijf());

			return new FactuurTransfer(f.getFactuurId(), bT, f.getDatumFactuur(), f.getBedrag(), f.getDatumAangemaakt(), f.getDatumVerwerktExactOnline(), f.getReferentie(), f.getSalesEntryGuid(), f.getFileNaam());
		} else return null;
	}

	public static GebruikerTransfer createGebruikerTransferFromGebruiker(Gebruiker g, Bedrijf bedrijf, String bedrijfTelefoonNummer, boolean isKlant, boolean isProspect, boolean isAdresOk, boolean includeWachtwoord, boolean isActionsPresent) {
		if(g != null) {
			String wachtwoord = null;
			String bedrijfsNaam = null;

			if(g.getWachtwoord() != null && includeWachtwoord) wachtwoord = g.getWachtwoord().getWachtwoord();
			//if(g.getBedrijf() != null) bedrijfsNaam = g.getBedrijf().getBedrijfsNaam();
			if (bedrijf != null) bedrijfsNaam = bedrijf.getBedrijfsNaam();

			Map<String, Boolean> rollen = new HashMap<>();
			Map<String, Boolean> bevoegdheden = new HashMap<>();

			// user roles are of own company, or from bedrijfmanaged
			Set<Rol> gebruikerRollen = g.getRollen();
			String voornaam = g.getVoornaam();
			String naam = g.getNaam();
			String afdeling = g.getAfdeling();
			String functie = g.getFunctie();
			String emailAdres = g.getEmailAdres();
			String telefoonNummer = g.getTelefoonNummer();
			String geslacht = g.getGeslacht();
			Integer bedrijfId = g.getBedrijfBedrijfId();
			boolean isBedrijfManager = false;
			
			// If user company is not equals to bedrijf company then user may be a managed user
			if (bedrijf != null && (g.getBedrijfBedrijfId() == null || !g.getBedrijfBedrijfId().equals(bedrijf.getBedrijfId()))) {
				if (g.getBedrijvenManagedDoorGebruikerId() != null) {
					for (BedrijfManaged bedrijfManaged : g.getBedrijvenManagedDoorGebruikerId()) {
						if (bedrijfManaged.isActief() && bedrijfManaged.getBedrijfBedrijfId().equals(bedrijf.getBedrijfId())) {
							// if bedrijfManaged changes role set of user to company user roles
							gebruikerRollen = bedrijfManaged.getRollen();
							// and change props...
							voornaam = bedrijfManaged.getVoornaam();
							naam = bedrijfManaged.getNaam();
							afdeling = bedrijfManaged.getAfdeling();
							functie = bedrijfManaged.getFunctie();
							emailAdres = bedrijfManaged.getEmailAdres();
							telefoonNummer = bedrijfManaged.getTelefoonNummer();
							geslacht = bedrijfManaged.getGeslacht();
							bedrijfId = bedrijfManaged.getBedrijfBedrijfId();
							isBedrijfManager = true;
						}
					}
				}
			}			
			
			for(Rol rol : gebruikerRollen) {
				rollen.put(rol.getCode(), true);
			}

			return new GebruikerTransfer(bedrijfId, bedrijfsNaam, g.getGebruikersNaam(), g.getGebruikerId(), voornaam, naam, afdeling, functie, emailAdres, telefoonNummer, bedrijfTelefoonNummer, wachtwoord, isKlant, isProspect, isAdresOk, rollen, bevoegdheden, isActionsPresent, g.getShowHelp(), geslacht, isBedrijfManager);
		} else return null;
	}

	public static BriefBatchTransfer createBriefBatchTransferFromBriefBatch(BriefBatch bB){
		if(bB!=null){
			return new BriefBatchTransfer(bB.getBriefBatchId(), bB.getBriefBatchStatusCode(), EBriefBatchStatus.get(bB.getBriefBatchStatusCode()).getOmschrijving(), bB.getBriefBatchTypeCode(), EBriefBatchType.get(bB.getBriefBatchTypeCode()).getOmschrijving(), bB.getDatumAangemaakt(), bB.getDatumVoltooid(), bB.getGebruikerGebruikerId());
		} else return null;
	}

	public static InternalProcessTransfer createInternalProcessTransferFromInternalProcess(InternalProcess iP) {
		if(iP != null) {
			BedrijfTransfer bT = null;
			BriefBatchTransfer bBT = null;
			if(iP.getBedrijf() != null) bT = createBedrijfTransferFromBedrijf(iP.getBedrijf());

			if(iP.getBriefBatch() != null) bBT = createBriefBatchTransferFromBriefBatch(iP.getBriefBatch());

			if(bT != null) {
				if(iP.getInternalProcessTypeCode().equals(EInternalProcessType.KLANT_BATCH.getCode()) || iP.getInternalProcessTypeCode().equals(EInternalProcessType.MELDING_BATCH.getCode())) {
					return new InternalProcessTransfer(iP.getInternalProcessId(), iP.getInternalProcessStatusCode(), iP.getInternalProcessTypeCode(), iP.getDatumAangemaakt(), iP.getDatumVerwerkt(), iP.getVerwerktDoorGebruikerGebruikerId(), EInternalProcessStatus.get(iP.getInternalProcessStatusCode()).getOmschrijving(), EInternalProcessType.get(iP.getInternalProcessTypeCode()).getOmschrijving(), iP.getDocument().getReferentieNummer(), bBT, iP.getDocumentDocumentId());
				} else {
					return new InternalProcessTransfer(bT.getBedrijfId(), bT.isActief(), bT.isHoofd(), bT.getBedrijfsNaam(), bT.getKvkNummer(), bT.getSubDossier(), bT.getSbdrNummer(), bT.getStraat(), bT.getHuisnummer(), bT.getHuisnummerToevoeging(), bT.getPostcode(), bT.getPlaats(), bT.getTelefoonnummer(), iP.getInternalProcessId(), iP.getInternalProcessStatusCode(), iP.getInternalProcessTypeCode(), iP.getDatumAangemaakt(), iP.getDatumVerwerkt(), iP.getVerwerktDoorGebruikerGebruikerId(), EInternalProcessStatus.get(iP.getInternalProcessStatusCode()).getOmschrijving(), EInternalProcessType.get(iP.getInternalProcessTypeCode()).getOmschrijving(), iP.getDocument().getReferentieNummer(), bBT, iP.getDocumentDocumentId());
				}
			} else return null;
		} else return null;
	}

	public static KlantBedrijfOverviewTransfer createKlantBedrijfOverviewTransferFromBedrijf(Bedrijf bedrijf, Klant klant) {
		if(bedrijf != null) {
			String huisNr = null;
			if(bedrijf.getHuisNr() != null) huisNr = bedrijf.getHuisNr().toString();

			String klantstatusCode = klant.getKlantStatusCode();
			String klantstatus = EKlantStatus.get(klantstatusCode).getOmschrijving();
			
			

			String referentieIntern = null;
			if(bedrijf.getSbdrNummer() != null)
				referentieIntern = EReferentieInternType.BEDRIJF.getPrefix() + bedrijf.getSbdrNummer();


			return new KlantBedrijfOverviewTransfer(bedrijf.getBedrijfId(), bedrijf.isBedrijfActief(), ConvertUtil.isHoofd(bedrijf.getHoofdNeven()), bedrijf.getBedrijfsNaam(), bedrijf.getKvKnummer(), bedrijf.getSubDossier(), referentieIntern, bedrijf.getStraat(), huisNr, bedrijf.getHuisNrToevoeging(), bedrijf.getPostcode(), bedrijf.getPlaats(), bedrijf.getDatumWijziging(), klant.getGebruikerId(), klant.getGebruikersNaam(), klant.getActivatieCode(), klantstatus, klantstatusCode, bedrijf.isAdresOk(), bedrijf.getTelefoonnummer(), klant.getBriefStatusCode(), EBriefStatus.get(klant.getBriefStatusCode()).getOmschrijving(), klant.getDatumAangemaakt());
		} else return null;
	}

	public static MeldingOverviewTransfer createMeldingOverviewTransferFromMelding(Melding melding) {
		if(melding != null) {
			Bedrijf bedrijf = melding.getBedrijfByMeldingOverBedrijfId();

			String huisNr = null;
			if(bedrijf.getHuisNr() != null) huisNr = bedrijf.getHuisNr().toString();

			String status = EMeldingStatus.get(melding.getMeldingStatusCode()).getOmschrijving();
			String statusCode = EMeldingStatus.get(melding.getMeldingStatusCode()).getCode();

			String referentieInternBE = null;
			if(bedrijf.getSbdrNummer() != null)
				referentieInternBE = EReferentieInternType.BEDRIJF.getPrefix() + bedrijf.getSbdrNummer();

			String referentieIntern = null;
			if(melding.getReferentieNummerIntern() != null)
				referentieIntern = EReferentieInternType.VERMELDING.getPrefix() + melding.getReferentieNummerIntern();

			return new MeldingOverviewTransfer(bedrijf.getBedrijfId(), bedrijf.isBedrijfActief(), ConvertUtil.isHoofd(bedrijf.getHoofdNeven()), bedrijf.getBedrijfsNaam(), bedrijf.getKvKnummer(), bedrijf.getSubDossier(), referentieInternBE, bedrijf.getStraat(), huisNr, bedrijf.getHuisNrToevoeging(), bedrijf.getPostcode(), bedrijf.getPlaats(), melding.getMeldingId(), melding.getGebruikerByMeldingDoorGebruikerIdGebruikerId(), melding.getReferentieNummer(), referentieIntern, statusCode, status, melding.getDatumIngediend(), melding.getDatumVerloopFactuur(), melding.getDatumGeaccordeerd(), melding.getDatumVerwijderd(), melding.getDatumLaatsteMutatie(), melding.getOorspronkelijkBedrag(), melding.getBedrag(), false, bedrijf.getTelefoonnummer(), null, melding.getTelefoonNummerDebiteur(), melding.getEmailAdresDebiteur());
		} else return null;
	}

	public static MeldingTransfer createMeldingTransferFromMelding(Melding m, Notitie n) {
		if(m != null) {
			Bedrijf bedrijf = m.getBedrijfByMeldingOverBedrijfId();

			String huisNr = null;
			if(bedrijf.getHuisNr() != null) huisNr = bedrijf.getHuisNr().toString();

			String meldingstatus = EMeldingStatus.get(m.getMeldingStatusCode()).getOmschrijving();
			String meldingstatusCode = EMeldingStatus.get(m.getMeldingStatusCode()).getCode();

			String redenVerwijderenCode = null;
			String redenVerwijderenOmschrijving = null;
			if(m.getRedenVerwijderenMeldingCode() != null) {
				ERedenVerwijderenMelding.get(m.getRedenVerwijderenMeldingCode()).getCode();
				ERedenVerwijderenMelding.get(m.getRedenVerwijderenMeldingCode()).getOmschrijving();
			}

			String referentieInternBE = null;
			if(bedrijf.getSbdrNummer() != null)
				referentieInternBE = EReferentieInternType.BEDRIJF.getPrefix() + bedrijf.getSbdrNummer();


			String referentieIntern = null;
			if(m.getReferentieNummer() != null)
				referentieIntern = EReferentieInternType.VERMELDING.getPrefix() + m.getReferentieNummerIntern();

			boolean bedrijfsgegevensNietJuist = false;
			if(meldingstatusCode != null && meldingstatusCode.equals(EMeldingStatus.DATA_NOK.getCode()))
				bedrijfsgegevensNietJuist = true;
			
			return new MeldingTransfer(m.getMeldingId(), m.getGebruikerByMeldingDoorGebruikerIdGebruikerId(), m.getGebruikerByLaatsteMutatieDoorGebruikerIdGebruikerId(), m.getGebruikerByGeaccordeerdDoorGebruikerIdGebruikerId(), m.getGebruikerByVerwijderdDoorGebruikerIdGebruikerId(), bedrijf.getBedrijfId(), bedrijf.isBedrijfActief(), ConvertUtil.isHoofd(bedrijf.getHoofdNeven()), bedrijf.getBedrijfsNaam(), bedrijf.getKvKnummer(), bedrijf.getSubDossier(), referentieInternBE, bedrijf.getStraat(), huisNr, bedrijf.getHuisNrToevoeging(), bedrijf.getPostcode(), bedrijf.getPlaats(), m.getReferentieNummer(), referentieIntern, m.getDatumIngediend(), m.getDatumLaatsteMutatie(), m.getDatumGeaccordeerd(), m.getDatumVerwijderd(), m.getDatumVerloopFactuur(), m.getBedrag(), m.getOorspronkelijkBedrag(), m.getBedrijfByMeldingDoorBedrijfIdBedrijfId(), meldingstatus, meldingstatusCode, m.isDoorBedrijfWeergeven(), m.isBedragWeergeven(), bedrijfsgegevensNietJuist, redenVerwijderenCode, redenVerwijderenOmschrijving, bedrijf.getTelefoonnummer(), m.getBriefStatusCode(), EBriefStatus.get(m.getBriefStatusCode()).getOmschrijving(), m.getBriefBatchBriefBatchId()!=null, false, n != null ? n.getNotitie() : null, m.getTelefoonNummerDebiteur() != null ? m.getTelefoonNummerDebiteur() : bedrijf.getTelefoonnummer(), m.getEmailAdresDebiteur());
		} else return null;
	}

	public static MonitoringOverviewTransfer createMonitoringOverviewTransferFromMonitoring(Monitoring monitoring) {
		if(monitoring != null) {
			Bedrijf bedrijf = monitoring.getBedrijfByMonitoringVanBedrijfId();

			String huisNr = null;
			if(bedrijf.getHuisNr() != null) huisNr = bedrijf.getHuisNr().toString();

			String status = EMonitoringStatus.get(monitoring.getMonitoringStatusCode()).getOmschrijving();

			String referentieInternBE = null;
			if(bedrijf.getSbdrNummer() != null)
				referentieInternBE = EReferentieInternType.BEDRIJF.getPrefix() + bedrijf.getSbdrNummer();

			String referentieInternMO = null;
			if(monitoring.getReferentieNummerIntern() != null)
				referentieInternMO = EReferentieInternType.MONITORING.getPrefix() + monitoring.getReferentieNummerIntern();


			return new MonitoringOverviewTransfer(bedrijf.getBedrijfId(), bedrijf.isBedrijfActief(), ConvertUtil.isHoofd(bedrijf.getHoofdNeven()), bedrijf.getBedrijfsNaam(), bedrijf.getKvKnummer(), bedrijf.getSubDossier(), referentieInternBE, bedrijf.getStraat(), huisNr, bedrijf.getHuisNrToevoeging(), bedrijf.getPostcode(), bedrijf.getPlaats(), monitoring.getMonitoringId(), referentieInternMO, monitoring.getDatumStart(), monitoring.getDatumLaatsteCheck(), monitoring.getDatumEinde(), monitoring.getGebruikerByMonitoringDoorGebruikerIdGebruikerId(), monitoring.getGebruikerByVerwijderdDoorGebruikerIdGebruikerId(), monitoring.getGebruikerByLaatsteMutatieDoorGebruikerIdGebruikerId(), bedrijf.getTelefoonnummer());
		} else return null;
	}

	public static RemovedBedrijfOverviewTransfer createRemovedBedrijvenOverviewTransferFromBedrijf(RemovedBedrijf removedBedrijf, Bedrijf bedrijf, Integer bedrijfId) {
		if(removedBedrijf != null && bedrijf != null) {
			String huisNr = null;

			if(bedrijf.getHuisNr() != null) huisNr = bedrijf.getHuisNr().toString();

			String portfolio = null;
			String referentieIntern = null;

			String referentieNummer = removedBedrijf.getReferentieNummer();
			String status = null;

			if(removedBedrijf.getMeldingId() != null) {
				status = EMeldingStatus.get(removedBedrijf.getStatus()).getOmschrijving();
				if(removedBedrijf.getReferentieNummerIntern() != null)
					referentieIntern = EReferentieInternType.VERMELDING.getPrefix() + removedBedrijf.getReferentieNummerIntern();
			} else if(removedBedrijf.getMonitoringId() != null) {
				status = EMonitoringStatus.get(removedBedrijf.getStatus()).getOmschrijving();
				if(removedBedrijf.getReferentieNummerIntern() != null)
					referentieIntern = EReferentieInternType.MONITORING.getPrefix() + removedBedrijf.getReferentieNummerIntern();
			}


			String referentieBE = null;
			if(bedrijf.getSbdrNummer() != null)
				referentieBE = EReferentieInternType.BEDRIJF.getPrefix() + bedrijf.getSbdrNummer();

			Integer meldingId = null;
			Integer monitoringId = null;
			Date datumStart = removedBedrijf.getDatumStart();
			Date datumEinde = removedBedrijf.getDatumEinde();

			if(removedBedrijf.getMeldingId() != null) portfolio = "Vermelding";
			else if(removedBedrijf.getMonitoringId() != null) portfolio = "Monitoring";

			return new RemovedBedrijfOverviewTransfer(bedrijf.getBedrijfId(), bedrijf.isBedrijfActief(), ConvertUtil.isHoofd(bedrijf.getHoofdNeven()), bedrijf.getBedrijfsNaam(), bedrijf.getKvKnummer(), bedrijf.getSubDossier(), referentieBE, bedrijf.getStraat(), huisNr, bedrijf.getHuisNrToevoeging(), bedrijf.getPostcode(), bedrijf.getPlaats(), referentieNummer, referentieIntern, portfolio, meldingId, monitoringId, datumStart, datumEinde, removedBedrijf.getGebruikerAangemaaktId(), removedBedrijf.getGebruikerVerwijderdId(), status, removedBedrijf.getRedenVerwijderenCode(), bedrijf.getTelefoonnummer());
		} else return null;
	}

	public static ReportRequestedTransfer createReportRequestedTransfer(Document document, EDocumentType docType, GebruikerTransfer gebruikerTransfer, BedrijfTransfer bedrijfTransfer, String indEigenGebruiker) {
		if(document != null && gebruikerTransfer != null && bedrijfTransfer != null) {
			String referentieInternRE = null;
			if(document.getReferentieNummer() != null)
				referentieInternRE = docType.getPrefix() + document.getReferentieNummer();


			return new ReportRequestedTransfer(document.getNaam(), referentieInternRE, bedrijfTransfer, gebruikerTransfer, indEigenGebruiker, document.getDatumAangemaakt());
		} else return null;

	}
	
	public static SearchResultsOverviewTransfer createSearchResultsOverviewTransfer(SearchResult searchResult, Bedrijf bedrijf, Bedrijf bedrijfvan) {
		if(searchResult != null && bedrijf != null) {
			String huisNr = null;

			if(bedrijf.getHuisNr() != null) huisNr = bedrijf.getHuisNr().toString();

			String type = null;
			String referentieIntern = null;

			String status = null;
			String statusCode = null;
			if(searchResult.getDatatype().equals(ESearchResultType.VERMELDING.getCode())) {
				type = ESearchResultType.VERMELDING.getOmschrijving();
				status = EMeldingStatus.get(searchResult.getStatus()).getOmschrijving();
				statusCode = EMeldingStatus.get(searchResult.getStatus()).getCode();
				if(searchResult.getReferentieNummerIntern() != null)
					referentieIntern = EReferentieInternType.VERMELDING.getPrefix() + searchResult.getReferentieNummerIntern();
			} else if(searchResult.getDatatype().equals(ESearchResultType.MONITORING.getCode())) {
				type = ESearchResultType.MONITORING.getOmschrijving();
				status = EMonitoringStatus.get(searchResult.getStatus()).getOmschrijving();
				statusCode = EMonitoringStatus.get(searchResult.getStatus()).getCode();
				if(searchResult.getReferentieNummerIntern() != null)
					referentieIntern = EReferentieInternType.MONITORING.getPrefix() + searchResult.getReferentieNummerIntern();
			} else if(searchResult.getDatatype().equals(ESearchResultType.KLANT.getCode())) {
				type = ESearchResultType.KLANT.getOmschrijving();
				status = EKlantStatus.get(searchResult.getStatus()).getOmschrijving();
				statusCode = EKlantStatus.get(searchResult.getStatus()).getCode();
				if(searchResult.getReferentieNummerIntern() != null)
					referentieIntern = EReferentieInternType.BEDRIJF.getPrefix() + searchResult.getReferentieNummerIntern();

			} else if(searchResult.getDatatype().equals(ESearchResultType.RAPPORT.getCode())) {
				type = ESearchResultType.RAPPORT.getOmschrijving();
				status = null; // Rapport has no status
				statusCode = null;
				if(searchResult.getReferentieNummerIntern() != null)
					referentieIntern = EDocumentType.RAPPORT.getPrefix() + searchResult.getReferentieNummerIntern();
			} else if(searchResult.getDatatype().equals(ESearchResultType.MOBILEAPPCHECK.getCode())) {
				type = ESearchResultType.MOBILEAPPCHECK.getOmschrijving();
				status = null; // Mobile app check has no status
				statusCode = null;
				if(searchResult.getReferentieNummerIntern() != null)
					referentieIntern = EDocumentType.MOBILEAPPCHECK.getPrefix() + searchResult.getReferentieNummerIntern();
			} 
			String referentieBE = null;
			if(bedrijf.getSbdrNummer() != null)
				referentieBE = EReferentieInternType.BEDRIJF.getPrefix() + bedrijf.getSbdrNummer();

			Date datumStart = searchResult.getDatumStart();
			Date datumEinde = searchResult.getDatumEinde();

			BedrijfTransfer bedrijfvanTransfer = null;
			if(bedrijfvan != null) bedrijfvanTransfer = createBedrijfTransferFromBedrijf(bedrijfvan);

			return new SearchResultsOverviewTransfer(bedrijf.getBedrijfId(), bedrijf.isBedrijfActief(), ConvertUtil.isHoofd(bedrijf.getHoofdNeven()), bedrijf.getBedrijfsNaam(), bedrijf.getKvKnummer(), bedrijf.getSubDossier(), referentieBE, bedrijf.getStraat(), huisNr, bedrijf.getHuisNrToevoeging(), bedrijf.getPostcode(), bedrijf.getPlaats(), bedrijfvanTransfer, referentieIntern, type, searchResult.getMeldingId(), searchResult.getMonitoringId(), searchResult.getDocumentId(), searchResult.getBedrijfId(), datumStart, datumEinde, searchResult.getGebruikerAangemaaktId(), statusCode, status, bedrijf.getTelefoonnummer());
		} else return null;
	}

	public static SupportBestandTransfer createSupportBestandTransferFromSupportBestand(SupportBestand sB) {
		return new SupportBestandTransfer(sB.getDatumUpload(), sB.isGearchiveerd(), sB.getOorspronkelijkBestandsNaam(), sB.getReferentieNummer(), sB.getSupportBestandId(), sB.getSupportSupportId(), sB.getVolledigPad());
	}

	public static SupportTransfer createSupportTransferFromSupport(Support s, String ref, String bNaam) {
		SupportTransfer sT = createSupportTransferFromSupport(s);

		sT.setMeldingFactuurNr(ref);
		sT.setMeldingBedrijfsNaam(bNaam);

		return sT;
	}

	public static SupportTransfer createSupportTransferFromSupport(Support s) {
		MeldingTransfer mTransfer = null;
		GebruikerTransfer gTransfer = null;
		BedrijfTransfer bTransfer = null;
		ArrayList<SupportBestandTransfer> sBTl = new ArrayList<>();
		SupportBestandTransfer[] sBTa = null;

		if(s.getMelding() != null) {
			if(meldingIsEligibleForConversion(s.getMelding()))
				mTransfer = createMeldingTransferFromMelding(s.getMelding(), s.getMelding().getNotitieGebruiker());
			else {
				mTransfer = new MeldingTransfer();
				mTransfer.setMeldingId(s.getMeldingMeldingId());
			}
		}

		// bedrijfTelefoonNummer is set to empty!!!
		if(s.getGebruikerByGebruikerId() != null)
			gTransfer = createGebruikerTransferFromGebruiker(s.getGebruikerByGebruikerId(), s.getBedrijf(), "-", false, false, true, false, false);

		if(s.getBedrijf() != null) bTransfer = createBedrijfTransferFromBedrijf(s.getBedrijf());

		if(s.getSupportBestands() != null) {
			for(SupportBestand sB : s.getSupportBestands()) {
				sBTl.add(createSupportBestandTransferFromSupportBestand(sB));
			}
			sBTa = sBTl.toArray(new SupportBestandTransfer[sBTl.size()]);
		}

		return new SupportTransfer(s.getBericht(), s.getDatumAangemaakt(), s.getDatumUpdate(), gTransfer, s.getGebruikerByGeslotenDoorGebruikerIdGebruikerId(), mTransfer, s.getBetwistBezwaar(), EReferentieInternType.SUPPORT.getPrefix() + s.getReferentieNummer(), s.getReferentieNummer(), s.getSupportId(), ESupportStatus.get(s.getSupportStatusCode()), ESupportReden.get(s.getSupportRedenCode()), s.getSupportSupportId(), ESupportType.get(s.getSupportTypeCode()), ((s.getSupports().size() > 0) ? s.getSupports().iterator().next().getSupportId() : null), bTransfer, sBTa);
	}

	public static BigDecimal getBrutoFromNetto(BigDecimal netto) {
		if(netto == null) return null;
		else {
			BigDecimal result = netto;
			BigDecimal vat = getBtwFromNetto(netto);

			result = result.add(vat);

			return result;
		}
	}

	public static BigDecimal getBtwFromNetto(BigDecimal netto) {
		BigDecimal btwpct = new BigDecimal(.21);
		if(netto != null) { return netto.multiply(btwpct).setScale(2, BigDecimal.ROUND_HALF_UP);}
		else { return null;}
	}
	
	public static List<ContactMomentTransfer> convertToContactMomentTranfers(List<Object[]> contactMomentAndNotities) {
		List<ContactMomentTransfer> results = null;
		
		if (contactMomentAndNotities != null) {
			results = new ArrayList<ContactMomentTransfer>();
			
			for (Object[] object : contactMomentAndNotities) {
				ContactMomentTransfer result = convertToContactMomentTransfer(object);
				results.add(result);
			}
		}
		
		return results;
	}

	public static ContactMomentTransfer convertToContactMomentTransfer(Object[] contactMomentAndNotitie) {
		ContactMomentTransfer result = null;

		if(contactMomentAndNotitie != null) {
			result = new ContactMomentTransfer();
			ContactMoment contactMoment = (ContactMoment) contactMomentAndNotitie[0];
			Notitie notitie = (Notitie) contactMomentAndNotitie[1];
			
			if (notitie != null && notitie.getNotitieType().equals(ENotitieType.CONTACT_MOMENT.getCode())) {
				result = new ContactMomentTransfer();
				result.setBeantwoord(contactMoment.getBeantwoord());
				result.setContactMomentId(contactMoment.getContactMomentId());
				result.setContactWijze(contactMoment.getContactWijze());
				result.setDatumContact(contactMoment.getDatumContact());
				result.setDatumContactTerug(contactMoment.getDatumContactTerug());
				result.setGebruikerId(contactMoment.getGebruikerId());
				result.setMeldingId(contactMoment.getMeldingMeldingId());
				result.setNotitie(notitie.getNotitie());
				result.setNotitieIntern(contactMoment.getNotitieIntern());
				result.setNotitieType(notitie.getNotitieType());
				result.setContactGegevens(contactMoment.getContactGegevens());
			}
		}

		return result;
	}
	
	public static List<NotificationPublicTransfer> convertMeldingenToNotificationsPublicTransfer(Date fromDate, List<Melding> meldingen) {
		List<NotificationPublicTransfer> results = null;
		
		if (meldingen != null) {
			results = new ArrayList<NotificationPublicTransfer>();
			
			for (Melding melding : meldingen) {
				NotificationPublicTransfer not = new NotificationPublicTransfer();
				
				not.setId(melding.getReferentieNummerIntern());
				
				// set 'Active' date or 'Create' date first
				if (melding.getDatumGeaccordeerd() != null)
					not.setDate(melding.getDatumGeaccordeerd());
				else if (melding.getDatumIngediend() != null)
					not.setDate(melding.getDatumIngediend());
				// change date to 'Removed' date or 'Mutation' date if available
				if (not.getDate() != null) {
					if (melding.getDatumVerwijderd() != null && melding.getDatumVerwijderd().after(not.getDate()))
						not.setDate(melding.getDatumVerwijderd());
					else if (melding.getDatumLaatsteMutatie() != null && melding.getDatumLaatsteMutatie().after(not.getDate()))
						not.setDate(melding.getDatumLaatsteMutatie());
				} else {
					if (melding.getDatumVerwijderd() != null)
						not.setDate(melding.getDatumVerwijderd());
					else if (melding.getDatumLaatsteMutatie() != null)
						not.setDate(melding.getDatumLaatsteMutatie());
				}
				
				// get 'current' amount
				not.setAmount(melding.getBedrag());
				
				// 'open' = active
				not.setDays(0);
				if (melding.getMeldingStatusCode().equals(EMeldingStatus.ACTIEF.getCode())) {					
					not.setOpen(true);
					if (melding.getDatumIngediend() != null) {
						long diff = fromDate.getTime() - melding.getDatumIngediend().getTime();
						not.setDays(new Integer((int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)));
					} 
				}
				else
					not.setOpen(false);
				
				results.add(not);
			}
		}
		
		return results;
	} 
}