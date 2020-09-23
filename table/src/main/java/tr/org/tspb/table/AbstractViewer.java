package tr.org.tspb.table;

import tr.org.tspb.datamodel.gui.FmsOnFlyData;
import tr.org.tspb.common.services.AppScopeSrvCtrl;
import tr.org.tspb.common.services.LoginController;
import tr.org.tspb.exceptions.NullNotExpectedException;
import tr.org.tspb.common.qualifier.MyLoginQualifier;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.naming.Reference;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import static tr.org.tspb.constants.ProjectConstants.*;
import tr.org.tspb.dao.MyField;
import tr.org.tspb.dao.MyForm;
import tr.org.tspb.pojo.ComponentType;
import tr.org.tspb.service.FilterService;
import tr.org.tspb.common.services.BaseService;
import tr.org.tspb.service.FormService;
import tr.org.tspb.util.qualifier.KeepOpenQualifier;
import tr.org.tspb.util.service.DlgCtrl;
import tr.org.tspb.util.tools.MongoDbUtilIntr;

/**
 *
 * @author Telman Şahbazoğlu
 */
public abstract class AbstractViewer implements FmsOnFlyData, Serializable {

    @Inject
    protected Logger logger;

    @Inject
    @KeepOpenQualifier
    protected MongoDbUtilIntr mongoDbUtil;

    @Inject
    protected AppScopeSrvCtrl uysApplicationMB;

    @Inject
    @MyLoginQualifier
    protected LoginController loginController;

    @Inject
    protected BaseService baseService;

    @Inject
    protected DlgCtrl dialogController;

    @Inject
    protected FilterService filterService;

    @Inject
    protected FormService formService;

    private static final String NOT_SUPPORTED_YET = "Not supported yet.";

    private String selectedFieldKey;
    private LazyDataModel<Map> data;
    private MyField selectedField;

    public List<String> createFormMsg(MyForm myForm) {
        List<String> msgs = new ArrayList<>();

        if (myForm.getConstantNote() != null && !myForm.getConstantNote().isEmpty()) {
            msgs.add(myForm.getConstantNote());
        }
        if (myForm.getUserConstantNoteList() != null) {
            for (String message : myForm.getUserConstantNoteList()) {
                msgs.add(message);
            }
        }
        if (myForm.getMyActions().isSave() && myForm.getReadOnlyNote() != null) {
            msgs.add(myForm.getReadOnlyNote());
        }
        if (myForm.getFuncNote() != null) {

            String code = myForm.getFuncNote().getCode();

            Document commandResult = mongoDbUtil.runCommand(myForm.getDb(), code, filterService.getTableFilterCurrent());

            String commandResultValue = commandResult.getString(RETVAL);
            if (commandResultValue != null) {
                msgs.add(commandResultValue);
            }
        }
        return msgs;
    }

    public LazyDataModel<Map> getData() {
        return data;
    }

    public void setData(LazyDataModel<Map> data) {
        this.data = data;
    }

