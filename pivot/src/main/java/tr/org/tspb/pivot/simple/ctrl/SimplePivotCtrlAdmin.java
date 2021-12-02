package tr.org.tspb.pivot.simple.ctrl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import tr.org.tspb.dao.FmsForm;
import tr.org.tspb.pivot.simple.datamodel.ItemProvider;
import tr.org.tspb.pivot.simple.datamodel.PivotRecord;
import tr.org.tspb.service.RepositoryService;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class SimplePivotCtrlAdmin implements Serializable {

    @Inject
    protected RepositoryService repositoryService;

    protected FmsForm myForm;
    protected ItemProvider itemProvider;
    private LazyDataModel<PivotRecord> pivotRecords;

    private PivotRecord selectedRow;

    public PivotRecord getRowData(String rowKey) {
        Map record = repositoryService.oneById(myForm.getDb(), myForm.getTable(), rowKey);
//TODO
        return new PivotRecord(itemProvider);
    }

    public void refreshLazy() {

        pivotRecords = new LazyDataModel<PivotRecord>() {

            @Override
            public List<PivotRecord> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
                return SimplePivotCtrlAdmin.this.load(null, false, first, pageSize, myForm);
            }

            @Override
            public PivotRecord getRowData(String rowKey) {
                return SimplePivotCtrlAdmin.this.getRowData(rowKey);
            }

            @Override
            public String getRowKey(PivotRecord pivotRecord) {
                return pivotRecord.getMongoIdAsString();
            }

        };

        pivotRecords.setRowCount(3000000);

    }

    public List load(String sortColumnName, boolean sortAscending, int startRow,
            int maxResults, FmsForm myForm) {

        Map searchedMap = new HashMap();

        if (myForm != null && myForm.getKey() != null) {
            try {

                Map sortMap = new HashMap();
                Map defaultSortField = myForm.getDefaultSortField();
                if (defaultSortField != null) {
                    sortMap.putAll(defaultSortField);
                }

                List<Map> cursor = repositoryService
                        .findSkipLimitAsLoggedUser(this.myForm.getDb(), myForm.getTable(), searchedMap, startRow, maxResults);

                List<PivotRecord> list = new ArrayList<>();

                for (Map doc : cursor) {
                    //TODO
                    list.add(new PivotRecord(itemProvider));
                }

                return list;

            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    public PivotRecord getSelectedRow() {
        return selectedRow;
    }

    public void setSelectedRow(PivotRecord selectedRow) {
        this.selectedRow = selectedRow;
    }

    public LazyDataModel<PivotRecord> getPivotRecords() {
        return pivotRecords;
    }

    public void setPivotRecords(LazyDataModel<PivotRecord> myRecords) {
        this.pivotRecords = myRecords;
    }

}
