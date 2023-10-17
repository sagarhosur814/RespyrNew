package com.humorstech.respyr.results;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.humorstech.respyr.utills.ActiveProfile;
import com.humorstech.respyr.utills.ReadingParameters;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

public class UserDataManager {




    public static String getUserFoodType(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(ActiveProfile.Title, Context.MODE_PRIVATE);
        String eatingType = sharedPreferences.getString(ActiveProfile.NOV_VEG, null);
        if (eatingType.equals("Never") || eatingType.equals("never")){
            return "veg";
        }else{
            return "nonveg";
        }
    }


    public static String getActivityLevel(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(ActiveProfile.Title, Context.MODE_PRIVATE);
        String exercise = sharedPreferences.getString(ActiveProfile.EXERCISE, null);

        switch (exercise){
            case "Sedentary" : return  "sedentary";
            case "Lightly" : return  "lightly_active";
            case "Moderately" : return  "moderately_active";
            case "Actively" : return  "very_active";
            case "Very Actively" : return  "extra_active";
            case "sedentary" : return  "sedentary";
            case "lightly_active" : return  "lightly_active";
            case "moderately_active" : return  "moderately_active";
            case "very_active" : return  "very_active";
            case "Very extra_active" : return  "extra_active";
            default: return "sedentary";
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getCurrentDayName() {
        LocalDate currentDate = LocalDate.now();
        DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
        return dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault());
    }
}
