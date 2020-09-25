/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package printer;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author MIGUEL
 */
public class htmlToPdf {
    
    public boolean convertHtmlToPDF(String html, String destino){
        boolean estado = false;
        ConverterProperties properties = new ConverterProperties();
        try {
            HtmlConverter.convertToPdf(html, new FileOutputStream(destino),properties);
            estado = true;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(htmlToPdf.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return estado;
    }
    
}
