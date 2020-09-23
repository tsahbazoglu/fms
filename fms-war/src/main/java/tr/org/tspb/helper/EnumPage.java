package tr.org.tspb.helper;

/**
 *
 * @author Telman Şahbazoğlu
 */
public enum EnumPage {

    PERSONNEL("/view/inputdata.xhtml",
            null,
            "Veri Yükleme",//
            "İşlemler | Veri Yükleme"),
    SETTING("/WEB-INF/view/setting.xhtml",
            null,
            "Ayarlar",//
            "İşlemler | Ayarlar"),
    USERS("/WEB-INF/view/users.xhtml",
            null,
            "Personeller",//
            "İşlemler | Personeller"),
    UPDATE_PASSWORD("/WEB-INF/view/updatepassword.xhtml",
            null,
            "Şifre Güncelle",//
            "İşlemler | Şifre Güncelle"),
    FUNCTIONAL_TEST("/WEB-INF/view/functionaltest.xhtml",
            "/WEB-INF/view/functionaltests.xhtml",
            "Fonksiyonel Testler",//
            "İşlemler | Fonksiyonel Testler",//
            null),
    SYSTEM_PAGE(//
            "/system/system.xhtml",
            "/system/system.xhtml",
            "Admin",
            "İşlemler | Dönem",
            null,
            null
    ),
    GENERAL_1D(
            "/1d/1dObject.xhtml",
            "/1d/1dObjects.xhtml",
            "Admin",
            "İşlemler | Dönem",
            "/1d/1dSelectObject.xhtml",
            "/1d/1dHistoryObjects.xhtml",
            "/1d/1dActionPanel.xhtml",
            "/common/esignHistory.xhtml",
            null,
            "/1d/ui-upload-one.xhtml"
    ),
    GENERAL_2D(//
            "/2d/2dObject.xhtml",
            "/2d/tableForm.xhtml",
            "Admin",
            "İşlemler | Dönem",
            "/2d/2dSelectObject.xhtml",
            "/2d/tableFormHistory.xhtml",
            "/2d/tableFormHeader.xhtml",
            "/common/esignHistory.xhtml",
            "/2d/twoDimDlgs.xhtml",
            "/2d/ui-upload-table-list.xhtml"
    ),
    GENERAL_2D_HADNSON(//
            "/2dHandson/2dObject.xhtml",
            "/2dHandson/2dObjects.xhtml",
            "Admin",
            "İşlemler | Dönem",
            "/2dHandson/2dSelectObject.xhtml",
            "/2dHandson/2dHistoryObjects.xhtml",
            "/2dHandson/2dActionPanel.xhtml"
    ),
    GENERAL_ND(
            "/nd/ndObject.xhtml",
            "/nd/ndObjects.xhtml",
            "Admin",
            "İşlemler | Dönem",
            "/nd/ndSelectObject.xhtml",
            "/nd/ndHistoryObjects.xhtml",
            "/nd/ndActionPanel.xhtml"
    ),
    MODULES("/ypi/modules.xhtml",
            null,
            "Admin",//
            "İşlemler | Modüller"
    ),
    WELCOME_PAGE(
            "/welcome/home.xhtml",
            "/welcome/home.xhtml",
            null,
            "Ana Sayfa",//
            "Ana Sayfa",
            null,
            "/welcome/welcomeActionPanel.xhtml"
    ),
    WELCOME_TDUB(
            "/welcometdub/home.xhtml",
            "/welcometdub/home.xhtml",
            null,
            "Ana Sayfa",//
            "Ana Sayfa",
            null,
            "/welcometdub/welcomeActionPanel.xhtml"
    ),
    DATABANK_WELCOME_PAGE(
            null,
            "/welcome/dataBankHome.xhtml",
            null,
            null,
            null,
            null,
            "/welcome/actionPanelHome.xhtml"),
    SPK_WELCOME_PAGE(//
            "/welcome/spkHome.xhtml",
            "/welcome/spkHome.xhtml",
            null,
            "Ana Sayfa",//
            "Ana Sayfa"),
    COMMISSION_WELCOME_PAGE(//
            "/welcome/commissionHome.xhtml",
            "/welcome/commissionHome.xhtml",
            null,
            "Ana Sayfa",//
            "Ana Sayfa"),
    UNDER_CONSTRUCTION_WELCOME_PAGE(//
            "/welcome/underConstructionHome.xhtml",
            "/welcome/underConstructionHome.xhtml",
            null,
            "Ana Sayfa",//
            "Ana Sayfa"),
    ION_WELCOME_PAGE(
            "/welcome/ionHome.xhtml",
            "/welcome/ionHome.xhtml",
            null,
            "Ana Sayfa",//
            "Ana Sayfa"),
    FOREX_WELCOME_PAGE(
            "/welcome/forexHome.xhtml",
            "/welcome/forexHome.xhtml",
            null,
            "Ana Sayfa",//
            "Ana Sayfa"),
    FREE_FORM_1D(
            "/hybridview/freeFormPageUser.xhtml",
            "/hybridview/freeFormPageUser.xhtml",
            "Admin",
            "İşlemler | Dönem",
            null,
            null,
            "/hybridview/freeFormToolbar.xhtml",
            "/common/esignHistory.xhtml"
    ),
    FREE_FORM_2D(
            "/hybridview/freeFormAdminList.xhtml",
            "/hybridview/freeFormAdminList.xhtml",
            "Admin",
            "İşlemler | Dönem",
            null,
            null,
            "/hybridview/freeFormToolbar.xhtml",
            "/common/esignHistory.xhtml"
    ),
    FREE_FORM_DAYANAK_VARLILARI_1D(
            "/hybridview/freeFormOne.xhtml",
            "/hybridview/freeFormDayanakOne.xhtml",
            "Admin",
            "İşlemler | Dönem",
            null,
            null,
            "/hybridview/freeFormDayanakToolbar.xhtml"),
    FREE_FORM_DAYANAK_VARLILARI_2D(
            null,
            "/hybridview/freeFormDayanakList.xhtml",
            "Admin",
            "İşlemler | Dönem",
            null,
            null,
            null),
    WELCOME_PAGE_APB(//
            "/welcome/welcomPageApb.xhtml",
            "/welcome/welcomPageApb.xhtml",
            null,
            "Ana Sayfa",//
            "Ana Sayfa"),
    GENERAL_4D(//
            "/4d/4dObject.xhtml",
            "/4d/4dObjects.xhtml",
            "Admin",
            "İşlemler | Dönem",
            "/4d/4dSelectObject.xhtml",
            null,
            "/4d/4dActionPanel.xhtml"
    ),
    PAYMENT_PAGE(//
            "/system/payment.xhtml",
            "/system/payment.xhtml",
            "Admin",
            "İşlemler | Aidat Ödeme",
            null,
            null
    ),
    FMS_PIVOT_1D(
            "/fmsPivot/fmsPivot.xhtml",
            "/fmsPivot/fmsPivot.xhtml",
            "Admin",
            "İşlemler | Dönem",
            null,
            null,
            "/fmsPivot/fmsPivotTool.xhtml",
            "/common/esignHistory.xhtml"
    ),
    FMS_PIVOT_2D(
            "/fmsPivot/fmsPivot.xhtml",
            "/fmsPivot/fmsPivot.xhtml",
            "Admin",
            "İşlemler | Dönem",
            null,
            null,
            "/fmsPivot/fmsPivotTool.xhtml",
            "/common/esignHistory.xhtml"
    );
//    ,
//    TDUB_SYSTEM_PAGE(//
//            "/system/tdubsystem.xhtml"//
//            , "/system/tdubsystem.xhtml"//
//            , "Admin"//
//            , "İşlemler | Dönem"//
//            , null //
//            , null//
//    );

