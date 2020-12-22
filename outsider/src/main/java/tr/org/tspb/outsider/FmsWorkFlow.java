package tr.org.tspb.outsider;

import java.io.Serializable;
import org.bson.Document;
import tr.org.tspb.dao.MyForm;
import tr.org.tspb.dao.MyMap;
import tr.org.tspb.exceptions.FormConfigException;

/**
 *
 * @author Telman Şahbazoğlu
 */
public interface FmsWorkFlow extends Serializable {

    public void init(MyForm selectedForm, MyMap crudObject, Document searchObject) throws FormConfigException;

    public void reset();

}
