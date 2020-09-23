package tr.org.tspb.outsider.producer;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import tr.org.tspb.impl.EsignDoorDefaultImpl;
import tr.org.tspb.outsider.EsignDoor;
import tr.org.tspb.outsider.qualifier.OyasEsignDoorQualifier;

/**
 *
 * @author Telman Şahbazoğlu
 */
@SessionScoped
public class EsignDoorProducer implements Serializable {

    private enum ChooseImpl {
        oyas,
        tspb
    }

    private ChooseImpl chooseImpl = ChooseImpl.oyas;

    @Inject
    @OyasEsignDoorQualifier
    private EsignDoor esignDoor;

    /**
     *
     * @param injectionPoint
     * @return
     */
    @Produces
    public EsignDoor getEsignDoor() {
        switch (chooseImpl) {
            case oyas:
                return esignDoor;
            case tspb:
                return new EsignDoorDefaultImpl();
            default:
                return new EsignDoorDefaultImpl();
        }
    }

}
