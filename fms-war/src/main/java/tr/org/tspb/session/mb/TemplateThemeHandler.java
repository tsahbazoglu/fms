/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tr.org.tspb.session.mb;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author telman
 *
 * https://www.youtube.com/watch?v=jbNdtndIsqk
 */
@Named
@SessionScoped
public class TemplateThemeHandler implements Serializable {

    private String contract = "vela";//["sentinel","spark"]
    private String layout = "web-layout";//["sentinel","spark"]

    /**
     * @return the contract
     */
    public String getContract() {
        return contract;
    }

    /**
     * @param contract the contract to set
     */
    public void setContract(String contract) {
        this.contract = contract;
    }

    public void changeContract(String contract) {
        this.contract = contract;
    }

    public void changeTemplate(String layout) {
        this.layout = layout;
    }

    /**
     * @return the layout
     */
    public String getLayout() {
        return layout;
    }

}
