package com.example.kreatimont.smartbusschedule;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateConverter {

    public static Date convertStringToDate(String string) {

        try {
            return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String convertDateToString(Date date) {
        return DateFormat.getDateInstance(SimpleDateFormat.LONG, new Locale("ru")).format(date);
    }

}
