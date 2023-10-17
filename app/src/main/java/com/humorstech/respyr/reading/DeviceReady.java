package com.humorstech.respyr.reading;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.humorstech.respyr.R;
import com.humorstech.respyr.StatusBarColor;

import java.lang.ref.WeakReference;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Set;

import pl.droidsonroids.gif.GifImageView;

public class DeviceReady extends AppCompatActivity {


    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case UsbService.ACTION_USB_PERMISSION_GRANTED: // USB PERMISSION GRANTED
                    connectionStatus = true;
                    highFlag();
                    break;

                case UsbService.ACTION_USB_PERMISSION_NOT_GRANTED: // USB PERMISSION NOT GRANTED
                case UsbService.ACTION_USB_DISCONNECTED: // USB DISCONNECTED
                case UsbService.ACTION_NO_USB: // NO USB CONNECTED
                case UsbService.ACTION_USB_NOT_SUPPORTED: // USB NOT SUPPORTED
                    connectionStatus = false;
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
        myEdit.commit();
    }

    private void lowFlag(){
        SharedPreferences sharedPreferences = getSharedPreferences("firstTime",MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putBoolean("bflag1", false);
        myEdit.putBoolean("bflag2", false);
        myEdit.commit();
    }




    //    usb
    private UsbService usbService;
    private MyHandler mHandler;
    private  boolean connectionStatus, isDeviceReady;

    private TextView txtLog;
    private TextView txtDeviceStatus;
    private GifImageView imgDeviceStatus;



    private CircularProgressIndicator circularProgressIndicator1;
    private CircularProgressIndicator circularProgressIndicator2;
    private CircularProgressIndicator circularProgressIndicator3;
    private CircularProgressIndicator circularProgressIndicator4;
    private CircularProgressIndicator circularProgressIndicator5;

    private ImageView imageView1;
    private ImageView imageView2;
    private ImageView imageView3;
    private ImageView imageView4;
    private ImageView imageView5;

    private View view1;
    private View view2;
    private View view3;
    private View view4;

    private  boolean isImageSet1 = false;
    private  boolean isImageSet2 = false;
    private  boolean isImageSet3= false;
    private  boolean isImageSet4 = false;
    private  boolean isImageSet5 = false;

    private  int calibrationTime;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_ready);
        StatusBarColor statusBarColor= new StatusBarColor(DeviceReady.this);
        statusBarColor.setColor(getResources().getColor(R.color.white));


        SharedPreferences preferences = getSharedPreferences("CALIBRATION_TIME_DATA", MODE_PRIVATE);
        calibrationTime = preferences.getInt("CALIBRATION_TIME", 182);


        Toast.makeText(DeviceReady.this, String.valueOf(calibrationTime), Toast.LENGTH_SHORT).show();


        mHandler = new MyHandler(this);
        initVars();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String data = "%";
                if (usbService != null) {
                    // if UsbService was correctly binded, Send data
                    usbService.write(data.getBytes());
                }
            }
        },500);

    }
    private void moveActivity(){
       try {
           Intent intent = new Intent(getApplicationContext(), BlowActivity.class);
           startActivity(intent);
           finish();
       }catch (Exception e){
           txtDeviceStatus.append(e.getMessage() + "\n");
       }
    }

    private void initVars(){
        txtLog=findViewById(R.id.txt_log);
        txtDeviceStatus=findViewById(R.id.txt_device_status);
        imgDeviceStatus=findViewById(R.id.img_image_status);
        txtLog.setMovementMethod(new ScrollingMovementMethod());

        circularProgressIndicator1 =findViewById(R.id.analyse_progress1);
        circularProgressIndicator2 =findViewById(R.id.analyse_progress2);
        circularProgressIndicator3 =findViewById(R.id.analyse_progress3);
        circularProgressIndicator4 =findViewById(R.id.analyse_progress4);
        circularProgressIndicator5 =findViewById(R.id.analyse_progress5);

        imageView1 =findViewById(R.id.analyse_image1);
        imageView2 =findViewById(R.id.analyse_image2);
        imageView3 =findViewById(R.id.analyse_image3);
        imageView4 =findViewById(R.id.analyse_image4);
        imageView5 =findViewById(R.id.analyse_image5);

        view1=findViewById(R.id.analyse_view1);
        view2=findViewById(R.id.analyse_view2);
        view3=findViewById(R.id.analyse_view3);
        view4=findViewById(R.id.analyse_view4);

        setAnalysingProgress(1);



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



        // Assuming your ImageView has the ID "imageView" in your XML layout
        int step1   =     R.drawable.cal0;
        int step2   =     R.drawable.cal1;
        int step3   =     R.drawable.cal2;
        int step4   =     R.drawable.cal3;
        int step5   =     R.drawable.cal4;


        switch (i){
            case 1 :

                /// set resource
                txtDeviceStatus.setText("Cleaning Inner\nChamber of Device");

                if (!isImageSet1){
                    imgDeviceStatus.setImageResource(step1);
                    isImageSet1=true;
                }



                circularProgressIndicator1.setVisibility(View.VISIBLE);
                circularProgressIndicator2.setVisibility(View.GONE);
                circularProgressIndicator3.setVisibility(View.GONE);
                circularProgressIndicator4.setVisibility(View.GONE);
                circularProgressIndicator5.setVisibility(View.GONE);

                imageView1.setImageResource(active);
                imageView2.setImageResource(inactive);
                imageView3.setImageResource(inactive);
                imageView4.setImageResource(inactive);
                imageView5.setImageResource(inactive);

                view1.setBackgroundTintList(ColorStateList.valueOf(colorInComplete));
                view2.setBackgroundTintList(ColorStateList.valueOf(colorInComplete));
                view3.setBackgroundTintList(ColorStateList.valueOf(colorInComplete));
                view4.setBackgroundTintList(ColorStateList.valueOf(colorInComplete));

                break;

            case 2 :


                /// set resource
                txtDeviceStatus.setText("Verifying\nCleanliness");
                if (!isImageSet2){
                    imgDeviceStatus.setImageResource(step2);
                    isImageSet2=true;
                }


                circularProgressIndicator2.setVisibility(View.VISIBLE);
                circularProgressIndicator1.setVisibility(View.GONE);
                circularProgressIndicator3.setVisibility(View.GONE);
                circularProgressIndicator4.setVisibility(View.GONE);
                circularProgressIndicator5.setVisibility(View.GONE);

                imageView1.setImageResource(completed);
                imageView2.setImageResource(active);
                imageView3.setImageResource(inactive);
                imageView4.setImageResource(inactive);
                imageView5.setImageResource(inactive);


                view1.setBackgroundTintList(ColorStateList.valueOf(colorCompleted));
                view2.setBackgroundTintList(ColorStateList.valueOf(colorInComplete));
                view3.setBackgroundTintList(ColorStateList.valueOf(colorInComplete));
                view4.setBackgroundTintList(ColorStateList.valueOf(colorInComplete));

                break;
            case 3 :

                /// set resource
                txtDeviceStatus.setText("Initiating Calibration");
                if (!isImageSet3){
                    imgDeviceStatus.setImageResource(step3);
                    isImageSet3=true;
                }

                circularProgressIndicator3.setVisibility(View.VISIBLE);
                circularProgressIndicator2.setVisibility(View.GONE);
                circularProgressIndicator1.setVisibility(View.GONE);
                circularProgressIndicator4.setVisibility(View.GONE);
                circularProgressIndicator5.setVisibility(View.GONE);

                imageView1.setImageResource(completed);
                imageView2.setImageResource(completed);
                imageView3.setImageResource(active);
                imageView4.setImageResource(inactive);
                imageView5.setImageResource(inactive);



                view1.setBackgroundTintList(ColorStateList.valueOf(colorCompleted));
                view2.setBackgroundTintList(ColorStateList.valueOf(colorCompleted));
                view3.setBackgroundTintList(ColorStateList.valueOf(colorInComplete));
                view4.setBackgroundTintList(ColorStateList.valueOf(colorInComplete));

                break;
            case 4 :

                /// set resource
                txtDeviceStatus.setText("Activating All Sensors");
                if (!isImageSet4){
                    imgDeviceStatus.setImageResource(step4);
                    isImageSet4=true;
                }


                circularProgressIndicator4.setVisibility(View.VISIBLE);
                circularProgressIndicator2.setVisibility(View.GONE);
                circularProgressIndicator1.setVisibility(View.GONE);
                circularProgressIndicator3.setVisibility(View.GONE);
                circularProgressIndicator5.setVisibility(View.GONE);

                imageView1.setImageResource(completed);
                imageView2.setImageResource(completed);
                imageView3.setImageResource(completed);
                imageView4.setImageResource(active);
                imageView5.setImageResource(inactive);

                view1.setBackgroundTintList(ColorStateList.valueOf(colorCompleted));
                view2.setBackgroundTintList(ColorStateList.valueOf(colorCompleted));
                view3.setBackgroundTintList(ColorStateList.valueOf(colorCompleted));
                view4.setBackgroundTintList(ColorStateList.valueOf(colorInComplete));

                break;
            case 5 :
                /// set resource
                txtDeviceStatus.setText("Getting Device Ready");
                if (!isImageSet5){
                    imgDeviceStatus.setImageResource(step5);
                    isImageSet5=true;
                }


                circularProgressIndicator5.setVisibility(View.VISIBLE);
                circularProgressIndicator2.setVisibility(View.GONE);
                circularProgressIndicator1.setVisibility(View.GONE);
                circularProgressIndicator3.setVisibility(View.GONE);
                circularProgressIndicator4.setVisibility(View.GONE);

                imageView1.setImageResource(completed);
                imageView2.setImageResource(completed);
                imageView3.setImageResource(completed);
                imageView4.setImageResource(completed);
                imageView5.setImageResource(active);

                view1.setBackgroundTintList(ColorStateList.valueOf(colorCompleted));
                view2.setBackgroundTintList(ColorStateList.valueOf(colorCompleted));
                view3.setBackgroundTintList(ColorStateList.valueOf(colorCompleted));
                view4.setBackgroundTintList(ColorStateList.valueOf(colorCompleted));

                break;

            case 6 :

                /// set resource
                txtDeviceStatus.setText("Getting Device Ready");
                if (!isImageSet5){
                    imgDeviceStatus.setImageResource(step5);
                    isImageSet5=true;
                }



                circularProgressIndicator5.setVisibility(View.GONE);
                circularProgressIndicator2.setVisibility(View.GONE);
                circularProgressIndicator1.setVisibility(View.GONE);
                circularProgressIndicator3.setVisibility(View.GONE);
                circularProgressIndicator4.setVisibility(View.GONE);

                imageView1.setImageResource(completed);
                imageView2.setImageResource(completed);
                imageView3.setImageResource(completed);
                imageView4.setImageResource(completed);
                imageView5.setImageResource(completed);

                view1.setBackgroundTintList(ColorStateList.valueOf(colorCompleted));
                view2.setBackgroundTintList(ColorStateList.valueOf(colorCompleted));
                view3.setBackgroundTintList(ColorStateList.valueOf(colorCompleted));
                view4.setBackgroundTintList(ColorStateList.valueOf(colorCompleted));

                break;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        setFilters();
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
    private void CalibrationCompleted(){
        moveActivity();
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
        private final WeakReference<DeviceReady> mActivity;

        public MyHandler(DeviceReady activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == UsbService.MESSAGE_FROM_SERIAL_PORT) {
                String data = (String) msg.obj;

                if (!data.isEmpty()){

                    data = data.trim();
                    // Remove extra spaces between words
                    String cleanedString = data.replaceAll("\\s+", " ");

                    if (cleanedString.contains("%")){
                        mActivity.get().txtLog.append("completed"+"\n");
                        mActivity.get().moveActivity();
                    }else{
                        try{
                            mActivity.get().txtLog.append(cleanedString+"\n");
                            int i = Integer.parseInt(cleanedString);
                            performTask(i);

                        }catch (Exception e){
                            mActivity.get().txtLog.append(e.getMessage()+"\n");
                        }
                    }

                }else{
                    mActivity.get().txtLog.append("empty"+"\n");
                }

            }
        }

        public void performTask(int value){
            int totalTasks = 5;
            int totalTimeInSeconds = mActivity.get().calibrationTime;
            int currentTask = (value * totalTasks) / totalTimeInSeconds + 1;
            mActivity.get().txtLog.append("Current Task " + String.valueOf(currentTask)+"\n");
            mActivity.get().setAnalysingProgress(currentTask);
        }
    }


}