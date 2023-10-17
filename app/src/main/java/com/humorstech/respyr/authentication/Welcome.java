package com.humorstech.respyr.authentication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.humorstech.respyr.Animatoo1;
import com.humorstech.respyr.R;
import com.humorstech.respyr.StatusBarColor;
import com.humorstech.respyr.authentication.login.Login;
import com.humorstech.respyr.dashboard.MainActivity;

public class Welcome extends AppCompatActivity {

    ViewPager mSlideViewPager;
    LinearLayout mDotLayout;
    Button nextBtn, skipBtn;

    TextView[] dots;
    ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        try {
            StatusBarColor statusBarColor = new StatusBarColor(Welcome.this);
            statusBarColor.setColor(getResources().getColor(R.color.white));

            nextBtn = findViewById(R.id.nextButton);
            skipBtn = findViewById(R.id.skipButton);

            nextBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getItem(0) < 2)
                        mSlideViewPager.setCurrentItem(getItem(1), true);
                    else {
                        Intent intent = new Intent(Welcome.this, Login.class);
                        startActivity(intent);
                        finish();
                        Animatoo1.animateSlideLeft(Welcome.this);
                    }
                }
            });

            skipBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Welcome.this, Login.class);
                    startActivity(intent);
                    finish();
                    Animatoo1.animateSlideLeft(Welcome.this);
                }
            });

            mSlideViewPager = findViewById(R.id.slideViewPager);
            mDotLayout = findViewById(R.id.indicator_layout);

            viewPagerAdapter = new ViewPagerAdapter(this);

            mSlideViewPager.setAdapter(viewPagerAdapter);

            setUpIndicator(0);
            mSlideViewPager.addOnPageChangeListener(viewListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setUpIndicator(int position) {
        try {
            dots = new TextView[3];
            mDotLayout.removeAllViews();

            for (int i = 0; i < dots.length; i++) {
                dots[i] = new TextView(this);
                dots[i].setText(Html.fromHtml("&#8226"));
                dots[i].setTextSize(50);
                dots[i].setTextColor(getResources().getColor(R.color.light, getApplicationContext().getTheme()));
                mDotLayout.addView(dots[i]);
            }

            dots[position].setTextColor(getResources().getColor(R.color.black, getApplicationContext().getTheme()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            try {
                setUpIndicator(position);

                if (position >= 2) {
                    nextBtn.setText("Get Started");
                } else {
                    nextBtn.setText("Next");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private int getItem(int i) {
        return mSlideViewPager.getCurrentItem() + i;
    }
}
