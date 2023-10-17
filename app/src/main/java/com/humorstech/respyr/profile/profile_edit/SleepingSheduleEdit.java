package com.humorstech.respyr.profile.profile_edit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.humorstech.respyr.Animatoo1;
import com.humorstech.respyr.HTTP_URLS;
import com.humorstech.respyr.R;
import com.humorstech.respyr.StatusBarColor;
import com.humorstech.respyr.utills.ActiveProfile;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.techiness.progressdialoglibrary.ProgressDialog;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import cz.msebera.android.httpclient.Header;

public class SleepingSheduleEdit extends AppCompatActivity {

    private TextView txtAppbarTitle;
    private Button buttonDataSave;


    private EditText etSleepHours;

    private String loginId, profileId;
    private String sleepHours;

    private  ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleeping_shedule_edit);

        StatusBarColor statusBarColor= new StatusBarColor(  SleepingSheduleEdit.this);
        statusBarColor.setColor(getResources().getColor(R.color.white));

        init();
    }

    private void init(){
        initVars();
        onClicks();
        setToolBar();
        setCurrentSleepHours();
    }

    private void initVars(){

        progressDialog =  new ProgressDialog(SleepingSheduleEdit.this);

        txtAppbarTitle = findViewById(R.id.txt_appbar_title);
        buttonDataSave = findViewById(R.id.button_data_save);
        etSleepHours = findViewById(R.id.et_hours);


    }

    private void setToolBar(){
        txtAppbarTitle.setText("Sleeping Schedule");
    }
    private void onClicks(){
        ImageButton buttonBack =findViewById(R.id.button_back);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Button buttonDataSave=findViewById(R.id.button_data_save);
        buttonDataSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSleepSchedule();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo1.animateSlideRight(SleepingSheduleEdit.this);
    }

    private void setCurrentSleepHours(){
        SharedPreferences sharedPreferences = getSharedPreferences(ActiveProfile.Title, Context.MODE_PRIVATE);
        sleepHours = sharedPreferences.getString(ActiveProfile.SLEEP_HOURS, "0");
        loginId = sharedPreferences.getString(ActiveProfile.LOGIN_ID, "0");
        profileId = sharedPreferences.getString(ActiveProfile.PROFILE_ID, "0");


        IndicatorSeekBar sleepSeekBar = findViewById(R.id.sleep_hour_bar);
        sleepSeekBar.setProgress(Integer.parseInt(sleepHours));
        etSleepHours.setText(String.valueOf(sleepHours));
        sleepSeekBar.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
                etSleepHours.setText(String.valueOf(seekParams.progress));
                sleepHours=String.valueOf(seekParams.progress);
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {

            }
        });
    }


    private void updateSleepSchedule(){
        RequestParams params = new RequestParams();
        params.put("login_id", loginId);
        params.put("profile_id", profileId);
        params.put("sleep_hours", etSleepHours.getText().toString());




        AsyncHttpClient client = new AsyncHttpClient();
        client.get(HTTP_URLS.UPDATE_SLEEP_ROUTINE, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                showLoading();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                dismissLoadingProgress();
                String response = new String(responseBody).trim();
                if (response.equals("Success")){
                    Toast.makeText(SleepingSheduleEdit.this, "Sleeping Schedule Updated", Toast.LENGTH_SHORT).show();
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



    private  void showLoading(  ){
        progressDialog = new ProgressDialog(SleepingSheduleEdit.this);
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