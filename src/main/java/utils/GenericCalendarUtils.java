package utils;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class GenericCalendarUtils {

    public static String getWeek(Date recordDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(recordDate);

        // "calculate" the start date of the week
        Calendar first = (Calendar) cal.clone();
        first.add(Calendar.DAY_OF_WEEK,
                first.getFirstDayOfWeek() - first.get(Calendar.DAY_OF_WEEK));

        // and add six days to the end date
        Calendar last = (Calendar) first.clone();
        last.add(Calendar.DAY_OF_YEAR, 6);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return (df.format(first.getTime()) + "_" +
                df.format(last.getTime()));
    }

    public static int getIndexByWeekDayAmPm(Date recordDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(recordDate);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }
}
