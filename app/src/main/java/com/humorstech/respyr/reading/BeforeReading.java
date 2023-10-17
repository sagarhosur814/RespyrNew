package com.humorstech.respyr.reading;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.humorstech.respyr.Animatoo1;
import com.humorstech.respyr.R;
import com.humorstech.respyr.StatusBarColor;
import com.humorstech.respyr.daily_routine.ConfirmReading;
import com.humorstech.respyr.dashboard.MainActivity;
import com.humorstech.respyr.utills.ReadingParameters;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Set;

import cz.msebera.android.httpclient.Header;

public class BeforeReading extends AppCompatActivity {

    private UsbService usbService;
    private MyHandler mHandler;
    private Toast usbConnectedToast; // Toast to display when USB device is connected
    private Toast usbDisconnectedToast; // Toast to display when USB device is disconnected


    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case UsbService.ACTION_USB_PERMISSION_GRANTED:
                    highFlag();
                    storeTime();
                    // Show a Toast when USB device is connected
                    showUsbConnectedToast();
                    break;
                case UsbService.ACTION_USB_PERMISSION_NOT_GRANTED:
                case UsbService.ACTION_USB_DISCONNECTED:
                    lowFlag();
                    // Show a Toast when USB device is disconnected
                    showUsbDisconnectedToast();
                    break;
                case UsbService.ACTION_NO_USB:
                case UsbService.ACTION_USB_NOT_SUPPORTED:
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


    ////////////////////// USB DEVICE IS CONNECTED
    private void showUsbConnectedToast() {
        if (usbConnectedToast != null) {
            usbConnectedToast.cancel();
        }
        usbConnectedToast = Toast.makeText(this, "Respyr is Connected", Toast.LENGTH_SHORT);
        usbConnectedToast.show();
        txtConnectionStatus.setText("All set... Let’s go!");
        txtConnectionStatus2.setText("Connect your respyr device to phone with the cable provided");
        imgConnectionStatus.setImageResource(R.drawable.device_connected_);

        int colorActive = ContextCompat.getColor(this, R.color.primary);
        ColorStateList colorStateList = ColorStateList.valueOf(colorActive);
        buttonNext.setBackgroundTintList(colorStateList);
        buttonNext.setTextColor(getResources().getColor(R.color.white));

        deviceConnected=true;

        fetchLastReadingTime();

      //  connectionIssueSheet.hideBottomSheet();

    }

    //////////// USB DEVICE IS DISCONNECTED
    private void showUsbDisconnectedToast() {
        if (usbDisconnectedToast != null) {
            usbDisconnectedToast.cancel(); // Cancel any existing toasts
        }
        usbDisconnectedToast = Toast.makeText(this, "Respyr is Disconnected", Toast.LENGTH_SHORT);
        usbDisconnectedToast.show();
        txtConnectionStatus.setText("Device Disconnected");
        txtConnectionStatus2.setText("Connect your respyr device to phone with the cable provided");
        imgConnectionStatus.setImageResource(R.drawable.device_not_connected);

        int colorDisabled = ContextCompat.getColor(this, R.color.disabled);
        ColorStateList colorStateList = ColorStateList.valueOf(colorDisabled);
        buttonNext.setBackgroundTintList(colorStateList);
        buttonNext.setTextColor(getResources().getColor(R.color.darK_grey));
        deviceConnected=false;
    }


