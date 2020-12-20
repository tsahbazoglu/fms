/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tr.org.tspb.dao;

import org.bson.Document;
import tr.org.tspb.datamodel.expected.FmsScriptRunner;

/**
 *
 * @author telman
 */
public class TagAjaxRef {

    private final String db;
    private final String table;
    private final Document filter;
    private Document render;

    public TagAjaxRef(Document ajaxDocRef) {
        this.db = ajaxDocRef.getString("db");
        this.table = ajaxDocRef.getString("table");
        this.filter = ajaxDocRef.get("filter", Document.class);
    }

    public void resolveRenderedFields(FmsScriptRunner fmsScriptRunner, MyMap crud) {
        this.render = new Document();

        Document query = new Document();

        for (String key : filter.keySet()) {
            if (crud.get(key) != null) {
                query.append(key, crud.get(key));
            }
        }

        Document doc = fmsScriptRunner.findOne(db, table, query);
        if (doc != null) {
            this.render = doc.get("render", Document.class);
        }

    }

    public String getDb() {
        return db;
    }

    public String getTable() {
        return table;
    }

    public Document getFilter() {
        return filter;
    }

    public Document getRender() {
        return render;
    }

}
