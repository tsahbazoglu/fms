package tr.org.tspb.session.mb;

import tr.org.tspb.common.services.LoginController;
import tr.org.tspb.common.qualifier.ViewerController;
import tr.org.tspb.common.qualifier.MyQualifier;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import tr.org.tspb.util.stereotype.MyController;
import tr.org.tspb.common.qualifier.MyLoginQualifier;
import tr.org.tspb.service.RepositoryService;
import static tr.org.tspb.constants.ProjectConstants.*;

/**
 *
 * @author Telman Şahbazoğlu
 */
@MyController
@MyQualifier(myEnum = ViewerController.crudFourDim)
public class CrudFourDim implements Serializable {

    @Inject
    private RepositoryService repositoryService;

    @Inject
    @MyLoginQualifier
    private LoginController loginController;

    private final static String DEFAULT_LOGIN_FK_FILED_NAME = "member";
    public static final String MB_REFR = "#{crud4dMB}";
    private List<String> selectedFormFieldKeySetAsList;
    private Map<String, Map<String, Object>> componentMap;
    private Map<String, Object> record;

    public Map<String, Map<String, Object>> getComponentMap() {
        return Collections.unmodifiableMap(componentMap);
    }

    public String save() {
        Map searchMap = new HashMap<String, Object>();
        searchMap.put(DEFAULT_LOGIN_FK_FILED_NAME, loginController.getLoggedUserDetail().getUsername());
        repositoryService.updateMany("anketdb", "anket_masak", searchMap, record, true);
        return null;
    }

    public String draw() {
        Map<String, Object> searchMap = new HashMap();
        searchMap.put(DEFAULT_LOGIN_FK_FILED_NAME, loginController.getLoggedUserDetail().getUsername());

        Map localRecord = repositoryService.one("anketdb", "anket_masak", searchMap);

        if (localRecord == null) {
            record = new HashMap<>();
        } else {
            record = localRecord;
        }

        Map<String, Object> searchMap1 = new HashMap();
        searchMap1.put(FORM_KEY, "anket_masak");

        Map<String, Object> dbo = repositoryService.one(CONFIG_DB, "graphanket", searchMap1);

        List items = (List) (((Map) ((Map) dbo.get(FORMFIELDS)).get("question")).get(ITEMS));

        componentMap = new HashMap<>();
        selectedFormFieldKeySetAsList = new ArrayList<>();

        for (Object field : items) {

            Map<String, Object> map = new HashMap<>();

            String code = (String) ((Map) field).get(CODE);
            String name = ((Map) field).get(NAME).toString();
            String componentType = ((Map) field).get(COMPONENTTYPE).toString();
            String style = (String) ((Map) field).get(STYLE);

            if ("selectOneMenu".equals(componentType)) {
                List<SelectItem> selectItems = new ArrayList<>();
                Object values = ((Map) field).get(VALUES);

                if (values instanceof List) {
                    for (Object i : (Iterable<? extends Object>) values) {
                        selectItems.add(new SelectItem(i));
                    }
                } else if (values instanceof Map) {

                    String db = (String) ((Map) values).get(FORM_DB);
                    String coll = (String) ((Map) values).get(COLLECTION);

                    Map query = new HashMap();
                    query.putAll((Map) ((Map) values).get(QUERY));

                    List<Map> cursor = repositoryService.list(db, coll, query);

                    for (Map next : cursor) {
                        selectItems.add(new SelectItem(next.get(NAME).toString()));
                    }
                }

                map.put(ITEMS, selectItems);
            }

            selectedFormFieldKeySetAsList.add(code);

            map.put(LABEL, name);
            map.put(RENDERED, true);
            map.put(COMPONENTTYPE, componentType);
            map.put(STYLE, style);

            componentMap.put(code, map);
        }

        return null;

    }

    public Map<String, Object> getRecord() {
        return Collections.unmodifiableMap(record);
    }

    public void setRecord(Map<String, Object> record) {
        this.record = record;
    }

    public List<String> getSelectedFormFieldKeySetAsList() {
        return Collections.unmodifiableList(selectedFormFieldKeySetAsList);
    }

    public void setSelectedFormFieldKeySetAsList(List<String> selectedFormFieldKeySetAsList) {
        this.selectedFormFieldKeySetAsList = selectedFormFieldKeySetAsList;
    }

}
