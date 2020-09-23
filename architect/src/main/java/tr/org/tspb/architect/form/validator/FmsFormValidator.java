package tr.org.tspb.architect.form.validator;

import javax.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class FmsFormValidator {

    @Inject
    @RestClient
    private JsonSchemaRestClient jsonSchemaRestClient;

    public String validate() {

        Object obj = jsonSchemaRestClient.form(null).getEntity();

        JSONObject jsonSchema = new JSONObject(
                new JSONTokener(FmsFormValidator.class.getResourceAsStream("fms-form-schema.json")));
        JSONObject jsonSubject = new JSONObject(
                new JSONTokener(FmsFormValidator.class.getResourceAsStream("form.json")));

        Schema schema = SchemaLoader.load(jsonSchema);

        try {
            schema.validate(jsonSubject);
            return null;
        } catch (ValidationException ex) {
            return ex.getLocalizedMessage();
        }
    }
}
