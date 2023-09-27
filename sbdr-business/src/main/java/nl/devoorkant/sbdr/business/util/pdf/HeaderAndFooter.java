package nl.devoorkant.sbdr.business.util.pdf;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Date;

import nl.devoorkant.sbdr.business.service.DocumentServiceImpl;
import nl.devoorkant.sbdr.data.util.EDocumentType;
import nl.devoorkant.util.FormatUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import com.google.common.io.ByteStreams;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Image;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.Barcode128;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

public class HeaderAndFooter extends PdfPageEventHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(HeaderAndFooter.class);
    
	
	/** Path to the resources. */
	
    public static final String IMAGE = "/%s.jpg";
	private Resource logoRes = null;   
	private boolean pagenumber = false;
	//private PdfPTable headerRightPart = null;
	private Date datumaangemaakt = null;
	private String documentReferentie = null;
	private String bedrijfaanvragerReferentie = null;
	private String gebruikersnaam = null;
	private boolean displayContact = false;
	
	private float rightVerticalBarLength = 0f;
	
	// total nr of pages template
	private PdfTemplate total = null;
	// total nr of pages reference template
	private PdfTemplate referenceTotal = null;
	// reference blue bar template
	private PdfTemplate referenceBar = null;
	// right vertical blue bar
	private PdfTemplate rightVerticalBar = null;
	
	boolean isFactuur = false;
	boolean isBrief = false;
	EDocumentType doctype = null;
	
	private String extraFooterTekst = null;
    
    public HeaderAndFooter(Resource logoRes, boolean pagenumber, Date datumaangemaakt, EDocumentType doctype, String documentReferentie, String bedrijfaanvragerReferentie, String extraFooterTekst)
    {
    	this.logoRes = logoRes;
    	this.pagenumber = pagenumber;
    	this.datumaangemaakt = datumaangemaakt;
    	this.bedrijfaanvragerReferentie = bedrijfaanvragerReferentie;
    	this.documentReferentie = documentReferentie;
    	
    	this.doctype = doctype;
    	if (doctype.equals(EDocumentType.FACTUUR))
    		isFactuur = true;
    	else if (doctype.equals(EDocumentType.MELDINGBRIEF) || doctype.equals(EDocumentType.AANMELDBRIEF))
    		isBrief = true;
    	
    	this.extraFooterTekst = extraFooterTekst;
    }
    
    public HeaderAndFooter(Resource logoRes, boolean pagenumber, Date datumaangemaakt, EDocumentType doctype, String reportReferentie, String bedrijfaanvragerReferentie, String gebruikersnaam, String extraFooterTekst, boolean displayContact) //PdfPTable headerRightPart)
    {
    	this.logoRes = logoRes;
    	this.pagenumber = pagenumber;
    	//this.headerRightPart = headerRightPart;
    	this.datumaangemaakt = datumaangemaakt;
    	this.documentReferentie = reportReferentie;
    	this.bedrijfaanvragerReferentie = bedrijfaanvragerReferentie;
    	this.gebruikersnaam = gebruikersnaam;
    	this.displayContact = displayContact;
    	
    	this.doctype = doctype;
    	if (doctype.equals(EDocumentType.FACTUUR))
    		isFactuur = true;    	
    	else if (doctype.equals(EDocumentType.MELDINGBRIEF) || doctype.equals(EDocumentType.AANMELDBRIEF))
    		isBrief = true;

    	this.extraFooterTekst = extraFooterTekst;    
    }
    
    public void onOpenDocument(PdfWriter writer, Document document) {
    	rightVerticalBarLength = 10f;
    	
    	total = writer.getDirectContent().createTemplate(30,11);
    	referenceBar =  writer.getDirectContent().createTemplate(2,10);
    	referenceTotal = writer.getDirectContent().createTemplate(26,8);
    	rightVerticalBar = writer.getDirectContent().createTemplate(rightVerticalBarLength,  document.top() - document.bottom() + document.bottomMargin() + document.topMargin());
    	
		// create bar template
		Rectangle rect = new Rectangle(0, 0, 1, 20);
		rect.setBorderColor(new Color(DocumentServiceImpl.COLOR_SBDR_BLUE));
        rect.setBorder(Rectangle.BOX);
        rect.setBorderWidth(1);
        referenceBar.rectangle(rect);
        
        // create right vertical bar template
        
		rect = new Rectangle(0, 0, rightVerticalBarLength, document.top() - document.bottom() + document.bottomMargin() + document.topMargin());
		rect.setBorderColor(new Color(DocumentServiceImpl.COLOR_SBDR_BLUE));
        rect.setBorder(Rectangle.BOX);
        rect.setBackgroundColor(new Color(DocumentServiceImpl.COLOR_SBDR_BLUE));
        rect.setBorderWidth(1);
        rightVerticalBar.rectangle(rect);
    }
    
    public void onEndPage (PdfWriter writer, Document document) {
		Image logo = null;
		Color bgColor = new Color(DocumentServiceImpl.COLOR_SBDR_DARKBLUE);
		
		// If MeldingBrief && on page 2 then skip header/footer parts. Because page 2 is on back of paper
		if (writer.getPageNumber() > 1 && doctype.equals(EDocumentType.MELDINGBRIEF))
			return;
		
		try {

			 if (logoRes != null && logoRes.exists()) {
		            final InputStream inputStream = logoRes.getInputStream();
		            
		            logo = Image.getInstance(ByteStreams.toByteArray(inputStream)); // utility to get byte array of contents
		            
		            logo.setDpi(300, 300);
		     }	
			 
			//logo = Image.getInstance(String.format(realPath + IMAGE, "logo-small_bkrconnect"));			
		} catch (BadElementException e) {
			LOGGER.error("BadElement", e);
		} catch (MalformedURLException e) {
			LOGGER.error("Wrong URL", e);
		} catch (IOException e) {
			LOGGER.error("IOException", e);
		}		
        
    	float totalwidth = document.right() - document.left() + document.leftMargin() + document.rightMargin(); // - document.leftMargin() - document.rightMargin();
		
//    	// overall master table
//		PdfPTable masterheader = new PdfPTable(1);
//		masterheader.setTotalWidth(totalwidth);
//		masterheader.setWidthPercentage(100f);
//		masterheader.setSpacingBefore(0f);
//		masterheader.setSpacingAfter(0f);
//		masterheader.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);		
//    	
//    	// overall master cell
//		PdfPCell mastercell = new PdfPCell();	
//		mastercell.setColspan(1);
//		mastercell.setBorder(Rectangle.BOX);
//		mastercell.setBorderColor(Color.ORANGE);
//		mastercell.setPadding(0f);
//		mastercell.setPaddingBottom(20f);
//		mastercell.setBackgroundColor(Color.WHITE);	
//        mastercell.setHorizontalAlignment(Element.ALIGN_LEFT);
        
		PdfPTable headerContent = new PdfPTable(1);
		headerContent.setTotalWidth(totalwidth-rightVerticalBarLength-1);
		//headerContent.setWidthPercentage(100f);
		headerContent.setSpacingBefore(0f);
		headerContent.setSpacingAfter(10f);
		headerContent.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);		
		
		// header master cell
		PdfPCell headercell = new PdfPCell();	
		headercell.setColspan(1);
		headercell.setBorder(PdfPCell.NO_BORDER);	
		//headercell.setBorder(Rectangle.BOX);
		headercell.setPadding(0f);
		//headercell.setBackgroundColor(bgColor);	// MBR 02-06-2015
		headercell.setBackgroundColor(Color.WHITE);	
        headercell.setHorizontalAlignment(Element.ALIGN_LEFT);

		float margintop = document.topMargin(); //30f;
		float marginbottom = 15f; //5f;
		
		//create page header with logo
        float logocellwidth = 90 + document.leftMargin();
        float headingcellwidth = totalwidth - rightVerticalBarLength - document.rightMargin();
 
        float offsetTop = 62f; //62f; // For Report + MonitorDetail report
        if (isFactuur || isBrief) // For Factur + Brief
        	offsetTop = 22f;
        
		 PdfPTable header = null;
		 //if (headerRightPart == null) {
		//	 header = new PdfPTable(1);
		 //    header.setTotalWidth(logocellwidth);
		 //}
		 //else {
			 header = new PdfPTable(2);			 
		     try {
		    	float times = headingcellwidth / logocellwidth;
				header.setWidths(new float[]{1, times-1});
				header.setTotalWidth(headingcellwidth);	
				header.setWidthPercentage(100f);
			} catch (DocumentException e) {
				// TODO: Exception handling
			}
		 //}
	     
		 header.setHorizontalAlignment(Element.ALIGN_LEFT);
	     header.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
	     header.getDefaultCell().setVerticalAlignment(Element.ALIGN_TOP); //setVerticalAlignment(Element.ALIGN_CENTER);
	     
	     PdfPCell emptyCell = PrintUtil.createCellWithColRowspanLeftColor(1,  1,  "", PrintUtil.calibri_8, Color.WHITE);//PrintUtil.createCell("a");
		 emptyCell.setPadding(0f);
		 emptyCell.setLeading(0f,  1f);		    
		 emptyCell.setBorder(PdfPCell.NO_BORDER);	     
	     
	     PdfPCell emptyCellLogo = new PdfPCell();
	     emptyCellLogo.setPaddingTop(document.topMargin()-margintop); // MBr added
	     emptyCellLogo.setBorder(PdfPCell.NO_BORDER);
	     emptyCellLogo.setPaddingLeft(document.leftMargin());	     
	     emptyCellLogo.setPaddingBottom(65f); //90f  62f logo offset up 70f

		 // we wrap the image in a PdfPCell
	     PdfPCell logocell = new PdfPCell();
	     logocell.setPaddingTop(document.topMargin()-margintop); // MBr added
	     logocell.setBorder(PdfPCell.NO_BORDER);
		 logocell.setPaddingLeft(document.leftMargin());
		 //if (headerRightPart == null)
			// logocell.setPaddingBottom(45f);
		 logocell.addElement(logo);
	     logocell.setPaddingBottom(offsetTop); // 62f logo offset up 70f
	     
		 if (!doctype.equals(EDocumentType.MELDINGBRIEF) && !doctype.equals(EDocumentType.AANMELDBRIEF))
			 header.addCell(logocell);
		 else
			 header.addCell(emptyCellLogo);
	     	     
		 // barcode
	     PdfPTable barcodeTable = new PdfPTable(3);
	     try {
			barcodeTable.setWidths(new float[]{15, 16, 5});										
			
			//document.add(new Paragraph("Barcode 128"));
	        Barcode128 code128 = new Barcode128();
	        code128.setCode(bedrijfaanvragerReferentie + "/" + documentReferentie);
	        code128.setStartStopText(false); 
	        code128.setFont(null);
	        PdfContentByte cb = writer.getDirectContent();
	        Image barcodeImg = code128.createImageWithBarcode(cb, null, null);
	        //code128.setCode("0123456789\uffffMy Raw Barcode (0 - 9)");
	        //code128.setCodeType(Barcode.CODE128_RAW);
	        //document.add(code128.createImageWithBarcode(cb, null, null));			
			
			PdfPCell labelCell = PrintUtil.createCellWithColRowspanLeftColor(1,  1,  barcodeImg, Color.WHITE);
			labelCell.setLeading(0f,  1f);
			labelCell.setPaddingBottom(10f);
		    labelCell.setBorder(PdfPCell.NO_BORDER);
		    
		    barcodeTable.addCell(emptyCell);
		    barcodeTable.addCell(labelCell);
		    barcodeTable.addCell(emptyCell);
		      
		} catch (DocumentException e3) {
			// TODO Auto-generated catch block
			//e3.printStackTrace();
		}
	     
	     // sbdrurl
	     PdfPTable sbdrurlTable = new PdfPTable(3);
	     try {
	    	 sbdrurlTable.setWidths(new float[]{7, 6, 1});
	    	 
			 PdfPCell labelCell = PrintUtil.createCellWithColRowspanRightColor(1,  1,  "CRZB", PrintUtil.calibri_10_italic_sbdrdark, Color.WHITE);
			 //labelCell.setLeading(0f,  1f);
			 labelCell.setPadding(10f);
			 labelCell.setBorder(PdfPCell.NO_BORDER);
			 
			 sbdrurlTable.addCell(emptyCell);
			 sbdrurlTable.addCell(labelCell);
			 sbdrurlTable.addCell(emptyCell);			 
	     } catch (DocumentException e3) {
				// TODO Auto-generated catch block
				//e3.printStackTrace();
		 }
	     
	     // empty table
	     PdfPTable emptyTable = new PdfPTable(3);
		 PdfPCell emptylabelCell = PrintUtil.createCellWithColRowspanRightColor(1,  1,  " ", PrintUtil.calibri_10_italic_sbdrdark, Color.WHITE);
		 emptylabelCell.setPadding(10f);
		 emptylabelCell.setBorder(PdfPCell.NO_BORDER);
	     emptyTable.addCell(emptyCell);
	     emptyTable.addCell(emptylabelCell);
	     emptyTable.addCell(emptyCell);			 
	          
	     // references
	     PdfPCell references = new PdfPCell();
	     references.setPadding(0f);
	     PdfPTable refTable = new PdfPTable(4);
	     references.setBorder(PdfPCell.NO_BORDER);
	     try {
			refTable.setWidths(new float[]{18, 7, 1, 10});
			refTable.setWidthPercentage(100f);
			refTable.getDefaultCell().setPadding(2);
			refTable.getDefaultCell().setUseAscender(false);
			refTable.getDefaultCell().setUseDescender(false);

			//PdfPCell emptyCell = PrintUtil.createCellWithColRowspanLeftColor(1,  1,  "", PrintUtil.calibri_8, Color.WHITE);//PrintUtil.createCell("a");
			emptyCell.setPadding(0f);
			emptyCell.setLeading(0f,  1f);		    
			emptyCell.setBorder(PdfPCell.NO_BORDER);
			
			PdfPCell refBar = PrintUtil.createCellWithColRowspanRightColor(1,  1,  Image.getInstance(referenceBar), Color.WHITE);
			refBar.setPadding(0f);
			refBar.setLeading(0f,  1f);
			refBar.setBorder(PdfPCell.NO_BORDER);
			
			PdfPCell labelCell = null;
			PdfPCell valueCell = null;
				
			if (!isFactuur && !isBrief) {	
				refTable.addCell(emptyCell);
				labelCell = PrintUtil.createCellWithColRowspanLeftColor(1,  1,  "Datum", PrintUtil.calibri_8, Color.WHITE);
				labelCell.setLeading(0f,  1f);
				labelCell.setPadding(0f);
			    labelCell.setBorder(PdfPCell.NO_BORDER);
	
			    refTable.addCell(labelCell);
			    refTable.addCell(refBar);
			    String datum = FormatUtil.formatDate(datumaangemaakt);
			    valueCell = PrintUtil.createCellWithColRowspanLeftColor(1,  1,  datum, PrintUtil.calibri_8, Color.WHITE);
			    valueCell.setPadding(0f);
			    valueCell.setPaddingLeft(5f);
			    valueCell.setLeading(0f,  1f);
			    valueCell.setBorder(PdfPCell.NO_BORDER);
			    refTable.addCell(valueCell);
			    
				refTable.addCell(emptyCell);
				labelCell = PrintUtil.createCellWithColRowspanLeftColor(1,  1,  "Tijd", PrintUtil.calibri_8, Color.WHITE);
			    labelCell.setBorder(PdfPCell.NO_BORDER);
			    refTable.addCell(labelCell);
			    
			    refTable.addCell(refBar);
				String tijd = FormatUtil.formatTime(datumaangemaakt);
			    valueCell = PrintUtil.createCellWithColRowspanLeftColor(1,  1,  tijd, PrintUtil.calibri_8, Color.WHITE);
			    valueCell.setPaddingLeft(5f);
			    valueCell.setBorder(PdfPCell.NO_BORDER);
			    refTable.addCell(valueCell);
			} else {
				refTable.addCell(emptyCell);
				if (isFactuur)
					labelCell = PrintUtil.createCellWithColRowspanLeftColor(1,  1,  "Factuurnummer", PrintUtil.calibri_8, Color.WHITE);
				else if (isBrief)
					labelCell = PrintUtil.createCellWithColRowspanLeftColor(1,  1,  "Referentienummer", PrintUtil.calibri_8, Color.WHITE);
			    labelCell.setBorder(PdfPCell.NO_BORDER);
			    refTable.addCell(labelCell);
			    refTable.addCell(refBar);
			    valueCell = PrintUtil.createCellWithColRowspanLeftColor(1,  1,  documentReferentie, PrintUtil.calibri_8, Color.WHITE);
			    valueCell.setPaddingLeft(5f);
			    valueCell.setBorder(PdfPCell.NO_BORDER);
			    refTable.addCell(valueCell);
			    
			    refTable.addCell(emptyCell);
				if (isFactuur)
					labelCell = PrintUtil.createCellWithColRowspanLeftColor(1,  1,  "Factuurdatum", PrintUtil.calibri_8, Color.WHITE);
				else if (isBrief)
					labelCell = PrintUtil.createCellWithColRowspanLeftColor(1,  1,  "Datum", PrintUtil.calibri_8, Color.WHITE);
				labelCell.setLeading(0f,  1f);
				labelCell.setPadding(0f);
			    labelCell.setBorder(PdfPCell.NO_BORDER);
	
			    refTable.addCell(labelCell);
			    refTable.addCell(refBar);
			    String datum = FormatUtil.formatDate(datumaangemaakt);
			    valueCell = PrintUtil.createCellWithColRowspanLeftColor(1,  1,  datum, PrintUtil.calibri_8, Color.WHITE);
			    valueCell.setPadding(0f);
			    valueCell.setPaddingLeft(5f);
			    valueCell.setLeading(0f,  1f);
			    valueCell.setBorder(PdfPCell.NO_BORDER);
			    refTable.addCell(valueCell);			    
			}
						    
			refTable.addCell(PrintUtil.createCell(" "));
		    refTable.addCell(PrintUtil.createCell(" "));
		    refTable.addCell(PrintUtil.createCell(" "));
		    refTable.addCell(PrintUtil.createCell(" "));	
		    
		    if (!isFactuur && !isBrief) {
				refTable.addCell(emptyCell);
				labelCell = PrintUtil.createCellWithColRowspanLeftColor(1,  1,  "Referentie", PrintUtil.calibri_8, Color.WHITE);
			    labelCell.setBorder(PdfPCell.NO_BORDER);
			    refTable.addCell(labelCell);
			    refTable.addCell(refBar);
			    valueCell = PrintUtil.createCellWithColRowspanLeftColor(1,  1,  documentReferentie, PrintUtil.calibri_8, Color.WHITE);
			    valueCell.setPaddingLeft(5f);
			    valueCell.setBorder(PdfPCell.NO_BORDER);
			    refTable.addCell(valueCell);
			    
			    if (gebruikersnaam != null) {
					refTable.addCell(emptyCell);
					labelCell = PrintUtil.createCellWithColRowspanLeftColor(1,  1,  "Opgevraagd door", PrintUtil.calibri_8, Color.WHITE);
				    labelCell.setBorder(PdfPCell.NO_BORDER);
				    refTable.addCell(labelCell);
				    refTable.addCell(refBar);
				    valueCell = PrintUtil.createCellWithColRowspanLeftColor(1,  1,  gebruikersnaam, PrintUtil.calibri_8, Color.WHITE);
				    valueCell.setPaddingLeft(5f);
				    valueCell.setBorder(PdfPCell.NO_BORDER);
				    refTable.addCell(valueCell);
			    } else {
					refTable.addCell(emptyCell);
					labelCell = PrintUtil.createCellWithColRowspanLeftColor(1,  1,  "Van", PrintUtil.calibri_8, Color.WHITE);
				    labelCell.setBorder(PdfPCell.NO_BORDER);
				    refTable.addCell(labelCell);
				    refTable.addCell(refBar);
				    valueCell = PrintUtil.createCellWithColRowspanLeftColor(1,  1,  "CRZB", PrintUtil.calibri_8, Color.WHITE);
				    valueCell.setPaddingLeft(5f);
				    valueCell.setBorder(PdfPCell.NO_BORDER);
				    refTable.addCell(valueCell);		    	
			    }
			    
			    refTable.addCell(PrintUtil.createCell(" "));
			    refTable.addCell(PrintUtil.createCell(" "));
			    refTable.addCell(PrintUtil.createCell(" "));
			    refTable.addCell(PrintUtil.createCell(" "));			    
		    }		    				
		    
			refTable.addCell(emptyCell);
			labelCell = PrintUtil.createCellWithColRowspanLeftColor(1,  1,  "BA-nummer", PrintUtil.calibri_8, Color.WHITE);
		    labelCell.setBorder(PdfPCell.NO_BORDER);
		    refTable.addCell(labelCell);
		    refTable.addCell(refBar);
		    valueCell = PrintUtil.createCellWithColRowspanLeftColor(1,  1,  bedrijfaanvragerReferentie, PrintUtil.calibri_8, Color.WHITE);
		    valueCell.setPaddingLeft(5f);
		    valueCell.setBorder(PdfPCell.NO_BORDER);
		    refTable.addCell(valueCell);
		    
			refTable.addCell(emptyCell);
			labelCell = PrintUtil.createCellWithColRowspanLeftColor(1,  1,  "Pagina", PrintUtil.calibri_8, Color.WHITE);
		    labelCell.setBorder(PdfPCell.NO_BORDER);
		    refTable.addCell(labelCell);
		    refTable.addCell(refBar);
		    PdfPCell pagenumber = PrintUtil.createCellWithColRowspanLeftColor(1, 1, "" + writer.getPageNumber(), PrintUtil.calibri_8, Color.WHITE);
		    pagenumber.setBorder(PdfPCell.NO_BORDER);
		    pagenumber.setPaddingLeft(5f);
		    Image refTotalImg = Image.getInstance(referenceTotal);
		    valueCell = PrintUtil.createCellWithColRowspanLeftColor(1, 1, refTotalImg, Color.WHITE); // Image.getInstance(referenceTotal)
		    valueCell.setBorder(PdfPCell.NO_BORDER);		    
		    // wrapper table
		    PdfPTable refpage = new PdfPTable(3);	
		    refpage.setWidths(new float[]{1, 5, 5});
		    refpage.setWidthPercentage(100f);
		    refpage.setSpacingAfter(0f);
		    refpage.setSpacingBefore(0f);
		    refpage.getDefaultCell().setPadding(0f);
		    refpage.getDefaultCell().setLeading(0f, 0f);
		    refpage.addCell(pagenumber);
		    refpage.addCell(valueCell);	
		    refpage.addCell(emptyCell);
		    PdfPCell refpagecell = new PdfPCell(refpage);
		    refpagecell.setBorder(PdfPCell.NO_BORDER);
		    refpagecell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
		    refpagecell.setPadding(0f);
		    refpagecell.setLeading(0f,  1f);
		    refTable.addCell(refpagecell);		    
	     
	     } catch (DocumentException e2) {
			// TODO Auto-generated catch block			
		 }
	     
	     if (!doctype.equals(EDocumentType.MELDINGBRIEF) && !doctype.equals(EDocumentType.AANMELDBRIEF)) {
		     if (isFactuur || isBrief)
		    	 // references.addElement(sbdrurlTable);
		    	 references.addElement(emptyTable);
		     else 
		    	 references.addElement(barcodeTable);	    	 
	     }
	     references.addElement(refTable);  
	     
	     
	     references.setBorder(PdfPCell.NO_BORDER);
	     header.addCell(references);
	     
