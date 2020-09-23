package tr.org.tspb.pojo;

import java.util.Date;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlInputTextarea;
import javax.faces.component.html.HtmlSelectBooleanCheckbox;
import javax.faces.component.html.HtmlSelectManyListbox;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.component.html.HtmlSelectOneRadio;
import org.primefaces.component.calendar.Calendar;

/**
 *
 * @author Telman Şahbazoğlu
 */
public enum ComponentType {

    selectOneMenu(String.class, HtmlSelectOneMenu.class, HtmlSelectOneMenu.COMPONENT_TYPE),
    selectManyListbox(String.class, HtmlSelectManyListbox.class, HtmlSelectManyListbox.COMPONENT_TYPE),
    inputDate(Date.class, Calendar.class, Calendar.COMPONENT_TYPE),
    inputText(String.class, HtmlInputText.class, HtmlInputText.COMPONENT_TYPE),
    inputTextarea(String.class, HtmlInputTextarea.class, HtmlInputTextarea.COMPONENT_TYPE),
    selectBooleanCheckbox(Boolean.class, HtmlSelectBooleanCheckbox.class, HtmlSelectBooleanCheckbox.COMPONENT_TYPE),
    selectOneRadio(String.class, HtmlSelectOneRadio.class, HtmlSelectOneRadio.COMPONENT_TYPE),
    commandButton(String.class, HtmlCommandButton.class, HtmlCommandButton.COMPONENT_TYPE),
    //FIXME
    inputFile(String.class, Object.class, null),
    inputMask(String.class, Object.class, null);
    //
    Class bindClass;
    Class componentClass;
    String componentType;

    public Class getBindClass() {
        return bindClass;
    }

    public Class getComponentClass() {
        return componentClass;
    }

    private ComponentType(Class bindClass, Class componentClass, String componentType) {
        this.bindClass = bindClass;
        this.componentClass = componentClass;
        this.componentType = componentType;
    }

    public static ComponentType value(String key) {
        try {
            return valueOf(key);
        } catch (Exception ex) {
            return null;
        }
    }

    public String getComponentType() {
        return componentType;
    }
}
