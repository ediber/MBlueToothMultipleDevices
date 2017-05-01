package com.example.nuvo.mbluetoothmultipledevices.utils;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    public static long differenceBetweenDatesLong(Date date1, Date date2) {
        return date2.getTime() - date1.getTime();
    }

    public static String longToStr( long difference, String format) {
        Date date = new Date(difference);
        return dateToStr(date, format);
    }

    public static String dateToStr(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String timeStr = sdf.format(date);
        return  timeStr;
    }

    public static Date getCurrentDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, 1);
        Date date = cal.getTime();
        return  date;
    }
}
