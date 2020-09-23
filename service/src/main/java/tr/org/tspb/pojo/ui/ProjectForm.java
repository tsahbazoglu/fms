package tr.org.tspb.pojo.ui;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class ProjectForm {

    private String page;
    private final String projectKey;
    private final String formKey;

    public ProjectForm(String json) throws Exception {
        if (json == null) {
            throw new Exception("no content");
        }

        JsonParser parser = new JsonParser();

        JsonElement element = parser.parse(json);

        JsonObject jsonObject = element.getAsJsonObject();

        if (!jsonObject.has("project-key")) {
            throw new Exception("'project-key' is missed");
        }

        if (!jsonObject.has("form-key")) {
            throw new Exception("'form-key' is missed");
        }

        if (jsonObject.has("!page")) {
            page = jsonObject.get("!page").getAsString();
        }

        projectKey = jsonObject.get("project-key").getAsString();
        formKey = jsonObject.get("form-key").getAsString();

    }

    public String getProjectKey() {
        return projectKey;
    }

    public String getFormKey() {
        return formKey;
    }

    public String getPage() {
        return page;
    }

}
