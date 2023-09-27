package nl.devoorkant.sbdr.business.util.pdf;

import java.awt.Color;
import java.math.BigDecimal;
import java.util.List;

import nl.devoorkant.sbdr.business.service.DocumentServiceImpl;
import nl.devoorkant.sbdr.business.util.ConvertUtil;
import nl.devoorkant.sbdr.business.util.FactuurRegelAggregate;
import nl.devoorkant.sbdr.data.model.Factuur;
import nl.devoorkant.sbdr.data.util.EProduct;
import nl.devoorkant.util.FormatUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

public class InvoiceContent {
    private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceContent.class);

    private static PdfPTable createTableInleiding() {
        PdfPTable result = null;

        try {
            result = new PdfPTable(1);
            result.setWidths(new float[]{1});
            result.setWidthPercentage(100);
            result.setKeepTogether(false);
        } catch (DocumentException e1) {
            LOGGER.error("Cannot set document table parameters", e1);
        }

		return result;
	}
    
    private static PdfPTable createTableFactuurRegel() {
        PdfPTable result = null;

        try {
            result = new PdfPTable(3);
            result.setWidths(new float[]{3, 1, 1});
            result.setWidthPercentage(100);
            result.setKeepTogether(false);
        } catch (DocumentException e1) {
            LOGGER.error("Cannot set document table parameters", e1);
        }

		return result;
	}

       
	public static PdfPTable createContent(Factuur factuur, List<FactuurRegelAggregate> factuurRegelAggregate, String extraTekst) {
		Color bluecolor = new Color(DocumentServiceImpl.COLOR_SBDR_DARKBLUE);

        PdfPTable result = PrintUtil.createMasterTable();
        result.setSpacingBefore(100f);
        
		// invoice master cell
		PdfPCell invoicecell = new PdfPCell();		
		invoicecell.setPadding(0);
		invoicecell.setBorder(PdfPCell.NO_BORDER);
		
		PdfPTable contentInleiding = createTableInleiding();
		
		// Inleiding
		String para1 = "Factuurdatum: " + FormatUtil.formatDate(factuur.getDatumFactuur());
		String para2 = "Rekeningnummer: " + factuur.getReferentie();
		contentInleiding.addCell(PrintUtil.createCell(para1));
		contentInleiding.addCell(PrintUtil.createCell(para2));
			
		// For last row
		contentInleiding.completeRow();
		
		PdfPTable contentFactuurRegel = createTableFactuurRegel();
		contentFactuurRegel.setSpacingBefore(50f);				
		// Product header
		PdfPCell cell = PrintUtil.createCellWithColRowspanLeftColor(1, 1, "Product", PrintUtil.calibri_8_bold_white, bluecolor);
		cell.setBorderColor(bluecolor);
		cell.setBorderWidthBottom(0f);
		cell.setPaddingTop(5f);
		cell.setPaddingBottom(5f);	
		cell.setPaddingLeft(5f);
		contentFactuurRegel.addCell(cell);

		// Aantal header
		cell = PrintUtil.createCellWithColRowspanCenterColor(1, 1, "Aantal", PrintUtil.calibri_8_bold_white, bluecolor);
		cell.setBorderColor(bluecolor);
		cell.setBorderWidthBottom(0f);
		cell.setPaddingTop(5f);
		cell.setPaddingBottom(5f);		
		contentFactuurRegel.addCell(cell);

		// Netto header
		cell = PrintUtil.createCellWithColRowspanCenterColor(1, 1, "Bedrag", PrintUtil.calibri_8_bold_white, bluecolor);
		cell.setBorderColor(bluecolor);
		cell.setBorderWidthBottom(0f);
		cell.setPaddingTop(5f);
		cell.setPaddingBottom(5f);		
		contentFactuurRegel.addCell(cell);
		
		BigDecimal totalBtw = null;
		BigDecimal total = null;
		if (factuurRegelAggregate != null) {
			int i = 0;
			for (FactuurRegelAggregate factuurregel : factuurRegelAggregate) {
				if (!factuurregel.getProductCode().equals(EProduct.DONATIE.getCode())) {
					if (totalBtw == null)
						totalBtw = factuurregel.getTotaalBedragNetto();
					else
						totalBtw = totalBtw.add(factuurregel.getTotaalBedragNetto());											
				} 
				if (total == null)
					total = factuurregel.getTotaalBedragNetto();
				else
					total = total.add(factuurregel.getTotaalBedragNetto());
				
				// product
				String btwtext = ". BTW (21%)";
				if (factuurregel.getProductCode().equals(EProduct.DONATIE.getCode()))
					btwtext = ". BTW (0%)";
				cell = PrintUtil.createCellWithColRowspanLeftColor(1, 1, factuurregel.getProductOmschrijving() + btwtext, PrintUtil.calibri_8_bold, Color.WHITE);
				cell.setBorderColor(bluecolor);
				cell.setBorderWidthTop(0f);
				cell.setPaddingTop(5f);
				cell.setPaddingBottom(5f);
				cell.setPaddingLeft(5f);
				if (i+1 != factuurRegelAggregate.size())
					cell.setBorderWidthBottom(0f);		
				contentFactuurRegel.addCell(cell);
				
				// Aantal
				String aantal = factuurregel.getAantal() + "";
				cell = PrintUtil.createCellWithColRowspanCenterColor(1, 1, aantal, PrintUtil.calibri_8_bold, Color.WHITE);
				cell.setBorderColor(bluecolor);
				cell.setBorderWidthTop(0f);
				cell.setPaddingTop(5f);
				cell.setPaddingBottom(5f);
				if (i+1 != factuurRegelAggregate.size())
					cell.setBorderWidthBottom(0f);		
				contentFactuurRegel.addCell(cell);

				// Bedrag
				String bedrag = "\u20AC " + PrintUtil.formatCurrency(factuurregel.getTotaalBedragNetto().doubleValue());
				cell = PrintUtil.createCellWithColRowspanRightColor(1, 1, bedrag, PrintUtil.calibri_8_bold, Color.WHITE);
				cell.setBorderColor(bluecolor);
				cell.setBorderWidthTop(0f);
				cell.setPaddingTop(5f);
				cell.setPaddingBottom(5f);
				if (i+1 != factuurRegelAggregate.size())
					cell.setBorderWidthBottom(0f);		
				cell.setPaddingRight(10f);			
				contentFactuurRegel.addCell(cell);
				
				i++;
			}
			
			// dummy
			String filler = "Subtotaal";
			cell = PrintUtil.createCellWithColRowspanRightColor(2, 1, filler, PrintUtil.calibri_8_bold, Color.WHITE);
			cell.setBorderColor(bluecolor);
			cell.setBorderWidthTop(0f);
			cell.setBorderWidthBottom(0f);	
			cell.setBorderWidthLeft(0f);	
			cell.setPaddingTop(5f);
			cell.setPaddingBottom(5f);
			cell.setPaddingRight(10f);			
			contentFactuurRegel.addCell(cell);

			// bedrag netto
			String bedrag = null;
			if (total != null)
				bedrag = "\u20AC " + PrintUtil.formatCurrency(total.doubleValue());
			else
				bedrag = "\u20AC 0,00";
			cell = PrintUtil.createCellWithColRowspanRightColor(1, 1, bedrag, PrintUtil.calibri_8_bold, Color.WHITE);
			cell.setBorderColor(bluecolor);
			cell.setBorderWidthTop(0f);
			cell.setPaddingTop(5f);
			cell.setPaddingBottom(5f);
			cell.setPaddingRight(10f);			
			contentFactuurRegel.addCell(cell);
			
			// dummy
			filler = "BTW (21%)";
			cell = PrintUtil.createCellWithColRowspanRightColor(2, 1, filler, PrintUtil.calibri_8_bold, Color.WHITE);
			cell.setBorderColor(bluecolor);
			cell.setBorderWidthTop(0f);
			cell.setBorderWidthBottom(0f);	
			cell.setBorderWidthLeft(0f);	
			cell.setPaddingTop(5f);
			cell.setPaddingBottom(5f);
			cell.setPaddingRight(10f);			
			contentFactuurRegel.addCell(cell);

			// bedrag VAT
			BigDecimal btw = ConvertUtil.getBtwFromNetto(totalBtw);
			if (btw != null)
				bedrag = "\u20AC " + PrintUtil.formatCurrency(btw.doubleValue());
			else
				bedrag = "\u20AC 0,00";
			cell = PrintUtil.createCellWithColRowspanRightColor(1, 1, bedrag, PrintUtil.calibri_8_bold, Color.WHITE);
			cell.setBorderColor(bluecolor);
			cell.setBorderWidthTop(0f);
			cell.setPaddingTop(5f);
			cell.setPaddingBottom(5f);
			cell.setPaddingRight(10f);			
			contentFactuurRegel.addCell(cell);
			
			// dummy
			filler = "Totaal";
			cell = PrintUtil.createCellWithColRowspanRightColor(2, 1, filler, PrintUtil.calibri_8_bold, Color.WHITE);
			cell.setBorderColor(bluecolor);
			cell.setBorderWidthTop(0f);
			cell.setBorderWidthBottom(0f);	
			cell.setBorderWidthLeft(0f);	
			cell.setPaddingTop(5f);
			cell.setPaddingBottom(5f);
			cell.setPaddingRight(10f);			
			contentFactuurRegel.addCell(cell);

			// bedrag bruto
			//BigDecimal bruto = ConvertUtil.getBrutoFromNetto(total);
			BigDecimal bruto = total;
			if (btw != null)
				bruto = bruto.add(btw);
			if (bruto != null)
				bedrag = "\u20AC " + PrintUtil.formatCurrency(bruto.doubleValue());
			else
				bedrag = "\u20AC 0,00";
			cell = PrintUtil.createCellWithColRowspanRightColor(1, 1, bedrag, PrintUtil.calibri_8_bold, Color.WHITE);
			cell.setBorderColor(bluecolor);
			cell.setBorderWidthTop(0f);
			cell.setPaddingTop(5f);
			cell.setPaddingBottom(5f);
			cell.setPaddingRight(10f);			
			contentFactuurRegel.addCell(cell);				
		}
		
		// For last row
		contentFactuurRegel.completeRow();				

        //invoicecell.addElement(contentInleiding);
        invoicecell.addElement(contentFactuurRegel);
                
        result.addCell(invoicecell);
        
        if (extraTekst != null) {
        	PdfPCell dummy = PrintUtil.createCellWithColRowspanCenterColor(1, 1, " ", PrintUtil.calibri_8_bold, Color.WHITE); 
        	PdfPCell extraTekstCell = PrintUtil.createCellWithColRowspanLeftColor(1, 1, extraTekst, PrintUtil.calibri_10_italic_sbdrdark, Color.WHITE); 
        	dummy.setBorder(PdfPCell.NO_BORDER);
        	extraTekstCell.setBorder(PdfPCell.NO_BORDER);
        	
	        result.addCell(dummy);
	        result.addCell(dummy);
	        result.addCell(dummy);
	        result.addCell(dummy);
	        result.addCell(dummy);
	        result.addCell(dummy);
	        result.addCell(dummy);
	        result.addCell(dummy);
	        result.addCell(dummy);
	        
	        result.addCell(extraTekstCell);
        }
        
		
		return result;
		
	}    
	
	
}
