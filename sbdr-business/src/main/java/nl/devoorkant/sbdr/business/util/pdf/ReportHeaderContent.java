package nl.devoorkant.sbdr.business.util.pdf;

import java.awt.Color;
import java.util.Date;

import nl.devoorkant.sbdr.business.service.DocumentServiceImpl;
import nl.devoorkant.util.FormatUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

public class ReportHeaderContent {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportHeaderContent.class);

    private static PdfPTable createTable() {
        PdfPTable result = null;

        try {
            result = new PdfPTable(3);
            result.setWidths(new float[]{1, 1, 1});
            result.setWidthPercentage(100);
        } catch (DocumentException e1) {
            LOGGER.error("Cannot set document table parameters", e1);
        }

		return result;
	}

	public static PdfPTable createContent(String titel, String bedrijfsnaam, String referentieNummer) {
		Color bgColor = new Color(DocumentServiceImpl.COLOR_SBDR_DARKBLUE);
		
        PdfPTable result = PrintUtil.createMasterTable();
               
		// master cell
		PdfPCell cell = new PdfPCell();		
		cell.setPadding(0);
		cell.setBorder(PdfPCell.NO_BORDER);
		
		PdfPTable content = createTable();
		
		// Titel
		String bedrijfsnaamshort = bedrijfsnaam.substring(0, Math.min(bedrijfsnaam.length(), 25));
		if (bedrijfsnaam.length() > 25)
			bedrijfsnaamshort += ".";
		
		PdfPCell cellheader = PrintUtil.createCellWithColspan(3,titel + ": " + bedrijfsnaamshort.toUpperCase(), PrintUtil.calibri_12_bold_white, false);
		cellheader.setBackgroundColor(bgColor);
		content.addCell(cellheader);
		
		// empty row
		//content.addCell(PrintUtil.createCellWithColspan(3," "));
		
		// Datum + tijd
		Date datum = new Date();
		PdfPCell cellDatum = PrintUtil.createCellWithColspan(2, "Datum: " + FormatUtil.formatDate(datum, "dd-MM-yyyy") + "\tTijd " + FormatUtil.formatDate(datum, "HH:mm"), PrintUtil.calibri_8_white, false);
		cellDatum.setVerticalAlignment(Element.ALIGN_CENTER);
		cellDatum.setBorder(PdfPCell.NO_BORDER); //Rectangle.BOTTOM);
		content.addCell(cellDatum);
				
		// Referentie
		PdfPCell cellRef = PrintUtil.createCellWithColspan(1,"Onze referentie " + referentieNummer, PrintUtil.calibri_8_white, false);		
		cellRef.setVerticalAlignment(Element.ALIGN_TOP);
		cellRef.setBorder(PdfPCell.NO_BORDER); //Rectangle.BOTTOM);
		content.addCell(cellRef);		
		
		// empty row
		//content.addCell(PrintUtil.createCellWithColspan(3, " "));
		
		// For last row
		content.completeRow();

        cell.addElement(content);
        result.addCell(cell);
		
		return result;
		
	}
}
