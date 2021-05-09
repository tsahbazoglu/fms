package tr.org.tspb.service;

import tr.org.tspb.util.stereotype.MyServices;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import org.bson.types.ObjectId;
import tr.org.tspb.common.pojo.CellMultiDimensionKey;
import tr.org.tspb.common.util.CustomOlapHashMap;
import static tr.org.tspb.constants.ProjectConstants.CELL_VERSION;
import static tr.org.tspb.constants.ProjectConstants.MEASURE;
import static tr.org.tspb.constants.ProjectConstants.MONGO_ID;
import tr.org.tspb.converter.base.MoneyConverter;
import tr.org.tspb.converter.base.NumberConverter;
import tr.org.tspb.converter.base.UysStringConverter;
import tr.org.tspb.dao.MyField;
import tr.org.tspb.dao.FmsForm;
import tr.org.tspb.dao.MyMap;
import tr.org.tspb.exceptions.FormConfigException;
import tr.org.tspb.exceptions.MongoOrmFailedException;
import tr.org.tspb.exceptions.MoreThenOneInListException;
import tr.org.tspb.exceptions.NullNotExpectedException;
import tr.org.tspb.exceptions.UserException;

/**
 *
 * @author Telman Şahbazoğlu
 */
@MyServices
public class DataService implements Serializable {

    @Inject
    protected MapReduceService mapReduceService;

    private Map<Map, Map> mapReduceCache = new HashMap<>();

    private MyMap myMap;

    public String create() {
        myMap = new MyMap();
        return null;
    }

    public String save() {
        myMap = new MyMap();
        return null;
    }

    public MyMap getMyMap() {
        return myMap;
    }

    public void setMyMap(MyMap myMap) {
        this.myMap = myMap;
    }

    public void resetMapReduceCache() {
        mapReduceCache = new HashMap<>();
    }

    /**
     *
     * @param myForm
     * @param filter
     * @param iksDimension
     * @param igrekDimension
     * @param mapOfMeasureField
     * @return
     * @throws NullNotExpectedException
     * @throws MongoOrmFailedException
     * @throws MoreThenOneInListException
     * @throws UserException
     * @throws FormConfigException
     */
    public Map<CellMultiDimensionKey, List<CustomOlapHashMap>> createPivotData(
            FmsForm myForm,
            Map filter,
            List<MyField> iksDimension,
            List<MyField> igrekDimension,
            Map<String, MyField> mapOfMeasureField)
            throws NullNotExpectedException, MongoOrmFailedException, MoreThenOneInListException, UserException, FormConfigException {

        Map<CellMultiDimensionKey, List<CustomOlapHashMap>> pivotData
                = createEmptyPivot(myForm.getForm(), iksDimension, igrekDimension, mapOfMeasureField);

        applyPersistentData(pivotData, myForm, filter);

        return pivotData;
    }

    public Map<CellMultiDimensionKey, List<CustomOlapHashMap>> createPivotDataSnapshot(
            FmsForm myForm,
            Map filter,
            List<MyField> iksDimension,
            List<MyField> igrekDimension,
            Map<String, MyField> mapOfMeasureField
    )
            throws NullNotExpectedException, MongoOrmFailedException, MoreThenOneInListException, UserException, FormConfigException {

        Map<CellMultiDimensionKey, List<CustomOlapHashMap>> pivotData = createEmptyPivot(myForm.getForm(),
                iksDimension, igrekDimension, mapOfMeasureField);

        applySnapshotPersistenceData(pivotData, myForm, filter);

        return pivotData;
    }

    public void cleanMapReduce(Map filter) {
        mapReduceCache.remove(filter);
    }

    public Map cacheAndGetPivotData(Map filter, FmsForm myForm)
            throws NullNotExpectedException, MongoOrmFailedException, MoreThenOneInListException, FormConfigException {
        Map map = mapReduceCache.get(filter);
        if (map == null) {
            mapReduceService.init(filter, myForm);
            map = mapReduceService.mapReduce(filter, myForm);
            mapReduceCache.put(filter, map);
        }
        return map;

    }

    private void applyPersistentData(Map<CellMultiDimensionKey, List<CustomOlapHashMap>> pivotData, 
            FmsForm myForm, Map filter)
            throws NullNotExpectedException, MongoOrmFailedException, MoreThenOneInListException, FormConfigException {
        mapReduceService.init(filter, myForm);
        armCellsWithData(pivotData, cacheAndGetPivotData(filter, myForm));
    }

    private void applySnapshotPersistenceData(Map<CellMultiDimensionKey, List<CustomOlapHashMap>> mapMultiDimension,
            FmsForm myForm, Map filter)
            throws NullNotExpectedException, MongoOrmFailedException, MoreThenOneInListException, FormConfigException {

        mapReduceService.init(filter, myForm);

        mapReduceService.cacheSnapshotKey(filter, myForm);

        armCellsWithData(mapMultiDimension, mapReduceService.getSnapshotMapReduceValueCache(filter));
    }

