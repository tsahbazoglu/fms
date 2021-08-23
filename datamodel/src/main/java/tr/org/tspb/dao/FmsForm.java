/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tr.org.tspb.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bson.Document;
import tr.org.tspb.pojo.RoleMap;
import tr.org.tspb.pojo.UserDetail;
import tr.org.tspb.wf.MyRule;

/**
 *
 * @author telman
 */
public interface FmsForm {

    public static final String SCHEMA_VERSION_100 = "1.0.0";
    public static final String SCHEMA_VERSION_110 = "1.1.0";
    public static final String SCHEMA_VERSION_111 = "1.1.1";
    public static final String SCHEMA_VERSION = "schemaVersion";
    public static final String ION_SETTING_ACTIVITY_STATUS = "ion_setting_activity_status";

    public String getKey();

    public String getLoginFkField();

    public String printToConfigAnalyze(String fieldKey);

    public String getName();

    public Number getHistoryPosition();

    public Number getDimension();

    public String getFormType();

    public HashMap<String, Object> getDefaultCurrentQuery();

    public HashMap<String, Object> getDefaultHistoryQuery();

    public List<String> getZetDimension();

    public MyProject getMyProject();

    public String getDb();

    public String getSchemaVersion();

    public RoleMap getRoleMap();

    public UserDetail getUserDetail();

    public MyField getField(String key);

    public String getTable();

    public Document getMyNamedQueries();

    public String getSnapshotCollection();

    public Set<String> getFieldsKeySet();

    public void initActions(MyActions myActions);

    public Object getActions();

    public MyActions getMyActions();

    public List<MyField> getAutosetFields();

    public Map<String, MyField> getFields();

    public String getForm();

    public CalculateTag getCalculateQuery();

    public String getPageName();

    public Map getDefaultSortField();

    public String getConstantNote();

    public List<String> getUserConstantNoteList();

    public String getFuncNote();

    public String getImportTextFormat();

    public Boolean getHistoryRendered();

    public void runAjaxBulk(Map<String, MyField> componentMap, MyMap crudObject, RoleMap roleMap, UserDetail userDetail);

    public List getUniqueIndexList();

    public Document getAccessControlLevelTwo();

    public String getUserNote();

    public boolean isHasAttachedFiles();

    public MyNotifies getMyNotifies();

    public String getVersionCollection();

    public List<String> getVersionFields();

    public String getReadOnlyNote();

    public Document getFindAndSaveFilter();

    public TagEvent getEventFormSelection();

    public String getEsignEmailToRecipients();

    public Document getExcelFormat();

    public Document getConstraintItems();

    public List<MyRule> getWorkflowSteps();

    public String getWorkflowStartStep();

    public Document getDbo();

    public TagEvent getEventPostSave();

    public TagEvent getEventPreSave();

    public TagEvent getEventPreDelete();

    public TagEvent getEventPostDelete();

    public String getPosAmountField();

    public String getControlCollection();

    public MyMerge getUploadMerge();

    public List<ChildFilter> getChilds();

    public boolean isJsonViewer();

    public void arrangeActions(RoleMap roleMap, Document searchObject, MyMap crudObject);

    public boolean isWorkFlowActive();

    public List< MyField> getFieldsAsList();

    public void runAjaxRender(MyField myField,
            Map<String, MyField> componentMap,
            final FmsForm selectedForm,
            MyMap crudObject,
            RoleMap roleMap,
            UserDetail userDetail,
            Document filter);

    public void runAjaxRenderRef(MyField myField,
            Map<String, MyField> componentMap,
            final FmsForm selectedForm,
            MyMap crudObject,
            RoleMap roleMap,
            UserDetail userDetail,
            Document filter);

    public void runAjaxList(MyField myField,
            Map<String, MyField> componentMap,
            final FmsForm selectedForm,
            MyMap crudObject,
            RoleMap roleMap,
            UserDetail userDetail,
            Document filter);

    public void runAjax__uys_member_generate_ldapUID(Map crudObject);

    public List<String> getFieldsRowKeys();

    public MyField getFieldRow(String key);

    public int getHandsonColWidths();

    public int getHandsonRowHeaderWidth();

    public String getEsignEmailBccRecipients();

    public String getAnotherEimzaColletionKey();

}
