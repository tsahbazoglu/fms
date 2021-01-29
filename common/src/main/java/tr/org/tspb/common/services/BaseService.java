package tr.org.tspb.common.services;

import htmlflow.StaticHtml;
import tr.org.tspb.constants.ProjectConstants;
import static tr.org.tspb.constants.ProjectConstants.FORM_KEY;
import static tr.org.tspb.constants.ProjectConstants.PATH_PDF_TOOLS;
import static tr.org.tspb.constants.ProjectConstants.PRODUCT;
import static tr.org.tspb.constants.ProjectConstants.TEST;
import tr.org.tspb.util.stereotype.MyServices;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.context.FacesContext;
import org.bson.Document;
import org.xmlet.htmlapifaster.EnumContenteditableType;
import org.xmlet.htmlapifaster.EnumTypeContentType;
import tr.org.tspb.dao.TagLogin;

/**
 *
 * @author Telman Şahbazoğlu
 */
@MyServices
public class BaseService extends AbstractSrv {

    @Resource(lookup = ProjectConstants.CUSTOM_RESOURCE_ENVIRONMENT)
    private String selectProperties;

    private AppProperties appProperties;
    private ReportProperties reportProperties;
    private Properties properties;
    private String webContext;
    private final String ESIGN_DOOR_ENVIRONMENT = "ENVIRONMENT";

    @PostConstruct
    public void init() {

        properties = new Properties();

        switch (selectProperties) {

            case PRODUCT:
                properties.putAll(mongoDbUtil.findOne("configdb", "properties", new Document(FORM_KEY, "product")));
                properties.put(ESIGN_DOOR_ENVIRONMENT, PRODUCT);
                break;
            case TEST:
                properties.putAll(mongoDbUtil.findOne("configdb", "properties", new Document(FORM_KEY, "test")));
                properties.put(ESIGN_DOOR_ENVIRONMENT, PRODUCT);
                break;
            default:
        }
        appProperties = new AppProperties(properties);
        reportProperties = new ReportProperties();
    }

    public AppProperties getProperties() {
        return appProperties;
    }

    public ReportProperties getReportProperties() {
        return reportProperties;
    }

    public Properties getEsignProperties() {
        return properties;
    }

    public String getLoginDB() {
        return appProperties.tagLogin.getDb();
    }

    public String getLoginTable() {
        return appProperties.tagLogin.getTable();
    }

    public String getLoginUsernameField() {
        return appProperties.tagLogin.getUsernanmeField();
    }

    public String getLoginEmailField() {
        return appProperties.tagLogin.getEmailField();
    }

    public TagLogin getTagLogin() {
        return appProperties.tagLogin;
    }

