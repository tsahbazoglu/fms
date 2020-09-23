package tr.org.tspb.common.util;

import org.bson.Document;
import org.bson.types.ObjectId;
import tr.org.tspb.constants.ProjectConstants;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class CustomOlapHashMap {

    private boolean rendered;
    private boolean readonly;
    private boolean calculated;
    private boolean calculate;
    private boolean negotivCheck;
    private Integer divider;
    private String ndType;
    private String ndAxis;
    private String componentType;
    private String measureField;
    private String field;
    private String style;
    private String uysformat;
    private String controlDesc;
    private String cellVersion;
    private String language;
    private String country;
    private String code;
    private Object minimumFractionDigits;
    private Object maximumFractionDigits;
    private Object converter;
    private Object value;
    private Object round;
    private ObjectId id;
    private ObjectId controlID;
    private ObjectId calculateFormulaId;

    /**
     * Put a rendered=true key-value pair to this Map instance.
     *
     * @param rendered
     */
    public CustomOlapHashMap(boolean rendered) {
        this.rendered = rendered;
    }

    public CustomOlapHashMap(String measureField, boolean rendered) {
        this(rendered);
        this.measureField = measureField;
        this.field = measureField;
    }

    public CustomOlapHashMap(String measureField, String style, boolean rendered) {
        this(measureField, rendered);
        this.style = style;
    }

    public CustomOlapHashMap(String measureField, String style, boolean rendered, boolean readonly, boolean calculated) {
        this(measureField, style, rendered);
        this.readonly = readonly;
        this.calculated = calculated;
    }

    public Document toDocument() {
        Document document = new Document();
        document.append(field, value);
        if (id != null) {
            document.append(ProjectConstants.MONGO_ID, id);
        }
        return document;
    }

    public boolean isRendered() {
        return rendered;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public boolean isCalculated() {
        return calculated;
    }

    public String getMeasureField() {
        return measureField;
    }

    public String getField() {
        return field;
    }

    public String getStyle() {
        return style;
    }

    public Object getValue() {
        return value;
    }

    public void resetValue() {
        this.value = null;
    }

    public void resetID() {
        this.id = null;
    }

    public void resetCellVersion() {
        this.cellVersion = null;
    }

    public Object getConverter() {
        return converter;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public ObjectId getId() {
        return id;
    }

    public String getUysformat() {
        return uysformat;
    }

    public Integer getDivider() {
        return divider;
    }

    public String getComponentType() {
        return componentType;
    }

    public Object getRound() {
        return round;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public ObjectId getControlID() {
        return controlID;
    }

    public void setControlID(ObjectId controlID) {
        this.controlID = controlID;
    }

    public String getControlDesc() {
        return controlDesc;
    }

    public void setControlDesc(String controlDesc) {
        this.controlDesc = controlDesc;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public void setCellVersion(String cellVersion) {
        this.cellVersion = cellVersion;
    }

    public void setDivider(Integer divider) {
        this.divider = divider;
    }

    public void setComponentType(String componentType) {
        this.componentType = componentType;
    }

    public void setNdType(String ndType) {
        this.ndType = ndType;
    }

    public void setConverter(Object converter) {
        this.converter = converter;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setNegotivCheck(boolean negotivCheck) {
        this.negotivCheck = negotivCheck;
    }

    public void setUysformat(String uysformat) {
        this.uysformat = uysformat;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setNdAxis(String ndAxis) {
        this.ndAxis = ndAxis;
    }

    public void setMinimumFractionDigits(Object minimumFractionDigits) {
        this.minimumFractionDigits = minimumFractionDigits;
    }

    public void setMaximumFractionDigits(Object maximumFractionDigits) {
        this.maximumFractionDigits = maximumFractionDigits;
    }

    public void setRendered(boolean rendered) {
        this.rendered = rendered;
    }

    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }

    public void setCalculate(boolean calculate) {
        this.calculate = calculate;
    }

    public void setCalculateFormulaId(ObjectId calculateFormulaId) {
        this.calculateFormulaId = calculateFormulaId;
    }
}
