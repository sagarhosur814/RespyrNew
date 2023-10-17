package com.humorstech.respyr.profile.profile_edit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.humorstech.respyr.Animatoo1;
import com.humorstech.respyr.HTTP_URLS;
import com.humorstech.respyr.R;
import com.humorstech.respyr.StatusBarColor;
import com.humorstech.respyr.authentication.profile_creation.lifestyle.LifeStyleScore;
import com.humorstech.respyr.authentication.profile_creation.lifestyle.MentalConditions;
import com.humorstech.respyr.utills.ActiveProfile;
import com.humorstech.respyr.utills.ProfileCreationData;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.techiness.progressdialoglibrary.ProgressDialog;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MentalStateEdit extends AppCompatActivity {

    private CheckBox chkNone, chk1, chk2, chk3, chk4, chk5, chk6, chk7, chk8;

    private ArrayList<String> conditions = new ArrayList<>();

    private Button buttonNext, buttonBack;
    private StringBuffer stringBuffer = new StringBuffer();

    private String mentalConditions;
    private boolean isSelected;

    private  ProgressDialog progressDialog;
    private String loginId, profileId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mental_stateedit);
        StatusBarColor statusBarColor = new StatusBarColor(MentalStateEdit.this);
        statusBarColor.setColor(getResources().getColor(R.color.white));

        init();
    }

    private void init(){
        onClicks();
        initVars();
        setToolBar();
        checkBoxes();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupCurrentData();
    }

    @SuppressLint("SetTextI18n")
    private void setToolBar(){
        TextView txtAppbarTitle = findViewById(R.id.txt_appbar_title);
        txtAppbarTitle.setText("Mental State");
    }


    private void initVars(){
        progressDialog =  new ProgressDialog(MentalStateEdit.this);
        chkNone = findViewById(R.id.check_box_none);
        chk1 = findViewById(R.id.chk1);
        chk2 = findViewById(R.id.chk2);
        chk3 = findViewById(R.id.chk3);
        chk4 = findViewById(R.id.chk4);
        chk5 = findViewById(R.id.chk5);
        chk6 = findViewById(R.id.chk6);
        chk7 = findViewById(R.id.chk7);
        chk8 = findViewById(R.id.chk8);
    }


    private void checkBoxes() {
        chkNone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    chk1.setChecked(false);
                    chk2.setChecked(false);
                    chk3.setChecked(false);
                    chk4.setChecked(false);
                    chk5.setChecked(false);
                    chk6.setChecked(false);
                    chk7.setChecked(false);
                    chk8.setChecked(false);

                    conditions.remove("Anxiety/Stress");
                    conditions.remove("Depression");
                    conditions.remove("Post-Traumatic Stress Disorder (PTSD)");
                    conditions.remove("Schizophrenia");
                    conditions.remove("Eating Disorders");
                    conditions.remove("Disruptive behaviour and dissocial disorders");
                    conditions.remove("Autism spectrum");
                    conditions.remove("Attention deficit hyperactivity disorder (ADHD)");
                    isSelected = true;

                } else {

                    isSelected = false;
                }
            }
        });

        chk1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    chkNone.setChecked(false);
                    conditions.add("Anxiety/Stress");
                    isSelected = true;
                } else {
                    conditions.remove("Anxiety/Stress");
                    isSelected = false;
                }
            }
        });

        chk2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    chkNone.setChecked(false);
                    conditions.add("Depression");
                    isSelected = true;
                } else {
                    conditions.remove("Depression");
                    isSelected = false;
                }
            }
        });
        chk3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    chkNone.setChecked(false);
                    conditions.add("Post-Traumatic Stress Disorder (PTSD)");
                    isSelected = true;
                } else {
                    conditions.remove("Post-Traumatic Stress Disorder (PTSD)");
                    isSelected = false;
                }
            }
        });

        chk4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    chkNone.setChecked(false);
                    conditions.add("Schizophrenia");
                    isSelected = true;
                } else {
                    conditions.remove("Schizophrenia");
                    isSelected = false;
                }
            }
        });

        chk5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    chkNone.setChecked(false);
                    conditions.add("Eating Disorders");
                    isSelected = true;
                } else {


                    conditions.remove("Eating Disorders");
                    isSelected = false;
                }
            }
        });

        chk6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    chkNone.setChecked(false);
                    conditions.add("Disruptive behaviour and dissocial disorders");
                    isSelected = true;

                } else {

                    conditions.remove("Disruptive behaviour and dissocial disorders");
                    isSelected = false;
                }
            }
        });

        chk7.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    chkNone.setChecked(false);
                    conditions.add("Autism spectrum");
                    isSelected = true;
                } else {

                    conditions.remove("Autism spectrum");
                    isSelected = false;
                }
            }
        });

        chk8.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    chkNone.setChecked(false);
                    conditions.add("Attention deficit hyperactivity disorder (ADHD)");
                    isSelected = true;
                } else {
                    conditions.remove("Attention deficit hyperactivity disorder (ADHD)");
                    isSelected = false;
                }
            }
        });


    }



    private void setupCurrentData(){
        SharedPreferences sharedPreferences = getSharedPreferences(ActiveProfile.Title, Context.MODE_PRIVATE);
        loginId = sharedPreferences.getString(ActiveProfile.LOGIN_ID, "undefined");
        profileId = sharedPreferences.getString(ActiveProfile.PROFILE_ID, "undefined");
        mentalConditions = sharedPreferences.getString(ActiveProfile.MENTAL_CONDITIONS, "None");
        String[] conditions = mentalConditions.split(",");
        for (String condition : conditions) {
            setCheckBoxes(condition);
        }

    }

    private void setCheckBoxes(String str){
        switch (str){
            case "None": chkNone.setChecked(true);break;
            case "Anxiety/ Stress": chk1.setChecked(true);break;
            case "Depression": chk2.setChecked(true);break;
            case "Post-Traumatic Stress Disorder (PTSD)": chk3.setChecked(true);break;
            case "Schizophrenia": chk4.setChecked(true);break;
            case "Eating Disorders": chk5.setChecked(true);break;
            case "Disruptive behaviour and dissocial disorders": chk6.setChecked(true);break;
            case "Autism spectrum": chk7.setChecked(true);break;
            case "Attention deficit hyperactivity disorder (ADHD)": chk8.setChecked(true);break;
        }
    }



    private void onClicks(){
        ImageButton buttonBack =findViewById(R.id.button_back);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        Button buttonDataSave =findViewById(R.id.button_data_save);
        buttonDataSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stringBuffer.setLength(0);


                for (int i = 0; i < conditions.size(); i++) {

                    stringBuffer.append(conditions.get(i));
                    if (i != conditions.size() - 1) {
                        stringBuffer.append(",");
                    }
                }

                if (stringBuffer.length() == 0) {
                    mentalConditions = "None";
                } else {
                    mentalConditions = stringBuffer.toString();
                }

                if (isSelected) {

                    // store gender id for profile creation
                     updateMentalConditions(mentalConditions);

                } else {
                    Toast.makeText(MentalStateEdit.this, "Please select any one of the following", Toast.LENGTH_SHORT).show();

                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo1.animateSlideRight(MentalStateEdit.this);
    }


    private void updateMentalConditions(String mentalConditions){
        RequestParams params = new RequestParams();
        params.put("login_id", loginId);
        params.put("profile_id", profileId);
        params.put("mental_state", mentalConditions);



        AsyncHttpClient client = new AsyncHttpClient();
        client.get(HTTP_URLS.UPDATE_MENTAL_CONDITIONS, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                showLoading();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                dismissLoadingProgress();
                String response = new String(responseBody).trim();


                System.out.println(response);
                System.out.println(params);
                System.out.println(HTTP_URLS.UPDATE_MENTAL_CONDITIONS+params);


                if (response.equals("Success")){
                    Toast.makeText(MentalStateEdit.this, "Profile updated", Toast.LENGTH_SHORT).show();
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



    private  void showLoading(  ){
        progressDialog = new ProgressDialog(MentalStateEdit.this);
        progressDialog.show();
    }

    private  void dismissLoadingProgress(){
        progressDialog.dismiss();
    }
}
