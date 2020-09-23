package tr.org.tspb.pivot.simple.datamodel;

import org.bson.Document;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class ItemTedbirTuru {

    /*
    
 mongo
 db.masak_anket_tedbir_turu.drop();
 exit
// mongoimport -d test -c masak_anket_tedbir_turu --type csv --file masak_anket_tedbir_turu.csv --headerline; 
 mongoimport -d test -c masak_anket_tedbir_turu --type csv --file masak_anket_tedbir_turu_v2.csv --headerline; 
 mongo
    
    
Number.prototype.pad = function (size) {
    var s = String(this);
    while (s.length < (size || 2)) {
        s = "0" + s;
    }
    return s;
}
    
bulk = db.masak_anket_tedbir_turu.initializeUnorderedBulkOp();
var i=10
db.masak_anket_tedbir_turu.find({}).forEach(function (v) {
    var code = "code_" + i.pad(4);
    var style = "font-weight: normal;";
    bulk.find({_id: v._id}).upsert().updateOne({$set: {code: code, style:style}});
    i += 10;
});
bulk.execute();
    
    
     */
    private int order;
    private String code;
    private String name;
    private String style;
    private String memberType;

    private static String CODE = "code";
    private static String NAME = "tedbir_turu";
    private static String STYLE = "style";
    private static String MEMBER_TYPE = "uye_tipi";

    public ItemTedbirTuru(Document document) {
        this.code = document.getString(CODE);
        this.name = document.getString(NAME);
        this.style = document.getString(STYLE);
        this.memberType = document.getString(MEMBER_TYPE);
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the style
     */
    public String getStyle() {
        return style;
    }

    /**
     * @return the memberType
     */
    public String getMemberType() {
        return memberType;
    }

    public int getOrder() {
        return order;
    }

}
