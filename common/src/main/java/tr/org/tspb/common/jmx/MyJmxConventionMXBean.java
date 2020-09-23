package tr.org.tspb.common.jmx;

/**
 *
 * @author Telman Şahbazoğlu
 */
public interface MyJmxConventionMXBean {

    void sayHello(String message);

    void clearCache(String message);

    int add(int x, int y);

    String getName();

    int getCacheSize();

    void setCacheSize(int size);
}
