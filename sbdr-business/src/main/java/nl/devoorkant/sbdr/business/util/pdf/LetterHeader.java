package nl.devoorkant.sbdr.business.util.pdf;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import nl.devoorkant.sbdr.business.service.DocumentServiceImpl;
import nl.devoorkant.sbdr.data.model.Bedrijf;
import nl.devoorkant.sbdr.data.model.CIKvKDossier;
import nl.devoorkant.sbdr.data.util.EDocumentType;
import nl.devoorkant.util.FormatUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

public class LetterHeader {
    private static final Logger LOGGER = LoggerFactory.getLogger(LetterHeader.class);
	
	public static PdfPTable createTable() {
        PdfPTable result = new PdfPTable(1);

        try {
			result.setWidths(new float[]{1});
        	result.setWidthPercentage(100);
		} catch (DocumentException e1) {
			LOGGER.error("Cannot set document table parameters", e1);
		}
		
		return result;
	}
	
	private static String formatDateFullMonth(Date datum) {
		Format formatter = new SimpleDateFormat("d MMMM yyyy", new Locale("nl"));
	    String datumStr = formatter.format(datum);
	    return datumStr;
	}
	
	public static PdfPTable createHeader(String tavNaam, Bedrijf bedrijf, CIKvKDossier dossier, EDocumentType doctype, boolean includeAdresSbdr) {
        PdfPTable result = PrintUtil.createMasterTable();
		
        PdfPTable content = createTable();
        
		// master cell
		PdfPCell cell = new PdfPCell();		
		cell.setPadding(0);
		if (doctype.equals(EDocumentType.FACTUUR))
			cell.setPaddingTop(DocumentServiceImpl.PADDINGTOP_TOPIC_CONTENT); // PADDING START (with spacing for heading title)
		cell.setBorder(PdfPCell.NO_BORDER);        
        
        // Bedrijfsnaam
        if (bedrijf != null) content.addCell(PrintUtil.createCell(bedrijf.getBedrijfsNaam()));

        // tav naam
        if (tavNaam != null) content.addCell(PrintUtil.createCell(tavNaam));        
        
     	boolean postbusAdres = false;
     	// NO POSTBUS!!! This data can not be trusted
        // adres, if Straat_CA == 'Postbus' then postbus in StraatHuisNummer_CA field
        //if (dossier != null && dossier.getStraatHuisnummerCa() != null && dossier.getStraatCa() != null && dossier.getStraatCa().equalsIgnoreCase("Postbus")) {
        //	postbusAdres = true;
        //	content.addCell(PrintUtil.createCell("Postbus " + dossier.getStraatHuisnummerCa()));
        //}
        // if not postbus, then get adres (street + huisnr + suffix) from dossier
        //else 
        	if (dossier != null && dossier.getStraatHuisnummer() != null){
        	content.addCell(PrintUtil.createCell(dossier.getStraatHuisnummer() ));
        } 
        // if no dossier get adres from bedrijf. This may not happen...
        else if (bedrijf != null) {
        	LOGGER.warn("Bedrijf has no CIKvkDossier record");
        	String adres = bedrijf.getStraat() + " " + bedrijf.getHuisNr();
        	if (bedrijf.getHuisNrToevoeging() != null)
        		adres += " " + bedrijf.getHuisNrToevoeging();
        	
        	content.addCell(PrintUtil.createCell(adres ));        	
        }
        
        // postcode + plaats
        if (!postbusAdres && bedrijf != null)
        	content.addCell(PrintUtil.createCell(bedrijf.getPostcode() + " " + bedrijf.getPlaats() ));
        else if (postbusAdres && dossier != null) {
        	content.addCell(PrintUtil.createCell(dossier.getPostcodeCa() + " " + dossier.getPlaatsCa()));
        }
        
        String datumPrefix = "";
        // ADRES SBDR
        if (includeAdresSbdr) {
    		// empty cell
    		content.addCell(PrintUtil.createCell(" "));
    		// empty cell
    		//content.addCell(PrintUtil.createCell(" "));
        	content.addCell(PrintUtil.createCell("Centraal Register Zakelijke Betalingsachterstanden (CRZB)" ));
        	content.addCell(PrintUtil.createCell("Jadelaan 20" ));
        	content.addCell(PrintUtil.createCell("2132XW  HOOFDDORP" ));
        }
    	datumPrefix = "Hoofddorp, ";

		// empty cell
		//content.addCell(PrintUtil.createCell(" "));
		// empty cell
		content.addCell(PrintUtil.createCell(" "));
        
		if (!doctype.equals(EDocumentType.FACTUUR)) {
	        // datum right aligment
	        content.addCell(PrintUtil.createCell(datumPrefix + formatDateFullMonth(new Date()), Element.ALIGN_RIGHT));
		}
        
		// empty cell
		//content.addCell(PrintUtil.createCell(" "));
		// empty cell
		//content.addCell(PrintUtil.createCell(" "));
		
        // For last row
        content.completeRow();
        
        cell.addElement(content);
        result.addCell(cell);        
        
        return result;
	}
}
