package nl.devoorkant.sbdr.business.util.pdf;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import nl.devoorkant.sbdr.business.transfer.BedrijfReportTransfer;
import nl.devoorkant.sbdr.business.transfer.OpmerkingenTransfer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import com.google.common.io.ByteStreams;
import com.lowagie.text.BadElementException;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

public class ReportOverviewContent {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportOverviewContent.class);
    
    public static PdfPTable createTableOverview() {
        PdfPTable result = null;

        try {
            result = new PdfPTable(2);
            result.setWidths(new float[]{1, 10});
            result.setWidthPercentage(100);
            result.setKeepTogether(false);
        } catch (DocumentException e1) {
            LOGGER.error("Cannot set document table parameters", e1);
        }

		return result;
	}
    
  	
	public static PdfPTable createContent(BedrijfReportTransfer report, Resource warning_icon, Resource info_icon) {
        PdfPTable result = PrintUtil.createMasterTable();
        
		// overview master cell
		PdfPCell overviewcell = new PdfPCell();		
		overviewcell.setPadding(0);
		//overviewcell.setPaddingTop(DocumentServiceImpl.PADDINGTOP_TOPIC_CONTENT); // PADDING START (with spacing for heading title)
		overviewcell.setBorder(PdfPCell.NO_BORDER);
		
		PdfPTable contentOverview = createTableOverview();
		
		Image warningImg = null;
		Image infoImg = null;
		
		try {

			 if (warning_icon != null && warning_icon.exists()) {
		            final InputStream inputStream = warning_icon.getInputStream();
		            
		            warningImg = Image.getInstance(ByteStreams.toByteArray(inputStream)); // utility to get byte array of contents
		            warningImg.scaleAbsolute(12f, 12f);
		            warningImg.setDpi(300, 300);
		     }	
			 if (info_icon != null && info_icon.exists()) {
		            final InputStream inputStream = info_icon.getInputStream();
		            
		            infoImg = Image.getInstance(ByteStreams.toByteArray(inputStream)); // utility to get byte array of contents
		            infoImg.scaleAbsolute(12f,  12f);
		            infoImg.setDpi(300, 300);
		     }				 
		} catch (BadElementException e) {
			LOGGER.error("BadElement", e);
		} catch (MalformedURLException e) {
			LOGGER.error("Wrong URL", e);
		} catch (IOException e) {
			LOGGER.error("IOException", e);
		}				
		
		if (report.getOpmerkingen() != null) {
			//for (int i=0;i<2;i++)
			for (OpmerkingenTransfer opmerking: report.getOpmerkingen()) {
			    //imgcell.setFixedHeight(10f);
				if (opmerking.getType().equals("info")) {
					// we wrap the image in a PdfPCell				
					PdfPCell cell = new PdfPCell(infoImg);
					cell.setBorder(PdfPCell.NO_BORDER);
					cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
					cell.setVerticalAlignment(PdfPCell.ALIGN_BOTTOM);
					cell.setPaddingTop(4f);
				    contentOverview.addCell(cell);
					contentOverview.addCell(PrintUtil.createCellWithColRowspanVerticalCenter(1, 1, opmerking.getOmschrijving(), PrintUtil.calibri_10_italic));
				} else if (opmerking.getType().equals("alert")) {
					// we wrap the image in a PdfPCell				
					PdfPCell cell = new PdfPCell(warningImg);
					cell.setBorder(PdfPCell.NO_BORDER);
					cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
					cell.setVerticalAlignment(PdfPCell.ALIGN_BOTTOM);
					cell.setPaddingTop(4f);
				    contentOverview.addCell(cell);				    
					contentOverview.addCell(PrintUtil.createCellWithColRowspanVerticalCenter(1, 1, opmerking.getOmschrijving(), PrintUtil.calibri_10_italic));					
				}
			}
		}
				
		// empty cell
		contentOverview.addCell(PrintUtil.createCellWithColspan(2, " "));	
		
		// For last row
		contentOverview.completeRow();

        overviewcell.addElement(contentOverview);
        result.addCell(overviewcell);
		
		return result;
		
	}
}
