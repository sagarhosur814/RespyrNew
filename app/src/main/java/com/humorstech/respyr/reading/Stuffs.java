package com.humorstech.respyr.reading;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

public class Stuffs {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("E, d MMM yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("H:mm a");

    public static String getCurrentTime() {
        return formatDateTime(LocalDateTime.now(), TIME_FORMATTER);
    }

    public static String getCurrentDate() {
        return formatDateTime(LocalDateTime.now(), DATE_FORMATTER);
    }

    private static String formatDateTime(LocalDateTime dateTime, DateTimeFormatter formatter) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            return dateTime.format(formatter);
        }
        return "";
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String timestampToDayName(long timestamp) {

        // Convert Unix timestamp to Instant in Indian time zone (Asia/Kolkata)
        Instant instant = Instant.ofEpochSecond(timestamp)
                .atZone(ZoneId.of("Asia/Kolkata")) // Set the desired time zone
                .toInstant();

        // Get day name from Instant
        return  instant.atZone(ZoneId.of("Asia/Kolkata"))
                .getDayOfWeek()
                .getDisplayName(TextStyle.SHORT, Locale.ENGLISH);


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String dateToDayName(String dateStr){
        LocalDate date = LocalDate.parse(dateStr);
        // Get the day name
        return  date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);

    }
}
