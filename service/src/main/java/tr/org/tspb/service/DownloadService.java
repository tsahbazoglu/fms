package tr.org.tspb.service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import tr.org.tspb.common.services.LoginController;
import static tr.org.tspb.constants.ProjectConstants.CODE;
import tr.org.tspb.common.qualifier.MyLoginQualifier;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import org.apache.commons.io.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import tr.org.tspb.common.pojo.CellMultiDimensionKey;
import tr.org.tspb.common.services.BaseService;
import tr.org.tspb.common.util.CustomOlapHashMap;
import static tr.org.tspb.constants.ProjectConstants.COMMON;
import static tr.org.tspb.constants.ProjectConstants.EXCEL_FORMAT_PATH;
import static tr.org.tspb.constants.ProjectConstants.MONGO_ID;
import static tr.org.tspb.constants.ProjectConstants.NAME;
import static tr.org.tspb.constants.ProjectConstants.PERIOD;
import static tr.org.tspb.constants.ProjectConstants.SIMPLE_DATE_FORMAT__5;
import static tr.org.tspb.constants.ProjectConstants.SORT;
import static tr.org.tspb.constants.ProjectConstants.TEMPLATE;
import static tr.org.tspb.constants.ProjectConstants.UYS_DOWNLOAD_READER;
import static tr.org.tspb.constants.ProjectConstants.UYS_DOWNLOAD_ROLES;
import static tr.org.tspb.constants.ProjectConstants.UYS_ND_ROW_SIZE;
import static tr.org.tspb.constants.ProjectConstants.UYS_SELECTED_FROM;
import static tr.org.tspb.constants.ProjectConstants.VALUE;
import tr.org.tspb.converter.base.BsonConverter;
import tr.org.tspb.converter.base.MoneyConverter;
import tr.org.tspb.converter.base.SelectOneObjectIdConverter;
import tr.org.tspb.dao.MyField;
import tr.org.tspb.dao.MyForm;
import tr.org.tspb.util.stereotype.MyServices;
import tr.org.tspb.util.tools.DocumentRecursive;

/**
 *
 * @author Telman Şahbazoğlu
 */
@MyServices
public class DownloadService extends CommonSrv {

    @Inject
    @MyLoginQualifier
    private LoginController loginController;

    @Inject
    private RepositoryService repositoryService;

    @Inject
    protected FormService formService;

    @Inject
    protected FilterService filterService;

    @Inject
    protected BaseService baseService;

    private static final String EXTENSION_XLSX = ".xlsx";
    private static final String COLUMN_FORMAT = "\"COLUMN%s\"";
    private static final String DATA_BANK_TEMPLATE = "dataBankTemplate";

    private List<MyField> objectsColumnDataModel;
    private MyForm selectedForm;
    private Map<String, Object> searchedMap;
    private static final String PARENT_VALUE = "parentValue";

    private Document searcheDBObject;
    private StringWriter sw;
    private int columnCount;
    private final Map<ObjectId, String> tempCache = new HashMap<>();
    private final Map<String, String> tempCacheBsonconverter = new HashMap<>();

    private String getTempCacheBsonconverter(String keyValue, MyField myField) {
        String mapKey = myField.getKey() + ":" + keyValue;
        String value = tempCacheBsonconverter.get(mapKey);
        if (value == null) {
            for (SelectItem si : myField.getSelectItemsCurrent()) {
                if (keyValue.equals(si.getValue())) {
                    value = si.getLabel();
                }
            }
            tempCacheBsonconverter.put(mapKey, value);
        }
        return value;
    }

    private DownloadService() {
    }

    public DownloadService(List<MyField> columns, MyForm myForm, Map searchMap) {
        this.objectsColumnDataModel = columns;
        this.selectedForm = myForm;
        this.searchedMap = searchMap;
        //
        init();
    }

