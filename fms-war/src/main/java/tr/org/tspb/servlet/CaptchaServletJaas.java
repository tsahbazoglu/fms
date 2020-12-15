package tr.org.tspb.servlet;

import tr.org.tspb.dao.Ppolicy;
import tr.org.tspb.dao.LoginUser;
import com.google.common.base.Strings;
import tr.org.tspb.common.services.BaseService;
import tr.org.tspb.util.VerifyRecaptcha;
import tr.org.tspb.constants.ProjectConstants;
import tr.org.tspb.fp.FgtPswdTokenManager;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tr.org.tspb.service.RepositoryService;

/**
 *
 * @author Telman Şahbazoğlu
 */
@WebServlet(name = "CaptchaServletJaas", urlPatterns = {"/loginJaas/*"})
public class CaptchaServletJaas extends HttpServlet {

    @Inject
    private BaseService baseService;

    @Inject
    private RepositoryService repositoryService;

    // @Inject
    // @LoggerProducerQualifier(type = LogType.JAVA_UTIL_LOGGING)
    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");

        boolean verified = false;

        try {
            verified = VerifyRecaptcha.verify(gRecaptchaResponse, baseService.getProperties().getGoogleRecaptchaSecret());
        } catch (IOException ex) {
            logger.error("error occured", ex);
        }

        verified = true;

        if (!verified) {
            return;
        }

        armResponse(response);

