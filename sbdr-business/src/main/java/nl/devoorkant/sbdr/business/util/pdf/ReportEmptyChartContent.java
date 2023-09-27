package nl.devoorkant.sbdr.business.util.pdf;

import java.util.ArrayList;
import java.util.List;

import nl.devoorkant.sbdr.business.transfer.MeldingOverviewTransfer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

public class ReportEmptyChartContent {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportEmptyChartContent.class);

    private static PdfPTable createTableEmpty() {
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

    private static String aantalCrediteuren(MeldingOverviewTransfer[] meldingen) {
    	List<String> bedrijfIds = new ArrayList<String>();
    	
    	if (meldingen != null) {
    		for (MeldingOverviewTransfer melding : meldingen) {
    			if (!bedrijfIds.contains(melding.getKvkNummer()))
    				bedrijfIds.add(melding.getKvkNummer());
    		}
    	}
    	
    	return bedrijfIds.size() + "";
    }
    
	public static PdfPTable createContent() {
        PdfPTable result = PrintUtil.createMasterTable();
        
		// melding master cell
		PdfPCell emptycell = new PdfPCell();		
		emptycell.setPadding(0);
		emptycell.setBorder(PdfPCell.NO_BORDER);
		
		PdfPTable contentEmpty = createTableEmpty();

		// empty
		contentEmpty.addCell(PrintUtil.createCellWithColspan(1, " "));
		
		// empty
		contentEmpty.addCell(PrintUtil.createCellWithColspan(1, " "));
		// empty
		contentEmpty.addCell(PrintUtil.createCellWithColspan(1, " "));
		// empty
		contentEmpty.addCell(PrintUtil.createCellWithColspan(1, " "));
		// empty
		contentEmpty.addCell(PrintUtil.createCellWithColspan(1, " "));
		// empty
		contentEmpty.addCell(PrintUtil.createCellWithColspan(1, " "));
		// empty
		contentEmpty.addCell(PrintUtil.createCellWithColspan(1, " "));
		// empty
		contentEmpty.addCell(PrintUtil.createCellWithColspan(1, " "));
		// empty
		contentEmpty.addCell(PrintUtil.createCellWithColspan(1, " "));
		// empty
		contentEmpty.addCell(PrintUtil.createCellWithColspan(1, " "));

		
		// For last row
		contentEmpty.completeRow();

        emptycell.addElement(contentEmpty);
        result.addCell(emptycell);
		
		return result;
		
	}
}