    public void refreshData1(final List list) throws NullNotExpectedException {

        data = new LazyDataModel<Map>() {

            boolean calculatedRowCount;
            int rowCount;

            @Override
            public List<Map> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                List<Map> randomCars = new ArrayList<>();
                for (Object obj : list) {
                    try {
                        randomCars.add(mongoDbUtil.wrapIt(formService.getMyForm().getDbo(), (Document) obj));
                    } catch (NullNotExpectedException ex) {
                        logger.error("error occured", ex);
                    }
                }
                return randomCars;
            }

            @Override
            public List<Map> load(int first, int pageSize, List<SortMeta> multiSortMeta, Map<String, Object> filters) {
                List<Map> randomCars = new ArrayList<>();
                for (Object obj : list) {
                    try {
                        randomCars.add(mongoDbUtil.wrapIt(formService.getMyForm().getDbo(), (Document) obj));
                    } catch (NullNotExpectedException ex) {
                        logger.error("error occured", ex);
                    }
                }
                return randomCars;
            }

            @Override
            public Map getRowData(String rowKey) {
                return AbstractViewer.this.retriveRowData(rowKey);
            }

            @Override
            public String getRowKey(Map object) {
                return object.get(MONGO_ID).toString();
            }

            @Override
            public int getRowCount() {
                return list.size();
            }
        };
    }

    @Override
    public Map retriveRowData(String rowKey) {
        ObjectId id = new ObjectId(rowKey);
        Document object = mongoDbUtil.findOne(formService.getMyForm().getDb(), formService.getMyForm().getTable(), new Document(MONGO_ID, id));
        return object;
    }

    public String getInputText() {
        return ComponentType.inputText.name();
    }

    public String getInputFile() {
        return ComponentType.inputFile.name();
    }

    public String getInputTextarea() {
        return ComponentType.inputTextarea.name();
    }

    public String getInputMask() {
        return ComponentType.inputMask.name();
    }

    public String getInputDate() {
        return ComponentType.inputDate.name();
    }

    public String getAutoComplete() {
        return "autoComplete";
    }

    public String getSelectOneMenu() {
        return ComponentType.selectOneMenu.name();
    }

    public String getSelectManyListbox() {
        return ComponentType.selectManyListbox.name();
    }

    public String getSelectOneRadio() {
        return ComponentType.selectOneRadio.name();
    }

    public String getSelectBooleanCheckbox() {
        return ComponentType.selectBooleanCheckbox.name();
    }

    public void setSelectProperties(Reference selectProperties) {
        throw new UnsupportedOperationException(NOT_SUPPORTED_YET);
    }

    public void setUysApplicationMB(AppScopeSrvCtrl uysApplicationMB) {
        this.uysApplicationMB = uysApplicationMB;
    }

    public String getSelectedFieldKey() {
        return selectedFieldKey;
    }

    public void setSelectedFieldKey(String selectedFieldKey) {
        this.selectedFieldKey = selectedFieldKey;
    }

    public TimeZone getTimeZone() {
        return java.util.TimeZone.getDefault();
    }

    public List<SelectItem> getEmailOptions() {
        List<SelectItem> selectItems = new ArrayList<>();
        selectItems.add(new SelectItem(null, SELECT_PLEASE));
        selectItems.add(new SelectItem(null, "Last Date"));
        selectItems.add(new SelectItem(null, "Last Week"));
        selectItems.add(new SelectItem(null, "Last Month"));
        return selectItems;
    }

    public void valueChangeListenerTelman(ValueChangeEvent event) {
        selectedFieldKey = (String) event.getNewValue();
    }

    public void putSessionMB(String mbName, Object objectMB) {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(mbName, objectMB);
    }

    protected void showOKMessage(String okMessage) {
        addMessage(null, MessageFormat.format("{0}", okMessage),
                okMessage,
                FacesMessage.SEVERITY_INFO);
    }

    protected void showNOKMessage(String nokMessage) {
        addMessage(null, MessageFormat.format("{0}", nokMessage),
                nokMessage,
                FacesMessage.SEVERITY_ERROR);
    }

    protected void addMessage(String componentId, String summary, String message, Severity severity) {
        FacesContext.getCurrentInstance().addMessage(componentId, new FacesMessage(severity, summary, message));
    }

    public Object getSearchObjectValue(String key) {
        return filterService.getTableFilterCurrent().get(key);
    }

    public void putSearchObjectValue(String key, Serializable value) {
        filterService.getTableFilterCurrent().put(key, value);
    }

    public void removeSearchObjectKey(String key) {
        filterService.getTableFilterCurrent().remove(key);
    }

    public Document getSearchObjectAsDbo() {
        return new Document(filterService.getTableFilterCurrent());
    }

    public MyField getSelectedField() {
        return selectedField;
    }

    public void setSelectedField(MyField selectedField) {
        this.selectedField = selectedField;
    }

    @Override
    public void warn(String clazzName, String errMsg, Exception ex) {
        logger.warn(errMsg, ex);
    }

}
