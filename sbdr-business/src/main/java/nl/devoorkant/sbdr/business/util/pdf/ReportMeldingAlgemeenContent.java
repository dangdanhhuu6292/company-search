package nl.devoorkant.sbdr.business.util.pdf;

import java.util.ArrayList;
import java.util.List;

import nl.devoorkant.sbdr.business.service.DocumentServiceImpl;
import nl.devoorkant.sbdr.business.transfer.BedrijfReportTransfer;
import nl.devoorkant.sbdr.business.transfer.MeldingOverviewTransfer;
import nl.devoorkant.util.FormatUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

public class ReportMeldingAlgemeenContent {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportMeldingAlgemeenContent.class);

    private static PdfPTable createTableMelding() {
        PdfPTable result = null;

        try {
            result = new PdfPTable(3);
            result.setWidths(new float[]{5, 4, 1});
            result.setWidthPercentage(100);
            result.setKeepTogether(false);
        } catch (DocumentException e1) {
            LOGGER.error("Cannot set document table parameters", e1);
        }
        
        return result;
	}
    
	public static PdfPTable createContent(BedrijfReportTransfer report) {
        PdfPTable result = PrintUtil.createMasterTable();
        
		// melding master cell
		PdfPCell meldingcell = new PdfPCell();		
		meldingcell.setPadding(0);
		meldingcell.setPaddingTop(DocumentServiceImpl.PADDINGTOP_TOPIC_CONTENT); // PADDING START (with spacing for heading title)
		meldingcell.setPaddingTop(DocumentServiceImpl.PADDINGTOP_TOPIC_CONTENT); // PADDING START (with spacing for heading title)
		meldingcell.setBorder(PdfPCell.NO_BORDER);
		
		PdfPTable contentMelding = createTableMelding();
				
		// Aantal openstaand
		//contentMelding.addCell(PrintUtil.createCellWithColspan(1, "Aantal openstaande vermeldingen"));
		contentMelding.addCell(PrintUtil.createCellWithColspan(1, "Geregistreerde betalingsachterstanden"));
		if (report.getAantalMeldingenActief() > 0) {
			if (report.getAantalMeldingenActief() == 1)
				contentMelding.addCell(PrintUtil.createCellWithColspan(1, report.getAantalMeldingenActief() + " vermelding"));
			else
				contentMelding.addCell(PrintUtil.createCellWithColspan(1, report.getAantalMeldingenActief() + " vermeldingen"));
		} else
			contentMelding.addCell(PrintUtil.createCellWithColspan(1, "geen vermeldingen"));
		contentMelding.addCell(PrintUtil.createCellWithColspan(1, " "));

		// Bedrag openstaand
		//contentMelding.addCell(PrintUtil.createCellWithColspan(1, "Bedrag openstaande vermeldingen"));
		contentMelding.addCell(PrintUtil.createCellWithColspan(1, "Totaal bedrag"));
		if (report.getBedragOpenstaand() != null && report.getBedragOpenstaand().doubleValue() > 0.0) 
			contentMelding.addCell(PrintUtil.createCellWithColspan(1, "\u20AC " + PrintUtil.formatCurrency(report.getBedragOpenstaand().doubleValue())));
		else
			contentMelding.addCell(PrintUtil.createCellWithColspan(1, "-"));
		contentMelding.addCell(PrintUtil.createCellWithColspan(1, " "));
		
		// Aantal crediteuren
		//contentMelding.addCell(PrintUtil.createCellWithColspan(1, "Aantal crediteuren openstaande vermeldingen"));
		contentMelding.addCell(PrintUtil.createCellWithColspan(1, "Vermeldingen gemaakt door"));
		int aantalCrediteuren = report.getAantalCrediteuren();
		if (aantalCrediteuren > 0) {
			if (aantalCrediteuren == 1)
				contentMelding.addCell(PrintUtil.createCellWithColspan(1, aantalCrediteuren + " crediteur"));
			else
				contentMelding.addCell(PrintUtil.createCellWithColspan(1, aantalCrediteuren + " crediteuren"));
		} else
			contentMelding.addCell(PrintUtil.createCellWithColspan(1, "geen crediteuren"));
		contentMelding.addCell(PrintUtil.createCellWithColspan(1, " "));		

//		// Aantal gesloten
//		contentMelding.addCell(PrintUtil.createCellWithColspan(1, "Aantal afgesloten vermeldingen"));
//		contentMelding.addCell(PrintUtil.createCellWithColspan(1, report.getAantalMeldingenResolved() + ""));
//		contentMelding.addCell(PrintUtil.createCellWithColspan(1, " "));
//		
//		// Bedrag gesloten
//		contentMelding.addCell(PrintUtil.createCellWithColspan(1, "Bedrag afgesloten vermeldingen"));
//		if (report.getBedragResolved() != null && report.getBedragResolved() > 0.0) 
//			contentMelding.addCell(PrintUtil.createCellWithColspan(1, "\u20AC " + FormatUtil.formatCurrency(report.getBedragResolved(), 2)));
//		else
//			contentMelding.addCell(PrintUtil.createCellWithColspan(1, "-"));
//			
//		contentMelding.addCell(PrintUtil.createCellWithColspan(1, " "));

		// empty
		contentMelding.addCell(PrintUtil.createCellWithColspan(1, " "));
		contentMelding.addCell(PrintUtil.createCellWithColspan(1, " "));
		contentMelding.addCell(PrintUtil.createCellWithColspan(1, " "));
		
		// For last row
		contentMelding.completeRow();

        meldingcell.addElement(contentMelding);
        result.addCell(meldingcell);
		
		return result;
		
	}
}
