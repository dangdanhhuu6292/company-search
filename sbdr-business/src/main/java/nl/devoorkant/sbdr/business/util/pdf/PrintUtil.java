package nl.devoorkant.sbdr.business.util.pdf;

import java.awt.Color;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import nl.devoorkant.sbdr.business.service.DocumentServiceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

//import com.itextpdf.text.Font;
//import com.itextpdf.text.FontFactory;

public class PrintUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(PrintUtil.class);
	
	public static Font calibri_6_bold =  FontFactory.getFont("Calibri", 6, Font.BOLD);
	public static Font calibri_7 =  FontFactory.getFont("Calibri", 7, Font.NORMAL);
	public static Font calibri_8 =  FontFactory.getFont("Calibri", 8, Font.NORMAL);
	public static Font calibri_8_white =  FontFactory.getFont("Calibri", 8, Font.NORMAL, Color.WHITE);	
	public static Font calibri_8_bold =  FontFactory.getFont("Calibri", 8, Font.BOLD);
	public static Font calibri_8_bold_white =  FontFactory.getFont("Calibri", 8, Font.BOLD, Color.WHITE);
	public static Font calibri_8_italic =  FontFactory.getFont("Calibri", 8, Font.ITALIC);	
	public static Font calibri_6_italic =  FontFactory.getFont("Calibri", 6, Font.ITALIC);	
	public static Font calibri_7_italic =  FontFactory.getFont("Calibri", 7, Font.ITALIC);	
	public static Font calibri_9 =  FontFactory.getFont("Calibri", 9, Font.NORMAL);	
	public static Font calibri_9_bold =  FontFactory.getFont("Calibri", 9, Font.BOLD);	
	public static Font calibri_9_italic =  FontFactory.getFont("Calibri", 9, Font.ITALIC);	
	public static Font calibri_10 =  FontFactory.getFont("Calibri", 10, Font.NORMAL);	
	public static Font calibri_10_italic =  FontFactory.getFont("Calibri", 10, Font.ITALIC);	
	public static Font calibri_10_italic_sbdr =  FontFactory.getFont("Calibri", 10, Font.ITALIC, new Color(0x3399CC));
	public static Font calibri_10_italic_sbdrdark =  FontFactory.getFont("Calibri", 10, Font.ITALIC, new Color(DocumentServiceImpl.COLOR_SBDR_DARKBLUE));
	public static Font calibri_9_italic_sbdrdark =  FontFactory.getFont("Calibri", 9, Font.ITALIC, new Color(DocumentServiceImpl.COLOR_SBDR_DARKBLUE));
	public static Font calibri_10_bold =  FontFactory.getFont("Calibri", 10, Font.BOLD);	
	public static Font calibri_10_white =  FontFactory.getFont(BaseFont.HELVETICA, 10, Font.NORMAL, Color.WHITE);
	public static Font calibri_10_red =  FontFactory.getFont(BaseFont.HELVETICA, 10, Font.NORMAL, Color.RED);	
	public static Font calibri_11 =  FontFactory.getFont("Calibri", 10, Font.NORMAL);
	public static Font calibri_12_bold =  FontFactory.getFont("Calibri", 12, Font.BOLD);
	public static Font calibri_12_bold_white =  FontFactory.getFont("Calibri", 12, Font.BOLD, Color.WHITE);
	public static Font calibri_14_bold_white =  FontFactory.getFont("Calibri", 14, Font.BOLD, Color.WHITE);
	
	
	public static PdfPTable createMasterTable()
	{		
        PdfPTable table = new PdfPTable(1);
        try {
			table.setWidths(new float[]{1});
        	table.setWidthPercentage(100);
        	table.setSpacingBefore(10);
        	table.setSpacingAfter(5);
        	// table on one page
        	table.setKeepTogether(false);
		} catch (DocumentException e1) {
			LOGGER.error("Cannot set document table parameters", e1);
		}
		
		return table;		
	}
	
	public static PdfPCell createTableHeader(String content)
	{
		Phrase p = new Phrase(content, PrintUtil.calibri_10_white);

		Float fontSize = p.getFont().getSize();
		BaseFont font = PrintUtil.calibri_10_white.getBaseFont();
		Float capHeight = font.getFontDescriptor(BaseFont.CAPHEIGHT, fontSize);

		Float padding = 5f;    

		PdfPCell cell = new PdfPCell(p);
		//cell.setPadding(padding);
		cell.setPaddingTop(capHeight - fontSize + padding);	
		cell.setPaddingBottom(2*(fontSize-capHeight));
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		
		cell.setBorder(PdfPCell.NO_BORDER);
        cell.setBackgroundColor(Color.BLACK);
        //cell.setVerticalAlignment(Element.ALIGN_CENTER);       
        
		return cell;
	}	
	
	public static PdfPCell createCell(String content, int align)
	{
		Phrase phrase = new Phrase(content, PrintUtil.calibri_10);
		
	    PdfPCell cell = new PdfPCell(phrase);
	    //// cell.VerticalAlignment = PdfCell.ALIGN_TOP;
	    ////cell.VerticalAlignment = align;
	    cell.setHorizontalAlignment( align);
	    cell.setBorder(PdfPCell.NO_BORDER);
	    //cell.PaddingBottom = 2f;
	    //cell.PaddingTop = 0f;
	    return cell;
	}	
	public static PdfPCell createCell(String content, int align, Font font)
	{
		Phrase phrase = new Phrase(content, font);
		
	    PdfPCell cell = new PdfPCell(phrase);
	    //// cell.VerticalAlignment = PdfCell.ALIGN_TOP;
	    ////cell.VerticalAlignment = align;
	    cell.setHorizontalAlignment( align);
	    cell.setBorder(PdfPCell.NO_BORDER);
	    //cell.PaddingBottom = 2f;
	    //cell.PaddingTop = 0f;
	    return cell;
	}
	public static PdfPCell createCell(String content, float padding)
	{
		PdfPCell cell = new PdfPCell();
		
        cell.addElement(new Phrase(content, PrintUtil.calibri_10));
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setPaddingBottom(padding);
        
		return cell;
	}	
	
	public static PdfPCell createCell(String content)
	{
		PdfPCell cell = new PdfPCell();
		cell.setPadding(0);
		
        cell.addElement(new Phrase(content, PrintUtil.calibri_10));
        cell.setBorder(PdfPCell.NO_BORDER);
        
		return cell;
	}	

	public static PdfPCell createCell(String content, Font font)
	{
		PdfPCell cell = new PdfPCell();
		cell.setPadding(0);
		
        cell.addElement(new Phrase(content, font));
        cell.setBorder(PdfPCell.NO_BORDER);
        
		return cell;
	}	
	
	public static PdfPCell createCellWithColRowspan(int colspan, int rowspan, String content, Font font)
	{
		PdfPCell cell = new PdfPCell();		
		cell.setColspan(colspan);
		cell.setRowspan(rowspan);
		cell.setPadding(0);
		
		Font nfont = font;
		if (font == null)
			nfont = PrintUtil.calibri_10;
        cell.addElement(new Phrase(content, nfont));
        cell.setBorder(PdfPCell.NO_BORDER);
        
		return cell;
	}

	public static PdfPCell createCellWithColRowspanCenter(int colspan, int rowspan, String content, Font font)
	{
		Font nfont = font;
		if (font == null)
			nfont = PrintUtil.calibri_10;
		Phrase phrase = new Phrase(content, nfont);
				
		PdfPCell cell = new PdfPCell(phrase);
		cell.setPadding(0);		
		cell.setColspan(colspan);
		cell.setRowspan(rowspan);
		
		//Float fontSize = phrase.getFont().getSize();
		//Float capHeight = phrase.getFont().getBaseFont().getFontDescriptor(BaseFont.CAPHEIGHT, fontSize);
		
		//cell.setPaddingBottom(2*(fontSize-capHeight));
	    cell.setVerticalAlignment( cell.ALIGN_MIDDLE);		
	    cell.setHorizontalAlignment( cell.ALIGN_CENTER);
        
		return cell;
	}

	public static PdfPCell createCellWithColRowspanVerticalCenter(int colspan, int rowspan, String content, Font font)
	{
		Font nfont = font;
		if (font == null)
			nfont = PrintUtil.calibri_10;
		Phrase phrase = new Phrase(content, nfont);
				
		PdfPCell cell = new PdfPCell(phrase);
		cell.setPadding(0);		
		cell.setColspan(colspan);
		cell.setRowspan(rowspan);
		cell.setBorder(cell.NO_BORDER);
		
		//Float fontSize = phrase.getFont().getSize();
		//Float capHeight = phrase.getFont().getBaseFont().getFontDescriptor(BaseFont.CAPHEIGHT, fontSize);
		
		//cell.setPaddingBottom(2*(fontSize-capHeight));
	    cell.setVerticalAlignment( cell.ALIGN_MIDDLE);		
        
		return cell;
	}
	
	public static PdfPCell createCellWithColRowspanLeftColor(int colspan, int rowspan, String content, Font font, Color bgcolor)
	{
		Font nfont = font;
		if (font == null)
			nfont = PrintUtil.calibri_10;
		Phrase phrase = new Phrase(content, nfont);
				
		PdfPCell cell = new PdfPCell(phrase);
		cell.setPadding(0);		
		cell.setColspan(colspan);
		cell.setRowspan(rowspan);
		cell.setBackgroundColor(bgcolor);
		
	    cell.setVerticalAlignment( cell.ALIGN_MIDDLE);		
	    cell.setHorizontalAlignment( cell.ALIGN_LEFT);
        
		return cell;
	}

	public static PdfPCell createCellWithColRowspanLeftParagraphColor(int colspan, int rowspan, Paragraph p, Color bgcolor)
	{					
		PdfPCell cell = new PdfPCell(p);
		cell.setPadding(0);		
		cell.setColspan(colspan);
		cell.setRowspan(rowspan);
		cell.setBackgroundColor(bgcolor);
		
	    cell.setVerticalAlignment( cell.ALIGN_MIDDLE);		
	    cell.setHorizontalAlignment( cell.ALIGN_LEFT);
        
		return cell;
	}
	
	public static PdfPCell createCellWithColRowspanRightColor(int colspan, int rowspan, String content, Font font, Color bgcolor)
	{
		Font nfont = font;
		if (font == null)
			nfont = PrintUtil.calibri_10;
		Phrase phrase = new Phrase(content, nfont);
				
		PdfPCell cell = new PdfPCell(phrase);
		cell.setPadding(0);		
		cell.setColspan(colspan);
		cell.setRowspan(rowspan);
		cell.setBackgroundColor(bgcolor);
		
	    cell.setVerticalAlignment( cell.ALIGN_MIDDLE);		
	    cell.setHorizontalAlignment( cell.ALIGN_RIGHT);
        
		return cell;
	}	
	
	public static PdfPCell createCellWithColRowspanCenterColor(int colspan, int rowspan, String content, Font font, Color bgcolor)
	{
		Font nfont = font;
		if (font == null)
			nfont = PrintUtil.calibri_10;
		Phrase phrase = new Phrase(content, nfont);
				
		PdfPCell cell = new PdfPCell(phrase);
		cell.setPadding(0);		
		cell.setColspan(colspan);
		cell.setRowspan(rowspan);
		cell.setBackgroundColor(bgcolor);
		
		//Float fontSize = phrase.getFont().getSize();
		//Float capHeight = phrase.getFont().getBaseFont().getFontDescriptor(BaseFont.CAPHEIGHT, fontSize);
		
		//cell.setPaddingBottom(2*(fontSize-capHeight));
	    cell.setVerticalAlignment( cell.ALIGN_MIDDLE);		
	    cell.setHorizontalAlignment( cell.ALIGN_CENTER);
        
		return cell;
	}
	
	public static PdfPCell createCellWithColRowspanCenterColor(int colspan, int rowspan, Image content, Color bgcolor)
	{		
		PdfPCell cell = new PdfPCell(content);
		cell.setPadding(0);		
		cell.setColspan(colspan);
		cell.setRowspan(rowspan);
		cell.setBackgroundColor(bgcolor);
		
	    cell.setVerticalAlignment( cell.ALIGN_MIDDLE);		
	    cell.setHorizontalAlignment( cell.ALIGN_CENTER);
        
		return cell;
	}	

	public static PdfPCell createCellWithColRowspanLeftColor(int colspan, int rowspan, Image content, Color bgcolor)
	{		
		PdfPCell cell = new PdfPCell(content);
		cell.setPadding(0);		
		cell.setColspan(colspan);
		cell.setRowspan(rowspan);
		cell.setBackgroundColor(bgcolor);
		
	    cell.setVerticalAlignment( cell.ALIGN_MIDDLE);		
	    cell.setHorizontalAlignment( cell.ALIGN_LEFT);
        
		return cell;
	}		

	public static PdfPCell createCellWithColRowspanRightColor(int colspan, int rowspan, Image content, Color bgcolor)
	{		
		PdfPCell cell = new PdfPCell(content);
		cell.setPadding(0);		
		cell.setColspan(colspan);
		cell.setRowspan(rowspan);
		cell.setBackgroundColor(bgcolor);
		
	    cell.setVerticalAlignment( cell.ALIGN_MIDDLE);		
	    cell.setHorizontalAlignment( cell.ALIGN_RIGHT);
        
		return cell;
	}		
	
	public static PdfPCell createCellWithColspan(int colspan, String content)
	{
		PdfPCell cell = new PdfPCell();		
		cell.setColspan(colspan);
		cell.setPadding(0);
		
        cell.addElement(new Phrase(content, PrintUtil.calibri_10));
        cell.setBorder(PdfPCell.NO_BORDER);
        
		return cell;
	}		

	public static PdfPCell createCellWithColspan(int colspan, Element element)
	{
		PdfPCell cell = new PdfPCell();		
		cell.setColspan(colspan);
		cell.setPadding(0);
		
   	 	cell.addElement(element);
   	 	cell.setBorder(PdfPCell.NO_BORDER);
        
		return cell;
	}		
	
	public static PdfPCell createCellWithColspan(int colspan, String content, Font font, boolean nopadding)
	{
		PdfPCell cell = new PdfPCell();		
		cell.setColspan(colspan);
		if (nopadding)
			cell.setPadding(0);
		
        cell.addElement(new Phrase(content, font));
        cell.setBorder(PdfPCell.NO_BORDER);
        
		return cell;
	}	
	
	public static PdfPCell createCellTableWithColspan(int colspan, PdfPTable content)
	{
		PdfPCell cell = new PdfPCell();		
		cell.setColspan(colspan);
		cell.setPadding(0);
		
        cell.addElement(content);
        cell.setBorder(PdfPCell.NO_BORDER);
        
		return cell;
	}	
	
	public static String convertDateToString(Date date)
	{
		String result = null;
		
		DateFormat df = new SimpleDateFormat("dd-MM-yyyy");

		result = df.format(date);

		return  result;					
	}
	
	public static  String formatCurrency(Double poValue) {
        if (poValue != null) {
            NumberFormat loNumberFormat = NumberFormat.getInstance(Locale.ITALY);
            loNumberFormat.setMinimumFractionDigits(2);
            loNumberFormat.setMaximumFractionDigits(2);
            loNumberFormat.setGroupingUsed(true);
            return loNumberFormat.format(poValue);
        } else return "";
    }
	
}
