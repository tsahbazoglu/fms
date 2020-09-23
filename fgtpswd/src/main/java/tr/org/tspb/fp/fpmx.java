package tr.org.tspb.fp;

import tr.org.tspb.cdi.qualifier.BasePolicyProvider;
import com.google.common.base.Strings;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;

/**
 *
 * @author Telman Şahbazoğlu
 */
@WebServlet(name = "FgtPswdMail", urlPatterns = {"/fpmx/*"})
public class fpmx extends HttpServlet {

    private final String JMS_EMAIL_QUEUE_CONN_FACTORY = "jms/fmsEmailQueueConnectionFactory";
    private final String JMS_EMAIL_QUEUE = "jms/fmsEmailQueue";

    @Inject
    @BasePolicyProvider
    private PPolicyProvider cdiPolicyProvider;

    @Inject
    private Logger logger;

    @Resource(lookup = "connFactory", mappedName = JMS_EMAIL_QUEUE_CONN_FACTORY)
    private QueueConnectionFactory queueConnectionFactory;

    @Resource(lookup = "jmsQueue", mappedName = JMS_EMAIL_QUEUE)
    private Queue queue;

    private final String PARAM_EMAIL = "email";
    private final String PARAM_RECAPTCHA = "g-recaptcha-response";

    private final Set<String> allowedPostAtrrs = new HashSet<>(Arrays.asList(PARAM_EMAIL, PARAM_RECAPTCHA));

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ERROR: Get Method is Not Allowed");
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
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "error : fpmx : post method is not allowed with param : ".concat(matchedParam));
            return;
        }

        String gRecaptchaResponse = request.getParameter(PARAM_RECAPTCHA);
        String email = request.getParameter(PARAM_EMAIL);

        String redirectPathInfo = "";
        if (!fphi.TEST && !VerifyRecaptcha.verify(gRecaptchaResponse)) {
            redirectPathInfo = "/fphi/wrongRobot";
        } else if (Strings.isNullOrEmpty(email) || !emailValidations(email)) {
            redirectPathInfo = "/fphi/wrongEmail";
        } else {

            PPolicyProvider ppp = getPPolicyProivder();
            if (ppp == null) {
                response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, "PPolicy Provider not found");
                return;
            }

            if (ppp.emailCount(email) == 1) {
                String token = FgtPswdTokenManager.instance().getToken(email, 1);
                String link = request.getRequestURL().toString().concat("?token=").concat(token).replace("fpmx", "fphi");
                String emailContent = MyWriteUtil.instance().createEmailContent(link);

                try {
                    ppp.sendMail("TSPB : UYS : Parola Hatırlatma", emailContent, email, queueConnectionFactory, queue);
                    log(emailContent);
                } catch (MessagingException ex) {
                    logger.error("error occured", ex);
                }
            } else {
                log(email.concat(" no email or more then one"));
            }

            redirectPathInfo = "/fphi/informUser";
        }
        response.sendRedirect(request.getContextPath().concat(redirectPathInfo));
    }

    private boolean emailValidations(String value) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    private static ServiceLoader<PPolicyProvider> serviceLoader = ServiceLoader.load(PPolicyProvider.class);

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
        // PPolicyProvider implementasyonunu verecek olan sınıfının tam adıyla aynı olacak şekilde 
        // dosya oluştulumalı ve bu dosya içinede tam isim yazılmalı
        for (PPolicyProvider ppp : serviceLoader) {
            return ppp;
        }

        return null;
    }

}
