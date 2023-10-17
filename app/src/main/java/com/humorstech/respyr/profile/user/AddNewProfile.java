package com.humorstech.respyr.profile.user;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.humorstech.respyr.Animatoo1;
import com.humorstech.respyr.HTTP_URLS;
import com.humorstech.respyr.KeyboardUtils;
import com.humorstech.respyr.R;
import com.humorstech.respyr.StatusBarColor;
import com.humorstech.respyr.authentication.profile_creation.personal.Gender;
import com.humorstech.respyr.dashboard.MainActivity;
import com.humorstech.respyr.utills.ActiveProfile;
import com.humorstech.respyr.utills.ProfileCreationData;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.techiness.progressdialoglibrary.ProgressDialog;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Objects;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.conn.ConnectTimeoutException;

public class AddNewProfile extends AppCompatActivity {

    private Button buttonNext;
    private ImageButton buttonBack;
    private EditText etName,etEmail;


    private ProgressDialog progressDialog;

    private String activeLoginId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_profile);
        StatusBarColor statusBarColor= new StatusBarColor(AddNewProfile.this);
        statusBarColor.setDarkColor(getResources().getColor(R.color.select_primary));

        init();
    }
    private void init(){
        initVars();
        onClick();
    }
    private void initVars(){


        SharedPreferences sharedPreferences = getSharedPreferences(ActiveProfile.Title, Context.MODE_PRIVATE);
        activeLoginId = sharedPreferences.getString(ActiveProfile.LOGIN_ID, null);


        progressDialog =  new ProgressDialog(AddNewProfile.this);

        buttonNext=findViewById(R.id.button_next);
        buttonBack=findViewById(R.id.button_back);
        etName=findViewById(R.id.et_name);
        etEmail=findViewById(R.id.et_email);

    }


    private void onClick(){
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!etName.getText().toString().isEmpty()){
                    checkName(v,etName.getText().toString(), etEmail.getText().toString());
                }else{
                    showToast(v, "provide all required details.");
                }
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }



    private void checkName(View v, String profileName, String profileEmail) {
        RequestParams params = new RequestParams();
        params.put("email", profileName);
        params.put("login_id", activeLoginId);
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(HTTP_URLS.checkNameExist, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // Handle any actions you want to take when the request starts.

                showLoading();

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                dismissLoadingProgress();
                String response = new String(responseBody).trim();

                response = response.trim();

                if (response.equals("exist")) {
                    // The name already exists, show an error message.
                    showToast(v, profileName + " is already used, please try another name");
                } else {

                    // Name is available, store the login name for profile creation
                    SharedPreferences sharedPreferences = getSharedPreferences(ProfileCreationData.TITLE, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(ProfileCreationData.NAME, profileName);
                    editor.putString(ProfileCreationData.EMAIL, profileEmail);
                    editor.apply();

                    // Proceed to the next step (e.g., Gender selection)
                    Intent intent = new Intent(getApplicationContext(), Gender.class);
                    Animatoo1.animateSlideRight(AddNewProfile.this);
                    startActivity(intent);

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
                // This method is called when the request is retried (e.g., in case of network issues)
                // You can handle retries or display a message to the user as needed.
            }
        });
    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(0,R.anim.top_to_bottom);
        KeyboardUtils.hideKeyboard(AddNewProfile.this);
        finish();
    }

    private void showToast(View v, String message){
        Snackbar.make(v, message, Snackbar.LENGTH_SHORT).show();
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



    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = Objects.requireNonNull(connectivityManager).getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }


    private void showLoading(){
         progressDialog = new ProgressDialog(this);
        progressDialog.show();
    }


    private void dismissLoadingProgress(){
        progressDialog.dismiss();
    }

}