        switch (request.getPathInfo()) {

            case "/test":

                if (request.getRemoteUser() != null) {
                    request.logout();
                    request.getSession().invalidate();
                }

                String role = request.getParameter("r");
                switch (role) {
                    case "B_KON_EGT":
                        request.login("", "");
                        break;
                    case "B_KON_IZL":
                        request.login("", "");
                        break;
                    case "B_KON_KUL":
                        request.login("", "");
                        break;
                    case "B_KON_ORT":
                        request.login("", "");
                        break;
                    case "B_KON_VEK":
                        request.login("", "");
                        break;
                    case "B_KON_YON":
                        request.login("", "");
                        break;
                    case "B_UYE_UZM":
                        request.login("15815254494", "12345678eE!");
                        break;
                    case "tdubtdub":
                        request.login("tdubadmin", "12345678eE!");
                        break;
                    case "TDUB_UYS_UZM":
                        request.login("70070070078", "12345678eE");
                        break;
                    case "T_TEM_VEK":
                        //request.login("42853479282", "12345678eE!");
                        //request.login("34417668638", "12345678eE!");
                        request.login("12313434645", "12345678eE!");
                        break;
                    case "B_UYE_UZMA":
                        request.login("", "");
                        break;
                }
                sendRedirectToHome(response, request, "/");
                break;
            case "/anonym":
                try {
                String loginDB = baseService.getLoginDB();
                String loginTable = baseService.getLoginTable();
                String usernameField = baseService.getLoginUsernameField();
                String emailField = baseService.getLoginEmailField();

                String username = "anonim";
                String whatisit = "anonim";

                // authentication check
                request.login(username, whatisit);
                request.logout();

                // username check
                Map searchMap = new HashMap();
                searchMap.put(usernameField, username);
                Map userDBO = repositoryService.one(loginDB, loginTable, searchMap);

                if (userDBO == null) {
                    sendRedirectToHome(response, request, "/loginJaas/errnouser");
                    return;
                }

                LoginUser loginUser = new LoginUser(userDBO, usernameField, emailField);

                if (checkUserDataState(loginUser, response, request, loginDB, loginTable, usernameField, emailField)) {
                    return;
                }

                // policy check
                byte ppolicuResult = verifyPPolicy(loginUser, request.getRemoteAddr());

                switch (ppolicuResult) {
                    case 0://blocked
                        sendRedirectToHome(response, request, "/loginJaas/errpolicy");
                        return;
                    case 1://expired
                        /*
                                anonim user is not provided with reset password link as it is a genereal username for all guests.
                         */
                        sendRedirectToHome(response, request, "/loginJaas/errpolicy");
                        return;
                }
                request.login(username, whatisit);
                sendRedirectToHome(response, request, "/");
            } catch (Exception ex) {
                sendRedirectToHome(response, request, "/loginJaas/errpolicy");
            }
            break;
            case "/erremail":
                writeToResponse(response, "Veri Hatası : e-posta tanımlı değil");
                break;
            case "/errnouser":
                writeToResponse(response, "Veri Hatası : Kullanıcı tanımlı değill");
                break;
            case "/errmultiemail":
                writeToResponse(response, "Veri Hatası : birden falza e-posta tanımlı.");
                break;
            case "/errmultiusername":
                writeToResponse(response, "Veri Hatası : birden falza kullanıcı adı tanımlı.");
                break;
            case "/errpolicy":
                writeToResponse(response, "Veri Hatası : Kullanıcı politikası tanımlı değill.");
                break;
            default:
                try {
                response.sendError(404);
            } catch (IOException ex) {
                logger.error("error occured", ex);
            }
            break;
        }
    }

    private boolean checkUserDataState(LoginUser loginUser, HttpServletResponse response, HttpServletRequest request,
            String loginDB, String loginTable, String usernameField, String emailField) {
        // email check
        if (loginUser.getEmail() == null) {
            sendRedirectToHome(response, request, "/loginJaas/erremail");
            return true;
        }
        // username  unique check
        Map searchMap = new HashMap();
        searchMap.put(usernameField, loginUser.getUsername());
        if (repositoryService.count(loginDB, loginTable, searchMap) > 1) {
            sendRedirectToHome(response, request, "/loginJaas/errmultiusername");
            return true;
        }
        //email unique check
        Map searchMap1 = new HashMap();
        searchMap1.put(emailField, loginUser.getEmail());
        if (repositoryService.count(loginDB, loginTable, searchMap1) > 1) {
            sendRedirectToHome(response, request, "/loginJaas/errmultiemail");
            return true;
        }
        return false;
    }

    private void writeToResponse(HttpServletResponse response, String msg) {
        try {
            response.getWriter().println(msg);
        } catch (IOException ex) {
            logger.error("error occured", ex);
        }
    }

    private void armResponse(HttpServletResponse response) {
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
        response.setDateHeader("Expires", 0);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        boolean verified = false;

        try {
            verified = VerifyRecaptcha.verify(gRecaptchaResponse, baseService.getProperties().getGoogleRecaptchaSecret());
        } catch (Exception ex) {
            logger.error("error occured", ex);
            sendRedirectToHome(response, request);
            return;
        }

        verified = true;

        if (!verified) {
            sendRedirectToHome(response, request);
            return;
        }

        String username = request.getParameter("j_username");
        String pswd = request.getParameter("j_password");

        if (Strings.isNullOrEmpty(username) || Strings.isNullOrEmpty(pswd)) {
            sendRedirectToHome(response, request);
            return;
        }

        //login check
        try {
            request.login(username, pswd);
            request.logout();
        } catch (ServletException ex) {
            logger.error("error occured", ex);
            sendRedirectToHome(response, request, "/error.html");
            return;
        }

        try {

            String loginDB = baseService.getLoginDB();
            String loginTable = baseService.getLoginTable();
            String usernameField = baseService.getLoginUsernameField();
            String emailField = baseService.getLoginEmailField();

            //exist check
            Map searchMap = new HashMap();
            searchMap.put(usernameField, username);
            Map userDBO = repositoryService.one(loginDB, loginTable, searchMap);

            if (userDBO == null) {
                sendRedirectToHome(response, request, "/loginJaas/errnouser");
                return;
            }

            LoginUser loginUser = new LoginUser(userDBO, usernameField, emailField);

            if (checkUserDataState(loginUser, response, request, loginDB, loginTable, usernameField, emailField)) {
                return;
            }

            //policy check
            byte ppolicuResult = verifyPPolicy(loginUser, request.getRemoteAddr());

            switch (ppolicuResult) {
                case 0://blocked
                    response.sendRedirect("fphi/blocked");
                    break;
                case 1://expired
                    response.sendRedirect("fphi?token="
                            .concat(FgtPswdTokenManager.instance().getToken(loginUser.getEmail(), 1)));
                    break;
                default:
                    try {
                    request.login(username, pswd);
                    //update policy
                    Map searchMap1 = new HashMap();
                    searchMap1.put(ProjectConstants.LDAP_UID, username);

                    Map dbo = repositoryService.one(ProjectConstants.CONFIG_DB, ProjectConstants.PPOLICY, searchMap1);

                    if (dbo != null) {
                        Ppolicy ppolicy = new Ppolicy.Builder()
                                .withMongoDBO(dbo)
                                .build();
                        repositoryService.updateMany(ProjectConstants.CONFIG_DB, ProjectConstants.PPOLICY,
                                ppolicy.createQuery(), ppolicy.createZeroTryCountSet());
                    }

                } catch (ServletException ex) {

                    logger.error("error occured", ex);

                    Map searchMap1 = new HashMap();
                    searchMap1.put(ProjectConstants.LDAP_UID, username);

                    Map dbo = repositoryService.one(ProjectConstants.CONFIG_DB, ProjectConstants.PPOLICY, searchMap1);

                    if (dbo != null) {
                        Ppolicy ppolicy = new Ppolicy.Builder()
                                .withMongoDBO(dbo)
                                .build();

                        ppolicy.incrementTryCount();

                        repositoryService.updateMany(ProjectConstants.CONFIG_DB, ProjectConstants.PPOLICY,
                                ppolicy.createQuery(), ppolicy.createTryCountSet());
                    }

                } finally {
                    sendRedirectToHome(response, request);
                }
            }
        } catch (Exception ex) {
            logger.error("error occured", ex);
            sendRedirectToHome(response, request);
            return;
        }

    }

    private void sendRedirectToHome(HttpServletResponse response, HttpServletRequest request, String path) {
        try {
            response.sendRedirect(request.getContextPath() + path);
        } catch (IOException ex) {
            logger.error("error occured", ex);
        }
    }

    private void sendRedirectToHome(HttpServletResponse response, HttpServletRequest request) {
        try {
            response.sendRedirect(request.getContextPath());
        } catch (IOException ex) {
            logger.error("error occured", ex);
        }
    }

    private byte verifyPPolicy(LoginUser loginUser, String reqRemoteAddr) throws Exception {

        if (loginUser == null) {
            throw new Exception("an authenticated username has no profile.");
        }

        Map queryPpolicy = new HashMap();
        queryPpolicy.put(ProjectConstants.LDAP_UID, loginUser.getUsername());

        Map dboPpolicy = repositoryService.one(ProjectConstants.CONFIG_DB, ProjectConstants.PPOLICY, queryPpolicy);

        if (dboPpolicy == null) {

            Ppolicy ppolicy = new Ppolicy.Builder()
                    .withDefault()
                    .withUid(loginUser.getUsername())
                    .withLastLoginIp(reqRemoteAddr)
                    .build();

            repositoryService.updateMany(ProjectConstants.CONFIG_DB, ProjectConstants.PPOLICY,
                    queryPpolicy,
                    ppolicy.toDocument(),
                    true);

            dboPpolicy = repositoryService.one(ProjectConstants.CONFIG_DB,
                    ProjectConstants.PPOLICY,
                    queryPpolicy);

        }

        if (dboPpolicy != null) {

            Ppolicy ppolicy = new Ppolicy.Builder()
                    .withMongoDBO(dboPpolicy)
                    .build();

            if (ppolicy.isLockExpired()) {
                repositoryService.updateMany(ProjectConstants.CONFIG_DB, ProjectConstants.PPOLICY,
                        ppolicy.createQuery(), ppolicy.createZeroTryCountSet());
            }

            if (ppolicy.hasBeenBlocked()) {// kullanıcıyı blocklandığı hakkında bilgilendir.
                return 0;
            } else if (ppolicy.isExpired()) {// kullanıcıyı parola güncelleme veya parola hatılatma sayfasına yonlendir.
                return 1;
            }
        }

        return 127;

    }

}
