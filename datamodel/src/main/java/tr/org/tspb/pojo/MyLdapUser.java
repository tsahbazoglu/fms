package tr.org.tspb.pojo;

import static tr.org.tspb.constants.ProjectConstants.MONGO_LDAP_UID;
import static tr.org.tspb.constants.ProjectConstants.NAME;
import java.util.Map;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class MyLdapUser {

    private String uid;
    private String ldapUID;
    private String email;
    private String name;
    private String firstname;
    private String lastname;
    private String password;
    private String cn;

    public MyLdapUser(String uid) {
        this.uid = uid;
    }

    public MyLdapUser(Map dbo) {
        uid = (String) dbo.get(MONGO_LDAP_UID);
        firstname = (String) dbo.get(NAME);
        lastname = (String) dbo.get(NAME);
    }

    public MyLdapUser() {
    }

    public void modify() {
        if (firstname == null) {
            firstname = name;
        }
        if (lastname == null) {
            lastname = name;
        }
        if (uid == null) {
            uid = ldapUID;
        }

        cn = firstname;
        if (lastname == null || lastname.trim().isEmpty() || lastname.equals(firstname)) {
            lastname = firstname;
        } else {
            cn = cn.concat(" ").concat(lastname);
        }

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getLdapUID() {
        return ldapUID;
    }

    public void setLdapUID(String ldapUID) {
        this.ldapUID = ldapUID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCn() {
        return cn;
    }

    public void setCn(String cn) {
        this.cn = cn;
    }

}
