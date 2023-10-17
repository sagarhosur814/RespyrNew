package com.humorstech.respyr.dashboard;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import com.humorstech.respyr.R;

import java.util.Objects;

public class Methods {

    public static void setToolBar(Context context, String profileId, String gender, ImageView imgProfileAv) {
        int profileColor1 = ContextCompat.getColor(context, R.color.profile1);
        int profileColor2 = ContextCompat.getColor(context, R.color.profile2);
        int profileColor3 = ContextCompat.getColor(context, R.color.profile3);
        int profileColor4 = ContextCompat.getColor(context, R.color.profile4);

        PorterDuffColorFilter colorFilter1 = new PorterDuffColorFilter(profileColor1, PorterDuff.Mode.SRC_ATOP);
        PorterDuffColorFilter colorFilter2 = new PorterDuffColorFilter(profileColor2, PorterDuff.Mode.SRC_ATOP);
        PorterDuffColorFilter colorFilter3 = new PorterDuffColorFilter(profileColor3, PorterDuff.Mode.SRC_ATOP);
        PorterDuffColorFilter colorFilter4 = new PorterDuffColorFilter(profileColor4, PorterDuff.Mode.SRC_ATOP);

        if (gender != null) {
            if (gender.equalsIgnoreCase("male")) {
                imgProfileAv.setImageResource(R.drawable.profile_av1);
            } else if (gender.equalsIgnoreCase("female")) {
                imgProfileAv.setImageResource(R.drawable.profile_av2);
            } else {
                // Handle unexpected gender values here (e.g., show a default image)
                imgProfileAv.setImageResource(R.drawable.profile_av1);
            }
        } else {
            // Handle null gender here (e.g., show a default image)
            imgProfileAv.setImageResource(R.drawable.profile_av1);
        }

        if (profileId != null) {
            switch (profileId) {
                case "profile1":
                    imgProfileAv.getBackground().setColorFilter(colorFilter1);
                    break;
                case "profile2":
                    imgProfileAv.getBackground().setColorFilter(colorFilter2);
                    break;
                case "profile3":
                    imgProfileAv.getBackground().setColorFilter(colorFilter3);
                    break;
                case "profile4":
                    imgProfileAv.getBackground().setColorFilter(colorFilter4);
                    break;
                default:
                    // Handle unexpected profileId values here
                    break;
            }
        } else {
            // Handle null profileId here (e.g., show a default background color)
            imgProfileAv.getBackground().setColorFilter(ContextCompat.getColor(context, R.color.green), PorterDuff.Mode.SRC_ATOP);
        }
    }
}
