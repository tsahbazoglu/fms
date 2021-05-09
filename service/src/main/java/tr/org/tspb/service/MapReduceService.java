package tr.org.tspb.service;

import com.mongodb.client.model.Filters;
import static tr.org.tspb.constants.ProjectConstants.*;
import tr.org.tspb.util.tools.DocumentRecursive;
import tr.org.tspb.exceptions.MongoOrmFailedException;
import tr.org.tspb.exceptions.MoreThenOneInListException;
import tr.org.tspb.exceptions.NullNotExpectedException;
import tr.org.tspb.common.mapreduce.MyMapReduce;
import tr.org.tspb.util.stereotype.MyServices;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import tr.org.tspb.common.services.AppScopeSrvCtrl;
import tr.org.tspb.dao.MyField;
import tr.org.tspb.dao.FmsForm;
import tr.org.tspb.exceptions.FormConfigException;
import tr.org.tspb.util.qualifier.KeepOpenQualifier;
import tr.org.tspb.util.tools.MongoDbUtilIntr;

/**
 *
 * @author Telman Şahbazoğlu
 */
@MyServices
public class MapReduceService implements Serializable {

    @Inject
    private AppScopeSrvCtrl appScopeSrvCtrl;

    @Inject
    private Logger logger;

    @Inject
    @KeepOpenQualifier
    private MongoDbUtilIntr mongoDbUtil;

    @Inject
    RepositoryService repositoryService;

    private final HashMap<Map, Map> snapshotMapReduceCache = new HashMap<>();

    private Map<String, Object> filter;
    private String configCollection;
    private String db;

    public void init(Map<String, Object> filter, FmsForm myForm) {
        this.filter = filter;
        this.configCollection = myForm.getMyProject().getConfigTable();
        this.db = myForm.getDb();
    }

    public void cacheSnapshotKey(Map key, FmsForm myForm)
            throws NullNotExpectedException, MongoOrmFailedException, MoreThenOneInListException, FormConfigException {
        if (snapshotMapReduceCache.get(key) == null) {
            snapshotMapReduceCache.put(key, mapReduceSnapshot(key, myForm));
        }
    }

    public Map getSnapshotMapReduceValueCache(Map key) {
        return snapshotMapReduceCache.get(key);
    }

    public Map<Map, Serializable> mapReduceSnapshot(Map cashedKey, FmsForm myForm)
            throws NullNotExpectedException, MongoOrmFailedException, MoreThenOneInListException, FormConfigException {

        for (String key : myForm.getZetDimension()) {
            String field = myForm.getField(key).getField();
            if (!cashedKey.containsKey(field)) {
                throw new RuntimeException(key.concat(" : z-dimension key missed on mapreduce filter"));
            }
        }

        return mapReduce(cashedKey, myForm.getSnapshotCollection());
    }

    public Map<Map, Serializable> mapReduce(Map cashedKey, FmsForm myForm)
            throws NullNotExpectedException, MongoOrmFailedException, MoreThenOneInListException, FormConfigException {

        for (String key : myForm.getZetDimension()) {
            String field = myForm.getField(key).getField();
            if (!cashedKey.containsKey(field)) {
                throw new RuntimeException(key.concat(" : z-dimension key missed on mapreduce filter"));
            }
        }

        return mapReduce(cashedKey, myForm.getTable());
    }

    private Map<Map, Serializable> mapReduce(Map cashedKey, String collection)
            throws NullNotExpectedException, MongoOrmFailedException, MoreThenOneInListException, FormConfigException {

        String collectionKey = (String) cashedKey.get(FORMS);

        FmsForm myForm = appScopeSrvCtrl.getFormDefinitionByKey(configCollection, collectionKey, filter);

        Bson concluededQuery = Filters.and(new Document(cashedKey), Filters.eq(FORMS, myForm.getKey()));

        List<Document> cursor = mongoDbUtil.find(db, collection, concluededQuery);

        List<DocumentRecursive> list = repositoryService.cursorToList(cursor, myForm);

        Map<String, List> initParams = new HashMap<>();
        initParams.put("x", new ArrayList());
        initParams.put("y", new ArrayList());

        for (String key : myForm.getFieldsKeySet()) {
            MyField fieldDefinition = myForm.getField(key);

            if (X_IKS.equals(fieldDefinition.getNdAxis())) {
                initParams.get("x").add(fieldDefinition.getField());
            } else if (Y_IGREK.equals(fieldDefinition.getNdAxis())) {
                initParams.get("y").add(fieldDefinition.getField());
            }
        }

        MyMapReduce myMapReduce = new MyMapReduce(initParams) {
            @Override
            public void map(Map recordMap) {

                for (Object y : (Iterable<? extends Object>) initParams.get("y")) {
                    for (Object x : (Iterable<? extends Object>) initParams.get("x")) {
                        Object xObject = recordMap.get(x);
                        Object yObject = recordMap.get(y);
                        if (yObject != null && xObject != null) {

                            Object xCode;
                            Object yCode;

                            if (xObject instanceof Map) {
                                xCode = ((Map) xObject).get(CODE);
                                if (xCode == null) {
                                    // if code is not defined then take the key
                                    xCode = x;
                                }
                            } else {
                                // this is the state when x coordinate is a measure
                                xCode = x;
                            }
                            if (yObject instanceof Map) {
                                yCode = ((Map) yObject).get(CODE);
                                if (yCode == null) {
                                    // if code is not defined then take the key
                                    yCode = y;
                                }
                            } else {
                                // this is the state when y coordinate is a measure
                                yCode = y;
                            }
                            Map<String, Object> emitKeyMap = new HashMap<>();
                            emitKeyMap.put(Y_CODE, yCode);
                            emitKeyMap.put(X_CODE, xCode);
                            emit(emitKeyMap, recordMap);
                        }
                    }
                }
            }

            @Override
            public Serializable reduce(Map key, List<Serializable> values) throws MoreThenOneInListException {
                if (values.size() > 1) {
                    throw new MoreThenOneInListException(key.toString());
                }
                return values.get(0);
            }
        };

        Map<Map, Serializable> result = myMapReduce.mapReduce(list);

        if (!list.isEmpty() && result.isEmpty()) {
            logger.error("Could not create pivot on not empty collection");
        }

        return result;

    }

}