    private void armCellsWithData(Map<CellMultiDimensionKey, List<CustomOlapHashMap>> pivotData, Map matris)
            throws MoreThenOneInListException {
        StringBuilder errorMessage = new StringBuilder();
        for (Map.Entry<CellMultiDimensionKey, List<CustomOlapHashMap>> entryOfMapCell : pivotData.entrySet()) {
            if (entryOfMapCell.getValue().isEmpty()) {
                errorMessage.append(entryOfMapCell.getKey());
                errorMessage.append("<br/>");
            }

            CustomOlapHashMap customOlapHaspMap = entryOfMapCell.getValue().get(0);
            Map recordMap = (Map) matris.get(entryOfMapCell.getKey());

            /**
             * mapMultiDimension can be cached and because of that fact we have
             * to refresh its old values in case of recordMap == null
             */
            if (recordMap == null) {
                customOlapHaspMap.resetValue();
                customOlapHaspMap.resetID();
                customOlapHaspMap.resetCellVersion();;
            } else {
                customOlapHaspMap.setValue(recordMap.get(customOlapHaspMap.getField()));
                customOlapHaspMap.setId((ObjectId) recordMap.get(MONGO_ID));
                customOlapHaspMap.setCellVersion((String) recordMap.get(CELL_VERSION));
            }
        }
        if (errorMessage.length() > 0) {
            //FIXME messagebundle
            throw new MoreThenOneInListException("Aşağıdaki koordinatlarda hücre tespit edilemedi<br/>" + errorMessage.toString());
        }
    }

    public HashMap createEmptyPivot(String selectedFormCode,
            List<MyField> iksDimension, List<MyField> igrekDimension, Map<String, MyField> mapOfMeasureField) throws FormConfigException {
        HashMap map = new HashMap();

        for (MyField xCoordinate : iksDimension) {
            for (MyField yCoordinate : igrekDimension) {
                List<MyField> listOfCoordinates = new ArrayList<>();
                listOfCoordinates.add(xCoordinate);
                listOfCoordinates.add(yCoordinate);
                CellMultiDimensionKey cellMultiDimensionKey = new CellMultiDimensionKey(listOfCoordinates);
                map.put(cellMultiDimensionKey, new ArrayList());

                for (MyField measure : mapOfMeasureField.values()) {
                    CustomOlapHashMap customOlapHashMap = new CustomOlapHashMap(measure.getField(), true);

                    Converter converter = measure.getMyconverter();
                    String uysformat = measure.getUysformat();

                    // If some dimension around this mesure have a defined converter (on fly db record)
                    // then use it instead of measure predefined one.
                    //
                    // BUT :  this coordinate must not been a measure at the same time
                    //
                    // for example have a look  to Promosion Data under kpdb configured project
                    if (xCoordinate.getMyconverter() != null && !MEASURE.equals(xCoordinate.getNdType())) {
                        converter = xCoordinate.getMyconverter();
                        uysformat = xCoordinate.getUysformat();
                    }
                    /*
                     * horizontal dimension has a hign priority
                     */
                    if (yCoordinate.getMyconverter() != null && !MEASURE.equals(yCoordinate.getNdType())) {
                        converter = yCoordinate.getMyconverter();
                        uysformat = yCoordinate.getUysformat();
                    }

                    String predefinedStyle = measure.getStyle();

                    customOlapHashMap.setComponentType(measure.getComponentType());
                    customOlapHashMap.setMinimumFractionDigits(measure.getMinFractationDigits());
                    customOlapHashMap.setMaximumFractionDigits(measure.getMaxFractationDigits());
                    customOlapHashMap.setDivider(measure.getDivider() == null ? null : ((Number) measure.getDivider()).intValue());

                    customOlapHashMap.setNdType(measure.getNdType());

                    if (measure.getNdAxis() != null) {
                        customOlapHashMap.setNdAxis(measure.getNdAxis());
                        customOlapHashMap.setCode(measure.getKey());
                    }

                    if (converter instanceof MoneyConverter) {
                        customOlapHashMap.setLanguage("tr");
                        customOlapHashMap.setCountry("TR");
                        customOlapHashMap.setNegotivCheck(Boolean.FALSE);
                        customOlapHashMap.setConverter(new MoneyConverter());
                    } else if (converter instanceof NumberConverter
                            || Arrays.asList("#.###.###", "#.###.###,#", "#.###.###,##", "#.###.###,###", "#.###.###,####", "#.###.###,#####").contains(uysformat)) {
                        customOlapHashMap.setConverter(new NumberConverter());
                        customOlapHashMap.setUysformat(uysformat);
                    } else if (converter instanceof UysStringConverter || converter instanceof NumberConverter) {
                        customOlapHashMap.setConverter(new UysStringConverter());
                    } else if (converter != null) {
                        throw new FormConfigException(String.format("The converter defined for %s  is not supported yet.", measure.getKey()));
                    }

                    if (predefinedStyle != null) {
                        customOlapHashMap.setStyle(predefinedStyle);
                    }

                    if (MEASURE.equals(xCoordinate.getNdType()) && !measure.getKey().equals(xCoordinate.getKey())) {
                        continue;
                    }
                    if (MEASURE.equals(yCoordinate.getNdType()) && !measure.getKey().equals(yCoordinate.getKey())) {
                        continue;
                    }
                    ((Collection) map.get(cellMultiDimensionKey)).add(customOlapHashMap);
                }
            }
            //FIXME provide support for Z_ZET also
        }

        return map;
    }

}
