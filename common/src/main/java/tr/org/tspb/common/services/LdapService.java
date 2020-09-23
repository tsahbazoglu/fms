package tr.org.tspb.common.services;

import com.mongodb.gridfs.GridFSDBFile;
import static tr.org.tspb.constants.ProjectConstants.CODE;
import static tr.org.tspb.constants.ProjectConstants.COLLECTION_SYSTEM_MESSAGES;
import static tr.org.tspb.constants.ProjectConstants.CONFIG_DB;
import static tr.org.tspb.constants.ProjectConstants.FORM_DB;
import tr.org.tspb.exceptions.LdapException;
import tr.org.tspb.util.crypt.StrongPasswordGenerator;
import tr.org.tspb.util.stereotype.MyServices;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;
import org.bson.Document;
import tr.org.tspb.pojo.MyLdapUser;
import tr.org.tspb.pojo.UserDetail;

/**
 *
 * @author Telman Şahbazoğlu
 */
@MyServices
public class LdapService extends AbstractSrv {

    @Inject
    protected MailService mailService;

    @Inject
    protected BaseService baseService;

    @Resource(lookup = "ldap/openldap")
    LdapContext ctxLocal;

    private static final String LDAP_GIVEN_NAME = "givenName";
    private static final String LDAP_MAIL = "mail";
    private static final String LDAP_CN = "cn";
    private static final String LDAP_SN = "sn";
    private static final String LDAPUSERWHATISIT = "userPassword";
    private static String UID = "uid";

    private class Updater {

        private DirContext context;
        private String uid;
        private String email;
        private String pswd;
        private String userBaseDn;

        public Updater(String userBaseDn) {
            this.userBaseDn = userBaseDn;
        }

        public Updater withContext(DirContext context) {
            this.context = context;
            return this;
        }

        public Updater withUid(String uid) {
            this.uid = uid;
            return this;
        }

        public Updater withPswd(String pswd) {
            this.pswd = pswd;
            return this;
        }

        public Updater withEmail(String email) {
            this.email = email;
            return this;
        }

        public Updater create() {
            return this;
        }

        public Updater update() throws NamingException, LdapException {

            findUserByUid(uid);

            String userDN = MessageFormat.format("uid={0}," + userBaseDn, uid);

            BasicAttributes attributes = new BasicAttributes();

            if (pswd != null) {
                attributes.put(new BasicAttribute(LDAPUSERWHATISIT, pswd));
            }

            if (email != null) {
                attributes.put(new BasicAttribute(LDAP_MAIL, email));
            }

            context.modifyAttributes(userDN, DirContext.REPLACE_ATTRIBUTE, attributes);

            return this;

        }

        public void sendMail() {
            try {
                LdapService.this.sendMail(uid, email, pswd);
            } catch (IOException ex) {
                logger.error("error occured", ex);
            } catch (MessagingException ex) {
                logger.error("error occured", ex);
            }
        }

    }

    public void updatePswdAndNotifyUser(String uid) throws LdapException {
        try {
            new Updater(baseService.getProperties().getLdapUsersDn())
                    .withUid(uid)
                    .withPswd(StrongPasswordGenerator.generateStrongPassword())
                    .withContext(getContext())
                    .update()
                    .sendMail();
        } catch (NamingException ex) {
            throw new LdapException("could not update pswd and email on ldap", ex);
        }
    }

    public void updatePswd(String uid) throws LdapException {
        try {
            new Updater(baseService.getProperties().getLdapUsersDn())
                    .withUid(uid)
                    .withPswd(StrongPasswordGenerator.generateStrongPassword())
                    .withContext(getContext())
                    .update();
        } catch (NamingException ex) {
            throw new LdapException("could not update pswd on ldap", ex);
        }
    }

    public void updatePswd(String uid, String pswd) throws LdapException {
        try {
            new Updater(baseService.getProperties().getLdapUsersDn())
                    .withUid(uid)
                    .withPswd(pswd)
                    .withContext(getContext())
                    .update();
        } catch (NamingException ex) {
            throw new LdapException("could not update pswd and email on ldap", ex);
        }
    }

    public void createUser(String uid, String fn, String ln, String email, String pswd) throws LdapException {

        MyLdapUser myLdapUser = null;

        try {
            myLdapUser = findUserByUid(uid);
        } catch (LdapException ex) {
            //
        }

        if (myLdapUser != null) {
            throw new LdapException("user already exists on ldap");
        }

        try {
            DirContext context = getContext();
            createUser(uid, fn, ln, email, pswd, context);
        } catch (NamingException ex) {
            throw new LdapException("could not create user on ldap", ex);
        }

    }

