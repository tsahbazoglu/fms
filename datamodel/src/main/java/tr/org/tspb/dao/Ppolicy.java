package tr.org.tspb.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.bson.Document;
import tr.org.tspb.constants.ProjectConstants;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class Ppolicy {

    private static final String _uid = "uid";
    private static final String _lastLoginTime = "lastLoginTime";
    private static final String _changePswdTime = "changePswdTime";
    private static final String _lockTime = "lockTime";
    private static final String _lock = "lock";
    private static final String _lockPeriodMinute = "lockPeriodMinute";
    private static final String _expirationDay = "expirationDay";
    private static final String _tryCount = "tryCount";
    private static final String _maxTryCount = "maxTryCount";
    private static final String _lastLoginIP = "lastLoginIP";
    private static final String _strongRegEx = "strongRegEx";
    private static final String _note = "note";
    private static final String _forms = "forms";
    private String uid;
    private Date lastLoginTime;
    private Date changePswdTime;
    private Date lockTime;
    private boolean lock;
    private int lockPeriodMinute;
    private int expirationDay;
    private int tryCount;
    private int maxTryCount;
    private String lastLoginIP;
    private String strongRegEx;
    private String note;
    private String forms;

    private Ppolicy() {
    }

    public Date getChangePswdTime() {
        return changePswdTime;
    }

    public Date getLockTime() {
        return lockTime;
    }

    public int getLockPeriodMinute() {
        return lockPeriodMinute;
    }

    public int getMaxTryCount() {
        return maxTryCount;
    }

    public Map createQuery() {
        return new Document(_uid, uid);
    }

    public String getUid() {
        return uid;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public String getLastLoginIP() {
        return lastLoginIP;
    }

    public int getExpirationDay() {
        return expirationDay;
    }

    public int getTryCount() {
        return tryCount;
    }

    public Map createZeroTryCountSet() {
        return new Document(_tryCount, 0);
    }

    public Map createTryCountSet() {
        return new Document(ProjectConstants.DOLAR_SET, new Document(_tryCount, tryCount));
    }

    public Map createUpdateSet() {
        return new Document(_lastLoginTime, lastLoginTime)
                .append(_changePswdTime, changePswdTime)
                .append(_lastLoginIP, lastLoginIP)
                .append(_tryCount, tryCount);
    }

    public boolean hasBeenBlocked() {
        if (lock) {
            return true;
        }
        return tryCount >= maxTryCount;
    }

    public void incrementTryCount() {
        this.tryCount++;
    }

    public boolean isLockExpired() {
        if (lockTime != null && lockPeriodMinute != 0) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(lockTime);
            cal.add(Calendar.MINUTE, 30);
            return cal.getTime().before(new Date());
        }
        return false;
    }

    public boolean isBlocked() {
        return lock;
    }

    public String getStrongRegEx() {
        return strongRegEx;
    }

    public String getNote() {
        return note;
    }

    public boolean isExpired() {

        if (lastLoginTime == null || expirationDay == 0) {
            return true;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(lastLoginTime);
        cal.add(Calendar.DATE, expirationDay);
        return cal.getTime().before(new Date());
    }

    public boolean isLock() {
        return lock;
    }

    public Map toDocument() {
        Map updateDoc = new HashMap();
        updateDoc.put(_uid, this.uid);
        updateDoc.put(_lastLoginTime, this.lastLoginTime);
        updateDoc.put(_changePswdTime, this.changePswdTime);
        updateDoc.put(_lastLoginIP, this.lastLoginIP);
        updateDoc.put(_expirationDay, this.expirationDay);
        updateDoc.put(_tryCount, this.tryCount);
        updateDoc.put(_maxTryCount, this.maxTryCount);
        updateDoc.put(_lock, this.lock);
        updateDoc.put(_lockTime, this.lockTime);
        updateDoc.put(_lockPeriodMinute, this.lockPeriodMinute);
        updateDoc.put(_strongRegEx, this.strongRegEx);
        updateDoc.put(_note, this.note);
        updateDoc.put(_forms, this.forms);
        return updateDoc;
    }

    public static class Builder {

        private Ppolicy ppolicy;

        public Builder() {
            this.ppolicy = new Ppolicy();
        }

        public Builder withMongoDBO(Map dbo) {
            this.ppolicy.uid = (String) dbo.get(Ppolicy._uid);
            this.ppolicy.lastLoginTime = (Date) dbo.get(Ppolicy._lastLoginTime);
            this.ppolicy.changePswdTime = (Date) dbo.get(Ppolicy._changePswdTime);
            this.ppolicy.lockTime = (Date) dbo.get(Ppolicy._lockTime);
            this.ppolicy.lock = Boolean.TRUE.equals(dbo.get(Ppolicy._lock));
            this.ppolicy.lockPeriodMinute = ((Number) dbo.get(Ppolicy._lockPeriodMinute)).intValue();
            this.ppolicy.expirationDay = ((Number) dbo.get(Ppolicy._expirationDay)).intValue();
            Object obj = dbo.get(Ppolicy._tryCount);
            if (obj instanceof Number) {
                this.ppolicy.tryCount = ((Number) obj).intValue();
            }
            this.ppolicy.maxTryCount = ((Number) dbo.get(Ppolicy._maxTryCount)).intValue();
            this.ppolicy.lastLoginIP = (String) dbo.get(Ppolicy._lastLoginIP);
            this.ppolicy.strongRegEx = (String) dbo.get(Ppolicy._strongRegEx);
            this.ppolicy.note = (String) dbo.get(Ppolicy._note);
            return this;
        }

        public Builder withDefault() {
            this.ppolicy.forms = "ppolicy";
            this.ppolicy.lastLoginIP = null;
            this.ppolicy.expirationDay = 30;
            this.ppolicy.lockPeriodMinute = 30;
            this.ppolicy.tryCount = 0;
            this.ppolicy.maxTryCount = 5;
            this.ppolicy.lock = false;

            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.DATE, -35);
            this.ppolicy.lastLoginTime = cal.getTime();
            return this;
        }

        public Builder withLastLoginIP(String lastLoginIP) {
            this.ppolicy.lastLoginIP = lastLoginIP;
            return this;
        }

        public Builder withChangePswdTime(Date today) {
            this.ppolicy.changePswdTime = today;
            return this;
        }

        public Builder withLastLoginTime(Date today) {
            this.ppolicy.lastLoginTime = today;
            return this;
        }

        public Builder withTryCount(int tryCount) {
            this.ppolicy.tryCount = tryCount;
            return this;
        }

        public Builder withUid(String username) {
            this.ppolicy.uid = username;
            return this;
        }

        public Builder withLastLoginIp(String reqRemoteAddr) {
            this.ppolicy.lastLoginIP = reqRemoteAddr;
            return this;
        }

        public Ppolicy build() {
            return this.ppolicy;
        }

    }

}