    //
    private String pagePath;
    private String pageLabel;
    private String pageTitle;
    private String pageObjectListPath;
    private String pageHistoryObjectListPath;
    private String selectObjectPath;
    private String pageActionPanel;
    private String esignHistory;
    private String dialogs;
    private String uploadPage;

    private EnumPage(
            String pagePath, //
            String pageObjectListPath, //
            String menuTitle, //
            String pageLabel) {
        this.pagePath = pagePath;
        this.pageObjectListPath = pageObjectListPath;
        this.pageTitle = menuTitle;
        this.pageLabel = pageLabel;
    }

    private EnumPage(
            String pagePath, //
            String pageObjectListPath, //
            String menuTitle, //
            String pageLabel,
            String selectObjectPath) {
        this(pagePath, pageObjectListPath, menuTitle, pageLabel);
        this.selectObjectPath = selectObjectPath;
    }

    private EnumPage(
            String pagePath, //
            String pageObjectListPath, //
            String menuTitle, //
            String pageLabel,
            String selectObjectPath,
            String pageHistoryObjectListPath) {
        this(pagePath, pageObjectListPath, menuTitle, pageLabel, selectObjectPath);
        this.pageHistoryObjectListPath = pageHistoryObjectListPath;
    }

    private EnumPage(
            String pagePath, //
            String pageObjectListPath, //
            String menuTitle, //
            String pageLabel,
            String selectObjectPath,
            String pageHistoryObjectListPath,
            String pageActionPanel) {
        this(pagePath, pageObjectListPath, menuTitle, pageLabel, selectObjectPath, pageHistoryObjectListPath);
        this.pageActionPanel = pageActionPanel;
    }

    private EnumPage(
            String pagePath, //
            String pageObjectListPath, //
            String menuTitle, //
            String pageLabel,
            String selectObjectPath,
            String pageHistoryObjectListPath,
            String pageActionPanel,
            String esignHistory) {
        this(pagePath, pageObjectListPath, menuTitle, pageLabel, selectObjectPath, pageHistoryObjectListPath, pageActionPanel);
        this.esignHistory = esignHistory;
    }

    private EnumPage(
            String pagePath, //
            String pageObjectListPath, //
            String menuTitle, //
            String pageLabel,
            String selectObjectPath,
            String pageHistoryObjectListPath,
            String pageActionPanel,
            String esignHistory,
            String dialogs) {
        this(pagePath, pageObjectListPath, menuTitle, pageLabel, selectObjectPath, pageHistoryObjectListPath, pageActionPanel, esignHistory);
        this.dialogs = dialogs;
    }

    private EnumPage(
            String pagePath, //
            String pageObjectListPath, //
            String menuTitle, //
            String pageLabel,
            String selectObjectPath,
            String pageHistoryObjectListPath,
            String pageActionPanel,
            String esignHistory,
            String dialogs,
            String uploadPage) {
        this(pagePath, pageObjectListPath, menuTitle, pageLabel, selectObjectPath, pageHistoryObjectListPath, pageActionPanel, esignHistory, dialogs);
        this.uploadPage = uploadPage;
    }

    public String getDialogs() {
        return dialogs;
    }

    public String getSelectObjectPath() {
        return selectObjectPath;
    }

    public String getPagePath() {
        return pagePath;
    }

    public String getPageLabel() {
        return pageLabel;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public String getPageObjectListPath() {
        return pageObjectListPath;
    }

    public String getPageHistoryObjectListPath() {
        return pageHistoryObjectListPath;
    }

    public String getPageActionPanel() {
        return pageActionPanel;
    }

    public String getEsignHistory() {
        return esignHistory;
    }

    public String getUploadPage() {
        return uploadPage;
    }

}
