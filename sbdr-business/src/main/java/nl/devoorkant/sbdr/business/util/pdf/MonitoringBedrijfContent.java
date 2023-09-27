package nl.devoorkant.sbdr.business.util.pdf;

import nl.devoorkant.sbdr.business.transfer.MonitoringOverviewTransfer;
import nl.devoorkant.sbdr.business.util.EBedrijfType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

public class MonitoringBedrijfContent {
    private static final Logger LOGGER = LoggerFactory.getLogger(MonitoringBedrijfContent.class);

    private static PdfPTable createTableBedrijf() {
        PdfPTable result = null;

        try {
            result = new PdfPTable(3);
            result.setWidths(new float[]{3, 5, 1});
            result.setWidthPercentage(100);
            result.setKeepTogether(false);
        } catch (DocumentException e1) {
            LOGGER.error("Cannot set document table parameters", e1);
        }

		return result;
	}

	public static PdfPTable createContent(MonitoringOverviewTransfer monitoring, boolean betalingsachterstand) {
        PdfPTable result = PrintUtil.createMasterTable();
        
		// bedrijf master cell
		PdfPCell bedrijfcell = new PdfPCell();		
		bedrijfcell.setPadding(0);
		bedrijfcell.setBorder(PdfPCell.NO_BORDER);
		
		PdfPTable contentBedrijf = createTableBedrijf();
						
		// Handelsnaam
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "Bedrijfsnaam"));
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, monitoring.getBedrijfsNaam()));
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
						
		// Adres
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "Straat en huisnummer"));
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, monitoring.getStraat() + " " + (monitoring.getHuisnummer() != null ? monitoring.getHuisnummer() : "") + (monitoring.getHuisnummerToevoeging() != null ? (" " + monitoring.getHuisnummerToevoeging()) : "")));
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
		
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "Postcode en plaats"));
		if (monitoring.getPostcode() != null)
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, monitoring.getPostcode() + " " + monitoring.getPlaats()));
		else
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "- " + monitoring.getPlaats()));
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));		
				
		// Hoofdvestiging
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "Vestiging"));
		if (monitoring.isHoofd())
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, EBedrijfType.HOOFD.getOmschrijving()));
		else
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, EBedrijfType.NEVEN.getOmschrijving()));
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));		

		// BA nummer
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "BA-nummer"));
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, monitoring.getSbdrNummer()));
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));

		// Kvk nummer
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "KvK-nummer"));
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, monitoring.getKvkNummer()));
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));		
		
		// Actief
		//contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "Bedrijfsstatus"));
		//contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, monitoring.getBedrijfActiefOmschrijving()));
				
		//contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
				
		// Empty regel
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));

		// For last row
		contentBedrijf.completeRow();

        bedrijfcell.addElement(contentBedrijf);
        result.addCell(bedrijfcell);
		
		return result;
		
	}    
	
}
