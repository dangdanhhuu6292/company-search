package nl.devoorkant.sbdr.ws;

import nl.devoorkant.sbdr.business.service.DocumentService;
import nl.devoorkant.sbdr.business.service.GebruikerService;
import nl.devoorkant.sbdr.business.service.SupportService;
import nl.devoorkant.sbdr.business.util.EBevoegdheid;
import nl.devoorkant.sbdr.data.model.Bedrijf;
import nl.devoorkant.sbdr.data.model.Document;
import nl.devoorkant.sbdr.data.model.Factuur;
import nl.devoorkant.sbdr.data.model.SupportBestand;
import nl.devoorkant.sbdr.data.service.BedrijfDataService;
import nl.devoorkant.sbdr.data.service.FactuurDataService;
import nl.devoorkant.sbdr.data.util.EDocumentType;
import nl.devoorkant.sbdr.idobfuscator.util.ObfuscatorUtils;
import nl.devoorkant.sbdr.ws.transfer.ErrorResource;
import nl.devoorkant.sbdr.ws.transfer.UserTransfer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;

@Component
@Path("/DocumentService/document")
public class DocumentResource {

	@Autowired
	private DocumentService documentService;
	
	@Autowired
	private FactuurDataService factuurService;

	@Autowired
	private BedrijfDataService bedrijfDataService;

	@Autowired
	private GebruikerService gebruikerService;

	@Autowired
	private SupportService supportService;

	@Autowired
	private UserResource userResource;

	private static Logger LOGGER = LoggerFactory.getLogger(DocumentResource.class);

	@GET
	@Path("downloadAttachment")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response downloadAttachment(@QueryParam("supportattachmentid") String obfSBId) {
		Integer sBId = ObfuscatorUtils.deofuscateInteger(obfSBId);
		ErrorResource err = null;
		SupportBestand sB;
		byte[] doc = null;
		String filename = null;

		UserTransfer user = userResource.getUser();

		try {
			if(sBId != null) {
				doc = documentService.getSupportAttachment(sBId);
				sB = supportService.findSupportTicketBestandBySupportBestandId(sBId);

				filename = sB.getOorspronkelijkBestandsNaam();
			}
		} catch(Exception e) {
			err = new ErrorResource(ErrorResource.CANNOT_FETCH_DOCUMENT);
		}

		if(err != null) return Response.serverError().build();
		else
			return Response.ok(new ByteArrayInputStream(doc)).header("x-filename", filename).header("content-type", MediaType.APPLICATION_OCTET_STREAM).build();
	}

	@GET
	@Path("downloadBatchDocument")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response downloadBatchDocument(@QueryParam("batchDocumentId") String obfBatchDocumentId) {
		Integer batchDocumentId = ObfuscatorUtils.deofuscateInteger(obfBatchDocumentId);
		ErrorResource error = null;
		UserTransfer user = userResource.getUser();
		byte[] doc = null;
		Document docObject = null;
		String filename = null;

		if(user == null) {error = new ErrorResource(ErrorResource.CANNOT_FETCH_DOCUMENT);} else {
			if(!gebruikerService.hasRightToDo(user.getUserId(), null, EBevoegdheid.SBDR_MEDEWERKER)) {
				error = new ErrorResource(ErrorResource.ACTION_NOT_ALLOWED);
			} else {
				if(batchDocumentId != null) {
					try {
						doc = documentService.getBatchDocument(batchDocumentId);
						docObject = documentService.getDocument(batchDocumentId);
					} catch(Exception e) {
						error = new ErrorResource(ErrorResource.CANNOT_FETCH_DOCUMENT);
					}
				} else {
					error = new ErrorResource(ErrorResource.PARAMETER_IS_EMPTY);
				}
			}
		}

		if(error != null) return Response.serverError().build();
		else
			return Response.ok(new ByteArrayInputStream(doc)).header("x-filename", docObject.getFileNaam()).header("content-type", MediaType.APPLICATION_OCTET_STREAM).build();
	}

