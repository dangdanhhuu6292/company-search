package nl.devoorkant.sbdr.business.util.pdf;

import nl.devoorkant.sbdr.data.model.Bedrijf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

public class InvoiceBedrijfContent {
    private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceBedrijfContent.class);

    private static PdfPTable createTableBedrijf() {
        PdfPTable result = null;

        try {
            result = new PdfPTable(2);
            result.setWidths(new float[]{1, 1});
            result.setWidthPercentage(100);
            result.setKeepTogether(false);
        } catch (DocumentException e1) {
            LOGGER.error("Cannot set document table parameters", e1);
        }

		return result;
	}

	public static PdfPTable createContent(Bedrijf bedrijf) {
        PdfPTable result = PrintUtil.createMasterTable();
        
		// bedrijf master cell
		PdfPCell bedrijfcell = new PdfPCell();		
		bedrijfcell.setPadding(0);
		bedrijfcell.setBorder(PdfPCell.NO_BORDER);
		
		PdfPTable contentBedrijf = createTableBedrijf();
				
//		// BA nummer
//		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "BA-nummer"));
//		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, monitoring.getSbdrNummer()));
//		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
//
//		// Kvk nummer
//		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "KvK nummer"));
//		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, monitoring.getKvkNummer()));
//		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));		
//		
//		// Handelsnaam
//		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "Bedrijfsnaam"));
//		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, monitoring.getBedrijfsNaam()));
//		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
//						
//		// Adres
//		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "Adres"));
//		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, monitoring.getStraat() + " " + (monitoring.getHuisnummer() != null ? monitoring.getHuisnummer() : "") + " " + (monitoring.getHuisnummerToevoeging() != null ? monitoring.getHuisnummerToevoeging() : null)));
//		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
//		
//		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
//		if (monitoring.getPostcode() != null)
//			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, monitoring.getPostcode() + " " + monitoring.getPlaats()));
//		else
//			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "- " + monitoring.getPlaats()));
//		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));		
//				
//		// Hoofdvestiging
//		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "Vestiging"));
//		if (monitoring.isHoofd())
//			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, EBedrijfType.HOOFD.getOmschrijving()));
//		else
//			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, EBedrijfType.NEVEN.getOmschrijving()));
//		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));		
//
//		// Actief
//		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "Bedrijfstatus"));
//		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, monitoring.getBedrijfActiefOmschrijving()));
//				
//		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
//				
//		// Empty regel
//		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
//		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
//		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
//
//		if (!betalingsachterstand)
//			contentBedrijf.addCell(PrintUtil.createCellWithColspan(3, "Er zijn van dit bedrijf geen betalingsachterstanden actief."));
//		// For last row
		contentBedrijf.completeRow();

        bedrijfcell.addElement(contentBedrijf);
        result.addCell(bedrijfcell);
		
		return result;
		
	}    
	
}
