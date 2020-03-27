/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nohaservices;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;

/**
 *
 * @author MIGUEL
 */
public class NohaServices implements Runnable {

    static final int PORT = 14285;
    static final String COMC = "COM1";
    private final Socket connect;

    //private final SerialPort sp;
    public NohaServices(Socket con) {
        connect = con;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);

            while (true) {
                NohaServices server = new NohaServices(serverSocket.accept());

                Thread thread = new Thread(server);
                thread.start();

            }
        } catch (IOException e) {
            System.err.println(e);
        } catch (Exception e) {
            System.err.print(e);
        }
    }

    @Override
    public void run() {
        BufferedReader in = null;
        PrintWriter out = null;
        BufferedOutputStream dataOut = null;
        String fileRequested = null;
        String prueba = "";
        byte[] data = new byte[prueba.length()];
        try {
            in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
            out = new PrintWriter(connect.getOutputStream());
            dataOut = new BufferedOutputStream(connect.getOutputStream());
            String input = in.readLine();
            StringTokenizer parse = new StringTokenizer(input);
            String method = parse.nextToken().toUpperCase();
            fileRequested = parse.nextToken();
            System.out.println(method);
            System.out.println(fileRequested);
            String[] getMethod;
            getMethod = fileRequested.split("\\?");
            System.out.print(getMethod[0].replace("/", ""));

            if (!method.equals("GET") && !method.equals("HEAD")) {
                httpHeaders(out, dataOut, "", true);
            } else {
                String dataPort = "";
                switch (getMethod.length) {
                    case 2:
                        dataPort = validateMethodResource(getMethod[0].replace("/", ""), getMethod[1]);
                        break;
                    case 1:
                        dataPort = validateMethodResource(getMethod[0].replace("/", ""), "");
                        break;
                    default:
                        dataPort = validateMethodResource("", "");
                        break;
                }

                // getDataSerialCom();
                httpHeaders(out, dataOut, dataPort, false);
            }
        } catch (IOException e) {

        } finally {
            try {
                in.close();
                out.close();
                dataOut.close();
                connect.close();
            } catch (IOException e) {
                System.err.println("Error close : " + e.getMessage());
            }
        } //To change body of generated methods, choose Tools | Templates.
    }

    public void httpHeaders(PrintWriter out, BufferedOutputStream dataOut, String dataPort, boolean isError) throws IOException {
        if (isError) {
            out.println("HTTP/1.1 501 Not Implemented");
            dataPort = "Metodo no soportado, valide nuevamente o contacte al administrador";
        } else {
            out.println("HTTP/1.1 200 OK");
        }
        out.println("Server: Java HTTP Server from Noha: 1.0");
        out.println("Date: " + new Date());
        out.println("Content-type: application/json");
        out.println("Content-length: " + dataPort.length());
        out.println();
        out.flush();
        dataOut.flush();
        dataOut.write(dataPort.getBytes(), 0, dataPort.length());
        dataOut.flush();
    }

    private String validateMethodResource(String resource, String parameters) {

        switch (resource) {
            case "getValueBascula":
                return getDataSerialCom();
            case "getConfigCom":
                return getConfigCom();
            case "getPrints":
                return printsList();
            case "print":
                return "Metodo Print";
            default:
                return "Error 404";

        }

    }

    private String getDataSerialCom() {
        //byte[] buffer = new byte[1024];
        SerialPort puerto_ser = null;
        int inicio = 0, fin = 0;
        //OutputStream out = null;
        InputStream in = null;
        CommPortIdentifier port = null;
        Enumeration puertos_libres = null;
        int salida = 0;
        //int salida2 = 0;
        String lectura = "";
        try {
            puertos_libres = CommPortIdentifier.getPortIdentifiers();
            while (puertos_libres.hasMoreElements()) {

                port = (CommPortIdentifier) puertos_libres.nextElement();
                if (port.getName().equals("COM7")) {
                    puerto_ser = (SerialPort) port.open("Noha Services", 2000);
                    int baudRate = 9600;
                    puerto_ser.setSerialPortParams(
                            baudRate,
                            SerialPort.DATABITS_8,
                            SerialPort.STOPBITS_1,
                            SerialPort.PARITY_NONE);
                    puerto_ser.setDTR(true);
                    //out = puerto_ser.getOutputStream();
                    //System.out.print(out);
                    in = puerto_ser.getInputStream();
                    long start = System.currentTimeMillis();
                    long end = start + 5000;
                    char prueba;
                    for (;;) {
                        //in = puerto_ser.getInputStream();

                        //salida = in.read(buffer, 0, buffer.length);
                        salida = in.read();

                        //System.out.println(salida);
                        //System.out.println(salida2);
                        /*if (end <= System.currentTimeMillis()) {
                        sg: wt, + 0.000, kg 
                        
                        }*/
                        prueba = (char) salida;
                        //lectura = lectura + new String(buffer).trim();

                        lectura = lectura + prueba;
                        inicio = lectura.indexOf("+");
                        fin = lectura.indexOf("Kg");

                        if (inicio != -1 && fin != -1 && inicio > fin) {
                            lectura = lectura.replace("Kg", "");
                        }
                        if (inicio != -1 && fin != -1 && inicio < fin) {
                            puerto_ser.close();
                            break;
                        }

                        //Double datoDouble = Double.valueOf(salida);
                        //System.out.println((char)salida2);
                        //System.out.println(lectura);

                    }
                    break;
                }

            }
        } catch (IOException | PortInUseException | UnsupportedCommOperationException e) {
            lectura = e.getMessage();
            System.out.print(e);

        }
        return lectura.substring(inicio + 1, fin + 2);
    }

    private String getConfigCom() {
        Enumeration puertos_libres = null;
        CommPortIdentifier port = null;
        puertos_libres = CommPortIdentifier.getPortIdentifiers();
        String aux = "";
        while (puertos_libres.hasMoreElements()) {
            port = (CommPortIdentifier) puertos_libres.nextElement();
            int type = port.getPortType();
            aux = aux + " " + type + ":" + port.getName();

        }

        return aux;

    }

    private String printsList() {
        String listado = "";
        PrintService[] ps = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService p : ps) {
            listado = listado + "' - '" + p.getName();
        }
        return listado;
    }

}
