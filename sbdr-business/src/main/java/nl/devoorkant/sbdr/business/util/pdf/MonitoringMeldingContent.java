package nl.devoorkant.sbdr.business.util.pdf;

import nl.devoorkant.sbdr.business.transfer.MeldingOverviewTransfer;
import nl.devoorkant.util.FormatUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPCellEvent;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPTableEvent;

public class MonitoringMeldingContent {
    private static final Logger LOGGER = LoggerFactory.getLogger(MonitoringMeldingContent.class);

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
            result.setWidths(new float[]{1, 1});
            result.setWidthPercentage(100);
    		result.setKeepTogether(true);

        } catch (DocumentException e1) {
            LOGGER.error("Cannot set document table parameters", e1);
        }

		return result;
	}    

	public static PdfPTable createContent(MeldingOverviewTransfer[] meldingen) {
		PdfPTableEvent tableBackground = new TableBackground();
		PdfPCellEvent cellBackground = new CellBackground();
        PdfPTable result = PrintUtil.createMasterTable();
               	        
		// meldingen master cell
		//PdfPCell meldingencell = new PdfPCell();		
		//meldingencell.setPadding(0);
		//meldingencell.setBorder(PdfPCell.NO_BORDER);
		
		//PdfPTable contentMeldingen = createTableMeldingen();
        		
        if (meldingen != null)
        {                  
        	int index = 1;
	        for (MeldingOverviewTransfer melding: meldingen) {
	        	// melding master cell
	    		PdfPCell meldingcell = new PdfPCell();		
	    		meldingcell.setPadding(0);
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
	    		
	    		// empty row
	    		result.addCell(PrintUtil.createCell(" "));
	    		
	    		result.addCell(meldingcell);
	    		
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
    		
    		result.addCell(meldingcell);
    		
        }
        
		// For last row
		//contentMeldingen.completeRow();
        
        //meldingencell.addElement(contentMeldingen);	 
        	 
        //result.addCell(bedrijfcell);        
        //result.addCell(meldingencell);
		return result;
		
	}
}
