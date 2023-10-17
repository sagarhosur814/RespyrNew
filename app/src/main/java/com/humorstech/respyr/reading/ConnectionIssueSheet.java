package com.humorstech.respyr.reading;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.humorstech.respyr.R;
import com.humorstech.respyr.help.ContactSupportSheet;
import com.humorstech.respyr.help.HelpCenter;
import com.humorstech.respyr.results.LockableScrollView;
import com.humorstech.respyr.suggestion.Suggestion;

import java.util.ArrayList;

public class ConnectionIssueSheet {


    private Context context;
    private Activity activity;

    private BottomSheetBehavior<View> bottomSheetBehavior;
    BottomSheetDialog bottomSheetDialog;

    ConnectionIssueSheet(Activity activity){
        this.activity = activity;
        this.context = activity;
    }

    @SuppressLint("InflateParams")
    public void showCancelBottomSheet() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_connection_issue, null);

        bottomSheetDialog = new BottomSheetDialog(activity, R.style.TransparentDialog);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.setCanceledOnTouchOutside(true);


        // Get the screen height
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;

        // Set the peek height to the entire screen height
        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
        bottomSheetBehavior.setPeekHeight(screenHeight);

        bottomSheetDialog.show();

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // Handle state changes if needed
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // Handle slide offset if needed
            }
        });

        Button buttonGoToSettings = bottomSheetDialog.findViewById(R.id.button_go_to_settings);
        buttonGoToSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                activity.startActivity(intent);
            }
        });

        Button buttonContactSupport = bottomSheetDialog.findViewById(R.id.button_contact_spport);
        buttonContactSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactSupportSheet.show(activity);            }
        });
    }

    public void hideBottomSheet(){
        if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
            // Check the state of the bottom sheet
            int currentState = bottomSheetBehavior.getState();
            if (currentState == BottomSheetBehavior.STATE_EXPANDED || currentState == BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetDialog.dismiss();
            }
        }
    }

}
