package nl.devoorkant.sbdr.business.util.pdf;

import nl.devoorkant.sbdr.business.service.DocumentServiceImpl;
import nl.devoorkant.sbdr.data.util.EDocumentType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

public class TopicLetter {
    private static final Logger LOGGER = LoggerFactory.getLogger(MeldingLetterContent.class);

    private static PdfPTable createTable() {
        PdfPTable result = null;

        try {
            result = new PdfPTable(1);
            result.setWidths(new float[]{1});
            result.setWidthPercentage(100);
        } catch (DocumentException e1) {
            LOGGER.error("Cannot set document table parameters", e1);
        }

		return result;
	}

	public static PdfPTable createTopicLetter(String kenmerk, String betreft, EDocumentType doctype) {
        PdfPTable result = PrintUtil.createMasterTable();
                
		// master cell
		PdfPCell cell = new PdfPCell();		
		cell.setPadding(0);
		if (doctype.equals(EDocumentType.FACTUUR))
			cell.setPaddingTop(DocumentServiceImpl.PADDINGTOP_TOPIC_CONTENT); // PADDING START (with spacing for heading title)
		cell.setBorder(PdfPCell.NO_BORDER);
		
		PdfPTable content = createTable();

		// kenmerk
		//content.addCell(PrintUtil.createCell("Ons kenmerk: " + kenmerk));
		
		// betreft
		content.addCell(PrintUtil.createCell("Onderwerp: " + betreft));
		
		// empty cell
		//result.addCell(PrintUtil.createCell(""));
		// empty cell
		result.addCell(PrintUtil.createCell("", PrintUtil.calibri_9));

		// For last row
		content.completeRow();

        cell.addElement(content);
        result.addCell(cell);
		
		return result;
		
	}
}