    private void highFlag(){
        SharedPreferences sharedPreferences = getSharedPreferences("firstTime",MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putBoolean("bflag1", true);
        myEdit.apply();
    }
    private void storeTime(){

    }

    private void lowFlag(){
        SharedPreferences sharedPreferences = getSharedPreferences("firstTime",MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putBoolean("bflag1", false);
        myEdit.putBoolean("bflag2", false);
        myEdit.apply();
    }



    private boolean deviceConnected = false;
    private ImageView imgConnectionStatus;
    private TextView txtConnectionStatus;
    private TextView txtConnectionStatus2;
    private Button buttonNext;
    private LinearLayout llDeviceConnected;
    private LinearLayout  llInsertCap;
    private TextView  txtLoading;
    private CountDownTimer timer;
    private NumberProgressBar numberProgressBar;

    private ConnectionIssueSheet connectionIssueSheet = new ConnectionIssueSheet(BeforeReading.this);

    String loginId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_before_reading);
        StatusBarColor statusBarColor= new StatusBarColor(BeforeReading.this);
        statusBarColor.setColor(getResources().getColor(R.color.white));
        mHandler = new MyHandler(this);
        init();




        TextView txtLog =findViewById(R.id.txt_log);

        SharedPreferences sharedPreferences = getSharedPreferences(ReadingParameters.TITLE, Context.MODE_PRIVATE);
        txtLog.append("Water Intake: " + sharedPreferences.getString(ReadingParameters.WATER_INTAKE, "") + "\n");
        txtLog.append("Cigarettes Count: " + sharedPreferences.getString(ReadingParameters.SMOKING_UNITS, "") + "\n");
        txtLog.append("Alcohol Consumption: " + sharedPreferences.getString(ReadingParameters.IS_TAKEN_ALCOHOL, "") + "\n");
        txtLog.append("Sleep Hours: " + sharedPreferences.getString(ReadingParameters.SLEEP_HOURS, "") + "\n");
        txtLog.append("Exercise Count: " + sharedPreferences.getString(ReadingParameters.EXERCISE_IN_MINUTES, "") + "\n");
        txtLog.append("Food Name: " + sharedPreferences.getString(ReadingParameters.FOOD_NAME, "") + "\n");
        txtLog.append("Food Quantity: " + sharedPreferences.getString(ReadingParameters.FOOD_QUANTITY, "") + "\n");
        txtLog.append("Food Count: " + sharedPreferences.getString(ReadingParameters.FOOD_COUNT, "") + "\n");
        txtLog.append("Had Breakfast: " + sharedPreferences.getString(ReadingParameters.IS_HAD_BREAKFAST, "") + "\n");
        txtLog.append("Had Lunch: " + sharedPreferences.getString(ReadingParameters.IS_HAD_LUNCH, "") + "\n");
        txtLog.append("Had Dinner: " + sharedPreferences.getString(ReadingParameters.IS_HAD_DINNER, "") + "\n");
        txtLog.append("Name: " + sharedPreferences.getString(ReadingParameters.NAME, "") + "\n");
        txtLog.append("Gender: " + sharedPreferences.getString(ReadingParameters.GENDER, "") + "\n");
        txtLog.append("Age: " + sharedPreferences.getString(ReadingParameters.AGE, "") + "\n");
        txtLog.append("Height: " + sharedPreferences.getString(ReadingParameters.HEIGHT, "") + "\n");
        txtLog.append("Weight: " + sharedPreferences.getString(ReadingParameters.WEIGHT, "") + "\n");

        loginId = sharedPreferences.getString(ReadingParameters.LOGIN_ID, "");

    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent usbServiceIntent = new Intent(this, UsbService.class);
        bindService(usbServiceIntent, usbConnection, BIND_AUTO_CREATE);


        if (timer!=null){
            timer.cancel();
        }

        if (usbService.CONNECTION_STATUS){
            txtConnectionStatus.setText("All set... Let’s go!");
            txtConnectionStatus2.setText("Connect your respyr device to phone with the cable provided");
            imgConnectionStatus.setImageResource(R.drawable.device_connected_);

            int colorActive = ContextCompat.getColor(this, R.color.primary);
            ColorStateList colorStateList = ColorStateList.valueOf(colorActive);
            buttonNext.setBackgroundTintList(colorStateList);
            buttonNext.setTextColor(getResources().getColor(R.color.white));




        }else if(!usbService.CONNECTION_STATUS){

            txtConnectionStatus.setText("Device Disconnected");
            txtConnectionStatus2.setText("Connect your respyr device to phone with the cable provided");
            imgConnectionStatus.setImageResource(R.drawable.device_not_connected);

            int colorDisabled = ContextCompat.getColor(this, R.color.disabled);
            ColorStateList colorStateList = ColorStateList.valueOf(colorDisabled);
            buttonNext.setBackgroundTintList(colorStateList);
            buttonNext.setTextColor(getResources().getColor(R.color.darK_grey));
        }

    }
    @SuppressLint("ClickableViewAccessibility")
    private void init(){
        initVars();
        onClicks();
        playVideo();
    }

