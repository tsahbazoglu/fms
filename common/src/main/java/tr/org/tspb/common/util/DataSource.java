package tr.org.tspb.common.util;

import java.util.ArrayList;
import java.util.List;
import tr.org.tspb.dao.MyForm;

/**
 *
 * @author Telman Şahbazoğlu
 */
public abstract class DataSource {
    // Sortable Headers

    protected String sortColumnName;
    protected boolean sortAscending;
    // DataModel bound to dataTable
    protected PagedListDataModel onePageRowDataModel;
    // bound to rows attribute in dataTable
    protected List lazyData = new ArrayList(getPageSize());
    protected List selectedList = new ArrayList();

    protected DataSource(String defaultSortColumn) {
        this.sortColumnName = defaultSortColumn;
        this.sortAscending = isDefaultAscending(defaultSortColumn);
    }

    public void setOnePageRowDataModel(PagedListDataModel onePageRowDataModel) {
        this.onePageRowDataModel = onePageRowDataModel;
    }

    public abstract List findPageData(//
            String sortColumnName, boolean sortAscending, int startRow, int maxResults, MyForm form);

    public abstract Long findTotalNumber(MyForm form);

    /**
     * This is where the Customer data is retrieved from the database and
     * returned as a list of CustomerBean objects for display in the UI.
     */
    protected DataPage getDataPage(int startRow, int pageSize, int dimesion, MyForm form) {
        // Retrieve the total number of customers from the database.  This
        // number is required by the DataPage object so the paginator will know
        // the relative location of the page data.
        Long total = findTotalNumber(form);

        int totalNumberCustomers = total == null ? 0 : total.intValue();

        // Calculate indices to be displayed in the ui.
        int endIndex = startRow + pageSize;
        if (endIndex > totalNumberCustomers) {
            endIndex = totalNumberCustomers;
        }

        // Query database for sorted results.
        //BEGIN : COLLECT previously selected objects/rows
//        if (lazyData != null) {
//            for (Object object : lazyData) {
//                if (Boolean.TRUE.equals(((BaseEntity) object).getTransfer())) {
//                    if (!selectedList.contains(object)) {
//                        selectedList.add(object);
//                    }
//                } else {
//                    if (selectedList.contains(object)) {
//                        selectedList.remove(object);
//                    }
//                }
//            }
//        }
        //BEGIN : COLLECT previously selected objects/rows
        lazyData = findPageData(sortColumnName, sortAscending, startRow, endIndex - startRow, form);

        onePageRowDataModel.setDirtyData(false);

        return new DataPage(totalNumberCustomers, startRow, lazyData);
    }

    /**
     * Is the default sortColumnName direction for the given column
     * "sortAscending" ?
     */
    protected abstract boolean isDefaultAscending(String sortColumn);

    /**
     * Gets the sortColumnName column.
     *
     * @return column to sortColumnName
     */
    public String getSortColumnName() {
        return sortColumnName;
    }

    /**
     * Sets the sortColumnName column.
     *
     * @param sortColumnName column to sortColumnName
     */
    public void setSortColumnName(String sortColumnName) {
        if (!sortColumnName.equals(this.sortColumnName)) {
            onePageRowDataModel.setDirtyData();
            this.sortColumnName = sortColumnName;
        }
    }

    /**
     * Is the sortColumnName sortAscending?
     *
     * @return true if the sortAscending sortColumnName otherwise false
     */
    public boolean isSortAscending() {
        return sortAscending;
    }

    /**
     * Sets sortColumnName type.
     *
     * @param sortAscending true for sortAscending sortColumnName, false for
     * descending sortColumnName.
     */
    public void setSortAscending(boolean sortAscending) {
        if (sortAscending != (this.sortAscending)) {
            onePageRowDataModel.setDirtyData();
            this.sortAscending = sortAscending;
        }
    }

    public PagedListDataModel getOnePageRowDataModel() {
        return onePageRowDataModel;
    }

    public abstract int getPageSize();
}
