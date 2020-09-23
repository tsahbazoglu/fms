package tr.org.tspb.converter.props;

import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.faces.context.FacesContext;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class MessageBundleLoaderv1 extends HashMap {

    public static final String MESSAGE_PATH = "tr.org.tspb.converter.props.translator";
    public static final String SELECTED_LANG = "SELECTED_LANG";
    private static HashMap messageBundles = new HashMap();

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
        try {
            Locale locale = FacesContext.getCurrentInstance()
                    .getViewRoot().getLocale();

            if (locale == null) {
                locale = new Locale("tr", "TR");
            }

            Locale selected_locale = (Locale) FacesContext.getCurrentInstance()
                    .getExternalContext().getSessionMap().get(SELECTED_LANG);

            if (selected_locale != null) {
                locale = selected_locale;
            }

            locale = new Locale("tr", "TR");

            ResourceBundle messages = (ResourceBundle) messageBundles.get(locale.toString());

            if (messages == null) {
                messages = ResourceBundle.getBundle(MESSAGE_PATH, locale);

                messageBundles.put(locale.toString(), messages);

            }
            return messages.getString(key);
        } // on any failure we just return the key, which should aid in debugging.
        catch (Exception e) {
            return key;
        }
    }

    @Override
    public Object get(Object key) {
        return getMessage((String) key);
    }
}
