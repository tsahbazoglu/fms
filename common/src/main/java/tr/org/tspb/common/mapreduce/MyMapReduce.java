package tr.org.tspb.common.mapreduce;

import tr.org.tspb.util.tools.DocumentRecursive;
import tr.org.tspb.exceptions.MoreThenOneInListException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Telman Şahbazoğlu
 */
public abstract class MyMapReduce {

    Map<Map, Serializable> output = new HashMap<>();
    Map<Map, List> map = new HashMap<>();
    protected Map initParams;

    public MyMapReduce(Map initParams) {
        this.initParams = initParams;
    }

    public void map(Map recordMap) {
    }

    public Serializable reduce(Map key, List<Serializable> values) throws MoreThenOneInListException {
        return 999D;
    }

    protected void emit(Map key, Object value) {
        if (map.get(key) == null) {
            map.put(key, new ArrayList<>());
        }
        map.get(key).add(value);
    }

    public Map<Map, Serializable> mapReduce(List<DocumentRecursive> collection) throws MoreThenOneInListException {

        for (Map recordMap : collection) {
            map(recordMap);
        }

        for (Map keyMap : map.keySet()) {
            output.put(keyMap, reduce(keyMap, map.get(keyMap)));
        }

        return Collections.unmodifiableMap(output);
    }
}
