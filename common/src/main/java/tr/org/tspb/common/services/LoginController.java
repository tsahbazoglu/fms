package tr.org.tspb.common.services;

import com.mongodb.client.model.Filters;
import static tr.org.tspb.constants.ProjectConstants.*;
import htmlflow.DynamicHtml;
import htmlflow.HtmlView;
import tr.org.tspb.util.service.DlgCtrl;
import htmlflow.StaticHtml;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.mail.MessagingException;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.bson.types.ObjectId;
import tr.org.tspb.util.stereotype.MyController;
import java.io.Serializable;
import java.util.Set;
import java.util.stream.Stream;
import javax.inject.Inject;
import org.bson.Document;
import org.slf4j.Logger;
import tr.org.tspb.common.qualifier.MyLoginQualifier;
import tr.org.tspb.dao.MyProject;
import tr.org.tspb.pojo.DatabaseUser;
import tr.org.tspb.pojo.RoleMap;
import tr.org.tspb.pojo.UserDetail;
import tr.org.tspb.pojo.html.HtmlCompany;
import tr.org.tspb.pojo.html.HtmlRole;
import tr.org.tspb.pojo.html.HtmlUsrDetail;
import tr.org.tspb.util.qualifier.KeepOpenQualifier;
import tr.org.tspb.util.tools.MongoDbUtilIntr;

/**
 *
 * @author Telman Şahbazoğlu
 */
@MyController
@MyLoginQualifier
public class LoginController implements Serializable {

    @Inject
    MailService mailService;

    @Inject
    LdapService ldapService;

    @Inject
    BaseService baseService;

    @Inject
    DlgCtrl dialogController;

    @Inject
    private Logger logger;

    @Inject
    @KeepOpenQualifier
    private MongoDbUtilIntr mongoDbUtil;

    @Inject
    private MyHtmlTemplates myHtmlTemplates;

//    @Inject
//    //@DefaultEsignDoor
//    @OyasEsignDoorQualifier
//    private EsignDoor esignDoor;
//
//    @Inject
//    @MyQualifier(myEnum = ViewerController.twoDimModifyCtrl)
//    TwoDimModifyCtrl twoDimModifyCtrl;
    private static final int DIALOG_STEP = 2;
    private String username;
    private String userpassword;
    private UserDetail loggedUserDetail;
    private int dialogStep = 0;
    private String forgetUsername;
    private String oldPassword;
    private String newPassword;
    private String newPasswordAgain;
    private String newLdapUserUID;
    private String newLdapUserName;
    private String newLdapUserPassword;
    private List<String> newLdapUserRoles;
    private List<String> roleAsList;
    private String newLdapUserRole;
    private final transient Map dialogMap = new HashMap();
    private RoleMap roleMap;

    @PostConstruct
    public void init() {

        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance()
                .getExternalContext()
                .getRequest();

        String jaasLoginUsername = request.getRemoteUser();

        if (jaasLoginUsername == null || jaasLoginUsername.trim().isEmpty()) {
            logBaseInfo(request);
            return;
        }

        loggedUserDetail = ldapService.getUserDetailByUserID(jaasLoginUsername);

        if (loggedUserDetail.getUsername() == null || loggedUserDetail.getUsername().trim().isEmpty()) {
            logBaseInfo(request);
            invalidateSession();
            return;
        }

        Object session = FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        Object loggedUserRoles = ((HttpSession) session).getAttribute(LOGGED_USER_ROLES);

        roleMap = new RoleMap();
        roleMap.setLoggedUserRoles((List) loggedUserRoles);

        roleAsList = new ArrayList();
        for (Object x : (List) loggedUserRoles) {
            roleAsList.add(x.toString());
        }

        String loginDB = baseService.getProperties().getLoginDB();
        String loginTable = baseService.getProperties().getLoginTable();
        String loginUsernameField = baseService.getProperties().getLoginUsernameField();

        Document query = new Document(loginUsernameField, loggedUserDetail.getUsername());

        Document memberDbo = mongoDbUtil.findOne(loginDB, loginTable, query);

        if (memberDbo != null) {
            memberDbo.append("lastLoginTime", new Date());
            memberDbo.append("lastLoginIP", request.getRemoteAddr());
            mongoDbUtil.updateOne(loginDB, loginTable, query, memberDbo);

            DatabaseUser databaseUser = new DatabaseUser(memberDbo, loginUsernameField);

            loggedUserDetail.initDatabaseUser(databaseUser);
            resolveDelegations(databaseUser);
        }

    }

