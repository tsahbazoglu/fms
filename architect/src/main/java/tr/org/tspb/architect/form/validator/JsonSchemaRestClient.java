package tr.org.tspb.architect.form.validator;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 *
 * @author Telman Şahbazoğlu
 */
@Path("jsonschema")
@RequestScoped
@Produces(TEXT_PLAIN)
@RegisterRestClient(baseUri = "http://localhost:9898/api/")
public interface JsonSchemaRestClient {

    @GET
    @Produces(MediaType.TEXT_HTML + ";charset=utf-8")
    @Consumes(MediaType.TEXT_HTML + ";charset=utf-8")
    @Path("form")
    public Response form(String json);

}
