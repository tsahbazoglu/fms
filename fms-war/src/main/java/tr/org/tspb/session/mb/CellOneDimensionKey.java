package tr.org.tspb.session.mb;

import static tr.org.tspb.constants.ProjectConstants.COMMA;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class CellOneDimensionKey {

    private Object axisX;
    private Object axisY;

    /**
     * @param axisX
     * @param axisY
     */
    public CellOneDimensionKey(Object axisX, Object axisY) {
        this.axisX = axisX;
        this.axisY = axisY;
    }

    /**
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
        if (obj instanceof CellOneDimensionKey) {
            CellOneDimensionKey other = (CellOneDimensionKey) obj;
            return other.getAxisX().equals(getAxisX()) && other.getAxisY().equals(getAxisY());
        }
        return super.equals(obj);
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return (12345 + getAxisX().hashCode()) * (67890 + getAxisY().hashCode());
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getAxisX().toString() + COMMA + getAxisY().toString();
    }

    /**
     * @return the axisX
     */
    public Object getAxisX() {
        return axisX;
    }

    /**
     * @param axisX the axisX to set
     */
    public void setAxisX(Object axisX) {
        this.axisX = axisX;
    }

    /**
     * @return the axisY
     */
    public Object getAxisY() {
        return axisY;
    }

    /**
     * @param axisY the axisY to set
     */
    public void setAxisY(Object axisY) {
        this.axisY = axisY;
    }
}
