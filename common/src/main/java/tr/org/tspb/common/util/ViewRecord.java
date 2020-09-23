package tr.org.tspb.common.util;

import static tr.org.tspb.constants.ProjectConstants.NAME;
import java.util.Map;
import org.bson.Document;
import org.bson.types.BasicBSONList;
import tr.org.tspb.dao.MyField;
import tr.org.tspb.dao.MyForm;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class ViewRecord {

    private ViewRecord() {
    }

    public static class Builder {

        private StringBuilder sb;
        private Map<String, Object> map;
        private MyForm myForm;

        public Builder(Map dbo, MyForm myForm) {
            this.sb = new StringBuilder();
            this.myForm = myForm;
            this.map = dbo;
        }

        public Builder withKeyAndValue(String key) {
            this.sb.append(myForm.getField(key).getName())
                    .append(" : ")
                    .append(map.get(key).toString())
                    .append("<br/>");
            return this;
        }

        public Builder withValue(String key) {
            Object value = map.get(key);
            this.sb.append(value == null ? "" : value.toString());
            return this;
        }

        public Builder withAllKey() {
            for (String key : this.map.keySet()) {
                MyField myField = myForm.getField(key);
                Object value = map.get(key);
                this.sb.append(myField == null ? key : myField.getName())
                        .append(" : ")
                        .append(value == null ? "" : value.toString())
                        .append("<br/>");
            }
            return this;
        }

        public Builder withBR() {
            this.sb.append("<br/>");
            return this;
        }

        public Builder withHR() {
            this.sb.append("<hr/>");
            return this;
        }

        public Builder withAllKeyAsTable(boolean option) {

            this.sb.append("<style>");

            if (option) {
                this.sb.append(".td-key {");
                this.sb.append("   color: #3a5f94;");
                this.sb.append("   font-style : italic;");
                this.sb.append("}");
            } else {
                this.sb.append(".telman-table, th, td {");
                this.sb.append("   border: 1px solid gray;");
                this.sb.append("   text-align: left;");
                this.sb.append("   border-collapse: collapse;");
                this.sb.append("}");
            }

            this.sb.append("</style>");

            this.sb.append("<table class='telman-table'>");
            for (String key : this.map.keySet()) {

                if ("historyColumnModel".equals(key)) {
                    continue;
                }

                MyField myField = myForm.getField(key);
                Object value = map.get(key);

                if (!(value instanceof BasicBSONList)
                        && value instanceof Document
                        && ((Document) value).get(NAME) != null) {
                    value = ((Document) value).get(NAME);
                }

                this.sb.append("<tr>")
                        .append("<td>")
                        .append("<span class='td-key'>")
                        .append(myField == null ? key : myField.getName())
                        .append("</span>")
                        .append("</td>")
                        .append("<td>")
                        .append(" : ")
                        .append("<td>")
                        .append("<td>")
                        .append(value == null ? "" : value.toString())
                        .append("</td>")
                        .append("</tr>");
            }
            this.sb.append("</table>");
            return this;
        }

        public String build() {
            return sb.toString();
        }

    }

}
