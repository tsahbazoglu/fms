package tr.org.tspb.pivot.datamodel;

/**
 *
 * @author Telman Şahbazoğlu
 */
public interface PivotDataModel {

    public int getHandsonRowHeaderWidth();

    public void setHandsonRowHeaderWidth(int handsonRowHeaderWidth);

    public int getHandsonColWidths();

    public void setHandsonColWidths(int handsonColWidths);

    public String getJsonColHeaders();

    public void setJsonColHeaders(String jsonColHeaders);

    public String getJsonRowHeaders();

    public void setJsonRowHeaders(String jsonRowHeaders);

    public String getJsonColRenderers();

    public void setJsonColRenderers(String jsonColRenderers);

    public String getJsonData();

    public void setJsonData(String jsonData);

    public String getJsonDataToModel();

    public void setJsonDataToModel(String jsonDataToModel);

}
