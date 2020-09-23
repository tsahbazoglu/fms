package tr.org.tspb.dao;

import java.util.Comparator;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class MyFieldComparator implements Comparator<MyField> {

    @Override
    public int compare(MyField t1, MyField t2) {
        Integer order1 = t1.getOrder();
        Integer order2 = t2.getOrder();
        return order1.compareTo(order2);
    }

}
