package tr.org.tspb.dao;

import java.util.ArrayList;
import java.util.Arrays;
import static tr.org.tspb.constants.ProjectConstants.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.bson.Document;
import org.bson.types.Code;
import org.bson.types.ObjectId;
import tr.org.tspb.constants.ProjectConstants;
import tr.org.tspb.datamodel.expected.FmsScriptRunner;
import tr.org.tspb.pojo.RoleMap;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class MyItems {

    public Code getQueryCode() {
        return queryCode;
    }

    public enum ItemType {
        list,
        doc,
        code
    }

    public enum ScriptEnv {
        app,
        mongo
    }

    private MyField myField;

    private ScriptEnv scriptEnv;
    private ItemType itemType;
    private MyLookup lookup;
    private String db;
    private String table;
    private String locale;
    private String labelStringFormat;
    private String searchField;//this is a filed regarding to wich the p:autocomplete completeMethod will be executed
    private List<String> view;
    private Document query;
    private Document historyQuery;
    private Document sort;
    private Document queryProjection;
    private Document resultProjection;
    private Number limit;
    private List list;
    private Code code;
    private Code queryCode;
    private Document itemsDoc;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(db == null ? "" : db);
        sb.append(":");
        sb.append(table == null ? "" : table);
        sb.append(":");
        sb.append(view == null ? "" : view);
        sb.append(":");
        sb.append(query == null ? "" : query);
        sb.append(":");
        sb.append(historyQuery == null ? "" : historyQuery);
        sb.append(":");
        sb.append(sort == null ? "" : sort);
        sb.append(":");
        sb.append(labelStringFormat == null ? "" : labelStringFormat);
        sb.append(":");
        sb.append(limit == null ? "" : limit);
        sb.append(":");
        sb.append(searchField == null ? "" : searchField);
        sb.append(":");
        sb.append(locale == null ? "" : locale);
        return sb.toString(); //To change body of generated methods, choose Tools | Templates.
    }

    public static final String ITEM_TABLE = "itemTable";
    public static final String ITEM_DB = "db";

    private MyItems() {
    }

    private MyItems(Object items) {
        Document dbo = (Document) items;

        this.db = (String) dbo.get(FORM_DB);
        if (db == null) {
            throw new RuntimeException("items.db is resolved to null");
        }

        this.searchField = (String) dbo.get("searchField");
        if (searchField == null) {
            searchField = "fullTextSearch";
        }

        this.table = (String) dbo.get(ITEM_TABLE);
        if (table == null) {
            throw new RuntimeException("itemTable is resolved to null");
        }

        Object labelStringFormat_ = dbo.get(LABEL_STRING_FORMAT);

        if (labelStringFormat_ != null) {
            this.labelStringFormat = labelStringFormat_.toString();
        }

        this.limit = (Number) dbo.get(LIMIT);
        this.locale = (String) dbo.get(LOCALE);

        String script = (String) dbo.get("script");
        if (script != null) {
            this.scriptEnv = ScriptEnv.valueOf(script);
        }
    }

    public String getLocale() {
        return locale;
    }

    public Document getQueryProjection() {
        return queryProjection;
    }

    public Document getResultProjection() {
        return resultProjection;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public List getList() {
        return list;
    }

    public Code getCode() {
        return code;
    }

    public String getLabelStringFormat() {
        return labelStringFormat;
    }

    public Document getQuery() {
        return query;
    }

    public Document getHistoryQuery() {
        return historyQuery;
    }

    public Document getSort() {
        return sort;
    }

    public List<String> getView() {
        return Collections.unmodifiableList(view);
    }

    public String getDb() {
        return db;
    }

    public String getTable() {
        return table;
    }

    public Number getLimit() {
        return limit;
    }

    public String getSearchField() {
        return searchField;
    }

    public MyLookup getLookup() {
        return lookup;
    }

    public static class Builder {

        private final MyItems myItems;
        private final Map filter;
        private final Object items;
        private final FmsScriptRunner fmsScriptRunner;
        private final Document defaultQueryProjection = new Document().append(CODE, true)
                .append(ORDER, true)
                .append(NAME, true);

        public Builder(Object items) {
            this.filter = null;
            this.items = items;
            this.fmsScriptRunner = null;
            this.myItems = new MyItems();
        }

        public Builder(Map filter, Object items, FmsScriptRunner fmsScriptRunner) {
            this.filter = filter;
            this.items = items;
            this.fmsScriptRunner = fmsScriptRunner;
            this.myItems = new MyItems(items);
        }

        public Builder withItemType(ItemType itemType) {
            this.myItems.itemType = itemType;
            return this;
        }

        public Builder withLookup() {
            if (items instanceof Document) {
                Document lookup = ((Document) items).get("lookup", Document.class);
                if (lookup != null) {
                    this.myItems.lookup = new MyLookup(lookup);
                }
            }
            return this;
        }

        public Builder withSort(Set<String> roleSet) {
            Document dbo = (Document) items;
            Object sort_ = dbo.get(SORT);

            if (sort_ instanceof Code) {
                Code codeSorter = (Code) sort_;
                codeSorter = new Code(codeSorter.getCode().replace(DIEZ, DOLAR));
                this.myItems.sort = (Document) fmsScriptRunner
                        .runCommand(this.myItems.getDb(), codeSorter.getCode(), filter, roleSet)
                        .get(RETVAL);
            } else if (sort_ instanceof Document) {
                this.myItems.sort = (Document) sort_;
            }
            return this;
        }

        public Builder withSortSchemaVersion110(Set<String> roleSet) {

            Document dbo = (Document) items;
            Document sort = dbo.get(SORT, Document.class);

            this.myItems.sort = new Document();

            if (sort.get("func") != null) {
                try {
                    this.myItems.sort = (Document) fmsScriptRunner
                            .runCommand(this.myItems.db, sort.get("func", String.class).replace(DIEZ, DOLAR), roleSet)
                            .get(RETVAL);
                } catch (Exception exception) {
                    //nothing
                }
            } else if (sort.get("list") != null) {
                for (Document d : (List<Document>) sort.get("list")) {
                    this.myItems.sort.put(d.get("key", String.class), d.get("value", Integer.class));
                }
            }

            return this;
        }

        public Builder withQuery(boolean admin) {

            Document dbo = (Document) items;

            Object query_ = dbo.get(QUERY);
            if (admin && dbo.get(ADMIN_QUERY) != null) {
                query_ = dbo.get(ADMIN_QUERY);
            }

            if (query_ instanceof Document) {
                this.myItems.query = fmsScriptRunner.replaceToDolar((Document) query_);
            } else if (query_ instanceof Code) {
                this.myItems.queryCode = (Code) query_;
                try {
                    this.myItems.query = (Document) fmsScriptRunner
                            .runCommand(this.myItems.db, ((Code) query_).getCode(), filter)
                            .get(RETVAL);
                } catch (Exception exception) {
                    this.myItems.query = new Document("fms_item_query_code_error", "fms_item_query_code_error");
                }
            }
            return this;
        }

        public Builder withHistoryQuery(boolean admin) {

            Document dbo = (Document) items;

            Object historyQuery_ = dbo.get(HISTORY_QUERY);
            if (admin && dbo.get(ADMIN_QUERY) != null) {
                historyQuery_ = dbo.get(ADMIN_QUERY);
            }

            if (historyQuery_ instanceof Document) {
                this.myItems.historyQuery = (Document) historyQuery_;
            } else if (historyQuery_ instanceof Code) {
                this.myItems.historyQuery = (Document) fmsScriptRunner
                        .runCommand(this.myItems.db, ((Code) historyQuery_).getCode(), filter).get(RETVAL);
            }

            if (this.myItems.historyQuery == null) {
                this.myItems.historyQuery = this.myItems.query;
            }

            return this;
        }

        public Builder withQuerySchemaVersion110(ObjectId loginMemberId, boolean admin) {
            this.myItems.itemsDoc = (Document) items;
            this.myItems.createCurrentQuery(loginMemberId, admin, filter, fmsScriptRunner);
            return this;
        }

        public Builder withHistoryQuerySchemaVersion110(ObjectId loginMemberId, boolean admin) {
            this.myItems.itemsDoc = (Document) items;
            this.myItems.createHistoryQuery(loginMemberId, admin, filter, fmsScriptRunner);
            return this;
        }

        public Builder withView(Set<String> roleSet) {
            Document dbo = (Document) items;
            if (dbo.get(VIEW) instanceof Document) {
                Document viewDoc = dbo.get(VIEW, Document.class);

                List<ViewOrder> list = new ArrayList<>();

                for (Entry entry : viewDoc.entrySet()) {

                    Object viewerKeyValue = entry.getValue();

                    if (viewerKeyValue instanceof Document) {
                        if (isUserInRole(roleSet, ((Document) viewerKeyValue).get(ACCESS_CONTROL))) {
                            Number number = (((Document) viewerKeyValue).get(ORDER, Number.class));
                            Integer order = (number == null) ? 0 : number.intValue();
                            list.add(new ViewOrder(entry.getKey().toString(), order == null ? 0 : order.intValue()));
                        }
                    } else {
                        list.add(new ViewOrder(entry.getKey().toString(), ((Number) viewerKeyValue).intValue()));
                    }
                }

                Collections.sort(list, new Comparator<ViewOrder>() {
                    @Override
                    public int compare(ViewOrder viewOrder, ViewOrder viewOrder1) {
                        return Integer.compare(viewOrder.order, viewOrder1.order);
                    }
                });

                this.myItems.view = new ArrayList<>();
                for (ViewOrder viewOrder : list) {
                    this.myItems.view.add(viewOrder.key);
                }

            }
            return this;
        }

        public Builder withViewSchemaVersion110(Set<String> roleSet) {
            Document dbo = (Document) items;
            if (dbo.get(VIEW) instanceof List) {
                List<Document> viewList = dbo.get(VIEW, List.class);

                List<ViewOrder> list = new ArrayList<>();

                for (Document entry : viewList) {
                    if (entry.get("permit") == null || isUserInRole(roleSet, ((Document) entry).get("permit"))) {
                        Number number = entry.get(ORDER, Number.class);
                        Integer order = (number == null) ? 0 : number.intValue();
                        list.add(new ViewOrder(entry.get("key").toString(), order == null ? 0 : order.intValue()));
                    }
                }

                Collections.sort(list, new Comparator<ViewOrder>() {
                    @Override
                    public int compare(ViewOrder viewOrder, ViewOrder viewOrder1) {
                        return Integer.compare(viewOrder.order, viewOrder1.order);
                    }
                });

                this.myItems.view = new ArrayList<>();
                for (ViewOrder viewOrder : list) {
                    this.myItems.view.add(viewOrder.key);
                }

            }
            return this;
        }

        public Builder withQueryProjection() {
            Document dbo = (Document) items;
            this.myItems.queryProjection = dbo.get("queryProjection", Document.class);
            if (this.myItems.queryProjection == null) {
                this.myItems.queryProjection = defaultQueryProjection;
            }
            return this;
        }

        public Builder withResultProjection() {
            Document dbo = (Document) items;
            this.myItems.resultProjection = dbo.get("resultProjection", Document.class);
            return this;
        }

        public Builder withList() {
            this.myItems.list = (List) items;
            return this;
        }

        public Builder withParent(MyField myField) {
            this.myItems.myField = myField;
            return this;
        }

        public MyItems build() {
            return this.myItems;
        }

        public Boolean isUserInRole(Set<String> myroles, Object commaSplittedRoles) {
            if (commaSplittedRoles != null) {
                if (commaSplittedRoles instanceof List) {
                    for (String string : (Iterable<? extends String>) commaSplittedRoles) {
                        if (myroles.contains(string)) {
                            return Boolean.TRUE;
                        }
                    }
                } else if (commaSplittedRoles instanceof String) {
                    String[] roles = ((String) commaSplittedRoles).split("[,]+");
                    for (String string : roles) {
                        if (myroles.contains(string)) {
                            return Boolean.TRUE;
                        }
                    }
                }
            }
            return Boolean.FALSE;
        }

        private class ViewOrder {

            String key;
            Integer order;

            public ViewOrder(String key, Integer order) {
                this.key = key;
                this.order = order;
            }

        }
    }

    public void reCreateQuery(ObjectId loginMemberId, Map filter, MyMap crudObject, RoleMap roleMap, FmsScriptRunner fmsScriptRunner) {
        if (queryCode != null) {
            Document tempDbObject = new Document(crudObject);
            tempDbObject.remove(INODE); // INODE is not serialized
            if (filter != null) {
                tempDbObject.putAll(filter);
            }

            Object object = fmsScriptRunner
                    .runCommand(db, queryCode.getCode(), tempDbObject)
                    .get(RETVAL);

            if (object instanceof Document) {
                this.query = (Document) object;
            } else {
                this.query = new Document("noresult", "noresult");
            }
        } else {
            boolean admin = roleMap.isUserInRole(myField.getMyForm().getMyProject().getAdminAndViewerRole());
            createCurrentQuery(loginMemberId, admin, filter, fmsScriptRunner);
        }
    }

    private void createCurrentQuery(ObjectId loginMemberId, boolean admin, Map filter, FmsScriptRunner fmsScriptRunner) throws RuntimeException {

        Document query_ = itemsDoc.get(QUERY, Document.class);
        if (admin && itemsDoc.get(ADMIN_QUERY) != null) {
            query_ = itemsDoc.get(ADMIN_QUERY, Document.class);
        }

        this.query = new Document();

        String func = query_.get("func", String.class);
        List<Document> listOfFilter = query_.get("list", List.class);

        if (func != null) {
            this.queryCode = new Code(func);
            try {
                this.query = (Document) fmsScriptRunner
                        .runCommand(this.db, this.queryCode.getCode(), filter)
                        .get(RETVAL);
            } catch (Exception exception) {
                this.query = new Document("fms_item_query_code_error", "fms_item_query_code_error");
            }
        } else if (listOfFilter != null) {

            for (Document d : listOfFilter) {

                String key = d.get("key", String.class);

                Document refValue = d.get("ref-value", Document.class);
                String fmsValue = d.get("fms-value", String.class);
                String strValue = d.get("string-value", String.class);
                Number numberValue = d.get("number-value", Number.class);

                if (refValue != null) {
                    this.query.put(key, new ItemsQueryRef(refValue, filter, fmsScriptRunner).value());
                } else if (fmsValue != null) {
                    switch (fmsValue) {
                        case ProjectConstants.REPLACEABLE_KEY_WORD_FOR_FUNCTONS_FILTER_PERIOD:
                            this.query.put(key, filter.get("period") == null ? "no result" : filter.get("period"));
                            break;
                        case ProjectConstants.REPLACEABLE_KEY_WORD_FOR_FUNCTONS_FILTER_TEMPLATE:
                            this.query.put(key, filter.get("template") == null ? "no result" : filter.get("template"));
                            break;
                        case ProjectConstants.REPLACEABLE_KEY_WORD_FOR_FUNCTONS_LOGIN_MEMBER_ID:
                            this.query.put(key, loginMemberId == null ? "no result" : loginMemberId);
                            break;
                        default:
                            throw new RuntimeException("could not find replaceble word");
                    }
                } else if (strValue != null) {
                    this.query.put(key, strValue);
                } else if (numberValue != null) {
                    this.query.put(key, numberValue);
                } else {

                    String type = d.get("type", String.class);
                    if (type == null) {
                        type = "string";
                    }
                    switch (type) {
                        case "number":
                            this.query.put(key, d.get(VALUE, Number.class));
                            break;
                        case "string":
                            this.query.put(key, d.get(VALUE, String.class).replaceAll(DIEZ, DOLAR));
                            break;
                        case "in":
                            this.query.put(key, new Document(DOLAR_IN, Arrays.asList(d.get(VALUE, String.class).replaceAll(DIEZ, DOLAR).split(","))));
                            break;
                        case "ne":
                            this.query.put(key, new Document(DOLAR_NE, d.get(VALUE, String.class).replaceAll(DIEZ, DOLAR)));
                            break;
                        case "regex":
                            this.query.put(key, new Document(DOLAR_REGEX, d.get(VALUE, String.class).replaceAll(DIEZ, DOLAR)));
                            break;
                        default:
                            throw new UnsupportedOperationException("field.items.query.type is not supported  : " + type);
                    }
                }
            }
        } else {
            throw new RuntimeException("field.items.query has not property func or list");
        }
    }

    private void createHistoryQuery(ObjectId loginMemberId, boolean admin, Map filter, FmsScriptRunner fmsScriptRunner) throws RuntimeException {

        Document historyQuery_ = itemsDoc.get(HISTORY_QUERY, Document.class);
        if (admin && itemsDoc.get(ADMIN_QUERY) != null) {
            historyQuery_ = itemsDoc.get(ADMIN_QUERY, Document.class);
        }

        if (historyQuery_ == null) {
            this.historyQuery = this.query;
            return;
        }

        this.historyQuery = new Document();

        String func = historyQuery_.get("func", String.class);
        List<Document> listOfFilter = historyQuery_.get("list", List.class);

        if (func != null) {
            try {
                this.historyQuery = (Document) fmsScriptRunner
                        .runCommand(this.db, func, filter)
                        .get(RETVAL);
            } catch (Exception exception) {
                this.historyQuery = new Document("fms_item_query_code_error", "fms_item_query_code_error");
            }
        } else if (listOfFilter != null) {

            for (Document d : listOfFilter) {

                String key = d.get("key", String.class);

                Document refValue = d.get("ref-value", Document.class);
                String fmsValue = d.get("fms-value", String.class);
                String strValue = d.get("string-value", String.class);
                Number numberValue = d.get("number-value", Number.class);

                if (refValue != null) {

                    this.historyQuery.put(key, new ItemsQueryRef(refValue, filter, fmsScriptRunner).value());

                } else if (fmsValue != null) {

                    switch (fmsValue) {
                        case ProjectConstants.REPLACEABLE_KEY_WORD_FOR_FUNCTONS_FILTER_PERIOD:
                            this.historyQuery.put(key, filter.get("period") == null ? "no result" : filter.get("period"));
                            break;
                        case ProjectConstants.REPLACEABLE_KEY_WORD_FOR_FUNCTONS_FILTER_TEMPLATE:
                            this.historyQuery.put(key, filter.get("template") == null ? "no result" : filter.get("template"));
                            break;
                        case ProjectConstants.REPLACEABLE_KEY_WORD_FOR_FUNCTONS_LOGIN_MEMBER_ID:
                            this.historyQuery.put(key, loginMemberId == null ? "no result" : loginMemberId);
                            break;
                        default:
                            throw new RuntimeException("could not find replaceble word");
                    }

                } else if (strValue != null) {
                    this.historyQuery.put(key, strValue);
                } else if (numberValue != null) {
                    this.historyQuery.put(key, numberValue);
                } else {

                    String type = d.get("type", String.class);
                    if (type == null) {
                        type = "string";
                    }
                    switch (type) {
                        case "number":
                            this.historyQuery.put(key, d.get(VALUE, Number.class));
                            break;
                        case "string":
                            this.historyQuery.put(key, d.get(VALUE, String.class).replaceAll(DIEZ, DOLAR));
                            break;
                        case "in":
                            this.historyQuery.put(key, new Document(DOLAR_IN, Arrays.asList(d.get(VALUE, String.class).replaceAll(DIEZ, DOLAR).split(","))));
                            break;
                        case "ne":
                            this.historyQuery.put(key, new Document(DOLAR_NE, d.get(VALUE, String.class).replaceAll(DIEZ, DOLAR)));
                            break;
                        case "regex":
                            this.historyQuery.put(key, new Document(DOLAR_REGEX, d.get(VALUE, String.class).replaceAll(DIEZ, DOLAR)));
                            break;
                        default:
                            throw new UnsupportedOperationException("field.items.query.type is not supported  : " + type);
                    }
                }
            }
        } else {
            throw new RuntimeException("field.items.query has not property func or list");
        }
    }

}
