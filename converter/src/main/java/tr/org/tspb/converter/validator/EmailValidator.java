package tr.org.tspb.converter.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 * 
 * @author Telman Şahbazoğlu
 */
@FacesValidator("emailValidator")
public class EmailValidator implements Validator {

    @Override
    public void validate(FacesContext arg0, UIComponent arg1, Object arg2)
            throws ValidatorException {
        String inputEmail = (String) arg2;

        //Set the email pattern string.  (?i) is a flag for case-insensitive. 
        Pattern p = Pattern.compile("(?i)\\b[A-Z0-9._%-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}\\b");

        //Match the given string with the pattern
        Matcher m = p.matcher(inputEmail);

        //Check whether match is found
        boolean matchFound = m.matches();

        if (!matchFound) {
            String invalidEmailMsg = "Invalid Email";
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, invalidEmailMsg, invalidEmailMsg);
            throw new ValidatorException(message);
        }

    }

}
