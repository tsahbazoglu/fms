package tr.org.tspb.process;

import com.github.oxo42.stateless4j.StateConfiguration;
import com.github.oxo42.stateless4j.StateMachine;
import com.github.oxo42.stateless4j.StateMachineConfig;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class MyStateMachine {

    private final StateMachine<String, String> stateMachine;
    private final StateMachineConfig stateMachineConfig;
    private StateConfiguration stateConfiguration;

    public MyStateMachine(StateMachine stateMachine, StateMachineConfig stateMachineConfig) {
        this.stateMachine = stateMachine;
        this.stateMachineConfig = stateMachineConfig;
    }

    public StateMachine<String, String> getStateMachine() {
        return stateMachine;
    }

    public StateMachineConfig getStateMachineConfig() {
        return stateMachineConfig;
    }

    public StateConfiguration getStateConfiguration() {
        return stateConfiguration;
    }

}
