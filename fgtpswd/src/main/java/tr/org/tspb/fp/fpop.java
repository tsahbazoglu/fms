package tr.org.tspb.fp;

import tr.org.tspb.cdi.qualifier.BasePolicyProvider;
import com.google.common.base.Strings;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.regex.Pattern;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Telman Şahbazoğlu
 */
@WebServlet(name = "FgtPswdOp", urlPatterns = {"/fpop/*"})
public class fpop extends HttpServlet {

    private static ServiceLoader<PPolicyProvider> serviceLoader = ServiceLoader.load(PPolicyProvider.class);

    @Inject
    @BasePolicyProvider
    private PPolicyProvider cdiPolicyProvider;

    private final String PARAM_UPDATE_PSWD_TOKEN = "updatePswdToken";
    private final String PARAM_PSWD = "pswd";
    private final String PARAM_RE_PSWD = "re-pswd";
    private final String PARAM_RECAPTCHA = "g-recaptcha-response";

    private final Set<String> allowedPostAtrrs = new HashSet<>(Arrays.asList(PARAM_UPDATE_PSWD_TOKEN, PARAM_PSWD, PARAM_RE_PSWD, PARAM_RECAPTCHA));

    private Pattern strongRegex = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#%\\*\\?\\+\\-])(?=.{8,})");
    private Pattern mediumRegex = Pattern.compile("^(((?=.*[a-z])(?=.*[A-Z]))|((?=.*[a-z])(?=.*[0-9]))|((?=.*[A-Z])(?=.*[0-9])))(?=.{6,7})");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ERROR : fpop : get is not allowed");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Enumeration<String> enums = request.getParameterNames();
        boolean err = false;
        String matchedParam = "";

        while (enums.hasMoreElements()) {
            matchedParam = enums.nextElement();
            if (err = !allowedPostAtrrs.contains(matchedParam)) {
                break;
            }
        }
        if (err) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ERROR : fpop : post method is not allowed with param : ".concat(matchedParam));
        }

        String gRecaptchaResponse = request.getParameter(PARAM_RECAPTCHA);
        String updatePswdToken = request.getParameter(PARAM_UPDATE_PSWD_TOKEN);
        String pswd = request.getParameter(PARAM_PSWD);
        String repswd = request.getParameter(PARAM_RE_PSWD);

        if (Strings.isNullOrEmpty(updatePswdToken)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "error : fpop : suspicious action detected");
        } else if (!FgtPswdTokenManager.instance().isTokenValid(updatePswdToken, 0)) {
            response.sendRedirect(request.getContextPath() + "/fphi/wrongStt");
        } else if ((fphi.TEST || VerifyRecaptcha.verify(gRecaptchaResponse))
                && !Strings.isNullOrEmpty(pswd)
                && !Strings.isNullOrEmpty(repswd)
                && pswd.equals(repswd)
                && validatePswdStrong(pswd)) {

            String email = (String) FgtPswdTokenManager.instance().getTokenData(updatePswdToken, 0);
            FgtPswdTokenManager.instance().invalidateToken(updatePswdToken, 0);

            PPolicyProvider ppp = getPPolicyProivder();
            if (ppp == null) {
                response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, "PPolicy Provider not found");
                return;
            }

            try {
                ppp.updatePswd(email, repswd);
                ppp.updatePpolicy(email, request.getRemoteAddr());
            } catch (Exception ex) {
                request.getSession().setAttribute("attrUpdateErrorMsg", ex.getLocalizedMessage());
                response.sendRedirect(request.getContextPath() + "/fphi/updateError");
                return;
            }

            response.sendRedirect(request.getContextPath() + "/fphi/updateOk");

        } else {
            //tekrar yeni kısa süreli token oluştur
            String email = (String) FgtPswdTokenManager.instance().getTokenData(updatePswdToken, 0);
            request.getSession().setAttribute("shortToken", FgtPswdTokenManager.instance().getToken(email, 0));
            response.sendRedirect(request.getContextPath() + "/fphi/update");
        }

    }

//    private boolean validatePswdStrong(String pswd) {
//        var strongRegex = new RegExp("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\$%\^&\*])(?=.{8,})");
//        return true;
//    }
    private boolean validatePswdStrong(String pswd) {
        return strongRegex.matcher(pswd).find();
    }

    private boolean validateToken(String token) {
        String pattern = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,16}";
        return token.matches(pattern);
    }

    /**
     * Uygulama tarafından tanımlanmış olan PPolicyProvider'ı döndürür.
     *
     * @return
     */
    private PPolicyProvider getPPolicyProivder() {

        if (true) {
            return cdiPolicyProvider;
        }

        // bu modulu import edecek olan maven projede META-INF/services klasörü altında 
        // PPolicyProvider implementasyonunu verecek olan sınıfının tam adıyla aynı 
        // olacak şekilde dosya oluştulumalı ve bu dosya içinede tam isim yazılmalı
        for (PPolicyProvider ppp : serviceLoader) {
            return ppp;
        }

        return null;
    }

}
