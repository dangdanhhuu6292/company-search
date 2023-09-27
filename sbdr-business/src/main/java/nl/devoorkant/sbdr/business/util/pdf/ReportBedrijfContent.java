package nl.devoorkant.sbdr.business.util.pdf;

import nl.devoorkant.sbdr.business.service.BedrijfService;
import nl.devoorkant.sbdr.business.service.DocumentServiceImpl;
import nl.devoorkant.sbdr.data.model.Bedrijf;

import java.awt.Color;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.Cell;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfTemplate;

public class ReportBedrijfContent {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportFromBedrijfContent.class);
    
    private static int COLOR_VERYGOOD = 0x2AE52A;
    private static int COLOR_GOOD = 0xA0E51C; // Color.WHITE.getRGB(); 
    private static int COLOR_WARNING = 0xE5A20E;
    private static int COLOR_DANGER = 0xE60700;    

    private static PdfPTable createTableBedrijf() {
        PdfPTable result = null;

        try {
            result = new PdfPTable(2);
            result.setWidths(new float[]{2, 1});
            result.setWidthPercentage(100);
            result.setKeepTogether(false);
        } catch (DocumentException e1) {
            LOGGER.error("Cannot set document table parameters", e1);
        }

		return result;
	}
    
    private static PdfPTable createTableRating() {
        PdfPTable result = null;

        try {
            result = new PdfPTable(4);
            result.setWidths(new float[]{1,1,1,1});
            result.setWidthPercentage(100);
            result.setKeepTogether(false);
            result.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
            result.getDefaultCell().setPaddingTop(5);
        } catch (DocumentException e1) {
            LOGGER.error("Cannot set document table parameters", e1);
        }

		return result;
	}

	public static PdfPTable createContent(Bedrijf bedrijf, boolean betalingsachterstand, int ratingScore, String ratingIndicatorScoreMessage) {
        PdfPTable result = PrintUtil.createMasterTable();
        
		// bedrijf master cell
		PdfPCell bedrijfcell = new PdfPCell();		
		bedrijfcell.setPadding(0);
		bedrijfcell.setPaddingTop(DocumentServiceImpl.PADDINGTOP_TOPIC_CONTENT); // PADDING START (with spacing for under status bar)
		bedrijfcell.setBorder(PdfPCell.NO_BORDER);
		
		PdfPTable contentBedrijf = createTableBedrijf();
				
		// Naam
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(2, bedrijf.getBedrijfsNaam()));

		// Adres
		String suffix = "";
		if (bedrijf.getHuisNrToevoeging() != null)
			suffix = " " + bedrijf.getHuisNrToevoeging();
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, bedrijf.getStraat() + " " + bedrijf.getHuisNr() + suffix));
		
		// DUMMY
		//if (ratingScore == -1)
		//	ratingScore = 50;
		
		if (!betalingsachterstand && ratingScore != -1) {
			// rating text
			String ratingText = "";
			if (ratingScore < 0 || ratingScore > 100) {
			    // out of range
				contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
			} else {
			   ratingText = BedrijfService.ratingScoreMessage.floorEntry(ratingScore).getValue()[BedrijfService.RATINGMESSAGE_INDICATOR];
			   contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, ratingText + ":"));
			}			
		} else if (betalingsachterstand && ratingScore > 0) {
			String ratingText = BedrijfService.ratingBetalingsachterstandMessage[BedrijfService.RATINGMESSAGE_INDICATOR];
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, ratingText + ":"));
		} else if (!betalingsachterstand && ratingScore <= 0 && ratingIndicatorScoreMessage != null){
			// cell with message
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, ratingIndicatorScoreMessage));				
		} else if (!betalingsachterstand && ratingScore <= 0 ){
			// empty cell
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));				
		} 

		// Postcode + plaats
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, bedrijf.getPostcode() + " " + bedrijf.getPlaats()));
				
		int span = 2;
		// BETALINGSACHTERSTAND false && betalingsachterstand
		if (ratingScore != -1) {			
			span = 1;
//			PdfPCell acell = PrintUtil.createCellWithColRowspanCenter(1, 3, "BETALINGSACHTERSTAND", PrintUtil.calibri_10_red);
//			acell.setCellEvent(new CellBackground());
//			acell.setBorder(PdfPCell.NO_BORDER);
//			acell.setPadding(3);
//			acell.setVerticalAlignment(Cell.ALIGN_MIDDLE);
//			acell.setPaddingBottom(3);
//			//contentBedrijf.addCell(acell);
			
			Color bluecolor = new Color(DocumentServiceImpl.COLOR_SBDR_BLUE);
			PdfPTable contentRatingScore = createTableRating();
			
			PdfPCell cell = PrintUtil.createCellWithColRowspanCenterColor(1, 2, " ", PrintUtil.calibri_6_bold, new Color(COLOR_DANGER));
			if (ratingScore <= 30) {
				cell.setBorder(PdfPCell.BOX);
				cell.setBorderWidth(2);
				//cell.setUseBorderPadding(true);
				cell.setBorderColor(bluecolor);
			} else
				cell.setBorder(PdfPCell.NO_BORDER);
			//cell.setBorderWidthTop(0f);
			contentRatingScore.addCell(cell);
			cell = PrintUtil.createCellWithColRowspanCenterColor(1, 2, " ", PrintUtil.calibri_6_bold, new Color(COLOR_WARNING));
			if (ratingScore > 30 && ratingScore <= 35) {
				cell.setBorder(PdfPCell.BOX);
				cell.setBorderWidth(2);
				//cell.setUseBorderPadding(true);
				cell.setBorderColor(bluecolor);
			} else
				cell.setBorder(PdfPCell.NO_BORDER);
			//cell.set
			//cell.setBorderWidthTop(0f);
			contentRatingScore.addCell(cell);
			cell = PrintUtil.createCellWithColRowspanCenterColor(1, 2, " ", PrintUtil.calibri_6_bold, new Color(COLOR_GOOD));
			if (ratingScore > 35 && ratingScore <= 80) {
				cell.setBorder(PdfPCell.BOX);
				cell.setBorderWidth(2);
				//cell.setUseBorderPadding(true);
				cell.setBorderColor(bluecolor);
			} else
				cell.setBorder(PdfPCell.NO_BORDER);
			//cell.setBorderWidthTop(0f);
			contentRatingScore.addCell(cell);
			cell = PrintUtil.createCellWithColRowspanCenterColor(1, 2, " ", PrintUtil.calibri_6_bold, new Color(COLOR_VERYGOOD));
			if (ratingScore > 80 && ratingScore <= 100) {
				cell.setBorder(PdfPCell.BOX);
				cell.setBorderWidth(2);
				//cell.setUseBorderPadding(true);
				cell.setBorderColor(bluecolor);
			} else
				cell.setBorder(PdfPCell.NO_BORDER);
			//cell.setBorderWidthTop(0f);
			contentRatingScore.addCell(cell);
			
			PdfPCell contentRatingCell = new PdfPCell(contentRatingScore);
			contentRatingCell.setBorder(PdfPCell.NO_BORDER);
			contentRatingCell.setPaddingTop(4.5f);
			
			contentBedrijf.addCell(contentRatingCell);
		} else {
			// empty cell
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
		}

		// SBDR Nummer
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(2, "BA-nummer " + bedrijf.getSbdrNummer()));		
				
		// KvKnummer
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(2, "KvK-nummer " + bedrijf.getKvKnummer()));		
		
//		// empty cell
//		contentBedrijf.addCell(PrintUtil.createCellWithColspan(2, " "));		
//
		// empty cell
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(2, " "));	
		
		// Meldingen titel!!
//		if (betalingsachterstand) 
//			contentBedrijf.addCell(PrintUtil.createCellWithColspan(2, "Meldingen"));	
//		else
//			contentBedrijf.addCell(PrintUtil.createCellWithColspan(2, "Er zijn op dit moment geen actieve betalingsachterstandsmeldingen voor dit bedrijf."));				

		// For last row
		contentBedrijf.completeRow();

        bedrijfcell.addElement(contentBedrijf);
        result.addCell(bedrijfcell);
		
		return result;
		
	}
}
