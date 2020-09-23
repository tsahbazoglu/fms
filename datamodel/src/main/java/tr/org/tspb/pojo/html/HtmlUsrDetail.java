package tr.org.tspb.pojo.html;

import tr.org.tspb.pojo.UserDetail;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class HtmlUsrDetail {

    private String firstname;
    private String lastname;
    private String email;
    private String rolesTable;
    private String companiesTable;

    public HtmlUsrDetail(UserDetail userDetail, String rolesTable, String companiesTable) {
        this.firstname = userDetail.getFirstName();
        this.lastname = userDetail.getLastName();
        this.email = "__@__.com";
        this.rolesTable = rolesTable;
        this.companiesTable = companiesTable;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRolesTable() {
        return rolesTable;
    }

    public void setRolesTable(String rolesTable) {
        this.rolesTable = rolesTable;
    }

    public String getCompaniesTable() {
        return companiesTable;
    }

    public void setCompaniesTable(String companiesTable) {
        this.companiesTable = companiesTable;
    }
}
