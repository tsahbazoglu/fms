package tr.org.tspb.service;

import java.io.Serializable;
import javax.inject.Inject;
import org.slf4j.Logger;
import tr.org.tspb.factory.cp.OgmCreatorIntr;
import tr.org.tspb.factory.qualifier.OgmCreatorQualifier;
import tr.org.tspb.util.qualifier.KeepOpenQualifier;
import tr.org.tspb.util.tools.MongoDbUtilIntr;

/**
 *
 * @author Telman Şahbazoğlu
 */
public abstract class CommonSrv implements Serializable {

    @Inject
    protected Logger logger;

    @Inject
    @KeepOpenQualifier
    protected MongoDbUtilIntr mongoDbUtil;

    @Inject
    @OgmCreatorQualifier
    protected OgmCreatorIntr ogmCreatorIntr;

}
