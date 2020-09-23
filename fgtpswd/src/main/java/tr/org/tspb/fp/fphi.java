package tr.org.tspb.fp;

import com.google.common.base.Strings;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Telman Şahbazoğlu
 */
@WebServlet(name = "FgtPswdHi", urlPatterns = {"/fphi/*"})
public class fphi extends HttpServlet {

    public static final boolean TEST = true;
    private final String PARAM_TOKEN = "token";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Enumeration<String> enums = request.getParameterNames();
        boolean err = false;
        String matchedParam = "";

        while (enums.hasMoreElements()) {
            matchedParam = enums.nextElement();
            if (err = !PARAM_TOKEN.equals(matchedParam)) {
                break;
            }
        }

        if (err) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ERROR : fphi : get is not allowed with param : ".concat(matchedParam));
            return;
        }

        String tokenLong = request.getParameter(PARAM_TOKEN);

        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
        response.setDateHeader("Expires", 0);

        String url = request.getRequestURL().toString();
        String uri = request.getRequestURI();

        String homeAddr = url.substring(0, url.indexOf(uri));

        if (Strings.isNullOrEmpty(tokenLong)) {
            try (PrintWriter out = response.getWriter()) {

                try {
                    String pathInfo = request.getPathInfo();

                    if (pathInfo == null) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "error : fphi get is not allowed with param : ".concat(PARAM_TOKEN));
                    } else {
                        HttpSession httpSession;
                        switch (pathInfo) {
                            case "/tspb":
                                MyWriteUtil.instance("TSPB");
                                MyWriteUtil.instance().writeWelcomePage(out, null, null, "/fpmx", homeAddr);
                                break;
                            case "/tdub":
                                MyWriteUtil.instance("TDUB");
                                MyWriteUtil.instance().writeWelcomePage(out, null, null, "/fpmx", homeAddr);
                                break;
                            case "/wrongEmail":
                                MyWriteUtil.instance().writeWelcomePage(out, null, "e-posta alanı yalnış biçimlendirlmiş", "/fpmx", homeAddr);
                                break;
                            case "/wrongRobot":
                                MyWriteUtil.instance().writeWelcomePage(out, null, "'Ben Robot Değilim' işlemi tamamlanmadı.", "/fpmx", homeAddr);
                                break;
                            case "/informUser":
                                MyWriteUtil.instance().writeNotifyUser(out, null, homeAddr);
                                break;
                            case "/update":
                                httpSession = request.getSession(false);
                                String shortToken = (String) httpSession.getAttribute("shortToken");
                                httpSession.removeAttribute("shortToken");
                                if (shortToken == null) {
                                    MyWriteUtil.instance().writeInvalidShortTermTokenInfo(out, homeAddr);
                                } else {
                                    MyWriteUtil.instance().writeUpdatePswd(out, shortToken, true, "/fpop", homeAddr);
                                }
                                break;
                            case "/wrongStt":
                                MyWriteUtil.instance().writeInvalidShortTermTokenInfo(out, homeAddr);
                                break;
                            case "/updateOk":
                                MyWriteUtil.instance().writeUpdatePswdOk(out, homeAddr);
                                break;
                            case "/blocked":
                                MyWriteUtil.instance().writeBlocked(out, homeAddr);
                                break;
                            case "/updateError":
                                httpSession = request.getSession(false);
                                String attrUpdateErrorMsg = (String) httpSession.getAttribute("attrUpdateErrorMsg");
                                httpSession.removeAttribute("attrUpdateErrorMsg");
                                MyWriteUtil.instance().writeUpdatePswdFail(out, homeAddr, attrUpdateErrorMsg, homeAddr);
                                break;
                            default:
                                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "error : fphi get is not allowed with param : ".concat(PARAM_TOKEN));
                                break;
                        }
                    }
                } catch (Exception ex) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "error occured");
                }

            }
        } else if (tokenLong.length() > 123) { // token convention check
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "error : fphi get is not allowed with param : ".concat(PARAM_TOKEN));
        } else if (!FgtPswdTokenManager.instance().isTokenValid(tokenLong, 1)) {
            try (PrintWriter out = response.getWriter()) {
                MyWriteUtil.instance().writeInvalidTokenInfo(out, homeAddr);
            }
        } else {
            // uzun süreli tokendan email adresini elde et ve tokeni yok et
            String email = (String) FgtPswdTokenManager.instance().getTokenData(tokenLong, 1);
            FgtPswdTokenManager.instance().invalidateToken(tokenLong, 1);

            // yeni kısa süreli token ile update sayfasını çiz
            try (PrintWriter out = response.getWriter()) {
                MyWriteUtil.instance().writeUpdatePswd(out, FgtPswdTokenManager.instance().getToken(email, 0), false, "/fpop", homeAddr);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ERROR : hi exception : post is not allowed");
    }

}
