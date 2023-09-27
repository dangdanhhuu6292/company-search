package nl.devoorkant.sbdr.business.util.pdf;

import java.io.ByteArrayOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfReader;

public class MergePdf
{
	private static final Logger LOGGER = LoggerFactory.getLogger(MergePdf.class);
	
	private ByteArrayOutputStream outStream = new ByteArrayOutputStream();
	private Document document = null;
	private PdfCopy  writer = null;

	public MergePdf()
	{  }

	ByteArrayOutputStream getOutputStream()
	{ return this.outStream; }

	void setOutputStream(ByteArrayOutputStream outStreamInput)
	{ this.outStream = outStreamInput; }

	public byte[] getMergedPdfByteArray()
	{
		if(this.outStream != null )
		{ return this.outStream.toByteArray(); }
		else
		{ return null;}
	}

	public void add(byte[] pdfByteArray)
	{
		try
		{
			PdfReader reader = new PdfReader(pdfByteArray);
			int numberOfPages = reader.getNumberOfPages();

			if(this.document == null)
			{
				setOutputStream(new ByteArrayOutputStream());
				this.document = new Document(); //reader.getPageSizeWithRotation(1));
				this.writer = new PdfCopy( this.document, this.outStream );
				this.document.open();
			}
			for (int i = 1; i <= reader.getNumberOfPages(); i++) {
            	this.writer.addPage(this.writer.getImportedPage(reader, i));
            }			
						
		}
		catch(Exception e)
		{ LOGGER.error("Error in MergePdf.add byte[]", e); }
	}
	
	public void close()
	{
		try
		{
			this.document.close();
			this.writer.close();
			this.outStream.flush();
			this.outStream.close();
			this.document = null;
		}
		catch(Exception e)
		{ LOGGER.error("Error in closing MergePdf handles", e); }
	}

}