    private void createUser(String uid, String firstName, String lastName, String email, String passwordNew, DirContext context) throws NamingException {
        String userDN = createUserDN(uid);

        String fn = firstName == null ? "" : firstName;
        String ln = lastName == null ? "" : lastName;
        String cn = String.format("%s %s", fn, ln);

        Attributes attributes = new BasicAttributes();

        Attribute objectClasses = createObjectClasses();

        attributes.put(objectClasses);
        attributes.put(new BasicAttribute(UID, uid));
        attributes.put(new BasicAttribute(LDAP_CN, cn));
        attributes.put(new BasicAttribute(LDAP_GIVEN_NAME, cn));
        attributes.put(new BasicAttribute(LDAP_SN, cn));
        attributes.put(new BasicAttribute(LDAP_MAIL, email));
        attributes.put(new BasicAttribute(LDAPUSERWHATISIT, passwordNew));

        context.createSubcontext(userDN, attributes);
    }

    private MyLdapUser findUser(Map<String, Object> searchMap) throws LdapException {

        final Attributes attr = new BasicAttributes(true); // ignore case
        for (String key : searchMap.keySet()) {
            attr.put(new BasicAttribute(key, searchMap.get(key)));
        }
        DirContext context = null;
        try {
            context = getContext();

            final NamingEnumeration answer = context.search(baseService.getProperties().getLdapUsersDn(), attr);
            if (answer != null) {
                try {
                    final SearchResult sr = (SearchResult) answer.next();
                    Attribute ldapUidAttr = sr.getAttributes().get(UID);
                    Attribute emailAttr = sr.getAttributes().get(LDAP_MAIL);

                    MyLdapUser myLdapUser = new MyLdapUser();
                    myLdapUser.setUid(ldapUidAttr.get().toString());
                    if (emailAttr != null) {
                        myLdapUser.setEmail(emailAttr.get().toString());
                    }
                    return myLdapUser;
                } catch (Exception ex) {
                    closeContext(context);
                    throw new LdapException("could not found user on ldap", ex);
                }
            }
        } catch (NamingException ex) {
            closeContext(context);
            throw new LdapException("could not found user on ldap", ex);
        }
        return null;
    }

    public UserDetail getUserDetailByUserID(String userID) {

        UserDetail userDetail = new UserDetail();

        userDetail.setUsername(userID);
        userDetail.setFirstName(getAttribute(userID, LDAP_GIVEN_NAME));
        userDetail.setLastName(getAttribute(userID, LDAP_SN));
        userDetail.setCommonName(getAttribute(userID, LDAP_CN));

        return userDetail;
    }

    public void removeUserToRole(String username, List<String> groupNames) throws LdapException {
        if (findUserByUid(username) != null) {
            try {
                DirContext context = getContext();
                for (String role : groupNames) {
                    if (isRoleExist(role)) {
                        ModificationItem[] mods = new ModificationItem[1];
                        Attribute attribute = new BasicAttribute("uniqueMember", createUserDN(username));
                        mods[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, attribute);
                        String groupDnPath = createGroupDN(role);
                        context.modifyAttributes(groupDnPath, mods);
                    }
                }
                closeContext(context);
            } catch (NamingException ex) {
                throw new LdapException("could not remove from role", ex);
            }
        }
    }

    public void addUserToRole(String username, List<String> groupNames) throws LdapException {

        if (findUserByUid(username) != null) {
            try {
                DirContext context = getContext();
                for (String role : groupNames) {
                    if (isRoleExist(role)) {
                        ModificationItem[] mods = new ModificationItem[1];
                        Attribute attribute = new BasicAttribute("uniqueMember", createUserDN(username));

                        mods[0] = new ModificationItem(DirContext.ADD_ATTRIBUTE, attribute);

                        String groupDnPath = createGroupDN(role);

                        context.modifyAttributes(groupDnPath, mods);
                    } else {
                        throw new LdapException(role.concat(" does not exists"));
                    }
                }
                closeContext(context);
            } catch (NamingException ex) {
                throw new LdapException("could not remove from role", ex);
            }
        }
    }

    private Attribute createObjectClasses() {
        Attribute objectClasses = new BasicAttribute("objectClass");
        objectClasses.add("person");
        objectClasses.add("organizationalPerson");
        objectClasses.add("inetOrgPerson");
        objectClasses.add("top");
        return objectClasses;
    }

    private String createUserDN(String username) {
        return MessageFormat.format("uid={0}," + baseService.getProperties().getLdapUsersDn(), username);
    }

    private String createGroupDN(String groupname) {
        return MessageFormat.format("cn={0}," + baseService.getProperties().getLdapRolesDn(), groupname);
    }

