package tr.org.tspb.common.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Telman Şahbazoğlu
 */
public abstract class AbstractDynamicFlow {

    protected Map input;
    protected Map output = new HashMap();

    public AbstractDynamicFlow(Map input) {
        this.input = input;

    }

    public Map getOutput() {
        return Collections.unmodifiableMap(output);
    }

    public Map getInput() {
        return Collections.unmodifiableMap(input);
    }

    public abstract void run();

    public abstract void makeRequiredAction();
}
