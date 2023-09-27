package nl.devoorkant.sbdr.business.util.pdf;

import java.awt.Color;
import java.util.List;

import nl.devoorkant.sbdr.business.service.DocumentServiceImpl;
import nl.devoorkant.sbdr.data.model.Bedrijf;
import nl.devoorkant.sbdr.data.model.Melding;
import nl.devoorkant.sbdr.data.util.EReferentieInternType;
import nl.devoorkant.util.FormatUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

public class MeldingLetterMeldingenContent {
    private static final Logger LOGGER = LoggerFactory.getLogger(MeldingLetterMeldingenContent.class);
   
    private static PdfPTable createTableMeldingRegel() {
        PdfPTable result = null;

        try {
            result = new PdfPTable(5);
            result.setWidths(new float[]{2, 1, 1, 1, 1});
            result.setWidthPercentage(100);
            result.setKeepTogether(false);
        } catch (DocumentException e1) {
            LOGGER.error("Cannot set document table parameters", e1);
        }

		return result;
	}

       
	public static PdfPTable createContent(Bedrijf overBedrijf, List<Melding> meldingen, int margin_right_extra) {
		Color bluecolor = new Color(DocumentServiceImpl.COLOR_SBDR_BLUE);

        PdfPTable result = PrintUtil.createMasterTable();
        
		// meldingen master cell
		PdfPCell meldingencell = new PdfPCell();		
		meldingencell.setPadding(0);
		if (margin_right_extra > 0) {
			meldingencell.setPaddingRight(margin_right_extra);
			meldingencell.setPaddingLeft(-30);
		}
		meldingencell.setBorder(PdfPCell.NO_BORDER);
		
		PdfPTable contentMeldingRegel = createTableMeldingRegel();
		contentMeldingRegel.setSpacingBefore(50f);				
		// Bedrijfsnaam header
		PdfPCell cell = PrintUtil.createCellWithColRowspanLeftColor(1, 1, "Bedrijfsnaam", PrintUtil.calibri_8_bold_white, bluecolor);
		cell.setBorderColor(bluecolor);
		cell.setBorderWidthBottom(0f);
		cell.setPaddingTop(5f);
		cell.setPaddingBottom(5f);	
		cell.setPaddingLeft(5f);
		contentMeldingRegel.addCell(cell);

		// KvKnummer header
		cell = PrintUtil.createCellWithColRowspanLeftColor(1, 1, "KvK-nummer", PrintUtil.calibri_8_bold_white, bluecolor);
		cell.setBorderColor(bluecolor);
		cell.setBorderWidthBottom(0f);
		cell.setPaddingTop(5f);
		cell.setPaddingBottom(5f);		
		cell.setPaddingLeft(5f);
		contentMeldingRegel.addCell(cell);
		
		// Factuurnummer header
		cell = PrintUtil.createCellWithColRowspanLeftColor(1, 1, "Factuurnummer (*)", PrintUtil.calibri_8_bold_white, bluecolor);
		cell.setBorderColor(bluecolor);
		cell.setBorderWidthBottom(0f);
		cell.setPaddingTop(5f);
		cell.setPaddingBottom(5f);		
		cell.setPaddingLeft(5f);
		contentMeldingRegel.addCell(cell);

		// Openstaand bedrag header
		cell = PrintUtil.createCellWithColRowspanLeftColor(1, 1, "Openstaand bedrag", PrintUtil.calibri_8_bold_white, bluecolor);
		cell.setBorderColor(bluecolor);
		cell.setBorderWidthBottom(0f);
		cell.setPaddingTop(5f);
		cell.setPaddingBottom(5f);		
		cell.setPaddingLeft(5f);
		contentMeldingRegel.addCell(cell);

		// Referentienummer VE header
		cell = PrintUtil.createCellWithColRowspanLeftColor(1, 1, "Onze referentie", PrintUtil.calibri_8_bold_white, bluecolor);
		cell.setBorderColor(bluecolor);
		cell.setBorderWidthBottom(0f);
		cell.setPaddingTop(5f);
		cell.setPaddingBottom(5f);		
		cell.setPaddingLeft(5f);
		contentMeldingRegel.addCell(cell);
		
		if (meldingen != null) {
			int i=0;
			for (Melding melding : meldingen) {
				// bedrijfsnaam
				cell = PrintUtil.createCellWithColRowspanLeftColor(1, 1, overBedrijf.getBedrijfsNaam(), PrintUtil.calibri_8_bold, Color.WHITE);
				cell.setBorderColor(bluecolor);
				cell.setBorderWidthTop(0f);
				cell.setPaddingTop(5f);
				cell.setPaddingBottom(5f);
				cell.setPaddingLeft(5f);
				if (i+1 != meldingen.size())
					cell.setBorderWidthBottom(0f);		
				contentMeldingRegel.addCell(cell);

				// KvK nummer
				cell = PrintUtil.createCellWithColRowspanLeftColor(1, 1, overBedrijf.getKvKnummer(), PrintUtil.calibri_8_bold, Color.WHITE);
				cell.setBorderColor(bluecolor);
				cell.setBorderWidthTop(0f);
				cell.setPaddingTop(5f);
				cell.setPaddingBottom(5f);
				cell.setPaddingLeft(5f);
				if (i+1 != meldingen.size())
					cell.setBorderWidthBottom(0f);		
				contentMeldingRegel.addCell(cell);
				
				// Factuurnummer
				cell = PrintUtil.createCellWithColRowspanLeftColor(1, 1, melding.getReferentieNummer(), PrintUtil.calibri_8_bold, Color.WHITE);
				cell.setBorderColor(bluecolor);
				cell.setBorderWidthTop(0f);
				cell.setPaddingTop(5f);
				cell.setPaddingBottom(5f);
				cell.setPaddingLeft(5f);
				if (i+1 != meldingen.size())
					cell.setBorderWidthBottom(0f);		
				contentMeldingRegel.addCell(cell);

				// Openstaandbedrag
				String bedrag = "\u20AC " + PrintUtil.formatCurrency(melding.getBedrag().doubleValue());
				cell = PrintUtil.createCellWithColRowspanRightColor(1, 1, bedrag, PrintUtil.calibri_8_bold, Color.WHITE);
				cell.setBorderColor(bluecolor);
				cell.setBorderWidthTop(0f);
				cell.setPaddingTop(5f);
				cell.setPaddingBottom(5f);
				cell.setPaddingLeft(5f);
				if (i+1 != meldingen.size())
					cell.setBorderWidthBottom(0f);		
				cell.setPaddingRight(10f);	
				contentMeldingRegel.addCell(cell);
				
				// Referentienummer
				cell = PrintUtil.createCellWithColRowspanLeftColor(1, 1, EReferentieInternType.VERMELDING.getPrefix() + melding.getReferentieNummerIntern(), PrintUtil.calibri_8_bold, Color.WHITE);
				cell.setBorderColor(bluecolor);
				cell.setBorderWidthTop(0f);
				cell.setPaddingTop(5f);
				cell.setPaddingBottom(5f);
				cell.setPaddingLeft(5f);
				if (i+1 != meldingen.size())
					cell.setBorderWidthBottom(0f);		
				contentMeldingRegel.addCell(cell);				
				i++;
			}			
		}
		
		// For last row
		contentMeldingRegel.completeRow();				

        meldingencell.addElement(contentMeldingRegel);
                
        result.addCell(meldingencell);
        
        PdfPCell dummy = PrintUtil.createCellWithColRowspanCenterColor(1, 1, " ", PrintUtil.calibri_8_bold, Color.WHITE); 
        PdfPCell extraTekstCell = PrintUtil.createCellWithColRowspanLeftColor(1, 1, "(*) Dit nummer kan ook een referentie- of dossiernummer zijn.", PrintUtil.calibri_9_italic_sbdrdark, Color.WHITE); 
        dummy.setBorder(PdfPCell.NO_BORDER);
        extraTekstCell.setBorder(PdfPCell.NO_BORDER);
        	
	    result.addCell(dummy);
	    result.addCell(dummy);
	    result.addCell(dummy);
	       
	    result.addCell(extraTekstCell);
	
		return result;
		
	}    
	
	
}
