package tr.org.tspb.datamodel.gui;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import static tr.org.tspb.constants.ProjectConstants.MONGO_ID;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class FmsTableDataModel extends LazyDataModel<Map> {

    private final FmsOnFlyData fmsOnFlyData;
    private int rowCount;
    private List<Map> listOfData;
    private int countOfData;

    public void initRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public void emptyListOfData() {
        this.listOfData = null;
    }

    public FmsTableDataModel(FmsOnFlyData fmsOnFlyData) {
        this.fmsOnFlyData = fmsOnFlyData;
    }

    @Override
    public List<Map> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        try {
            listOfData = fmsOnFlyData.findLazyData(first, pageSize);
            return listOfData;
        } catch (Exception ex) {
            fmsOnFlyData.warn(this.getClass().getName(), "load data", ex);
            return Collections.emptyList();
        }
    }

    @Override
    public Map getRowData(String rowKey) {
        return fmsOnFlyData.retriveRowData(rowKey);
    }

    @Override
    public String getRowKey(Map object) {
        return object.get(MONGO_ID).toString();
    }

    @Override
    public int getRowCount() {
        return rowCount;
    }

    @Override
    public int count(Map<String, FilterMeta> map) {
        return rowCount;
    }
}
