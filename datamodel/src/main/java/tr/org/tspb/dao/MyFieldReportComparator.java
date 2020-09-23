package tr.org.tspb.dao;

import java.util.Comparator;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class MyFieldReportComparator implements Comparator<MyField> {

    @Override
    public int compare(MyField t1, MyField t2) {
        Integer order1 = t1.getReportOrder();
        Integer order2 = t2.getReportOrder();
        return order1.compareTo(order2);
    }

}
