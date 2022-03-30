/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tr.org.tspb.datamodel.gui;

/**
 *
 * @author telman
 */
public class FormDef {

    private String value;
    private String label;
    private String jsfupdate;
    private static final String COMMA = ",";
    private static final String ID_TWO_DIM_DLG_PNL = "";//"crud2dForm:crud_2d_dlg_panel";
    private static final String ID_MSG_DLG = "idMessageDialog";
    private static final String ID_CENTER_MIDDLE = "idCenterCenter";
    private static final String ID_DLGS = "id-dlgs";
    private static final String ID_CENTER_TOP = "idCenterTop";
    private static final String ID_DIM_BASED_DLGS = "id-dim-based-dlgs";

    private String menuSelectForm = ID_MSG_DLG
            // .concat(COMMA).concat(ID_CRUD_OPS)
            .concat(COMMA).concat(ID_TWO_DIM_DLG_PNL)
            .concat(COMMA).concat(ID_CENTER_MIDDLE)
            .concat(COMMA).concat(ID_DLGS)
            .concat(COMMA).concat(ID_CENTER_TOP)
            .concat(COMMA).concat(ID_DIM_BASED_DLGS);

    public FormDef(String value, String label, String jsfupdate) {
        this.value = value;
        this.label = label;
        this.jsfupdate = jsfupdate;
    }

    public FormDef(String value, String label) {
        this.value = value;
        this.label = label;
        this.jsfupdate = menuSelectForm.concat(" iceformLeft");
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @return the jsfupdate
     */
    public String getJsfupdate() {
        return jsfupdate;
    }

}
