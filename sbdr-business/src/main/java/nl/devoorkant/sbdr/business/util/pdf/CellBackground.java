package nl.devoorkant.sbdr.business.util.pdf;

import java.awt.Color;

import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPCellEvent;
import com.lowagie.text.pdf.PdfPTable;

public class CellBackground implements PdfPCellEvent {

	public Color bgcolor = new Color(0xE5E5E5); //Color.LIGHT_GRAY;
	
	public CellBackground() {
		
	}
	
	public CellBackground(Color bgcolor) {
		this.bgcolor = bgcolor;
	}
	
	
    public void cellLayout(PdfPCell cell, Rectangle rect,
            PdfContentByte[] canvas) {
        PdfContentByte cb = canvas[PdfPTable.BACKGROUNDCANVAS];
        cb.roundRectangle(
            rect.getLeft() + 1.5f, rect.getBottom() + 1.5f, rect.getWidth() - 3,
            rect.getHeight() - 3, 4);
        cb.setColorFill(bgcolor);
        //.setCMYKColorFill(0x00, 0x00, 0xFF, 0x0F);
        cb.fill();
    }
}