    private String getAttribute(final String uid, final String attribute) {

        if (uid == null || attribute == null) {
            // We do this because of OpenDS dummy behavior 
            // It returns the first user in Users even in case of uid = null
            return null;
        }

        final Attributes matchAttrs = new BasicAttributes(true); // ignore case

        matchAttrs.put(new BasicAttribute(UID, uid));

        String result = null;

        try {
            NamingEnumeration answer = ctxLocal.search(baseService.getProperties().getLdapUsersDn(), matchAttrs);

            final SearchResult sr = (SearchResult) answer.next();

            result = (String) sr.getAttributes().get(attribute).get();
        } catch (final NamingException e) {
            logger.warn("NamingException occurred while trying to fetch attribute: ", e);
            result = String.format("No Record for UID %s", uid);
        } catch (final NullPointerException e) {
            logger.warn("NullPointerException occurred while trying to fetch attribute: ", e);
            result = String.format("No Record for UID %s", uid);
        }
        return result;
    }

    private boolean isRoleExist(String role) {

        boolean result = false;

        try {

            final Attributes attr = new BasicAttributes(true);
            attr.put(new BasicAttribute(LDAP_CN, role));

            DirContext context = getContext();
            final NamingEnumeration searchResults = context.search(baseService.getProperties().getLdapRolesDn(), attr);
            while (searchResults != null && searchResults.hasMoreElements()) {
                result = true;
                searchResults.next();
            }

        } catch (NamingException ex) {
            logger.info("the role had not been found on LDAP : ".concat(role));
        }

        return result;
    }

    private void closeContext(DirContext context) {
        try {
            if (context != null) {
                context.close();
            }
        } catch (NamingException ex) {
            logger.error("error occured", ex);
        }
    }

    private DirContext getContext() throws NamingException {

        Hashtable env = new Hashtable();

        DirContext initial;

        BaseService.AppProperties properties = baseService.getProperties();

        String ldapAdmin = properties.getLdapAdmin();
        String ldapPswd = properties.getLdapPswd();
        String ldapUrl = properties.getLdapUrl();

        if (ldapPswd == null || ldapUrl == null || ldapAdmin == null) {
            throw new RuntimeException("Ldap Central url and password is resolved to null");
        }

        env.put(Context.SECURITY_PRINCIPAL, ldapAdmin);
        env.put(Context.SECURITY_CREDENTIALS, ldapPswd);

        initial = new InitialDirContext(env);

        return (DirContext) initial.lookup(ldapUrl);
    }

    public MyLdapUser findUserByUid(String username) throws LdapException {
        Map<String, Object> filter = new HashMap<>();
        filter.put(UID, username);
        return findUser(filter);
    }

    public List<String> findAllRoles() throws LdapException {

        List<String> allRoles = new ArrayList<>();

        final Attributes attr = new BasicAttributes(true); // ignore case

        DirContext context = null;
        try {
            context = getContext();

            final NamingEnumeration answer = context.search(baseService.getProperties().getLdapRolesDn(), attr);
            if (answer != null) {
                try {
                    while (answer.hasMore()) {
                        final SearchResult sr = (SearchResult) answer.next();
                        Attribute ldapUidAttr = sr.getAttributes().get("cn");
                        //allRoles.add(ldapUidAttr.get().toString());
                        NamingEnumeration cnValue = ldapUidAttr.getAll();
                        while (cnValue.hasMore()) {
                            allRoles.add(cnValue.next().toString());
                        }
                    }
                } catch (Exception ex) {
                    closeContext(context);
                    throw new LdapException("could not found user on ldap", ex);
                }
            }
        } catch (NamingException ex) {
            closeContext(context);
            throw new LdapException("could not found user on ldap", ex);
        }
        return allRoles;

    }

    public void sendMail(String uid, String email, String password) throws IOException, MessagingException {
        String url = baseService.getWebContext();

        Document doc = mongoDbUtil.findOne(CONFIG_DB, COLLECTION_SYSTEM_MESSAGES, new Document(CODE, "FORGET_PASSWORD"));

        if (doc != null && doc.get("contentQuery") != null) {

            Document fileQuery = (Document) doc.get("contentQuery");

            String db = (String) fileQuery.get(FORM_DB);
            String filename = (String) fileQuery.get("filename");

            List<GridFSDBFile> gridFSDBFiles = mongoDbUtil.findFiles(db, filename);;// gridFS.find(filename);

            StringWriter sw = new StringWriter();

            InputStreamReader streamReader = new InputStreamReader(gridFSDBFiles.get(0).getInputStream());

            int c;
            while ((c = streamReader.read()) != -1) {
                sw.append((char) c);
            }

            String content = MessageFormat.format(sw.toString(), url, uid, password);

            byte[] attachment = mailService.createAttachment(doc, db);

            mailService.sendMail("TSPAKB - Üye Yönetim Sistemi hk.", content, email, attachment);

        }
    }

}
