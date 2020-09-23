package tr.org.tspb.servlet;

import tr.org.tspb.dao.Ppolicy;
import tr.org.tspb.cdi.qualifier.BasePolicyProvider;
import java.util.Date;
import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import tr.org.tspb.fp.FgtPswdTokenManager;
import tr.org.tspb.fp.PPolicyProvider;
import tr.org.tspb.common.services.BaseService;
import tr.org.tspb.common.services.MailService;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import tr.org.tspb.common.services.LdapService;
import tr.org.tspb.service.RepositoryService;
import tr.org.tspb.constants.ProjectConstants;

/**
 *
 * @author Telman Şahbazoğlu
 */
@SessionScoped
@BasePolicyProvider
public class PPolicyProviderImpl implements PPolicyProvider, Serializable {

    @Inject
    private LdapService ldapService;

    @Inject
    private MailService mailService;

    @Inject
    private BaseService baseService;

    @Inject
    private RepositoryService repositoryService;

    @Produces
    public void init() {
    }

    @Override
    public void sendMail(String subject, String message, String recipients,
            QueueConnectionFactory queueConnectionFactory, Queue queue
    ) throws AddressException, MessagingException {

        String env = (String) FgtPswdTokenManager.instance().getTokenData("env-forget-pswd-email", 1);

        if ("TEST".equals(env)) {
            String emails = (String) FgtPswdTokenManager.instance().getTokenData("test-forget-pswd-email", 1);
            if (emails != null) {
                recipients = emails;
            }
            subject = "TSPB : UYS : *** TEST *** : Parola Hatırlatma";
        }

        Map query = new HashMap();
        query.put(ProjectConstants.EMAIL, recipients);

        long count = repositoryService.count(baseService.getProperties().getLoginDB(), baseService.getProperties().getLoginTable(), query);

        if (count == 1) {
            mailService.sendMail(subject, message, recipients);
        } else {
            System.out.println("no email or more then one");
            System.out.println(subject);
        }

    }

    @Override
    public void updatePpolicy(String shortTokenEmail, String lastLoginIP) {

        Map query = new HashMap();
        query.put(ProjectConstants.EMAIL, shortTokenEmail);

        if (repositoryService.count(baseService.getProperties().getLoginDB(), baseService.getProperties().getLoginTable(), query) == 1) {

            Map document = repositoryService.one(baseService.getProperties().getLoginDB(), baseService.getProperties().getLoginTable(), query);

            String ldapUID = (String) document.get(baseService.getProperties().getLoginUsernameField());

            if (ldapUID != null) {
                Date today = new Date();

                Ppolicy ppolicy = new Ppolicy.Builder()
                        .withDefault()
                        .withUid(ldapUID)
                        .withLastLoginTime(today)
                        .withChangePswdTime(today)
                        .withLastLoginIP(lastLoginIP)
                        .withTryCount(0)
                        .build();

                repositoryService.updateMany(ProjectConstants.CONFIG_DB, ProjectConstants.PPOLICY, ppolicy.createQuery(), ppolicy.createUpdateSet());
            }
        }
    }

    @Override
    public void updatePswd(String shortTokenEmail, String pswd) throws Exception {

        Map query = new HashMap();
        query.put(ProjectConstants.EMAIL, shortTokenEmail);

        long count = repositoryService.count(baseService.getProperties().getLoginDB(), baseService.getProperties().getLoginTable(), query);

        switch ((int) count) {
            case 0:
                throw new RuntimeException("Kullanıcı Adı veya E-posta yalnış.");
            case 1:
                Map document = repositoryService.one(baseService.getProperties().getLoginDB(), baseService.getProperties().getLoginTable(), query);
                ldapService.updatePswd((String) document.get(baseService.getProperties().getLoginUsernameField()), pswd);
                break;
            default:
                throw new RuntimeException("Bu Kullanıcı e-postası ile birden falza kullanıcı tanımlı.");
        }

    }

    @Override
    public int emailCount(String email) {

        Map query = new HashMap();
        query.put(ProjectConstants.EMAIL, email);

        return (int) repositoryService.count(baseService.getProperties().getLoginDB(), baseService.getProperties().getLoginTable(), query);

    }
}
