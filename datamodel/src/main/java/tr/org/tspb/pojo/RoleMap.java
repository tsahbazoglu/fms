package tr.org.tspb.pojo;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class RoleMap extends HashMap implements Serializable {

    /**
     * Creates a new instance of RoleHandlerMB
     * @param loggedUserRoles
     */
    public void setLoggedUserRoles(List<String> loggedUserRoles) {
        if (loggedUserRoles != null) {
            for (String roleName : loggedUserRoles) {
                this.put(roleName, true);
            }
        }
    }

    @Override
    public Boolean get(Object role) {
        return isUserInRole(role);
    }

    public Boolean checkForFunction() {
        return null;
    }

    /**
     *
     * @param commaSplittedRoles
     * @return
     */
    public Boolean isUserInRole(Object commaSplittedRoles) {
        if (commaSplittedRoles != null) {
            if (commaSplittedRoles instanceof List) {
                for (String string : (Iterable<? extends String>) commaSplittedRoles) {
                    if (keySet().contains(string)) {
                        return Boolean.TRUE;
                    }
                }
            } else if (commaSplittedRoles instanceof String) {
                String[] roles = ((String) commaSplittedRoles).split("[,]+");
                for (String string : roles) {
                    if (keySet().contains(string)) {
                        return Boolean.TRUE;
                    }
                }
            }
        }
        return Boolean.FALSE;
    }

    public Boolean isUserJustInRole(Object commaSplittedRoles) {
        if (commaSplittedRoles != null) {
            List<String> list = Arrays.asList(((String) commaSplittedRoles).split("[,]+"));
            for (Object string : keySet()) {
                if (list.indexOf(string) < 0) {
                    return Boolean.FALSE;
                }
            }
        }
        return Boolean.TRUE;
    }
}
