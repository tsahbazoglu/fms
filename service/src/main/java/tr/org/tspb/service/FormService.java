package tr.org.tspb.service;

import tr.org.tspb.util.stereotype.MyServices;
import java.io.Serializable;
import tr.org.tspb.dao.MyForm;

/**
 *
 * @author Telman Şahbazoğlu
 */
@MyServices
public class FormService implements Serializable {

    private MyForm myForm;

    public MyForm getMyForm() {
        return myForm;
    }

    public void setMyForm(MyForm myForm) {
        this.myForm = myForm;
    }

}
