package com.humorstech.respyr.reading;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.humorstech.respyr.dashboard.MainActivity;
import com.humorstech.respyr.R;
import com.humorstech.respyr.StatusBarColor;
import com.humorstech.respyr.utills.BloodPressureResults;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.mackhartley.roundedprogressbar.RoundedProgressBar;


public class BlowActivity extends AppCompatActivity {

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case UsbService.ACTION_USB_PERMISSION_GRANTED: // USB PERMISSION GRANTED
                    isConnected = true;
                    highFlag();
                    break;

                case UsbService.ACTION_USB_PERMISSION_NOT_GRANTED: // USB PERMISSION NOT GRANTED
                case UsbService.ACTION_USB_DISCONNECTED: // USB DISCONNECTED
                case UsbService.ACTION_NO_USB: // NO USB CONNECTED
                case UsbService.ACTION_USB_NOT_SUPPORTED: // USB NOT SUPPORTED
                    isConnected = false;
                    lowFlag();
                    break;
            }
        }
    };

    private final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            usbService = ((UsbService.UsbBinder) arg1).getService();
            usbService.setHandler(mHandler);
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            usbService = null;
        }
    };

    private void highFlag(){
        SharedPreferences sharedPreferences = getSharedPreferences("firstTime",MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putBoolean("bflag1", true);
        myEdit.apply();
    }

    private void lowFlag(){
        SharedPreferences sharedPreferences = getSharedPreferences("firstTime",MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putBoolean("bflag1", false);
        myEdit.putBoolean("bflag2", false);
        myEdit.apply();
    }

    // Usb vars
    private UsbService usbService;
    private MyHandler mHandler;

    //// vars
    private Boolean isConnected = false;

    private StringBuffer stringBuffer = new StringBuffer();


    private RelativeLayout layoutTakeDeepBreath,layoutBlowNow;
    private static final String TAG = "BlowActivity";

    private int bFlag2;


    ///  xml references///////////////////////////////////////////////////////
    private TextView txtAutoNext;
    private TextView txtAnalyzeBreath;


    private CircularProgressIndicator circularProgressIndicator1;
    private CircularProgressIndicator circularProgressIndicator2;
    private CircularProgressIndicator circularProgressIndicator3;

    private ImageView imageView1;
    private ImageView imageView2;
    private ImageView imageView3;

    private View view1;
    private View view2;
    private View view3;

    private TextView txtDisplayText;
    private LinearLayout  layoutAnalise,layoutSettingEnvironmentLayout;
    private LinearLayout  layoutGetStarted;
    private TextView settingEnvironmentText, txtTakeDeepBreath;

    private Button buttonGetStarted;


    private CountDownTimer settingUpTimer;
    private CountDownTimer takeDeepBreathTimer;
    private CountDownTimer blowTimer;
    private CountDownTimer analyzeTimer;


    ///////////////////////////////******* BLOW  VARIABLES *******************//////////////////////
    private double BlowBaseValue=0;
    private double BlowThresholdValue=0;
    private double FinalBlowValue=0;
    private boolean isBaseValueCaptured=false;
    private boolean isBlowValueCaptured=false;
    private boolean isBlowStartTimeCaptured=false;
    private boolean isBlowEndTimeCaptured=false;

    private ArrayList<Double> blowValuesList = new ArrayList<Double>();

    private long blowStartTime=0;
    private long blowEndTime=0;
    private long finalBlowTime=0;

    private TextView txtBlowValue;
    ////////////////////////////////////////////////////////////////////////////////////////////////


    private RoundedProgressBar progressBarBlow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blow);
        StatusBarColor statusBarColor= new StatusBarColor(BlowActivity.this);
        statusBarColor.setColor(getResources().getColor(R.color.white));

        mHandler = new MyHandler(this);

        init();

        progressBarBlow =findViewById(R.id.progress_blow);



    }


    private void stepper(){


        circularProgressIndicator1 =findViewById(R.id.analyse_progress1);
        circularProgressIndicator2 =findViewById(R.id.analyse_progress2);
        circularProgressIndicator3 =findViewById(R.id.analyse_progress3);

        imageView1 =findViewById(R.id.analyse_image1);
        imageView2 =findViewById(R.id.analyse_image2);
        imageView3 =findViewById(R.id.analyse_image3);

        view1=findViewById(R.id.analyse_view1);
        view2=findViewById(R.id.analyse_view2);
        view3=findViewById(R.id.analyse_view3);
        txtAnalyzeBreath=findViewById(R.id.txt_analyze_breath);

        setAnalysingProgress(1);

        new CountDownTimer(90000, 1000) {
            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                long taskDuration = 90 / 3; // Duration of each task (in seconds)
                int currentTask = (int)((90 - seconds) / taskDuration + 1); // Current task number



                // Perform actions based on the current task
                switch (currentTask) {
                    case 1:
                        setAnalysingProgress(1);
                        txtAnalyzeBreath.setText("Analyzing your breath");
                        break;
                    case 2:
                        setAnalysingProgress(2);
                        txtAnalyzeBreath.setText("Generating your score");
                        break;
                    case 3:
                        setAnalysingProgress(3);
                        txtAnalyzeBreath.setText("Starting vital test");
                        break;
                }
            }

            public void onFinish() {
                setAnalysingProgress(4);
            }
        }.start();

    }

    private void setAnalysingProgress(int i){
        // 1. Calculating your results
        // 2. Analysing your results
        // 3. Generating your report
        int active = R.drawable.uncheck_progress1;
        int inactive = R.drawable.uncheck_progress;
        int completed = R.drawable.check_progress;

        int colorCompleted = getResources().getColor(R.color.green);
        int colorInComplete = getResources().getColor(R.color.calories_progress_bg_active);


        switch (i){
            case 1 :
                circularProgressIndicator1.setVisibility(View.VISIBLE);
                circularProgressIndicator2.setVisibility(View.GONE);
                circularProgressIndicator3.setVisibility(View.GONE);

                imageView1.setImageResource(active);
                imageView2.setImageResource(inactive);
                imageView3.setImageResource(inactive);

                view1.setBackgroundTintList(ColorStateList.valueOf(colorInComplete));
                view2.setBackgroundTintList(ColorStateList.valueOf(colorInComplete));

                break;

            case 2 :

                circularProgressIndicator2.setVisibility(View.VISIBLE);
                circularProgressIndicator1.setVisibility(View.GONE);
                circularProgressIndicator3.setVisibility(View.GONE);

                imageView1.setImageResource(completed);
                imageView2.setImageResource(active);
                imageView3.setImageResource(inactive);


                view1.setBackgroundTintList(ColorStateList.valueOf(colorCompleted));
                view2.setBackgroundTintList(ColorStateList.valueOf(colorInComplete));

                break;
            case 3 :
                circularProgressIndicator3.setVisibility(View.VISIBLE);
                circularProgressIndicator2.setVisibility(View.GONE);
                circularProgressIndicator1.setVisibility(View.GONE);

                imageView1.setImageResource(completed);
                imageView2.setImageResource(completed);
                imageView3.setImageResource(active);



                view1.setBackgroundTintList(ColorStateList.valueOf(colorCompleted));
                view2.setBackgroundTintList(ColorStateList.valueOf(colorCompleted));

                break;
            case 4 :
                circularProgressIndicator3.setVisibility(View.GONE);
                circularProgressIndicator2.setVisibility(View.GONE);
                circularProgressIndicator1.setVisibility(View.GONE);

                imageView1.setImageResource(completed);
                imageView2.setImageResource(completed);
                imageView3.setImageResource(completed);

                view1.setBackgroundTintList(ColorStateList.valueOf(colorCompleted));
                view2.setBackgroundTintList(ColorStateList.valueOf(colorCompleted));
                view3.setBackgroundTintList(ColorStateList.valueOf(colorCompleted));

                break;
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sh = getSharedPreferences("firstTime", MODE_PRIVATE);
        boolean s1 = sh.getBoolean("bflag1", false);
        boolean s2 = sh.getBoolean("bflag2", false);

        if (s1 && s2){
            bFlag2=0;
        }else if(s1 && !s2){
            bFlag2=1;
        }
    }

    private void init(){
        initVars();
        onClicks();
    }

    private void initVars() {
        txtDisplayText = findViewById(R.id.txtDisplay);
        txtDisplayText.setMovementMethod(new ScrollingMovementMethod());

        txtAutoNext = findViewById(R.id.txt_auto_next);


        layoutTakeDeepBreath = findViewById(R.id.layout_take_deep_breath);
        layoutBlowNow = findViewById(R.id.layout_blow_now);
        layoutAnalise = findViewById(R.id.layout_analysing);
        layoutSettingEnvironmentLayout = findViewById(R.id.setting_environment_layout);
        settingEnvironmentText = findViewById(R.id.setting_environment_textview);


        buttonGetStarted=findViewById(R.id.button_start_test);
        layoutGetStarted=findViewById(R.id.layout_start_test);

        txtTakeDeepBreath=findViewById(R.id.txtTakeDeepBreath);
        txtBlowValue=findViewById(R.id.txtBlowValue);

    }

    private void onClicks(){
        ImageButton buttonCancel=findViewById(R.id.button_cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CancelTest.show(BlowActivity.this, BlowActivity.this);
            }
        });

        buttonGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                buttonGetStarted.setEnabled(false);
                String data = "?";
                settingEnvironment();



                if (usbService != null) {
                    // if UsbService was correctly bound, Send data

                    usbService.write(data.getBytes());

                    layoutGetStarted.setVisibility(View.GONE);
                    layoutSettingEnvironmentLayout.setVisibility(View.VISIBLE);


                }else{
                    handleException("An error occurred while connecting with device. Please and make device is properly connected with mobile");
                }
            }
        });
    }


    private void settingEnvironment(){
        settingUpTimer = new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                // Handle timer tick
                long seconds = millisUntilFinished / 1000;
                long taskDuration = 30 / 3; // Duration of each task (in seconds)
                int currentTask = (int)((30 - seconds) / taskDuration + 1); // Current task number

                // Perform actions based on the current task
                switch (currentTask) {
                    case 1:
                        settingEnvironmentText.setText("Adjusting to environment...");
                        break;
                    case 2:
                        settingEnvironmentText.setText("Setting up test...");
                        break;
                    case 3:
                        settingEnvironmentText.setText("Starting breath test...");
                        break;
                }
            }

            public void onFinish() {
                // Handle timer finish
                settingUpTimer.cancel();
            }
        };

        settingUpTimer.start(); // Start the timer
    }




    private void inhaleStart(){

        if (settingUpTimer != null) {
            settingUpTimer.cancel(); // Cancel the timer if it's running
        }

        layoutSettingEnvironmentLayout.setVisibility(View.GONE);
        layoutTakeDeepBreath.setVisibility(View.VISIBLE);
        autoNext();
    }

    private void blowNowStart(){

        if (takeDeepBreathTimer != null) {
            takeDeepBreathTimer.cancel(); // Cancel the timer if it's running
        }
        layoutTakeDeepBreath.setVisibility(View.GONE);
        layoutBlowNow.setVisibility(View.VISIBLE);
        blowNowTimer();
        blowTimer.start();
    }
    private void analiseStart(){
        layoutBlowNow.setVisibility(View.GONE);
        layoutAnalise.setVisibility(View.VISIBLE);
        stepper();
        blowTimer.cancel();
    }
    private void finialStringProcess(String data){
        stringBuffer.append(data);
    }


    private void finalDo(String data){
        stringBuffer.append(data);
        txtDisplayText.append(String.valueOf(stringBuffer) +"\n");

        //// save device RAW Data
        String completeRawData = String.valueOf(stringBuffer);


        // Calculate the sum of values
        double sum = 0;
        for (double number : blowValuesList) {
            sum += number;
        }

        // Calculate the average
        double BestPR = (double) sum / blowValuesList.size();


        double MaxPR = Collections.max(blowValuesList);


        /// add blow data to raw data
        String finalData = completeRawData.replace("Best_pr", String.valueOf(BestPR));
        String finalData2 = finalData.replace("MAXPR", String.valueOf(MaxPR));
        String finalData3 = finalData2.replace("BDur", String.valueOf(finalBlowTime));



        txtDisplayText.append("Best_pr : " +String.valueOf(BestPR) );
        txtDisplayText.append("MAXPR : " +String.valueOf(MaxPR) );
        txtDisplayText.append("BDur : " +String.valueOf(finalData3) );

        /// save data
        saveDeviceRawData(finalData3);
    }

    private void autoNext() {
        try {

            takeDeepBreathTimer = new CountDownTimer(5000, 1000) {
                public void onTick(long millisUntilFinished) {
                    // Handle timer tick


                    txtAutoNext.setText("Auto Next..." + millisUntilFinished / 1000 + " seconds");
                    // Logic to set the EditText or perform other actions could go

                    long seconds = millisUntilFinished / 1000;
                    long taskDuration = 4 / 2; // Duration of each task (in seconds)
                    int currentTask = (int)((4 - seconds) / taskDuration + 1); // Current task number

                    // Perform actions based on the current task
                    switch (currentTask) {
                        case 1:
                            txtTakeDeepBreath.setText("Take A Deep Breath in...");
                            break;
                        case 2:
                            txtTakeDeepBreath.setText("Hold...");
                            break;
                    }


                }

                public void onFinish() {
                    // Handle timer finish
                    takeDeepBreathTimer.cancel();
                }
            };

            takeDeepBreathTimer.start(); // Start the timer

        } catch (Exception e) {
            // Handle any exceptions that may occur during countdown timer setup
            e.printStackTrace();
            // You can display a Toast message or log the error for debugging.
        }
    }


    private void blowNowTimer() {
        try {
            TextView txtBlowWithin = findViewById(R.id.txt_blow_within);
            blowTimer = new CountDownTimer(12000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    int seconds = (int) millisUntilFinished / 1000;
                    txtBlowWithin.setText(String.valueOf(seconds));
                }

                @Override
                public void onFinish() {
                    //abortReading();
                }
            };
        } catch (Exception e) {
            // Handle any exceptions that may occur during timer setup
            e.printStackTrace();
            // You can display a Toast message or log the error for debugging.
        }
    }


    private void abortReading(){
        String data = "#";
        if (usbService != null) {
            // if UsbService was correctly binded, Send data
            usbService.write(data.getBytes());
        }
        AbortBlow.show(BlowActivity.this, BlowActivity.this);
    }


    @Override
    public void onResume() {
        super.onResume();
        setFilters();
        startService(UsbService.class, usbConnection, null);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mUsbReceiver);
        unbindService(usbConnection);
    }


    private void handleException(Exception e) {
        e.printStackTrace();
        showErrorDialog("An error occurred: " + e.getMessage());
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
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Disable outside touch dismissal
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);

        alertDialog.show();
    }


    private void stopBlow(){
        String data = "/";
        if (usbService != null) {
            // if UsbService was correctly binded, Send data
            usbService.write(data.getBytes());
        }
    }


    private void startService(Class<?> service, ServiceConnection serviceConnection, Bundle extras) {
        if (!UsbService.SERVICE_CONNECTED) {
            Intent startService = new Intent(this, service);
            if (extras != null && !extras.isEmpty()) {
                Set<String> keys = extras.keySet();
                for (String key : keys) {
                    String extra = extras.getString(key);
                    startService.putExtra(key, extra);
                }
            }
            startService(startService);
        }
        Intent bindingIntent = new Intent(this, service);
        bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }
    private void setFilters() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbService.ACTION_USB_PERMISSION_GRANTED);
        filter.addAction(UsbService.ACTION_NO_USB);
        filter.addAction(UsbService.ACTION_USB_DISCONNECTED);
        filter.addAction(UsbService.ACTION_USB_NOT_SUPPORTED);
        filter.addAction(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED);
        registerReceiver(mUsbReceiver, filter);
    }


    private static class MyHandler extends Handler {
        private final WeakReference<BlowActivity> mActivity;

        public MyHandler(BlowActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            BlowActivity activity = mActivity.get();
            if (activity == null) {
                return; // Activity has been destroyed, exit gracefully
            }

            try {
                switch (msg.what) {
                    case UsbService.MESSAGE_FROM_SERIAL_PORT:
                        String data = (String) msg.obj;

                        // Handle different types of messages

                        activity.txtDisplayText.append(data + "\n");

                        if (data.contains("inhale")){
                            activity.inhaleStart();
                        }else if (data.contains("blownow")) {
                            activity.blowNowStart();
                        } else if (data.contains("analize")) {
                            activity.analiseStart();
                        } else if (data.contains("$")) {
                            activity.finialStringProcess(data);
                        } else if (data.contains("*")) {
                            activity.finalDo(data);
                        } else {
                            processBlowData(data, activity);
                        }
                        break;
                }
            } catch (Exception e) {
                Log.e("MyHandler", "An error occurred: " + e.getMessage());
                // Handle the error gracefully or rethrow if necessary.
            }
        }

        private void processBlowData(String data, BlowActivity activity) {
            String baseValuePattern = "/([0-9.]+)/";
            String blowValuePattern = "\\{([0-9.]+)\\}";

            Pattern baseValueRegexPattern = Pattern.compile(baseValuePattern);
            Matcher baseValueMatcher = baseValueRegexPattern.matcher(data);

            Pattern blowValueRegexPattern = Pattern.compile(blowValuePattern);
            Matcher blowValueMatcher = blowValueRegexPattern.matcher(data);

            if (baseValueMatcher.find()) {
                String baseValueStr = baseValueMatcher.group(1);
                activity.BlowBaseValue = Double.parseDouble(baseValueStr);
                activity.BlowThresholdValue = activity.BlowBaseValue + 3;
                activity.isBaseValueCaptured = true;
                activity.txtDisplayText.append("BlowBaseValue----->" + String.valueOf(activity.BlowBaseValue) + "\n");
            } else if (blowValueMatcher.find()) {
                if (activity.isBaseValueCaptured) {
                    String blowValueStr = blowValueMatcher.group(1);
                    double blowValue = Double.parseDouble(blowValueStr);
                    int blowPercentage = (int) ((blowValue / 1000.0) * 100);
                    activity.progressBarBlow.setProgressPercentage(blowPercentage, false);

                    activity.txtDisplayText.append("------------------------------------------------\n");
                    activity.txtDisplayText.append("Base Value " + String.valueOf(activity.BlowBaseValue) + "\n");
                    activity.txtDisplayText.append("Blow Value " + String.valueOf(blowValue) + "\n");
                    activity.txtDisplayText.append("Threshold Value " + String.valueOf(activity.BlowThresholdValue) + "\n");
                    activity.txtDisplayText.append("------------------------------------------------\n");



                    if (blowValue >= activity.BlowThresholdValue) {
                        if (!activity.isBlowValueCaptured) {
                            if (!activity.isBlowStartTimeCaptured) {
                                activity.blowStartTime = System.currentTimeMillis();
                                activity.isBlowStartTimeCaptured = true;
                            }
                            activity.txtDisplayText.append("OK\n");
                            activity.FinalBlowValue = activity.BlowThresholdValue;
                            activity.blowValuesList.add( activity.FinalBlowValue );
                            activity.txtBlowValue.setText(String.valueOf(blowValue));



                        }
                    } else {
                        if (activity.isBlowStartTimeCaptured) {
                            if (!activity.isBlowEndTimeCaptured) {
                                activity.stopBlow();
                                activity.isBlowEndTimeCaptured = true;
                                activity.blowEndTime = System.currentTimeMillis();
                                activity.finalBlowTime = activity.blowEndTime - activity.blowStartTime;

                            }

                            activity.txtDisplayText.append("Final Blow Value " + String.valueOf(activity.FinalBlowValue) + "\n");
                            activity.txtDisplayText.append("Final Blow Time " + String.valueOf(activity.finalBlowTime) + "\n");

                        }
                    }
                }
            }
        }
    }



    public void saveDeviceRawData(String rawData){

        SharedPreferences sharedPreferences = getSharedPreferences(BloodPressureResults.TITLE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(BloodPressureResults.DEVICE_RAW_DATA, String.valueOf(rawData));

        // Commit the changes and check if the data is saved successfully
        boolean isDataSaved = editor.commit();

        if (isDataSaved) {
            // Data saved successfully, move to the next activity
            Intent intent = new Intent(getApplicationContext(), BloodPressure.class);
            startActivity(intent);
            finish();

        } else {
            // Data not saved, handle the retry logic here
            saveDeviceRawData(rawData);
        }
    }

}