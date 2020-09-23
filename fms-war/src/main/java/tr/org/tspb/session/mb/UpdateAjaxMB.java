package tr.org.tspb.session.mb;

import tr.org.tspb.common.qualifier.MyAjaxQualifier;
import tr.org.tspb.util.stereotype.MyController;
import java.io.Serializable;
import javax.annotation.PostConstruct;

/**
 *
 * @author Telman Şahbazoğlu
 */
@MyController
@MyAjaxQualifier
public class UpdateAjaxMB implements Serializable {

    private static final String COMMA_SEMICOLON = ",:";
    private static final String COMMA = ",";

    private static final String ID_DLG_CTRL_AND_SAVE = "idDialogControlAndSave";
    private static final String ID_TO_BE_SIGNED_DLG = "idToBeSignedDialog";
    private static final String ID_MSG_DLG = "idMessageDialog";
    private static final String ID_DLGS = "id-dlgs";
    private static final String ID_CENTER_TOP = "idCenterTop";
    private static final String ID_CENTER_MIDDLE = "idCenterCenter";
    private static final String ID_DIM_BASED_DLGS = "id-dim-based-dlgs";
    private static final String ID_CRUD_OPS = "idCrudOperation";
    private static final String ID_DLG_CRUD_JSON = "id-dlg-crud-json";
    private static final String ID_2D_ACTION_TOOLBAR = ",id-2d-action-form:id-2d-action-toolbar";
    private static final String ID_2D_DLG_TABVIEW_CRUD_TOOLBAR = "idTwoDlgTabView:id-2d-toolbar";
    private static final String ID_2D_DLG_TABVIEW_CRUD_SCROLL_PNL = "idTwoDlgTabView:idTwoDimDlgCrudScrPnl";
    private static final String ID_TAB_VIEW = "id-tab-view";
    private static final String ID_2D_CURRENT_LIST = ID_TAB_VIEW.concat(":").concat("id2DCurrentObjectList");
    private static final String ID_2D_HISTORY_LIST = ID_TAB_VIEW.concat(":").concat("id2DHistoryObjectsList");
    private static final String ID_TWO_DIM_DLG_PNL = "";//"crud2dForm:crud_2d_dlg_panel";
    private static final String ID_DATATABLE_WF_HISTORY = "idTwoDlgTabView:id-dt-wf";
    private static final String ID_CONTENT = "";//ID_TAB_VIEW.concat(":").concat("idContentObjectList");
    private static final String ID_PNL_ESIGN_HISTORY = "";//ID_TAB_VIEW.concat(":").concat("id_eimza_tarihce");
    private static final String ID_2D_DATA_TABLE = ID_TAB_VIEW.concat(":").concat("objectsDataTable");
    private static final String ID_ESIGN = "";//ID_TAB_VIEW.concat(":").concat("id_eimza");
    private static final String ID_UPLOADED_FILES = ID_TAB_VIEW.concat(":").concat("idUploadedFiles");
    private static final String ID_1D_INPUT_FILE_FORM = "";//ID_TAB_VIEW.concat(":").concat("idInputFileForm1D");
    private static final String ID_VERSION_TABLE = "";// "idVersionTable";
    private static final String ID_1D_FRM = "";//ID_TAB_VIEW.concat(":").concat("crud1dForm");
    private static final String ID_1D_PNL_UPL_FILE = "";//ID_TAB_VIEW.concat(":").concat("idPanelUploadFile1D");
    private static final String ID_CURRENT_MYGRID_EDITABLE = ID_TAB_VIEW.concat(":").concat("idCurrentMyGrid");
    private static final String ID_CURRENT_MYGRID_READONLY = ID_TAB_VIEW.concat(":").concat("currentMyGridReadOnly");
    private static final String ID_HISTORY_MYGRID_READONLY = ID_TAB_VIEW.concat(":").concat("historyMyGridReadOnly");
    private static final String ID_2D_HISTORY = ID_TAB_VIEW.concat(":").concat("id2DHistoryObjectsList");
    private static final String ID_TABLE_UPLOADED_FILES = "idTwoDlgTabView:id2dUploadedFiles";