    private void init() {
        sw = new StringWriter();
        columnCount = objectsColumnDataModel.size();
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMinimumIntegerDigits(2);

        for (int i = 0; i < columnCount; i++) {
            sw.append(String.format("\"COLUMN%s\"", numberFormat.format(i)));
            sw.append(";");
        }

        sw.append("\n");

        for (MyField column : objectsColumnDataModel) {
            sw.append(column.getName());
            sw.append(";");
        }
        sw.append("\n");

        searcheDBObject = new Document()
                .append(selectedForm.getLoginFkField(), searchedMap.get(selectedForm.getLoginFkField()))
                .append(PERIOD, searchedMap.get(PERIOD));

        if (searcheDBObject.get(selectedForm.getLoginFkField()) == null && loginController.isUserInRole(selectedForm.getMyProject().getAdminAndViewerRole())) {
            searcheDBObject.remove(selectedForm.getLoginFkField());
        }

        if (searcheDBObject.get(PERIOD) == null && loginController.isUserInRole(selectedForm.getMyProject().getAdminAndViewerRole())) {
            searcheDBObject.remove(PERIOD);
        }
    }

    public void downloadExcell() {

        Bson sort = null;

        int limit = 500;
        if (formService.getMyForm().getExcelFormat() != null) {
            sort = (Document) formService.getMyForm().getExcelFormat().get(SORT);
            limit = ((Number) formService.getMyForm().getExcelFormat().get("limit")).intValue();
        }

        List<Document> cursor;

        cursor = mongoDbUtil.find(formService.getMyForm().getDb(), formService.getMyForm().getTable(),
                filterService.getTableFilterCurrent(), sort, limit);

        try (XSSFWorkbook xssfWorkbook = new XSSFWorkbook()) {

            XSSFSheet sheet = xssfWorkbook.createSheet();

            int i = 0;

            List<String> columnList = new ArrayList<>(formService.getMyForm().getFieldsKeySet());
            if (formService.getMyForm().getExcelFormat() != null) {
                columnList = (List<String>) formService.getMyForm().getExcelFormat().get("keys");
            }

            XSSFRow row = sheet.createRow(i);
            int y = 0;
            for (String key : columnList) {
                MyField field = formService.getMyForm().getField(key);
                row.createCell(y++).setCellValue(field.getName());
            }
            i++;
            FileOutputStream fileOut;

            for (Document data : cursor) {
                row = sheet.createRow(i);
                y = 0;
                for (String key : columnList) {

                    MyField field = formService.getMyForm().getField(key);

                    Object keyValue = data.get(key);
                    if (keyValue instanceof ObjectId && !MONGO_ID.equals(key)) {
                        if (field != null) {
                            String myDb = field.getItemsAsMyItems().getDb();
                            String myColl = field.getRefCollection() == null ? field.getItemsAsMyItems().getTable() : field.getRefCollection();
                            String columnName = field.getItemsAsMyItems().getView().get(0);
                            if (tempCache.get((ObjectId) keyValue) == null) {
                                Document record = mongoDbUtil.findOne(
                                        myDb == null ? formService.getMyForm().getDb() : myDb,
                                        myColl,
                                        new Document(MONGO_ID, keyValue));
                                tempCache.put((ObjectId) keyValue, record == null ? "NO DATA ERROR"
                                        : record.get(columnName) == null ? "NO NAME" : record.get(columnName).toString());
                            }

                            keyValue = tempCache.get((ObjectId) keyValue);

                        }
                    } else if (keyValue instanceof Document) {
                        keyValue = ((Document) keyValue).get(NAME);
                    } else if (field.getMyconverter() instanceof BsonConverter && keyValue != null) {
                        keyValue = getTempCacheBsonconverter(keyValue.toString(), field);
                    }

                    XSSFCell cell = row.createCell(y++);
                    cell.setCellValue(keyValue == null ? "no data" : keyValue.toString());
                    if (keyValue instanceof Number) {
                        if (field.getMyconverter() instanceof MoneyConverter) {
                            keyValue = ((Number) keyValue).doubleValue() / 100;
                        }
                        XSSFCellStyle style = xssfWorkbook.createCellStyle();
                        style.setDataFormat((short) 0x27);
                        cell.setCellStyle(style);
                        cell.setCellValue(((Number) keyValue).doubleValue());
                        cell.setCellType(XSSFCell.CELL_TYPE_NUMERIC);
                    }
                }
                i++;
            }

            for (int j = 0; j < columnList.size(); j++) {
                sheet.autoSizeColumn(j);
            }

            FacesContext fc = FacesContext.getCurrentInstance();
            ExternalContext ec = fc.getExternalContext();

            String fileName = SIMPLE_DATE_FORMAT__5.format(new Date())
                    .concat("_")
                    .concat(((HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false)).getId())
                    .concat("_")
                    .concat(String.valueOf((int) (Math.random() * 1000000)));

            try {
                fileOut = new FileOutputStream(baseService.getProperties().getTmpDownloadPath()
                        .concat(fileName).concat(EXTENSION_XLSX), true);
                // write this workbook to an Outputstream.
                xssfWorkbook.write(fileOut);
                fileOut.flush();
                fileOut.close();
            } catch (IOException ex) {
                logger.error("error occured", ex);
            }

            ec.responseReset(); // Some JSF component library or some Filter might have set some headers in the buffer beforehand. We want to get rid of them, else it may collide.
            ec.setResponseContentType("application/vnd.ms-excel"); // Check http://www.iana.org/assignments/media-types for all types. Use if necessary ExternalContext#getMimeType() for auto-detection based on filename.
            // ec.setResponseContentLength(contentLength); // Set it with the file size. This header is optional. It will work if it's omitted, but the download progress will be unknown.
            ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + formService.getMyForm().getName().concat(EXTENSION_XLSX) + "\""); // The Save As popup magic is done here. You can give it any file name you want, this only won't work in MSIE, it will use current request URL as file name instead.
            try (OutputStream responceOutput = ec.getResponseOutputStream();
                    FileInputStream fileInputStream = new FileInputStream(baseService.getProperties().getTmpDownloadPath().concat(fileName).concat(EXTENSION_XLSX));) {
                IOUtils.copy(fileInputStream, responceOutput);
            } catch (IOException ex) {
                logger.error("error occured", ex);
            }

            fc.responseComplete(); // Important! Otherwise JSF will attempt to render the response which obviously will fail since it's already written with a file and closed.
        } catch (IOException ex) {
            logger.error("error occured", ex);
        }
    }

    public String downloadCsvFile() {

        boolean isExcellFormatDefined = selectedForm.getExcelFormat() != null; //support_3791

        if (!isExcellFormatDefined) {
            if (searchedMap.get(PARENT_VALUE) != null) {
                searcheDBObject.append(PARENT_VALUE, searchedMap.get(PARENT_VALUE));
            }
        }

        List<DocumentRecursive> list = repositoryService.findList(selectedForm, searcheDBObject, 20000);

        for (DocumentRecursive tdo : list) {
            for (MyField column : objectsColumnDataModel) {
                String thekey = column.getKey();
                String converterFormat = column.getConverterFormat();
                Object value = tdo.get(thekey);

                if (value == null) {
                    sw.append("");
                } else if (column.getMyconverter() instanceof MoneyConverter || "#.###.###,##".equals(converterFormat)) {
                    NumberFormat toView = createNumberFormat();

                    if (value instanceof Number) {
                        double myValue = ((Number) value).doubleValue() / 100;
                        if (Double.isInfinite(myValue) || Double.isNaN(myValue)) {
                            sw.append(Double.toString(myValue));
                        } else {
                            // sw.append(toView.format(Math.round(myValue)));
                            sw.append(toView.format(myValue));
                        }
                    } else {
                        sw.append(value.toString());
                    }
                } else if ("'%' ##.##".equals(converterFormat)) {
                    NumberFormat toView = createNumberFormat();

                    if (value instanceof Number) {
                        double myValue = ((Number) value).doubleValue();
                        if (Double.isInfinite(myValue) || Double.isNaN(myValue)) {
                            sw.append(Double.toString(myValue));
                        } else {
                            // sw.append(toView.format(Math.round(myValue)));
                            sw.append(toView.format(myValue));
                        }
                    } else {
                        sw.append(value.toString());
                    }
                } else if (column.getMyconverter() instanceof SelectOneObjectIdConverter && value instanceof Document) {
                    if (PARENT_VALUE.equals(thekey)) {
                        sw.append(((Document) value).getString(CODE));
                        if (isExcellFormatDefined) {
                            sw.append(";");
                            sw.append(((Document) value).getString(NAME));
                            sw.append(";");
                            sw.append(((Document) value).getString("abstractOrder"));
                            sw.append(";");
                            sw.append(((Document) value).getString("detailOrder"));
                        }
                    } else {
                        sw.append(((Document) value).getString(NAME));
                    }
                } else if (column.getMyconverter() instanceof BsonConverter && value instanceof Document) {
                    sw.append(((Document) value).getString(NAME));
                } else {
                    sw.append(value.toString());
                }
                sw.append(";");
            }
            sw.append("\n");
        }

        Reader reader = new StringReader(sw.toString());

        armHttpSession(reader);

        try {
            String servletUrl = "downloadDynamicContentServlet/";
            FacesContext.getCurrentInstance().getExternalContext().redirect(servletUrl);
            /*
            This will automatically invoke responseComplete() and it also
            keeps your code clean from unnecessary Servlet API stuff.
            Balusc
             */
        } catch (IOException ex) {
            logger.error("error occured", ex);
        }

        return null;
    }

    private void armHttpSession(Reader reader) {
        HttpSession httpSession = (HttpSession) FacesContext
                .getCurrentInstance()
                .getExternalContext()
                .getSession(false);

        httpSession.setAttribute(UYS_DOWNLOAD_READER, reader);
        httpSession.setAttribute(UYS_SELECTED_FROM, selectedForm);
        httpSession.setAttribute("uysJasperColumnSize", columnCount);
    }

    private NumberFormat createNumberFormat() {
        Locale localeTR = new Locale("tr", "TR");
        NumberFormat toView = NumberFormat.getNumberInstance(localeTR);
        toView.setCurrency(Currency.getInstance(localeTR));
        toView.setGroupingUsed(true);
        toView.setMaximumFractionDigits(2);
        toView.setMinimumFractionDigits(2);
        return toView;
    }

    public String downloadCsvFilePivot(List<MyField> iksDimension, List<MyField> igrekDimension,
            Map<CellMultiDimensionKey, List<CustomOlapHashMap>> mapMultiDimension,
            Map<CellMultiDimensionKey, List<CustomOlapHashMap>> mapMultiDimensionHistory
    ) {
        StringWriter sw = new StringWriter();

        Document period = mongoDbUtil.findOne(formService.getMyForm().getDb(),
                COMMON, new Document(MONGO_ID, filterService.getPivotFilterCurrent().get(PERIOD)));

        Document template = mongoDbUtil.findOne(formService.getMyForm().getDb(),
                DATA_BANK_TEMPLATE, new Document(MONGO_ID, filterService.getPivotFilterCurrent().get(TEMPLATE)));

        StringBuilder header = new StringBuilder();

        int columnCount = (period == null ? 1 : 2) + igrekDimension.size();//period and kalem
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMinimumIntegerDigits(2);

        for (int i = 0; i < columnCount; i++) {
            header.append(String.format(COLUMN_FORMAT, numberFormat.format(i)));
            header.append(";");
        }
        header.append("\n");
        sw.append(header.toString());

        header = new StringBuilder();
        header.append("Dönem;");
        header.append("Şablon;");
        header.append("Kalem;");

        for (MyField coordinateX : igrekDimension) {
            header.append(coordinateX.getName().concat(";"));
        }
        header.append("\n");
        sw.append(header.toString());

        for (MyField coordinateX : iksDimension) {
            String row = "";
            row += (period == null ? "-" : period.get(NAME).toString()).concat(";");
            row += (template == null ? "-" : template.get(NAME).toString()).concat(";");

            row += coordinateX.getName().concat(";");

            for (MyField coordinateY : igrekDimension) {
                List<MyField> coordinates = new ArrayList<>();
                coordinates.add(coordinateY);
                coordinates.add(coordinateX);
                CellMultiDimensionKey key = new CellMultiDimensionKey(coordinates);
                CustomOlapHashMap customOlapHaspMap = mapMultiDimension.get(key).get(0);
                Object value = customOlapHaspMap.getValue();

                if (value == null) {
                    row += " ";
                } else if (value != null) {
                    Object converter = customOlapHaspMap.getConverter();
                    if (converter != null //
                            && !"NULL_VALUE".equals(converter)//
                            && converter instanceof MoneyConverter) {
                        Locale locale = new Locale("tr", "TR");
                        NumberFormat toView = NumberFormat.getInstance(locale);//

                        if (value instanceof Number) {
                            double myValue = ((Number) value).doubleValue() / 100;

                            if (Double.isInfinite(myValue) || Double.isNaN(myValue)) {
                                row += Double.toString(myValue);
                            } else {
                                row += toView.format(Math.round(myValue));
                            }
                        }
                    } else {
                        row += value.toString();
                    }
                }
                row += ";";
            }
            row += "\n";
            sw.append(row);
        }

        sw.append("\n");

        if (Boolean.TRUE.equals(formService.getMyForm().getHistoryRendered())) {

            period = mongoDbUtil.findOne(formService.getMyForm().getDb(), COMMON,
                    new Document(MONGO_ID, filterService.getPivotFilterCurrent().get(PERIOD)));

            template = mongoDbUtil.findOne(formService.getMyForm().getDb(), DATA_BANK_TEMPLATE,
                    new Document(MONGO_ID, filterService.getPivotFilterCurrent().get(TEMPLATE)));

            if (formService.getMyForm().getField(PERIOD) == null) {
                period = null;
            }

            for (MyField coordinateX : iksDimension) {
                String row = "";
                row += (period == null ? "-" : period.get(NAME).toString()).concat(";");
                row += (template == null ? "-" : template.get(NAME).toString()).concat(";");

                row += coordinateX.getName().concat(";");

                for (MyField coordinateY : igrekDimension) {
                    List<MyField> coordinates = new ArrayList<>();
                    coordinates.add(coordinateY);
                    coordinates.add(coordinateX);
                    CellMultiDimensionKey key = new CellMultiDimensionKey(coordinates);
                    CustomOlapHashMap customOlapHaspMap = mapMultiDimensionHistory.get(key).get(0);
                    Object value = customOlapHaspMap.getValue();
                    if (value != null) {
                        Object converter = customOlapHaspMap.getConverter();
                        if (converter != null //
                                && !"NULL_VALUE".equals(converter)//
                                && converter instanceof MoneyConverter) {
                            Locale locale = new Locale("tr", "TR");
                            NumberFormat toView = NumberFormat.getInstance(locale);//

                            if (value instanceof Number) {
                                double myValue = ((Number) value).doubleValue() / 100;

                                if (Double.isInfinite(myValue) || Double.isNaN(myValue)) {
                                    row += Double.toString(myValue);
                                } else {
                                    row += toView.format(Math.round(myValue));
                                }
                            }
                        } else {
                            row += value.toString();
                        }
                    }
                    row += ";";
                }
                row += "\n";
                sw.append(row);
            }

            sw.append("\n");
        }

        Reader reader = new StringReader(sw.toString());

        String excelFormat = getExcelFormatPath(formService.getMyForm().getKey(), template == null ? "" : template.get(VALUE).toString());

        HttpSession httpSession = (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(false);

        httpSession.setAttribute(EXCEL_FORMAT_PATH, excelFormat);
        httpSession.setAttribute(UYS_DOWNLOAD_READER, reader);
        httpSession.setAttribute(UYS_SELECTED_FROM, formService.getMyForm());
        httpSession.setAttribute(UYS_ND_ROW_SIZE, iksDimension.size());
        httpSession.setAttribute(UYS_DOWNLOAD_ROLES, loginController.getRoleMap().keySet());

        httpSession.setAttribute("uysJasperColumnSize", columnCount);

        try {
            String servletUrl = "downloadDynamicContentServlet/";
            FacesContext.getCurrentInstance().getExternalContext().redirect(servletUrl);
            /*
             * This will automatically invoke responseComplete() and it also
             * keeps your code clean from unnecessary Servlet API stuff.
             * Balusc
             */
        } catch (IOException ex) {
            logger.error("error occured", ex);
        }

        return null;
    }

    private Map<String, String> excelformat;

    public String getExcelFormatPath(String selectedFormKey, String templateValue) {
        if (excelformat == null) {
            excelformat = new HashMap();
            excelformat.put("UFRS-201306__balanceAbstract", "/home/telman/ufrs_201306_balance_abstract_sablon.xls");
            excelformat.put("UFRS-201306__balanceDetail", "/home/telman/ufrs_201306_balance_detail_sablon.xls");
            excelformat.put("UFRS-201306__incomeAbstract", "/home/telman/ufrs_201306_income_abstract_sablon.xls");
            excelformat.put("UFRS-201306__incomeDetail", "/home/telman/ufrs_201306_income_detail_sablon.xls");
        }
        return excelformat.get(templateValue.concat("__").concat(selectedFormKey));
    }

}
