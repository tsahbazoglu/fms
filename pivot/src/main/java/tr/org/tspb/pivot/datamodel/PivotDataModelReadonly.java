package tr.org.tspb.pivot.datamodel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import org.bson.types.ObjectId;
import tr.org.tspb.common.pojo.CellMultiDimensionKey;
import tr.org.tspb.common.util.CustomOlapHashMap;
import static tr.org.tspb.constants.ProjectConstants.COLOR;
import static tr.org.tspb.constants.ProjectConstants.DESCRIPTION;
import static tr.org.tspb.constants.ProjectConstants.MONGO_ID;
import tr.org.tspb.converter.base.MoneyConverter;
import tr.org.tspb.dao.MyField;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class PivotDataModelReadonly implements PivotDataModel {

    private final DataModel rows;
    private final DataModel columns;
    private final Map<CellMultiDimensionKey, List<CustomOlapHashMap>> pivotData;
    private final Map<CellMultiDimensionKey, List<CustomOlapHashMap>> pivotDataSnapshot;
    private final Map cellControl;
    private final Object snapshotNullHandler;
    private final int handsonRowHeaderWidth;
    private final int handsonColWidths;

    /**
     *
     * @param colWidth
     * @param rowWidth
     * @param rows
     * @param columns
     * @param pivotData
     * @param pivotDataSnapshot
     * @param cellControl
     * @param snapshotNullHandler
     */
    public PivotDataModelReadonly(
            int colWidth,
            int rowWidth,
            List<MyField> rows,
            List<MyField> columns,
            Map<CellMultiDimensionKey, List<CustomOlapHashMap>> pivotData,
            Map<CellMultiDimensionKey, List<CustomOlapHashMap>> pivotDataSnapshot,
            Map cellControl,
            Object snapshotNullHandler) {
        this.rows = new ListDataModel(rows);
        this.columns = new ListDataModel(columns);
        this.pivotData = pivotData;
        this.pivotDataSnapshot = pivotDataSnapshot;
        this.cellControl = cellControl;
        this.snapshotNullHandler = snapshotNullHandler;
        this.handsonRowHeaderWidth = rowWidth;
        this.handsonColWidths = colWidth;
    }

    public Object getCell() {

        if (pivotData != null
                && rows != null
                && columns != null
                && rows.isRowAvailable()
                && columns.isRowAvailable()) {

            MyField coordinateColumn = (MyField) columns.getRowData();
            MyField coordinateRow = (MyField) rows.getRowData();
            List<MyField> coordinates = new ArrayList<>();
            coordinates.add(coordinateColumn);
            coordinates.add(coordinateRow);
            CellMultiDimensionKey cellMultiDimensionKey = new CellMultiDimensionKey(coordinates);

            List<CustomOlapHashMap> customOlapHaspMaps = pivotData.get(cellMultiDimensionKey);

            if (customOlapHaspMaps != null && !customOlapHaspMaps.isEmpty()) {
                CustomOlapHashMap customOlapHaspMap = customOlapHaspMaps.get(0);
                Map control = (Map) cellControl.get(cellMultiDimensionKey);
                if (control != null) {
                    String style = (String) customOlapHaspMap.getStyle();
                    style = String.format("%s background-color:%s;", style == null ? "" : style, control.get(COLOR));
                    customOlapHaspMap.setStyle(style);
                    customOlapHaspMap.setControlID((ObjectId) control.get(MONGO_ID));
                    customOlapHaspMap.setControlDesc(String.format("<hr/><b>kontrol</b> :  %s", control.get(DESCRIPTION)));
                }

                //provide snapshot diff values view for end user
                if (pivotDataSnapshot != null) {
                    List<CustomOlapHashMap> snapshot = pivotDataSnapshot.get(cellMultiDimensionKey);
                    if (snapshot != null && !snapshot.isEmpty()) {
                        CustomOlapHashMap snapshotMap = snapshot.get(0);
                        Object currentValue = customOlapHaspMap.getValue();
                        Object snapshotValue = snapshotMap.getValue();

                        if (snapshotValue == null && snapshotNullHandler != null) {
                            snapshotValue = Double.NaN;
                        }

                        if (currentValue instanceof Long) {
                            currentValue = ((Number) currentValue).doubleValue();
                        }

                        if (snapshotValue instanceof Long) {
                            snapshotValue = ((Number) snapshotValue).doubleValue();
                        }

                        if (currentValue != null && snapshotValue != null && !currentValue.equals(snapshotValue)) {

                            if (control == null) {
                                String style = (String) customOlapHaspMap.getStyle();
                                style = String.format("%s background:%s;", style, "rgb(126, 164, 221)");
                                customOlapHaspMap.setStyle(style);
                            }

                            customOlapHaspMap.setControlID(new ObjectId("dummy"));

                            StringBuilder controlDesc = new StringBuilder();
                            if (control != null) {
                                controlDesc.append(String.format("<hr/><b>kontrol</b> :  %s", control.get(DESCRIPTION)));
                            }

                            Locale locale = new Locale("tr", "TR");
                            NumberFormat toView = NumberFormat.getInstance(locale);
                            double myValue;
                            if (snapshotValue instanceof String) {
                                //FIXME
                                // we dont epexct string or empty string here 
                                if (snapshotValue.toString().trim().isEmpty()) {
                                    snapshotValue = "0";
                                }

                                myValue = Double.valueOf((String) snapshotValue).doubleValue();
                                if (customOlapHaspMap.getConverter() instanceof MoneyConverter) {
                                    myValue /= 100;
                                }
                            } else {
                                myValue = ((Number) snapshotValue).doubleValue();
                                if (customOlapHaspMap.getConverter() instanceof MoneyConverter) {
                                    myValue /= 100;
                                }
                            }
                            if (Double.isInfinite(myValue) || Double.isNaN(myValue)) {
                                //FIXME messagebundle
                                controlDesc.append(String.format("<hr/><b>önceki değer</b> :  %s", "BOŞ"));
                            } else {
                                controlDesc.append(String.format("<hr/><b>önceki değer</b> :  %s", toView.format(Math.round(myValue))));
                            }

                            customOlapHaspMap.setControlDesc(controlDesc.toString());

                        }
                    }
                }
                return customOlapHaspMap;
            }
        }
        return null;
    }

    public DataModel getRows() {
        return rows;
    }

    public DataModel getColumns() {
        return columns;
    }

    @Override
    public int getHandsonRowHeaderWidth() {
        return handsonRowHeaderWidth;
    }

    @Override
    public int getHandsonColWidths() {
        return handsonColWidths;
    }

    @Override
    public void setHandsonRowHeaderWidth(int handsonRowHeaderWidth) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setHandsonColWidths(int handsonColWidths) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getJsonColHeaders() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setJsonColHeaders(String jsonColHeaders) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getJsonRowHeaders() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setJsonRowHeaders(String jsonRowHeaders) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getJsonColRenderers() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setJsonColRenderers(String jsonColRenderers) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getJsonData() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setJsonData(String jsonData) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getJsonDataToModel() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setJsonDataToModel(String jsonDataToModel) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
