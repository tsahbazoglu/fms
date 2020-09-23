package tr.org.tspb.util;

import javax.faces.component.html.HtmlCommandButton;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ValueChangeEvent;
import javax.faces.event.ValueChangeListener;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class ResetChangeListener implements ValueChangeListener {

    private String myKey;

    public ResetChangeListener(String myKey) {
        this.myKey = myKey;
    }

    @Override
    public void processValueChange(ValueChangeEvent event) throws AbortProcessingException {
        HtmlCommandButton button = (HtmlCommandButton) FacesContext//
                .getCurrentInstance().getViewRoot()//
                .findComponent("iceformContent:buttonResetId");
        button.invokeOnComponent(null, myKey, null);
    }
}
