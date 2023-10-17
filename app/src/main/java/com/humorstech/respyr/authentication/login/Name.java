package com.humorstech.respyr.authentication.login;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.humorstech.respyr.Animatoo1;
import com.humorstech.respyr.Dialogs;
import com.humorstech.respyr.HTTP_URLS;
import com.humorstech.respyr.KeyboardUtils;
import com.humorstech.respyr.NetWork;
import com.humorstech.respyr.NetworkErrorSheet;
import com.humorstech.respyr.R;
import com.humorstech.respyr.StatusBarColor;
import com.humorstech.respyr.authentication.profile_creation.personal.Gender;
import com.humorstech.respyr.profile.user.AddNewProfile;
import com.humorstech.respyr.utills.ProfileCreationData;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.techiness.progressdialoglibrary.ProgressDialog;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.conn.ConnectTimeoutException;

public class Name extends AppCompatActivity {

    private Button buttonNext;
    private EditText etName, etEmail;
    private static final String TAG = "Name";

    private TextView txtEmailError, txtNameError;
    private boolean isEmailValid=false;
    private boolean isNameFill=false;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);
        StatusBarColor statusBarColor= new StatusBarColor(Name.this);
        statusBarColor.setColor(getResources().getColor(R.color.light_secondary));
        init();
    }

    private void init(){
        initVars();
        emailValidation();
        onClick();
    }
    private void initVars(){

        progressDialog =  new ProgressDialog(Name.this);


        buttonNext=findViewById(R.id.button_next);
        etName=findViewById(R.id.et_name);
        etEmail=findViewById(R.id.et_email);
        txtEmailError=findViewById(R.id.txt_email_error);
        txtNameError=findViewById(R.id.txt_name_error);
    }

    private void emailValidation(){
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (etEmail.getText().toString().isEmpty()){
                    txtEmailError.setText("Please enter email address");
                    isEmailValid=false;
                }else{
                    txtEmailError.setText("");
                    if (Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString()).matches()){
                        txtEmailError.setText("");
                        isEmailValid=true;
                    }else{
                        txtEmailError.setText("Please enter valid email address");
                        isEmailValid=false;
                    }
                }

            }
        });

        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (etName.getText().toString().isEmpty()){
                    txtNameError.setText("Please enter name");
                    isNameFill=false;
                }else{
                    txtNameError.setText("");
                    isNameFill=true;
                }
            }
        });


        // Set an InputFilter to restrict special characters
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    char character = source.charAt(i);
                    // Allow only alphanumeric characters and space
                    if (!Character.isLetterOrDigit(character) && !Character.isWhitespace(character)) {
                        txtNameError.setText("Special characters are not allowed");
                        isNameFill=false;
                        return "";
                    }else{
                        isNameFill=true;
                    }
                }
                return null;
            }
        };

        etName.setFilters(new InputFilter[]{filter});
    }
    private void onClick(){

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (NetWork.isNetworkAvailable(Name.this)){
//
//                }else{
//                    NetworkErrorSheet.show(Name.this, Name.this, Name.class);
//                }

                if (isNameFill && isEmailValid){
                    checkEmail(etEmail.getText().toString());
                }
            }
        });
    }

    private void checkEmail(String email) {
        RequestParams params = new RequestParams();
        params.put("email", email);
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(HTTP_URLS.checkEmailExist, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
                showLoading();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    dismissLoadingProgress();
                    String response = new String(responseBody).trim();
                    if ("exist".equals(response)) {
                        txtEmailError.setText("Entered email address is already exist");
                    } else {
                        txtEmailError.setText("");

                        // store login name for profile creation
                        SharedPreferences sharedPreferences = getSharedPreferences(ProfileCreationData.TITLE, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(ProfileCreationData.NAME, etName.getText().toString());
                        editor.putString(ProfileCreationData.EMAIL, etEmail.getText().toString());
                        editor.apply();

                        Intent intent = new Intent(getApplicationContext(), Gender.class);
                        startActivity(intent);
                    }
                } catch (Exception e) {


                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                // Handle various types of exceptions and errors
                dismissLoadingProgress();


                if (error instanceof ConnectTimeoutException || error instanceof SocketTimeoutException) {
                    // Handle timeout error (e.g., request took too long)
                    // You can display a user-friendly message

                    handleException("Request timed out. Please check your internet connection and try again.");

                } else if (error instanceof UnknownHostException) {
                    // Handle unknown host exception (e.g., no internet connectivity)

                    handleException("No internet connection. Please check your network settings.");

                } else if (statusCode >= 400 && statusCode < 500) {
                    // Handle client-side error (e.g., 404 Not Found)
                    // You can display an appropriate message
                    handleException("Client error: " + statusCode);
                } else if (statusCode >= 500) {
                    // Handle server-side error (e.g., 500 Internal Server Error)
                    // You can display an appropriate message
                    handleException("Server error: " + statusCode);
                } else {
                    // Handle other types of errors (e.g., network error)
                    // You can display a generic error message
                    handleException("Something went wrong! Please check your internet connection or try again later.");
                }

            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
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
                // Perform an action when the "OK" button is clicked
                // For example, you can close the dialog or perform another task.
                recreate();
            }
        });

        // Disable outside touch dismissal
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);

        alertDialog.show();
    }


    @Override
    public void onBackPressed() {
        KeyboardUtils.hideKeyboard(Name.this);
        Animatoo1.animateSlideLeft(Name.this);
        finish();
    }



    private void showLoading(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Verifying...");
        progressDialog.show();
    }

    private void dismissLoadingProgress(){
        progressDialog.dismiss();
    }

}