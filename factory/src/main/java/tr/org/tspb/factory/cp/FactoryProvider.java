package tr.org.tspb.factory.cp;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import tr.org.tspb.factory.qualifier.OgmCreatorQualifier;
import tr.org.tspb.util.qualifier.KeepOpenQualifier;
import tr.org.tspb.util.tools.MongoDbUtilIntr;

/**
 *
 * @author Telman Şahbazoğlu
 */
@ApplicationScoped
public class FactoryProvider {

    @Inject
    @KeepOpenQualifier
    private MongoDbUtilIntr mongoDbUtil;

    private static OgmCreatorIntr ogmCreator;

    @Produces
    @OgmCreatorQualifier
    public OgmCreatorIntr produceOgmCreator() {

        if (ogmCreator == null) {
            ogmCreator = new OgmCreatorImpl(mongoDbUtil);
        }

        return ogmCreator;
    }
}
