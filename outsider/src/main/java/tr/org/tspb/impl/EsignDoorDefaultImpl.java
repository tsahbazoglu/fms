package tr.org.tspb.impl;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import javax.enterprise.inject.Alternative;
import tr.org.tspb.dao.MyForm;
import tr.org.tspb.outsider.EsignDoor;

/**
 *
 * @author Telman Şahbazoğlu
 */
@Alternative
public class EsignDoorDefaultImpl implements EsignDoor {

    @Override
    public void eimzaContextInstance(Properties properties) {
        //
    }

    @Override
    public boolean isTest() {
        return true;
    }

    @Override
    public String getSignType() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void initAndShowEsignDlg(List<Map> list, MyForm selectedForm, String widgetVarName, String multiUnique) {
        //
    }

    @Override
    public void initEsignCtrl(MyForm selectedForm, List<Map> listOfCruds, String fullTextSearch, String multiUnique) {
        //
    }

    @Override
    public void initEsignCtrlV2(MyForm selectedForm, String fullTextSearch, String multiUnique) {
        //
    }

    @Override
    public void initEsignCtrlV3(MyForm myFormLarge) {
        //
    }

    @Override
    public void iniAndShowEsignDlgV1(TreeMap<Integer, String> treeMap, List<Map> listOfCruds, MyForm selectedForm, String widgetVarToBeSignedDialog, String UNIQUE) {
        //
    }

    @Override
    public void initAndFindEsigns(MyForm selectedForm, String fullTextSearch) {
        //
    }

    @Override
    public void initAndFindEsignsV1(MyForm selectedForm, String fullTextSearch, List<Map> listOfCruds, String multiUnique) {
        //
    }

}
