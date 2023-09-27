package nl.devoorkant.sbdr.business.util.pdf;

import nl.devoorkant.sbdr.business.service.DocumentServiceImpl;
import nl.devoorkant.sbdr.data.model.Bedrijf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPCellEvent;
import com.lowagie.text.pdf.PdfPTable;

public class ReportFromBedrijfContent {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportFromBedrijfContent.class);
   
    private static PdfPTable createTableBedrijf() {    	
        PdfPTable result = null;

        try {
            result = new PdfPTable(1);
            result.setWidths(new float[]{1});
            result.setWidthPercentage(100);
    		result.setHorizontalAlignment(PdfPTable.ALIGN_LEFT);
    		result.setKeepTogether(true);

        } catch (DocumentException e1) {
            LOGGER.error("Cannot set document table parameters", e1);
        }

		return result;
	}    
    
    private static PdfPTable createTable() {
        PdfPTable result = null;

        try {
            result = new PdfPTable(1);
            result.setWidths(new float[]{1});
            //result.setTotalWidth(180f);
    		//result.setHorizontalAlignment(PdfPTable.ALIGN_LEFT);
            result.setWidthPercentage(100);
            result.setKeepTogether(false);
        } catch (DocumentException e1) {
            LOGGER.error("Cannot set document table parameters", e1);
        }

		return result;
	}

	public static PdfPTable createContent(Bedrijf bedrijf, Bedrijf fromBedrijf) {
        PdfPTable result = PrintUtil.createMasterTable();
        PdfPCell acell = null;
       
		// master cell
		PdfPCell cell = new PdfPCell();		
		cell.setPadding(0);
		cell.setPaddingTop(DocumentServiceImpl.PADDINGTOP_TOPIC_CONTENT); // PADDING START (with spacing for heading title)
		cell.setBorder(PdfPCell.NO_BORDER);		
		
		PdfPTable content = createTable();
		
		//------------------------------------//
		
		// Bedrijf info short
		//PdfPTableEvent tableBackground = new TableBackground();
		PdfPCellEvent cellBackground = new CellBackground();
		
		// bedrijf info master cell
		PdfPCell bedrijfcell = new PdfPCell();		
		bedrijfcell.setPadding(0);
		bedrijfcell.setBorder(PdfPCell.NO_BORDER);
		//bedrijfcell.setBorder(PdfPCell.BOX);
		//bedrijfcell.setBackgroundColor(Color.BLUE);
		bedrijfcell.setCellEvent(cellBackground);
		bedrijfcell.setPadding(5f);
		bedrijfcell.setPaddingBottom(10f);
		
		// bedrijf info backgroundtable
		//PdfPTable bedrijfBgTable = createTableBgBedrijf();
		//bedrijfBgTable.setTableEvent(tableBackground);

		
		// bedrijf master table
		PdfPTable bedrijftable = createTableBedrijf();	
		
		// Naam
		acell = PrintUtil.createCellWithColRowspan(1, 1, bedrijf.getBedrijfsNaam(), PrintUtil.calibri_10_bold);
		bedrijftable.addCell(acell);

		// Adres
		String suffix = "";
		if (bedrijf.getHuisNrToevoeging() != null)
			suffix = " " + bedrijf.getHuisNrToevoeging();
		acell = PrintUtil.createCellWithColRowspan(1, 1, bedrijf.getStraat() + " " + (bedrijf.getHuisNr() != null ? bedrijf.getHuisNr() : "")  + suffix, PrintUtil.calibri_10);
		acell.setPadding(0);
		bedrijftable.addCell(acell);

		// Postcode + plaats
		acell = PrintUtil.createCellWithColRowspan(1, 1, bedrijf.getPostcode() + " " + bedrijf.getPlaats(), PrintUtil.calibri_10);
		acell.setPadding(0);
		bedrijftable.addCell(acell);

		// SBDR Nummer
		//acell = PrintUtil.createCell("BA-nummer " + fromBedrijf.getSbdrNummer());
		//acell.setPadding(0);
		//content.addCell(acell);
		
		// KvKnummer
		acell = PrintUtil.createCellWithColRowspan(1, 1, "KvK-nummer " + bedrijf.getKvKnummer(), PrintUtil.calibri_10);
		bedrijftable.addCell(acell);
		
		// For last row
		bedrijftable.completeRow();
			 
		bedrijfcell.addElement(bedrijftable);
		// table wrapper
		PdfPTable wrapper = new PdfPTable(2);
		try {
			wrapper.setWidths(new float[]{80f, 20f});
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		wrapper.setHorizontalAlignment(PdfPTable.ALIGN_LEFT);
		//wrapper.setWidthPercentage(100f);
		wrapper.addCell(bedrijfcell);
		wrapper.addCell(PrintUtil.createCell(" "));
		acell = new PdfPCell();
		acell.setVerticalAlignment( PdfPCell.ALIGN_MIDDLE);		
	    acell.setHorizontalAlignment( PdfPCell.ALIGN_LEFT);
        acell.setBorder(PdfPCell.NO_BORDER);
//	    acell.setBorder(PdfPCell.BOX);
//	    acell.setBorderColor(Color.GREEN);
        acell.addElement(wrapper);
        content.addCell(acell);		
		
//		PdfPTable wrapper = new PdfPTable(2);
//        try {
//			wrapper.setWidths(new float[]{1, 1});
//	        wrapper.setWidthPercentage(100);
//	        wrapper.addCell(bedrijfcell);
//	        wrapper.addCell(PrintUtil.createCell(" "));
//	        acell = new PdfPCell();
//	        acell.setBorder(PdfPCell.NO_BORDER);
//	        acell.addElement(wrapper);
//	        content.addCell(acell);
//		} catch (DocumentException e) {
//			// TODO Auto-generated catch block
//			
//		}
		
		//---------------------------------------//
		
        // empty cell
     	content.addCell(PrintUtil.createCell(" "));
     	
        // empty cell
     	content.addCell(PrintUtil.createCell(" "));

        // empty cell
     	content.addCell(PrintUtil.createCell(" "));

     	// Intro
		content.addCell(PrintUtil.createCell("Dit rapport wordt strikt vertrouwelijk en uitsluitend voor interne doeleinden verstrekt aan:"));
					
		// empty cell
		content.addCell(PrintUtil.createCell(" "));						
		
		// Naam
		acell = PrintUtil.createCellWithColRowspan(1,  1, fromBedrijf.getBedrijfsNaam(), PrintUtil.calibri_10); //PrintUtil.createCell(fromBedrijf.getBedrijfsNaam());
		acell.setPadding(0);
		content.addCell(acell);

		// Adres
		suffix = "";
		if (fromBedrijf.getHuisNrToevoeging() != null)
			suffix = " " + fromBedrijf.getHuisNrToevoeging();
		//acell = PrintUtil.createCell(fromBedrijf.getStraat() + " " + (fromBedrijf.getHuisNr() != null ? fromBedrijf.getHuisNr() : "")  + suffix);
		acell = PrintUtil.createCellWithColRowspan(1, 1, fromBedrijf.getStraat() + " " + (fromBedrijf.getHuisNr() != null ? fromBedrijf.getHuisNr() : "")  + suffix, PrintUtil.calibri_10);
		acell.setPadding(0);
		content.addCell(acell);

		// Postcode + plaats
		//acell = PrintUtil.createCell(fromBedrijf.getPostcode() + " " + fromBedrijf.getPlaats());
		acell = PrintUtil.createCellWithColRowspan(1, 1, fromBedrijf.getPostcode() + " " + fromBedrijf.getPlaats(), PrintUtil.calibri_10);
		acell.setPadding(0);
		content.addCell(acell);

		// SBDR Nummer
		//acell = PrintUtil.createCellWithColRowspan(1, 1, "BA-nummer " + fromBedrijf.getSbdrNummer(), PrintUtil.calibri_10_bold);
		//acell.setPadding(0);
		//content.addCell(acell);
		
		// KvKnummer
		//content.addCell(PrintUtil.createCell("Kvk nummer: " + fromBedrijf.getKvKnummer()));
		content.addCell(PrintUtil.createCellWithColRowspan(1, 1, "Kvk-nummer: " + fromBedrijf.getKvKnummer(), PrintUtil.calibri_10));

		// empty cell
		content.addCell(PrintUtil.createCell(" "));
				
        // empty cell
     	content.addCell(PrintUtil.createCell(" "));
     	
        // empty cell
     	content.addCell(PrintUtil.createCell(" "));
		
		// Bijschrijft
		content.addCell(PrintUtil.createCell("De inhoud of een gedeelte daarvan mag zonder schriftelijke toestemming van CRZB niet aan derden worden verstrekt."));
		
		// empty cell
		content.addCell(PrintUtil.createCell(" "));
		
		// For last row
		content.completeRow();

		cell.addElement(content);
        result.addCell(cell);
		
		return result;
		
	}
}
