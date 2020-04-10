/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nohaservices;

/**
 *
 * @author MIGUEL
 */
public class atributos_bascula {
    private String port;
    private int time;
    private boolean second;
   

    public atributos_bascula(String port, int time, boolean second) {
        this.port = port;
        this.time = time;
        this.second = second;
    }

   

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public boolean isSecond() {
        return second;
    }

    public void setSecond(boolean second) {
        this.second = second;
    }
    
}
