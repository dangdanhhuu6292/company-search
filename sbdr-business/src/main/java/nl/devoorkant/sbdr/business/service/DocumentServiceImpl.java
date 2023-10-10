package nl.devoorkant.sbdr.business.service;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.*;
import com.lowagie.text.pdf.draw.LineSeparator;
import nl.devoorkant.sbdr.business.transfer.*;
import nl.devoorkant.sbdr.business.util.ConvertUtil;
import nl.devoorkant.sbdr.business.util.*;
import nl.devoorkant.sbdr.business.util.pdf.*;
import nl.devoorkant.sbdr.business.wrapper.ErrorService;
import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.*;
import nl.devoorkant.sbdr.data.model.Document;
import nl.devoorkant.sbdr.data.service.*;
import nl.devoorkant.sbdr.data.util.*;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.ui.HorizontalAlignment;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.chart.util.UnitType;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

//import nl.devoorkant.sbdr.business.util.pdf.Footer;

@Service("documentService")
@Transactional(readOnly = true)
public class DocumentServiceImpl implements DocumentService {
	public static int COLOR_SBDR_BLUE = 0x6cacdf; // 0x88bbe3;//0x3399CC;
	public static int COLOR_SBDR_DARKBLUE = 0x0a212f; // 0x2C3D4F;
	public static float PADDINGTOP_TOPIC_CONTENT = 60f;
	
	public static String CONFIG_INCASSANTENNUMMER = "INCASSANTENNUMMER";

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private BedrijfDataService bedrijfDataService;

	@Autowired
	private BedrijfService bedrijfService;
	
	@Autowired
	private ConfiguratieService configuratieService;

	@Autowired
	private CompanyInfoDataService companyInfoDataService;

	@Autowired
	private DocumentDataService documentDataService;

	@Autowired
	private EmailService emailService;

	@Autowired
	private FactuurDataService factuurDataService;

	@Autowired
	private GebruikerService gebruikerService;

	@Autowired
	private InternalProcessService internalProcessService;

	@Autowired
	private KlantDataService klantDataService;

	@Autowired
	private MeldingDataService meldingDataService;

	@Autowired
	private ProductService productService;

	@Autowired
	private SupportDataService supportDataService;

	private static String CURRENCY_FORMAT = "€ #,##0.00;€ #,##0.00";
	private static int MARGIN_BOTTOM = 50;
	private static int MARGIN_LEFT = 60;
	private static int MARGIN_RIGHT = 30;
	private static int MARGIN_TOP = 190;
	private static int MARGIN_TOP_LETTER = 150;
	private static int MARGIN_TOP_LETTER_PRINTED = 165; //175 190;
	private static CellStyle cellStyleCurrency;
	private static CellStyle cellStyleDate;
	private static final Logger LOGGER = LoggerFactory.getLogger(DocumentServiceImpl.class);

