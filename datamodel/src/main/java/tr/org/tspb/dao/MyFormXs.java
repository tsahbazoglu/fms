package tr.org.tspb.dao;

import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Telman Şahbazoğlu
 */
public interface MyFormXs {

    public MyProject getMyProject();

    public String getKey();

    public String getLoginFkField();

    public String printToConfigAnalyze(String fieldKey);

    public String getName();

    public Number getHistoryPosition();

    public Number getDimension();

    public String getFormType();

    public HashMap<String, Object> getDefaultCurrentQuery();

    public HashMap<String, Object> getDefaultHistoryQuery();

    public List<String> getZetDimension();

}
