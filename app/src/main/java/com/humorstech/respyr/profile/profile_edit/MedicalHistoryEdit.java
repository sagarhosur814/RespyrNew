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
import android.widget.CheckBox;
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

import cz.msebera.android.httpclient.Header;

public class MedicalHistoryEdit extends AppCompatActivity {

    private CheckBox chkNone, chkDia,chkLipid, chkKidney, chkLiver, chkLungs;
    private boolean ch1,ch2,ch3,ch4,ch5;
    private final StringBuilder stringBuilder = new StringBuilder();

    private String loginId, profileId;
    private  ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_history_edit);

        StatusBarColor statusBarColor = new StatusBarColor(MedicalHistoryEdit.this);
        statusBarColor.setColor(getResources().getColor(R.color.white));



    }

    @Override
    protected void onStart() {
        super.onStart();
        initVars();
        setCurrentData();
        init();
    }

    private void init(){
        setToolBar();
        checkBoxListener();
        onClick();
    }
    private void setCurrentData(){
        SharedPreferences sharedPreferences = getSharedPreferences(ActiveProfile.Title, Context.MODE_PRIVATE);
        loginId = sharedPreferences.getString(ActiveProfile.LOGIN_ID, "undefined");
        profileId = sharedPreferences.getString(ActiveProfile.PROFILE_ID, "undefined");
        String medicalCondition = sharedPreferences.getString(ActiveProfile.MEDICAL_CONDITION, "None");


        String[] conditions = medicalCondition.split(",");
        for (String condition : conditions) {
           if(condition!=""){
               setCheckBoxes(condition);
           }
        }
    }

    private void setCheckBoxes(String str) {

        if (str.contains("None")) {
            chkNone.setChecked(true);
        } else if (str.contains("Diabetes")) {
            chkDia.setChecked(true);
        } else if (str.contains("Lungs")) {
            chkLungs.setChecked(true);
        } else if (str.contains("Lipid")) {
            chkLipid.setChecked(true);
        } else if (str.contains("Kidney")) {
            chkKidney.setChecked(true);
        } else if (str.contains("Liver")) {
            chkLiver.setChecked(true);
        }

    }

    @SuppressLint("SetTextI18n")
    private void setToolBar(){
        TextView txtAppbarTitle = findViewById(R.id.txt_appbar_title);
        txtAppbarTitle.setText("Mental State");
    }

    private void initVars(){

        progressDialog =  new ProgressDialog(MedicalHistoryEdit.this);

        // checkboxes
        chkNone=findViewById(R.id.chk_none);
        chkDia=findViewById(R.id.chk_dia);
        chkLipid=findViewById(R.id.chk_lungs);
        chkKidney=findViewById(R.id.chk_lipid);
        chkLiver=findViewById(R.id.chk_kidney);
        chkLungs=findViewById(R.id.chk_liver);

    }
    private void onClick(){
        ImageButton buttonBack =findViewById(R.id.button_back);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Button buttonSaveData =findViewById(R.id.button_data_save);
        buttonSaveData.setOnClickListener(v -> {
            stringBuilder.setLength(0);

            if (ch1){
                stringBuilder.append("Diabetes ,");
            }
            if (ch2){
                stringBuilder.append("Lungs Issue ,");
            }
            if (ch3){
                stringBuilder.append("Lipid Issue ,");
            }

            if (ch4){
                stringBuilder.append("Kidney Issue ,");
            }

            if (ch5){
                stringBuilder.append("Liver Issue");
            }

            updateMedicalHistoryData();



        });
    }

    private void checkBoxListener(){

        chkNone.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked){
                chkDia.setChecked(false);
                chkLipid.setChecked(false);
                chkKidney.setChecked(false);
                chkLiver.setChecked(false);
                chkLungs.setChecked(false);

                stringBuilder.setLength(0);
            }else{
                ch1=false;
                ch2=false;
                ch3=false;
                ch4=false;
                ch5=false;
            }

        });


        chkDia.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                chkNone.setChecked(false);
                ch1=true;
            }else{
                ch1=false;
            }
        });

        chkLipid.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                chkNone.setChecked(false);
                ch2=true;
            }else{
                ch2=false;
            }
        });
        chkKidney.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                chkNone.setChecked(false);
                ch3=true;
            }else{
                ch3=false;
            }
        });
        chkLiver.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                chkNone.setChecked(false);
                ch4=true;
            }else{
                ch4=false;
            }
        });
        chkLungs.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                chkNone.setChecked(false);
                ch5=true;
            }else{
                ch5=false;
            }
        });

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo1.animateSlideRight(MedicalHistoryEdit.this);
    }

    private void updateMedicalHistoryData(){
        RequestParams params = new RequestParams();
        params.put("login_id", loginId);
        params.put("profile_id", profileId);
        params.put("conditions", String.valueOf(stringBuilder));





        AsyncHttpClient client = new AsyncHttpClient();
        client.get(HTTP_URLS.UPDATE_MEDICAL_CONDITIONS, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                showLoading();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                dismissLoadingProgress();
                String response = new String(responseBody).trim();
                if (response.equals("Success")){
                    Toast.makeText(MedicalHistoryEdit.this, "Hobbies & Interest updated", Toast.LENGTH_SHORT).show();
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
        progressDialog = new ProgressDialog(MedicalHistoryEdit.this);
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