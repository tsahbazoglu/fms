package tr.org.tspb.table;

import tr.org.tspb.datamodel.gui.FmsTableDataModel;
import tr.org.tspb.common.qualifier.ViewerController;
import java.util.*;
import javax.faces.event.AjaxBehaviorEvent;
import tr.org.tspb.util.stereotype.MyController;
import tr.org.tspb.common.qualifier.MyQualifier;
import static tr.org.tspb.constants.ProjectConstants.CALCULATE;
import static tr.org.tspb.constants.ProjectConstants.MONGO_ID;
import static tr.org.tspb.constants.ProjectConstants.PERIOD;
import static tr.org.tspb.constants.ProjectConstants.SHARE_AMOUNT;
import static tr.org.tspb.constants.ProjectConstants.STYLE;
import tr.org.tspb.converter.base.MapConverter;
import tr.org.tspb.dao.FmsForm;
import tr.org.tspb.exceptions.NullNotExpectedException;

/**
 *
 * @author Telman Şahbazoğlu
 */
@MyController
@MyQualifier(myEnum = ViewerController.twoDimViewCtrl)
public class TwoDimViewCtrl extends FmsTableView {

    private boolean editmode;
    private boolean assignToFieldQuery;
    private String assignToFormFieldQuery;
    private String assignQueryToFormKey;
    private MapConverter myMapConverter;
    private Map retrievedObject;

    public void valueChangeListenerTableSearch(AjaxBehaviorEvent event) {
        search();
    }

    public String actionSearchObject() {
        search();
        return null;
    }

    public String resetFilter() {
        filterService.getTableFilterHistory().clear();
        search();
        return null;
    }

    private void search() {
        try {
            filterService.createTableFilterHistory(formService.getMyForm());
            ((FmsTableDataModel) getData()).initRowCount(findDataCount());
        } catch (NullNotExpectedException ex) {

        }
    }

    public MapConverter getMyMapConverter() {
        if (myMapConverter == null) {
            myMapConverter = new MapConverter();
        }
        return myMapConverter;
    }

    public Map getRetrievedObject() {
        return Collections.unmodifiableMap(retrievedObject);
    }

    public void setRetrievedObject(Map retrievedObject) {
        this.retrievedObject = retrievedObject;
    }

    /**
     * axises: data,structure,data, ....
     * <pre>
     * ---------------------
     * |v1,..|v2,..|   |   |
     * ---------------------
     * |v1,..|v2,..|   |   |
     * ---------------------
     * |v1,..|v2,..|   |   |
     * ---------------------
     * </pre> <ul> <li>lisp approach for mine would be <li>for(loop) AXISes if
     * data put(data.field,data.value) else if structure
     * put(structure.field,sumCellValue(v1,...)) <li>new record check = not
     * exist data.field on the map should create new record map. </ul>
     */
    public String getAssignToFormFieldQuery() {
        return assignToFormFieldQuery;
    }

    public void setAssignToFormFieldQuery(String assignToFormFieldQuery) {
        this.assignToFormFieldQuery = assignToFormFieldQuery;
    }

    public String getAssignQueryToFormKey() {
        return assignQueryToFormKey;
    }

    public void setAssignQueryToFormKey(String assignQueryToFormKey) {
        this.assignQueryToFormKey = assignQueryToFormKey;
    }

    public boolean isAssignToFieldQuery() {
        return assignToFieldQuery;
    }

    public void setAssignToFieldQuery(boolean assignToFieldQuery) {
        this.assignToFieldQuery = assignToFieldQuery;
    }

    /**
     * @return the editmode
     */
    public boolean isEditmode() {
        return editmode;
    }

    /**
     * @param editmode the editmode to set
     */
    public void setEditmode(boolean editmode) {
        this.editmode = editmode;
    }

    @Override
    public List<Map> findLazyData(int startRow, int maxResults) throws NullNotExpectedException {

        FmsForm myForm = formService.getMyForm();
        if (myForm == null || myForm.getKey() == null) {
            return Collections.emptyList();
        }

        HashMap sortMap = new HashMap();
        if (myForm.getDefaultSortField() != null) {
            for (Map.Entry entry : (Set<Map.Entry>) myForm.getDefaultSortField().entrySet()) {
                sortMap.put(entry.getKey(), entry.getValue());
            }
        }

        List<Map> list = mongoDbUtil.find(formService.getMyForm(),
                myForm.getTable(), filterService.getTableFilterHistory(), null, startRow, maxResults,
                sortMap,
                null);

        // FIXME : Generalize it : Ortaklık Yapısı
        if ("OY".equals(formService.getMyForm().getForm())) {
            double sum = 0D;
            for (Map<String, Object> map : list) {
                Object obj = map.get(SHARE_AMOUNT);
                if (obj instanceof Number) {
                    sum += ((Number) obj).doubleValue();
                }
            }
            for (Map<String, Object> map : list) {
                Object obj = map.get(SHARE_AMOUNT);
                if (obj instanceof String) {
                    obj = 0D;
                }
                if (obj != null && sum != 0D) {
                    map.put("sharePercent", 100 * ((Number) obj).doubleValue() / sum);
                }
            }

            Map<String, Object> calculcateSum = new HashMap<>();
            calculcateSum.put(MONGO_ID, "dummy_sum_id");
            calculcateSum.put(formService.getMyForm().getLoginFkField(), "");
            calculcateSum.put(PERIOD, "");
            calculcateSum.put("partnerNameTitle", "Toplam");
            calculcateSum.put(SHARE_AMOUNT, sum);
            calculcateSum.put("sharePercent", 100);
            calculcateSum.put(CALCULATE, true);
            calculcateSum.put(STYLE, "background:paleGreen;text-align:right");
            list.add(calculcateSum);
        }

        return list;

    }

    @Override
    public int findDataCount() throws NullNotExpectedException {
        if (!loginController.isUserInRole(formService.getMyForm().getMyProject().getAdminAndViewerRole())) {
            if (formService.getMyForm().getLoginFkField() == null) {
                throw new NullNotExpectedException("login foreign key had not been set");
            }
        }
        return (int) mongoDbUtil
                .count(formService.getMyForm().getDb(), formService.getMyForm().getTable(), filterService.getTableFilterHistory());
    }

}
