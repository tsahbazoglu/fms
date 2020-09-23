package tr.org.tspb.fp;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class FgtPswdTokenManager implements Serializable {

    private Cache<String, Object> tokensShort;
    private Cache<String, Object> tokensLong;

    private static FgtPswdTokenManager instance;

    public static final int shortTermTokenTime = 120;

    protected FgtPswdTokenManager() {
        tokensShort = CacheBuilder.newBuilder().expireAfterWrite(shortTermTokenTime, TimeUnit.SECONDS).build();
        tokensLong = CacheBuilder.newBuilder().expireAfterWrite(12, TimeUnit.HOURS).build();
    }

    /**
     *
     * @param data
     * @param longShort
     * @return
     */
    public String getToken(Object data, int longShort) {
        String token = UUID.randomUUID().toString();

        //FIXME :  Idendity shoul take a place here.
        switch (longShort) {
            case 0:
                tokensShort.put(token, data);
                break;
            case 1:
                tokensLong.put(token, data);
                break;
        }
        return token;
    }

    /**
     *
     * @param token
     * @param data
     * @param longShort
     * @return
     */
    public String getToken(String token, Object data, int longShort) {
        switch (longShort) {
            case 0:
                tokensShort.put(token, data);
                break;
            case 1:
                tokensLong.put(token, data);
                break;
        }
        return token;
    }

    /**
     *
     * @param token
     * @param longShort
     * @return
     */
    public Boolean isTokenValid(String token, int longShort) {
        switch (longShort) {
            case 0:
                return tokensShort.getIfPresent(token) != null;
            case 1:
                return tokensLong.getIfPresent(token) != null;
        }
        return false;
    }

    /**
     *
     * @param token
     * @param longShort
     * @return
     */
    public Object getTokenData(String token, int longShort) {

        switch (longShort) {
            case 0:
                return tokensShort.getIfPresent(token);
            case 1:
                return tokensLong.getIfPresent(token);
        }
        return null;
    }

    /**
     *
     * @param token
     * @param longShort
     */
    public void invalidateToken(String token, int longShort) {
        switch (longShort) {
            case 0:
                tokensShort.invalidate(token);
            case 1:
                tokensLong.invalidate(token);
        }
    }

    /**
     *
     * @return
     */
    public static FgtPswdTokenManager instance() {

        if (instance == null) {
            instance = new FgtPswdTokenManager();
        }

        return instance;
    }

}
