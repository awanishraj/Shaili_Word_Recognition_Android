package in.ac.iitm.shaili.Helpers;

import android.os.Environment;
import android.util.Log;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Awanish Raj on 24/06/15.
 */
public class PDFHelper {

    private static final String LOG_TAG = "PDFHelper";

    public static void write(ReportBuilder report) throws IOException, DocumentException {

        File pdfFolder = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Shaili Reports");
        if (!pdfFolder.exists()) {
            pdfFolder.mkdir();
            Log.i(LOG_TAG, "Pdf Directory created");
        }

        //Create time stamp
        Date date = new Date();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date);

        File myFile = new File(pdfFolder + "/" + timeStamp + ".pdf");

        OutputStream output = new FileOutputStream(myFile);

        //Step 1
        Document document = new Document();

        float documentWidth = document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin();
        float documentHeight = document.getPageSize().getHeight() - document.topMargin() - document.bottomMargin();

        PdfWriter.getInstance(document, output);

        document.open();
        /**
         * Setting title of the document
         */
        Paragraph title = new Paragraph("Shaili - Report " + timeStamp, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20));
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(Chunk.NEWLINE);

        /**
         * Setting the original image
         */
        Image img = Image.getInstance(report.fileOrig);
        img.scaleToFit(documentWidth, documentHeight);
        img.setBorder(Image.BOX);
        img.setBorderColor(BaseColor.BLACK);
        img.setBorderWidth(3);
        document.add(img);

        /**
         * Adding headings for otsu and adaptive thresholding
         */
        PdfPTable tbl = new PdfPTable(2);
        tbl.setWidthPercentage(100);
        tbl.setSpacingBefore(10f);
        tbl.setSpacingAfter(0f);
        PdfPCell cell = new PdfPCell(new Phrase("Otsu thresholding", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.disableBorderSide(Rectangle.BOX);
        tbl.addCell(cell);
        cell = new PdfPCell(new Phrase("Adaptive thresholding", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.disableBorderSide(Rectangle.BOX);
        tbl.addCell(cell);
        document.add(tbl);


        /**
         * Adding Otsu image
         */

        tbl = new PdfPTable(2);
        tbl.setWidthPercentage(100);
        tbl.setSpacingBefore(10f);
        tbl.setSpacingAfter(0f);

        img = Image.getInstance(report.fileOtsu);
        img.scaleToFit(documentWidth / 2, documentHeight / 2);
        img.setBorder(Image.BOX);
        img.setBorderColor(BaseColor.BLACK);
        img.setBorderWidth(3);
        cell = new PdfPCell(img);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.disableBorderSide(Rectangle.BOX);
        tbl.addCell(cell);

        img = Image.getInstance(report.fileAdap);
        img.scaleToFit(documentWidth / 2, documentHeight / 2);
        img.setBorder(Image.BOX);
        img.setBorderColor(BaseColor.BLACK);
        img.setBorderWidth(3);
        cell = new PdfPCell(img);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.disableBorderSide(Rectangle.BOX);
        tbl.addCell(cell);


        document.add(tbl);

        BaseFont urName = BaseFont.createFont("assets/hindi.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font urFontName = new Font(urName, 12);

        document.add(new Paragraph("  "));
        document.add(new Paragraph("OCR", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        document.add(new Paragraph(report.ocr, urFontName));
        Log.d(LOG_TAG, report.ocr);
        document.add(new Paragraph("  "));
        document.add(new Paragraph("Translation", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        document.add(new Paragraph(report.translation));

        //Step 5: Close the document
        document.close();

    }


    public static class BlackBorder extends PdfPageEventHelper {
        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte canvas = writer.getDirectContent();
            Rectangle rect = document.getPageSize();
            rect.setBorder(Rectangle.BOX); // left, right, top, bottom border
            rect.setBorderWidth(5); // a width of 5 user units
            rect.setBorderColor(BaseColor.BLACK); // a red border
            rect.setUseVariableBorders(true); // the full width will be visible
            canvas.rectangle(rect);
        }
    }
}
