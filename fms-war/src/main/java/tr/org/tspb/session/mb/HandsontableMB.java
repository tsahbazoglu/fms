package tr.org.tspb.session.mb;

import static tr.org.tspb.constants.ProjectConstants.*;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author Telman Şahbazoğlu
 */
@Named
@SessionScoped
public class HandsontableMB implements Serializable {

    private String name;
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //getter and setter methods for name and email
    public String registerAction() {
        return RESULT;
    }
}
