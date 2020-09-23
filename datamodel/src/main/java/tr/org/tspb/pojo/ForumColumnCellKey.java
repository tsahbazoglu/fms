package tr.org.tspb.pojo;

import static tr.org.tspb.constants.ProjectConstants.COMMA;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class ForumColumnCellKey {

    private final String form;
    private final String column;

    /**
     * @param form
     * @param column
     */
    public ForumColumnCellKey(String form, String column) {
        this.form = form;
        this.column = column;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    /**
     * @param obj
     * @return
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj instanceof ForumColumnCellKey) {
            ForumColumnCellKey other = (ForumColumnCellKey) obj;
            return other.form.equals(form) && other.column.equals(column);
        }
        return super.equals(obj);
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    /**
     * @return @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return (12345 + form.hashCode()) * (67890 + column.hashCode());
    }

    /**
     * @see java.lang.Object#toString()
     */
    /**
     * @return @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return form.concat(COMMA).concat(column);
    }
}
