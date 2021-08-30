package tr.org.tspb.factory.util;

import com.mongodb.MongoException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.faces.model.SelectItem;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.Code;
import tr.org.tspb.constants.ProjectConstants;
import static tr.org.tspb.constants.ProjectConstants.CODE;
import static tr.org.tspb.constants.ProjectConstants.DIEZ;
import static tr.org.tspb.constants.ProjectConstants.DOLAR;
import static tr.org.tspb.constants.ProjectConstants.DOLAR_IN;
import static tr.org.tspb.constants.ProjectConstants.FORM_DB;
import static tr.org.tspb.constants.ProjectConstants.FORM_KEY;
import static tr.org.tspb.constants.ProjectConstants.LOGIN_FK;
import static tr.org.tspb.constants.ProjectConstants.MONGO_ID;
import static tr.org.tspb.constants.ProjectConstants.NAME;
import static tr.org.tspb.constants.ProjectConstants.RETVAL;
import static tr.org.tspb.constants.ProjectConstants.SELECT_PLEASE;
import static tr.org.tspb.constants.ProjectConstants.SIMPLE_DATE_FORMAT__0;
import static tr.org.tspb.constants.ProjectConstants.UPPER_NODES;
import tr.org.tspb.converter.base.BsonConverter;
import tr.org.tspb.converter.base.SelectOneDBObjectConverter;
import tr.org.tspb.converter.base.SelectOneObjectIdConverter;
import tr.org.tspb.converter.base.SelectOneStringConverter;
import tr.org.tspb.converter.mb.MySessionStore;
import tr.org.tspb.dao.FmsAutoComplete;
import tr.org.tspb.dao.MyField;
import tr.org.tspb.dao.MyItems;
import tr.org.tspb.dao.MyMap;
import tr.org.tspb.dao.MyProject;
import tr.org.tspb.dao.refs.PlainRecord;
import tr.org.tspb.dp.nullobj.PlainRecordData;
import tr.org.tspb.pojo.ComponentType;
import tr.org.tspb.pojo.RoleMap;
import tr.org.tspb.pojo.UserDetail;
import tr.org.tspb.converter.base.ConverterAttrs;
import tr.org.tspb.util.tools.MongoDbUtilIntr;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class OnFlyItems implements FmsAutoComplete {

    private final MyProject myProject;
    private final MyField myField;
    private final Document docForm;
    private final Map<String, Object> filter;
    private final RoleMap roleMap;
    private final UserDetail userDetail;
    private final MongoDbUtilIntr mongoDbUtil;

    public OnFlyItems(MyProject myProject, MyField myField, Document docForm,
            Map<String, Object> filter,
            RoleMap roleMap,
            UserDetail userDetail,
            MongoDbUtilIntr mongoDbUtil) {
        this.myProject = myProject;
        this.myField = myField;
        this.docForm = docForm;
        this.filter = filter;
        this.roleMap = roleMap;
        this.userDetail = userDetail;
        this.mongoDbUtil = mongoDbUtil;
    }

    @Override
    public List<PlainRecord> completeMethod(Document query) {
        List<PlainRecord> plainRecords = new ArrayList<>();
        List<SelectItem> items = createAutoCompleteItems(query, new MyMap());
        for (SelectItem item : items) {
            plainRecords.add(PlainRecordData.getPlainRecord(item));
        }
        return plainRecords;
    }

    /**
     *
     * @param searchObject
     * @param crudObject
     * @return
     */
    @Override
    public List<SelectItem> createSelectItems(Map searchObject, MyMap crudObject) {

        boolean isMulti = ComponentType.selectOneMenu.name().equals(myField.getComponentType());

        isMulti = isMulti || ComponentType.selectManyListbox.name().equals(myField.getComponentType());

        isMulti = isMulti || myField.isAutoComplete();

        if (!isMulti) {
            return null;
        }

        List<SelectItem> items = new ArrayList();

        String loginFk = (String) docForm.get(LOGIN_FK);

        if (myField.getKey().equals(loginFk) && !roleMap.isUserInRole(myProject.getAdminRole())) {

            List listOfIds = new ArrayList();
            for (UserDetail.EimzaPersonel ep : userDetail.getEimzaPersonels()) {
                listOfIds.add(ep.getDelegatingMember());
            }

            List<Document> documents = mongoDbUtil.find(myField.getItemsAsMyItems().getDb(), myField.getItemsAsMyItems().getTable(), new Document(MONGO_ID, new Document(DOLAR_IN, listOfIds)));

            if (!ComponentType.selectManyListbox.name().equals(myField.getComponentType())) {
                items.add(new SelectItem(SelectOneObjectIdConverter.NULL_VALUE, SELECT_PLEASE));
            }

            items.addAll(documentsToSelectItems(documents, myField.getItemsAsMyItems().getView()));

            return items;
        }

        switch (myField.getItemsAsMyItems().getItemType()) {
            case doc:
                items = documentToItems(myField.getItemsAsMyItems());
                break;
            case list:
                items = listToItems(myField.getItemsAsMyItems().getList());
                break;
            case code:
                items = codeToItems(myField.getItemsAsMyItems().getCode(), searchObject, roleMap);
                break;
            default:
                throw new UnsupportedOperationException();

        }

        myField.setSessionKey(MySessionStore.createSessionKey(
                docForm.getString(ProjectConstants.PROJECT_KEY),
                docForm.get(UPPER_NODES).toString(),
                docForm.getString(FORM_KEY),
                myField.getKey()));

        return items;

    }

    /**
     *
     * @param searchObject
     * @param crudObject
     * @return
     */
    @Override
    public List<SelectItem> createSelectItemsHistory(Map searchObject, MyMap crudObject) {

        boolean isMulti = ComponentType.selectOneMenu.name().equals(myField.getComponentType());

        isMulti = isMulti || ComponentType.selectManyListbox.name().equals(myField.getComponentType());

        isMulti = isMulti || myField.isAutoComplete();

        if (!isMulti) {
            return null;
        }

        List<SelectItem> items = new ArrayList();

        String loginFk = (String) docForm.get(LOGIN_FK);

        if (myField.getKey().equals(loginFk) && !roleMap.isUserInRole(myProject.getAdminRole())) {

            List listOfIds = new ArrayList();
            for (UserDetail.EimzaPersonel ep : userDetail.getEimzaPersonels()) {
                listOfIds.add(ep.getDelegatingMember());
            }

            List<Document> documents = mongoDbUtil.find(myField.getItemsAsMyItems().getDb(), myField.getItemsAsMyItems().getTable(), new Document(MONGO_ID, new Document(DOLAR_IN, listOfIds)));

            if (!ComponentType.selectManyListbox.name().equals(myField.getComponentType())) {
                items.add(new SelectItem(SelectOneObjectIdConverter.NULL_VALUE, SELECT_PLEASE));
            }

            items.addAll(documentsToSelectItems(documents, myField.getItemsAsMyItems().getView()));

            return items;
        }

        switch (myField.getItemsAsMyItems().getItemType()) {
            case doc:
                items = documentToItemsHistory(myField.getItemsAsMyItems());
                break;
            case list:
                items = listToItems(myField.getItemsAsMyItems().getList());
                break;
            case code:
                items = codeToItems(myField.getItemsAsMyItems().getCode(), searchObject, roleMap);
                break;
            default:
                throw new UnsupportedOperationException();

        }

        myField.setSessionKey(MySessionStore.createSessionKey(
                docForm.getString(ProjectConstants.PROJECT_KEY),
                docForm.get(UPPER_NODES).toString(),
                docForm.getString(FORM_KEY),
                myField.getKey()));

        return items;

    }

    private List<SelectItem> createAutoCompleteItems(Map autoCompleteSearch, MyMap crudObject) {

        if (!myField.isAutoComplete()) {
            return new ArrayList<>();
        }

        List<SelectItem> items = new ArrayList();

        String loginFk = (String) docForm.get(LOGIN_FK);

        if (myField.getKey().equals(loginFk) && !roleMap.isUserInRole(myProject.getAdminRole())) {

            List listOfIds = new ArrayList();
            for (UserDetail.EimzaPersonel ep : userDetail.getEimzaPersonels()) {
                listOfIds.add(ep.getDelegatingMember());
            }

            List<Document> docs = mongoDbUtil
                    .find(myField.getItemsAsMyItems().getDb(), myField.getItemsAsMyItems().getTable(), new Document(MONGO_ID, new Document(DOLAR_IN, listOfIds)));

            if (!ComponentType.selectManyListbox.name().equals(myField.getComponentType())) {
                items.add(new SelectItem(SelectOneObjectIdConverter.NULL_VALUE, SELECT_PLEASE));
            }

            items.addAll(documentsToSelectItems(docs, myField.getItemsAsMyItems().getView()));

            return items;
        }

        switch (myField.getItemsAsMyItems().getItemType()) {
            case doc:
                Document query = new Document(myField.getItemsAsMyItems().getQuery());
                if (autoCompleteSearch != null) {
                    query.putAll(autoCompleteSearch);
                }
                items = documentToItems(query, myField.getItemsAsMyItems(), 10);
                break;
            case list:
                break;
            case code:
                items = codeToItems(myField.getItemsAsMyItems().getCode(), autoCompleteSearch, roleMap);
                break;
            default:
                throw new UnsupportedOperationException();

        }
        return items;

    }

    private List<SelectItem> documentsToSelectItems(List<Document> documents, List<String> viewObjectKeySet)
            throws MongoException {

        List<SelectItem> items = new ArrayList<>();

        for (Document document : documents) {
            StringBuilder value = new StringBuilder();
            Iterator<String> iterator = viewObjectKeySet.iterator();
            while (iterator.hasNext()) {
                String field = iterator.next();
                Object fieldValue = document.get(field);
                if (fieldValue instanceof Date) {
                    value.append(SIMPLE_DATE_FORMAT__0.format(fieldValue));
                } else {
                    value.append(fieldValue);
                }
                if (iterator.hasNext()) {
                    value.append(" - ");
                }
            }
            items.add(new SelectItem(document.get(MONGO_ID), value.toString()));
        }

        return items;
    }

    private List<SelectItem> codeToItems(Code itemsObject, Map searchObject, RoleMap loginController) {

        List<SelectItem> items = new ArrayList<>();

        items.add(new SelectItem(SelectOneStringConverter.NULL_VALUE, SELECT_PLEASE));

        Document commandResult = mongoDbUtil.runCommand(myField.getDbo().get(FORM_DB).toString(),
                itemsObject.getCode().replace(DIEZ, DOLAR),
                searchObject, loginController.keySet());

        List documents = (List) commandResult.get(RETVAL);

        for (Object itemValue : documents) {
            // FIXME What about converter and rowSelectionListener ? haaa ????
            String selectItemLabel = null;

            if (itemValue instanceof Document) {
                selectItemLabel = (String) ((Document) itemValue).get(NAME);
            }

            if (selectItemLabel == null) {
                selectItemLabel = itemValue.toString();
            }

            items.add(new SelectItem(itemValue, selectItemLabel));
        }

        sortItemsByLabel(items);

        return items;
    }

    private List<SelectItem> listToItems(List documentItems) throws UnsupportedOperationException {

        List<SelectItem> items = new ArrayList<>();

        if (myField.getMyconverter() == null) {
            throw new UnsupportedOperationException("config error : converter is required : related key is : ".concat(myField.getKey()));
        }

        if (!"selectManyListbox".equals(myField.getComponentType())) {
            if (myField.getMyconverter() instanceof SelectOneStringConverter) {
                items.add(new SelectItem(((ConverterAttrs) myField.getMyconverter()).getNullValue(), SELECT_PLEASE));//FIXME Generalize NULL statemnet. use somthing like NullPttern
            } else if (myField.getMyconverter() instanceof SelectOneDBObjectConverter) {
                items.add(new SelectItem(((ConverterAttrs) myField.getMyconverter()).getNullValue(), SELECT_PLEASE));//FIXME Generalize NULL statemnet. use somthing like NullPttern
            } else if (myField.getMyconverter() instanceof BsonConverter) {
                if (!ComponentType.selectManyListbox.name().equals(myField.getComponentType())) {
                    //FIXME Generalize NULL statemnet. use somthing like NullPttern
                    items.add(new SelectItem(BsonConverter.NULL_VALUE, SELECT_PLEASE));
                }
            } else {
                throw new UnsupportedOperationException("engine does not support this type of converter.");
            }
        }

        for (Object itemValue : documentItems) {
            if (itemValue instanceof String) {
                items.add(new SelectItem(itemValue));
            } else if (itemValue instanceof Document) {
                Document selectItemDbObject = (Document) itemValue;
                String itemCode = selectItemDbObject.get(CODE).toString();
                String itemName = selectItemDbObject.get(NAME).toString();
                items.add(new SelectItem(itemCode, itemName));
            }
        }

        sortItemsByLabel(items);

        return items;
    }

    private List<SelectItem> documentToItemsHistory(MyItems myItems) throws MongoException {
        return documentToItems(myItems.getHistoryQuery(), myItems, myItems.getLimit());
    }

    private List<SelectItem> documentToItems(MyItems myItems) throws MongoException {
        return documentToItems(myItems.getQuery(), myItems, myItems.getLimit());
    }

    private List<SelectItem> documentToItems(Document query, MyItems myItems, Number limit) throws MongoException {

        List<SelectItem> items = new ArrayList<>();

        if (!"selectManyListbox".equals(myField.getComponentType())) {
            items.add(new SelectItem(SelectOneObjectIdConverter.NULL_VALUE, SELECT_PLEASE));
        }

        if (myField.getObserver() instanceof Map) {
            return items;
        }

        String db = myItems.getDb();
        String collectionName = myItems.getTable();

        if (myItems.getSort().isEmpty()) {
            List<Document> cursor = mongoDbUtil.find(db, collectionName, query, null, limit);

            items.addAll(documentsToSelectItems(cursor, myItems.getView()));

            if (myItems.getSort().isEmpty()) {
                sortItemsByLabel(items);
            }
        } else {
            mongoDbUtil.createIndex(myItems);
            List<Document> documents = mongoDbUtil.find(db, collectionName, query, myItems.getSort(), limit);
            items.addAll(documentsToSelectItems(documents, myItems.getView()));
        }

        return items;

    }

    private void sortItemsByLabel(List<SelectItem> items) {
        Collections.sort(items, new Comparator() {
            @Override
            public int compare(Object t1, Object t2) {
                if (t1 instanceof SelectItem) {
                    String m1 = ((SelectItem) t1).getLabel();
                    String m2 = ((SelectItem) t2).getLabel();

                    if (m1.equalsIgnoreCase("diğer")) {
                        return 1;
                    }

                    return m2.compareToIgnoreCase(m2);
                }
                return 0;
            }
        });
    }

    public Map<String, Object> getFilter() {
        return filter;
    }

    public RoleMap getRoleMap() {
        return roleMap;
    }

};
