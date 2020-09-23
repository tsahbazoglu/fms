package tr.org.tspb.common.jmx;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class MyJmxMonitor implements MyJmxConventionMXBean {

    public final static String objectName = "tr.org.tspb.common.jmx:type=MyJmxMonitor";
    private static final int DEFAULT_CACHE_SIZE = 200;
    private final String name = "Reginald";
    private int cacheSize = DEFAULT_CACHE_SIZE;

    @Override
    public void sayHello(String sessionId) {
        //ServletContext servletContext = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext());
        //final HttpSession session = (HttpSession) servletContext.getAttribute(sessionId);

    }

    @Override
    public void clearCache(String message) {
    }

    @Override
    public int add(int x, int y) {
        return x + y;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getCacheSize() {
        return this.cacheSize;
    }

    @Override
    public synchronized void setCacheSize(int size) {

        this.cacheSize = size;
    }

}
