package tr.org.tspb.pivot.simple.datamodel;

import com.mongodb.client.model.Filters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.model.SelectItem;
import org.bson.Document;
import tr.org.tspb.dao.FmsForm;
import tr.org.tspb.util.tools.MongoDbUtilIntr;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class ItemProvider {

    private final static String FONT_STYLE = "font-size: 24px;font-weight: bolder;";

    private List<ItemTedbirTuru> tedbirTuleriList;
    private Map<String, ItemTedbirTuru> tedbirTuleriMap;

    private List<ItemKontrolTipi> kontrolTipleri;
    private List<ItemUygulamaYonetimi> uygulamaYonetimleri;
    private List<ItemField> fields;
    private List<SelectItem> fieldsAsItems;

    public ItemProvider(MongoDbUtilIntr mongoDbUtil, String role, FmsForm myForm) {
        init(mongoDbUtil, role, myForm);
    }

    private void init(MongoDbUtilIntr mongoDbUtil, String role, FmsForm myForm) {
        if (tedbirTuleriList == null) {

            tedbirTuleriList = new ArrayList<>();
            tedbirTuleriMap = new HashMap<>();

            List<Document> docs = mongoDbUtil
                    .find("test", "masak_anket_tedbir_turu",
                            Filters.and(
                                    Filters.regex("uye_tipi", role),
                                    Filters.ne("form", "HAA")
                            )
                    );

            if ("masak-faz-2-05-haa".equals(myForm.getKey())) {
                docs = mongoDbUtil
                        .find("test", "masak_anket_tedbir_turu",
                                Filters.and(
                                        Filters.regex("uye_tipi", role),
                                        Filters.eq("form", "HAA")
                                )
                        );
            }

            for (Document doc : docs) {
                ItemTedbirTuru itemTedbirTuru = new ItemTedbirTuru(doc);
                tedbirTuleriList.add(itemTedbirTuru);
                tedbirTuleriMap.put(itemTedbirTuru.getCode(), itemTedbirTuru);
            }

            Collections.sort(tedbirTuleriList, new Comparator<ItemTedbirTuru>() {
                @Override
                public int compare(ItemTedbirTuru t1, ItemTedbirTuru t2) {
                    return Integer.compare(t1.getOrder(), t2.getOrder());
                }
            });
        }

        if (kontrolTipleri == null) {
            kontrolTipleri = new ArrayList<>();
            //FIXME messagebundle
            List<Document> docs = mongoDbUtil.find("test", "masak_anket_kontrol_tipi");
            for (Document doc : docs) {
                kontrolTipleri.add(new ItemKontrolTipi(doc));
            }
        }

        if (uygulamaYonetimleri == null) {
            uygulamaYonetimleri = new ArrayList<>();
            List<Document> docs = mongoDbUtil.find("test", "masak_anket_uygulama_yonetimi");
            for (Document doc : docs) {
                uygulamaYonetimleri.add(new ItemUygulamaYonetimi(doc));
            }
        }

        if (fields == null) {
            fields = new ArrayList<>();

            fields.add(new ItemField(myForm.getKey(), myForm.getPageName(), FONT_STYLE));
//            fields.add(new ItemField("order_010", "Pay (Hisse Senedi) Alım Satımına Aracılık", FONT_STYLE));
//            fields.add(new ItemField("order_020", "Türev Araçların Alım Satımına Aracılık (VİOP ve/veya Tezgahüstü)", FONT_STYLE));
//            fields.add(new ItemField("order_030", "Kaldıraçlı Alım Satım İşlemleri (Forex)", FONT_STYLE));
//            fields.add(new ItemField("order_040", "Borçlanma Araçlarının Alım Satımına Aracılık (Devlet İç Borçlanma Araçları ve Özel Sektör Borçlanma Araçları)", FONT_STYLE));
//            fields.add(new ItemField("order_050", "Halka Arza Aracılık Faaliyetleri", FONT_STYLE));
//            fields.add(new ItemField("order_060", "Bireysel Portföy Yöneticiliği", FONT_STYLE));
//            fields.add(new ItemField("order_070", "Yatırım Danışmanlığı", FONT_STYLE));
//            fields.add(new ItemField("order_080", "Saklama Hizmeti (Sınırlı Saklama - Genel Saklama - Portföy Saklama)", FONT_STYLE));
//            fields.add(new ItemField("order_090", "Repo - Ters Repo İşlemleri", FONT_STYLE));
//            fields.add(new ItemField("order_100", "Yatırım Ortaklığı Faaliyetleri", FONT_STYLE));
//            
        }

    }

    /**
     * @return the tedbirTuleri
     */
    public List<ItemTedbirTuru> getTedbirTuleri() {
        return tedbirTuleriList;
    }

    /**
     * @return the kontrolTipleri
     */
    public List<ItemKontrolTipi> getKontrolTipleri() {
        return kontrolTipleri;
    }

    /**
     * @return the uygulamaYonetimleri
     */
    public List<ItemUygulamaYonetimi> getUygulamaYonetimleri() {
        return uygulamaYonetimleri;
    }

    public List<ItemField> getFields() {
        return fields;
    }

    public List<SelectItem> getFieldsAsItems() {
        fieldsAsItems = new ArrayList<>();
        for (ItemField field : fields) {
            fieldsAsItems.add(new SelectItem(field.getCode(), field.getName()));
        }
        return fieldsAsItems;
    }

    public Map<String, ItemTedbirTuru> getTedbirTuleriMap() {
        return tedbirTuleriMap;
    }

    public void setTedbirTuleriMap(Map<String, ItemTedbirTuru> tedbirTuleriMap) {
        this.tedbirTuleriMap = tedbirTuleriMap;
    }

}
