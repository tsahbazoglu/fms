package tr.org.tspb.dao;

import org.bson.types.Code;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class MyWorkflowRelation {

    private String projectKey;
    private String formKey;
    private Code workFlowStoreId;

    public String getProjectKey() {
        return projectKey;
    }

    public String getFormKey() {
        return formKey;
    }

    public Code getWorkFlowStoreId() {
        return workFlowStoreId;
    }

}
