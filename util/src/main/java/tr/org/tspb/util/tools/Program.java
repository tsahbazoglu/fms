package tr.org.tspb.util.tools;

import java.util.*;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class Program {

    private static final int millisPerHour = 60 * 60 * 1000;

    public static void main(String[] args) {

        TimeZone tz = SimpleTimeZone.getTimeZone("Europe/Istanbul");
        tz = SimpleTimeZone.getDefault();

        /* 
         * ERRORR ın DEFAULT TIME ZONE FOR TURKEY
         * 
         * it is not convenient wiht javascript
         * 
         * it doesnot take into account the javascript behavior
         * 
         */
        //abstract public class TimeZone implements Serializable, Cloneable   ERRORR 
        tz = new SimpleTimeZone(2 * millisPerHour,//
                "Europe/Istanbul" /*EE%sT*/,//ID
                Calendar.MARCH, //startMonth
                -1, //startDay
                Calendar.SUNDAY /*DOW_IN_DOM*/, //startDayOfWeek
                3 * millisPerHour,//startTime

                Calendar.OCTOBER, //endMonth
                -1,//endDay
                Calendar.SUNDAY /*DOW_IN_DOM*/, //endDayOfWeek
                3 * millisPerHour, //endTime
                1 * millisPerHour);

        /*
         * Exact day of month Day Setting 
         * To specify an exact day of month, set the month and day-of-month to an exact value,
         * and day-of-week to zero. 
         * For example, to specify March 1, set the month to MARCH, 
         * day-of-month to 1, and day-of-week to 0.
         * 
         * Turkey
         * ======
         * Daylight Saving Time 2011
         * DST starts on Monday 28 March 2011, 03:00 local standard time
         * DST ends on Sunday 30 October 2011, 04:00 local daylight time
         */
        tz = new SimpleTimeZone(2 * millisPerHour,//
                "Europe/Istanbul" /*EE%sT*/,//ID
                Calendar.MARCH, //startMonth
                28, //startDay
                0, //startDayOfWeek
                3 * millisPerHour,//startTime
                Calendar.OCTOBER, //endMonth
                30,//endDay
                0, //endDayOfWeek
                3 * millisPerHour, //endTime
                1 * millisPerHour);

        Calendar calendar = Calendar.getInstance(tz);

        calendar.clear();
        calendar.set(2011, Calendar.MARCH, 28);

        calendar.clear();
        calendar.set(2011, Calendar.MARCH, 28, 0, 0);

        calendar.clear();
        calendar.set(2011, Calendar.MARCH, 28, 3, 0);

        calendar.clear();
        calendar.set(2011, Calendar.MARCH, 28, 4, 0);
        /*
         * JavaScript results :
         * new Date(2011,2,28,3,0).getTimezoneOffset() 
         * new Date(2011,2,28,4,0).getTimezoneOffset()
         * 120
         * 180 
         * new Date(2011,9,30,2,0).getTimezoneOffset()
         * new Date(2011,9,30,3,0).getTimezoneOffset() 
         * new Date(2011,9,30,4,0).getTimezoneOffset()
         * 180
         * 120
         * 120
         */    }
}
