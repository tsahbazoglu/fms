package tr.org.tspb.pivot.datamodel;

import com.google.gson.Gson;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import tr.org.tspb.common.pojo.CellMultiDimensionKey;
import tr.org.tspb.common.util.CustomOlapHashMap;
import static tr.org.tspb.constants.ProjectConstants.COLOR;
import tr.org.tspb.converter.base.MoneyConverter;
import tr.org.tspb.converter.base.NumberConverter;
import tr.org.tspb.dao.MyField;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class PivotDataModelHandson implements PivotDataModel {

    private int handsonRowHeaderWidth;
    private int handsonColWidths;
    private String jsonRowHeaders;
    private String jsonColHeaders;
    private String jsonDataToView;
    private String jsonDataToModel;
    private String jsonCellRenderers;

    public PivotDataModelHandson(
            int colWidth,
            int rowWidth,
            List<MyField> iksDimension, List<MyField> igrekDimension,
            Map<CellMultiDimensionKey, List<CustomOlapHashMap>> pivotData,
            Map cellControl) {

        this.handsonRowHeaderWidth = rowWidth;
        this.handsonColWidths = colWidth;

        Gson gson = new Gson();

        List colHeaders = new ArrayList();
        List rowHeaders = new ArrayList();
        List data = new ArrayList();
        List dataRenderers = new ArrayList();

        for (MyField x : iksDimension) {
            rowHeaders.add(x.getName());
        }

        for (MyField x : igrekDimension) {
            colHeaders.add(x.getName());
        }

        for (MyField coordinateRow : iksDimension) {
            List row = new ArrayList();
            List renderer = new ArrayList();
            for (MyField coordinateColumn : igrekDimension) {
                List<MyField> coordinates = new ArrayList<>();
                coordinates.add(coordinateColumn);
                coordinates.add(coordinateRow);
                CellMultiDimensionKey cellMultiDimensionKey = new CellMultiDimensionKey(coordinates);

                List<CustomOlapHashMap> customOlapHaspMaps = pivotData.get(cellMultiDimensionKey);

                if (customOlapHaspMaps != null && !customOlapHaspMaps.isEmpty()) {
                    CustomOlapHashMap customOlapHaspMap = customOlapHaspMaps.get(0);
                    Object value = customOlapHaspMap.getValue();
                    if (value == null || value instanceof String && ((String) value).isEmpty()) {
                        row.add(null);
                    } else {
                        Object converter = customOlapHaspMap.getConverter();
                        if (converter != null && !"NULL_VALUE".equals(converter)
                                && converter instanceof MoneyConverter) {
                            Locale locale = new Locale("tr", "TR");
                            NumberFormat toView = NumberFormat.getInstance(locale);

                            if (value instanceof Number) {
                                double myValue = ((Number) value).doubleValue() / 100;

                                if (Double.isInfinite(myValue) || Double.isNaN(myValue)) {
                                    row.add(Double.toString(myValue));
                                } else {
                                    row.add(toView.format(Math.round(myValue)));
                                }
                            }
                        } else if (converter instanceof NumberConverter) {
                            Locale locale = new Locale("tr", "TR");
                            NumberFormat toView = NumberFormat.getInstance(locale);

                            int div = ((NumberConverter) converter).getDiv();

                            if (customOlapHaspMap.getDivider() != null) {
                                div = customOlapHaspMap.getDivider();
                            }

                            if (customOlapHaspMap.getUysformat() != null) {
                                div = ((NumberConverter) converter).getDiv(customOlapHaspMap.getUysformat());
                            }

                            if (value instanceof Number) {
                                double myValue = ((Number) value).doubleValue() / div;

                                if (Double.isInfinite(myValue) || Double.isNaN(myValue)) {
                                    row.add(Double.toString(myValue));
                                } else if (customOlapHaspMap.getRound() == null) {
                                    row.add(toView.format(myValue));
                                } else {
                                    row.add(toView.format(Math.round(myValue)));
                                }
                            }
                        } else {
                            row.add(value);
                        }
                    }

                    String background = Boolean.TRUE.equals(customOlapHaspMap.isReadonly()) ? "#d8e4bc"/*"paleGreen"*/ : "white";
                    Map control = (Map) cellControl.get(cellMultiDimensionKey);
                    if (control != null) {
                        background = (String) control.get(COLOR);
                    }

                    renderer.add(new HandsonTableColRenderer(
                            customOlapHaspMap.getComponentType() == null ? "inputText" : (String) customOlapHaspMap.getComponentType(),
                            background,
                            Boolean.TRUE.equals(customOlapHaspMap.isReadonly())));
//#17D1E6
                }
            }
            dataRenderers.add(renderer);
            data.add(row);
        }

        jsonColHeaders = gson.toJson(colHeaders);
        jsonRowHeaders = gson.toJson(rowHeaders);
        jsonDataToView = gson.toJson(data);
        jsonCellRenderers = gson.toJson(dataRenderers);

    }

    public int getHandsonRowHeaderWidth() {
        return handsonRowHeaderWidth;
    }

    public void setHandsonRowHeaderWidth(int handsonRowHeaderWidth) {
        this.handsonRowHeaderWidth = handsonRowHeaderWidth;
    }

    public int getHandsonColWidths() {
        return handsonColWidths;
    }

    public void setHandsonColWidths(int handsonColWidths) {
        this.handsonColWidths = handsonColWidths;
    }

    public String getJsonColHeaders() {
        return jsonColHeaders;
    }

    public void setJsonColHeaders(String jsonColHeaders) {
        this.jsonColHeaders = jsonColHeaders;
    }

    public String getJsonRowHeaders() {
        return jsonRowHeaders;
    }

    public void setJsonRowHeaders(String jsonRowHeaders) {
        this.jsonRowHeaders = jsonRowHeaders;
    }

    public String getJsonColRenderers() {
        return jsonCellRenderers;
    }

    public void setJsonColRenderers(String jsonColRenderers) {
        this.jsonCellRenderers = jsonColRenderers;
    }

    public String getJsonData() {
        return jsonDataToView;
    }

    public void setJsonData(String jsonData) {
        this.jsonDataToView = jsonData;
    }

    public String getJsonDataToModel() {
        return jsonDataToModel;
    }

    public void setJsonDataToModel(String jsonDataToModel) {
        this.jsonDataToModel = jsonDataToModel;
    }

    private class HandsonTableColRenderer {

        private String component;
        private String background;
        private Boolean readonly;

        HandsonTableColRenderer(String component, String background, Boolean readonly) {
            this.component = component;
            this.background = background;
            this.readonly = readonly;
        }

        public String getComponent() {
            return component;
        }

        public void setComponent(String component) {
            this.component = component;
        }

        public String getBackground() {
            return background;
        }

        public void setBackground(String background) {
            this.background = background;
        }

        public Boolean getReadonly() {
            return readonly;
        }

        public void setReadonly(Boolean readonly) {
            this.readonly = readonly;
        }
    }

}