    private String btnFileUpload;
    private String btnNotifyWoEsing;
    private String btnShowMetada;
    private String btnShowEsignedDoc;
    private String btnNewRecord;
    private String btnBulkLoad;
    private String btnEsignDirectly;
    private String btn2dDlgEsign;
    private String btn2dSearch;
    private String btn2dSave;
    private String btn2dSaveAs;
    private String btnSistem;
    private String btn1dSave;
    private String btn1dRemoveFile;
    private String btnCheckAllAsAdmin;
    private String btnCheckAll;
    private String btnDetay;
    private String btnEditDetail;
    private String buttonDeleteDirectly;
    private String btnNdSave;
    private String btnPostCreditCard;
    private String btnRunTbb;
    private String btnMergeTbb;
    private String btnReportWebOzet;
    private String btnWfStart;
    private String btnShowWf;
    private String btnWfOnCrud;
    private String btnLdapAddUserToRole;
    private String btnPageShowDesc;

    //
    private String pnlEsignHistory;
    private String action2DSearch;
    private String action2DHistory;
    private String menuTabChange;
    private String menuSelectForm;
    private String action1dUploadFile;
    private String actionEsignAjaxPoll;
    private String dateTimeEntry1dEventChangeUpdate;
    private String dateTimeEntry1dEventDateSelectUpdate;
    private String selectMenu1dChange;
    private String filterNdDataAndTop;
    private String filterNdDataCurrent;
    private String filterNdDataHistory;
    private String filter2dData;
    private String twoDimRowSelect;

