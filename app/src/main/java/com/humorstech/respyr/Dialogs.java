package com.humorstech.respyr;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;

public class Dialogs {
    static ProgressDialog progressDialog;
    static AlertDialog dialog;

    public static void showLoadingDialog(Context context, String message) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(message);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }
    public static void hideLoadingDialog() {
        progressDialog.dismiss();
    }



    public static void showCustomDialog(Activity activity) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.TransparentDialog2);
        // Inflate the custom layout
        View customLayout = activity.getLayoutInflater().inflate(R.layout.progress_loading, null);

        builder.setView(customLayout);

        // Create the dialog
        dialog = builder.create();

        // Set an OnShowListener on the dialog to change its dimensions when it is about to be shown
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                // Set custom width and height for the dialog window
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(dialog.getWindow().getAttributes());
                layoutParams.width = 350;  // Set your custom width here
                layoutParams.height = 350;  // Set your custom height here
                dialog.getWindow().setAttributes(layoutParams);
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        // Show the dialog
        dialog.show();
    }



    public static void dismissDialog(){
        dialog.dismiss();
    }
}
