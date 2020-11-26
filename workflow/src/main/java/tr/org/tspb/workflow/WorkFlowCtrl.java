package tr.org.tspb.workflow;

import com.github.oxo42.stateless4j.StateMachine;
import com.github.oxo42.stateless4j.triggers.TriggerWithParameters1;
import com.mongodb.client.model.Filters;
import static tr.org.tspb.constants.ProjectConstants.MONGO_ID;
import static tr.org.tspb.constants.ProjectConstants.RETVAL;
import tr.org.tspb.exceptions.WorkflowExcepiton;
import tr.org.tspb.process.MyStateMachine;
import tr.org.tspb.process.MyStateMachineFactory;
import tr.org.tspb.util.service.DlgCtrl;
import tr.org.tspb.util.stereotype.MyController;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.primefaces.model.diagram.Connection;
import org.primefaces.model.diagram.DefaultDiagramModel;
import org.primefaces.model.diagram.DiagramModel;
import org.primefaces.model.diagram.Element;
import org.primefaces.model.diagram.connector.FlowChartConnector;
import org.primefaces.model.diagram.endpoint.BlankEndPoint;
import org.primefaces.model.diagram.endpoint.EndPoint;
import org.primefaces.model.diagram.endpoint.EndPointAnchor;
import org.primefaces.model.diagram.overlay.ArrowOverlay;
import org.primefaces.model.diagram.overlay.LabelOverlay;
import org.slf4j.Logger;
import tr.org.tspb.dao.MyField;
import tr.org.tspb.dao.MyForm;
import tr.org.tspb.dao.MyMap;
import tr.org.tspb.pojo.MyCommandResult;
import tr.org.tspb.common.qualifier.MyLoginQualifier;
import tr.org.tspb.common.services.LoginController;
import tr.org.tspb.outsider.FmsWorkFlow;
import tr.org.tspb.dao.MyFieldComparator;
import tr.org.tspb.factory.qualifier.OgmCreatorQualifier;
import tr.org.tspb.util.qualifier.KeepOpenQualifier;
import tr.org.tspb.util.tools.MongoDbUtilIntr;
import tr.org.tspb.wf.MyRule;
import tr.org.tspb.wf.MyTransition;
import tr.org.tspb.factory.cp.OgmCreatorIntr;
import tr.org.tspb.outsider.qualifier.MyWorkFlowQualifier;

/**
 *
 * @author Telman Şahbazoğlu
 */
@MyController
@MyWorkFlowQualifier
public class WorkFlowCtrl implements FmsWorkFlow {

    @Inject
    private DlgCtrl dialogController;

    @Inject
    @MyLoginQualifier
    private LoginController loginController;

    @Inject
    private Logger logger;

    @Inject
    @OgmCreatorQualifier
    private OgmCreatorIntr ogmCreator;

    @Inject
    @KeepOpenQualifier
    private MongoDbUtilIntr mongoDbUtil;

    private static final String SM_RESULT = "smResult";
    private static final String SM_TRIGGER = "smTrigger";
    private static final String SM_TRIGGER_DATE = "smTriggerDate";
    private static final String SM_PRE_STATE = "smPreState";
    private static final String SM_STATE = "smState";

    private String trigger;
    private List<String> triggers = new ArrayList<>();
    private DefaultDiagramModel model;
    private Map<String, Element> uniqueMyRuleMap;
    private MyForm myForm;
    private MyMap crudObject;
    private Document filter;
    private List<MyField> fieldsAsList = new ArrayList<>();
    private MyStateMachine myStateMachine;
    private boolean canstart;

    Document smPreState = new Document()
            .append("key", SM_PRE_STATE)
            .append("field", SM_PRE_STATE)
            .append("name", "Önceki Adım")
            .append("shortName", "Önceki Adım")
            .append("componentType", "inputText")
            .append("roleCheck", true)
            .append("rendered", true)
            .append("db", "anydb")
            .append("reportRendered", true)
            .append("readonly", true)
            .append("order", 1)
            .append("reportOrder", 1)
            .append("valueType", "java.lang.String")
            .append("style", "color: Red;")
            .append("accesscontrol", "")
            .append("version", true)
            .append("smfield", true)
            .append("", "");

