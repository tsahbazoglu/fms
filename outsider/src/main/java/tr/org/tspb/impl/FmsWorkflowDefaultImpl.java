package tr.org.tspb.impl;

import org.bson.Document;
import tr.org.tspb.dao.FmsForm;
import tr.org.tspb.dao.MyMap;
import tr.org.tspb.outsider.FmsWorkFlow;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class FmsWorkflowDefaultImpl implements FmsWorkFlow {

    @Override
    public void init(FmsForm selectedForm, MyMap crudObject, Document searchObject) {
        //
    }

    @Override
    public void reset() {
        //
    }

}