    @PostConstruct
    public void postConstruct() {

        btnLdapAddUserToRole = ID_MSG_DLG
                .concat(COMMA).concat(":id_dlg_ldap");
        btnShowMetada = ID_2D_DATA_TABLE
                .concat(COMMA).concat(ID_MSG_DLG);
        btnNotifyWoEsing = ID_TAB_VIEW
                .concat(COMMA).concat(ID_TO_BE_SIGNED_DLG);
        btnShowEsignedDoc = "idSignedEimzaDialog"
                .concat(COMMA).concat(ID_MSG_DLG);
        btnNewRecord = ID_CRUD_OPS
                .concat(COMMA).concat(ID_MSG_DLG)
                .concat(ID_2D_ACTION_TOOLBAR)
                .concat(COMMA).concat(ID_CENTER_TOP);
        btnBulkLoad = ID_2D_CURRENT_LIST
                .concat(COMMA).concat(ID_MSG_DLG);
        btnEsignDirectly = ID_TO_BE_SIGNED_DLG
                .concat(COMMA).concat(ID_PNL_ESIGN_HISTORY)
                .concat(COMMA).concat(ID_MSG_DLG);
        btn2dDlgEsign = ID_2D_DATA_TABLE
                .concat(COMMA).concat(ID_MSG_DLG)
                .concat(COMMA).concat(ID_TO_BE_SIGNED_DLG)
                .concat(COMMA).concat(ID_PNL_ESIGN_HISTORY);
        btn2dSearch = ID_2D_CURRENT_LIST
                .concat(COMMA_SEMICOLON).concat(ID_MSG_DLG);
        btn2dSave = ID_2D_DLG_TABVIEW_CRUD_TOOLBAR
                .concat(COMMA).concat(ID_2D_CURRENT_LIST)
                .concat(COMMA).concat(ID_CENTER_TOP)
                .concat(COMMA).concat(ID_TWO_DIM_DLG_PNL)
                .concat(COMMA).concat("id_2d_DialogControlAndSave")
                .concat(COMMA).concat(ID_TABLE_UPLOADED_FILES)
                .concat(COMMA).concat(ID_MSG_DLG);
        btn2dSaveAs = ID_2D_DATA_TABLE
                .concat(COMMA).concat(ID_2D_CURRENT_LIST)
                .concat(COMMA).concat(ID_CENTER_TOP)
                .concat(COMMA).concat(ID_MSG_DLG)
                .concat(COMMA).concat("id_2d_DialogControlAndSave")
                .concat(COMMA).concat(ID_TABLE_UPLOADED_FILES)
                .concat(COMMA).concat(ID_CRUD_OPS);
        btnSistem = ID_CENTER_TOP
                .concat(COMMA).concat(ID_CENTER_MIDDLE)
                .concat(COMMA).concat(ID_ESIGN);
        btn1dSave = ID_UPLOADED_FILES
                .concat(COMMA).concat(ID_VERSION_TABLE)
                .concat(COMMA).concat(ID_MSG_DLG)
                .concat(COMMA).concat("@form");
        btn1dRemoveFile = ID_UPLOADED_FILES
                .concat(COMMA).concat(ID_1D_INPUT_FILE_FORM);
        btnEditDetail = ID_CENTER_TOP
                .concat(COMMA).concat(ID_CONTENT)
                .concat(COMMA).concat(ID_ESIGN)
                .concat(COMMA).concat(ID_MSG_DLG)
                .concat(COMMA).concat(ID_DLGS)
                .concat(COMMA).concat(ID_DIM_BASED_DLGS);
        buttonDeleteDirectly = ID_2D_CURRENT_LIST
                .concat(COMMA).concat(ID_MSG_DLG);
        btnDetay = ID_MSG_DLG;
        btnCheckAllAsAdmin = ID_2D_DATA_TABLE
                .concat(COMMA).concat(ID_MSG_DLG)
                .concat(COMMA).concat(ID_CENTER_TOP);
        btnCheckAll = ID_2D_DATA_TABLE
                .concat(COMMA).concat(ID_MSG_DLG)
                .concat(COMMA).concat(ID_CENTER_TOP);
        btnNdSave = ID_MSG_DLG
                .concat(COMMA).concat(ID_CENTER_TOP)
                .concat(COMMA).concat(ID_CURRENT_MYGRID_EDITABLE)
                .concat(COMMA).concat(ID_DLG_CTRL_AND_SAVE);
        //
        action1dUploadFile = ID_UPLOADED_FILES;
        actionEsignAjaxPoll = ID_PNL_ESIGN_HISTORY;
        dateTimeEntry1dEventChangeUpdate = ID_1D_FRM;
        dateTimeEntry1dEventDateSelectUpdate = ID_1D_FRM;
        selectMenu1dChange = ID_1D_PNL_UPL_FILE;
        pnlEsignHistory = ID_PNL_ESIGN_HISTORY;
        filterNdDataAndTop = ID_CURRENT_MYGRID_EDITABLE
                .concat(COMMA).concat(ID_CURRENT_MYGRID_READONLY)
                .concat(COMMA).concat(ID_CENTER_TOP);

        filterNdDataCurrent = ID_CURRENT_MYGRID_EDITABLE.concat(COMMA).concat(ID_CURRENT_MYGRID_READONLY);
        filterNdDataHistory = ID_HISTORY_MYGRID_READONLY;

        action2DSearch = ID_CENTER_TOP
                .concat(COMMA).concat(ID_2D_CURRENT_LIST);
        action2DHistory = ID_CENTER_TOP
                .concat(COMMA).concat(ID_2D_HISTORY_LIST);

        menuTabChange = ID_CENTER_TOP
                .concat(COMMA).concat(ID_CONTENT)
                .concat(COMMA).concat(ID_MSG_DLG);

        menuSelectForm = ID_MSG_DLG
                // .concat(COMMA).concat(ID_CRUD_OPS)
                .concat(COMMA).concat(ID_TWO_DIM_DLG_PNL)
                .concat(COMMA).concat(ID_CENTER_MIDDLE)
                .concat(COMMA).concat(ID_DLGS)
                .concat(COMMA).concat(ID_CENTER_TOP)
                .concat(COMMA).concat(ID_DIM_BASED_DLGS);

        twoDimRowSelect = ID_MSG_DLG
                .concat(COMMA).concat(ID_CRUD_OPS)
                .concat(COMMA).concat(ID_DLG_CRUD_JSON)
                .concat(COMMA).concat(ID_TWO_DIM_DLG_PNL);

        filter2dData = ID_2D_HISTORY_LIST;
        btnPostCreditCard = "";//:id-post-tst
        btnRunTbb = "";//:uys_system_form:idBankalarPath
        //btnMergeTbb = ID_TAB_VIEW.concat(":uys_system_form:idWarningList");
        //btnMergeTbb = "id-tbb-web-service";//:uys_system_form:idWarningList";
        btnMergeTbb = ""; //:uys_system_form:idWarningList";

        btnReportWebOzet = "";//ID_MSG_DLG;

        btnWfOnCrud = ID_MSG_DLG
                .concat(COMMA).concat(ID_2D_DLG_TABVIEW_CRUD_TOOLBAR)
                .concat(COMMA).concat(ID_2D_DLG_TABVIEW_CRUD_SCROLL_PNL)
                .concat(COMMA).concat(ID_2D_DLG_TABVIEW_WF_TOOLBAR);

        btnShowWf = ID_MSG_DLG
                .concat(COMMA).concat(ID_2D_DLG_TABVIEW_CRUD_TOOLBAR)
                .concat(COMMA).concat(ID_2D_DLG_TABVIEW_WF_TOOLBAR)
                .concat(COMMA).concat("id-diagram")
                .concat(COMMA).concat("id-diagram-panel");

        btnWfStart = ID_MSG_DLG
                .concat(COMMA).concat(ID_2D_DLG_TABVIEW_CRUD_TOOLBAR)
                .concat(COMMA).concat(ID_2D_DLG_TABVIEW_WF_TOOLBAR)
                .concat(COMMA).concat(ID_DATATABLE_WF_HISTORY)
                .concat(COMMA).concat(ID_2D_DLG_TABVIEW_CRUD_SCROLL_PNL)
                .concat(COMMA).concat("id-diagram")
                .concat(COMMA).concat("id-diagram-panel");

        btnFileUpload = ID_TABLE_UPLOADED_FILES;

        btnPageShowDesc = "id-tab-view:id-desc-dlg-pnl";
    }
    private static final String ID_2D_DLG_TABVIEW_WF_TOOLBAR = "id-2d-wf-toolbar";

