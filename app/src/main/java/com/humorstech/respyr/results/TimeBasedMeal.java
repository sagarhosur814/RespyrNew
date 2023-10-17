package com.humorstech.respyr.results;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimeBasedMeal {

    public static String getMealTime() {
        // Get the current time
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        // Determine the meal based on the current hour
        String meal;
        if (hour >= 4 && hour < 12) {
            meal = "breakfast";
        } else if (hour >= 12 && hour < 16) {
            meal = "lunch";
        } else if (hour >= 16 && hour < 19) {
            meal = "snack";
        } else {
            meal = "dinner";
        }

        return meal;
    }

    public static String getFormattedTime() {
        // Get the current time
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Format the time as "hh:mm a"
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        String formattedTime = sdf.format(calendar.getTime());

        return formattedTime;
    }
}