    private void logBaseInfo(HttpServletRequest request) {
        StringBuilder serverLog = new StringBuilder("uys app log : jaas login is success but jaasLoginUsername is null");

        serverLog.append(COMMA);
        serverLog.append(request.getRemoteHost() == null ? "REMOTE HOST" : request.getRemoteHost());

        serverLog.append(COMMA);
        serverLog.append(request.getRemoteAddr() == null ? "REMOTE ADDR" : request.getRemoteAddr());

        String logMsg = serverLog.toString();

        logger.error(logMsg);

        try {
            mailService.sendMail("UYS URGENT ISSUE", "please have a look to server.log\n ".concat(serverLog.toString()), "tsahbazoglu@tspb.org.tr");
        } catch (MessagingException ex) {
            logger.error("error occured", ex);
        }
    }

    private void invalidateSession() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        session.invalidate();
    }

    /**
     * Creates a new instance of LoginMB
     */
    public LoginController() {
        dialogMap.put(0, "loginDiaolg");
        dialogMap.put(2, "passwordForgetDiaolg");
        dialogMap.put(4, "loginDiaolg");
    }

    public String getWelcomeContent() {
        StringBuilder sb = new StringBuilder();

        String company = "TSPB2";// TDUB
        String html = "";

        switch (company) {
            case "TSPB":
                html = StaticHtml
                        .view()
                        .html()
                        .head()
                        .__() //head
                        .body()
                        .div().attrClass("container")
                        .p().text("Sayın üyemiz,").__()
                        .br().__()
                        .br().__()
                        .p().text("TSPB Üye Yönetim Sistemine hoş geldiniz.").__()
                        .br().__()
                        .br().__()
                        .p().text("TSPB Üye Yönetim Sisteminin, Google Chrome ve  Mozilla Firefox tarayıcılarıyla kullanılması tavsiye edilmektedir.").__()
                        .br().__()
                        .br().__()
                        .p().text("Giriş yapmak istediğiniz verileri, sağ mönüde yer alan ilgili başlıklara tıklayarak doldurabilirsiniz.").__()
                        .br().__()
                        .ul()
                        .li().text("<b>Bilgi Bankası</b> başlığında, Birliğimize üçer aylık dönemler itibariyle iletilen faaliyet ve finansal veriler yer almaktadır.").__()
                        .li().text("<b>Komisyon ve Promosyon Bildirimi</b> mönüsünde, Birliğimize aylık olarak bildirilen aracılık komisyonu ve promosyon uygulamalarına ilişkin form bulunmaktadır.").__()
                        .li().text("SPK’nın ilgili düzenlemeleri gereği yapılması gereken bildirimlere ilişkin formlar “<b>Bildirim Sistemi</b>” mönüsünde yer almaktadır.").__()
                        .li().text("<b>Yabancı Piyasa İşlemleri</b> mönüsünde, Birliğimize bildirilen yurtdışı piyasalardaki işlemlere ilişkin bilgiler yer almaktadır.").__()
                        .__()//ul
                        .br().__()
                        .__()
                        .__() //body
                        .__() //html
                        .render();
                break;
            case "TSPB2":
                html = StaticHtml
                        .view()
                        .html()
                        .head()
                        .__() //head
                        .body()
                        .div().attrClass("container")
                        .p().text("Sayın Üyemiz,").__()
                        .br().__()
                        .br().__()
                        .p().text("T.C. Hazine ve Maliye Bakanlığı’nın Vergi Harcamaları raporunda kullanılmak üzere, yatırımcılarca, banka ve aracı kurumlar vasıtasıyla gerçekleştirilen hisse senedi alım ve satımlarından elde edilen kazançlara ilişkin yatırımcı bazında bilgilere ihtiyaç duyulmaktadır. Bu kapsamda, 2019 yılı verileri ile yatırımcı grubu bazında pay senedi ve menkul kıymet yatırım ortaklığı pay senedi alım ve satımlarından elde edilen kazanç verilerinin  “25 Mayıs 2020 Pazartesi gününe kadar Birliğimize iletilmesi gerekmektedir.").__()
                        .br().__()
                        .br().__()
                        .p().text("Form doldurulurken dikkat edilmesi gereken hususlar aşağıdaki gibidir:").__()
                        .br().__()
                        .ul()
                        .li().text("Yıl içinde pay senedi ve menkul kıymet yatırım ortaklığı payı senedi alım satımından her bir yatırımcının net kazancı hesaplanır.").__()
                        .li().text("Yatırımcı bazında hesaplanan bilgiler ilgili yatırımcı gruplarında konsolide edilerek form doldurulur.").__()
                        .li().text("Yıl içinde net anlamda zarar eden yatırımcıların verileri dahil edilmez.").__()
                        .li().text("Gayrimenkul Yatırım Ortaklıkları ile Girişim Sermayesi Yatırım Ortaklıklarının alım ve satımlarından elde edilen kazançlar “Pay” kırılımının altında takip edilir.").__()
                        .__()//ul
                        .br().__()
                        .p().text("Söz konusu veriler ile ilgili herhangi bir konuda tereddüt olması durumunda T.C. Hazine ve Maliye Bakanlığı Gelir İdaresi Başkanlığı Uzmanı Galip Haksever (galip.haksever@gelirler.gov.tr veya 0 312 415 30 89 ) ile, anket formunun gönderimiyle ilgili olarak tereddüt olması durumunda ise Araştırma ve İstatistik Bölümü ile arastirma@tspb.org.tr e-posta adresi üzerinden irtibat kurulabilir.").__()
                        .br().__()
                        .__()
                        .__() //body
                        .__() //html
                        .render();
                break;
            case "TDUB":
                sb.append("Sayın üyemiz,  <br/><br/>");
                sb.append("TDUB Üye Yönetim Sistemine hoş geldiniz.  <br/><br/>");
                sb.append("TDUB Üye Yönetim Sisteminin, Google Chrome ve  Mozilla Firefox tarayıcılarıyla kullanılması tavsiye edilmektedir.<br/><br/>");
                sb.append("Giriş yapmak istediğiniz verileri, sağ mönüde yer alan ilgili başlıklara tıklayarak doldurabilirsiniz.<br/>");
                sb.append("<br/>");
                break;
            default:
                break;
        }
        return html;
    }

    public String getNewLdapUserUID() {
        return newLdapUserUID;
    }

    public void setNewLdapUserUID(String newLdapUserUID) {
        this.newLdapUserUID = newLdapUserUID;
    }

    public String getNewLdapUserName() {
        return newLdapUserName;
    }

    public void setNewLdapUserName(String newLdapUserName) {
        this.newLdapUserName = newLdapUserName;
    }

    public String getNewLdapUserPassword() {
        return newLdapUserPassword;
    }

    public void setNewLdapUserPassword(String newLdapUserPassword) {
        this.newLdapUserPassword = newLdapUserPassword;
    }

    public List<String> getNewLdapUserRoles() {
        return Collections.unmodifiableList(newLdapUserRoles);
    }

    public String getNewLdapUserRole() {
        return newLdapUserRole;
    }

    public void setNewLdapUserRole(String newLdapUserRole) {
        this.newLdapUserRole = newLdapUserRole;
    }

    public void setNewLdapUserRole(List<String> newLdapUserRoles) {
        this.newLdapUserRoles = newLdapUserRoles;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getNewPasswordAgain() {
        return newPasswordAgain;
    }

    public void setNewPasswordAgain(String newPasswordAgain) {
        this.newPasswordAgain = newPasswordAgain;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getForgetUsername() {
        return forgetUsername;
    }

    public void setForgetUsername(String forgetUsername) {
        this.forgetUsername = forgetUsername;
    }

    public UserDetail getLoggedUserDetail() {
        return loggedUserDetail;
    }

    public void resolveDelegations(DatabaseUser member) {
        BaseService.AppProperties serverProperties = baseService.getProperties();

        String delegateDB = serverProperties.getDelegateDbName();
        String delegateTable = serverProperties.getDelegateTableName();
        String delegatedField = serverProperties.getDelegatedMemberFieldName();
        String delegatingField = serverProperties.getDelegatingMemberFieldName();
        Map delegateInitialSearch = serverProperties.getDelegateInıtıailSearch();

        if (delegatedField == null || delegatingField == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("Konfigürasyon Eksikliği : ");
            sb.append("<br/><br/>");
            sb.append("delegation config is missed");
            dialogController.showMsgDlgWarnWithB(sb.toString());
            return;
        }

        List<Document> eimzaInfo = mongoDbUtil.find(delegateDB, delegateTable,
                new Document(delegateInitialSearch)
                        .append(delegatedField, member.getObjectId()));

        List<UserDetail.EimzaPersonel> listofEligibale = new ArrayList<>();

        for (Document delegationRecord : eimzaInfo) {
            Document delegatedFieldRecord = mongoDbUtil.findOne(serverProperties.getLoginDB(),
                    serverProperties.getLoginTable(),
                    Filters.eq(MONGO_ID, delegationRecord.get(delegatedField, ObjectId.class)));

            UserDetail.EimzaPersonel eimzaPersonel
                    = new UserDetail().new EimzaPersonel(delegationRecord, delegatedFieldRecord, delegatedField, delegatingField);

            listofEligibale.add(eimzaPersonel);
        }

        loggedUserDetail.createEimzaPersonels(listofEligibale);
        loggedUserDetail.createLoginFkSearchMapInListOfValues(member.getObjectId());
    }

    public String showRoles() {

        List<HtmlRole> list = new ArrayList<>();
        for (String role : roleAsList) {
            list.add(myHtmlTemplates.getRole(role));
        }

        List<HtmlCompany> listCompany = new ArrayList<>();
        for (UserDetail.EimzaPersonel ep : loggedUserDetail.getEimzaPersonels()) {
            listCompany.add(myHtmlTemplates.getCompany(ep.getDelegatingMember()));
        }

        Stream<HtmlRole> stream = list.stream();

        HtmlView<Stream<HtmlRole>> viewRolesTable = DynamicHtml.view(MyHtmlTemplates::roleTableTemplate);
        HtmlView<Stream<HtmlCompany>> viewCompaniesTable = DynamicHtml.view(MyHtmlTemplates::companyTableTemplate);

        String viewRolesTableStr = viewRolesTable.render(stream);

        Stream<HtmlCompany> streamCompanies = listCompany.stream();
        String viewCompaniesTableStr = viewCompaniesTable.render(streamCompanies);

        HtmlUsrDetail htmlUsrDetail = new HtmlUsrDetail(loggedUserDetail, viewRolesTableStr, viewCompaniesTableStr);

        HtmlView<HtmlUsrDetail> viewDetail = DynamicHtml.view(MyHtmlTemplates::userDetailTemplate);

        dialogController.showPopupInfo2("Kullanıcı bilgileri", viewDetail.render(htmlUsrDetail));

        return null;

    }

    public String login() throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().redirect(
                "j_security_check?j_username=" + username + "&j_password=" + userpassword);
        return null;
    }

    public int getLoginDialog() {
        return dialogStep;
    }

    public String forgetPassword() {
        dialogStep = 2;
        return null;
    }

    public String sendPassword() {
        try {
            localSendPassword();
        } catch (Exception ex) {
            logger.error("error occured", ex);
            dialogController.showPopupError("şifre güncelleme işlemi başarısız");
        }
        return null;
    }

    public void localSendPassword() throws NamingException, Exception {
        if (forgetUsername == null) {
            return;
        }

        ldapService.updatePswdAndNotifyUser(forgetUsername);
        dialogStep = 4;
        dialogController.showPopup((String) dialogMap.get(dialogStep));

    }

    public String diaologBack() {
        dialogStep -= DIALOG_STEP;
        dialogController.showPopup((String) dialogMap.get(dialogStep));
        return null;
    }

    public String goToLoginPage() {
        dialogStep = 0;
        return null;
    }

    public void logout() throws IOException {

        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        session.invalidate();
        /**
         * <welcome-file-list>.
         *
         * <welcome-file>loginServlet</welcome-file>.
         *
         * </welcome-file-list>.
         *
         * going to /uys it will try to load welcome page which is a
         * loginServlet in our case :)
         *
         * loginServlet is responsible for retrieving Jaas roles
         *
         * the following force the LoginServlet triggered
         */
        FacesContext.getCurrentInstance().getExternalContext().redirect(request.getContextPath());

    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the userpassword
     */
    public String getUserpassword() {
        return userpassword;
    }

    /**
     * @param userpassword the userpassword to set
     */
    public void setUserpassword(String userpassword) {
        this.userpassword = userpassword;
    }

    public RoleMap getRoleMap() {
        return roleMap;
    }

    public void setRoleMap(RoleMap roleMap) {
        this.roleMap = roleMap;
    }

    public boolean isUserInRole(Object roles) {
        return roleMap.isUserInRole(roles);
    }

    public Set getRolesAsSet() {
        return roleMap.keySet();
    }

    public List getRolesAsList() {
        return roleAsList;
    }

    public boolean isEmpty() {
        return roleMap.isEmpty();
    }

    void setLoggedUserRoles(List list) {
        roleMap.setLoggedUserRoles(list);
    }

    public boolean notMemberNotAdminNotViewer(MyProject myProject) {
        return loggedUserDetail.getDbo().getObjectId() == null && !isUserInRole(myProject.getAdminAndViewerRole());
    }

}
