package tr.org.tspb.datamodel.gui;

import java.util.List;
import java.util.Map;
import tr.org.tspb.exceptions.NullNotExpectedException;

/**
 *
 * @author Telman Şahbazoğlu
 */
public interface FmsOnFlyData {

    List<Map> findLazyData(int first, int pageSize) throws NullNotExpectedException;

    Map retriveRowData(String rowKey);

    int findDataCount() throws NullNotExpectedException;

    void warn(String clazzName, String errMsg, Exception ex);

}
