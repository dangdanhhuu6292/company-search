package nl.devoorkant.sbdr.business.util.pdf;

import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPCellEvent;
import com.lowagie.text.pdf.PdfPTable;

public class RoundRectangle implements PdfPCellEvent {
    /** the border color described as CMYK values. */
    protected int[] color;
    /** Constructs the event using a certain color. */
    public RoundRectangle(int[] color) {
        this.color = color;
    }

    public void cellLayout(PdfPCell cell, Rectangle rect,
            PdfContentByte[] canvas) {
        PdfContentByte cb = canvas[PdfPTable.LINECANVAS];
        cb.roundRectangle(
            rect.getLeft() + 1.5f, rect.getBottom() + 1.5f, rect.getWidth() - 3,
            rect.getHeight() - 3, 4);
        cb.setLineWidth(1.5f);
        cb.setCMYKColorStrokeF(color[0], color[1], color[2], color[3]);
        cb.stroke();
    }
}    