    public String getPnlEsignHistory() {
        return pnlEsignHistory;
    }

    public String getMenuTabChange() {
        return menuTabChange;
    }

    public String getMenuSelectForm() {
        return menuSelectForm;
    }

    public String getBtnShowMetada() {
        return btnShowMetada;
    }

    public String getBtnShowEsignedDoc() {
        return btnShowEsignedDoc;
    }

    public String getBtnNewRecord() {
        return btnNewRecord;
    }

    public String getBtn2dSave() {
        return btn2dSave;
    }

    public String getBtnSistem() {
        return btnSistem;
    }

    public String getAction1dUploadFile() {
        return action1dUploadFile;
    }

    public String getBtnCheckAllAsAdmin() {
        return btnCheckAllAsAdmin;
    }

    public String getBtnCheckAll() {
        return btnCheckAll;
    }

    public String getActionEsignAjaxPoll() {
        return actionEsignAjaxPoll;
    }

    public String getBtn2dSaveAs() {
        return btn2dSaveAs;
    }

    public String getBtn2dDlgEsign() {
        return btn2dDlgEsign;
    }

    public String getBtnDetay() {
        return btnDetay;
    }

    public String getBtnEditDetail() {
        return btnEditDetail;
    }

    public String getButtonDeleteDirectly() {
        return buttonDeleteDirectly;
    }

    public String getBtn1dSave() {
        return btn1dSave;
    }

    public String getBtn1dRemoveFile() {
        return btn1dRemoveFile;
    }

    public String getBtnBulkLoad() {
        return btnBulkLoad;
    }

    public String getBtnEsignDirectly() {
        return btnEsignDirectly;
    }

    public String getBtn2dSearch() {
        return btn2dSearch;
    }

    public String getAction2DSearch() {
        return action2DSearch;
    }

    public String getBtnNotifyWoEsing() {
        return btnNotifyWoEsing;
    }

    public String getDateTimeEntry1dEventChangeUpdate() {
        return dateTimeEntry1dEventChangeUpdate;
    }

    public String getDateTimeEntry1dEventDateSelectUpdate() {
        return dateTimeEntry1dEventDateSelectUpdate;
    }

    public String getSelectMenu1dChange() {
        return selectMenu1dChange;
    }

    public String getBtnNdSave() {
        return btnNdSave;
    }

    public String getFilterNdDataAndTop() {
        return filterNdDataAndTop;
    }

    public String getFilterNdData() {
        return filterNdDataCurrent;
    }

    public String getFilterNdDataHistory() {
        return filterNdDataHistory;
    }

    public String getFilter2dData() {
        return filter2dData;
    }

    public String getBtnPostCreditCard() {
        return btnPostCreditCard;
    }

    public String getBtnRunTbb() {
        return btnRunTbb;
    }

    public String getBtnMergeTbb() {
        return btnMergeTbb;
    }

    public String getBtnReportWebOzet() {
        return btnReportWebOzet;
    }

    public String getTwoDimRowSelect() {
        return twoDimRowSelect;
    }

    public String getBtnWfStart() {
        return btnWfStart;
    }

    public String getBtnShowWf() {
        return btnShowWf;
    }

    public String getBtnWfOnCrud() {
        return btnWfOnCrud;
    }

    public String getBtnFileUpload() {
        return btnFileUpload;
    }

    public String getBtnLdapAddUserToRole() {
        return btnLdapAddUserToRole;
    }

    public String getAction2DHistory() {
        return action2DHistory;
    }

    public String getBtnPageShowDesc() {
        return btnPageShowDesc;
    }

}
