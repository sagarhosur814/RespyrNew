package com.humorstech.respyr.profile.profile_edit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.humorstech.respyr.Animatoo1;
import com.humorstech.respyr.HTTP_URLS;
import com.humorstech.respyr.MathMine;
import com.humorstech.respyr.R;
import com.humorstech.respyr.StatusBarColor;
import com.humorstech.respyr.authentication.login.Login;
import com.humorstech.respyr.dashboard.MainActivity;
import com.humorstech.respyr.dashboard.Methods;
import com.humorstech.respyr.profile.Profile;
import com.humorstech.respyr.utills.ActiveProfile;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.techiness.progressdialoglibrary.ProgressDialog;

import cz.msebera.android.httpclient.Header;

public class PersonalInfoEdit extends AppCompatActivity {


    private EditText etProfileName, etProfileAge, etProfileHeight, etProfileWeight, etProfileBMI;
    private String profileName, profileAge, profileHeight, profileWeight,profileGender, profileBMI;
    private String profileId,loginId;

    private RadioGroup rgProfileGender, rgHeightType;
    private RadioButton rbProfileGenderMale, rbProfileGenderFemale;


    private  ProgressDialog progressDialog;

    private double height, weight;

    private ImageView imgProfileAv;

    private boolean isHeightInFeet = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info_edit);
        StatusBarColor statusBarColor= new StatusBarColor(  PersonalInfoEdit.this);
        statusBarColor.setDarkColor(getResources().getColor(R.color.primary));


        init();

    }
    private void init(){
        initVars();
        onClicks();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setCurrentProfileData();
    }

    private void initVars(){
        progressDialog =  new ProgressDialog(PersonalInfoEdit.this);

        etProfileName=findViewById(R.id.profile_change_name);
        etProfileAge=findViewById(R.id.profile_change_age);
        etProfileHeight=findViewById(R.id.profile_change_height);
        etProfileWeight=findViewById(R.id.profile_change_weight);
        etProfileBMI=findViewById(R.id.profile_change_bmi);

        rgProfileGender=findViewById(R.id.rg_gender);
        rbProfileGenderMale=findViewById(R.id.rb_gender_male);
        rbProfileGenderFemale=findViewById(R.id.rb_gender_female);

        rgHeightType=findViewById(R.id.rg_height_type);

        imgProfileAv=findViewById(R.id.img_profile_av);
    }
    private void onClicks(){
        ImageButton buttonBack=findViewById(R.id.btn_back);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Button buttonUpdateProfileData =findViewById(R.id.button_update_profile_photo);
        buttonUpdateProfileData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PersonalInfoEdit.this, "Coming soon", Toast.LENGTH_SHORT).show();
            }
        });

        rgProfileGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_gender_male:
                        // Handle the selection of the "Male" radio button
                        // You can perform actions specific to the "Male" selection here
                        profileGender="Male";
                        break;
                    case R.id.rb_gender_female:
                        // Handle the selection of the "Female" radio button
                        // You can perform actions specific to the "Female" selection here
                        profileGender="Female";
                        break;
                }
            }
        });


        rgHeightType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_height_cm:
                        // Handle the selection of the "Male" radio button
                        // You can perform actions specific to the "Male" selection here
                       isHeightInFeet=false;
                        break;
                    case R.id.rb_height_ft:
                        // Handle the selection of the "Female" radio button
                        // You can perform actions specific to the "Female" selection here
                        isHeightInFeet=true;
                        break;
                }
            }
        });



        Button buttonSaveProfile=findViewById(R.id.button_save_profile);
        buttonSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upDatePersonalInformation();
            }
        });


        etProfileHeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newText = s.toString();
                if (!newText.isEmpty()){
                    height = Double.parseDouble(newText);
                    etProfileBMI.setText(String.valueOf(MathMine.findBMI(weight,height)));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etProfileWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newText = s.toString();
                if (!newText.isEmpty()){
                    weight = Double.parseDouble(newText);
                    etProfileBMI.setText(String.valueOf(MathMine.findBMI(weight,height)));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });




    }

    private void setCurrentProfileData(){




        SharedPreferences sharedPreferences = getSharedPreferences(ActiveProfile.Title, Context.MODE_PRIVATE);
        profileName = sharedPreferences.getString(ActiveProfile.NAME, "undefined");
        profileAge = sharedPreferences.getString(ActiveProfile.AGE, "0");
        profileHeight = sharedPreferences.getString(ActiveProfile.HEIGHT, "0");
        profileWeight = sharedPreferences.getString(ActiveProfile.WEIGHT, "0");
        profileId = sharedPreferences.getString(ActiveProfile.PROFILE_ID, "0");
        loginId = sharedPreferences.getString(ActiveProfile.LOGIN_ID, "0");
        profileGender = sharedPreferences.getString(ActiveProfile.GENDER, "male");
        String activeProfileId = sharedPreferences.getString(ActiveProfile.PROFILE_ID, "male");


        // setup profile avatar
        Methods.setToolBar(getApplicationContext(), activeProfileId, profileGender,imgProfileAv);


        // set edit texts
        etProfileName.setText(profileName);
        etProfileAge.setText(profileAge);
        etProfileHeight.setText(profileHeight);
        etProfileWeight.setText(profileWeight);

        // set BMI
         height = Double.parseDouble(profileHeight);
         weight = Double.parseDouble(profileWeight);
        etProfileBMI.setText(String.valueOf(MathMine.findBMI(weight,height)));

        // set Gender
        if (profileGender.equals("Male") || profileGender.equals("male") ){
            rbProfileGenderMale.setChecked(true);
        }else{
            rbProfileGenderFemale.setChecked(true);
        }
    }

    private void upDatePersonalInformation(){

        double finalHeight = Double.parseDouble(etProfileHeight.getText().toString());
        if(isHeightInFeet){
            finalHeight=   MathMine.feetToCM(Double.parseDouble(etProfileHeight.getText().toString()));
        }

        RequestParams params = new RequestParams();
        params.put("login_id", loginId);
        params.put("profile_id", profileId);
        params.put("dob", "profileID");
        params.put("age", etProfileAge.getText().toString());
        params.put("height", finalHeight);
        params.put("weight", etProfileWeight.getText().toString());
        params.put("water_consumption", "0");
        params.put("bmi", etProfileBMI.getText().toString());
        params.put("gender",profileGender);


        AsyncHttpClient client = new AsyncHttpClient();
        client.get(HTTP_URLS.UPDATE_PERSONAL_INFORMATION, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                showLoading();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                dismissLoadingProgress();
                String response = new String(responseBody).trim();
                if (response.equals("Record updated successfully")){
                    Toast.makeText(PersonalInfoEdit.this, "Profile updated", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }else{
                    handleException("Something went wrong");
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dismissLoadingProgress();
                if (statusCode == 404) {
                    // Handle a 404 Not Found error.
                    handleException( "Server not found. Please check your internet connection.");
                } else if (statusCode >= 500) {
                    // Handle server errors.
                    handleException( "Server error. Please try again later.");
                } else if (responseBody != null) { // Check if responseBody is not null
                    // Handle other errors with an error response body.
                    String errorMessage = new String(responseBody);
                    handleException("Error: " + errorMessage);
                } else {
                    // Handle other unknown errors.
                    handleException( "Something went wrong! Please try again.");
                }
            }

        });
    }

    private void handleException(String s) {
        showErrorDialog("An error occurred: " + s);
    }
    private void showErrorDialog(String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage(errorMessage);

        // Set a positive button with a click listener
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
              onBackPressed();
            }
        });

        // Disable outside touch dismissal
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);

        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo1.animateSlideRight(PersonalInfoEdit.this);
    }

    private  void showLoading(  ){
        progressDialog = new ProgressDialog(PersonalInfoEdit.this);
        progressDialog.show();
    }

    private  void dismissLoadingProgress(){
        progressDialog.dismiss();
    }



}