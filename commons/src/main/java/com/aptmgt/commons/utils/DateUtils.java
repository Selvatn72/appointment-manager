package com.aptmgt.commons.utils;

import java.sql.Time;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateUtils {

    private static final String DAY_IN_WEEK_FORMAT = "EEEE";

    public static final String TIME_FORMAT_12_HRS = "hh:mm a";

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static final String TIME_FORMAT = "HH:mm:ss";

    public static String getDayInWords(Date date) {
        return new SimpleDateFormat(DAY_IN_WEEK_FORMAT).format(date);
    }

    public static String getDateAsString(Date date) {
        return new SimpleDateFormat(DATE_FORMAT).format(date);
    }

    public static long getMilisFromMinutes(Integer minutes) {
        return TimeUnit.MINUTES.toMillis(minutes);
    }

    public static Time getSqlTimeFromUtilDate(Date date) {
        return new Time(date.getTime());
    }

    public static Time getTime(Object value) {
        try {
            return toTime(value);
        } catch (ParseException pe) {
            pe.printStackTrace();
            return null;
        }
    }

    public static Time toTime(Object value) throws ParseException {
        if (value == null)
            return null;
        if (value instanceof Time)
            return (Time) value;
        if (value instanceof String) {
            if ("".equals(value))
                return null;
            return new Time(new SimpleDateFormat(TIME_FORMAT).parse((String) value).getTime());
        }
        return new Time(new SimpleDateFormat(TIME_FORMAT).parse(value.toString()).getTime());
    }

    public static String get12HrsFrom24Hrs(Time time) {
        return new SimpleDateFormat(TIME_FORMAT_12_HRS).format(time);
    }

    public static String get12HrsFrom24Hrs(String time) throws ParseException {
        final SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT);
        final Date dateObj = sdf.parse(time);
        return new SimpleDateFormat("hh:mm aa").format(dateObj);
    }

    public static Time getForwardTime(Time startTime, Integer slotInterval) {
        Long timeMs = startTime.getTime();
        timeMs += getMilisFromMinutes(slotInterval);
        Date endTime = new Date(timeMs);
        return getSqlTimeFromUtilDate(endTime);
    }

    public static String getMonthInWords(Date date) {
        final Calendar calender = Calendar.getInstance();
        calender.setTime(date);
        return getMonthInWords(calender.get(Calendar.MONTH));
    }

    public static int getDayInMonth(Date date) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    public static String getMonthInWords(int num) {
        String month = " ";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (num >= 0 && num <= 11) {
            month = months[num];
        }
        return month;
    }

    public static int getYearFromDate(Date date) {
        final Calendar calender = Calendar.getInstance();
        calender.setTime(date);
        return calender.get(Calendar.YEAR);
    }

    public static Date getPreviousDate(Date date, Integer days) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, days);
        return getDateAlone(calendar.getTime());
    }

    public static Date getDateAlone(Date date) {
        try {
            return new SimpleDateFormat(DATE_FORMAT).parse(getDateAsString(date));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
