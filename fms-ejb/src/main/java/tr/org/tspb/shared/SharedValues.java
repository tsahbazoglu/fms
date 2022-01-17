package tr.org.tspb.shared;

import java.util.Calendar;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class SharedValues {

    public static final String TEST = "TEST";
    public static final String LOCAL = "LOCAL";
    public static final String PRODUCT = "PRODUCT";
    public static final String ITEMS = "items";
    public static final String DB = "db";
    public static final String RESULT = "result";
    public static final String FORMS = "forms";
    //
    public static final String MONGODB_STOREDATA = "storedata";
    public static final String DB_LOGS = "dblogs";
    public static final String DB_CONFIG = "configdb";
    public static final String COLLECTION_UPLOADEDFILE = "uploadedfile";
    public static final String COLLECTION_USERS = "users";
    public static final String COLLECTION_PROJECT = "project3";
    public static final String PATTERN_MAPREDUCE_NAME_DEPARTMENT_USERS_AND_LEADERS = "mapReduceDepartmentUserEmailsPivot";
    public static final String PATTERN_MAPREDUCE_NAME_LEADER_DEPARTMENT_PERSONEL = "mapReduceLeaderDepratmenUser";
    public static final String PATTERN_MAPREDUCE_NAME_IOLOG_MIN = "minMapReduce";
    public static final String PATTERN_MAPREDUCE_NAME_IOLOG_MAX = "maxMapReduce";
    public static final String PATTERN_MAPREDUCE_NAME_IOLOG_MIN_MAX = "mapReduceInMinOutMax";
    public static final String PATTERN_MAPREDUCE_OUT_COLLECTION = "outCollection";
    public static final String PATTERN_MAPREDUCE_IN_COLLECTION = "inCollection";
    public static final String PATTERN_FIELD = "field";
    private static final int MILLIS_PER_HOUR = 60 * 60 * 1000;

    public static Calendar getCalendarTurkey() {
        TimeZone tz = new SimpleTimeZone(2 * MILLIS_PER_HOUR,//
                "Europe/Istanbul" /*
                 * EE%sT
                 */,//ID
                Calendar.MARCH, //startMonth
                28, //startDay
                0, //startDayOfWeek
                3 * MILLIS_PER_HOUR,//startTime
                Calendar.OCTOBER, //endMonth
                30,//endDay
                0, //endDayOfWeek
                3 * MILLIS_PER_HOUR, //endTime
                1 * MILLIS_PER_HOUR);

        return Calendar.getInstance(tz);

    }
}
