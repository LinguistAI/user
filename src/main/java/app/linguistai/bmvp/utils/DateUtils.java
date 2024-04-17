package app.linguistai.bmvp.utils;

import java.time.LocalDate;
import java.util.Calendar;

public class DateUtils {
    public static java.sql.Date convertUtilDateToSqlDate(java.util.Date utilDate) {
        return utilDate != null ? new java.sql.Date(utilDate.getTime()) : null;
    }

    public static java.util.Date convertSqlDateToUtilDate(java.sql.Date sqlDate) {
        return sqlDate != null ? new java.util.Date(sqlDate.getTime()) : null;
    }

    public static Boolean isSqlDatesEqual(java.sql.Date date1, java.sql.Date date2) {
        return date1.toLocalDate().isEqual(date2.toLocalDate());
    }

    public static java.util.Date addTime(java.util.Date date, int days, int hours, int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        if (days != 0) {
            calendar.add(Calendar.DAY_OF_MONTH, days);
        }
        if (hours != 0) {
            calendar.add(Calendar.HOUR_OF_DAY, hours);
        }
        if (minutes != 0) {
            calendar.add(Calendar.MINUTE, minutes);
        }

        return calendar.getTime();
    }

    public static java.sql.Date convertLocalDateToSqlDate(java.time.LocalDate localDate) {
        return localDate != null ? java.sql.Date.valueOf(localDate) : null;
    }

    public static java.util.Date convertLocalDateToUtilDate(java.time.LocalDate localDate) {
        return convertSqlDateToUtilDate(convertLocalDateToSqlDate(localDate));
    }
}
