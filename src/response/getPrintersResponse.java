/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package response;

import java.util.List;

/**
 *
 * @author MIGUEL
 */
public class getPrintersResponse {
    private List<printers> prints;

    public List<printers> getPrints() {
        return prints;
    }

    public void setPrints(List<printers> prints) {
        this.prints = prints;
    }
}
