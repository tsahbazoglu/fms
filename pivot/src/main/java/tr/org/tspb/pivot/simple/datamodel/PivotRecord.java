package tr.org.tspb.pivot.simple.datamodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class PivotRecord {

    private ObjectId objectId;
    private ObjectId member;
    private boolean ok = true;

    private Map<String, List<RowRecord>> rowRecordMap;

    public Document toDocument() {
        Document doc = new Document();
        for (String key : rowRecordMap.keySet()) {
            List<RowRecord> listOfRowRecord = rowRecordMap.get(key);
            List<Document> listOfQuestion = new ArrayList<>();
            ok = true;
            for (RowRecord rowRecord : listOfRowRecord) {
                if (rowRecord.getCheck()) {
                    if ("null".equals(rowRecord.getKontrolTipi())) {
                        ok = false;
                    } else if ("null".equals(rowRecord.getUygulamaYonetimi())) {
                        ok = false;
                    }
                }
                listOfQuestion.add(rowRecord.toDocument());
            }
            doc.append(key, listOfQuestion);
        }
        return doc;
    }

    public static PivotRecord toObject(ItemProvider itemProvider, Document document) {

        PivotRecord pivotRecord = new PivotRecord(itemProvider);

        if (document != null) {
            pivotRecord.member = document.getObjectId("member");
            pivotRecord.objectId = document.getObjectId("_id");

            document.remove("member");
            document.remove("_id");

            for (String key : document.keySet()) {
                List<RowRecord> listOfRowRecord = new ArrayList<>();
                List<Document> list = (List) document.get(key);
                for (Document docForRowRecord : list) {
                    listOfRowRecord.add(RowRecord.toObject(docForRowRecord));
                }
                pivotRecord.rowRecordMap.put(key, listOfRowRecord);
            }

        }
        return pivotRecord;
    }

    public PivotRecord(ItemProvider itemProvider) {
        this.rowRecordMap = new HashMap<>();

        for (ItemField field : itemProvider.getFields()) {
            List<RowRecord> rowRecords = new ArrayList<>();

            for (ItemTedbirTuru tt : itemProvider.getTedbirTuleri()) {
                rowRecords.add(new RowRecord(tt.getCode(), null, null));
            }

            rowRecordMap.put(field.getCode(), rowRecords);

        }
    }

    public PivotRecord(ItemProvider itemProvider, Document document) {

    }

    public String getMongoIdAsString() {
        return objectId.toString();
    }

    /**
     * @return the myRecord
     */
    public Map<String, List<RowRecord>> getRowRecordMap() {
        return rowRecordMap;
    }

    /**
     * @param rowRecordMap the myRecord to set
     */
    public void setRowRecordMap(Map<String, List<RowRecord>> rowRecordMap) {
        this.rowRecordMap = rowRecordMap;
    }

    public ObjectId getObjectId() {
        return objectId;
    }

    public ObjectId getMember() {
        return member;
    }

    public boolean ok() {
        return ok;
    }

}
