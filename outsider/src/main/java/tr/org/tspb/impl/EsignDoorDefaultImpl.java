package tr.org.tspb.impl;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import javax.enterprise.inject.Alternative;
import tr.org.tspb.dao.FmsForm;
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
    public void initAndShowEsignDlg(List<Map> list, FmsForm selectedForm, String widgetVarName, String multiUnique) {
        //
    }

    @Override
    public void initEsignCtrl(FmsForm selectedForm, List<Map> listOfCruds, String fullTextSearch, String multiUnique) {
        //
    }

    @Override
    public void initEsignCtrlV2(FmsForm selectedForm, String fullTextSearch, String multiUnique) {
        //
    }

    @Override
    public void initEsignCtrlV3(FmsForm myFormLarge) {
        //
    }

    @Override
    public void iniAndShowEsignDlgV1(TreeMap<Integer, String> treeMap, List<Map> listOfCruds,
            FmsForm selectedForm, String widgetVarToBeSignedDialog, String UNIQUE) {
        //
    }

    @Override
    public void initAndFindEsigns(FmsForm selectedForm, String fullTextSearch) {
        //
    }

    @Override
    public void initAndFindEsignsV1(FmsForm selectedForm, String fullTextSearch, List<Map> listOfCruds, String multiUnique) {
        //
    }

}
