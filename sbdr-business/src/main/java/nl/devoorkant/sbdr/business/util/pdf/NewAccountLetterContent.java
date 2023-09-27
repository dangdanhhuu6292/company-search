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

public class NewAccountLetterContent {
    private static final Logger LOGGER = LoggerFactory.getLogger(NewAccountLetterContent.class); 
    private static final String IMAGE = "/%s.jpg";

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

	public static PdfPTable createLetterContent(String aanhef, String activationCode, Resource signatureRes) {
        PdfPTable result = PrintUtil.createMasterTable();
        
		Image logo = null;
		
		 try {
			 
			//logo = Image.getInstance(String.format(realPath + IMAGE, "handtekening_voorthuizen"));
			 if (signatureRes.exists()) {
		            final InputStream inputStream = signatureRes.getInputStream();
		            
		            logo = Image.getInstance(ByteStreams.toByteArray(inputStream)); // utility to get byte array of contents
		            logo.scalePercent(30);
		     }			
		} catch (BadElementException e) {
			LOGGER.error("BadElement", e);
		} catch (MalformedURLException e) {
			LOGGER.error("Wrong URL", e);
		} catch (IOException e) {
			LOGGER.error("IOException", e);
		}		
		 
		//create page header with logo
	     //PdfPTable signature = new PdfPTable(1);
	     //signature.setTotalWidth(75);
	     
	     //signature.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
	     //signature.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);
	     
	     // we wrap the image in a PdfPCell
	     PdfPCell signaturecell = new PdfPCell();
	     signaturecell.setBorder(PdfPCell.NO_BORDER);
	     signaturecell.addElement(logo);
	     //signature.addCell(signaturecell);   
	     
	     // TODO: add signature on page via PDF Writer
        
	    String para1 = "Wij berichten u naar aanleiding van uw eerdere inschrijving op www.crzb.nl. Uw inschrijving is nog niet afgerond. Wij verzoeken u de laatste stap uit deze brief te doorlopen om uw inschrijving te verifiëren. Hierna is uw inschrijving definitief.";
        String para2 = "Op dit moment heeft u een voorlopig account. Uw voorlopige account heeft echter een maximale geldigheid van 30 dagen. Meldingen die tot op heden door u zijn gemaakt zijn nog niet door ons verwerkt. Deze zullen door ons in behandeling genomen worden na verificatie. Wij verzoeken u derhalve om uw voorlopige account om te zetten in een definitief account. Dit is mogelijk met behulp van de onderstaande verificatiecode.";
        String para3 = "Deze verificatiecode kunt u invullen bij het inloggen op www.crzb.nl onder het kopje 'mijn account'.";
        
        String para4 = "CRZB heeft betrouwbaarheid hoog in het vaandel staan. Het is hierbij noodzakelijk dat onze gebruikers zich verifiëren.";
        //String para5 = "Mocht u onverhoopt te laat zijn met het invullen van de verificatiecode, dan zal dit noodzakelijk leiden tot verwijdering van uw account en melding/en. Wij verzoeken u daarom tijdig te verifiëren.";
        //String para6 = "Heeft u nog vragen over uw registratie? Bezoek dan eens onze website. Hier staan veel antwoorden op veel gestelde vragen. Mocht u het antwoord hier niet vinden, neem dan contact op met onze Service afdeling op het volgende telefoonnummer: 085-4845700.";
        
		// master cell
		PdfPCell cell = new PdfPCell();		
		cell.setPadding(0);
		cell.setBorder(PdfPCell.NO_BORDER);
		
		PdfPTable content = createTable();

		// aanhef
		content.addCell(PrintUtil.createCell("Geachte " + aanhef + ",", PrintUtil.calibri_9));
		
		// empty cell
		content.addCell(PrintUtil.createCell(" ", PrintUtil.calibri_9));

		// para1
		content.addCell(PrintUtil.createCell(para1, PrintUtil.calibri_9));
		
		// empty cell
		content.addCell(PrintUtil.createCell(" ", PrintUtil.calibri_9));		
		
		// para2
		content.addCell(PrintUtil.createCell(para2, PrintUtil.calibri_9));
		
		// empty cell
		content.addCell(PrintUtil.createCell(" ", PrintUtil.calibri_9));		
		
		// activation code
		content.addCell(PrintUtil.createCell("Uw unieke verificatiecode: " + activationCode, PrintUtil.calibri_9_bold));	
		// para3
		content.addCell(PrintUtil.createCell(para3, PrintUtil.calibri_9));
		
		// empty cell
		content.addCell(PrintUtil.createCell(" ", PrintUtil.calibri_9));		
		
		content.addCell(PrintUtil.createCellWithColRowspan(1, 1, "Waarom dient u zich te verifiëren?", PrintUtil.calibri_9_bold));
		// para4
		content.addCell(PrintUtil.createCell(para4, PrintUtil.calibri_9));

		// empty cell
		content.addCell(PrintUtil.createCell(" ", PrintUtil.calibri_9));
		
		// groet
		content.addCell(PrintUtil.createCell("Met vriendelijke groet, ", PrintUtil.calibri_9));
		// ondertekening
		content.addCell(PrintUtil.createCell("Centraal Register Zakelijke Betalingsachterstanden (CRZB)", PrintUtil.calibri_9));

		// empty cell
		content.addCell(PrintUtil.createCell(" ", PrintUtil.calibri_9));
		
		// signature image
		//content.addCell(signaturecell);
		// empty cell
		content.addCell(PrintUtil.createCell(" ", PrintUtil.calibri_9));
		 
		// empty cell
		content.addCell(PrintUtil.createCell(" ", PrintUtil.calibri_9));

		// ondertekening
		content.addCell(PrintUtil.createCell("Mw. J.J. Willemsen", PrintUtil.calibri_9));
		// ondertekening
		content.addCell(PrintUtil.createCell("Hoofd relatiebeheer", PrintUtil.calibri_9));

		// empty cell
		content.addCell(PrintUtil.createCell(" ", PrintUtil.calibri_9));

		// automatisch
		content.addCell(PrintUtil.createCell("Deze brief is geautomatiseerd gegenereerd en derhalve niet ondertekend.", PrintUtil.calibri_9_italic));

		// For last row
		content.completeRow();

        cell.addElement(content);
        result.addCell(cell);
		
		return result;
	} 
}
