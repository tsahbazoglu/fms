package tr.org.tspb.converter.base;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author Telman Şahbazoğlu
 */
@FacesConverter("fmsDateTimeConverterReadonly")
public class FmsDateTimeConverterReadonly implements Converter {

    private static final DateFormat SIMPLE_DATE_FORMAT__0 = new SimpleDateFormat("dd.MM.yyyy");
    private static final DateFormat SIMPLE_DATE_FORMAT__1 = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
    private static final DateFormat SIMPLE_DATE_FORMAT__2 = new SimpleDateFormat("dd MMMM yyyy ; yyyy.MM.dd", new Locale("tr", "TR"));
    private static final DateFormat SIMPLE_DATE_FORMAT__3 = new SimpleDateFormat("dd MMMM yyyy hh:mm:ss", new Locale("tr"));
    private static final DateFormat SIMPLE_DATE_FORMAT__4 = new SimpleDateFormat("yyyyMMddHHmm");
    private static final DateFormat SIMPLE_DATE_FORMAT__5 = new SimpleDateFormat("yyyyMMdd_hhmmss");
    private static final DateFormat SIMPLE_DATE_FORMAT__6 = new SimpleDateFormat("yyyyMMdd");
    private static final DateFormat SIMPLE_DATE_FORMAT__7 = new SimpleDateFormat("HH:mm");
    private static final DateFormat SIMPLE_DATE_FORMAT__8 = new SimpleDateFormat("yyyy");

    @Override
    public Object getAsObject(FacesContext fc, UIComponent component, String value) {
        return value;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {

        if (!(value instanceof Date)) {
            return null;
        }

        String datePattern = (String) component.getAttributes().get("datePattern");

        switch (datePattern) {
            case "yyyy":
                return SIMPLE_DATE_FORMAT__8.format((Date) value);
            case "dd.MM.yyyy":
                return SIMPLE_DATE_FORMAT__0.format((Date) value);
            case "dd/MM/yyyy hh:mm:ss":
                return SIMPLE_DATE_FORMAT__1.format((Date) value);
            case "dd MMMM yyyy ; yyyy.MM.dd":
                return SIMPLE_DATE_FORMAT__2.format((Date) value);
            case "dd MMMM yyyy hh:mm:ss":
                return SIMPLE_DATE_FORMAT__3.format((Date) value);
            case "yyyyMMddHHmm":
                return SIMPLE_DATE_FORMAT__4.format((Date) value);
            case "yyyyMMdd_hhmmss":
                return SIMPLE_DATE_FORMAT__5.format((Date) value);
            case "yyyyMMdd":
                return SIMPLE_DATE_FORMAT__6.format((Date) value);
            case "HH:mm":
                return SIMPLE_DATE_FORMAT__7.format((Date) value);

        }
        return new SimpleDateFormat(datePattern).format((Date) value);
    }

}
