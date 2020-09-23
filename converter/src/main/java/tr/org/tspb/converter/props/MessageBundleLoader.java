package tr.org.tspb.converter.props;

import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.faces.context.FacesContext;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class MessageBundleLoader extends HashMap {

    public static final String MESSAGE_PATH = "tr.org.tspb.converter.props.translator";
    public static final String SELECTED_LANG = "SELECTED_LANG";
    private static final HashMap<String, ResourceBundle> mapResourceBundles = new HashMap();

    /**
     * Gets a string for the given key from this resource bundle or one of its
     * parents.
     *
     * @param key the key for the desired string
     * @return the string for the given key. If the key string value is not
     * found the key itself is returned.
     */
    public static String getMessage(String key) {

        if (key == null) {
            return null;
        }

        Locale locale = null;
        Locale selected_locale = null;

        try {
            locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
            selected_locale = (Locale) FacesContext//
                    .getCurrentInstance()//
                    .getExternalContext()//
                    .getSessionMap()//
                    .get(SELECTED_LANG);
        } catch (Exception e) {
            //no action
        }

        if (locale == null) {
            locale = new Locale("tr", "TR");
        }

        if (selected_locale != null) {
            locale = selected_locale;
        }

        ResourceBundle messages = mapResourceBundles.get(locale.toString());

        if (messages == null) {
            messages = ResourceBundle.getBundle(MESSAGE_PATH, locale);
            mapResourceBundles.put(locale.toString(), messages);
        }

        String value = messages.getString(key);

        return value == null ? key : value;

    }

    @Override
    public Object get(Object key) {
        return getMessage((String) key);
    }

}
