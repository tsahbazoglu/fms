package tr.org.tspb.outsider;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import tr.org.tspb.dao.MyForm;

/**
 *
 * @author Telman Şahbazoğlu
 */
public interface EsignDoor extends Serializable {

    public static final String ENVIRONMENT = "ENVIRONMENT";

    public void eimzaContextInstance(Properties properties);

    public boolean isTest();

    public String getSignType();

    public void initAndShowEsignDlg(List<Map> list, MyForm selectedForm, String widgetVarName, String multiUnique);

    public void initEsignCtrl(MyForm selectedForm, List<Map> listOfCruds, String fullTextSearch, String multiUnique);

    public void initEsignCtrlV3(MyForm myFormLarge);

    public void iniAndShowEsignDlgV1(TreeMap<Integer, String> treeMap, List<Map> listOfCruds, MyForm selectedForm, String widgetVarToBeSignedDialog, String UNIQUE);

    public void initEsignCtrlV2(MyForm selectedForm, String fullTextSearch, String multiUnique);

    public void initAndFindEsigns(MyForm selectedForm, String fullTextSearch);

    public void initAndFindEsignsV1(MyForm selectedForm, String fullTextSearch, List<Map> listOfCruds, String multiUnique);

}
