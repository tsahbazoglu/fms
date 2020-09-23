package tr.org.tspb.outsider;

import java.io.Serializable;

/**
 *
 * @author Telman Şahbazoğlu
 */
public interface PaymentDoor extends Serializable {

    public void newOrder(String amount);

}