    private void playVideo(){
        VideoView videoView = findViewById(R.id.videoView);
        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.place_cap));
        videoView.start();
        videoView.setOnPreparedListener(mp -> {
            // Autoplay
            videoView.start();
            // Loop
            videoView.setOnCompletionListener(MediaPlayer::start);
        });

        videoView.setOnTouchListener((View v, @SuppressLint("ClickableViewAccessibility") MotionEvent event) -> true);
        videoView.setMediaController(null);
    }

    private void initVars(){

        buttonNext=findViewById(R.id.button_next);


        imgConnectionStatus=findViewById(R.id.img_connection_status);
        txtConnectionStatus=findViewById(R.id.txt_connection_status);
        txtConnectionStatus2=findViewById(R.id.txt_connection_sub_status);
        numberProgressBar=findViewById(R.id.number_progress);


        llDeviceConnected =findViewById(R.id.device_connected_layout);
        llInsertCap =findViewById(R.id.insert_cap_layout);
        llDeviceConnected.setVisibility(View.VISIBLE);
        llInsertCap.setVisibility(View.GONE);

    }


    private void onClicks(){
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (usbService.CONNECTION_STATUS){
                    startLoading();
                }else{
                    connectionIssueSheet.showCancelBottomSheet();
                }


            }
        });

        ImageButton buttonCancel=findViewById(R.id.button_cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CancelTest.show(BeforeReading.this, BeforeReading.this);
            }
        });


        Button buttonIssueWithConnection=findViewById(R.id.button_issue_with_connection);
        buttonIssueWithConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectionIssueSheet.showCancelBottomSheet();
            }
        });
    }
    private void startLoading(){

        llDeviceConnected.setVisibility(View.GONE);
        llInsertCap.setVisibility(View.VISIBLE);

        long duration = 10000; // 10 seconds in milliseconds
        long interval = 1; // Update interval in milliseconds

        numberProgressBar.setMax(100);

        timer = new CountDownTimer(duration, interval) {
            @Override
            public void onTick(long millisUntilFinished) {
                int secondsRemaining = (int) (millisUntilFinished / 1000);
                int percentage = (int) ((secondsRemaining / 10.0) * 100);
                int finalPercentage = 100 - percentage;

                numberProgressBar.setProgress(finalPercentage);
            }

            @Override
            public void onFinish() {
                moveToNext();
            }
        };

        timer.start();


    }
    private void moveToNext(){
        Intent intent = new Intent(getApplicationContext(), CheckList.class);
        startActivity(intent);
        finish();
    }





    @Override
    public void onResume() {
        super.onResume();
        // Start listening notifications from UsbService
        setFilters();
        // Start UsbService(if it was not started before) and Bind it
        startService(UsbService.class, usbConnection, null);

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
        private final WeakReference<BeforeReading> mActivity;

        public MyHandler(BeforeReading activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == UsbService.MESSAGE_FROM_SERIAL_PORT) {
                String data = (String) msg.obj;
            }
        }

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
        Animatoo1.animateSlideRight(BeforeReading.this);
    }


    private void fetchLastReadingTime() {

        AsyncHttpClient client = new AsyncHttpClient();
        String url = "https://humorstech.com/humors_app/app_final/fetch_last_data_time.php?login_id=" + loginId;

        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject jsonObject = new JSONObject(new String(responseBody));
                    int tstamp = jsonObject.getInt("tstamp");
                    long differenceInMinutes = (System.currentTimeMillis() / 1000 - tstamp) / 60;

                    Toast.makeText(BeforeReading.this, String.valueOf(differenceInMinutes), Toast.LENGTH_SHORT).show();

                    SharedPreferences.Editor editor = getSharedPreferences("CALIBRATION_TIME_DATA", MODE_PRIVATE).edit();
                    if (differenceInMinutes <= 60) {
                        editor.putInt("CALIBRATION_TIME", 121);
                        sendOpenBracket();
                    } else {
                        editor.putInt("CALIBRATION_TIME", 182);
                    }
                    editor.apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // Handle failure if needed
            }
        });
    }



    private void sendOpenBracket(){

        String data = "{";
        if (usbService != null) {
            usbService.write(data.getBytes());
        }
    }
}