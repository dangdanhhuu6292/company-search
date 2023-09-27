package nl.devoorkant.sbdr.business.util.pdf;

import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPTableEvent;

public class TableBackground implements PdfPTableEvent {

    public void tableLayout(PdfPTable table, float[][] width, float[] height,
            int headerRows, int rowStart, PdfContentByte[] canvas) {
        PdfContentByte background = canvas[PdfPTable.BASECANVAS];
        background.saveState();
        background.setCMYKColorFill(0x00, 0x00, 0xFF, 0x0F);
        background.roundRectangle(
            width[0][0], height[height.length - 1] - 2,
            width[0][1] - width[0][0] + 6, height[0] - height[height.length - 1] - 4, 4);
        background.fill();
        background.restoreState();
    }

}