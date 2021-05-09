package tr.org.tspb.service;

import tr.org.tspb.util.stereotype.MyServices;
import java.io.Serializable;
import tr.org.tspb.dao.FmsForm;

/**
 *
 * @author Telman Şahbazoğlu
 */
@MyServices
public class FormService implements Serializable {

    private FmsForm myForm;

    public FmsForm getMyForm() {
        return myForm;
    }

    public void setMyForm(FmsForm myForm) {
        this.myForm = myForm;
    }

}