    Document smTrigerDate = new Document()
            .append("key", SM_TRIGGER_DATE)
            .append("field", SM_TRIGGER_DATE)
            .append("name", "Tetik Tarihi")
            .append("shortName", "Tetik Taihi")
            .append("componentType", "inputDate")
            .append("roleCheck", true)
            .append("rendered", true)
            .append("db", "anydb")
            .append("reportRendered", true)
            .append("readonly", true)
            .append("order", 2)
            .append("reportOrder", 2)
            .append("datePattern", "dd.MM.yyyy")
            .append("valueType", "java.lang.Date")
            .append("style", "color: Red;")
            .append("accesscontrol", "")
            .append("version", true)
            .append("smfield", true)
            .append("", "");

    Document smTrigger = new Document()
            .append("key", SM_TRIGGER)
            .append("field", SM_TRIGGER)
            .append("name", "Yapılan Tetikleme")
            .append("shortName", "Yapılan Tetikleme")
            .append("componentType", "inputText")
            .append("roleCheck", true)
            .append("rendered", true)
            .append("db", "anydb")
            .append("reportRendered", true)
            .append("readonly", true)
            .append("order", 3)
            .append("reportOrder", 3)
            .append("valueType", "java.lang.String")
            .append("style", "color: Red;")
            .append("accesscontrol", "")
            .append("version", true)
            .append("smfield", true)
            .append("", "");

    Document smResult = new Document()
            .append("key", SM_RESULT)
            .append("field", SM_RESULT)
            .append("name", "Yapılan Tetik Sonucu")
            .append("shortName", "Yapılan Tetik Sonucu")
            .append("componentType", "inputText")
            .append("roleCheck", true)
            .append("rendered", true)
            .append("db", "anydb")
            .append("reportRendered", true)
            .append("readonly", true)
            .append("order", 4)
            .append("reportOrder", 4)
            .append("valueType", "java.lang.String")
            .append("style", "color: Red;")
            .append("accesscontrol", "")
            .append("version", true)
            .append("smfield", true)
            .append("", "");

    Document smState = new Document()
            .append("key", SM_STATE)
            .append("field", SM_STATE)
            .append("name", "Mevcut Adım")
            .append("shortName", "Mevcut Adım")
            .append("componentType", "inputText")
            .append("roleCheck", true)
            .append("rendered", true)
            .append("db", "anydb")
            .append("reportRendered", true)
            .append("readonly", true)
            .append("order", 5)
            .append("reportOrder", 5)
            .append("valueType", "java.lang.String")
            .append("style", "color: Red;")
            .append("accesscontrol", "")
            .append("version", true)
            .append("smfield", true)
            .append("", "");

    public List<Document> getHistory() {
        List<Document> list = new ArrayList<>();
        if (crudObject != null) {
            Document doc = mongoDbUtil.findOne(myForm.getDb(), myForm.getTable(), Filters.eq(MONGO_ID, crudObject.get(MONGO_ID)));
            list = doc.getList("wfHistory", Document.class, new ArrayList<>());
        }
        return list;
    }

    @Override
    public void init(MyForm myForm, MyMap crudbject, Document filter) {
        this.myForm = myForm;
        this.crudObject = crudbject;
        this.filter = filter;

        MyField smPreStateField = ogmCreator.getMyField(myForm, smPreState, filter, loginController.getRoleMap(), loginController.getLoggedUserDetail());
        MyField smStateField = ogmCreator.getMyField(myForm, smState, filter, loginController.getRoleMap(), loginController.getLoggedUserDetail());
        MyField smResultField = ogmCreator.getMyField(myForm, smResult, filter, loginController.getRoleMap(), loginController.getLoggedUserDetail());
        MyField smTriggerField = ogmCreator.getMyField(myForm, smTrigger, filter, loginController.getRoleMap(), loginController.getLoggedUserDetail());
        MyField smTriggerDateField = ogmCreator.getMyField(myForm, smTrigerDate, filter, loginController.getRoleMap(), loginController.getLoggedUserDetail());

        this.fieldsAsList = new ArrayList<>();

        this.fieldsAsList.add(smPreStateField);
        this.fieldsAsList.add(smStateField);
        this.fieldsAsList.add(smResultField);
        this.fieldsAsList.add(smTriggerField);
        this.fieldsAsList.add(smTriggerDateField);

        Collections.sort(fieldsAsList, new MyFieldComparator());
    }

    public void trigWorkFlow(MyCommandResult smControlResult) {

        if (myStateMachine != null) {
            MyRule myRule = getMyRule(myStateMachine.getStateMachine());
            bringGuiImpactOfStateMachine(myStateMachine.getStateMachine(), myRule, smControlResult);
        }
    }

    public List<String> getTriggers() {
        return Collections.unmodifiableList(triggers);
    }

