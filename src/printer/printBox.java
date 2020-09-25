/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package printer;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;
import javax.print.PrintServiceLookup;
import javax.print.attribute.standard.PrinterState;

/**
 *
 * @author MIGUEL
 */
public class printBox implements Printable {

    public void printerBox(PrintService service) throws PrintException {
        PrinterJob printerJob = PrinterJob.getPrinterJob();
        Book book = new Book();
        book.append(new printBox(), new PageFormat());
        printerJob.setPageable(book);
        DocPrintJob job = service.createPrintJob();

        job.print((Doc) book, null);
        try {
            printerJob.print();
        } catch (PrinterException exception) {
            System.err.println("Printing error: " + exception);
        }

    }

    @Override
    public int print(Graphics g, PageFormat pf, int page) throws PrinterException {
        if (page > 0) {
            return NO_SUCH_PAGE;
        }

        /*
		 * User (0,0) is typically outside the imageable area, so we must
		 * translate by the X and Y values in the PageFormat to avoid clipping
         */
        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(1, 1);
        System.out.println(pf.getImageableX());
        System.out.println(pf.getImageableY());
        System.out.println("g2d= " + g2d.getClipBounds());
        System.out.println("g = " + g.getClipBounds());
        /* Now we perform our rendering */

        //g.setFont(new Font("Roman", 0, 8));
        g.drawString("", 0, 0);
        return Printable.PAGE_EXISTS;
    }

    public void printString(String printerName, String text) throws PrinterException {

        DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
        PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
        //pras.add(new MediaSize(1, 1, 1));
        PrintService printService[] = PrintServiceLookup.lookupPrintServices(
                flavor, pras);
        PrintService service = findPrintService(printerName, printService);
        System.out.print(service.isAttributeCategorySupported(PrinterState.class));
        /*   PrintServiceAttributeSet attrib = service.getAttributes();
        PrinterState printerState = (PrinterState)  attrib.get(PrinterState.class);
        System.out.print(printerState.getValue());*/
        PrinterJob job = PrinterJob.getPrinterJob();
        PageFormat pf = job.defaultPage();
        Paper paper = new Paper();
        paper.setSize(10, 1);
        paper.setImageableArea(0, 0, 0, 0);
        pf.setPaper(paper);
        job.setPrintable(new printBox(), pf);
        job.setPrintService(service);
        job.print();

    }

    private PrintService findPrintService(String printerName,
            PrintService[] services) {
        for (PrintService service : services) {
            if (service.getName().equalsIgnoreCase(printerName)) {
                return service;
            }
        }

        return null;
    }

    public void printDocument(String printerName, String destFile) throws IOException, PrinterException {
        PDDocument document = new PDDocument();
        document = PDDocument.load(new File(destFile));
        DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
        PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
        //pras.add(new MediaSize(1, 1, 1));
        PrintService printService[] = PrintServiceLookup.lookupPrintServices(
                flavor, pras);
        PrintService service = findPrintService(printerName, printService);
        //System.out.print(service.isAttributeCategorySupported(PrinterState.class));
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPageable(new PDFPageable(document));
        job.setPrintService(service);
        job.print();
        document.close();
    }
}
