package tr.org.tspb.datamodel.gui;

import org.bson.Document;
import static tr.org.tspb.constants.ProjectConstants.FORM;
import static tr.org.tspb.constants.ProjectConstants.FORM_KEY;
import static tr.org.tspb.constants.ProjectConstants.NAME;
import static tr.org.tspb.constants.ProjectConstants.MENU_ORDER;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class FormItem {

    private final String key;
    private final String name;
    private final String form;
    private final int menuOrder;

    public FormItem(Document doc) {
        this.key = doc.get(FORM_KEY).toString();
        this.name = doc.get(NAME).toString();
        this.form = (String) doc.get(FORM);
        this.menuOrder = ((Number) doc.get(MENU_ORDER)).intValue();
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getForm() {
        return form;
    }

    public int getMenuOrder() {
        return menuOrder;
    }

}
