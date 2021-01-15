/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tr.org.tspb.session.mb;

import java.io.Serializable;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import tr.org.tspb.util.stereotype.MyController;

/**
 *
 * @author telman
 */
@MyController
public class FmsLabels implements Serializable {

    private ResourceBundle messages;
    private static final String MESSAGE_PATH = "tr.org.tspb.props.labels";

    @PostConstruct
    public void init() {
        Locale trLocale = new Locale("tr", "TR");
        messages = ResourceBundle.getBundle(MESSAGE_PATH, trLocale);
    }

    public ResourceBundle getMessages() {
        return messages;
    }

}
