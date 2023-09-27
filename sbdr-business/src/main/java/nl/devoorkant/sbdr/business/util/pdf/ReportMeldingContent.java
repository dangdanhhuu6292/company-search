package nl.devoorkant.sbdr.business.util.pdf;

import nl.devoorkant.sbdr.business.service.DocumentServiceImpl;
import nl.devoorkant.sbdr.business.transfer.BedrijfReportTransfer;
import nl.devoorkant.sbdr.business.transfer.MeldingOverviewTransfer;
import nl.devoorkant.util.FormatUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPCellEvent;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPTableEvent;

public class ReportMeldingContent {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportMeldingContent.class);

    private static PdfPTable createTableBgMelding() {
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
    
    private static PdfPTable createTableMelding() {    	
        PdfPTable result = null;

        try {
            result = new PdfPTable(2);
            result.setWidths(new float[]{3, 2});
            result.setWidthPercentage(100);
    		result.setKeepTogether(true);

        } catch (DocumentException e1) {
            LOGGER.error("Cannot set document table parameters", e1);
        }

		return result;
	}    

	public static PdfPTable createContent(Document document, BedrijfReportTransfer bedrijfreport) {
		PdfPTableEvent tableBackground = new TableBackground();
		PdfPCellEvent cellBackground = new CellBackground();
        PdfPTable result = PrintUtil.createMasterTable();
               	        
		// meldingen master cell page one
		PdfPCell meldingencellOne = new PdfPCell();		
		meldingencellOne.setPadding(0);
		meldingencellOne.setPaddingTop(DocumentServiceImpl.PADDINGTOP_TOPIC_CONTENT); // PADDING START (with spacing for heading title)
		meldingencellOne.setPaddingBottom(document.bottomMargin()); // footer spacing		
		meldingencellOne.setBorder(PdfPCell.NO_BORDER);
		
		// meldingen master cell page > 1
		PdfPCell meldingencell = new PdfPCell();		
		meldingencell.setPadding(0);
		meldingencell.setPaddingTop(0);
		meldingencell.setPaddingBottom(document.bottomMargin()); // footer spacing
		meldingencell.setBorder(PdfPCell.NO_BORDER);		
		//PdfPTable contentMeldingen = createTableMeldingen();        		
		
        if (bedrijfreport.getMeldingen() != null)
        {                  
    		PdfPTable contentMeldingenOne = new PdfPTable(1);
    		contentMeldingenOne.setWidthPercentage(100);
    		contentMeldingenOne.setSplitRows(false);
    		contentMeldingenOne.setKeepTogether(true);
    		contentMeldingenOne.setHeaderRows(0);
    		
    		PdfPTable contentMeldingen = new PdfPTable(1);
    		contentMeldingen.setWidthPercentage(100);
    		
        	int index = 1;
	        for (MeldingOverviewTransfer melding: bedrijfreport.getMeldingen()) {
	        	// melding master cell
	    		PdfPCell meldingcell = new PdfPCell();		
	    		meldingcell.setPadding(0);
	    		//if (index == 1)
	    		//	meldingcell.setPaddingTop(DocumentServiceImpl.PADDINGTOP_TOPIC_CONTENT); // PADDING START (with spacing for heading title)
	    		//else
	    		//	meldingcell.setPaddingTop(0);
	    		meldingcell.setBorder(PdfPCell.NO_BORDER);
	    		meldingcell.setCellEvent(cellBackground);
	    		meldingcell.setPadding(10f);
	    		meldingcell.setPaddingTop(5f);
	    		
	    		// melding backgroundtable
	    		PdfPTable meldingBgTable = createTableBgMelding();
	    		meldingBgTable.setTableEvent(tableBackground);

	    		
	    		// melding master table
	    		PdfPTable meldingtable = createTableMelding();	
	    		
	    		float padding = 3;
	    			    		
	    		meldingtable.addCell(PrintUtil.createCellWithColRowspan(1, 1, "Vermelding", PrintUtil.calibri_10_bold));	    		
	    		meldingtable.addCell(PrintUtil.createCellWithColRowspan(1, 1, melding.getReferentieIntern(), PrintUtil.calibri_10_bold));	    		

	    		meldingtable.addCell(PrintUtil.createCellWithColRowspan(1, 1, "Opname in register", PrintUtil.calibri_10_bold));	    		
	    		meldingtable.addCell(PrintUtil.createCellWithColRowspan(1, 1, FormatUtil.formatDate(melding.getDatumGeaccordeerd(), "dd-MM-yyyy"), PrintUtil.calibri_10_bold));	    		
	    			    			
	    		meldingtable.addCell(PrintUtil.createCellWithColRowspan(1, 1, "Verloopdatum factuur", PrintUtil.calibri_10));	    		
	    		meldingtable.addCell(PrintUtil.createCellWithColRowspan(1, 1, FormatUtil.formatDate(melding.getVerloopdatumFactuur(), "dd-MM-yyyy"), PrintUtil.calibri_10));
	    		
	    		
	    		
	    		String meldingBedrag = "Afgeschermd"; //"MEER DAN \u20AC 500,-";
	    		if (melding.getBedrag() != null) {
	    			meldingBedrag = "\u20AC " + PrintUtil.formatCurrency(melding.getBedrag().doubleValue());			    		
	    		}	 
	    		
	    		String meldingOorspronkelijkBedrag = "Afgeschermd"; //"MEER DAN \u20AC 500,-";
	    		if (melding.getOorspronkelijkBedrag() != null)
	    			meldingOorspronkelijkBedrag = "\u20AC " + PrintUtil.formatCurrency(melding.getOorspronkelijkBedrag().doubleValue());	
	    		
	    		if (melding.getBedrijfId() != null) { // wel zichtbaar
	    			String adres = melding.getStraat();
	    			if (melding.getStraat() != null && melding.getHuisnummer() != null && melding.getHuisnummerToevoeging() != null)
	    				adres = melding.getStraat() + " " + melding.getHuisnummer() + melding.getHuisnummerToevoeging();
	    			else if (melding.getStraat() != null && melding.getHuisnummer() != null)
	    				adres = melding.getStraat() + " " + melding.getHuisnummer();
	    			else
	    				adres = melding.getStraat();	
	    			
	    			// verwijderd 
		    		//meldingtable.addCell(PrintUtil.createCellWithColRowspan(1, 1, "Factuur referentie bedrijf", PrintUtil.calibri_10));	    		
		    		//meldingtable.addCell(PrintUtil.createCellWithColRowspan(1, 1, melding.getReferentie(), PrintUtil.calibri_10));	    		

		    		meldingtable.addCell(PrintUtil.createCellWithColRowspan(1, 1, "Bedrijfsgegevens vermelder", PrintUtil.calibri_10));	    		
		    		meldingtable.addCell(PrintUtil.createCellWithColRowspan(1, 1, melding.getBedrijfsNaam(), PrintUtil.calibri_10));	    		

		    		// verwijderd
		    		//meldingtable.addCell(PrintUtil.createCellWithColRowspan(1, 1, "Adresgegevens", PrintUtil.calibri_10));	    		
		    		//meldingtable.addCell(PrintUtil.createCellWithColRowspan(1, 1, adres + ", " + melding.getPostcode() + " " + melding.getPlaats(), PrintUtil.calibri_10));	    				    		

	    		} else { // niet zichtbaar
	    			// verwijderd
		    		//meldingtable.addCell(PrintUtil.createCellWithColRowspan(1, 1, "Factuur referentie bedrijf", PrintUtil.calibri_10));	    		
		    		//meldingtable.addCell(PrintUtil.createCellWithColRowspan(1, 1, "Afgeschermd", PrintUtil.calibri_10));	    		

		    		meldingtable.addCell(PrintUtil.createCellWithColRowspan(1, 1, "Bedrijfssector vermelder", PrintUtil.calibri_10));	 
		    		if (melding.getSbiOmschrijving() != null)
		    			meldingtable.addCell(PrintUtil.createCellWithColRowspan(1, 1, melding.getSbiOmschrijving(), PrintUtil.calibri_10));	    		
		    		else 
		    			meldingtable.addCell(PrintUtil.createCellWithColRowspan(1, 1, "Geen", PrintUtil.calibri_10));

		    		// verwijderd
		    		//meldingtable.addCell(PrintUtil.createCellWithColRowspan(1, 1, "Adresgegevens", PrintUtil.calibri_10));	    		
		    		//meldingtable.addCell(PrintUtil.createCellWithColRowspan(1, 1, "Afgeschermd", PrintUtil.calibri_10));	    				    		
		    		
	    		}
	    		
	    		meldingtable.addCell(PrintUtil.createCellWithColRowspan(1, 1, "Oorspronkelijk bedrag", PrintUtil.calibri_10_bold));	    		
	    		meldingtable.addCell(PrintUtil.createCellWithColRowspan(1, 1, meldingOorspronkelijkBedrag, PrintUtil.calibri_10_bold));	    		
	    		
	    		meldingtable.addCell(PrintUtil.createCellWithColRowspan(1, 1, "Openstaand bedrag", PrintUtil.calibri_10_bold));	    		
	    		meldingtable.addCell(PrintUtil.createCellWithColRowspan(1, 1, meldingBedrag, PrintUtil.calibri_10_bold));	    		
	    		
	    		// For last row
	    		meldingtable.completeRow();
	    			 
	    		meldingcell.addElement(meldingtable);    		
	    		
	    		//contentMeldingen.addCell(meldingcell); 
	    		if (index <= 3) {
	    			contentMeldingenOne.addCell(meldingcell);
		    		// empty row
	    			contentMeldingenOne.addCell(PrintUtil.createCell(" "));	    			
	    		}
	    		else {
	    			contentMeldingen.addCell(meldingcell);
		    		// empty row
	    			contentMeldingen.addCell(PrintUtil.createCell(" "));
	    			
	    		}
	    		
	    		
	    		// empty row
	    		//result.addCell(PrintUtil.createCell(" "));
	    		
	    		//result.addCell(meldingcell);	    		
	    		
	    		index++;
	        }
	        
	        // For last row
	        contentMeldingenOne.completeRow();
	        meldingencellOne.addElement(contentMeldingenOne);
	        result.addCell(meldingencellOne);
	        if (index > 5) {
		        contentMeldingen.completeRow();
	        	meldingencell.addElement(contentMeldingen);
		        result.addCell(meldingencell);
	        }
        }
        else {
        	// melding master cell
    		PdfPCell meldingcell = new PdfPCell();		
    		meldingcell.setPadding(0);
        	meldingcell.setBorder(PdfPCell.NO_BORDER);
        	
        	// melding master table
    		PdfPTable meldingtable = createTableMelding();	        	
        	
    		meldingtable.addCell(PrintUtil.createCell("Geen vermeldingen actief in het register."));
    		// empty cell
    		meldingtable.addCell(PrintUtil.createCell(" "));
    		
    		// For last row
    		meldingtable.completeRow();

    		meldingcell.addElement(meldingtable);
    		
    		PdfPTable contentMeldingen = new PdfPTable(1);
    		contentMeldingen.setWidthPercentage(100);
    		contentMeldingen.addCell(meldingcell);
    		meldingencell.addElement(contentMeldingen);
    		
    		
    		result.addCell(meldingencell);
    		
        }
		
        return result;
	}
}
