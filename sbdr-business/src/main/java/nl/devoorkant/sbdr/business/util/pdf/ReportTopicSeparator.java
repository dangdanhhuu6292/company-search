package nl.devoorkant.sbdr.business.util.pdf;

import nl.devoorkant.sbdr.business.service.DocumentServiceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;

import com.lowagie.text.Chunk;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.draw.LineSeparator;

public class ReportTopicSeparator {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportTopicSeparator.class);

    private static PdfPTable createTable() {
        PdfPTable result = null;

        try {
            result = new PdfPTable(1);
            result.setWidths(new float[]{1});
            result.setWidthPercentage(100);
            result.setKeepTogether(true);
        } catch (DocumentException e1) {
            LOGGER.error("Cannot set document table parameters", e1);
        }

		return result;
	}

	public static PdfPTable createContent(String topic, boolean paddingtopic) {
        PdfPTable result = PrintUtil.createMasterTable();
               
		// master cell
		PdfPCell cell = new PdfPCell();		
		cell.setPadding(0);
		if (paddingtopic)
			cell.setPaddingTop(DocumentServiceImpl.PADDINGTOP_TOPIC_CONTENT); // PADDING START (with spacing for heading title)
		cell.setBorder(PdfPCell.NO_BORDER);
		
		PdfPTable content = createTable();		
		
		// header topic
		// MBR no left
		//content.addCell(PrintUtil.createCellWithColRowspan(1, 1, topic, PrintUtil.calibri_10_bold));
		PdfPCell topicCell = PrintUtil.createCellWithColRowspanLeftColor(1, 1, topic, PrintUtil.calibri_12_bold, Color.WHITE);
		
		topicCell.setBorder(PdfPCell.NO_BORDER);
	    content.addCell(topicCell);
		
		// For last row
		content.completeRow();

        cell.addElement(content);
        result.addCell(cell);
		
		return result;
		
	}
	
	public static PdfPTable createUnderline(String underlineText, boolean paddingtopic) {
        PdfPTable result = PrintUtil.createMasterTable();
               
        LineSeparator UNDERLINE =
                new LineSeparator(1, 100, null, Element.ALIGN_CENTER, -3);
        
        Paragraph p = new Paragraph(underlineText);
        p.setFont(PrintUtil.calibri_12_bold);        
        p.add(UNDERLINE);
        
		// master cell
		PdfPCell cell = new PdfPCell();		
		cell.setPadding(0);
		if (paddingtopic)
			cell.setPaddingTop(DocumentServiceImpl.PADDINGTOP_TOPIC_CONTENT); // PADDING START (with spacing for heading title)
		cell.setBorder(PdfPCell.NO_BORDER);
		
		PdfPTable content = createTable();		
		
		// header topic
		// MBR no left
		//content.addCell(PrintUtil.createCellWithColRowspan(1, 1, topic, PrintUtil.calibri_10_bold));
		PdfPCell topicCell = PrintUtil.createCellWithColRowspanLeftParagraphColor(1, 1, p, Color.WHITE);		
		
		topicCell.setBorder(PdfPCell.NO_BORDER);
	    content.addCell(topicCell);	    
		
		// For last row
		content.completeRow();

        cell.addElement(content);
        result.addCell(cell);
		
		return result;
		
	}	
	
}
