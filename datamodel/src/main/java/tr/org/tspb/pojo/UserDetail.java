package tr.org.tspb.pojo;

import static tr.org.tspb.constants.ProjectConstants.MONGO_ID;
import static tr.org.tspb.constants.ProjectConstants.NAME;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class UserDetail implements Serializable {

    private String username;
    private String firstName;
    private String lastName;
    private String commonName;
    private List<EimzaPersonel> eimzaPersonels = new ArrayList<>();
    private DatabaseUser databaseUser;
    private Document loginFkSearchMapInListOfValues;

    public void initDatabaseUser(DatabaseUser databaseUser) {
        this.databaseUser = databaseUser;

    }

    public void createEimzaPersonels(List<EimzaPersonel> eimzaPersonels) {
        this.eimzaPersonels = eimzaPersonels;
    }

    public List<UserDetail.EimzaPersonel> getEimzaPersonels() {
        return Collections.unmodifiableList(eimzaPersonels);
    }

    public void createLoginFkSearchMapInListOfValues(ObjectId loginUserId) {
        List list = new ArrayList();
        for (UserDetail.EimzaPersonel ep : eimzaPersonels) {
            if (!list.contains(ep.getDelegatingMember())) {
                list.add(ep.getDelegatingMember());
            }
        }
        if (loginUserId != null && !list.contains(loginUserId)) {
            list.add(loginUserId);
        }
        loginFkSearchMapInListOfValues = new Document("$in", list);
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Document getLoginFkSearchMapInListOfValues() {
        return loginFkSearchMapInListOfValues;
    }

    public DatabaseUser getDbo() {
        return databaseUser;
    }

    public class EimzaPersonel {

        private String firstname;
        private String lastname;
        private String email;
        private String telefon;
        private String tcno;
        private String esign_provider;
        private final ObjectId delegatedField;
        private final ObjectId delegatingMember;

        public EimzaPersonel(Document dbo, Document delegatedFieldRecord, String delegatedField, String delegatingField) {
            if (dbo.get("firstname") == null) {
                this.firstname = delegatedFieldRecord.get("adi").toString();
            } else {
                this.firstname = dbo.get("firstname").toString();
            }

            if (dbo.get("lastname") == null) {
                this.lastname = delegatedFieldRecord.get("soyadi").toString();
            } else {
                this.lastname = dbo.get("lastname").toString();
            }

            if (dbo.get("tcno") == null) {
                this.tcno = delegatedFieldRecord.get("kimlikNo").toString();
            } else {
                this.tcno = dbo.get("tcno").toString();
            }

            if (dbo.get("email") != null) {
                this.email = dbo.get("email").toString();
            }
            if (dbo.get("telefon") != null) {
                this.telefon = dbo.get("telefon").toString();
            }
            if (dbo.get("esign_provider") != null) {
                this.esign_provider = dbo.get("esign_provider").toString();
            }

            this.delegatedField = (ObjectId) dbo.get(delegatedField);
            this.delegatingMember = (ObjectId) dbo.get(delegatingField);

            validate();

        }

        public String getEmail() {
            return email;
        }

        public String getTelefon() {
            return telefon;
        }

        public String getTcno() {
            return tcno;
        }

        public String getEsign_provider() {
            return esign_provider;
        }

        public ObjectId getDelegatedField() {
            return delegatedField;
        }

        public String getFirstname() {
            return firstname;
        }

        public ObjectId getDelegatingMember() {
            return delegatingMember;
        }

        public String getLastname() {
            return lastname;
        }

        @Override
        public String toString() {
            super.toString(); //To change body of generated methods, choose Tools | Templates.
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            sb.append("'tcno':'".concat(tcno).concat("', "));
            sb.append("'firstname':'".concat(firstname).concat("', "));
            sb.append("'lastname':'".concat(lastname).concat("'}"));
            return sb.toString();
        }

        private void validate() {
            if (this.tcno == null) {
                throw new RuntimeException("Could not detect logged or delegated user national id.");
            }
            if (this.firstname == null) {
                throw new RuntimeException("Could not detect logged or delegated user firstname.");
            }
            if (this.lastname == null) {
                throw new RuntimeException("Could not detect logged or delegated user lastname.");
            }
        }

    }

}
