package tr.org.tspb.util.tools;

import com.mongodb.ServerAddress;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import tr.org.tspb.constants.ProjectConstants;
import tr.org.tspb.util.qualifier.KeepOpenQualifier;
import tr.org.tspb.util.qualifier.OpenCloseQualifier;

/**
 *
 * @author Telman Şahbazoğlu
 */
@ApplicationScoped
public class MongoDbUtilFactory {

    @Resource(lookup = ProjectConstants.CUSTOM_RESOURCE_MONGO_URL)
    private String resourceMongoUrl;

    @Resource(lookup = ProjectConstants.CUSTOM_RESOURCE_MONGO_ADMIN_PSWD)
    private String resourceMongoAdminPswd;

    public static String mongoAdminUser;
    public static String mongoAdminPswd;
    public static ServerAddress serverAddress;

    @PostConstruct
    public void init() {
        MongoDbUtilFactory.mongoAdminUser = "tspb-db-admin";
        MongoDbUtilFactory.mongoAdminPswd = resourceMongoAdminPswd;
        MongoDbUtilFactory.serverAddress = new ServerAddress(resourceMongoUrl);
    }

    private MongoDbUtilIntr mongoDbUtil;

    @Produces
    @KeepOpenQualifier
    public MongoDbUtilIntr getKeepOpen() {
        if (mongoDbUtil == null) {
            mongoDbUtil = new MongoDbUtilImplKeepOpen(mongoAdminUser, mongoAdminPswd, serverAddress);
        }
        return mongoDbUtil;
    }

    @Produces
    @OpenCloseQualifier
    public MongoDbUtilIntr getOpenClose() {
        return new MongoDbUtilImplOpenClose(mongoAdminUser, mongoAdminPswd, serverAddress);
    }

}
