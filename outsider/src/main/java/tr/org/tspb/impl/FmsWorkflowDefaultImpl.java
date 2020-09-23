package tr.org.tspb.impl;

import org.bson.Document;
import tr.org.tspb.dao.MyForm;
import tr.org.tspb.dao.MyMap;
import tr.org.tspb.outsider.FmsWorkFlow;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class FmsWorkflowDefaultImpl implements FmsWorkFlow {

    @Override
    public void init(MyForm selectedForm, MyMap crudObject, Document searchObject) {
        //
    }

    @Override
    public void reset() {
        //
    }

}
