package tr.org.tspb.outsider;

import java.io.Serializable;
import org.bson.Document;
import tr.org.tspb.dao.MyForm;
import tr.org.tspb.dao.MyMap;

/**
 *
 * @author Telman Şahbazoğlu
 */
public interface FmsWorkFlow extends Serializable {

    public void init(MyForm selectedForm, MyMap crudObject, Document searchObject);

    public void reset();

}