//	     if (headerRightPart != null) {
//
//	    	 PdfPCell headerRightPartCell = PrintUtil.createCellWithColspan(2, headerRightPart);
//	    	 headerRightPartCell.setBackgroundColor(bgColor);
//	    	 headerRightPartCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//	    	 headerRightPartCell.setBorder(PdfPCell.NO_BORDER);
//	    	 headerRightPartCell.setBorder(Rectangle.BOX);
//	    	 headerRightPartCell.setBorderColor(Color.RED);
//	    	 headerRightPartCell.setPaddingLeft(document.leftMargin());
//	    	 headerRightPartCell.setPaddingRight(document.rightMargin());
//	    	 
//	    	 //header.addCell(headerRightPartCell);	    	 
//	     }
	     
	     // For last row
		 header.completeRow();
	     
	     //float x = document.leftMargin();
	     float x = 0;
	     //float hei = document.topMargin();
	     float hei = 0;
	     //align bottom between page edge and page margin
	     float y = document.getPageSize().getHeight() - document.topMargin()+30f + header.getTotalHeight(); // - 75;

	     //write the table
	     //header.writeSelectedRows(0, -1, x, y, writer.getDirectContent());

	     headercell.addElement(header);
	     headerContent.addCell(headercell);
	     headerContent.completeRow();
	     
	     //mastercell.addElement(headerContent);
	     //masterheader.addCell(mastercell);
	     try {
	    	 headerContent.writeSelectedRows(0, -1, x, y, writer.getDirectContent()); // headerContent
	     } catch (Exception e) {
	    	 LOGGER.error("Error writing report: " + e.getMessage());
	     }
	     
		
	     if (pagenumber) {
		     // create page footer with page number

	 		PdfPTable footerContent = new PdfPTable(1);
			footerContent.setTotalWidth(totalwidth-rightVerticalBarLength-1);
			footerContent.setWidthPercentage(100f);
			footerContent.setSpacingBefore(0f);
			footerContent.setSpacingAfter(10f);
			footerContent.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);		
	    	 
	        float footercellwidth = totalwidth-rightVerticalBarLength-1; // - document.rightMargin();
		
			// footer master cell
			PdfPCell footercell = new PdfPCell();	
			footercell.setColspan(1);
			footercell.setBorder(PdfPCell.NO_BORDER);
			footercell.setPadding(0f);
			footercell.setPaddingTop(marginbottom);
			footercell.setVerticalAlignment(PdfPCell.ALIGN_RIGHT);
		     //footercell.setBorder(Rectangle.BOX);
		     //footercell.setBorderColor(Color.BLUE);

			//footercell.setBackgroundColor(bgColor);	
	        //footercell.setHorizontalAlignment(Element.ALIGN_CENTER);
	     
			// Contact
	    	 PdfPTable contact = new PdfPTable(2);	    	 
	    	 contact.setTotalWidth(footercellwidth);
	    	 contact.setTotalWidth(100f);
		     contact.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
		     contact.getDefaultCell().setVerticalAlignment(Element.ALIGN_TOP);		     			
					     
		     PdfPCell email = PrintUtil.createCellWithColRowspanLeftColor(1, 1, "E-mail: info@crzb.nl", PrintUtil.calibri_10_italic_sbdr, Color.WHITE);
		     email.setPaddingTop(10f); // spacing between content en footer
		     email.setPaddingLeft(document.leftMargin());
		     email.setBorder(PdfPCell.NO_BORDER);
		     PdfPCell phone = PrintUtil.createCellWithColRowspanLeftColor(1, 1, "Telefoon: 085-4845700", PrintUtil.calibri_10_italic_sbdr, Color.WHITE);
		     phone.setPaddingLeft(document.leftMargin());
		     phone.setBorder(PdfPCell.NO_BORDER);
		     
		     PdfPCell extraFooter = null;
		     if (extraFooterTekst != null) {
		    	 extraFooter = PrintUtil.createCellWithColRowspanLeftColor(1, 1, extraFooterTekst, PrintUtil.calibri_10_italic_sbdr, Color.WHITE);
		    	 extraFooter.setPaddingLeft(document.leftMargin());
			     extraFooter.setBorder(PdfPCell.NO_BORDER);		    	 
		     }		          
		     
		     contact.addCell(email);
		     contact.addCell(PrintUtil.createCell(""));
		     contact.addCell(phone);
		     contact.addCell(PrintUtil.createCell(""));
		     
		   // Ref, copyright, page		     			
	    	 PdfPTable footerData = new PdfPTable(3);	
	    	 try {
				footerData.setWidths(new float[]{1, 4, 1});
			} catch (DocumentException e2) {
				// TODO Auto-generated catch block
				
			}	    	 
	    	 footerData.setTotalWidth(footercellwidth);
	    	 footerData.setTotalWidth(100f);
		     footerData.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
		     footerData.getDefaultCell().setVerticalAlignment(Element.ALIGN_TOP);		     			
	        	
		     PdfPTable footer = new PdfPTable(2);	
		     try {
				footer.setWidths(new float[]{4,3}); // 1,3
			} catch (DocumentException e2) {
				// TODO Auto-generated catch block				
			}
		     footer.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
		     footer.getDefaultCell().setVerticalAlignment(Element.ALIGN_TOP);		     			
	        		     
		     // we wrap the page number in a PdfPCell
		     PdfPCell pagenumber = PrintUtil.createCellWithColRowspanRightColor(1, 1, "Pagina " + writer.getPageNumber(), PrintUtil.calibri_8_italic, Color.WHITE);
		     //pagenumber.setPaddingTop(marginbottom);
		       //pagenumber.setBorder(Rectangle.BOX);
		       //pagenumber.setBorderColor(Color.RED);
		     pagenumber.setBorder(PdfPCell.NO_BORDER);
		     PdfPCell totalpgnumber = null;
		     try {
				totalpgnumber = PrintUtil.createCellWithColRowspanLeftColor(1, 1, Image.getInstance(total), Color.WHITE); //PrintUtil.calibri_10_white, 
				//totalpgnumber.setPaddingTop(marginbottom);
				totalpgnumber.setPaddingLeft(2f);
				float fontheight = 14f;//PrintUtil.calibri_8_italic.getSize();
				//totalpgnumber.setPaddingBottom(document.bottomMargin()-marginbottom-fontheight); 	
				//totalpgnumber.setPaddingTop(marginbottom); 	
				  //totalpgnumber.setBorder(Rectangle.BOX);
				  //totalpgnumber.setBorderColor(Color.PINK);
				totalpgnumber.setBorder(PdfPCell.NO_BORDER);									
			 } catch (BadElementException e1) {
				LOGGER.error("" + e1.getMessage());
				//throw new BadElementException(e1);
			 }
		     //pagenumber.setPadding(0f);
		     //pagenumber.setPaddingTop(document.bottomMargin()-marginbottom);
		     
		     //
		     //pagenumber.setPaddingBottom(marginbottom+1);
		     
		     //pagenumber.setPaddingRight(document.rightMargin()); 	
		     
		     //
		     //pagenumber.setPaddingLeft(document.leftMargin());
		     //pagenumber.setPaddingBottom(document.bottomMargin()-marginbottom); 		     
		     
		     //pagenumber.setBackgroundColor(bgColor);
		     //pagenumber.setBorder(PdfPCell.NO_BORDER);	
		     //pagenumber.setHorizontalAlignment(Element.ALIGN_CENTER);
		     //pagenumber.setBorder(Rectangle.BOX);
		     //pagenumber.setBorderColor(Color.RED);
		     footer.addCell(pagenumber);
		     if (totalpgnumber != null)
		    	 footer.addCell(totalpgnumber);
		     else
		    	 footer.addCell(PrintUtil.createCellWithColspan(1, " ")); // dummy
		     
		     footer.completeRow();
		     footercell.addElement(footer);
		     //footercell.setBorder(PdfPCell.RECTANGLE);
		     	
		     PdfPCell reference = PrintUtil.createCellWithColRowspanLeftColor(1, 1, documentReferentie, PrintUtil.calibri_8_italic, Color.WHITE);
		     reference.setPaddingTop(marginbottom);
		     //reference.setPaddingLeft(document.leftMargin());
		     reference.setBorder(PdfPCell.NO_BORDER);
		     footerData.addCell(reference);
		     PdfPCell claim = PrintUtil.createCellWithColRowspanCenterColor(1, 1, "Dit document is vertrouwelijk en uitsluitend voor interne doeleinden verstrekt", PrintUtil.calibri_8_italic, Color.WHITE);
		     claim.setPaddingTop(marginbottom);
		     claim.setBorder(PdfPCell.NO_BORDER);
		     footerData.addCell(claim);
		     footerData.addCell(footercell);
		     
		     // dummy regel
		     footerData.addCell(PrintUtil.createCellWithColRowspan(1,  1,  " ",  PrintUtil.calibri_8_italic));
		     // copyright
		     PdfPCell copyright = PrintUtil.createCellWithColRowspanCenterColor(1, 1, "\u00A9 CRZB", PrintUtil.calibri_8_italic, Color.WHITE);
		     //copyright.setPaddingBottom(document.bottomMargin()-marginbottom);
		     copyright.setPaddingBottom(10f);
		     copyright.setBorder(PdfPCell.NO_BORDER);
		     footerData.addCell(copyright);		     
		     // dummy regel
		     footerData.addCell(PrintUtil.createCellWithColRowspan(1,  1,  " ",  PrintUtil.calibri_8_italic));

		     PdfPCell footerDataCell = new PdfPCell();
		     footerDataCell.setPadding(0f);
		     footerDataCell.setBorder(PdfPCell.NO_BORDER);
		     //footerDataCell.setBorder(Rectangle.BOX);
		     //footerDataCell.setBorderColor(Color.GREEN);

		     footerDataCell.addElement(footerData);
		     if (displayContact && writer.getPageNumber() == 1) {
			     footerContent.addCell(email);
			     footerContent.addCell(phone);
		     }
		     if (extraFooter != null) {
		    	 footerContent.addCell(extraFooter);
		     }
		     footerContent.addCell(footerDataCell);
		     footerContent.completeRow();

		     x =  0; //document.leftMargin() + (PageSize.A4.getWidth() / 2) - (footer.getTotalWidth()/2); //document.leftMargin()
		     hei = footerContent.getTotalHeight(); 
		     //align bottom between page edge and page margin
		     y =  Math.max(hei, document.bottomMargin());
		    
		     //write the table
		     try {
		    	 footerContent.writeSelectedRows(0, -1, x, y, writer.getDirectContent());	// footcontent
		     } catch (Exception e) {
		    	 LOGGER.error("Error writing report: " + e.getMessage());
		     }
		     		     		     
	     } else if (!doctype.equals(EDocumentType.MELDINGBRIEF) && !doctype.equals(EDocumentType.AANMELDBRIEF) && (isFactuur || isBrief)) {
		     // create page footer with data

	 		PdfPTable footerContent = new PdfPTable(1);
			footerContent.setTotalWidth(totalwidth-rightVerticalBarLength-1);
			footerContent.setWidthPercentage(100f);
			footerContent.setSpacingBefore(0f);
			footerContent.setSpacingAfter(10f);
			footerContent.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);				
	    	 
	        float footercellwidth = totalwidth-rightVerticalBarLength-1; // - document.rightMargin();			
	     
		     PdfPCell extraFooter = null;
		     if (extraFooterTekst != null) {
		    	 extraFooter = PrintUtil.createCellWithColRowspanLeftColor(1, 1, extraFooterTekst, PrintUtil.calibri_10_italic_sbdr, Color.WHITE);
		    	 extraFooter.setPaddingLeft(document.leftMargin());
			     extraFooter.setBorder(PdfPCell.NO_BORDER);		    	 
		     }		          
		     
		     // address, iban + btw + kvk		     			
	    	 PdfPTable footerData = new PdfPTable(3);	
	    	 try {
				footerData.setWidths(new float[]{1, 7, 1});
	    	 } catch (DocumentException e2) {
				// TODO Auto-generated catch block
				
	    	 }	    	 
	    	 footerData.setTotalWidth(footercellwidth);
	    	 footerData.setTotalWidth(100f);
		     footerData.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
		     footerData.getDefaultCell().setVerticalAlignment(Element.ALIGN_TOP);		     			
		     	
		     // address url
		     footerData.addCell(PrintUtil.createCellWithColRowspan(1,  1,  " ",  PrintUtil.calibri_7)); // dummy
		     PdfPCell claim = PrintUtil.createCellWithColRowspanCenterColor(1, 1, "Centraal Register Zakelijke Betalingsachterstanden (CRZB) | Jadelaan 20, 2132 XW Hoofddorp | Postbus 1323, 2130 EK Hoofddorp", PrintUtil.calibri_7, Color.WHITE);
		     claim.setPaddingTop(marginbottom);
		     claim.setBorder(PdfPCell.NO_BORDER);
		     footerData.addCell(claim);
		     footerData.addCell(PrintUtil.createCellWithColRowspan(1,  1,  " ",  PrintUtil.calibri_7)); // dummy
		     
		     // dummy regel
		     footerData.addCell(PrintUtil.createCellWithColRowspan(1,  1,  " ",  PrintUtil.calibri_7));
		     // iban btw and kvk
		     PdfPCell copyright = PrintUtil.createCellWithColRowspanCenterColor(1, 1, "KvK-nummer 64814432 | BTW-nummer NL855865143.B01 | IBAN NL16INGB0007059004 | BIC INGBNL2A", PrintUtil.calibri_7, Color.WHITE);
		     //copyright.setPaddingBottom(document.bottomMargin()-marginbottom);
		     copyright.setBorder(PdfPCell.NO_BORDER);
		     footerData.addCell(copyright);		     
		     // dummy regel
		     footerData.addCell(PrintUtil.createCellWithColRowspan(1,  1,  " ",  PrintUtil.calibri_7));

		     PdfPCell footerDataCell = new PdfPCell();
		     footerDataCell.setPadding(0f);
		     footerDataCell.setBorder(PdfPCell.NO_BORDER);
		     //footerDataCell.setBorder(Rectangle.BOX);
		     //footerDataCell.setBorderColor(Color.GREEN);

		     if (!doctype.equals(EDocumentType.MELDINGBRIEF) && !doctype.equals(EDocumentType.AANMELDBRIEF))
		    	 footerDataCell.addElement(footerData);

		     if (extraFooter != null) {
		    	 footerContent.addCell(extraFooter);
		     }
		     footerContent.addCell(footerDataCell);
		     footerContent.completeRow();

		     x =  0; //document.leftMargin() + (PageSize.A4.getWidth() / 2) - (footer.getTotalWidth()/2); //document.leftMargin()
		     hei = footerContent.getTotalHeight(); 
		     //align bottom between page edge and page margin
		     y =  document.bottomMargin();
		     
		     //write the table
		     try {
		    	 footerContent.writeSelectedRows(0, -1, x, y, writer.getDirectContent());	// footcontent
		     } catch (Exception e) {
		    	 LOGGER.error("Error writing report: " + e.getMessage());
		     }
		     
	    	 
	     }
	     
