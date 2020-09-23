package tr.org.tspb.process;

import com.github.oxo42.stateless4j.StateConfiguration;
import tr.org.tspb.constants.ProjectConstants;
import com.github.oxo42.stateless4j.StateMachine;
import com.github.oxo42.stateless4j.StateMachineConfig;
import com.github.oxo42.stateless4j.delegates.Action;
import com.github.oxo42.stateless4j.delegates.Action1;
import com.github.oxo42.stateless4j.delegates.FuncBoolean;
import com.github.oxo42.stateless4j.triggers.TriggerWithParameters1;
import java.util.HashMap;
import tr.org.tspb.pojo.MyCommandResult;
import java.util.List;
import java.util.Map;
import org.bson.Document;
import tr.org.tspb.util.tools.MongoDbUtilIntr;
import tr.org.tspb.wf.MyRule;
import tr.org.tspb.wf.MyTransition;
import tr.org.tspb.workflow.WorkFlowCtrl;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class MyStateMachineFactory {

    private MongoDbUtilIntr mongoDbUtil;

    private WorkFlowCtrl workFlowCtrl;

    private static final MyStateMachineFactory INSTANCE = new MyStateMachineFactory();

    public static MyStateMachineFactory instance(WorkFlowCtrl twoDimModifyCtrl, MongoDbUtilIntr mongoDbUtil) {
        INSTANCE.workFlowCtrl = twoDimModifyCtrl;
        INSTANCE.mongoDbUtil = mongoDbUtil;
        return INSTANCE;
    }

    public static MyStateMachineFactory instance() {
        return INSTANCE;
    }

    private MyStateMachineFactory() {
    }

    public MyStateMachine buildStateMachine(List<MyRule> myRules, String state) {

        StateMachineConfig<String, String> stateMachineConfig = new StateMachineConfig();
        StateMachine<String, String> stateMachine = new StateMachine(state, stateMachineConfig);

        Map<String, MyRule> ruleMap = new HashMap<>();
        for (final MyRule myRule : myRules) {
            ruleMap.put(myRule.getCode(), myRule);
        }

        for (final MyRule myRule : myRules) {

            StateConfiguration stateConfiguration = stateMachineConfig.configure(myRule.getCode());

            for (final MyTransition myTransition : myRule.getTransitions()) {

                String expandedTrigger = myRule.getCode() + "_" + myTransition.getTrigger();

                TriggerWithParameters1 twp1 = stateMachineConfig.setTriggerParameters(expandedTrigger, WorkFlowCtrl.class);

                stateConfiguration
                        .permitIf(expandedTrigger, myTransition.getState(),
                                new MyFuncBoolean(myTransition.getTrigger(), myRule.getControlFunc().getCode()));

                String nextRuleCode = myTransition.getState();
                MyRule nextRule = ruleMap.get(nextRuleCode);

                stateMachineConfig.configure(nextRuleCode)
                        .onEntry(new OnEntryAction(nextRule.getOnEntry().getCode(), expandedTrigger))
                        .onEntryFrom(twp1, new OnEntryFromAction(nextRule.getOnEntryFrom().getCode(), expandedTrigger), WorkFlowCtrl.class);

            }
        }

        return new MyStateMachine(stateMachine, stateMachineConfig);
    }

    private class OnEntryAction implements Action {

        private String mongoCode;
        private String trigger;

        public OnEntryAction(String mongoCode, String trigger) {
            this.mongoCode = mongoCode;
            this.trigger = trigger;
        }

        @Override
        public void doIt() {
//            Document result = MongoDbUtil.instance().runCommand("test", mongoCode, workFlowCtrl.getCrudObject(), trigger);
//            System.out.println(result);
        }

    }

    private class OnEntryFromAction implements Action1<WorkFlowCtrl> {

        private String mongoCode;
        private String trigger;

        public OnEntryFromAction(String mongoCode, String trigger) {
            this.mongoCode = mongoCode;
            this.trigger = trigger;
        }

        @Override
        public void doIt(WorkFlowCtrl t) {
            Document result = mongoDbUtil.runCommand("test", mongoCode, workFlowCtrl.getCrudObject(), trigger);
            System.out.println(result);
        }

    }

    private class MyFuncBoolean implements FuncBoolean {

        private final String trigger;
        private final String code;

        public MyFuncBoolean(String trigger, String code) {
            this.trigger = trigger;
            this.code = code;
        }

        @Override
        public boolean call() {
            Document commandResult = mongoDbUtil.runCommand("test", code, workFlowCtrl.getCrudObject(), workFlowCtrl.getLoginController().getRolesAsSet());
            return getControlResult(commandResult, trigger).getResult();
        }

    }

    private MyCommandResult getControlResult(Document commandResult, String trigger) {

        Object retval = commandResult.get(ProjectConstants.RETVAL);

        boolean result = Boolean.TRUE.equals(retval);
        String msg = result ? "Kısıtlayıcı Kontrol Yok" : "Veri Eksikliğini Düzeltiniz";

        if (retval instanceof Document) {
            Document resultDBO = (Document) retval;
            String calculatedTriggers = resultDBO.getString("trigger");
            result = calculatedTriggers == null ? false : calculatedTriggers.contains(trigger);
            msg = resultDBO.get("msg").toString();
        }

        return new MyCommandResult(result, msg);
    }

    public enum Workflow {
        APPLICATION,
        INVOICE,
        GELENEVRAK,
        GIDENEVRAK
    }

}