    public String getWebContext() {
        if (webContext == null) {
            String protocol = FacesContext.getCurrentInstance().getExternalContext().getRequestScheme();
            String serverName = FacesContext.getCurrentInstance().getExternalContext().getRequestServerName();
            String serverPort = ":".concat(String.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestServerPort()));
            String contextPath = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
            webContext = protocol.concat("://").concat(serverName).concat("https".equals(protocol) ? "" : serverPort).concat(contextPath);
        }
        return webContext;
    }

    public String getWelcomePage() {
        return StaticHtml
                .view()
                .html()
                .head()
                .style().attrType(EnumTypeContentType.TEXT_CSS)
                .text(""
                        //.concat(".div-center {margin: auto;width: 50%;border: 3px solid green;padding: 10px;text-align:center}")
                        .concat(".div-center {margin: auto;width: 50%;text-align:center;padding-top: 180px;}")
                        .concat(".p-class{font-size:34px;font-weight:bold;}")
                )
                .__() //style
                .__() //head
                .body()
                .div().attrClass("div-center")
                .img()
                .addAttr("src", "/images/tspb_logo_large.png")
                .addAttr("width", "400px")
                .__() //img
                .p().attrClass("p-class").text(appProperties.getWelcomePageContent())
                .__() //p
                .__() //div
                .br().__()
                .br().__()
                .br().__()
                .br().__()
                .br().__()
                .br().__()
                .br().__()
                .br().__()
                .br().__()
                .br().__()
                .br().__()
                .br().__()
                .br().__()
                .br().__()
                .br().__()
                .__() //body
                .__() //html
                .render();
    }

    public class ReportProperties {

        public final String loginFormKey = "member";
    }

    public class AppProperties {

        private String welcomePageContent;
        private String pdfTool;
        private String googleRecaptchaSecret;
        private final String mainProjectCode;
        private final String debugMode;
        private final String icon;
        private final String titleAcronym;
        private final String title;
        private final String eimzaLicencePath;
        private final String eimzaConfigPath;
        private final String eimzaPolicyPath;
        private final String eimzaType;
        private final String ldapAdmin;
        private final String ldapPswd;
        private final String ldapUrl;
        private final String ldapUsersDn;
        private final String ldapRolesDn;
        private final String sendEmailDiaabled;
        private final String delegateDbName;
        private final String delegateTableName;
        private final String delegatedMemberFieldName;
        private final String delegatingMemberFieldName;
        private final String delegatingFormFieldName;
        private String esignCtxtEnv;
        private final String tmpDownloadPath;
        private final Map<String, String> delegateInitialSearch;
        private final TagLogin tagLogin;

        public AppProperties(Properties document) {
            googleRecaptchaSecret = document.getProperty("googleRecaptchaSecret");
            mainProjectCode = document.getProperty(ProjectConstants.MAIN_PROJECT_CODE);
            debugMode = document.getProperty(ProjectConstants.DEBUG_MODE);
            icon = document.getProperty("ICON");
            titleAcronym = document.getProperty(ProjectConstants.TITLE_ACRONYM);
            title = document.getProperty(ProjectConstants.TITLE);
            eimzaLicencePath = document.getProperty("EIMZA_LICENCE_PATH");
            eimzaConfigPath = document.getProperty("EIMZA_CONFIG_PATH");
            eimzaPolicyPath = document.getProperty("EIMZA_POLICY_PATH");
            eimzaType = document.getProperty("EIMZA_TYPE");
            ldapAdmin = document.getProperty(ProjectConstants.LDAP_ADMIN);
            ldapPswd = document.getProperty(ProjectConstants.LDAP_WHATISIT);
            ldapUrl = document.getProperty("LDAP_URL");
            ldapUsersDn = document.getProperty(ProjectConstants.LDAP_USERS_DN);
            ldapRolesDn = document.getProperty(ProjectConstants.LDAP_ROLES_DN);
            sendEmailDiaabled = document.getProperty("SEND_EMAIL_DISABLED");
            delegatedMemberFieldName = document.getProperty(ProjectConstants.DELEGATED_MEMBER_FIELD_NAME);
            delegatingMemberFieldName = document.getProperty(ProjectConstants.DELEGATING_MEMBER_FIELD_NAME);
            delegatingFormFieldName = document.getProperty(ProjectConstants.DELEGATING_FORM_FIELD_NAME);
            welcomePageContent = document.getProperty("WELCOME_PAGE");
            //
            delegateDbName = document.getProperty(ProjectConstants.DELEGATE_DB_NAME, "iondb");
            delegateTableName = document.getProperty(ProjectConstants.DELEGATE_TABLE_NAME, "member_eimza_info");
            pdfTool = document.getProperty(ProjectConstants.CUSTOM_RESOURCE_PDFTOOL, PATH_PDF_TOOLS);
            tmpDownloadPath = document.getProperty(ProjectConstants.PATH_UYS_EXPORT, "/tmp/fms/download");
            delegateInitialSearch = new HashMap<>();
            delegateInitialSearch.put("status", "000");

            tagLogin = new TagLogin((Document) document.get("LOGIN"));

        }

        public String getEsignCtxtEnv() {
            return esignCtxtEnv;
        }

        public void setEsignCtxtEnv(String esignCtxtEnv) {
            this.esignCtxtEnv = esignCtxtEnv;
        }

        public String getPdfTool() {
            return pdfTool;
        }

        public String getMainProjectCode() {
            return mainProjectCode;
        }

        public String getDebugMode() {
            return debugMode;
        }

        public String getIcon() {
            return icon;
        }

        public String getTitleAcronym() {
            return titleAcronym;
        }

        public String getTitle() {
            return title;
        }

        public String getEimzaLicencePath() {
            return eimzaLicencePath;
        }

        public String getEimzaConfigPath() {
            return eimzaConfigPath;
        }

        public String getEimzaPolicyPath() {
            return eimzaPolicyPath;
        }

        public String getEimzaType() {
            return eimzaType;
        }

        public String getLdapAdmin() {
            return ldapAdmin;
        }

        public String getLdapPswd() {
            return ldapPswd;
        }

        public String getLdapUrl() {
            return ldapUrl;
        }

        public String getLdapUsersDn() {
            return ldapUsersDn;
        }

        public String getLdapRolesDn() {
            return ldapRolesDn;
        }

        public String getSendEmailDisabled() {
            return sendEmailDiaabled;
        }

        public String getDelegateDbName() {
            return delegateDbName;
        }

        public String getDelegateTableName() {
            return delegateTableName;
        }

        public String getDelegatedMemberFieldName() {
            return delegatedMemberFieldName;
        }

        public String getDelegatingFormFieldName() {
            return delegatingFormFieldName;
        }

        public String getDelegatingMemberFieldName() {
            return delegatingMemberFieldName;
        }

        public String getTmpDownloadPath() {
            return tmpDownloadPath;
        }

        public Map<String, String> getDelegateInıtıailSearch() {
            return delegateInitialSearch;
        }

        public String getGoogleRecaptchaSecret() {
            return googleRecaptchaSecret;
        }

        public String getUploadTable() {
            return "ion_uploaded_files";
        }

        public String getWelcomePageContent() {
            return welcomePageContent;
        }
    }

}
