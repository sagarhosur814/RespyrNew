package com.humorstech.respyr.profile.profile_edit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.humorstech.respyr.Animatoo1;
import com.humorstech.respyr.HTTP_URLS;
import com.humorstech.respyr.R;
import com.humorstech.respyr.StatusBarColor;
import com.humorstech.respyr.profile.user.ProfileData;
import com.humorstech.respyr.utills.ActiveProfile;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.techiness.progressdialoglibrary.ProgressDialog;

import cz.msebera.android.httpclient.Header;

public class HobbiesEdit extends AppCompatActivity {

    private TextView txtAppbarTitle;
    private Button buttonDataSave;

    private String alcoholValue, smokeValue, eatingValue, exerciseValue;
    private String loginId, profileId;
    private String waterConsume, sleepHours, mentalConditions, lifeStyleScore;


    private RadioGroup rgAlcohol, rgSmoking, rgEat, rgExc;
    private RadioButton rbAlcohol1,rbAlcohol2,rbAlcohol3;
    private RadioButton rbSmoking1,rbSmoking2,rbSmoking3;
    private RadioButton rbExercise1,rbExercise2,rbExercise3,rbExercise4,rbExercise5;
    private RadioButton rbEat1,rbEat2,rbEat3;


    private  ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hobbies_edit);
        StatusBarColor statusBarColor= new StatusBarColor(  HobbiesEdit.this);
        statusBarColor.setColor(getResources().getColor(R.color.white));



        init();
    }
    private void init(){
        initVars();
        onClicks();
        setToolBar();
        setCurrentData();
    }

    @SuppressLint("SetTextI18n")
    private void setToolBar(){
        txtAppbarTitle.setText("Hobbies & Interest");
    }

    private void initVars(){

        progressDialog =  new ProgressDialog(HobbiesEdit.this);

        txtAppbarTitle = findViewById(R.id.txt_appbar_title);
        buttonDataSave = findViewById(R.id.button_data_save);

        rgAlcohol=findViewById(R.id.rg_alcohol);
        rgSmoking=findViewById(R.id.rg_smoking);
        rgEat=findViewById(R.id.rg_eat);
        rgExc=findViewById(R.id.rg_exc);

        rbAlcohol1=findViewById(R.id.rd_al_1);
        rbAlcohol2=findViewById(R.id.rd_al_2);
        rbAlcohol3=findViewById(R.id.rd_al_3);

        rbSmoking1=findViewById(R.id.rb_sm_1);
        rbSmoking2=findViewById(R.id.rb_sm_2);
        rbSmoking3=findViewById(R.id.rb_sm_3);

        rbExercise1=findViewById(R.id.rb_exc_1);
        rbExercise2=findViewById(R.id.rb_exc_2);
        rbExercise3=findViewById(R.id.rb_exc_3);
        rbExercise4=findViewById(R.id.rb_exc_4);
        rbExercise5=findViewById(R.id.rb_exc_5);

        rbEat1=findViewById(R.id.rb_eat_1);
        rbEat2=findViewById(R.id.rb_eat_2);
        rbEat3=findViewById(R.id.rb_eat_3);


    }

    private void onClicks(){
        ImageButton buttonBack =findViewById(R.id.button_back);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        buttonDataSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateHobbiesData();
            }
        });




        rgAlcohol.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rd_al_1:
                        // Handle the selection of the "Male" radio button
                        // You can perform actions specific to the "Male" selection here
                        alcoholValue="Never";
                        break;
                    case R.id.rd_al_2:
                        // Handle the selection of the "Female" radio button
                        // You can perform actions specific to the "Female" selection here
                        alcoholValue="Occasionally";
                        break;
                    case R.id.rd_al_3:
                        // Handle the selection of the "Female" radio button
                        // You can perform actions specific to the "Female" selection here
                        alcoholValue="Regularly";
                        break;
                }
            }
        });

        rgSmoking.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_sm_1:
                        // Handle the selection of the "Male" radio button
                        // You can perform actions specific to the "Male" selection here
                        smokeValue="Never";
                        break;
                    case R.id.rb_sm_2:
                        // Handle the selection of the "Female" radio button
                        // You can perform actions specific to the "Female" selection here
                        smokeValue="Occasionally";
                        break;
                    case R.id.rb_sm_3:
                        // Handle the selection of the "Female" radio button
                        // You can perform actions specific to the "Female" selection here
                        smokeValue="Regularly";
                        break;
                }
            }
        });


        rgEat.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_eat_1:
                        // Handle the selection of the "Male" radio button
                        // You can perform actions specific to the "Male" selection here
                        eatingValue="Never";
                        break;
                    case R.id.rb_eat_2:
                        // Handle the selection of the "Female" radio button
                        // You can perform actions specific to the "Female" selection here
                        eatingValue="Occasionally";
                        break;
                    case R.id.rb_eat_3:
                        // Handle the selection of the "Female" radio button
                        // You can perform actions specific to the "Female" selection here
                        eatingValue="Regularly";
                        break;
                }
            }
        });



        rgExc.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_exc_1:
                        // Handle the selection of the "Male" radio button
                        // You can perform actions specific to the "Male" selection here
                        exerciseValue="Never";
                        break;
                    case R.id.rb_exc_2:
                        // Handle the selection of the "Female" radio button
                        // You can perform actions specific to the "Female" selection here
                        exerciseValue="Lightly";
                        break;
                    case R.id.rb_exc_3:
                        // Handle the selection of the "Female" radio button
                        // You can perform actions specific to the "Female" selection here
                        exerciseValue="Moderately";
                        break;
                    case R.id.rb_exc_4:
                        // Handle the selection of the "Female" radio button
                        // You can perform actions specific to the "Female" selection here
                        exerciseValue="Actively";
                        break;
                    case R.id.rb_exc_5:
                        // Handle the selection of the "Female" radio button
                        // You can perform actions specific to the "Female" selection here
                        exerciseValue="Very Actively";
                        break;
                }
            }
        });
    }


    private void setCurrentData(){
        SharedPreferences sharedPreferences = getSharedPreferences(ActiveProfile.Title, Context.MODE_PRIVATE);
        alcoholValue = sharedPreferences.getString(ActiveProfile.ALCOHOL, "Never");
        smokeValue = sharedPreferences.getString(ActiveProfile.SMOKING, "Never");
        eatingValue = sharedPreferences.getString(ActiveProfile.NOV_VEG, "Never");
        exerciseValue = sharedPreferences.getString(ActiveProfile.EXERCISE, "Never");

        loginId = sharedPreferences.getString(ActiveProfile.LOGIN_ID, "0");
        profileId = sharedPreferences.getString(ActiveProfile.PROFILE_ID, "0");

        waterConsume = sharedPreferences.getString(ActiveProfile.WATER_CONSUMPTION, null);
        sleepHours = sharedPreferences.getString(ActiveProfile.SLEEP_HOURS, null);
        mentalConditions = sharedPreferences.getString(ActiveProfile.MENTAL_CONDITIONS, null);


        setAlcoholRadioButton(alcoholValue);
        setSmokingRadioButtons(smokeValue);
        setEatingRadioButtons(eatingValue);
        setExerciseRadioButtons(exerciseValue);
    }

    private void setAlcoholRadioButton(String alcoholValue){
        switch (alcoholValue){
            case "Never" : rbAlcohol1.setChecked(true);break;
            case "Occasionally" : rbAlcohol2.setChecked(true);break;
            case "Regularly" : rbAlcohol3.setChecked(true);break;
        }
    }

    private void setSmokingRadioButtons(String smokeValue){
        switch (smokeValue){
            case "Never" : rbSmoking1.setChecked(true); break;
            case "Occasionally" : rbSmoking2.setChecked(true);break;
            case "Regularly" : rbSmoking3.setChecked(true);break;
        }
    }

    private void setEatingRadioButtons(String smokeValue){
        switch (smokeValue){
            case "Never" : rbEat1.setChecked(true);break;
            case "Occasionally" : rbEat2.setChecked(true);break;
            case "Regularly" : rbEat3.setChecked(true);break;
        }
    }

    private void setExerciseRadioButtons(String exerciseValue){
        switch (exerciseValue){
            case "Never" : rbExercise1.setChecked(true);break;
            case "Regularly" : rbExercise2.setChecked(true);break;
            case "Moderately" : rbExercise3.setChecked(true);break;
            case "Actively" : rbExercise4.setChecked(true);break;
            case "Very actively" : rbExercise5.setChecked(true);break;
        }
    }



    private void updateHobbiesData(){
        RequestParams params = new RequestParams();
        params.put("login_id", loginId);
        params.put("profile_id", profileId);
        params.put("water_consume", waterConsume);
        params.put("alcoholic", alcoholValue);
        params.put("smoking", smokeValue);
        params.put("non_veg", eatingValue);
        params.put("exercise", exerciseValue);
        params.put("sleep_hours", sleepHours);
        params.put("mental_conditions",mentalConditions);
        params.put("life_stytle_score","0");




        AsyncHttpClient client = new AsyncHttpClient();
        client.get(HTTP_URLS.UPDATE_HOBBIES_DATA, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                showLoading();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                dismissLoadingProgress();
                String response = new String(responseBody).trim();
                if (response.equals("Success")){
                    Toast.makeText(HobbiesEdit.this, "Hobbies & Interest updated", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }else{
                    handleException("Something went wrong " + response);
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo1.animateSlideRight(HobbiesEdit.this);
    }


    private  void showLoading(  ){
        progressDialog = new ProgressDialog(HobbiesEdit.this);
        progressDialog.show();
    }

    private  void dismissLoadingProgress(){
        progressDialog.dismiss();
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
}