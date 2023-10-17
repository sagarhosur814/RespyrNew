package com.humorstech.respyr;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.humorstech.respyr.authentication.Welcome;
import com.humorstech.respyr.authentication.login.Login;
import com.humorstech.respyr.dashboard.Methods;
import com.humorstech.respyr.preferences.ActiveResultData;
import com.humorstech.respyr.profile.user.AddNewProfile;
import com.humorstech.respyr.utills.ActiveProfile;
import com.humorstech.respyr.utills.DashboardData;
import com.humorstech.respyr.utills.LoginUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.techiness.progressdialoglibrary.ProgressDialog;

import cz.msebera.android.httpclient.Header;

public class DeleteAccount {

    public static Dialog bsDialog;

    private static String userLoginId;
    private static String userPhoneNumber;
    private static ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    public static void show( Activity activity) {
        if (activity == null || activity.isFinishing()) {
            // Handle the case when the activity is not valid or already finishing
            return;
        }

        Context context = activity.getApplicationContext();
        progressDialog =  new ProgressDialog(activity);

        try {
            bsDialog = new Dialog(activity, R.style.TransparentDialog);
            bsDialog.setContentView(R.layout.layout_delete_account);
            bsDialog.getWindow().setNavigationBarColor(ContextCompat.getColor(context, R.color.white));
            bsDialog.show();



            onClick(activity);



            SharedPreferences preferences1 = context.getSharedPreferences(LoginUtils.LOGIN_TITLE, Context.MODE_PRIVATE);
            userLoginId  = preferences1.getString(LoginUtils.LOGIN_ID,null);
            userPhoneNumber  = preferences1.getString(LoginUtils.PHONE_NUMBER,null);

            TextView txtProfileName = bsDialog.findViewById(R.id.txt_delete_box_profile_name);



            txtProfileName.setText( "Delete  account connected to +91 " + userPhoneNumber);

        } catch (WindowManager.BadTokenException e) {
            // Handle the BadTokenException
            e.printStackTrace();
            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
        }







    }

    private static void onClick(Activity activity){

        Button buttonCancel = bsDialog.findViewById(R.id.button_cancel_delete);

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bsDialog.dismiss();

            }
        });

        Button buttonDoNotCancel = bsDialog.findViewById(R.id.button_yes_delete);
        buttonDoNotCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bsDialog.dismiss();
                performDelete(activity);
            }
        });
    }

    public static void performDelete(Activity activity) {



        try {
            RequestParams params = new RequestParams();
            params.put("login_id",userLoginId);
            params.put("status","deactivated");


            AsyncHttpClient client = new AsyncHttpClient();
            client.get(HTTP_URLS.UPDATE_ACCOUNT_STATUS,params,
                    new AsyncHttpResponseHandler() {
                        @Override
                        public void onStart() {
                            showLoading(activity);
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                            String response = new String(responseBody).trim();
                            dismissLoadingProgress();

                            if (response.equals("success")){
                                logout(activity);
                            }else{
                                showToast(response, activity);
                            }

                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                            dismissLoadingProgress();
                            // Check the status code to determine the specific error
                            if (statusCode == 0) {
                               // errorMsg = "No internet connection. Please check your network.";
                               showToast("No internet connection. Please check your network.",activity);
                            } else {
                              //  errorMsg = ";
                                showToast("HTTP request failed with status code: " + statusCode, activity);
                            }

                        }

                    });
        }catch (Exception e){

        }

    }

    private static void logout(Activity activity){
        SharedPreferences preferences = activity.getApplicationContext().getSharedPreferences(LoginUtils.LOGIN_TITLE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        SharedPreferences preferences1 = activity.getApplicationContext().getSharedPreferences(ActiveProfile.Title, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor1 = preferences1.edit();
        editor1.clear();
        editor1.apply();

        Intent intent = new Intent(activity.getApplicationContext(), Login.class);
        activity.startActivity(intent);
        activity.finish();
    }


    private static void showToast(String message, Activity activity){
        Toast.makeText(activity.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


    private static void showLoading(Activity activity ){
        progressDialog = new ProgressDialog(activity);
        progressDialog.show();
    }


    private static void dismissLoadingProgress(){
        progressDialog.dismiss();
    }




}
