package com.humorstech.respyr.dashboard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.widget.TextView;
import androidx.viewpager.widget.ViewPager;
import com.afollestad.viewpagerdots.DotsIndicator;
import com.humorstech.respyr.HeroBannerAdapter;
import com.humorstech.respyr.HeroBannerImageModel;
import com.humorstech.respyr.R;
import com.humorstech.respyr.authentication.login.Login;
import com.humorstech.respyr.utills.ActiveProfile;
import com.humorstech.respyr.utills.LoginUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Stuffs {


    public static void performGrowth(float yesterdayValue, float todayValue, float avg, TextView txtGrowth, TextView txtGrowthMessage){

        float percentageImprovement = ((todayValue - yesterdayValue) / yesterdayValue) * 100;

        txtGrowthMessage.setText(String.valueOf(Math.round(avg))+ "%");

        int icUP = R.drawable.increasing_icon;
        int icDown = R.drawable.decreasing_icon;

        txtGrowth.setText(String.valueOf(Math.abs(Math.round(percentageImprovement))) + "%");



        if (percentageImprovement<0){

            txtGrowth.setCompoundDrawablesWithIntrinsicBounds(0, 0,icDown, 0);
        }else if(percentageImprovement==0){
        }else if(percentageImprovement > 0){
            txtGrowth.setCompoundDrawablesWithIntrinsicBounds(0, 0,icUP, 0);
        }
    }


    public  static void performLogout(Activity activity) {
        SharedPreferences preferences = activity.getApplicationContext().getSharedPreferences(LoginUtils.LOGIN_TITLE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        SharedPreferences preferences1 = activity.getApplicationContext().getSharedPreferences(ActiveProfile.Title, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor1 = preferences1.edit();
        editor1.clear();
        editor1.apply();

        Intent intent = new Intent(activity.getApplicationContext(), Login.class);
        activity.startActivity(intent);
        activity.finish();
    }



    public static void mainDashboardHeadingBanner(final Activity activity, final ViewPager viewPager, final DotsIndicator dotsIndicator) {
        final List<HeroBannerImageModel> images = new ArrayList<>();
        images.add(new HeroBannerImageModel(R.drawable.banner1));
        images.add(new HeroBannerImageModel(R.drawable.banner2));

        final HeroBannerAdapter adapter = new HeroBannerAdapter(activity, images);
        viewPager.setAdapter(adapter);

        dotsIndicator.attachViewPager(viewPager);

        final Handler handler = new Handler();
        final Runnable update = new Runnable() {
            int currentPage = 0;

            @Override
            public void run() {
                if (currentPage == images.size()) {
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++, true);
                handler.postDelayed(this, 10000); // Change slide interval here (in milliseconds)
            }
        };

        // Start auto sliding
        handler.postDelayed(update, 10000); // Delayed start (in milliseconds)
    }




    public static Calendar  getCurrentDate(){
        int mYear, mMonth, mDay;

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        Calendar selectedDate = Calendar.getInstance();
        selectedDate.set(mYear, mMonth, mDay);

        return  selectedDate;
    }
}
