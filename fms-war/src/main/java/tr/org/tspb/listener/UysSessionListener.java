package tr.org.tspb.listener;

import java.util.Map;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 *
 * @author Telman Şahbazoğlu
 */
@WebListener()
public class UysSessionListener implements HttpSessionListener {

    private static int totalActiveSessions;

    public static int getTotalActiveSession() {
        return totalActiveSessions;
    }

    @Override
    public void sessionCreated(HttpSessionEvent sessionEvent) {
        totalActiveSessions++;
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent sessionEvent) {
        totalActiveSessions--;
        HttpSession httpSession = sessionEvent.getSession();
        Map loggedUsers = (Map) httpSession.getServletContext().getAttribute("loggedUsers");
        String loggedUserLdapUID = (String) httpSession.getAttribute("loggedUser");
        if (loggedUsers != null) {
            loggedUsers.remove(loggedUserLdapUID);
        }
    }
}
