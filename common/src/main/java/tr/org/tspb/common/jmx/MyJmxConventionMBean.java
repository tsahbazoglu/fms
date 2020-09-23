package tr.org.tspb.common.jmx;

/**
 *
 * @author Telman Şahbazoğlu
 */
public interface MyJmxConventionMBean {

    void sayHello();

    int add(int x, int y);

    String getName();

    int getCacheSize();

    void setCacheSize(int size);
}
