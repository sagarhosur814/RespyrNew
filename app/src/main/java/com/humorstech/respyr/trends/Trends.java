package com.humorstech.respyr.trends;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.aries.ui.view.tab.CommonTabLayout;
import com.aries.ui.view.tab.listener.CustomTabEntity;
import com.aries.ui.view.tab.listener.OnTabSelectListener;
import com.google.android.material.tabs.TabLayout;
import com.humorstech.respyr.Animatoo1;
import com.humorstech.respyr.R;
import com.humorstech.respyr.StatusBarColor;
import com.humorstech.respyr.TabEntity;
import com.humorstech.respyr.daily_routine.ConfirmReading;
import com.humorstech.respyr.daily_routine.DailyRoutinForm;
import com.humorstech.respyr.dashboard.MainActivity;
import com.humorstech.respyr.dashboard.Methods;
import com.humorstech.respyr.profile.user.SelectProfile;
import com.humorstech.respyr.reward.Reward;
import com.humorstech.respyr.utills.ActiveProfile;
import com.humorstech.respyr.utills.BottomBarUtils;

import java.util.ArrayList;
import java.util.Objects;

public class Trends extends AppCompatActivity {
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private String activeProfileId, activeLoginId;

    private int backPressCount = 0;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trends);
        StatusBarColor statusBarColor = new StatusBarColor(this);
        statusBarColor.setColor(ContextCompat.getColor(this, R.color.white));

        SharedPreferences sharedPreferences = getSharedPreferences(ActiveProfile.Title, Context.MODE_PRIVATE);
        activeLoginId = sharedPreferences.getString(ActiveProfile.LOGIN_ID, null);
        activeProfileId = sharedPreferences.getString(ActiveProfile.PROFILE_ID, null);

        ImageView profileImage = findViewById(R.id.img_toolbar_av);
        Methods.setToolBar(this, activeProfileId, sharedPreferences.getString(ActiveProfile.GENDER, null), profileImage);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SelectProfile.class));
                Animatoo1.animateSlideLeft(Trends.this);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStart() {
        super.onStart();
        bottomNavigation();
        setupTabLayout();
    }

    private void bottomNavigation(){

        BottomBarUtils bottomBarUtils = new BottomBarUtils();
        int[] mIconSelectIds = bottomBarUtils.bottomSelectedIcons;
        int[] mIconUnSelectIds = bottomBarUtils.bottomUnSelectedIcons;
        ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
        String[] mTitles = bottomBarUtils.bottomTitles;

        CommonTabLayout tableLayoutHome;
        tableLayoutHome = findViewById(R.id.table_layout_home);
        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnSelectIds[i]));
        }
        tableLayoutHome.setTabData(mTabEntities);
        tableLayoutHome.setCurrentTab(2);

        tableLayoutHome.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {


                switch (position){
                    case 0:
                        startActivity(new Intent(Trends.this, MainActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        break;
                    case 1:
                        startActivity(new Intent(Trends.this, ConfirmReading.class));
                        overridePendingTransition(0,0);
                        finish();
                        break;
                    case 2:
                        startActivity(new Intent(Trends.this, Trends.class));
                        overridePendingTransition(0,0);
                        finish();
                        break;
                }
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (backPressCount == 1) {
            finish();
            Animatoo1.animateSlideRight(Trends.this);
        } else {
            // Show a toast message indicating the need to press again to exit
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
            backPressCount++;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupTabLayout() {
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        TrendAdapter adapter = new TrendAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);
        Objects.requireNonNull(tabLayout.getTabAt(0)).setText("Weekly");
        Objects.requireNonNull(tabLayout.getTabAt(1)).setText("Monthly");
        Objects.requireNonNull(tabLayout.getTabAt(2)).setText("All Time");

        tabLayout.setSelectedTabIndicator(null);

        int selectedTextColor = ContextCompat.getColor(this, R.color.white);
        int defaultTextColor = ContextCompat.getColor(this, R.color.grey);
        tabLayout.setTabTextColors(defaultTextColor, selectedTextColor);

        TabLayout.Tab tab = tabLayout.getTabAt(0);
        tab.view.setBackgroundColor(ContextCompat.getColor(this, R.color.primary));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.view.setBackgroundColor(ContextCompat.getColor(Trends.this, R.color.primary));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.view.setBackgroundColor(Color.TRANSPARENT);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }
}
