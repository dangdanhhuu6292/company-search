package nl.devoorkant.sbdr.business.util.pdf;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import com.google.common.io.ByteStreams;
import com.lowagie.text.BadElementException;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

public class MeldingLetterContent {
    private static final Logger LOGGER = LoggerFactory.getLogger(MeldingLetterContent.class);

    private static PdfPTable createTable() {
        PdfPTable result = null; 

        try { 
            result = new PdfPTable(1);
            result.setWidths(new float[]{1});
            result.setWidthPercentage(100);
        } catch (DocumentException e1) { 
            LOGGER.error("Cannot set document table parameters", e1); 
        }

		return result;  
	} 
 
	public static PdfPTable createLetterContent(Resource signatureRes) { 		
        PdfPTable result = PrintUtil.createMasterTable();        
        
        String para1 = "Middels dit schrijven stellen wij u op de hoogte van één of meer betalingsachterstanden van uw onderneming die zijn aangemeld bij het CRZB.";
        String para1_1 = "Op de achterzijde van deze brief treft u de details van de vermelding(en) aan.";
        String para2 = "De achterstand(en) zal/zullen 14 dagen na dagtekening van deze brief opgenomen worden in het CRZB.";
        String para3 = "Het CRZB is het nationaal register waarin zakelijke betalingsachterstanden geregistreerd worden. Dit ter bevordering van een betrouwbaar ondernemingsklimaat en ter bestrijding van fraude en misbruik.";
        String para4 = "U kunt registratie in het CRZB voorkomen door over te gaan tot betaling ofwel door een betalingsregeling te treffen met uw crediteur(en). Hiervoor dient u contact met hen op te nemen.";
        String para5 = "Mocht u het niet eens zijn met de aangemelde achterstand(en) en komt u er niet uit met uw crediteur(en)? Dan bestaat er de mogelijkheid tot indienen van bezwaar bij Stichting Betalingsachterstandenregistratie.";
        String para5_5 = "Meer informatie hierover vindt u op www.crzb.nl/oneens-met-registratie";
        String para6 = "Hopend u hiermee voldoende te hebben geïnformeerd.";
        
		Image logo = null;
		
		 try {
			 
			//logo = Image.getInstance(String.format(realPath + IMAGE, "handtekening_voorthuizen"));
			 if (signatureRes.exists()) {
		            final InputStream inputStream = signatureRes.getInputStream();
		            
		            logo = Image.getInstance(ByteStreams.toByteArray(inputStream)); // utility to get byte array of contents
		            logo.scalePercent(40);
		     }								 
		} catch (BadElementException e) {
			LOGGER.error("BadElement", e);
		} catch (MalformedURLException e) {
			LOGGER.error("Wrong URL", e);
		} catch (IOException e) {
			LOGGER.error("IOException", e);
		}		
	    // we wrap the image in a PdfPCell
	    PdfPCell signaturecell = new PdfPCell();
	    signaturecell.setBorder(PdfPCell.NO_BORDER);
	    signaturecell.addElement(logo);

        
		// master cell
		PdfPCell cell = new PdfPCell();		
		cell.setPadding(0);
		cell.setBorder(PdfPCell.NO_BORDER);		
		 
		PdfPTable content = createTable();
 
		content.addCell(PrintUtil.createCell("Onderwerp: voorkom registratie in het Centraal Register Zakelijke Betalingsachterstanden (CRZB)", PrintUtil.calibri_9));
		
		// empty cell
		content.addCell(PrintUtil.createCell(" ", PrintUtil.calibri_9));
		
		// aanhef
		content.addCell(PrintUtil.createCell("Geachte heer/mevrouw,", PrintUtil.calibri_9));
		
		// empty cell
		content.addCell(PrintUtil.createCell(" ", PrintUtil.calibri_9));

		// para1
		content.addCell(PrintUtil.createCell(para1, PrintUtil.calibri_9));
		content.addCell(PrintUtil.createCell(para1_1, PrintUtil.calibri_9));

		// empty cell
		content.addCell(PrintUtil.createCell(" ", PrintUtil.calibri_9));
		
		// Melding content
		//if (melding != null && fromBedrijf != null) {
		//	content.addCell(PrintUtil.createCell("Datum van melding betalingsachterstand: " + FormatUtil.formatDate(melding.getDatumIngediend())));
		//	content.addCell(PrintUtil.createCell("Crediteur: " + fromBedrijf.getBedrijfsNaam()));
		//	if (melding.getBedrag() != null)
		//		content.addCell(PrintUtil.createCell("Waarde van de vordering: \u20AC " + FormatUtil.formatCurrency(melding.getBedrag().doubleValue())));
		//	content.addCell(PrintUtil.createCell("Factuurnummer / referentienummer: " + melding.getReferentieNummerIntern()));
		//}
		// empty cell
		//content.addCell(PrintUtil.createCell(" "));
		
		// para2
		content.addCell(PrintUtil.createCell(para2, PrintUtil.calibri_9));

		// empty cell
		content.addCell(PrintUtil.createCell(" ", PrintUtil.calibri_9));
		
		// para3
		content.addCell(PrintUtil.createCell(para3, PrintUtil.calibri_9));

		// empty cell
		content.addCell(PrintUtil.createCell(" ", PrintUtil.calibri_9));

		// para4
		content.addCell(PrintUtil.createCell(para4, PrintUtil.calibri_9));

		// empty cell
		content.addCell(PrintUtil.createCell(" ", PrintUtil.calibri_9));

		// para5
		content.addCell(PrintUtil.createCell(para5, PrintUtil.calibri_9));
		content.addCell(PrintUtil.createCell(para5_5, PrintUtil.calibri_9));		

		// empty cell
		content.addCell(PrintUtil.createCell(" ", PrintUtil.calibri_9));

		// para6
		content.addCell(PrintUtil.createCell(para6, PrintUtil.calibri_9));
		
		// empty cell
		content.addCell(PrintUtil.createCell(" ", PrintUtil.calibri_9));


		// ondertekening
		content.addCell(PrintUtil.createCell("Hoogachtend,", PrintUtil.calibri_9));
		// ondertekening
		content.addCell(PrintUtil.createCell("Centraal Register Zakelijke Betalingsachterstanden (CRZB)", PrintUtil.calibri_9));
		
		// empty cell
		content.addCell(PrintUtil.createCell(" ", PrintUtil.calibri_9));
		// signature image
		content.addCell(signaturecell);		
		// empty cell
		content.addCell(PrintUtil.createCell(" ", PrintUtil.calibri_9));

		// ondertekening
		content.addCell(PrintUtil.createCell("J.C.B. van Voorthuizen", PrintUtil.calibri_9));
		// ondertekening
		content.addCell(PrintUtil.createCell("Hoofd registerbeheer", PrintUtil.calibri_9));

		// For last row
		content.completeRow();

        cell.addElement(content);
        result.addCell(cell);
		
		return result;
	} 
}