    public String getTrigger() {
        return trigger;
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

    public boolean isCanStartProcess() {
        return canstart;
    }

    public String createModel() {

        model = new DefaultDiagramModel();
        model.setMaxConnections(-1);
        model.setConnectionsDetachable(false);

        model.getDefaultConnectionOverlays().add(new ArrowOverlay(20, 20, 1, 1));
        //StraightConnector connector = new StraightConnector();
        FlowChartConnector connector = new FlowChartConnector();
        connector.setPaintStyle("{strokeStyle:'#98AFC7', lineWidth:3}");

        connector.setHoverPaintStyle("{strokeStyle:'#5C738B'}");
        model.setDefaultConnector(connector);

        collectWorkflowRules();

        for (MyRule myRule : myForm.getWorkflowSteps()) {

            Element el = getElement(myRule.getCode());

            el.setX(myRule.getGuiX() + "em");
            el.setY(myRule.getGuiY() + "em");
            model.addElement(el);

            for (MyTransition myTransition : myRule.getTransitions()) {

                EndPointAnchor sourceEndPointAnchor = EndPointAnchor.BOTTOM;
                EndPointAnchor destinationEndPointAnchor = EndPointAnchor.TOP;

                switch (myTransition.getSrcpoint()) {
                    case "bottom":
                        sourceEndPointAnchor = EndPointAnchor.BOTTOM;
                        break;
                    case "top":
                        sourceEndPointAnchor = EndPointAnchor.TOP;
                        break;
                    case "left":
                        sourceEndPointAnchor = EndPointAnchor.LEFT;
                        break;
                    case "right":
                        sourceEndPointAnchor = EndPointAnchor.RIGHT;
                        break;
                    default:
                        break;
                }

                switch (myTransition.getEndpoint()) {
                    case "bottom":
                        destinationEndPointAnchor = EndPointAnchor.BOTTOM;
                        break;
                    case "top":
                        destinationEndPointAnchor = EndPointAnchor.TOP;
                        break;
                    case "left":
                        destinationEndPointAnchor = EndPointAnchor.LEFT;
                        break;
                    case "right":
                        destinationEndPointAnchor = EndPointAnchor.RIGHT;
                        break;
                    default:
                        break;
                }

                EndPoint currentEndPoint = new BlankEndPoint(sourceEndPointAnchor);//DotEndPoint
                el.addEndPoint(currentEndPoint);

                Element nextElement = getElement(myTransition.getState());
                EndPoint nextEndPoint = new BlankEndPoint(destinationEndPointAnchor);

                if (nextElement == null) {
                    throw new RuntimeException(myTransition.getState().concat(" is not defined"));
                }
                nextElement.addEndPoint(nextEndPoint);

                Connection conn = new Connection(currentEndPoint, nextEndPoint);
                conn.getOverlays().add(new LabelOverlay(myTransition.getTrigger(), "ui-diagram-label", 0.5));

                model.connect(conn);
            }

            //StraightConnector connector1 = new StraightConnector();
            //FlowChartConnector connector2 = new FlowChartConnector();
            //model.connect(new Connection(right, left, new StraightConnector()));
        }

        return null;
    }

    public void armWorkflowRules() {
        for (MyRule myRule : myForm.getWorkflowSteps()) {
            try {
                myRule.maskFields(myForm, filter, loginController.getRoleMap());
                myRule.arrangeActions(myForm, loginController.getRoleMap(), filter);
            } catch (Exception ex) {
                logger.error("error occured", ex);
            }
        }
    }

    public String show() {
        try {
            localShowWorkFlow();
            dialogController.showPopup("wv-work-flow");
        } catch (WorkflowExcepiton ex) {
            logger.error("error occured", ex);
            dialogController.showPopupError(ex.getMessage());
        }
        return null;
    }

    private void refreshCanStart() {
        Document document = mongoDbUtil.findOne(myForm.getDb(), myForm.getTable(), Filters.eq(MONGO_ID, crudObject.get(MONGO_ID)));
        canstart = document.get(SM_STATE) == null;
    }

    private String localShowWorkFlow() throws WorkflowExcepiton {

        if (crudObject == null || crudObject.isNew()) {
            throw new WorkflowExcepiton("could not start workflow process on null or new object");
        }

        Document crudDoc = mongoDbUtil.findOne(myForm.getDb(), myForm.getTable(), Filters.eq(MONGO_ID, crudObject.get(MONGO_ID)));

        refreshCanStart();

        createModel();

        myStateMachine = createStateMachine(crudDoc);

        if (myStateMachine != null) {
            Element element = getElement(myStateMachine.getStateMachine().getState());
            if (element != null) {
                MyRule myRule = (MyRule) element.getData();
                bringGuiImpactOfStateMachine(myStateMachine.getStateMachine(), myRule, controleStateMachineRule(myRule));
            }
            highlightDiagramSmState(myStateMachine.getStateMachine(), uniqueMyRuleMap);
        } else {
            resetTriggers();
        }

        return null;
    }

    public String startProcess() throws WorkflowExcepiton {

        if (crudObject.isNew()) {
            throw new WorkflowExcepiton("couldnot staet process on new Object");
        }

        try {

            myStateMachine = MyStateMachineFactory
                    .instance(this, mongoDbUtil)
                    .buildStateMachine(myForm.getWorkflowSteps(), myForm.getWorkflowStartStep());

            String state = myStateMachine.getStateMachine().getState();

            Element element = uniqueMyRuleMap.get(state);

            if (element == null) {
                dialogController.showPopupError(String.format("element on state %s is resolved to null ", state));
                return null;
            }

            MyRule myRule = (MyRule) element.getData();

            MyCommandResult myCommandResult = controleStateMachineRule(myRule);

            saveSmStateOnObject(myStateMachine.getStateMachine(), myCommandResult, "START");

            bringGuiImpactOfStateMachine(myStateMachine.getStateMachine(), myRule, myCommandResult);

        } catch (Exception ex) {
            logger.error("could not start process", ex);
            dialogController.showPopupError("Beklenmeyen Hata Oluştu");
        }

        refreshCanStart();

        return null;
    }

    public String fireProcess() {

        if (myStateMachine != null) {

            for (String t : myStateMachine.getStateMachine().getPermittedTriggers()) {

                if (trigger.equals(t) && myStateMachine.getStateMachine().canFire(t)) {

                    TriggerWithParameters1 twp1 = (TriggerWithParameters1) myStateMachine.getStateMachineConfig()
                            .getTriggerConfiguration(trigger);

                    if (twp1 == null) {
                        twp1 = new TriggerWithParameters1<>(t, WorkFlowCtrl.class);
                    }

                    myStateMachine.getStateMachine().fire(twp1, this);

                    MyRule myRule = (MyRule) getElement(myStateMachine.getStateMachine().getState()).getData();

                    MyCommandResult myCommandResult = controleStateMachineRule(myRule);

                    saveSmStateOnObject(myStateMachine.getStateMachine(), myCommandResult, trigger);

                    bringGuiImpactOfStateMachine(myStateMachine.getStateMachine(), myRule, myCommandResult);
                }
            }
        }

        refreshCanStart();

        return null;
    }

    private MyCommandResult controleStateMachineRule(MyRule myRule) {
        Document commandResult = mongoDbUtil.runCommand("test", myRule.getControlFunc().getCode(), crudObject, loginController.getRolesAsSet());
        return getControlResult(commandResult);
    }

    public void highlightDiagramSmState(StateMachine<String, String> sm, Map<String, Element> elementMap) {
        if (sm == null) {
            return;
        }

        elementMap.get(sm.getState()).setStyleClass("ui-diagram-element-entry");
    }

    public DiagramModel getModel() {
        return model;
    }

    public void setModel(DefaultDiagramModel model) {
        this.model = model;
    }

    public void resetModel() {
        this.model = null;
    }

    public Map<String, Element> getUniqueMyRuleMap() {
        return Collections.unmodifiableMap(uniqueMyRuleMap);
    }

    public void createTriggers(MyMap myMap) {
        triggers = Collections.emptyList();
        if (myStateMachine != null) {
            triggers = myStateMachine.getStateMachine().getPermittedTriggers();
        }
    }

    public MyCommandResult saveObjTrig(MyCommandResult smControlResult) {

        if (myStateMachine != null) {
            MyRule myRule = getMyRule(myStateMachine.getStateMachine());

            crudObject.put(SM_STATE, myStateMachine.getStateMachine().getState());

            if (smControlResult == null) {

                smControlResult = controleStateMachineRule(myRule);

                crudObject.put(SM_TRIGGER, crudObject.isNew() ? "CREATE_RECORD" : "UPDATE_RECORD");

                bringGuiImpactOfStateMachine(myStateMachine.getStateMachine(), myRule, smControlResult);
            }

            crudObject.put(SM_RESULT, smControlResult.getMsg());
        }
        return smControlResult;
    }

    private MyCommandResult getControlResult(Document commandResult) {

        Object retval = commandResult.get(RETVAL);

        boolean result = Boolean.TRUE.equals(retval);
        String msg = result ? "Kısıtlayıcı Kontrol Yok" : "Veri Eksikliğini Düzeltiniz";

        if (retval instanceof Document) {
            Document resultDBO = (Document) retval;
            result = Boolean.TRUE.equals(resultDBO.get("result"));
            msg = resultDBO.get("msg").toString();
        }

        return new MyCommandResult(result, msg);
    }

    private void saveSmStateOnObject(StateMachine stateMachine, MyCommandResult mcr, String smTrigger) {

        Document crud = mongoDbUtil.findOne(myForm.getDb(), myForm.getTable(), Filters.eq(MONGO_ID, crudObject.get(MONGO_ID)));

        Bson updateFilter = Filters.eq(MONGO_ID, crudObject.get(MONGO_ID));

        Date now = new Date();

        Document update = new Document()
                .append(SM_PRE_STATE, crud.get(SM_STATE))
                .append(SM_STATE, stateMachine.getState())
                .append(SM_TRIGGER, smTrigger)
                .append(SM_TRIGGER_DATE, now)
                .append(SM_RESULT, mcr.getMsg());

        mongoDbUtil.updateOne(myForm.getDb(), myForm.getTable(), updateFilter, update);
        mongoDbUtil.updatePush(myForm.getDb(), myForm.getTable(), updateFilter, new Document("wfHistory", update));
    }

    private void collectWorkflowRules() {
        armWorkflowRules();
        //collect rule elements
        uniqueMyRuleMap = new HashMap<>();
        for (MyRule myRule : myForm.getWorkflowSteps()) {
            uniqueMyRuleMap.put(myRule.getCode(), new Element(myRule));
        }
    }

    private void bringGuiImpactOfStateMachine(StateMachine<String, String> stateMachine, MyRule myRule, MyCommandResult mcr) {

        highlightDiagramSmState(stateMachine, uniqueMyRuleMap);

        renderFields(stateMachine);

        createTriggers(crudObject);

        myForm.getMyActions().merge(myRule.getMyActions());

        if (!mcr.getResult()) {

            String err = new StringBuilder()
                    .append("adım : ")
                    .append(stateMachine.getState())
                    .append(" - ")
                    .append(getMyRule(stateMachine).getName())
                    .append("<hr/>")
                    .append("kontrol : ")
                    .append(mcr.getMsg()).toString();

            FacesMessage facesMessageRequired = new FacesMessage(FacesMessage.SEVERITY_ERROR, err, "*");
            FacesContext.getCurrentInstance().addMessage(null, facesMessageRequired);

        }
        if (myRule.isLast()) {
            getElement(stateMachine.getState()).setStyleClass("ui-diagram-element-exit");
        }

    }

    private MyRule getMyRule(StateMachine<String, String> stateMachine) {
        return (MyRule) uniqueMyRuleMap.get(stateMachine.getState()).getData();
    }

    private Element getElement(String state) {
        return uniqueMyRuleMap.get(state);
    }

    private MyStateMachine createStateMachine(Document crudDoc) {
        String crudObjectSmState = (String) crudDoc.get(SM_STATE);
        if (crudObjectSmState == null) {
            return null;
        }
        return MyStateMachineFactory
                .instance(this, mongoDbUtil)
                .buildStateMachine(myForm.getWorkflowSteps(), crudObjectSmState);
    }

    public List<MyField> getFieldsAsList() {
        return Collections.unmodifiableList(fieldsAsList);
    }

    private void renderFields(StateMachine<String, String> sm) {
        MyRule currentRule = (MyRule) getElement(sm.getState()).getData();
        for (String fieldKey : currentRule.getFields().keySet()) {
            myForm.getField(fieldKey).setRendered(true);
            myForm.getField(fieldKey).createSelectItems(filter,
                    crudObject,
                    loginController.getRoleMap(),
                    loginController.getLoggedUserDetail(),
                    false);
        }
    }

    private void resetActions() {
        myForm.getMyActions().reset();
    }

    private void resetTriggers() {
        triggers = Collections.emptyList();
    }

    public MyMap getCrudObject() {
        return crudObject;
    }

    @Override
    public void reset() {
        this.fieldsAsList = new ArrayList<>();
        resetTriggers();
        this.model = new DefaultDiagramModel();
    }

    public LoginController getLoginController() {
        return loginController;
    }

}
