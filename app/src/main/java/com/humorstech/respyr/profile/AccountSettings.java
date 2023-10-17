package com.humorstech.respyr.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.humorstech.respyr.Animatoo1;
import com.humorstech.respyr.DeleteAccount;
import com.humorstech.respyr.Logout;
import com.humorstech.respyr.R;
import com.humorstech.respyr.StatusBarColor;
import com.humorstech.respyr.profile.user.ProfileData;
import com.humorstech.respyr.utills.ActiveProfile;
import com.humorstech.respyr.utills.LoginUtils;

public class AccountSettings extends AppCompatActivity {

    private TextView txtPrimaryMobileNumber, txtPrimaryEmailAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        StatusBarColor statusBarColor= new StatusBarColor(  AccountSettings.this);
        statusBarColor.setColor(getResources().getColor(R.color.white));


        init();

    }

    private void init(){
        initVars();
        onClick();
        setTexts();
    }

    @SuppressLint("SetTextI18n")
    private void setTexts(){


        SharedPreferences preferences1 = getSharedPreferences(LoginUtils.LOGIN_TITLE, Context.MODE_PRIVATE);
        String userLoginId  = preferences1.getString(LoginUtils.LOGIN_ID,null);
        String userPhoneNumber  = preferences1.getString(LoginUtils.PHONE_NUMBER,null);

        SharedPreferences preferences2 = getSharedPreferences(ActiveProfile.Title, Context.MODE_PRIVATE);
        String userEmailAddress  = preferences2.getString(ActiveProfile.EMAIL,null);

        txtPrimaryMobileNumber.setText("+91 " + userPhoneNumber);
        txtPrimaryEmailAddress.setText(userEmailAddress);
    }

    private void initVars(){
        txtPrimaryMobileNumber = findViewById(R.id.txt_primary_account_number);
        txtPrimaryEmailAddress = findViewById(R.id.txt_primary_email_address);
    }
    private void onClick(){
        Button buttonDeleteAccount =findViewById(R.id.button_delete_account);
        buttonDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DeleteAccount.show(AccountSettings.this);
            }
        });

        ImageButton buttonBack =findViewById(R.id.button_back);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo1.animateSlideRight(AccountSettings.this);
    }
}