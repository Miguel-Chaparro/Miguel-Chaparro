/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package printer;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.UnsupportedEncodingException;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;

import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;

import javax.print.PrintServiceLookup;

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
            /* We have only one page, and 'page' is zero-based */
            return NO_SUCH_PAGE;
        }

        /*
		 * User (0,0) is typically outside the imageable area, so we must
		 * translate by the X and Y values in the PageFormat to avoid clipping
         */
        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());
        /* Now we perform our rendering */

        g.setFont(new Font("Roman", 0, 8));
        g.drawString("", 0, 10);

        return Printable.PAGE_EXISTS;
    }

    public void printString(String printerName, String text) throws PrinterException {

        DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
        PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
          
        PrintService printService[] = PrintServiceLookup.lookupPrintServices(
                flavor, pras);
        PrintService service = findPrintService(printerName, printService);
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(new printBox());
        job.setPrintService(service);
        DocPrintJob job1 = service.createPrintJob();

        try {

            byte[] bytes;

            bytes = text.getBytes("CP437");
            
            Doc doc = new SimpleDoc(bytes, flavor, null);

            job.print();

        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            System.out.print(e);
        }

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
}
