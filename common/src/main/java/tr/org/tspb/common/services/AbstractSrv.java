package tr.org.tspb.common.services;

import java.io.Serializable;
import javax.inject.Inject;
import org.slf4j.Logger;
import tr.org.tspb.util.qualifier.KeepOpenQualifier;
import tr.org.tspb.util.tools.MongoDbUtilIntr;

/**
 *
 * @author Telman Şahbazoğlu
 */
public abstract class AbstractSrv implements Serializable {

    @Inject
    protected Logger logger;

    @Inject
    @KeepOpenQualifier
    protected MongoDbUtilIntr mongoDbUtil;

}
