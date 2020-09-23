package tr.org.tspb.service.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.bson.Document;
import org.bson.types.Code;
import org.bson.types.ObjectId;
import static tr.org.tspb.constants.ProjectConstants.COMMA;
import static tr.org.tspb.constants.ProjectConstants.CREATE_SESSIONID;
import static tr.org.tspb.constants.ProjectConstants.DEFAULT_QUERY;
import static tr.org.tspb.constants.ProjectConstants.DIEZ;
import static tr.org.tspb.constants.ProjectConstants.DOLAR;
import static tr.org.tspb.constants.ProjectConstants.DOLAR_GTE;
import static tr.org.tspb.constants.ProjectConstants.DOLAR_LTE;
import static tr.org.tspb.constants.ProjectConstants.DOLAR_NE;
import static tr.org.tspb.constants.ProjectConstants.DOLAR_OPTIONS;
import static tr.org.tspb.constants.ProjectConstants.DOLAR_REGEX;
import static tr.org.tspb.constants.ProjectConstants.DOT;
import static tr.org.tspb.constants.ProjectConstants.FORMS;
import static tr.org.tspb.constants.ProjectConstants.INCLUDE;
import static tr.org.tspb.constants.ProjectConstants.MONGO_ID;
import static tr.org.tspb.constants.ProjectConstants.MONGO_LDAP_UID;
import static tr.org.tspb.constants.ProjectConstants.ON_USER_ROLE;
import static tr.org.tspb.constants.ProjectConstants.QUERY;
import static tr.org.tspb.constants.ProjectConstants.RETVAL;
import tr.org.tspb.converter.base.BsonConverter;
import tr.org.tspb.converter.base.NumberConverter;
import tr.org.tspb.converter.base.SelectOneObjectIdConverter;
import tr.org.tspb.converter.base.SelectOneStringConverter;
import tr.org.tspb.converter.base.TelmanStringConverter;
import tr.org.tspb.dao.MyField;
import tr.org.tspb.dao.MyFieldReportComparator;
import tr.org.tspb.dao.MyForm;
import tr.org.tspb.dao.MyItems;
import tr.org.tspb.dao.refs.PlainRecord;
import tr.org.tspb.exceptions.NullNotExpectedException;
import tr.org.tspb.pojo.RoleMap;
import tr.org.tspb.pojo.UserDetail;
import tr.org.tspb.util.tools.MongoDbUtilIntr;
import tr.org.tspb.factory.cp.OgmCreatorIntr;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class FilterUtil {

    private static final FilterUtil instance = new FilterUtil();

    public static FilterUtil instance(MongoDbUtilIntr mongoDbUtil, OgmCreatorIntr ogmCreator) {
        instance.mongoDbUtil = mongoDbUtil;
        instance.ogmCreator = ogmCreator;
        return instance;
    }

    private MongoDbUtilIntr mongoDbUtil;
    private OgmCreatorIntr ogmCreator;

    public List<MyField> createCurrentQuickFilters(MyForm myForm, RoleMap roleMap, UserDetail userDetail, Document filter) {

        List<MyField> filterFields = new ArrayList<>();

        if (myForm != null) {
            for (String key : myForm.getFieldsKeySet()) {
                MyField myField = myForm.getField(key);
                String accesscontrol = myField.getAccesscontrol();
                String visibleValue = myField.getVisible();
                if (!myField.getQuickFilter()) {
                    continue;
                }
                if (accesscontrol == null && visibleValue == null) {
                    continue;
                }
                if (roleMap.isUserInRole(accesscontrol)
                        || roleMap.isUserInRole(visibleValue)
                        || roleMap.isUserInRole(myForm.getMyProject().getAdminAndViewerRole())) {
                    myField.createSelectItems(filter,
                            ogmCreator.getCrudObject(),
                            roleMap,
                            userDetail,
                            false);
                    filterFields.add(myField);
                }
            }
        }

        Collections.sort(filterFields, new MyFieldReportComparator());

        return filterFields;

    }

    public List<MyField> createCurrentFilters(MyForm myForm, RoleMap roleMap, UserDetail userDetail, Document filter) {

        List<MyField> filterFields = new ArrayList<>();

        if (myForm != null) {
            for (String key : myForm.getFieldsKeySet()) {
                MyField myField = myForm.getField(key);
                String accesscontrol = myField.getAccesscontrol();
                String visibleValue = myField.getVisible();
                if (!myField.getSearchAccess()) {
                    continue;
                }
                if (accesscontrol == null && visibleValue == null) {
                    continue;
                }
                if (roleMap.isUserInRole(accesscontrol)
                        || roleMap.isUserInRole(visibleValue)
                        || roleMap.isUserInRole(myForm.getMyProject().getAdminAndViewerRole())) {
                    myField.createSelectItems(filter,
                            ogmCreator.getCrudObject(),
                            roleMap,
                            userDetail,
                            false);
                    filterFields.add(myField);
                }
            }
        }

        Collections.sort(filterFields, new MyFieldReportComparator());

        return filterFields;

    }

    public Document createTableHistory(MyForm selectedForm, Map<String, Object> baseCurrent, Map<String, Object> guiHistory,
            boolean admin, UserDetail userDetail, RoleMap roleMap)
            throws NullNotExpectedException {

        Document filter = new Document();

        filter.putAll(baseCurrent);

        if (guiHistory != null) {
            filter.putAll(guiHistory);
        }

        if (!admin) {
            filter.put(selectedForm.getLoginFkField(), userDetail.getLoginFkSearchMapInListOfValues());
        }

        filter.put(MONGO_LDAP_UID, userDetail.getUsername());

        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext != null) {
            filter.put(CREATE_SESSIONID, ((HttpSession) facesContext.getExternalContext().getSession(false)).getId());
        }

        Map<String, Object> concurrentModification = new HashMap(filter);

        for (Map.Entry<String, Object> entry : concurrentModification.entrySet()) {
            Object value = entry.getValue();
            if ((value == null || SelectOneObjectIdConverter.NULL_VALUE.equals(value))) {
                filter.remove(entry.getKey());
            }
        }

        Document modifiedSearchObject = new Document();

        // to avoid java.util.ConcurrentModificationException
        for (Map.Entry<String, Object> entry : filter.entrySet()) {
            MyField myField = selectedForm.getField(entry.getKey());

            if (myField == null) {
                // this is the state when we surf accross forms with the search object 
                // from previous forms
                continue;
            }

            Object valueType = myField.getValueType();
            String field = myField.getField();
            Object value = entry.getValue();

            /**
             * for some form a "key" can be differ from "field" it represents
             * (mongoDataBank.js / alternative channels)
             */
            if (field == null) {
                throw new NullNotExpectedException("field must be defined for ".concat(entry.getKey()));
            }

            if (valueType == null
                    || value == null
                    || "null".equals(value)
                    || SelectOneStringConverter.NULL_VALUE.equals(value)
                    || value instanceof String && ((String) value).isEmpty()) {
                continue;
            }

            if (value instanceof ObjectId) {
                modifiedSearchObject.put(field, value);
            } else if (value instanceof String) {

                if (myField.getMyconverter() instanceof BsonConverter) {
                    modifiedSearchObject.put(field, value);
                } else if (myField.getMyconverter() instanceof TelmanStringConverter) {
                    if (value.toString().startsWith("(!=)")) { // NOT EQUAL
                        modifiedSearchObject.put(entry.getKey(),
                                new Document(DOLAR_NE, value.toString().substring(4))
                        );
                    } else if (value.toString().startsWith("(!%)")) { // NOT LIKE
                        modifiedSearchObject.put(entry.getKey(),
                                new Document(DOLAR_OPTIONS, "i")
                                        .append(DOLAR_REGEX, "^((?!" + value.toString().substring(4) + ").)*$")
                        );
                    } else {
                        // EQUAL
                        modifiedSearchObject.put(entry.getKey(),
                                new Document(DOLAR_OPTIONS, "i")
                                        //.append($REGEX, ((String) value).toUpperCase(new Locale("tr", "TR")))
                                        .append(DOLAR_REGEX, value)
                        );
                    }

                } else if (myField.getMyconverter() instanceof NumberConverter) {
                    Object valueCopy = value;
                    // NOT EQUAL
                    if (value.toString().startsWith("(>)") || value.toString().startsWith("(<)")) {
                        valueCopy = value.toString().substring(3);
                    }

                    if ("java.lang.Double".equals(valueType)) {
                        valueCopy = Double.valueOf(((String) valueCopy).replace(COMMA, DOT));
                    } else if ("java.lang.Integer".equals(valueType)) {
                        valueCopy = Integer.valueOf(((String) valueCopy).replace(COMMA, DOT));
                    } else {
                        valueCopy = Integer.valueOf(((String) valueCopy).replace(COMMA, DOT));
                    }

                    if (value.toString().startsWith("(>)")) { // NOT EQUAL
                        modifiedSearchObject.put(entry.getKey(), new Document(DOLAR_GTE, valueCopy));
                    } else if (value.toString().startsWith("(<)")) { // NOT EQUAL
                        modifiedSearchObject.put(entry.getKey(), new Document(DOLAR_LTE, valueCopy));
                    } else {
                        modifiedSearchObject.put(entry.getKey(), valueCopy);
                    }

                } else if ("true".equals(value) || "false".equals(value)) {
                    modifiedSearchObject.put(entry.getKey(), Boolean.valueOf((String) value));
                } else if ("java.lang.Integer".equals(valueType)) {
                    modifiedSearchObject.put(entry.getKey(), Integer.valueOf(((String) value).replace(COMMA, DOT)));
                } else if ("java.lang.Double".equals(valueType)) {
                    modifiedSearchObject.put(entry.getKey(), 100 * Double.valueOf(((String) value).replace(COMMA, DOT)));
                } else {
                    modifiedSearchObject.put(entry.getKey(),
                            new Document(DOLAR_OPTIONS, "i")
                                    .append(DOLAR_REGEX, //((String) value).toUpperCase(new Locale("tr", "TR")))
                                            value)
                    );
                }
            }
        }

        if (selectedForm.getMyNamedQueries() != null && selectedForm.getMyNamedQueries().get(DEFAULT_QUERY) instanceof Document) {
            modifiedSearchObject.putAll(((Document) selectedForm.getMyNamedQueries().get(DEFAULT_QUERY)));
        } else if (selectedForm.getFindAndSaveFilter() != null) {
            modifiedSearchObject.putAll(selectedForm.getFindAndSaveFilter());
        } else {
            modifiedSearchObject.put(FORMS, selectedForm.getForm());
        }

        Document myNamedQueries = selectedForm.getMyNamedQueries();
        if (myNamedQueries != null && myNamedQueries.get(INCLUDE) != null) {
            Document includeQuery = (Document) myNamedQueries.get(INCLUDE);
            String onUserRole = (String) includeQuery.get(ON_USER_ROLE);
            if (onUserRole != null && roleMap.isUserInRole(onUserRole)) {
                Document query = mongoDbUtil.replaceToDollar((Document) includeQuery.get(QUERY));
                modifiedSearchObject.putAll(query);
            } else {
                int priority = 0;
                Document query = null;
                for (String role : includeQuery.keySet()) {
                    Object rolePriorityObj = ((Document) includeQuery.get(role)).get("priority");

                    int rolePriority = ((Number) rolePriorityObj).intValue();

                    if (roleMap.isUserInRole(role) && rolePriority >= priority) {

                        Object roleQuery = ((Document) includeQuery.get(role)).get(QUERY);
                        if (roleQuery instanceof Code) {
                            String code = ((Code) roleQuery).getCode();
                            code = code.replace(DIEZ, DOLAR);

                            Document commandResult = mongoDbUtil
                                    .runCommand(selectedForm.getDb(), code, filter, roleMap.keySet());
                            roleQuery = commandResult.get(RETVAL);
                        } else if (roleQuery instanceof Document) {
                            roleQuery = ((Document) includeQuery.get(role)).get(QUERY);
                        }

                        if (roleQuery == null) {
                            throw new NullNotExpectedException(
                                    "</br>You are not a priveleged user for this query or you a not found in ths project database.</br>"
                                    + "It seems the related module config file is not set properly.</br>"
                                    + "Please contact with module architect.");
                        }
                        priority = rolePriority;
                        query = mongoDbUtil.replaceToDollar((Document) roleQuery);
                    }
                }
                if (query == null) {
                    // FIXME need to be reviewed
                    if (roleMap.isUserInRole(selectedForm.getMyProject().getAdminAndViewerRole())) {
                        query = new Document();
                    } else {
                        throw new NullNotExpectedException(
                                "</br>You are not a priveleged user for this query or you a not found in ths project database.</br>"
                                + "It seems the related module config file is not set properly.</br>"
                                + "Please contact with module architect.");
                    }
                }
                modifiedSearchObject.putAll(query);
            }
        }

        if (!admin) {
            modifiedSearchObject.put(selectedForm.getLoginFkField(), userDetail.getLoginFkSearchMapInListOfValues());
        }

        return modifiedSearchObject;

    }

    public Document createTableFilter(MyForm myForm, Document baseCurrent, Map<String, Object> guiCurrent,
            boolean admin, UserDetail userDetail, RoleMap roleMap) throws NullNotExpectedException {

        Document filter = new Document();

        filter.putAll(baseCurrent);

        if (guiCurrent != null) {
            for (String key : guiCurrent.keySet()) {
                Object value = guiCurrent.get(key);
                if (value instanceof PlainRecord) {
                    value = ((PlainRecord) value).getObjectId();
                }
                filter.append(key, value);
            }
        }

        for (Iterator<String> iterator = filter.keySet().iterator(); iterator.hasNext();) {
            String key = iterator.next();
            Object value = filter.get(key);
            if ((value == null || SelectOneObjectIdConverter.NULL_VALUE.equals(value))) {
                iterator.remove();
            }

        }

        // apply autoset field values
        for (MyField myField : myForm.getAutosetFields()) {

            if (!admin && myForm.getLoginFkField().equals(myField.getKey())) {
                filter.put(myForm.getLoginFkField(), userDetail.getLoginFkSearchMapInListOfValues());
                continue;
            }

            Object filterValue = filter.get(myField.getKey());
            if (filterValue == null) {
                if (MyItems.ItemType.doc.equals(myField.getItemsAsMyItems().getItemType())) {
                    Document document = resolveAutosetValue(myField.getItemsAsMyItems(), myForm, filter, false);
                    if (document != null) {
                        filter.put(myField.getKey(), document.get(MONGO_ID));
                    } else {
                        filter.put(myField.getKey(), SelectOneObjectIdConverter.NULL_VALUE);
                    }
                }
            }
        }

        //modify filter
        Document modifiedFilter = new Document();

        // to avoid java.util.ConcurrentModificationException
        for (Map.Entry<String, Object> entry : filter.entrySet()) {
            MyField myField = myForm.getField(entry.getKey());

            if (myField == null) {
                // this is the state when we surf accross forms with the search object 
                // from previous forms
                continue;
            }

            Object filterType = myField.getValueType();
            String filterField = myField.getField();
            Object filterValue = entry.getValue();

            /**
             * for some form a "key" can be differ from "field" it represents
             * (mongoDataBank.js / alternative channels)
             */
            if (filterField == null) {
                throw new NullNotExpectedException("field must be defined for ".concat(entry.getKey()));
            }

            if (filterType == null
                    || filterValue == null
                    || "null".equals(filterValue)
                    || SelectOneStringConverter.NULL_VALUE.equals(filterValue)
                    || filterValue instanceof String && ((String) filterValue).isEmpty()) {
                continue;
            }

            if (filterValue instanceof ObjectId) {
                modifiedFilter.put(filterField, filterValue);
            } else if (filterValue instanceof String) {

                if (myField.getMyconverter() instanceof BsonConverter) {
                    modifiedFilter.put(filterField, filterValue);
                } else if (myField.getMyconverter() instanceof TelmanStringConverter) {
                    if (filterValue.toString().startsWith("(!=)")) { // NOT EQUAL
                        modifiedFilter.put(entry.getKey(),
                                new Document(DOLAR_NE, filterValue.toString().substring(4))
                        );
                    } else if (filterValue.toString().startsWith("(!%)")) { // NOT LIKE
                        modifiedFilter.put(entry.getKey(),
                                new Document(DOLAR_OPTIONS, "i")
                                        .append(DOLAR_REGEX, "^((?!" + filterValue.toString().substring(4) + ").)*$")
                        );
                    } else {
                        // EQUAL
                        modifiedFilter.put(entry.getKey(),
                                new Document(DOLAR_OPTIONS, "i")
                                        //.append($REGEX, ((String) value).toUpperCase(new Locale("tr", "TR")))
                                        .append(DOLAR_REGEX, filterValue)
                        );
                    }

                } else if (myField.getMyconverter() instanceof NumberConverter) {
                    Object valueCopy = filterValue;
                    // NOT EQUAL
                    if (filterValue.toString().startsWith("(>)") || filterValue.toString().startsWith("(<)")) {
                        valueCopy = filterValue.toString().substring(3);
                    }

                    if ("java.lang.Double".equals(filterType)) {
                        valueCopy = Double.valueOf(((String) valueCopy).replace(COMMA, DOT));
                    } else if ("java.lang.Integer".equals(filterType)) {
                        valueCopy = Integer.valueOf(((String) valueCopy).replace(COMMA, DOT));
                    } else {
                        valueCopy = Integer.valueOf(((String) valueCopy).replace(COMMA, DOT));
                    }

                    if (filterValue.toString().startsWith("(>)")) { // NOT EQUAL
                        modifiedFilter.put(entry.getKey(), new Document(DOLAR_GTE, valueCopy));
                    } else if (filterValue.toString().startsWith("(<)")) { // NOT EQUAL
                        modifiedFilter.put(entry.getKey(), new Document(DOLAR_LTE, valueCopy));
                    } else {
                        modifiedFilter.put(entry.getKey(), valueCopy);
                    }

                } else if ("true".equals(filterValue) || "false".equals(filterValue)) {
                    modifiedFilter.put(entry.getKey(), Boolean.valueOf((String) filterValue));
                } else if ("java.lang.Integer".equals(filterType)) {
                    modifiedFilter.put(entry.getKey(), Integer.valueOf(((String) filterValue).replace(COMMA, DOT)));
                } else if ("java.lang.Double".equals(filterType)) {
                    modifiedFilter.put(entry.getKey(), 100 * Double.valueOf(((String) filterValue).replace(COMMA, DOT)));
                } else {
                    modifiedFilter.put(entry.getKey(),
                            new Document(DOLAR_OPTIONS, "i")
                                    .append(DOLAR_REGEX, //((String) value).toUpperCase(new Locale("tr", "TR")))
                                            filterValue)
                    );
                }
            }
        }

        if (myForm.getMyNamedQueries() != null && myForm.getMyNamedQueries().get(DEFAULT_QUERY) instanceof Document) {
            modifiedFilter.putAll(((Document) myForm.getMyNamedQueries().get(DEFAULT_QUERY)));
        } else if (myForm.getFindAndSaveFilter() != null) {
            modifiedFilter.putAll(myForm.getFindAndSaveFilter());
        } else {
            modifiedFilter.put(FORMS, myForm.getForm());
        }

        Document myNamedQueries = myForm.getMyNamedQueries();
        if (myNamedQueries
                != null && myNamedQueries.get(INCLUDE)
                != null) {
            Document includeQuery = (Document) myNamedQueries.get(INCLUDE);
            String onUserRole = (String) includeQuery.get(ON_USER_ROLE);
            if (onUserRole != null && roleMap.isUserInRole(onUserRole)) {
                Document query = mongoDbUtil.replaceToDollar((Document) includeQuery.get(QUERY));
                modifiedFilter.putAll(query);
            } else {
                int priority = 0;
                Document query = null;
                for (String role : includeQuery.keySet()) {
                    Object rolePriorityObj = ((Document) includeQuery.get(role)).get("priority");

                    int rolePriority = ((Number) rolePriorityObj).intValue();

                    if (roleMap.isUserInRole(role) && rolePriority >= priority) {

                        Object roleQuery = ((Document) includeQuery.get(role)).get(QUERY);
                        if (roleQuery instanceof Code) {
                            String code = ((Code) roleQuery).getCode();
                            code = code.replace(DIEZ, DOLAR);

                            Map map = new HashMap();
                            map.put(MONGO_LDAP_UID, userDetail.getUsername());
                            map.put(myForm.getLoginFkField(), filter.get(myForm.getLoginFkField()));
                            FacesContext facesContext = FacesContext.getCurrentInstance();
                            if (facesContext != null) {
                                map.put(CREATE_SESSIONID, ((HttpSession) facesContext.getExternalContext().getSession(false)).getId());
                            }
                            Document commandResult = mongoDbUtil.runCommand(myForm.getDb(), code, map, roleMap.keySet());
                            roleQuery = commandResult.get(RETVAL);
                        } else if (roleQuery instanceof Document) {
                            roleQuery = ((Document) includeQuery.get(role)).get(QUERY);
                        }

                        if (roleQuery == null) {
                            throw new NullNotExpectedException(
                                    "</br>You are not a priveleged user for this query or you a not found in ths project database.</br>"
                                    + "It seems the related module config file is not set properly.</br>"
                                    + "Please contact with module architect.");
                        }
                        priority = rolePriority;
                        query = mongoDbUtil.replaceToDollar((Document) roleQuery);
                    }
                }
                if (query == null) {
                    // FIXME need to be reviewed
                    if (roleMap.isUserInRole(myForm.getMyProject().getAdminAndViewerRole())) {
                        query = new Document();
                    } else {
                        throw new NullNotExpectedException(
                                "</br>You are not a priveleged user for this query or you a not found in ths project database.</br>"
                                + "It seems the related module config file is not set properly.</br>"
                                + "Please contact with module architect.");
                    }
                }
                modifiedFilter.putAll(query);
            }
        }

        if (!admin) {
            modifiedFilter.put(myForm.getLoginFkField(), userDetail.getLoginFkSearchMapInListOfValues());
            filter.put(myForm.getLoginFkField(), userDetail.getLoginFkSearchMapInListOfValues());
        }

        return modifiedFilter;

    }

    private Document resolveAutosetValue(MyItems myItems, MyForm selectedForm, Map<String, Object> filter, boolean history) {
        String collectionName = myItems.getTable();
        String database = myItems.getDb();

        Object queryObject = history ? myItems.getHistoryQuery() : myItems.getQuery();

        if (queryObject == null) {
            queryObject = myItems.getQuery();
        }

        if (queryObject instanceof Code) {
            Code func = new Code(((Code) queryObject).getCode().replace(DIEZ, DOLAR));

            Document commandResult = mongoDbUtil.runCommand(selectedForm.getDb(), func.getCode(), filter, null);

            queryObject = commandResult.get(RETVAL);
        }

        Document queryDoc = mongoDbUtil.replaceToDollar((Document) queryObject);

        Document sortObject = myItems.getSort();

        return mongoDbUtil.findOne(database == null ? selectedForm.getDb() : database, collectionName, queryDoc);

    }

}