	@GET
	@Path("downloadFile")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/pdf")
	public Response getDocument(@QueryParam("method") String method, @QueryParam("bedrijfid") String obfBedrijfId, @QueryParam("meldingid") String obfMeldingId, @QueryParam("referentie") String referentie, @QueryParam("monitorid") String obfMonitorId) {		
		Integer bedrijfId = null;
		Integer meldingId = null;
		Integer monitorId = null;
		try {
			bedrijfId = ObfuscatorUtils.deofuscateInteger(obfBedrijfId);
		} catch (Exception e) 
		{
			// do nothing
		}
		try {
			meldingId = ObfuscatorUtils.deofuscateInteger(obfMeldingId);
		} catch (Exception e) 
		{
			// do nothing
		}
		try {
			monitorId = ObfuscatorUtils.deofuscateInteger(obfMonitorId);
		} catch (Exception e) 
		{
			// do nothing
		}			
		
		// method = 'report', 'letter_customer', 'letter_notification'
		// meldingid is set when 'letter_notification'
		// bedrijfid + requestedfrombedrijfid is set when 'report'
		// referentie is set when 'report'
		ErrorResource errorResource = null;
		byte[] pdfDocument = null;
		String filename = "document.pdf";

		UserTransfer user = userResource.getUser();

		if(user != null) {
			if(method == null || (method.equals("report") && !gebruikerService.hasRightToDo(user.getUserId(), user.getBedrijfId(), EBevoegdheid.RAPPORT_INZIEN))) // klant, hoofd, gebruiker
			{
				errorResource = new ErrorResource(ErrorResource.CANNOT_FETCH_DOCUMENT);
			}
		}

		try {
			if(method != null) {
				Bedrijf bedrijf = null;
			
				if ((method.equals("report") || method.equals("letter_customer")) && bedrijfId != null) 
					bedrijf = bedrijfDataService.findByBedrijfId(bedrijfId);
					
				if(method.equals("report") && referentie != null) {
					filename = "rapport.pdf";

					if (bedrijf != null)
					{
						filename = "Betalingsachterstandenrapport_" + bedrijf.getKvKnummer() + ".pdf";
					}
					pdfDocument = documentService.getDocumentContent(EDocumentType.RAPPORT, referentie); //documentService.createReportPdf(bedrijfId, requestedFromBedrijfId, realPath);
				} else if(method.equals("letter_customer") && bedrijfId != null) {
					pdfDocument = documentService.getNewAccountLetterContent(bedrijfId); //documentService.createNewAccountLetterPdf(klantId, realPath);
					
					if (bedrijf != null)
					{
						filename = "nieuweklantbrief" + bedrijf.getKvKnummer() + bedrijf.getSubDossier() + ".pdf";
					}
					
				} else if(method.equals("letter_notification") && meldingId != null) {
					pdfDocument = documentService.getNotificationLetterContent(meldingId);
					filename = "meldingbrief" + ObfuscatorUtils.obfuscateInteger(meldingId) + ".pdf";
				} else if(method.equals("monitorDetails") && monitorId != null) {
					pdfDocument = documentService.createMonitorDetailPdf(user.getUserId(), bedrijfId, monitorId);
					filename = "monitor" + ObfuscatorUtils.obfuscateInteger(monitorId) + "details.pdf";
				} else errorResource = new ErrorResource(ErrorResource.GENERAL_FAILURE);
			}

		} catch(Exception e) {
			errorResource = new ErrorResource(ErrorResource.CANNOT_FETCH_DOCUMENT);
		}

		if(errorResource != null) return Response.serverError().build();
		else
			return Response.ok(new ByteArrayInputStream(pdfDocument)).header("x-filename", filename).header("content-type", "application/pdf").build();

	}

	@GET
	@Path("downloadExcel")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/vnd.ms-excel")
	public Response getExcel(@QueryParam("method") String method, @QueryParam("bedrijfid") String obfBedrijfId) {
		Integer bedrijfId = ObfuscatorUtils.deofuscateInteger(obfBedrijfId);
		// method = 'removed_companies'
		// meldingid is set when 'letter_notification'
		// bedrijfid + requestedfrombedrijfid is set when 'report'
		ErrorResource errorResource = null;
		byte[] xlsDocument = null;
		String filename = "document.xls";

		UserTransfer user = userResource.getUser();

		try {

			if(method != null && user != null) {
				if(method.equals("removed_companies") && bedrijfId != null) {
					xlsDocument = documentService.getRemovedCompaniesOverview(bedrijfId);
					filename = "verwijderdeBedrijvenOverzicht" + ".xls";
				} else if(method.equals("monitored_companies") && bedrijfId != null) {
					xlsDocument = documentService.getMonitoredCompaniesOverview(bedrijfId);
					filename = "monitoringBedrijvenOverzicht" + ".xls";
				} else if(method.equals("notified_companies") && bedrijfId != null) {
					xlsDocument = documentService.getNotifiedCompaniesOverview(bedrijfId);
					filename = "vermeldingBedrijvenOverzicht" + ".xls";
				} else if(method.equals("reported_companies") && bedrijfId != null) {
					xlsDocument = documentService.getReportedCompaniesOverview(user.getUserId(), bedrijfId);
					filename = "reportBedrijvenOverzicht" + ".xls";
				} else errorResource = new ErrorResource(ErrorResource.GENERAL_FAILURE);
			}

		} catch(Exception e) {
			errorResource = new ErrorResource(ErrorResource.CANNOT_FETCH_DOCUMENT);
		}

		if(errorResource != null) return Response.serverError().build();
		else
			return Response.ok(new ByteArrayInputStream(xlsDocument)).header("x-filename", filename).header("Content-Disposition", "attachment; filename=" + filename).build();

	}

	@GET
	@Path("downloadFactuur")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/pdf")
	public Response getFactuur(@QueryParam("factuurid") String obfFId) {
		Integer fId = ObfuscatorUtils.deofuscateInteger(obfFId);
		ErrorResource err = null;
		Factuur f;
		byte[] doc = null;
		String filename = null;

		try {
			if(fId != null) {
				f = factuurService.findByFactuurId(fId);
				doc = documentService.getFactuur(fId);
				filename = "Factuur_CRZB_" + f.getReferentie() + ".pdf";
			}
		} catch(Exception e) {
			err = new ErrorResource(ErrorResource.CANNOT_FETCH_DOCUMENT);
		}

		if(err != null) return Response.serverError().build();
		else
			return Response.ok(new ByteArrayInputStream(doc)).header("x-filename", filename).header("content-type", MediaType.APPLICATION_OCTET_STREAM).build();
	}

}

