package com.kap_vlad.android.javachat.messages;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateConverter {

    //формат сообщения, пришедшего с сервера 2018-04-01 19:12:59
    public static final DateFormat formatServer = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

    //формат сообщения, для вывода в сообщение 2018-04-01 19:12:59
    public static final DateFormat formatOut = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy", Locale.ENGLISH);


    public static String outDate (String inDate) {
        formatServer.setTimeZone(TimeZone.getTimeZone("GMT"));
        formatOut.setTimeZone(TimeZone.getDefault());
        java.util.Date date = null;
        try {
            date = formatServer.parse(inDate);
        }
        catch (ParseException e){
            return "00:00:00 00-00-0000";
        }
        return formatOut.format(date);
    }

    public static String outDate (Date date) {

        return formatOut.format(date);
    }
}





