package nl.devoorkant.sbdr.business.util.pdf;

import java.awt.Color;

import nl.devoorkant.sbdr.business.service.DocumentServiceImpl;
import nl.devoorkant.sbdr.business.transfer.BedrijfReportTransfer;
import nl.devoorkant.sbdr.business.util.EBedrijfUitvoeringStatus;
import nl.devoorkant.util.FormatUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

public class ReportStatusHeaders {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportFromBedrijfContent.class);
    
    private static int COLOR_UNKNOWN = 0x5bc0de;
    private static int COLOR_SUCCESS = Color.WHITE.getRGB(); // 0x5cb85c;
    private static int COLOR_WARNING = 0xf0ad4e;
    private static int COLOR_DANGER = 0xd9534f;

    private static PdfPTable createTableStatus() {
        PdfPTable result = null;

        try {
            result = new PdfPTable(7);
            result.setWidths(new float[]{15, 1, 15, 1, 15, 1, 15});
            result.setWidthPercentage(100);
            result.setKeepTogether(false);
        } catch (DocumentException e1) {
            LOGGER.error("Cannot set document table parameters", e1);
        }

		return result;
	}
    
  	private static boolean isStatusSuccess(String status) {
		return status.equals(EBedrijfUitvoeringStatus.ACTIEF.getKorteOmschrijving());
  	}

  	private static boolean isStatusWarning(String status) {
		return status.equals(EBedrijfUitvoeringStatus.UITGESCHREVEN.getKorteOmschrijving());
  	}
  	
  	private static boolean isStatusDanger(String status) {
		return status.equals(EBedrijfUitvoeringStatus.NIET_ACTIEF.getKorteOmschrijving()) ||
				status.equals(EBedrijfUitvoeringStatus.ONTBONDEN.getKorteOmschrijving()) ||
				status.equals(EBedrijfUitvoeringStatus.OPGEHEVEN.getKorteOmschrijving());
  	}
  	
  	
  	private static boolean isFaillietSurseanceSuccess(String status) {
		return status.equals(EBedrijfUitvoeringStatus.NIET_FAILLIET_SURSEANCE.getKorteOmschrijving());
  	}

  	private static boolean isFaillietSurseanceWarning(String status) {
		return status.equals(EBedrijfUitvoeringStatus.SURSEANCE.getKorteOmschrijving());
  	}
  	
  	private static boolean isFaillietSurseanceDanger(String status) {
		return status.equals(EBedrijfUitvoeringStatus.FAILLIET_SURSEANCE.getKorteOmschrijving()) || status.equals(EBedrijfUitvoeringStatus.FAILLIET.getKorteOmschrijving());
  	}
  	    
  	private static boolean isBedragSuccess(Double bedrag) {
		return bedrag == null;
  	}
  	
  	private static boolean isBedragWarning(Double bedrag) {
		return bedrag != null && bedrag > 0;
  	}

  	private static Color getBedrijfStatusColor(String status) {
  		if (isStatusSuccess(status))
  			return new Color(COLOR_SUCCESS);
  		else if (isStatusWarning(status))
  			return new Color(COLOR_WARNING);
  		else if (isStatusDanger(status))
  			return new Color(COLOR_DANGER);
  		else
  			return new Color(COLOR_UNKNOWN);
  	}
  	
  	private static Color getBedragColor(Double bedrag) {
  		if (isBedragSuccess(bedrag))
  			return new Color(COLOR_SUCCESS);
  		else if (isBedragWarning(bedrag))
  			return new Color(COLOR_WARNING);
  		else
  			return new Color(COLOR_UNKNOWN);
  	}
  	
  	private static Color getVermeldingenActiefColor(int aantal) {
  		if (aantal <= 0)
  			return new Color(COLOR_SUCCESS);
  		else if (aantal > 0)
  			return new Color(COLOR_WARNING);
  		else
  			return new Color(COLOR_UNKNOWN);  		
  	}

  	private static Color getVermeldingenOpgelostColor(int aantal) {
  		if (aantal <= 0)
  			return new Color(COLOR_SUCCESS);
  		else if (aantal > 0)
  			return new Color(COLOR_WARNING);
  		else
  			return new Color(COLOR_UNKNOWN);  		
  	}  	
  	
  	private static Color getFaillietSurseanceColor(String failietSurseance) {
  		if (isFaillietSurseanceSuccess(failietSurseance))
  			return new Color(COLOR_SUCCESS);
  		//else if (isFaillietSurseanceWarning(failietSurseance))
  		//	return new Color(COLOR_WARNING);
  		//else
  		else if (isFaillietSurseanceDanger(failietSurseance))
  			return new Color(COLOR_DANGER);
  		else
  			return new Color(COLOR_UNKNOWN);  		
  	}
  	
	public static PdfPTable createContent(BedrijfReportTransfer report) {
		Color bluecolor = new Color(DocumentServiceImpl.COLOR_SBDR_BLUE);
		
        PdfPTable result = PrintUtil.createMasterTable();
        
		// bedrijf master cell
		PdfPCell statuscell = new PdfPCell();		
		statuscell.setPadding(0);
		statuscell.setPaddingTop(DocumentServiceImpl.PADDINGTOP_TOPIC_CONTENT); // PADDING START (with spacing for heading title)
		statuscell.setBorder(PdfPCell.NO_BORDER);
		
		PdfPTable contentStatus = createTableStatus();
				
		// BedrijfStatus
		PdfPCell cell = PrintUtil.createCellWithColRowspanCenterColor(1, 1, "Bedrijfsstatus", PrintUtil.calibri_8_bold_white, bluecolor);
		cell.setBorderColor(bluecolor);
		cell.setBorderWidthBottom(0f);
		contentStatus.addCell(cell);
		
		// filler
		contentStatus.addCell(PrintUtil.createCellWithColspan(1, " "));
		
		// Vermeldingen
		cell = PrintUtil.createCellWithColRowspanCenterColor(1, 1, "Betalingsachterstanden", PrintUtil.calibri_8_bold_white, bluecolor);
		cell.setBorderColor(bluecolor);
		cell.setBorderWidthBottom(0f);		
		contentStatus.addCell(cell);
		
		// filler
		contentStatus.addCell(PrintUtil.createCellWithColspan(1, " "));
		
		
		// Faillissement of surseance
		cell = PrintUtil.createCellWithColRowspanCenterColor(1, 1, "Faillissement of surseance", PrintUtil.calibri_8_bold_white, bluecolor);
		cell.setBorderColor(bluecolor);
		cell.setBorderWidthBottom(0f);				
		contentStatus.addCell(cell);
		
		// filler
		contentStatus.addCell(PrintUtil.createCellWithColspan(1, " "));
		
		
		// Bedragen
		cell = PrintUtil.createCellWithColRowspanCenterColor(1, 1, "Bedragen", PrintUtil.calibri_8_bold_white, bluecolor);
		cell.setBorderColor(bluecolor);
		cell.setBorderWidthBottom(0f);						
		contentStatus.addCell(cell);
		
		// values row
		
		// BedrijfStatus
		cell = PrintUtil.createCellWithColRowspanCenterColor(1, 1, report.getKvkDossierTransfer().getActief(), PrintUtil.calibri_8_bold, getBedrijfStatusColor(report.getKvkDossierTransfer().getActief()));
		cell.setBorderColor(bluecolor);
		cell.setBorderWidthTop(0f);
		cell.setBorderWidthBottom(0f);		
		contentStatus.addCell(cell);
		
		// filler
		contentStatus.addCell(PrintUtil.createCellWithColspan(1, " "));
		
		// Vermeldingen
		cell = PrintUtil.createCellWithColRowspanCenterColor(1, 1, report.getAantalMeldingenActief() + "", PrintUtil.calibri_8_bold, getVermeldingenActiefColor(report.getAantalMeldingenActief()));
		cell.setBorderColor(bluecolor);
		cell.setBorderWidthTop(0f);
		cell.setBorderWidthBottom(0f);				
		contentStatus.addCell(cell);
		
		// filler
		contentStatus.addCell(PrintUtil.createCellWithColspan(1, " "));
		
		
		// Faillissement of surseance
		cell = PrintUtil.createCellWithColRowspanCenterColor(1, 1, report.getKvkDossierTransfer().getFaillietSurseance() + "", PrintUtil.calibri_8_bold, getFaillietSurseanceColor(report.getKvkDossierTransfer().getFaillietSurseance()));
		cell.setBorderColor(bluecolor);
		cell.setBorderWidthTop(0f);
		cell.setBorderWidthBottom(0f);		
		contentStatus.addCell(cell);
		
		// filler
		contentStatus.addCell(PrintUtil.createCellWithColspan(1, " "));
		
		
		// Bedragen		
		Double bedragval = null;
		if (report.getBedragOpenstaand() != null)
			bedragval = report.getBedragOpenstaand().doubleValue();
		String bedrag = PrintUtil.formatCurrency(bedragval);				
		
		String bedragopen = "";
		if (bedrag != null && !bedrag.equals(""))
			bedragopen += "\u20AC " + bedrag;
		else
			bedragopen += "-";
		cell = PrintUtil.createCellWithColRowspanCenterColor(1, 1, bedragopen, PrintUtil.calibri_8_bold, getBedragColor(bedragval));
		cell.setBorderColor(bluecolor);
		cell.setBorderWidthTop(0f);
		cell.setBorderWidthBottom(0f);		
		contentStatus.addCell(cell);

		// values row
		
		// BedrijfStatus
		cell = PrintUtil.createCellWithColRowspanCenterColor(1, 1, report.getKvkDossierTransfer().getActiefOmschrijving(), PrintUtil.calibri_6_bold, getBedrijfStatusColor(report.getKvkDossierTransfer().getActief()));
		cell.setBorderColor(bluecolor);
		cell.setBorderWidthTop(0f);
		contentStatus.addCell(cell);
		
		// filler
		contentStatus.addCell(PrintUtil.createCellWithColspan(1, " "));
		
		// Vermeldingen
		String notificationText = report.getAantalMeldingenActief()==1?"Vermelding actief":"Vermeldingen actief";
		cell = PrintUtil.createCellWithColRowspanCenterColor(1, 1, notificationText, PrintUtil.calibri_6_bold, getVermeldingenActiefColor(report.getAantalMeldingenActief()));
		cell.setBorderColor(bluecolor);
		cell.setBorderWidthTop(0f);
		contentStatus.addCell(cell);
		
		// filler
		contentStatus.addCell(PrintUtil.createCellWithColspan(1, " "));
		
		
		// Faillissement of surseance
		cell = PrintUtil.createCellWithColRowspanCenterColor(1, 1, report.getKvkDossierTransfer().getFaillietSurseanceOmschrijving(), PrintUtil.calibri_6_bold, getFaillietSurseanceColor(report.getKvkDossierTransfer().getFaillietSurseance()));
		cell.setBorderColor(bluecolor);
		cell.setBorderWidthTop(0f);
		contentStatus.addCell(cell);
		
		// filler
		contentStatus.addCell(PrintUtil.createCellWithColspan(1, " "));
		
		
		// Bedragen
		bedragval = null;
		if (report.getBedragOpenstaand() != null)
			bedragval = report.getBedragOpenstaand().doubleValue();
		bedrag = PrintUtil.formatCurrency(bedragval);
//		String bedragresolved = "Gesloten: ";
//		if (bedrag != null && !bedrag.equals(""))
//			bedragresolved += "\u20AC " + bedrag;
//		else
//			bedragresolved += "-";
		String bedragOpenText = "Bedrag openstaand";
		cell = PrintUtil.createCellWithColRowspanCenterColor(1, 1, bedragOpenText, PrintUtil.calibri_6_bold, getBedragColor(bedragval));
		cell.setBorderColor(bluecolor);
		cell.setBorderWidthTop(0f);
		contentStatus.addCell(cell);

		// For last row
		contentStatus.completeRow();

        statuscell.addElement(contentStatus);
        result.addCell(statuscell);
		
		return result;
		
	}
}
