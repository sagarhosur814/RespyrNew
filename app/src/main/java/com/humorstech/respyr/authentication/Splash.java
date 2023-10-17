package com.humorstech.respyr.authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.firebase.messaging.FirebaseMessaging;
import com.humorstech.respyr.Animatoo1;
import com.humorstech.respyr.R;
import com.humorstech.respyr.StatusBarColor;
import com.humorstech.respyr.authentication.login.Login;
import com.humorstech.respyr.authentication.login.SelectProfile;
import com.humorstech.respyr.utills.LoginUtils;
import com.humorstech.respyr.utills.ProfileCreationData;

public class Splash extends AppCompatActivity {

    private static final String TAG = "Splash";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Set status bar color
        StatusBarColor statusBarColor = new StatusBarColor(this);
        statusBarColor.setColor(getResources().getColor(R.color.white));

        ImageView splashImage = findViewById(R.id.splash_image);
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        splashImage.startAnimation(fadeInAnimation);

        SharedPreferences sharedPreferences = getSharedPreferences(LoginUtils.LOGIN_TITLE, Context.MODE_PRIVATE);
        String loginId = sharedPreferences.getString(LoginUtils.LOGIN_ID, "null");



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;


                if (isFirstTime()){
                    SharedPreferences sharedPreferences1 = getSharedPreferences("FIRST_TIME", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences1.edit();
                    editor.putBoolean("IS_FIRST_TIME", false);
                    editor.apply();

                    intent = new Intent(getApplicationContext(), Welcome.class);
                }else{
                    if (!loginId.equals("null")) {
                        // Already logged in
                        intent = new Intent(getApplicationContext(), SelectProfile.class);
                    } else {
                        // Logged out
                        intent = new Intent(getApplicationContext(), Login.class);
                    }
                }

                startActivity(intent);
                finish();
            }
        }, 1000);
    }

    private boolean isFirstTime(){
        SharedPreferences sharedPreferences1 = getSharedPreferences("FIRST_TIME", Context.MODE_PRIVATE);
        return  sharedPreferences1.getBoolean("IS_FIRST_TIME", true); // Provide a default value in case the key is not found
    }
}
