package tr.org.tspb.dao;

import java.util.Map;
import tr.org.tspb.exceptions.NullNotExpectedException;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class LoginUser {

    private final String username;
    private final String email;

    public LoginUser(Map dbo, String usernameField, String emailField) throws Exception {
        this.username = dbo.get(usernameField).toString();
        if (dbo.get(emailField) == null) {
            throw new NullNotExpectedException(emailField);
        }
        this.email = dbo.get(emailField).toString();
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

}
