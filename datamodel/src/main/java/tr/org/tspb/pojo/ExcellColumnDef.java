package tr.org.tspb.pojo;

import org.bson.Document;
import org.bson.types.Code;
import tr.org.tspb.dao.MyField;
import tr.org.tspb.dao.FmsForm;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class ExcellColumnDef {

    private final String type;
    private final boolean cache;
    private final int cellType;
    private final Code converter;
    private final MyField toMyField;

    public ExcellColumnDef(FmsForm myForm, Document dbo) {
        this.type = dbo.getString("type");
        this.cache = Boolean.TRUE.equals(dbo.get("cache"));
        this.cellType = resolveCellType();
        this.converter = new Code(dbo.getString("converter"));

        String toFieldKey = dbo.getString("to");

        toMyField = myForm.getField(toFieldKey);
        if (toMyField == null) {
            throw new RuntimeException(String.format("form fields does not contain key %s mentioned in upload-config property", toFieldKey));
        }
    }

    public Code getConverter() {
        return converter;
    }

    public MyField getToMyField() {
        return toMyField;
    }

    public String getType() {
        return type;
    }

    public boolean isCache() {
        return cache;
    }

    public int getCellType() {
        return cellType;
    }

    private int resolveCellType() {
        switch (type) {
            case "CELL_TYPE_STRING":
                return 1;//apachi poi Cell.CELL_TYPE_STRING;
            case "CELL_TYPE_NUMERIC":
                return 0;//Cell.CELL_TYPE_NUMERIC;
            case "CELL_TYPE_DATE":
                return 0;//Cell.CELL_TYPE_NUMERIC;
        }
        return 0;//Cell.CELL_TYPE_NUMERIC;
    }

    @Override
    public String toString() {
        return this.toMyField.getKey();
    }

}
