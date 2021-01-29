package tr.org.tspb.service;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import tr.org.tspb.outsider.EsignDoor;

/**
 *
 * @author Telman Şahbazoğlu
 */
@Named
@SessionScoped
public class FeatureService implements Serializable {

    @Inject
    private EsignDoor esignDoor;

    public EsignDoor getEsignDoor() {
        return esignDoor;
    }

}
