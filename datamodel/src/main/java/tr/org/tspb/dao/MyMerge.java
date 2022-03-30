package tr.org.tspb.dao;

import tr.org.tspb.pojo.ExcellColumnDef;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.Document;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class MyMerge {

    private final String exampleFile;
    private final String toDb;
    private final String toCollection;
    private final int workbookSheetColumnCount;
    private final int workbookSheetStartRow;
    private final List<ExcellColumnDef> workbookSheetColumnList;
    private final List<MyField> appendFields;
    private final List<MyField> upsertFields;
    private final List<MyField> importAndAppendedFields;
    private final String strategy;

    MyMerge(Map<String, Object> toMap, FmsForm myForm) throws Exception {
        this.exampleFile = (String) toMap.get("example-file");
        this.toDb = (String) toMap.get("toDB");
        this.toCollection = (String) toMap.get("toCollection");
        this.workbookSheetColumnCount = ((Number) toMap.get("workbookSheetColumnCount")).intValue();
        this.workbookSheetStartRow = ((Number) toMap.get("workbookSheetStartRow")).intValue();
        this.workbookSheetColumnList = new ArrayList<>();
        this.appendFields = new ArrayList<>();
        this.upsertFields = new ArrayList<>();
        this.importAndAppendedFields = new ArrayList<>();
        this.strategy = toMap.get("strategy") == null ? "insert" : toMap.get("strategy").toString();

        List<Document> listOfDoc = (List<Document>) toMap.get("workbookSheetColumns");
        if (listOfDoc != null) {
            for (Document key : listOfDoc) {
                ExcellColumnDef ecd = new ExcellColumnDef(myForm, key);
                workbookSheetColumnList.add(ecd);
                importAndAppendedFields.add(myForm.getField(ecd.getToMyField().getKey()));
            }
        }

        List<String> list = (List<String>) toMap.get("appendFields");
        if (list != null) {

            for (String key : list) {

                MyField myField = myForm.getField(key);
                if (myField == null) {
                    throw new Exception(String.format("form definition does not a %s key predefined on appendFields. %s",
                            key, myForm.printToConfigAnalyze("smth")));
                }

                appendFields.add(myField);
                importAndAppendedFields.add(myField);

                myField.createSelectItems(null, null, myForm.getRoleMap(), myForm.getUserDetail(), false);
            }
        }

        List<String> listOfUpsertFields = (List<String>) toMap.get("upsertFields");
        if (listOfUpsertFields != null) {
            for (String key : listOfUpsertFields) {
                MyField myField = myForm.getField(key);
                upsertFields.add(myField);
                myField.createSelectItems(null, null, myForm.getRoleMap(), myForm.getUserDetail(), false);
            }
        }

    }

    public boolean isInsert() {
        return "insert".equals(this.strategy);
    }

    public boolean isUpdate() {
        return "update".equals(this.strategy);
    }

    public List<MyField> getAppendFields() {
        return Collections.unmodifiableList(appendFields);
    }

    public List<MyField> getImportAndAppendedFields() {
        return Collections.unmodifiableList(importAndAppendedFields);
    }

    public String getStrategy() {
        return strategy;
    }

    public String getToDb() {
        return toDb;
    }

    public String getToCollection() {
        return toCollection;
    }

    public List<MyField> getUpsertFields() {
        return Collections.unmodifiableList(upsertFields);
    }

    public int getWorkbookSheetColumnCount() {
        return workbookSheetColumnCount;
    }

    public List<ExcellColumnDef> getWorkbookSheetColumnList() {
        return Collections.unmodifiableList(workbookSheetColumnList);
    }

    public int getWorkbookSheetStartRow() {
        return workbookSheetStartRow;
    }

    public String getExampleFile() {
        return exampleFile;
    }

}
