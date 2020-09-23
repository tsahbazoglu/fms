package tr.org.tspb.converter.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class PasswordValidator implements Validator {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {

        String passwordId = (String) component.getAttributes().get("passwordId");

        UIInput passwordInput = (UIInput) context.getViewRoot().findComponent(passwordId);

        String password = (String) passwordInput.getValue();

        String confirm = (String) value;

        if (false//
                || password == null //
                || confirm == null//
                || password.trim().isEmpty() //
                || confirm.trim().isEmpty() //
                || !password.equals(confirm)) {
            throw new ValidatorException(
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Şifreler boş veya eşit değil.", "*"));
        }
    }
}
