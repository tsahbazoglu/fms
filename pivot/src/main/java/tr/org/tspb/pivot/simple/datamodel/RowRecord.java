package tr.org.tspb.pivot.simple.datamodel;

import org.bson.Document;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class RowRecord {

    private static final String KONTROL_TIPI = "kontrolTipi";
    private static final String UYGULAMA_YONETIMI = "uygulamaYonetimi";
    private static final String CHECK = "check";
    private static final String TEDBIR_TURU = "tedbirTuru";

    private String tedbirTuru;
    private String uygulamaYonetimi;
    private String kontrolTipi;
    private Boolean check;

    public RowRecord(String tedbirTuru, String uygulamaYonetimi, String kontrolTipi) {
        this.tedbirTuru = tedbirTuru;
        this.uygulamaYonetimi = uygulamaYonetimi;
        this.kontrolTipi = kontrolTipi;
    }

    public RowRecord(String tedbirTuru, String uygulamaYonetimi, String kontrolTipi, Boolean check) {
        this(tedbirTuru, uygulamaYonetimi, kontrolTipi);
        this.check = check;
    }

    /**
     * @return the tedbirTuru
     */
    public String getTedbirTuru() {
        return tedbirTuru;
    }

    /**
     * @param tedbirTuru the tedbirTuru to set
     */
    public void setTedbirTuru(String tedbirTuru) {
        this.tedbirTuru = tedbirTuru;
    }

    /**
     * @return the uygulamaYonetimi
     */
    public String getUygulamaYonetimi() {
        return uygulamaYonetimi;
    }

    /**
     * @param uygulamaYonetimi the uygulamaYonetimi to set
     */
    public void setUygulamaYonetimi(String uygulamaYonetimi) {
        this.uygulamaYonetimi = uygulamaYonetimi;
    }

    /**
     * @return the kontrolTipi
     */
    public String getKontrolTipi() {
        return kontrolTipi;
    }

    /**
     * @param kontrolTipi the kontrolTipi to set
     */
    public void setKontrolTipi(String kontrolTipi) {
        this.kontrolTipi = kontrolTipi;
    }

    /**
     * @return the check
     */
    public Boolean getCheck() {
        return check;
    }

    /**
     * @param check the check to set
     */
    public void setCheck(Boolean check) {
        this.check = check;
    }

    public Document toDocument() {
        Document doc = new Document();
        doc.append(KONTROL_TIPI, kontrolTipi);
        doc.append(UYGULAMA_YONETIMI, uygulamaYonetimi);
        doc.append(TEDBIR_TURU, tedbirTuru);
        doc.append(CHECK, check);
        return doc;
    }

    public static RowRecord toObject(Document document) {
        return new RowRecord(document.getString(TEDBIR_TURU),
                document.getString(UYGULAMA_YONETIMI),
                document.getString(KONTROL_TIPI),
                document.getBoolean(CHECK));

    }

}
