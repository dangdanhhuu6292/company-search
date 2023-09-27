package nl.devoorkant.sbdr.business.util.pdf;

import java.awt.Color;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPCellEvent;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class HeadingContent {

	public static PdfPTable createContent(PdfWriter writer, Document document, String title, Color color) {
        float totalwidth = document.right() - document.left() + document.leftMargin() + document.rightMargin();        		
		
        PdfPCell acell = PrintUtil.createCellWithColRowspanLeftColor(1,  1,  title,  PrintUtil.calibri_14_bold_white, color);
        acell.setVerticalAlignment(PdfPCell.ALIGN_CENTER);
        acell.setPaddingLeft(10f + document.leftMargin());
        acell.setBorder(PdfPCell.NO_BORDER);
        
        PdfPTable headerContent = new PdfPTable(1);
		//headerContent.setTotalWidth(totalwidth * .75f);
        float widthText = PrintUtil.calibri_14_bold_white.getCalculatedBaseFont(false).getWidthPoint(title, 15); // PrintUtil.calibri_14_bold_white.getCalculatedSize()     
        widthText = Math.max(widthText, 166f); // min width set
		headerContent.setTotalWidth(widthText + document.leftMargin() + 30f);
		
		headerContent.setWidthPercentage(100f);
		headerContent.setLockedWidth(true);
		headerContent.setSpacingBefore(0f);
		headerContent.setSpacingAfter(0f);
		headerContent.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);	
		
		// heading cell
		//PdfPTableEvent headingtableBackground = new TableBackground();
		PdfPCellEvent headingcellBackground = new CellBackground(color); // new Color(0x2C3D4F)
		
		PdfPCell headingcell = new PdfPCell();	
		headingcell.setColspan(1);
		headingcell.setBorder(PdfPCell.NO_BORDER);
		//headingcell.setLeading(0f,  1f);		
		//headingcell.setBorder(Rectangle.BOX);
		//headingcell.setBorderColor(Color.BLUE);
		headingcell.setCellEvent(headingcellBackground);
		//headingcell.setPadding(10f);
		//headingcell.setPaddingLeft(20f);
		headingcell.setPaddingTop(10f);
		headingcell.setPaddingBottom(10f);
        headingcell.setHorizontalAlignment(Element.ALIGN_CENTER);
	 
        PdfPTable headingtable = new PdfPTable(1);	
        headingtable.setWidthPercentage(100f);
        headingtable.setSpacingBefore(5f);
        headingtable.setSpacingAfter(5f);
		headingtable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
        headingtable.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);		     					
        
        headingtable.addCell(acell);	        
        headingcell.addElement(headingtable);
        
        headerContent.addCell(headingcell);        
		
        float x = -10f;
        float y = document.getPageSize().getHeight() - document.topMargin() + headerContent.getTotalHeight() - 30f; // - 75;
        
        try {
	    	 headerContent.writeSelectedRows(0, -1, x, y, writer.getDirectContent()); // headerContent
	     } catch (Exception e) {
	    	 //LOGGER.error("Error writing report: " + e.getMessage());
	     }
        
        return headerContent;
	}
}