	@Override
	@Transactional
	public void createFactuurPdf(Integer factuurId) throws ServiceException {
		ByteArrayOutputStream result = new ByteArrayOutputStream();

		com.lowagie.text.Document pdfdoc;
		PdfWriter pdfwriter;
		Factuur factuur = null;
		Bedrijf bedrijf = null;
		Klant klant = null;
		List<FactuurRegelAggregate> factuurRegelAggregate = null;
		CIKvKDossier dossier = null;
		Resource logo = null;
		String incassantennummertekst = "";

		try {
			factuur = factuurDataService.findById(factuurId);

			bedrijf = bedrijfDataService.findByBedrijfId(factuur.getBedrijfBedrijfId());

			klant = klantDataService.findKlantOfBedrijfByBedrijfId(factuur.getBedrijfBedrijfId());
			
			// if incassantennummer available create text
			Configuratie configincassantennummer = configuratieService.getConfiguratie(CONFIG_INCASSANTENNUMMER);
			if (configincassantennummer != null && configincassantennummer.getWaardeString() != null)
				incassantennummertekst = " hierbij wordt het incassantennummer " +  configincassantennummer.getWaardeString() + " vermeld";

			factuurRegelAggregate = productService.getFactuurRegelAggregate(factuurId);
			//dossier = companyInfoDataService.findOne(bedrijf.getCIKvKDossierCikvKdossierId());

			//if(dossier == null) throw new ServiceException("Cannot find CompanyInfo dossier of company.");

			//logo = applicationContext.getResource("classpath:/images/Logo_BA_DEF_diap.png");
			logo = applicationContext.getResource("classpath:/images/Logo_CRZB_DEF.png");

		} catch(DataServiceException e) {
			throw new ServiceException(e);
		}

		//pdfdoc = new com.lowagie.text.Document(PageSize.A4, 30, 30, 100, 80);
		pdfdoc = new com.lowagie.text.Document(PageSize.A4, MARGIN_LEFT, MARGIN_RIGHT, MARGIN_TOP_LETTER, MARGIN_BOTTOM); //30, 30, 130, 30);

		try {
			pdfwriter = PdfWriter.getInstance(pdfdoc, result);

			// datumFactuur is date of invoice period, better to use datumAangemaakt for invoice
			HeaderAndFooter event = new HeaderAndFooter(logo, false, factuur.getDatumAangemaakt(), EDocumentType.FACTUUR, EDocumentType.FACTUUR.getPrefix() + factuur.getReferentie(), EReferentieInternType.BEDRIJF.getPrefix() + bedrijf.getSbdrNummer(), null);
			pdfwriter.setPageEvent(event);
		} catch(DocumentException e) {
			LOGGER.error("Error creating PDF headerfooter ", e);
			throw new ServiceException(e);
		}

		pdfdoc.open();

		HeadingContent.createContent(pdfwriter, pdfdoc, "FACTUUR", new Color(COLOR_SBDR_DARKBLUE));

		PdfPTable header = LetterHeader.createHeader(null, bedrijf, dossier, EDocumentType.FACTUUR, false);
		String referentieNummer = factuur.getReferentie();
		//PdfPTable topic = TopicLetter.createTopicLetter(EDocumentType.FACTUUR.getPrefix() + referentieNummer, "Factuur");

		PdfPTable factuurContent = null;
		
		if (klant.isAkkoordIncasso() && klant.getBankrekeningNummer() != null && klant.getTenaamstelling() != null) {
			// May not occur, only for non-active klant (gebruikerbasis.actief = false)
			String bankrekeningnr = "onbekend";
			if(klant != null) bankrekeningnr = klant.getBankrekeningNummer();

			factuurContent = InvoiceContent.createContent(factuur, factuurRegelAggregate, "het bovenstaande bedrag wordt binnen 5 werkdagen automatisch van uw IBAN " + bankrekeningnr + " afgeschreven" + incassantennummertekst + ".");
		}
		else
			factuurContent = InvoiceContent.createContent(factuur, factuurRegelAggregate, "U wordt verzocht het bovenstaande bedrag binnen 14 dagen over te maken op onze rekening.");


		try {
			pdfdoc.add(header);
			//pdfdoc.add(topic);
			pdfdoc.add(factuurContent);
		} catch(DocumentException e) {
			LOGGER.error("Error adding block to PDF", e);
		}

		pdfdoc.close();

		String filename = DocumentUtil.generateFilename(EDocumentType.FACTUUR) + ".pdf";
		//String filename = EDocumentType.FACTUUR.getPrefix() + factuur.getReferentie() + ".pdf";
		DocumentUtil.saveFile(result.toByteArray(), filename);

		// Update factuur record
		try {
			factuur.setFileNaam(filename);
			factuurDataService.save(factuur);
		} catch(DataServiceException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	@Transactional
	public void createMeldingLetter(Integer vanBedrijfId, Integer overBedrijfId, List<Melding> meldingen, Date datumAangemaakt) throws ServiceException {
		if(vanBedrijfId == null) {throw new ServiceException("vanBedrijfId cannot be null");}
		if(overBedrijfId == null) {throw new ServiceException("overBedrijfId cannot be null");}
		if(meldingen == null) {throw new ServiceException("meldingen cannot be null");}

		ByteArrayOutputStream result = new ByteArrayOutputStream();

		com.lowagie.text.Document pdfdoc;
		PdfWriter pdfwriter;
		Bedrijf bedrijfVan = null;
		Bedrijf bedrijfOver = null;
		CIKvKDossier dossierOver = null;
		//CIKvKDossier dossierVan = null;
		Resource logo = null;
		Resource signature = null;
		String referentieNummer = null;

		try {
			bedrijfVan = bedrijfDataService.findByBedrijfId(vanBedrijfId);
			bedrijfOver = bedrijfDataService.findByBedrijfId(overBedrijfId);
			//dossierVan = companyInfoDataService.findOne(bedrijfVan.getCIKvKDossierCikvKdossierId());
			dossierOver = companyInfoDataService.findById(bedrijfOver.getCIKvKDossierCikvKdossierId());

			if(dossierOver == null) throw new ServiceException("Cannot find CompanyInfo dossier of company.");

			//logo = applicationContext.getResource("classpath:/images/Logo_BA_DEF_diap.png");
			logo = applicationContext.getResource("classpath:/images/Logo_CRZB_DEF.png");

			signature = applicationContext.getResource("classpath:/images/handtekening_voorthuizen.png");
			
			//Generate referentienummer
			Date date = new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			int year = cal.get(Calendar.YEAR) % 100;
			referentieNummer = year + SerialNumber.generateRandomSerialNumber8_32();
			while(documentDataService.findByReferentieNummerIntern(referentieNummer) != null) {
				referentieNummer = year + SerialNumber.generateRandomSerialNumber8_32();
			}
		} catch(DataServiceException e) {throw new ServiceException(e);}

		pdfdoc = new com.lowagie.text.Document(PageSize.A4, MARGIN_LEFT, MARGIN_RIGHT, MARGIN_TOP_LETTER_PRINTED, MARGIN_BOTTOM); //30, 30, 130, 30);

		try {
			pdfwriter = PdfWriter.getInstance(pdfdoc, result);

			HeaderAndFooter event = new HeaderAndFooter(logo, false, datumAangemaakt, EDocumentType.MELDINGBRIEF, EDocumentType.MELDINGBRIEF.getPrefix() + referentieNummer, EReferentieInternType.BEDRIJF.getPrefix() + bedrijfOver.getSbdrNummer(), null);
			pdfwriter.setPageEvent(event);
		} catch(DocumentException e) {
			LOGGER.error("Error creating PDF headerfooter ", e);
			throw new ServiceException(e);
		}

		pdfdoc.open();

		// MBR 26-11-2015: bedrijfVan / bedrijfOver swapped
		PdfPTable header = LetterHeader.createHeader(null, bedrijfOver, dossierOver, EDocumentType.MELDINGBRIEF, false);
		PdfPTable topic = TopicLetter.createTopicLetter(EDocumentType.MELDINGBRIEF.getPrefix() + referentieNummer, "voorkom registratie in het Centraal Register Zakelijke Betalingsachterstanden (CRZB)", EDocumentType.MELDINGBRIEF);
		PdfPTable content = MeldingLetterContent.createLetterContent(signature);
		PdfPTable meldingencontent = MeldingLetterMeldingenContent.createContent(bedrijfVan, meldingen, 0);

		try {
			pdfdoc.add(header);
			pdfdoc.add(topic);
			pdfdoc.add(content);
			pdfdoc.setMargins(MARGIN_RIGHT, MARGIN_LEFT, MARGIN_TOP_LETTER_PRINTED, MARGIN_BOTTOM); // swap left/right margin
			pdfdoc.newPage();
			pdfdoc.add(meldingencontent);
		} catch(DocumentException e) {
			LOGGER.error("Error adding block to PDF", e);

		}

		pdfdoc.close();

		String filename = DocumentUtil.generateFilename(EDocumentType.MELDINGBRIEF) + ".pdf";
		DocumentUtil.saveFile(result.toByteArray(), filename);

		// Create document record
		try {
			Document document = new Document();

			document.setBedrijfByBedrijfIdBedrijfId(bedrijfVan.getBedrijfId());
			document.setDatumAangemaakt(new Date());
			document.setDocumentTypeCode(EDocumentType.MELDINGBRIEF.getCode());
			document.setFileNaam(filename);
			document.setNaam("Meldingbrief");
			document.setReferentieNummer(referentieNummer);
			document.setActief(true);

			Document savedDocument = documentDataService.save(document);

			for(Melding melding : meldingen) {
				melding.setDocumentDocumentId(savedDocument.getDocumentId());
				melding.setBriefStatusCode(EBriefStatus.DL_READY.getCode());
				melding.setBriefBatch(null);
				meldingDataService.save(melding);
			}
		} catch(DataServiceException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	@Transactional
	public byte[] createMonitorDetailPdf(Integer gebruikerId, Integer bedrijfId, Integer monitoringId) throws ServiceException {
		// TODO omgevingen platslaan DEV/TST/PRD
		// TODO logo path werkt niet
		ByteArrayOutputStream result = new ByteArrayOutputStream();

		com.lowagie.text.Document pdfdoc;
		PdfWriter pdfwriter;
		MonitoringDetailsTransfer monitoringDetail = null;
		Resource logo = null;
		String referentieNummer = null;
		Bedrijf bedrijfAanvrager = null;
		Bedrijf bedrijf = null;
		Date datumaangemaakt = new Date();
		Gebruiker gebruiker = null;
		if(gebruikerId != null) gebruiker = gebruikerService.findByGebruikerId(gebruikerId);

		try {
			Date date = new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			int year = cal.get(Calendar.YEAR) % 100; // last 2 numbers of year

			bedrijfAanvrager = bedrijfService.getBedrijf(bedrijfId);
			// MBR RA nummer uniek genereren.
			//referentieNummer = bedrijfAanvrager.getSbdrNummer() + "-" + counter;
			// Create new reference number for notification
			referentieNummer = year + SerialNumber.generateRandomSerialNumber8_32();
			while(documentDataService.findByReferentieNummerIntern(referentieNummer) != null) {
				referentieNummer = year + SerialNumber.generateRandomSerialNumber8_32();
			}
			monitoringDetail = bedrijfService.monitoringDetailTransfer(bedrijfId, monitoringId);
			if(monitoringDetail.getMonitoring() != null)
				bedrijf = bedrijfService.getBedrijf(monitoringDetail.getMonitoring().getBedrijfId());

			if(monitoringDetail == null)
				throw new ServiceException("No monitoringDetail data found. Cannot generate monitoringDetail document.");

			//logo = applicationContext.getResource("classpath:/images/Logo_BA_DEF_diap.png");
			logo = applicationContext.getResource("classpath:/images/Logo_CRZB_DEF.png");
		} catch(Exception e) {
			throw new ServiceException(e);
		}

		pdfdoc = new com.lowagie.text.Document(PageSize.A4, MARGIN_LEFT, MARGIN_RIGHT, MARGIN_TOP, MARGIN_BOTTOM); //PageSize.A4, 30, 30, 100, 80
		try {
			pdfwriter = PdfWriter.getInstance(pdfdoc, result);

			//PdfPTable reportHeader = ReportHeaderContent.createContent("MONITORING RAPPORT", monitoringDetail.getMonitoring().getBedrijfsNaam(), monitoringDetail.getMonitoring().getMonitoringReferentieIntern());
			String gebruikerNaam = null;
			if(gebruiker != null) gebruikerNaam = gebruiker.getVoornaam() + " " + gebruiker.getNaam();
			HeaderAndFooter event = new HeaderAndFooter(logo, true, datumaangemaakt, EDocumentType.MONITORINGDETAIL, EDocumentType.MONITORINGDETAIL.getPrefix() + referentieNummer, bedrijfAanvrager.getSbdrNummer(), (gebruikerNaam != null ? gebruikerNaam : ""), null, false); //reportHeader);
			pdfwriter.setPageEvent(event);
		} catch(DocumentException e) {
			LOGGER.error("Error creating PDF headerfooter ", e);
			throw new ServiceException(e);
		}
		pdfdoc.open();

		PdfPTable fromBedrijfContent = ReportFromBedrijfContent.createContent(bedrijf, bedrijfAanvrager);
		boolean betalingsachterstand = false;
		if(monitoringDetail.getMeldingen() != null && monitoringDetail.getMeldingen().length > 0)
			betalingsachterstand = true;

		PdfPTable bedrijfContent = MonitoringBedrijfContent.createContent(monitoringDetail.getMonitoring(), betalingsachterstand);
		PdfPTable meldingContent = MonitoringMeldingContent.createContent(monitoringDetail.getMeldingen());


		try {

			//pdfdoc.add(reportHeader);
			//pdfdoc.add(new Chunk(ls));

			HeadingContent.createContent(pdfwriter, pdfdoc, "MONITORING DETAILS", new Color(COLOR_SBDR_BLUE));

			//pdfdoc.add(fromBedrijfContent);

			//pdfdoc.newPage();

			float leftMargin = pdfdoc.leftMargin();
			float rightMargin = pdfdoc.rightMargin();
			float contentWidth = pdfdoc.getPageSize().getWidth() - pdfdoc.leftMargin() - pdfdoc.rightMargin();

			//addNewTopic(pdfdoc, "Bedrijf");
			addNewTopic(pdfdoc, "Bedrijfsgegevens", true);
			pdfdoc.add(bedrijfContent);

			//if(betalingsachterstand) {
			//pdfdoc.newPage();
			addNewTopic(pdfdoc, "Betalingsachterstand(en) in het register", false);
			pdfdoc.add(meldingContent);
			//}


		} catch(DocumentException e) {
			LOGGER.error("Error adding block to PDF", e);
			throw new ServiceException(e);
		}

		pdfdoc.close();

		return result.toByteArray();

		//		String filename = DocumentUtil.generateFilename(EDocumentType.RAPPORT) + ".pdf";
		//		saveFile(result.toByteArray(), filename);
		//
		//		// Create document record
		//		try {
		//			Document document = new Document();
		//
		//			document.setGebruikerGebruikerId(gebruikerId);
		//			document.setBedrijfByBedrijfIdBedrijfId(bedrijfAanvrager.getBedrijfId());
		//			document.setBedrijfByRapportBedrijfIdBedrijfId(monitoringDetail.getMonitoring().getBedrijfId());
		//			document.setDatumAangemaakt(new Date());
		//			document.setDocumentTypeCode(EDocumentType.MONITORINGDETAIL.getCode());
		//			document.setFileNaam(filename);
		//			document.setNaam("Monitoring");
		//			document.setReferentieNummer(referentieNummer);
		//			document.setActief(true);
		//
		//			documentDataService.save(document);
		//
		//		} catch(DataServiceException e) {
		//			throw new ServiceException(e);
		//		}
	}

	@Override
	@Transactional
	public void createNewAccountLetterPdf(Integer klantId) throws ServiceException {
		// TODO omgevingen platslaan DEV/TST/PRD
		// TODO logo path werkt niet
		ByteArrayOutputStream result = new ByteArrayOutputStream();

		com.lowagie.text.Document pdfdoc;
		PdfWriter pdfwriter;
		Klant klant = null;
		String aanhefbrief = null;
		String aanhefadres = null;
		String tavNaam = null;
		Bedrijf bedrijf = null;
		CIKvKDossier dossier = null;
		Resource signature = null;
		Resource logo = null;
		Date datumaangemaakt = new Date();

		try {
			klant = klantDataService.findByKlantId(klantId);
			if(klant == null) throw new ServiceException("Cannot create newAccount letter. Klant not found.");

			aanhefadres = "";
			aanhefbrief = "";
			if(klant.getGeslacht() != null && klant.getGeslacht().equals("M")) {
				aanhefbrief = "heer ";
				aanhefadres += "de heer ";
			} else if(klant.getGeslacht() != null && klant.getGeslacht().equals("V")) {
				aanhefbrief = "mevrouw ";
				aanhefadres += "mevrouw ";
			}

			tavNaam = "";
			if(klant.getVoornaam() != null) tavNaam += klant.getVoornaam() + ' ' + klant.getNaam();
			else tavNaam += klant.getNaam();

			bedrijf = bedrijfDataService.findByBedrijfId(klant.getBedrijfBedrijfId());
			dossier = companyInfoDataService.findById(bedrijf.getCIKvKDossierCikvKdossierId());

			if(dossier == null) throw new ServiceException("Cannot find CompanyInfo dossier of company.");

			signature = applicationContext.getResource("classpath:/images/handtekening_voorthuizen.png");
			//logo = applicationContext.getResource("classpath:/images/Logo_BA_DEF_diap.png");
			logo = applicationContext.getResource("classpath:/images/Logo_CRZB_DEF.png");
		} catch(DataServiceException e) {
			throw new ServiceException(e);
		}


		pdfdoc = new com.lowagie.text.Document(PageSize.A4, MARGIN_LEFT, MARGIN_RIGHT, MARGIN_TOP_LETTER_PRINTED, MARGIN_BOTTOM); //30, 30, 130, 30);
		//pdfdoc = new com.lowagie.text.Document(PageSize.A4, 30, 30, 100, 80);
		try {
			pdfwriter = PdfWriter.getInstance(pdfdoc, result);

			HeaderAndFooter event = new HeaderAndFooter(logo, false, datumaangemaakt, EDocumentType.AANMELDBRIEF, EDocumentType.AANMELDBRIEF.getPrefix() + bedrijf.getSbdrNummer(), EReferentieInternType.BEDRIJF.getPrefix() + bedrijf.getSbdrNummer(), null);
			pdfwriter.setPageEvent(event);
		} catch(DocumentException e) {
			LOGGER.error("Error creating PDF headerfooter ", e);
			throw new ServiceException(e);
		}
		pdfdoc.open();

		PdfPTable header = LetterHeader.createHeader(aanhefadres + tavNaam, bedrijf, dossier, EDocumentType.AANMELDBRIEF, false);
		String referentieNummer = bedrijf.getSbdrNummer();
		PdfPTable topic = TopicLetter.createTopicLetter(EDocumentType.AANMELDBRIEF.getPrefix() + referentieNummer, "uw verificatiecode", EDocumentType.AANMELDBRIEF);
		PdfPTable content = NewAccountLetterContent.createLetterContent(aanhefbrief + klant.getNaam(), klant.getActivatieCode(), signature);
		try {
			pdfdoc.add(header);
			pdfdoc.add(topic);
			pdfdoc.add(content);
		} catch(DocumentException e) {
			LOGGER.error("Error adding block to PDF", e);
			throw new ServiceException(e);
		}

		pdfdoc.close();

		String filename = DocumentUtil.generateFilename(EDocumentType.AANMELDBRIEF) + ".pdf";
		DocumentUtil.saveFile(result.toByteArray(), filename);

		// Create document record
		try {
			Document document = new Document();

			document.setBedrijfByBedrijfIdBedrijfId(bedrijf.getBedrijfId());
			document.setDatumAangemaakt(datumaangemaakt);
			document.setDocumentTypeCode(EDocumentType.AANMELDBRIEF.getCode());
			document.setFileNaam(filename);
			document.setNaam("Aanmeldbrief");
			document.setReferentieNummer(referentieNummer);
			document.setActief(true);

			documentDataService.save(document);
		} catch(DataServiceException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public byte[] createRemovedCompaniesOverview(PageTransfer<RemovedBedrijfOverviewTransfer> removedBedrijven) throws ServiceException {
		byte[] data = null;
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet();

		cellStyleDate = workbook.createCellStyle();
		cellStyleDate.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("dd-mm-yyyy"));

		String headers[] = {"Bedrijf", "KvK-nummer", "Type", "Status", "Uw referentie", "Referentienummer", "Start datum", "Eind datum", "Aangemaakt door", "E-mailadres", "Functie", "Verwijderd door", "Reden"};


		int j = 0;
		HSSFRow row = sheet.createRow(j++);

		int c = 0;
		for(String header : headers) {
			HSSFCell cell = row.createCell(c++);
			cell.setCellValue(header);
		}

		int count = 0;

		for(RemovedBedrijfOverviewTransfer removedBedrijf : removedBedrijven.getContent()) {
			row = sheet.createRow(j++);

			//Document document = XmlUtil.parse(aanvraagEntity.getXml());
			//CachedXPathAPI xpathAPI = new CachedXPathAPI();

			GebruikerTransfer gebruikerAangemaakt = null;
			if(removedBedrijf.getGebruikerAangemaaktId() != null)
				gebruikerAangemaakt = gebruikerService.findTransferByGebruikerId(removedBedrijf.getGebruikerAangemaaktId(), removedBedrijf.getBedrijfId());

			GebruikerTransfer gebruikerVerwijderd = null;
			if(removedBedrijf.getGebruikerVerwijderdId() != null)
				gebruikerVerwijderd = gebruikerService.findTransferByGebruikerId(removedBedrijf.getGebruikerVerwijderdId(), removedBedrijf.getBedrijfId());

			int i = 0;

			//Bedrijfsnaam
			addCell(row, i++, removedBedrijf.getBedrijfsNaam());

			//KvK nummer
			addCell(row, i++, removedBedrijf.getKvkNummer());

			//Bedrijf referentie
			//addCell(row, i++, removedBedrijf.getSbdrNummer() != null ? removedBedrijf.getSbdrNummer().toString() : "onbekend");

			//Type
			addCell(row, i++, removedBedrijf.getPortfolio());

			//Status
			addCell(row, i++, removedBedrijf.getStatus());

			//Referentie
			addCell(row, i++, removedBedrijf.getReferentieMelding());

			//Referentienummer
			addCell(row, i++, removedBedrijf.getReferentieIntern());

			//Startdatum
			addDateCell(row, i++, removedBedrijf.getDatumStart());

			//Einddatum
			addDateCell(row, i++, removedBedrijf.getDatumEinde());

			//Aangemaakt door
			addCell(row, i++, getNaamFromGebruikerTransfer(gebruikerAangemaakt));

			//E-mail van opvrager
			addCell(row, i++, gebruikerAangemaakt.getEmailAdres());

			//Functie van opvrager
			addCell(row, i++, gebruikerAangemaakt.getFunctie());

			//Verwijderd door
			addCell(row, i++, getNaamFromGebruikerTransfer(gebruikerVerwijderd));

			//Reden
			if(removedBedrijf.getRedenVerwijderenCode() != null)
				addCell(row, i++, ERedenVerwijderenMelding.get(removedBedrijf.getRedenVerwijderenCode()).getOmschrijving());
			else addCell(row, i++, "Anders");

			count = i;
		}

		for(int col = 0; col < count; col++) {
			System.out.println("auto " + col);
			sheet.autoSizeColumn(col);
		}

		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			try {
				workbook.write(bos);
			} finally {
				bos.close();
			}
			data = bos.toByteArray();
		} catch(IOException e) {
			throw new ServiceException("Cannot convert workbook to byte[]: " + e.getMessage());
		}

		return data;
	}

	@Override
	public Document findByReferentieIntern(String ref) throws ServiceException{
		try{
			return documentDataService.findByReferentieNummerIntern(ref);
		} catch(DataServiceException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	@Transactional
	public BedrijfReportTransfer createReportPdf(Integer gebruikerId, Integer bedrijfId, Integer fromBedrijfId) throws ServiceException {
		// TODO omgevingen platslaan DEV/TST/PRD
		// TODO logo path werkt niet
		ByteArrayOutputStream result = new ByteArrayOutputStream();

		com.lowagie.text.Document pdfdoc;
		PdfWriter pdfwriter;
		BedrijfReportTransfer bedrijfReport = new BedrijfReportTransfer();
		Resource logo = null;
		Resource warning_icon = null;
		Resource info_icon = null;
		String referentieNummer = null;
		Date datumaangemaakt = new Date();
		Gebruiker gebruiker = gebruikerService.findByGebruikerId(gebruikerId);

		try {
			Date date = new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			int year = cal.get(Calendar.YEAR) % 100; // last 2 numbers of year

			Integer counter = bedrijfService.newRapportCounter(bedrijfId);
			Bedrijf bedrijfAanvrager = bedrijfService.getBedrijf(bedrijfId);
			// MBR RA nummer uniek genereren.
			//referentieNummer = bedrijfAanvrager.getSbdrNummer() + "-" + counter;
			// Create new reference number for notification
			referentieNummer = year + SerialNumber.generateRandomSerialNumber8_32();
			while(documentDataService.findByReferentieNummerIntern(referentieNummer) != null) {
				referentieNummer = year + SerialNumber.generateRandomSerialNumber8_32();
			}
			bedrijfReport = bedrijfService.getReportData(fromBedrijfId, bedrijfId, EDocumentType.RAPPORT.getPrefix() + referentieNummer);
			if(bedrijfReport == null)
				throw new ServiceException("No bedrijfReport data found. Cannot generate report document.");

			//logo = applicationContext.getResource("classpath:/images/Logo_BA_DEF_diap.png");
			logo = applicationContext.getResource("classpath:/images/Logo_CRZB_DEF.png");
			warning_icon = applicationContext.getResource("classpath:/images/warning.png");
			info_icon = applicationContext.getResource("classpath:/images/info-circle.png");
		} catch(Exception e) {
			throw new ServiceException(e);
		}

		pdfdoc = new com.lowagie.text.Document(PageSize.A4, MARGIN_LEFT, MARGIN_RIGHT, MARGIN_TOP, MARGIN_BOTTOM); //PageSize.A4, 30, 30, 100, 80
		try {
			pdfwriter = PdfWriter.getInstance(pdfdoc, result);

			String gebruikerNaam = null;
			if(gebruiker != null) gebruikerNaam = gebruiker.getVoornaam() + " " + gebruiker.getNaam();
			//PdfPTable reportHeader = ReportHeaderContent.createContent("BETALINGSACHTERSTANDEN RAPPORT", bedrijfReport.getBedrijf().getBedrijfsNaam(), EDocumentType.RAPPORT.getPrefix() + referentieNummer);
			HeaderAndFooter event = new HeaderAndFooter(logo, true, datumaangemaakt, EDocumentType.RAPPORT, EDocumentType.RAPPORT.getPrefix() + referentieNummer, EReferentieInternType.BEDRIJF.getPrefix() + bedrijfReport.getBedrijfaanvrager().getSbdrNummer(), (gebruikerNaam != null ? gebruikerNaam : ""), null, true); //reportHeader);
			//Footer eventfooter = new Footer(true);
			pdfwriter.setPageEvent(event);
			//pdfwriter.setPageEvent(eventfooter);
		} catch(DocumentException e) {
			LOGGER.error("Error creating PDF headerfooter ", e);
			throw new ServiceException(e);
		}
		pdfdoc.open();

		PdfPTable fromBedrijfContent = ReportFromBedrijfContent.createContent(bedrijfReport.getBedrijf(), bedrijfReport.getBedrijfaanvrager());
		boolean betalingsachterstand = false;
		if(bedrijfReport.getMeldingen() != null && bedrijfReport.getMeldingen().length > 0) betalingsachterstand = true;

		PdfPTable emptyChartContent = ReportEmptyChartContent.createContent();

		//PdfPTable headingContent = HeadingContent.createContent(pdfdoc, "TEST", new Color(0x2C3D4F));
		PdfPTable bedrijfContent = ReportBedrijfContent.createContent(bedrijfReport.getBedrijf(), betalingsachterstand, bedrijfReport.getRatingScore(), bedrijfReport.getRatingScoreIndicatorMessage());
		PdfPTable bedrijfDetailsContentRechtspersoon = ReportBedrijfDetailsContent.createContentRechtspersoon(bedrijfReport);
		PdfPTable bedrijfDetailsContentOnderneming = ReportBedrijfDetailsContent.createContentOnderneming(bedrijfReport);
		PdfPTable bedrijfDetailsContentMoederMaatschappijen = ReportBedrijfDetailsContent.createContentMoederMaatschappijen(bedrijfReport);
		PdfPTable bedrijfDetailsContentBestuurders = ReportBedrijfDetailsContent.createContentBestuurders(bedrijfReport);
		PdfPTable bedrijfDetailsContentContact = ReportBedrijfDetailsContent.createContentContactgegevens(bedrijfReport);
		PdfPTable bedrijfDetailsContentAandeelhouders = ReportBedrijfDetailsContent.createContentAandeelhouders(bedrijfReport);
		PdfPTable bedrijfDetailsContentCurator = ReportBedrijfDetailsContent.createContentCurator(bedrijfReport);

		PdfPTable statusContent = ReportStatusHeaders.createContent(bedrijfReport);
		PdfPTable overviewContent = ReportOverviewContent.createContent(bedrijfReport, warning_icon, info_icon);
		PdfPTable meldingenAlgemeenContent = ReportMeldingAlgemeenContent.createContent(bedrijfReport);
		PdfPTable meldingContent = ReportMeldingContent.createContent(pdfdoc, bedrijfReport);
		//PdfPTable historieContent = ReportHistoryContent.createContent(bedrijfReport, warning_icon, info_icon);


		try {
			//pdfdoc.add(reportHeader);
			//pdfdoc.add(new Chunk(ls));

			//pdfdoc.add(headingContent);
			HeadingContent.createContent(pdfwriter, pdfdoc, "BETALINGSACHTERSTANDENRAPPORT", new Color(COLOR_SBDR_DARKBLUE));

			pdfdoc.add(fromBedrijfContent);

			pdfdoc.newPage();

			//addNewTopic(pdfdoc, "Samenvatting");
			HeadingContent.createContent(pdfwriter, pdfdoc, "SAMENVATTING", new Color(COLOR_SBDR_BLUE));
			pdfdoc.add(statusContent);
			pdfdoc.add(bedrijfContent);
			pdfdoc.add(overviewContent);

			float leftMargin = pdfdoc.leftMargin();
			float rightMargin = pdfdoc.rightMargin();
			float contentWidth = pdfdoc.getPageSize().getWidth() - pdfdoc.leftMargin() - pdfdoc.rightMargin();

			boolean newpageforgraph = false;
			// if more than 4 remarks, graph won't fit on page, so new page
			//if (bedrijfReport.getOpmerkingen() != null && bedrijfReport.getOpmerkingen().length > 4) {
			//	newpageforgraph = true;
			//	pdfdoc.newPage();
			//}

			addGraph("Opgevraagde rapporten laatste 14 dagen", "dag/maand", bedrijfReport.getReportsLastTwoWeeks(), leftMargin, rightMargin, contentWidth, pdfwriter, pdfdoc);
			//pdfdoc.add(emptyChartContent);

			// if (not already newpage for graph, create new page
			//if (!newpageforgraph)
			pdfdoc.newPage();
			// else if newpage created for graph, but no customer contact data, graph will fit on same page. Create new page if there is customer contact data
			//else if (newpageforgraph && bedrijfReport.getKvkDossierTransfer() != null &&
			//		(bedrijfReport.getKvkDossierTransfer().isKlant() || bedrijfReport.getKvkDossierTransfer().isKvkContact()))
			//	pdfdoc.newPage();

			//addNewTopic(pdfdoc, "Bedrijf");
			HeadingContent.createContent(pdfwriter, pdfdoc, "BEDRIJFSGEGEVENS", new Color(COLOR_SBDR_BLUE));
			addNewTopic(pdfdoc, null, true);
			pdfdoc.add(bedrijfDetailsContentRechtspersoon);
			addNewTopic(pdfdoc, "Overige gegevens", false);
			pdfdoc.add(bedrijfDetailsContentOnderneming);
			if(bedrijfReport.getKvkDossierTransfer().getCuratorTransfer() != null) {
				addNewTopic(pdfdoc, "Contactgegevens curator", false);
				pdfdoc.add(bedrijfDetailsContentCurator);
			} else {
				addNewTopic(pdfdoc, "Contactgegevens", false);
				pdfdoc.add(bedrijfDetailsContentContact);
			}
			
			boolean hasAnyParent = bedrijfReport.getParent() != null || bedrijfReport.getUltimateParent() != null;
			boolean hasKvKBestuurders = bedrijfReport.getKvkDossierTransfer().getKvkBestuurderTransfer() != null && !bedrijfReport.getKvkDossierTransfer().getKvkBestuurderTransfer().isEmpty();
			boolean hasAandeelhouders = bedrijfReport.getKvkDossierTransfer().getAandeelhoudersTransfer() != null && !bedrijfReport.getKvkDossierTransfer().getAandeelhoudersTransfer().isEmpty();

			if(hasAnyParent||hasKvKBestuurders||hasAandeelhouders){
				pdfdoc.newPage();
				HeadingContent.createContent(pdfwriter, pdfdoc, "BESTUURDERS", new Color(COLOR_SBDR_BLUE));
				boolean headerspace = true;
				if(hasAnyParent) {
					addNewTopic(pdfdoc, null, headerspace);
					headerspace = false;
					pdfdoc.add(bedrijfDetailsContentMoederMaatschappijen);
				}
				if(hasKvKBestuurders) {
					addNewTopic(pdfdoc, "Directiegegevens (P)", headerspace);
					headerspace = false;
					pdfdoc.add(bedrijfDetailsContentBestuurders);
				}
				if(hasAandeelhouders) {
					addNewTopic(pdfdoc, "Directiegegevens (B)", headerspace);
					headerspace = false;
					pdfdoc.add(bedrijfDetailsContentAandeelhouders);
				}
			}

			pdfdoc.newPage();
			//addNewTopic(pdfdoc, "Vermeldingen algemeen");
			HeadingContent.createContent(pdfwriter, pdfdoc, "BETALINGSACHTERSTANDEN ALGEMEEN", new Color(COLOR_SBDR_BLUE));
			pdfdoc.add(meldingenAlgemeenContent);

			addGraph("Actieve betalingsachterstanden", "maand/jaar", bedrijfReport.getMeldingenLastYear(), leftMargin, rightMargin, contentWidth, pdfwriter, pdfdoc);
			pdfdoc.add(emptyChartContent);
			if(betalingsachterstand) {
				pdfdoc.newPage();
				//addNewTopic(pdfdoc, "Vermeldingen");
				HeadingContent.createContent(pdfwriter, pdfdoc, "VERMELDINGEN", new Color(COLOR_SBDR_BLUE));
				pdfdoc.add(meldingContent);
			}

			//			if(bedrijfReport.getHistorie() != null && bedrijfReport.getHistorie().length > 0) {
			//				pdfdoc.newPage();
			//				//addNewTopic(pdfdoc, "Historie overzicht");
			//				HeadingContent.createContent(pdfwriter, pdfdoc, "HISTORIE OVERZICHT", new Color(COLOR_SBDR_BLUE));
			//				pdfdoc.add(historieContent);
			//			}

		} catch(DocumentException e) {
			LOGGER.error("Error adding block to PDF", e);
			throw new ServiceException(e);
		}

		pdfdoc.close();

		String filename = DocumentUtil.generateFilename(EDocumentType.RAPPORT) + ".pdf";
		DocumentUtil.saveFile(result.toByteArray(), filename);

		// Create document record
		try {
			Document document = new Document();

			document.setGebruikerGebruikerId(gebruikerId);
			document.setBedrijfByBedrijfIdBedrijfId(bedrijfReport.getBedrijfaanvrager().getBedrijfId());
			document.setBedrijfByRapportBedrijfIdBedrijfId(bedrijfReport.getBedrijf().getBedrijfId());
			document.setDatumAangemaakt(datumaangemaakt);
			document.setDocumentTypeCode(EDocumentType.RAPPORT.getCode());
			document.setFileNaam(filename);
			document.setNaam("Rapport");
			document.setReferentieNummer(referentieNummer);
			document.setActief(true);

			documentDataService.save(document);

			Integer eigenBedrijfId = null;
			if (gebruiker != null)
				eigenBedrijfId = gebruiker.getBedrijfBedrijfId();
			
			if (eigenBedrijfId == null || eigenBedrijfId.equals(fromBedrijfId))		
				productService.purchaseRapport(fromBedrijfId, null, document.getDatumAangemaakt());
			else
				productService.purchaseRapport(eigenBedrijfId, fromBedrijfId, document.getDatumAangemaakt());

			emailService.sendReportRequestedEmail(gebruiker, (EDocumentType.RAPPORT.getPrefix() + referentieNummer));
		} catch(DataServiceException e) {
			throw new ServiceException(e);
		}

		return bedrijfReport;
	}


	@Override
	public byte[] getBatchDocument(Integer batchDocumentId) throws ServiceException {
		if(batchDocumentId != null) {
			try {
				Document d = documentDataService.findById(batchDocumentId);
				if(d != null && d.getFileNaam() != null) {
					internalProcessService.setBatchAsDownloaded(d.getBriefBatchBriefBatchId());

					return getFile(d.getFileNaam(), d.getDatumAangemaakt(), false);
				} else throw new ServiceException("Batch document not found");
			} catch(DataServiceException e) {
				throw new ServiceException(e);
			}
		} else throw new ServiceException("Parameter batchDocumentId cannot be empty");
	}


	@Override
	public Document getDocument(Integer document_ID) throws ServiceException {
		LOGGER.info("Method getDocument.");

		if(document_ID != null) {
			try {
				return documentDataService.findById(document_ID);
			} catch(DataServiceException e) {
				throw new ServiceException(e);
			}
		} else {
			LOGGER.warn("Method getDocument. Kan geen Document ophalen als key niet is meegegeven.");
			return null;
		}
	}


	@Override
	public byte[] getDocumentContent(EDocumentType doctype, String reference) throws ServiceException {
		try {
			if(reference != null) {
				String ref = null;

				if(doctype.equals(EDocumentType.RAPPORT)) ref = SearchUtil.repNumber(reference);
				else if(doctype.equals(EDocumentType.AANMELDBRIEF)) ref = SearchUtil.abrNumber(reference);
				else if(doctype.equals(EDocumentType.MELDINGBRIEF)) ref = SearchUtil.mbrNumber(reference);

				Document doc = documentDataService.findDocumentByReference(ref);

				if(doc != null && doc.getFileNaam() != null)
					return getFile(doc.getFileNaam(), doc.getDatumAangemaakt(), false);
				else throw new ServiceException("Document by reference document not found: " + reference);
			} else throw new ServiceException("Cannot get document. Reference is null");
		} catch(DataServiceException e) {
			throw new ServiceException("Error fetching document data: " + e.getMessage());
		}
	}


	@Override
	public byte[] getFactuur(Integer fId) throws ServiceException {
		if(fId != null) {
			try {
				Factuur f = factuurDataService.findByFactuurId(fId);

				if(f != null) return getFactuur(f.getFileNaam(), f.getDatumAangemaakt());
				else throw new ServiceException("Factuur with factuurId " + fId + " not found");
			} catch(DataServiceException e) {
				throw new ServiceException(e.getMessage());
			}
		} else throw new ServiceException("Cannot get factuur, factuurId is null");
	}


	@Override
	@Transactional(readOnly = true, propagation=Propagation.SUPPORTS)
	public byte[] getMonitoredCompaniesOverview(Integer bedrijfId) throws ServiceException {
		try {
			if(bedrijfId != null) {
				List<Sort.Order> orders = new LinkedList<Sort.Order>();

				Order order = null;
				Direction sortDirection = null;

				order = new Order(Sort.Direction.ASC, "datumEinde");

				orders.add(order);

				// Max 10000!!!!!
				PageRequest pageRequest = new PageRequest(0, 10000, new Sort(orders));

				PageTransfer<MonitoringOverviewTransfer> monitoringBedrijven = bedrijfService.findActiveMonitoringOfBedrijf(bedrijfId, null, pageRequest);


				return createMonitoringCompaniesOverview(monitoringBedrijven, bedrijfId);
			} else throw new ServiceException("Cannot create removedBedrijven overview. Reference is null");
		} catch(ServiceException e) {
			throw new ServiceException("Error fetching removedBedrijven data: " + e.getMessage());
		}
	}


	@Override
	public byte[] getNewAccountLetterContent(Integer bedrijfId) throws ServiceException {
		try {
			if(bedrijfId != null) {
				Document doc = documentDataService.findNewAccountLetterByBedrijfId(bedrijfId);

				if(doc != null && doc.getFileNaam() != null) {
					return getFile(doc.getFileNaam(), doc.getDatumAangemaakt(), false);
				} else throw new ServiceException("New account letter document not found");
			} else throw new ServiceException("Cannot get new account letter document. BedrijfId is null");
		} catch(DataServiceException e) {
			throw new ServiceException("Error fetching document data: " + e.getMessage());
		}
	}


	@Override
	public byte[] getNotificationLetterContent(Integer meldingId) throws ServiceException {
		try {
			if(meldingId != null) {
				Document doc = documentDataService.findNotificationLetterByMeldingId(meldingId);

				if(doc != null && doc.getFileNaam() != null) {
					return getFile(doc.getFileNaam(), doc.getDatumAangemaakt(), false);
				} else throw new ServiceException("Notification letter document not found");
			} else throw new ServiceException("Cannot get notification letter document. MeldingId is null");
		} catch(DataServiceException e) {
			throw new ServiceException("Error fetching document data: " + e.getMessage());
		}
	}


	@Override
	@Transactional(readOnly = true, propagation=Propagation.SUPPORTS)
	public byte[] getNotifiedCompaniesOverview(Integer bedrijfId) throws ServiceException {
		try {
			if(bedrijfId != null) {

				// Max 10000!!!!!
				PageRequest pageRequest = new PageRequest(0, 10000);

				PageTransfer<MeldingOverviewTransfer> notificationBedrijven = bedrijfService.findActiveMeldingenOfBedrijf(bedrijfId, null, pageRequest);


				return createNotificationCompaniesOverview(notificationBedrijven, bedrijfId);
			} else throw new ServiceException("Cannot create removedBedrijven overview. Reference is null");
		} catch(ServiceException e) {
			throw new ServiceException("Error fetching removedBedrijven data: " + e.getMessage());
		}
	}


	@Override
	@Transactional(readOnly = true, propagation=Propagation.SUPPORTS)
	public byte[] getRemovedCompaniesOverview(Integer bedrijfId) throws ServiceException {
		try {
			if(bedrijfId != null) {
				List<Sort.Order> orders = new LinkedList<Sort.Order>();

				Order order = null;
				Direction sortDirection = null;

				order = new Order(Sort.Direction.ASC, "datumEinde");

				orders.add(order);

				// Max 10000!!!!!
				PageRequest pageRequest = new PageRequest(0, 10000, new Sort(orders));

				PageTransfer<RemovedBedrijfOverviewTransfer> removedBedrijven = bedrijfService.findRemovedBedrijvenOfBedrijf(bedrijfId, "all", null, pageRequest);


				return createRemovedCompaniesOverview(removedBedrijven);
			} else throw new ServiceException("Cannot create removedBedrijven overview. Reference is null");
		} catch(ServiceException e) {
			throw new ServiceException("Error fetching removedBedrijven data: " + e.getMessage());
		}
	}


	@Override
	@Transactional(readOnly = true, propagation=Propagation.SUPPORTS)
	public byte[] getReportedCompaniesOverview(Integer gebruikerId, Integer bedrijfId) throws ServiceException {
		try {
			if(bedrijfId != null) {

				// Max 10000!!!!!
				PageRequest pageRequest = new PageRequest(0, 10000);

				PageTransfer<ReportRequestedTransfer> reportedBedrijven = getRequestedReportsByGebruikerIdBedrijfId(gebruikerId, bedrijfId, null, pageRequest);


				return createReportedCompaniesOverview(reportedBedrijven);
			} else throw new ServiceException("Cannot create reportBedrijven overview. Reference is null");
		} catch(ServiceException e) {
			throw new ServiceException("Error fetching reportBedrijven data: " + e.getMessage());
		}
	}


	@Override
	@Transactional(readOnly = true)
	public PageTransfer<ReportRequestedTransfer> getRequestedReportsByGebruikerIdBedrijfId(Integer gebruikerId, Integer bedrijfId, String search, Pageable pageable) throws ServiceException {
		PageTransfer<ReportRequestedTransfer> result = null;

		try {
			boolean isKlant = false;
			boolean isProspect = false;
			boolean isAdresOk = true;
			String bedrijfTelefoonNummer = null;

			try {

				Klant klant = klantDataService.findByGebruikerId(gebruikerId);
				if(klant != null) {
					isKlant = true;
					if(klant.getKlantStatusCode().equals(EKlantStatus.PROSPECT.getCode())) isProspect = true;
					else if(klant.getKlantStatusCode().equals(EKlantStatus.DATA_NOK.getCode())) isAdresOk = false;

					bedrijfTelefoonNummer = bedrijfDataService.findByBedrijfId(bedrijfId).getTelefoonnummer();
				}

			} catch(DataServiceException e) {
				// error in finding klant

			}

			Page<Object[]> objects = documentDataService.findRequestedReportsOfGebruikerIdBedrijfId(gebruikerId, bedrijfId, search, pageable);

			if(objects != null) {
				Bedrijf bedrijf = null;
				if (bedrijfId != null)
					bedrijf = bedrijfDataService.findByBedrijfId(bedrijfId);
				result = ConvertUtil.convertPageToReportRequestedPageTransfer(objects, bedrijf, gebruikerId, bedrijfTelefoonNummer, isProspect, isAdresOk);
				if(result.getContent() != null) {
					List<BedrijfTransfer> bedrijven = new ArrayList<BedrijfTransfer>();
					for(ReportRequestedTransfer reportRequested : result.getContent()) {
						if(reportRequested.getBedrijf() != null) bedrijven.add(reportRequested.getBedrijf());
					}
					bedrijfService.addRapportTodayCreated(bedrijfId, bedrijven);
				}
			}

			return result;
		} catch(DataServiceException e) {
			throw new ServiceException(e);
		} catch(IllegalAccessException e) {
			throw new ServiceException(e);
		} catch(InvocationTargetException e) {
			throw new ServiceException(e);
		}
	}


	@Override
	@Transactional
	public byte[] getSupportAttachment(Integer sBId) throws ServiceException {
		if(sBId != null) {
			try {
				SupportBestand sB = supportDataService.findSupportTicketBestandBySupportBestandId(sBId);

				if(sB != null) return getFile(sB.getVolledigPad(), sB.getDatumUpload(), true);
				else throw new ServiceException("Document by document Id not found");
			} catch(DataServiceException e) {
				throw new ServiceException("Error fetching document data: " + e.getMessage());
			}
		} else throw new ServiceException("Error in getSupportAttachment: Integer sBId is null");
	}


	@Override
	@Transactional
	public void removeOldNewAccountLetter(Integer bedrijfId) throws ServiceException {
		try {
			if(bedrijfId != null) {
				Document doc = documentDataService.findNewAccountLetterByBedrijfId(bedrijfId);

				if(doc != null) documentDataService.delete(doc);
			}
		} catch(DataServiceException e) {
			throw new ServiceException(e);
		}
	}


	@Override
	@Transactional
	public void removeOldNotificationLetter(Integer meldingId) throws ServiceException {
		try {
			if(meldingId != null) {
				Document doc = documentDataService.findNotificationLetterByMeldingId(meldingId);

				if(doc != null) documentDataService.delete(doc);
			}
		} catch(DataServiceException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public void removeAllDocumentsOfBedrijf(Integer bedrijfId) throws ServiceException {
		if(bedrijfId!=null){
			try{
				List<Document> allDocs = documentDataService.findAllDocumentsOfBedrijf(bedrijfId);

				for(Document d : allDocs){
					d.setActief(false);

					documentDataService.save(d);
				}
			} catch(DataServiceException e){
				throw new ServiceException(e);
			}
		} else throw new ServiceException(new ErrorService(ErrorService.PARAMETER_IS_EMPTY).getErrorMsg());
	}


	private static void addCell(HSSFRow row, int i, String value) {
		HSSFCell cell = row.createCell(i);
		cell.setCellValue(value);
	}

	private static void addCurrencyCell(HSSFRow row, int i, BigDecimal bedrag) {

		HSSFCell cell = row.createCell(i);

		if(bedrag != null) {
			cell.setCellValue(bedrag.doubleValue());
		}

		cell.setCellStyle(cellStyleCurrency);
	}

	private static void addDateCell(HSSFRow row, int i, Date datum) {
		HSSFCell cell = row.createCell(i);

		if(datum != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(datum);

			cell.setCellValue(calendar);
		}

		cell.setCellStyle(cellStyleDate);
	}

	private void addGraph(String title, String seriesname, ChartDataTransfer[] chartData, float leftMargin, float rightMargin, float contentWidth, PdfWriter docWriter, com.lowagie.text.Document doc) {
		Color bluecolor = new Color(COLOR_SBDR_BLUE);
		//    	// build up the dataset for the char
		//    	XYSeriesCollection dataset = new XYSeriesCollection();
		//
		//    	XYSeries series = new XYSeries("XYGraph");
		//    	series.add(1, 1);
		//    	series.add(2, 3);
		//    	series.add(3, 9);
		//    	series.add(4, 11);
		//
		//    	dataset.addSeries(series);

		CategoryDataset dataset = createDataset(seriesname, chartData);

		int maxVal = 0;
		for(ChartDataTransfer chartdata : chartData) {
			if(chartdata.getAantal() > maxVal) maxVal = chartdata.getAantal();
		}

		// max range interval of 5 + one interval
		int maxRange = maxVal / 5;
		maxRange++;
		if(maxVal % 5 > 5 / 2) maxRange++;

		// set up the chart
		JFreeChart chart = ChartFactory.createLineChart(title, // chart title
				null, //"x-axis",   // domain axis label
				null, //"y-axis",   // range axis label
				dataset,    // data
				PlotOrientation.VERTICAL,   // orientation
				true,   // include legend
				true,   // tooltips
				false   // urls
		);

		CategoryPlot plot = chart.getCategoryPlot();
		// border around chart area
		plot.setOutlineVisible(false);
		plot.setBackgroundPaint(Color.white);
		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlineStroke(new BasicStroke((float) 0.2));
		plot.setDomainGridlinesVisible(true);
		plot.setDomainGridlineStroke(new BasicStroke((float) 0.2));
		plot.setDomainGridlinePaint(Color.lightGray);
		plot.setRangeGridlinePaint(Color.lightGray);
		plot.getRenderer().setSeriesPaint(0, bluecolor);

		// renderer props
		// help: XYLineAndShapeRendererDemo2.java
		java.awt.geom.Ellipse2D.Double double1 = new java.awt.geom.Ellipse2D.Double(-1D, -1D, 2D, 2D);
		LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
		// MBR 28-09-2018 not available any more:
		// renderer.setBasShapesVisible
		renderer.setDefaultShapesVisible(true);
		DecimalFormat decimalformat1 = new DecimalFormat("##");
		// MBR 28-09-2018 not available any more:
		// renderer..setBaseItemLabelGenerator
		// renderer.setBaseItemLabelFont
		// renderer.setBaseItemLabelsVisible
		// renderer.setBaseSEriesVisible
		renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}", decimalformat1));
		renderer.setDefaultItemLabelFont(new java.awt.Font("Calibri", Font.NORMAL, 6));
		renderer.setDefaultItemLabelsVisible(true);
		renderer.setDefaultSeriesVisible(true);
		renderer.setSeriesShape(0, double1);
		renderer.setSeriesPaint(0, bluecolor);
		renderer.setSeriesFillPaint(0, bluecolor);
		//renderer.setSeriesOutlinePaint(0, Color.gray);
		renderer.setUseFillPaint(true);

		// axis labels
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		rangeAxis.setAutoRangeIncludesZero(true);
		rangeAxis.setRange(0.00, maxRange * 5.0);
		rangeAxis.setTickUnit(new NumberTickUnit(5));
		rangeAxis.setTickLabelFont(new java.awt.Font("Calibri", Font.NORMAL, 6));
		rangeAxis.setTickMarksVisible(false);
		rangeAxis.setAxisLineVisible(false);

		CategoryAxis domainAxis = (CategoryAxis) plot.getDomainAxis();
		domainAxis.setTickLabelFont(new java.awt.Font("Calibri", Font.NORMAL, 6));
		domainAxis.setAxisLineVisible(false);
		domainAxis.setTickMarksVisible(false);
		domainAxis.setMaximumCategoryLabelLines(2);
		//domainAxis.setMaximumCategoryLabelWidthRatio((float)50.0);

		// trick to change the default font of the chart
		//chart.setTitle(new TextTitle(title, new java.awt.Font("Calibri", Font.NORMAL, 10)));
		chart.setTitle(new TextTitle(title, new java.awt.Font("Calibri", Font.BOLD, 8)));
		chart.setBackgroundPaint(Color.white);
		chart.setBorderPaint(bluecolor); // Color.lightGray
		chart.setBorderStroke(new BasicStroke((float) 0.5));
		chart.setBorderVisible(true);
		chart.setPadding(new RectangleInsets(UnitType.ABSOLUTE, 0, 0, 10, 0));
		//chart.getLegend(0).setFrame(BlockBorder.NONE);
		chart.getLegend(0).setVisible(false);
		//chart.getTitle().setPaint(bluecolor);
		//chart.getTitle().setHorizontalAlignment(HorizontalAlignment.LEFT);
		//chart.getTitle().setPadding(new RectangleInsets(UnitType.ABSOLUTE, 5, 5, 2, 2));
		chart.getTitle().setPaint(Color.WHITE);
		chart.getTitle().setBackgroundPaint(bluecolor);
		chart.getTitle().setHorizontalAlignment(HorizontalAlignment.CENTER);
		chart.getTitle().setPadding(new RectangleInsets(UnitType.ABSOLUTE, 5, 5, 2, 2));
		chart.getTitle().setExpandToFitSpace(true);

		int width = (int) contentWidth; //350;
		int height = 150;

		// get the direct pdf content
		PdfContentByte dc = docWriter.getDirectContent();

		// get a pdf template from the direct content
		PdfTemplate tp = dc.createTemplate(width, height);

		// create an AWT renderer from the pdf template
		Graphics2D g2 = tp.createGraphics(width, height, new DefaultFontMapper());
		Rectangle2D r2D = new Rectangle2D.Double(0, 0, width, height);
		chart.draw(g2, r2D, null);
		g2.dispose();

		// add the rendered pdf template to the direct content
		// you will have to play around with this because the chart is absolutely positioned.
		// 38 is just a typical left margin
		// docWriter.getVerticalPosition(true) will approximate the position that the content above the chart ended

		dc.addTemplate(tp, (int) leftMargin, docWriter.getVerticalPosition(true) - height);    // x = 38

		try {
			// add template to doc for object flow
			Image tmpImage = Image.getInstance(tp);
			doc.add(tmpImage);
			if(title.equals("Actieve betalingsachterstanden")) {
				Paragraph p = new Paragraph(new Phrase("Let op, genoemde aantallen omvatten niet de reeds opgeloste betalingsachterstanden.\nDeze worden definitief verwijderd uit het register en zullen niet worden weergegeven.", PrintUtil.calibri_10_italic));
				doc.add(p);
			}
		} catch(Exception e) {

		}
	}

	private void addNewTopic(com.lowagie.text.Document doc, String topic, boolean paddingtopic) throws DocumentException {

		//doc.add(ReportTopicSeparator.createContent(topic, paddingtopic));
		if(topic != null) doc.add(ReportTopicSeparator.createUnderline(topic, paddingtopic));
		else {
			doc.add(ReportTopicSeparator.createContent(topic, paddingtopic));

			if(!paddingtopic) {
				// MBR line
				LineSeparator ls = new LineSeparator();
				//ls.setLineColor(new Color(COLOR_SBDR_BLUE));
				doc.add(new Chunk(ls));
			}
		}
	}

	/**
	 * Creates a sample dataset.
	 *
	 * @return The dataset.
	 */
	private CategoryDataset createDataset(String seriesname, ChartDataTransfer[] chartData) {

		// row keys...

		// create the dataset...
		final DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		for(ChartDataTransfer item : chartData) {
			dataset.addValue(item.getAantal(), seriesname, item.getLabel());
		}

		return dataset;

	}

	private byte[] createMonitoringCompaniesOverview(PageTransfer<MonitoringOverviewTransfer> monitoringBedrijven, Integer bedrijfId) throws ServiceException {
		byte[] data = null;
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet();

		cellStyleDate = workbook.createCellStyle();
		cellStyleDate.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("dd-mm-yyyy"));


		String headers[] = {"Bedrijf", "KvK-nummer", "Referentienummer", "Status", "Start datum", "Aangemaakt door", "E-mailadres", "Functie"};


		int j = 0;
		HSSFRow row = sheet.createRow(j++);

		int c = 0;
		for(String header : headers) {
			HSSFCell cell = row.createCell(c++);
			cell.setCellValue(header);
		}

		int count = 0;

		for(MonitoringOverviewTransfer monitoringBedrijf : monitoringBedrijven.getContent()) {
			row = sheet.createRow(j++);

			//Document document = XmlUtil.parse(aanvraagEntity.getXml());
			//CachedXPathAPI xpathAPI = new CachedXPathAPI();

			GebruikerTransfer gebruikerAangemaakt = null;
			if(monitoringBedrijf.getGebruikerAangemaaktId() != null)
				gebruikerAangemaakt = gebruikerService.findTransferByGebruikerId(monitoringBedrijf.getGebruikerAangemaaktId(), bedrijfId);

			//            GebruikerTransfer gebruikerVerwijderd = null;
			//            if (monitoringBedrijf.getGebruikerVerwijderdId() != null)
			//            	gebruikerService.findTransferByGebruikerId(monitoringBedrijf.getGebruikerAangemaaktId());


			int i = 0;

			//Bedrijfsnaam
			addCell(row, i++, monitoringBedrijf.getBedrijfsNaam());

			//KvK nummer
			addCell(row, i++, monitoringBedrijf.getKvkNummer());

			//Rapport referentie
			addCell(row, i++, monitoringBedrijf.getMonitoringReferentieIntern());

			//Monitor status
			if(monitoringBedrijf.getVerwijderd() != null) addCell(row, i++, "verwijderd");
			else addCell(row, i++, "actief");

			//Start monitor
			addDateCell(row, i++, monitoringBedrijf.getToegevoegd());
			//            if (monitoringBedrijf.getVerwijderd() != null)
			//                addDateCell(row, i++, monitoringBedrijf.getVerwijderd());
			//            else
			//                addCell(row, i++, "");

			//Gebruikersnaam
			addCell(row, i++, getNaamFromGebruikerTransfer(gebruikerAangemaakt));
			//            addCell(row, i++, getNaamFromGebruikerTransfer(gebruikerVerwijderd));

			//E-mail
			addCell(row, i++, gebruikerAangemaakt.getEmailAdres());

			//Functie van gebruiker
			addCell(row, i++, gebruikerAangemaakt.getFunctie());

			count = i;
		}

		for(int col = 0; col < count; col++) {
			System.out.println("auto " + col);
			sheet.autoSizeColumn(col);
		}

		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			try {
				workbook.write(bos);
			} finally {
				bos.close();
			}
			data = bos.toByteArray();
		} catch(IOException e) {
			throw new ServiceException("Cannot convert workbook to byte[]: " + e.getMessage());
		}

		return data;
	}

	private byte[] createNotificationCompaniesOverview(PageTransfer<MeldingOverviewTransfer> notificationBedrijven, Integer bedrijfId) throws ServiceException {
		byte[] data = null;
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet();

		cellStyleDate = workbook.createCellStyle();
		cellStyleDate.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("dd-mm-yyyy"));

		cellStyleCurrency = workbook.createCellStyle();
		DataFormat format = workbook.createDataFormat();
		cellStyleCurrency.setDataFormat(format.getFormat(CURRENCY_FORMAT));


		String headers[] = {"Bedrijf", "KvK-nummer", "Factuurnummer", "Referentienummer", "Bedrag", "Status", "Start datum", "Aangemaakt door", "E-mailadres", "Functie"};


		int j = 0;
		HSSFRow row = sheet.createRow(j++);

		int c = 0;
		for(String header : headers) {
			HSSFCell cell = row.createCell(c++);
			cell.setCellValue(header);
		}

		int count = 0;

		for(MeldingOverviewTransfer notificationBedrijf : notificationBedrijven.getContent()) {
			row = sheet.createRow(j++);

			//Document document = XmlUtil.parse(aanvraagEntity.getXml());
			//CachedXPathAPI xpathAPI = new CachedXPathAPI();

			GebruikerTransfer gebruikerAangemaakt = null;
			if(notificationBedrijf.getGebruikerId() != null)
				gebruikerAangemaakt = gebruikerService.findTransferByGebruikerId(notificationBedrijf.getGebruikerId(), bedrijfId);

			//            GebruikerTransfer gebruikerVerwijderd = null;
			//            if (monitoringBedrijf.getGebruikerVerwijderdId() != null)
			//            	gebruikerService.findTransferByGebruikerId(monitoringBedrijf.getGebruikerAangemaaktId());


			int i = 0;

			//Bedrijfsnaam
			addCell(row, i++, notificationBedrijf.getBedrijfsNaam());

			//KvK nummer
			addCell(row, i++, notificationBedrijf.getKvkNummer());

			//Bedrijf referentie
			//addCell(row, i++, notificationBedrijf.getSbdrNummer() != null ? notificationBedrijf.getSbdrNummer().toString() : "onbekend");

			//Vermelding referentie
			addCell(row, i++, notificationBedrijf.getReferentie());

			//Interne referentie
			addCell(row, i++, notificationBedrijf.getReferentieIntern());

			//Bedrag
			addCurrencyCell(row, i++, notificationBedrijf.getBedrag());

			//Status bedrijf
			addCell(row, i++, notificationBedrijf.getStatus());

			//Datum toegevoegd
			addDateCell(row, i++, notificationBedrijf.getToegevoegd());
			//            if (monitoringBedrijf.getVerwijderd() != null)
			//                addDateCell(row, i++, monitoringBedrijf.getVerwijderd());
			//            else
			//                addCell(row, i++, "");

			//Naam gebruiker
			addCell(row, i++, getNaamFromGebruikerTransfer(gebruikerAangemaakt));
			//            addCell(row, i++, getNaamFromGebruikerTransfer(gebruikerVerwijderd));

			//E-mail adres
			addCell(row, i++, gebruikerAangemaakt.getEmailAdres());

			//Functie gebruiker
			addCell(row, i++, gebruikerAangemaakt.getFunctie());

			count = i;
		}

		for(int col = 0; col < count; col++) {
			System.out.println("auto " + col);
			sheet.autoSizeColumn(col);
		}

		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			try {
				workbook.write(bos);
			} finally {
				bos.close();
			}
			data = bos.toByteArray();
		} catch(IOException e) {
			throw new ServiceException("Cannot convert workbook to byte[]: " + e.getMessage());
		}

		return data;
	}

	private byte[] createReportedCompaniesOverview(PageTransfer<ReportRequestedTransfer> reportedBedrijven) throws ServiceException {
		byte[] data = null;
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet();

		cellStyleDate = workbook.createCellStyle();
		cellStyleDate.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("dd-mm-yyyy"));

		cellStyleCurrency = workbook.createCellStyle();
		//cellStyleCurrency.setDataFormat((short) 7);
		DataFormat format = workbook.createDataFormat();
		cellStyleCurrency.setDataFormat(format.getFormat(CURRENCY_FORMAT));

		String headers[] = {"Bedrijf", "KvK-nummer", "Rapport referentienummer", "Opmaakdatum", "Opgevraagd door", "E-mailadres", "Functie"};


		int j = 0;
		HSSFRow row = sheet.createRow(j++);

		int c = 0;
		for(String header : headers) {
			HSSFCell cell = row.createCell(c++);
			cell.setCellValue(header);
		}

		int count = 0;

		for(ReportRequestedTransfer reportedBedrijf : reportedBedrijven.getContent()) {
			if(reportedBedrijf.getBedrijf() != null) {
				row = sheet.createRow(j++);

				GebruikerTransfer gebruikerAangemaakt = null;
				if(reportedBedrijf.getGebruiker() != null) gebruikerAangemaakt = reportedBedrijf.getGebruiker();

				int i = 0;

				//Bedrijfsnaam
				addCell(row, i++, reportedBedrijf.getBedrijf().getBedrijfsNaam());

				//KvK nummer
				addCell(row, i++, reportedBedrijf.getBedrijf().getKvkNummer());

				//Bedrijf referentie
				//addCell(row, i++, reportedBedrijf.getBedrijf().getSbdrNummer() != null ? reportedBedrijf.getBedrijf().getSbdrNummer().toString() : "onbekend");

				//Rapport referentie
				addCell(row, i++, reportedBedrijf.getReferentieNummer());

				//Aanmaakdatum
				addDateCell(row, i++, reportedBedrijf.getDatum());

				//Naam van opvrager
				addCell(row, i++, getNaamFromGebruikerTransfer(gebruikerAangemaakt));

				//E-mail van opvrager
				addCell(row, i++, gebruikerAangemaakt.getEmailAdres());

				//Functie van opvrager
				addCell(row, i++, gebruikerAangemaakt.getFunctie());

				count = i;
			}
		}

		for(int col = 0; col < count; col++) {
			System.out.println("auto " + col);
			sheet.autoSizeColumn(col);
		}

		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			try {
				workbook.write(bos);
			} finally {
				bos.close();
			}
			data = bos.toByteArray();
		} catch(IOException e) {
			throw new ServiceException("Cannot convert workbook to byte[]: " + e.getMessage());
		}

		return data;
	}

	private byte[] getFactuur(String filename, Date dateCreated) {
		try {
			Calendar cal = Calendar.getInstance();
			cal.setTime(dateCreated);
			File dir = DocumentUtil.getPathByDate(cal);

			File serverFile = new File(dir.getAbsolutePath() + File.separator + filename);

			byte[] data = new byte[(int) serverFile.length()];
			DataInputStream dis = new DataInputStream(new FileInputStream(serverFile));
			dis.readFully(data);
			dis.close();

			return data;
		} catch(Exception e) {
			throw new ServiceException("Cannot get document file: " + filename + " " + e.getMessage());
		}
	}

	private byte[] getFile(String filename, Date datumAangemaakt, boolean isAttachment) throws ServiceException {
		try {
			File dir;

			if(isAttachment) dir = DocumentUtil.getAttachmentRootPath();
			else {
				Calendar cal = Calendar.getInstance();
				cal.setTime(datumAangemaakt);
				dir = DocumentUtil.getPathByDate(cal);
			}

			File serverFile = new File(dir.getAbsolutePath() + File.separator + filename);

			byte[] data = new byte[(int) serverFile.length()];
			DataInputStream dis = new DataInputStream(new FileInputStream(serverFile));
			dis.readFully(data);
			dis.close();

			return data;
		} catch(Exception e) {
			throw new ServiceException("Cannot get document file: " + filename + " " + e.getMessage());
		}
	}

	private String getNaamFromGebruikerTransfer(GebruikerTransfer gebruiker) {
		String naam = "";

		if(gebruiker != null) {
			if(gebruiker.getVoornaam() != null) naam += gebruiker.getVoornaam();

			if(gebruiker.getNaam() != null) if(gebruiker.getVoornaam() != null) naam += " " + gebruiker.getNaam();
			else naam += gebruiker.getNaam();
		}

		return naam;
	}

}