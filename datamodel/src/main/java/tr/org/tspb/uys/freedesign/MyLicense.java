package tr.org.tspb.uys.freedesign;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.bson.Document;
import static tr.org.tspb.constants.ProjectConstants.ADMIN_METADATA;
import static tr.org.tspb.constants.ProjectConstants.CREATE_DATE;
import static tr.org.tspb.constants.ProjectConstants.CREATE_SESSIONID;
import static tr.org.tspb.constants.ProjectConstants.CREATE_USER;
import static tr.org.tspb.constants.ProjectConstants.UPDATE_DATE;
import static tr.org.tspb.constants.ProjectConstants.UPDATE_USER;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class MyLicense {

    private static final String CSS_STYLE__FONT_WEIGHT__NORMAL = "font-weight: normal;";
    private static final String CSS_STYLE__FONT_WEIGHT__BOLD = "font-weight: bold;";
    //
    public static final String OUT_KEY = "out";
    public static final String IN_KEY = "in";
    public static final String IN_OUT_KEY = "inout";
    public static final String LICENSE_KEY = "license";
    private static Map<String, MyLicense> licenseParent;
    private static Map<String, MyLicense> licenseParentPys;

    public static Map<String, MyLicense> getLicenceParent() {
        if (licenseParent == null) {
            licenseParent = new HashMap<>();
            licenseParent.put("F2010", new MyLicense(null, "F2010", "A) EMİR İLETİMİNE ARACILIK FAALİYETİ", CSS_STYLE__FONT_WEIGHT__BOLD, 100, false, false, false, false));
            licenseParent.put("F2020", new MyLicense(null, "F2020", "A.1 - Paylar", CSS_STYLE__FONT_WEIGHT__NORMAL, 110, true, true, false, false));
            licenseParent.put("F2030", new MyLicense(null, "F2030", "A.2 - Diğer Menkul Kıymetler", CSS_STYLE__FONT_WEIGHT__NORMAL, 120, true, true, false, false));
            licenseParent.put("F2040", new MyLicense(null, "F2040", "A.3 - Kaldıraçlı Alım Satım İşlemleri", CSS_STYLE__FONT_WEIGHT__NORMAL, 130, true, true, false, false));
            licenseParent.put("F2050", new MyLicense(null, "F2050", "A.4 - Paya Dayalı Türev Araçlar", CSS_STYLE__FONT_WEIGHT__NORMAL, 140, true, true, false, false));
            licenseParent.put("F2060", new MyLicense(null, "F2060", "A.5 - Pay Endekslerine Dayalı Türev Araçlar", CSS_STYLE__FONT_WEIGHT__NORMAL, 150, true, true, false, false));
            licenseParent.put("F2070", new MyLicense(null, "F2070", "A.6 - Diğer Türev Araçlar", CSS_STYLE__FONT_WEIGHT__NORMAL, 160, true, true, false, false));
            licenseParent.put("F2080", new MyLicense(null, "F2080", "B) İŞLEM ARACILIĞI FAALİYETİ", CSS_STYLE__FONT_WEIGHT__BOLD, 170, false, false, false, false));
            licenseParent.put("F2090", new MyLicense(null, "F2090", "B.1 - Paylar", CSS_STYLE__FONT_WEIGHT__NORMAL, 180, true, true, false, false));
            licenseParent.put("F2100", new MyLicense(null, "F2100", "B.2 - Diğer Menkul Kıymetler", CSS_STYLE__FONT_WEIGHT__NORMAL, 190, true, true, false, false));
            licenseParent.put("F2110", new MyLicense(null, "F2110", "B.3 - Kaldıraçlı Alım Satım İşlemleri", CSS_STYLE__FONT_WEIGHT__NORMAL, 200, true, true, false, false));
            licenseParent.put("F2120", new MyLicense(null, "F2120", "B.4 - Paya Dayalı Türev Araçlar", CSS_STYLE__FONT_WEIGHT__NORMAL, 210, true, true, false, false));
            licenseParent.put("F2130", new MyLicense(null, "F2130", "B.5 - Pay Endekslerine Dayalı Türev Araçlar", CSS_STYLE__FONT_WEIGHT__NORMAL, 220, true, true, false, false));
            licenseParent.put("F2140", new MyLicense(null, "F2140", "B.6 - Diğer Türev Araçlar", CSS_STYLE__FONT_WEIGHT__NORMAL, 230, true, true, false, false));
            licenseParent.put("F2150", new MyLicense(null, "F2150", "C) PORTFÖY ARACILIĞI FAALİYETİ", CSS_STYLE__FONT_WEIGHT__BOLD, 240, false, false, false, false));
            licenseParent.put("F2160", new MyLicense(null, "F2160", "C.1 - Paylar", CSS_STYLE__FONT_WEIGHT__NORMAL, 250, true, false, false, false));
            licenseParent.put("F2170", new MyLicense(null, "F2170", "C.2 - Diğer Menkul Kıymetler", CSS_STYLE__FONT_WEIGHT__NORMAL, 260, true, false, false, false));
            licenseParent.put("F2180", new MyLicense(null, "F2180", "C.3 - Kaldıraçlı Alım Satım İşlemleri", CSS_STYLE__FONT_WEIGHT__NORMAL, 270, true, false, false, false));
            licenseParent.put("F2190", new MyLicense(null, "F2190", "C.4 - Paya Dayalı Türev Araçlar", CSS_STYLE__FONT_WEIGHT__NORMAL, 280, true, false, false, false));
            licenseParent.put("F2200", new MyLicense(null, "F2200", "C.5 - Pay Endekslerine Dayalı Türev Araçlar", CSS_STYLE__FONT_WEIGHT__NORMAL, 290, true, false, false, false));
            licenseParent.put("F2210", new MyLicense(null, "F2210", "C.6 - Diğer Türev Araçlar", CSS_STYLE__FONT_WEIGHT__NORMAL, 300, true, false, false, false));
            licenseParent.put("F2220", new MyLicense(null, "F2220", "D) BİREYSEL PORTFÖY YÖNETİCİLİĞİ FAALİYETİ", CSS_STYLE__FONT_WEIGHT__BOLD, 310, true, false, false, false));
            licenseParent.put("F2230", new MyLicense(null, "F2230", "E) YATIRIM DANIŞMANLIĞI FAALİYETİ", CSS_STYLE__FONT_WEIGHT__BOLD, 320, true, false, false, false));
            licenseParent.put("F2240", new MyLicense(null, "F2240", "F) HALKA ARZA ARACILIK FAALİYETİ", CSS_STYLE__FONT_WEIGHT__BOLD, 330, false, false, false, false));
            licenseParent.put("F2250", new MyLicense(null, "F2250", "F.1 - En İyi Gayret Aracılığı Faaliyeti", CSS_STYLE__FONT_WEIGHT__NORMAL, 340, true, false, false, false));
            licenseParent.put("F2260", new MyLicense(null, "F2260", "F.2 - Aracılık Yüklenimi Faaliyeti", CSS_STYLE__FONT_WEIGHT__NORMAL, 350, true, false, false, false));
            licenseParent.put("F2270", new MyLicense(null, "F2270", "G) SAKLAMA HİZMETİ", CSS_STYLE__FONT_WEIGHT__BOLD, 360, false, false, false, false));
            licenseParent.put("F2280", new MyLicense(null, "F2280", "G.1 - Sınırlı Saklama", CSS_STYLE__FONT_WEIGHT__NORMAL, 370, true, false, false, false));
            licenseParent.put("F2290", new MyLicense(null, "F2290", "G.2 - Genel Saklama", CSS_STYLE__FONT_WEIGHT__NORMAL, 380, true, false, false, false));
            licenseParent.put("F2300", new MyLicense(null, "F2300", "H) PORTFÖY SAKLAMA HİZMETİ", CSS_STYLE__FONT_WEIGHT__BOLD, 390, true, false, false, false));

        }
        return Collections.unmodifiableMap(licenseParent);
    }

    public static Map<String, MyLicense> getLicenceParentPys() {
        if (licenseParentPys == null) {
            licenseParentPys = new HashMap<>();
            licenseParentPys.put("F3000", new MyLicense(null, "F3000", "A) PORTFÖY YÖNETİCİLİĞİ FAALİYETİ", CSS_STYLE__FONT_WEIGHT__BOLD, 100, true, false));
            licenseParentPys.put("F3010", new MyLicense(null, "F3010", "B) YATIRIM DANIŞMANLIĞI FAALİYETİ", CSS_STYLE__FONT_WEIGHT__BOLD, 170, true, false));
            licenseParentPys.put("F3020", new MyLicense(null, "F3020", "C) YATIRIM FONU KATILMA PAYI PAZARLAMA VE DAĞITIM FAALİYETİ", CSS_STYLE__FONT_WEIGHT__BOLD, 240, true, true));
        }
        return Collections.unmodifiableMap(licenseParentPys);
    }
    //Eager Fields
    private String licenseID;
    private String licenseCode;
    private String licenseName;
    private String style;
    private int orderno;

    private Boolean renderedIn;
    private Boolean renderedOut;
    private Boolean renderedInOut;
    private Boolean readonlyIn;
    private Boolean readonlyOut;
    private Boolean readonlyInOut;

    private Boolean saveToDb;
    //Lazy Fields
    private String license;//should be ObjectId in case resolving from db
    private Boolean in;
    private Boolean out;
    private Boolean inout;

    private MyLicense eager;

    public MyLicense(Map o) {
        this.in = (Boolean) o.get(IN_KEY);
        this.out = (Boolean) o.get(OUT_KEY);
        this.inout = (Boolean) o.get(IN_OUT_KEY);
        this.license = (String) o.get(LICENSE_KEY);//FIXME should be replace with ObjectId
        this.uysAdditionalMetaData = (Document) o.get(ADMIN_METADATA);
        this.eager = getLicenceParent().get(this.license);
        if (this.eager == null) {//FIXME burası cok kotu filtre eklenmeli
            this.eager = getLicenceParentPys().get(this.license);
        }
    }

    private MyLicense(String licenseID, String licenseCode, String licenseName, String style, int orderno,
            boolean renderedIn, boolean renderedOut, boolean readonlyIn, boolean readonlyOut) {
        this.licenseID = licenseID;
        this.licenseCode = licenseCode;
        this.licenseName = licenseName;
        this.license = licenseCode;//FIXME shoul be replace with ObjectId
        this.style = style;
        this.orderno = orderno;
        this.renderedIn = renderedIn;
        this.renderedOut = renderedOut;
        this.readonlyIn = readonlyIn;
        this.readonlyOut = readonlyOut;
        this.saveToDb = renderedIn || renderedOut;
    }

    private MyLicense(String licenseID, String licenseCode, String licenseName, String style, int orderno,
            boolean renderedInOut, boolean readonlyInOut) {
        this.licenseID = licenseID;
        this.licenseCode = licenseCode;
        this.licenseName = licenseName;
        this.license = licenseCode;//FIXME shoul be replace with ObjectId
        this.style = style;
        this.orderno = orderno;
        this.renderedInOut = renderedInOut;
        this.readonlyInOut = readonlyInOut;
        this.saveToDb = true;
    }

    public MyLicense(MyLicense license, Set<String> roles) {
        this.licenseID = license.licenseID;
        this.licenseCode = license.licenseCode;
        this.licenseName = license.licenseName;
        this.license = license.license;
        this.style = license.style;
        this.orderno = license.orderno;
        this.renderedIn = license.renderedIn;
        this.renderedOut = license.renderedOut;
        this.renderedInOut = license.renderedInOut;
        this.readonlyIn = license.readonlyIn;
        this.readonlyOut = license.readonlyOut;
        this.readonlyInOut = license.readonlyInOut;
        this.saveToDb = license.saveToDb;

        Set<String> bnkuser_ktbuser_readonly = new HashSet<>(Arrays.asList(//
                "F2040", "F2090", "F2110", "F2120", "F2130", "F2160", "F2180", "F2190",//
                "F2240", "F2220", "F2230", "F2250", "F2260"
        ));
        Set<String> ybuser_kbuser_readonly = new HashSet<>(Arrays.asList("F2040", "F2090",//
                "F2110", "F2120", "F2130", "F2160", "F2180", "F2190"
        ));

        if ((roles.contains("bnkuser") || roles.contains("mbuser") || roles.contains("ktbuser")) && bnkuser_ktbuser_readonly.contains(licenseCode)) {
            this.readonlyIn = true;
            this.readonlyOut = true;
            this.style = this.style.concat("background:#DFEFFC none repeat scroll 0% 0%;");
        }

        if ((roles.contains("ybuser") || roles.contains("kbuser")) && ybuser_kbuser_readonly.contains(licenseCode)) {
            this.readonlyIn = true;
            this.readonlyOut = true;
            this.style = this.style.concat("background:#DFEFFC none repeat scroll 0% 0%;");
        }

    }

    public Document getAsDBObject() {
        Document o = new Document();
        o.append(MyLicense.LICENSE_KEY, this.license);
        o.append(MyLicense.IN_KEY, this.in);//stayed for quick search by key
        o.append(MyLicense.OUT_KEY, this.out);
        o.append(MyLicense.IN_OUT_KEY, this.inout);
        o.append(ADMIN_METADATA, uysAdditionalMetaData);
        return o;
    }

    /**
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof MyLicense) {
            return ((MyLicense) o).getLicense().equals(this.getLicense());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.license);
        return hash;
    }

    public Boolean getSaveToDb() {
        return saveToDb;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public Boolean getIn() {
        return in;
    }

    public void setIn(Boolean in) {
        this.in = in;
    }

    public Boolean getOut() {
        return out;
    }

    public void setOut(Boolean out) {
        this.out = out;
    }

    public String getLicenseName() {
        return licenseName;
    }

    public String getStyle() {
        return style;
    }

    public Boolean getRenderedIn() {
        return renderedIn;
    }

    public Boolean getRenderedOut() {
        return renderedOut;
    }

    public Boolean getRenderedInOut() {
        return renderedInOut;
    }

    public String getLicenseID() {
        return licenseID;
    }

    public MyLicense getEager() {
        return eager;
    }

    public Boolean getReadonlyIn() {
        return readonlyIn;
    }

    public void setReadonlyIn(Boolean readonlyIn) {
        this.readonlyIn = readonlyIn;
    }

    public Boolean getReadonlyOut() {
        return readonlyOut;
    }

    public void setReadonlyOut(Boolean readonlyOut) {
        this.readonlyOut = readonlyOut;
    }

    public Boolean getReadonlyInOut() {
        return readonlyInOut;
    }

    public void setReadonlyInOut(Boolean readonlyInOut) {
        this.readonlyInOut = readonlyInOut;
    }

    public int getOrderno() {
        return orderno;
    }

    public Boolean getInout() {
        return inout;
    }

    public void setInout(Boolean inout) {
        this.inout = inout;
    }

    public String getLicenseCode() {
        return licenseCode;
    }

    private Document uysAdditionalMetaData;

    public void setAdminMetaData(String username, Date date, String sessionId) {
        if (uysAdditionalMetaData == null) {
            uysAdditionalMetaData = new Document();
            uysAdditionalMetaData.put(CREATE_USER, username);
            uysAdditionalMetaData.put(CREATE_DATE, date);
            uysAdditionalMetaData.put(CREATE_SESSIONID, sessionId);
        } else {
            uysAdditionalMetaData.put(UPDATE_DATE, new Date());
            uysAdditionalMetaData.put(UPDATE_USER, username);
        }

    }

}
