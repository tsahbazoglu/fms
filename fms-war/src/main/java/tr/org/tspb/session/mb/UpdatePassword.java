package tr.org.tspb.session.mb;

import tr.org.tspb.common.services.LoginController;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Hashtable;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Named;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.*;
import static tr.org.tspb.constants.ProjectConstants.*;

/**
 *
 * @author Telman Şahbazoğlu
 */
@Named("updatePassword")
@SessionScoped
public class UpdatePassword implements Serializable {

    public final static String USERS_OU = "ou=Users,dc=example,dc=com";

    public static String createUserDN(String username) {
        return MessageFormat.format("uid={0}," + USERS_OU, username);
    }

    // @Resource(name = "ldap/openldap")
    // private LdapContext ctxLocal;
    @Resource(lookup = "fms/environment")
    private String environment;
    @Resource(name = "fms/productProperties")
    private Properties productProperties;
    @Resource(name = "fms/testProperties")
    private Properties testProperties;
    //
    private String passwordNew;
    private Properties serverProperties;

    public Properties getServerProperties() {
        if (serverProperties == null) {
            switch (environment) {
                case PRODUCT:
                    serverProperties = productProperties;
                    break;
                case TEST:
                    serverProperties = testProperties;
                    break;
                default:
                    return null;
            }
        }
        return serverProperties;
    }

    //
    //Have you set SECURITY_AUTHENTICATION mode that you are using to "simple","strong" or "none"?
    //Try using "none", or "simple" and see if that helps.
//    Context.SECURITY_AUTHENTICATION ("java.naming.security.authentication"). 
//Specifies the authentication mechanism to use. For the Sun LDAP service provider, this can be one of the following strings: "none", "simple", sasl_mech, where sasl_mech is a space-separated list of SASL mechanism names. See the next section for a description of these strings.
//
//Context.SECURITY_PRINCIPAL ("java.naming.security.principal"). 
//Specifies the name of the user/program doing the authentication and depends on the value of the Context.SECURITY_AUTHENTICATION property. See the next few sections in this lesson for details and examples.
//
//Context.SECURITY_CREDENTIALS ("java.naming.security.credentials"). 
//Specifies the credentials of the user/program doing the authentication and depends on the value of the Context.SECURITY_AUTHENTICATION property. See the next few sections in this lesson for details and examples.
    public String update() {
        LoginController loginMB = CDI.current().select(LoginController.class).get();
        String userDN = createUserDN(loginMB.getUsername());
        updatePassword(userDN, passwordNew);
        return null;
    }

    private String updatePassword(String userDN, String passwordNew) {

        try {

//        ctx.modifyAttributes(//
//                createUserDN(loginMB.getUsername())//
//                , DirContext.REPLACE_ATTRIBUTE//
//                , new BasicAttributes("userPassword", passwordNew));
            //  This is the error you get when trying to set the password in Active Directory over a non-SSL connection. Try your code again without the password line.
            Hashtable env = new Hashtable();

            String ldapAdmin = getServerProperties().getProperty(LDAP_ADMIN);
            String ldapPswd = getServerProperties().getProperty(LDAP_WHATISIT);
            String ldapUrl = getServerProperties().getProperty(LDAP_URL);

            env.put(Context.SECURITY_PRINCIPAL, ldapAdmin);
            env.put(Context.SECURITY_CREDENTIALS, ldapPswd);

            DirContext initial = new InitialDirContext(env);
            DirContext context = (DirContext) initial.lookup(ldapUrl);

            BasicAttributes attributes = new BasicAttributes();
            attributes.put(new BasicAttribute("userPassword", passwordNew));

            //attributes.put(new BasicAttribute("mail", "shahbazov@gmail.com"));
            context.modifyAttributes(userDN, DirContext.REPLACE_ATTRIBUTE, attributes);
            //at that point the old passord is still work untill we redeploy project.
            // it happen even if we chage password via opends interface
            //it seems the realm is using cache
            //How to clear caches.
            //
            //com.sun.enterprise.security.auth.realm.ldap.ldaprealm
        } catch (NamingException ex) {
            Logger.getLogger(UpdatePassword.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String getPasswordNew() {
        return passwordNew;
    }

    public void setPasswordNew(String passwordNew) {
        this.passwordNew = passwordNew;
    }

}
