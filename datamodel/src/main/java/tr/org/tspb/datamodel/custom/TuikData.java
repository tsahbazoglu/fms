package tr.org.tspb.datamodel.custom;

import org.bson.Document;
import org.bson.types.ObjectId;
import static tr.org.tspb.constants.ProjectConstants.FORMS;
import static tr.org.tspb.constants.ProjectConstants.MEMBER;
import static tr.org.tspb.constants.ProjectConstants.MONGO_LDAP_UID;
import static tr.org.tspb.constants.ProjectConstants.PERIOD;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class TuikData {

    private final static String DEFAULT_LOGIN_FK_FILED_NAME = "member";

    private final String ldapUID;
    private final double periodValue;
    private final double genel_yonetim_giderleri;
    private final double personel_giderleri;
    private final double calisan_sayisi;
    //
    private ObjectId period;
    private ObjectId member;
    private String forms;

    public TuikData(String ldapUID, double periodValue, double genel_yonetim_giderleri, double personel_giderleri, double calisan_sayisi) {
        this.ldapUID = ldapUID;
        this.periodValue = periodValue;
        this.genel_yonetim_giderleri = genel_yonetim_giderleri;
        this.personel_giderleri = personel_giderleri;
        this.calisan_sayisi = calisan_sayisi;
    }

    public void createMeta(ObjectId member, ObjectId period, String forms) {
        this.member = member;
        this.period = period;
        this.forms = forms;
    }

    public Document toDoc() {
        Document document = new Document();
        document.append(MONGO_LDAP_UID, ldapUID);
        document.append("periodValue", periodValue);
        document.append("genel_yonetim_giderleri", genel_yonetim_giderleri);
        document.append("personel_giderleri", personel_giderleri);
        document.append("calisan_sayisi", calisan_sayisi);
        document.append(MEMBER, member);
        document.append(PERIOD, period);
        document.append(FORMS, forms);

        return document;
    }

    public Document searchDoc() {
        Document document = new Document();
        document.append(MEMBER, member);
        document.append(PERIOD, period);
        document.append(FORMS, forms);
        document.append(DEFAULT_LOGIN_FK_FILED_NAME, member);

        return document;
    }

    public String getLdapUID() {
        return ldapUID;
    }

    public double getPeriodValue() {
        return periodValue;
    }

}
