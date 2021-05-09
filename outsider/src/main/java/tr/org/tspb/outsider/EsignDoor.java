package tr.org.tspb.outsider;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import tr.org.tspb.dao.FmsForm;

/**
 *
 * @author Telman Şahbazoğlu
 */
public interface EsignDoor extends Serializable {

    public static final String ENVIRONMENT = "ENVIRONMENT";

    public void eimzaContextInstance(Properties properties);

    public boolean isTest();

    public String getSignType();

    public void initAndShowEsignDlg(List<Map> list, FmsForm selectedForm, String widgetVarName, String multiUnique);

    public void initEsignCtrl(FmsForm selectedForm, List<Map> listOfCruds, String fullTextSearch, String multiUnique);

    public void initEsignCtrlV3(FmsForm myFormLarge);

    public void iniAndShowEsignDlgV1(TreeMap<Integer, String> treeMap, List<Map> listOfCruds,
            FmsForm selectedForm, String widgetVarToBeSignedDialog, String UNIQUE);

    public void initEsignCtrlV2(FmsForm selectedForm, String fullTextSearch, String multiUnique);

    public void initAndFindEsigns(FmsForm selectedForm, String fullTextSearch);

    public void initAndFindEsignsV1(FmsForm selectedForm, String fullTextSearch, List<Map> listOfCruds, String multiUnique);

}
