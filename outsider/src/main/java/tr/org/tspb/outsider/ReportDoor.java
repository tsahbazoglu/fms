package tr.org.tspb.outsider;

import java.io.Serializable;
import org.primefaces.model.StreamedContent;
import tr.org.tspb.dao.FmsForm;
import tr.org.tspb.dao.MyMap;

/**
 *
 * @author Telman Şahbazoğlu
 */
public interface ReportDoor extends Serializable {

    public StreamedContent getFile(FmsForm myForm, MyMap crud);

}
