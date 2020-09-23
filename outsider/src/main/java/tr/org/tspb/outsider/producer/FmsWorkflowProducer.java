package tr.org.tspb.outsider.producer;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import tr.org.tspb.impl.FmsWorkflowDefaultImpl;
import tr.org.tspb.outsider.FmsWorkFlow;

/**
 *
 * @author Telman Şahbazoğlu
 */
@SessionScoped
public class FmsWorkflowProducer implements Serializable {

    /**
     *
     * @return
     */
    @Produces
    public FmsWorkFlow getFmsWorkFlow() {
        return new FmsWorkflowDefaultImpl();
    }

}
