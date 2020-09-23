package tr.org.tspb.util.stereotype;

import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Stereotype;
import javax.inject.Named;

/**
 *
 * @author Telman Şahbazoğlu
 */
@Named
@SessionScoped
@Stereotype
@Target(TYPE)
@Retention(RUNTIME)
public @interface MyController {

}