//    	Image image;
//		try {
//			// get the direct pdf content
//			//PdfContentByte dc = writer.getDirectContent();						
//
//			// add the rendered pdf template to the direct content
//			// you will have to play around with this because the chart is absolutely positioned.
//			// docWriter.getVerticalPosition(true) will approximate the position that the content above the chart ended
//
//			//dc.addTemplate(rightVerticalBar, totalwidth-rightVerticalBarLength, 0);    		
//			image = Image.getInstance(rightVerticalBar);
//	    	document.add(image);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//		}
		
	    if (!isFactuur && !isBrief) {
	   	 	Image image;
			try {
				image = Image.getInstance(rightVerticalBar);
		    	image.setAbsolutePosition(totalwidth-rightVerticalBarLength, 0);
		    	document.add(image);
			} catch (Exception e) {
				// TODO Auto-generated catch block
			}		
	    }
	}
    
    public void onCloseDocument(PdfWriter writer, Document document) {
    	Phrase totalnrofpages = new Phrase("van " + String.valueOf(writer.getPageNumber() - 1), PrintUtil.calibri_8_italic);
    	Phrase referencetotalnrofpages = new Phrase("van " + String.valueOf(writer.getPageNumber() - 1), PrintUtil.calibri_8);
    	referencetotalnrofpages.setLeading(0f);    	
    	ColumnText.showTextAligned(total, Element.ALIGN_LEFT,
                totalnrofpages,
                2, 2, 0);
    	ColumnText.showTextAligned(referenceTotal, Element.ALIGN_BOTTOM,
                referencetotalnrofpages,
                0, 0, 0);
    	
    }

  
}
