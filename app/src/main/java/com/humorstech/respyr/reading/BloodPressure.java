package com.humorstech.respyr.reading;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.humorstech.respyr.HTTP_URLS;
import com.humorstech.respyr.LoginData;
import com.humorstech.respyr.dashboard.MainActivity;
import com.humorstech.respyr.R;
import com.humorstech.respyr.StatusBarColor;
import com.humorstech.respyr.reading.Math.Fft;
import com.humorstech.respyr.reading.Math.Fft2;
import com.humorstech.respyr.results.GeneratingResults;
import com.humorstech.respyr.utills.BloodPressureResults;
import com.humorstech.respyr.utills.ReadingParameters;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Set;
import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;
import cz.msebera.android.httpclient.Header;

public class BloodPressure extends AppCompatActivity implements MyAsyncTask.AsyncTaskResponseListener {


    private UsbService usbService;
    private MyHandler mHandler;

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case UsbService.ACTION_USB_PERMISSION_GRANTED: // USB PERMISSION GRANTED
                    highFlag();
                    storeTime();
                    break;
                case UsbService.ACTION_USB_PERMISSION_NOT_GRANTED: // USB PERMISSION NOT GRANTED
                case UsbService.ACTION_USB_DISCONNECTED: // USB DISCONNECTED
                case UsbService.ACTION_NO_USB: // NO USB CONNECTED
                case UsbService.ACTION_USB_NOT_SUPPORTED: // USB NOT SUPPORTED
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

    private String gender;


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




    // bp calculation variables
    double Agg=26, Hei = 176, Wei=65, Gen=1;
    double Q=4.5;



    /// ppg variables
    private ArrayList<Double> GreenAvgList = new ArrayList<>();
    private ArrayList<Double> RedAvgList = new ArrayList<>();
    private int counter = 0;
    long startTime = 0 , endTime = 0, startWrongTime=0 , endWrongTime=0;
    private boolean startTimeFlag=false, endWrongTimeFlag=false, startWrongTimeFlag=false;
    private double SamplingFreq=0;
    private double totalTimeInSecs=0, wrongTime=0, wrongTime1=0;

    private  double bufferAvgB = 0, bufferAvgBr =0;

    private ArrayList<Double> wrongTimeArray = new ArrayList<>();

    private ArrayList<Double> tempBPM= new ArrayList<>();


    private boolean bpmCompletedFlag=false;
    private boolean isFirstTime=false;
    private boolean isFirstTime2=false;


    private int firstDataCounter;

    private CircularProgressIndicator circularProgressIndicator;


    // SPO2 variables
    double Stdr = 0;
    double Stdb = 0;
    double sumred = 0;
    double sumblue = 0;
    public int o2;

    double bpm1, bpm2;



    private LinearLayout  layoutBloodPressure, layoutDoNotRemoveFinger, layoutRemoveFinger;

    private RelativeLayout layoutKeepYourFinger;
    private boolean removeFinger=false;
    private boolean PPGDATA=false;



    private TextView txtVitalStatus;
    private TextView txtWaitingForYourFinger;
    private TextView txtWaitingForYourFinger2;
    private TextView txtLog;


    private String loginId, profileId;
    private int bFlag2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_pressure);
        StatusBarColor statusBarColor= new StatusBarColor(BloodPressure.this);
        statusBarColor.setColor(getResources().getColor(R.color.white));

        LoginData loginData = new LoginData(this);
        loginId = loginData.getLoginId();
        profileId = loginData.getProfileId();


        mHandler =new MyHandler(this);
        init();

    }


    @Override
    protected void onStart() {
        super.onStart();

        try {
            GreenAvgList.clear();
            RedAvgList.clear();


            SharedPreferences sh = getSharedPreferences("firstTime", MODE_PRIVATE);
            boolean s1 = sh.getBoolean("bflag1", false);
            boolean s2 = sh.getBoolean("bflag2", false);

            if (s1 && s2){
                bFlag2=0;
            }else if(s1 && !s2){
                bFlag2=1;
            }


            SharedPreferences sharedPreferences = getSharedPreferences(ReadingParameters.TITLE, Context.MODE_PRIVATE);
            String weight = sharedPreferences.getString(ReadingParameters.WEIGHT, "86");
            String height = sharedPreferences.getString(ReadingParameters.HEIGHT, "176");
            gender = sharedPreferences.getString(ReadingParameters.GENDER, "Male");
            String age = sharedPreferences.getString(ReadingParameters.AGE, "26");


            try {
                Hei = Double.parseDouble(height);
                Wei = Double.parseDouble(weight);
                Agg = Double.parseDouble(age);
            } catch (NumberFormatException e) {
                // Handle the error when parsing fails
                // For example, you can set default values or display an error message
                Hei = 0.0;  // Default value for height
                Wei = 0.0;  // Default value for weight
                Agg = 0.0;  // Default value for age
            }

            if (gender != null && (gender.equalsIgnoreCase("Female") || gender.equalsIgnoreCase("female"))) {
                Gen = 2;
            } else {
                Gen = 1;
            }

            if (Gen == 1) {
                Q = 5;
            }


            Toast.makeText(getApplicationContext(), String.valueOf(Gen), Toast.LENGTH_SHORT).show();


        } catch (NumberFormatException e) {
            // Handle the exception (e.g., show an error message or log the error)
            e.printStackTrace(); // You can replace this with your preferred error handling.
            handleException(e);
        }
    }


    private void init(){
        setReferences();
        onClicks();
    }


    private void setReferences(){
        txtLog=findViewById(R.id.txtLog);
        txtLog.setMovementMethod(new ScrollingMovementMethod());
        txtWaitingForYourFinger=findViewById(R.id.txt_waiting_for_your_finger);
        txtWaitingForYourFinger2=findViewById(R.id.txt_waiting_for_your_finger2);
        txtVitalStatus=findViewById(R.id.txt_vital_percent);



        circularProgressIndicator=findViewById(R.id.circular_progress1);
        circularProgressIndicator.setMaxProgress(100);

        // layouts
        layoutBloodPressure=findViewById(R.id.layout_blood_pressure);
        layoutKeepYourFinger=findViewById(R.id.layout_keep_finger);
        layoutDoNotRemoveFinger=findViewById(R.id.layout_do_not_remove_finger);
        layoutRemoveFinger=findViewById(R.id.layout_remove_finger);

    }

    private void onClicks(){
        ImageButton buttonCancel=findViewById(R.id.button_cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CancelTest.show(BloodPressure.this, BloodPressure.this);
            }
        });
    }

    private void stopPPG() {
        try {
            endTime = System.currentTimeMillis() / 1000;
            totalTimeInSecs = endTime - startTime;

            for (int i = 0; i < wrongTimeArray.size(); i++) {
                totalTimeInSecs = totalTimeInSecs - wrongTimeArray.get(i);
                txtLog.append("difference array-->" + String.valueOf(wrongTimeArray.get(i)) + "\n");
            }

            txtLog.append("totalTimeInSecs-->" + String.valueOf(totalTimeInSecs) + "\n");
            txtLog.append("counter-->" + String.valueOf(counter) + "\n");

            int n = counter;
            int progress = (int) ((n * 100.0) / 1200);

            txtLog.append("progress-->" + String.valueOf(progress) + "\n");
            circularProgressIndicator.setCurrentProgress(progress);
            txtVitalStatus.setText(progress + "%");
            System.out.println(progress);

            if (counter == 1200) {
                txtLog.append("start time-->" + startTime + "\n");
                txtLog.append("end time-->" + endTime + "\n");
                txtLog.append("counter-->" + counter + "\n");
                txtLog.append("Red Length-->" + RedAvgList.size() + "\n");
                txtLog.append("Green Length-->" + GreenAvgList.size() + "\n");

                bpmCompletedFlag = true;


                //bpmProcess();
            }
        } catch (NumberFormatException e) {
            // Handle NumberFormatException here
            // For example, set default values or display an error message to the user
            e.printStackTrace(); // Log the exception for debugging
        } catch (Exception e) {
            // Handle other exceptions here
            e.printStackTrace(); // Log the exception for debugging
        }
    }


    private void bpmProcess() {
        try {
            // Pulse reading is completed
            bpmCompletedFlag = true;

            Double[] Green = GreenAvgList.toArray(new Double[GreenAvgList.size()]);
            Double[] Red = RedAvgList.toArray(new Double[RedAvgList.size()]);

            SamplingFreq = (counter / 24.0);

            double HRFreq = Fft.FFT(Green, counter, SamplingFreq);
            double HRFreq1 = Fft.FFT(Red, counter, SamplingFreq);

            bpm1 = (int) Math.ceil(HRFreq * 60);
            bpm2 = (int) Math.ceil(HRFreq1 * 60);

            double finalBPM = 0;

            if (bpm1 >= 50 && bpm1 <= 200) {
                if (bpm2 >= 50 && bpm2 <= 200) {
                    if (bpm1 == bpm2) {
                        finalBPM = (bpm1 + bpm2) / 2;
                    } else {
                        double difference1 = bpm1 - bpm2;
                        double difference2 = bpm2 - bpm1;
                        if ((difference1 > 0 && difference1 < 30) || (difference2 > 0 && difference2 < 30)) {
                            finalBPM = (bpm1 + bpm2) / 2;
                        } else if (bpm1 > 60 && bpm1 < 120) {
                            finalBPM = bpm1;
                        } else if (bpm2 > 60 && bpm2 < 120) {
                            finalBPM = bpm2;
                        } else {
                            finalBPM = getNearestBPM(bpm1, bpm2);
                        }
                    }
                } else {
                    if (bpm1 >= 50 && bpm1 <= 140) {
                        finalBPM = bpm1;
                    } else if (bpm2 >= 50 && bpm2 <= 140) {
                        finalBPM = bpm2;
                    } else {
                        finalBPM = getNearestBPM(bpm1, bpm2);
                    }
                }
            } else {
                if (bpm1 >= 50 && bpm1 <= 140) {
                    finalBPM = bpm1;
                } else if (bpm2 >= 50 && bpm2 <= 140) {
                    finalBPM = bpm2;
                } else {
                    finalBPM = getNearestBPM(bpm1, bpm2);
                }
            }

            int beats = (int) finalBPM;

            // Breath Rate calculations
            double RRFreq = Fft2.FFT(Green, counter, SamplingFreq);
            double rrBPM1 = (int) Math.ceil(RRFreq * 24);
            double RR1Freq = Fft2.FFT(Red, counter, SamplingFreq);
            double rrBPM2 = (int) Math.ceil(RR1Freq * 24);

            if ((rrBPM1 > 10 || rrBPM1 < 24)) {
                if ((rrBPM2 > 10 || rrBPM2 < 24)) {
                    bufferAvgBr = (rrBPM1 + rrBPM2) / 2;
                } else {
                    bufferAvgBr = rrBPM1;
                }
            } else if ((rrBPM2 > 10 || rrBPM2 < 24)) {
                bufferAvgBr = rrBPM2;
            }

            int breath = (int) bufferAvgBr;

            // SPO2 calculations
            double meanr = sumred / counter;
            double meanb = sumblue / counter;

            for (int i = 0; i < counter - 1; i++) {
                Double bufferb = Green[i];
                Stdb = Stdb + ((bufferb - meanb) * (bufferb - meanb));

                Double bufferr = Red[i];
                Stdr = Stdr + ((bufferr - meanr) * (bufferr - meanr));
            }

            double varr = Math.sqrt(Stdr / (counter - 1));
            double varb = Math.sqrt(Stdb / (counter - 1));

            double R = (varr / meanr) / (varb / meanb);
            double spo2 = 100 - 5 * (R);
            o2 = (int) (spo2);

            if (beats <= 55) {
                beats = 60;
            }

            txtLog.append("Breath  ---> " + breath + "\n");
            txtLog.append("HRFreq---> " + HRFreq + "\n");
            txtLog.append("HRFreq1---> " + HRFreq1 + "\n");
            txtLog.append("Time---> " + totalTimeInSecs + "\n");
            txtLog.append("Sampling Freq---> " + SamplingFreq + "\n");
            txtLog.append("BPM1  ---> " + bpm1 + "\n");
            txtLog.append("BPM2  ---> " + bpm2 + "\n");
            txtLog.append("Beats  ---> " + beats + "\n");
            txtLog.append("SPO2  ---> " + o2 + "\n");

            SharedPreferences sharedPreferences = getSharedPreferences(BloodPressureResults.TITLE, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(BloodPressureResults.BPM1, String.valueOf(bpm1));
            editor.putString(BloodPressureResults.BPM2, String.valueOf(bpm2));
            editor.putString(BloodPressureResults.FINAL_BPM, String.valueOf(beats));
            editor.putString(BloodPressureResults.SPO2, String.valueOf(o2));
            editor.putString(BloodPressureResults.BREATH_RATE, String.valueOf(breath));
            editor.putString(BloodPressureResults.SYSTOLIC, String.valueOf(BloodPressureCalculation.finalSP(beats, Hei, Wei, Agg, gender)));
            editor.putString(BloodPressureResults.DIASTOLIC, String.valueOf(BloodPressureCalculation.finalDP(beats, Hei, Wei, Agg, gender)));
            editor.putString(BloodPressureResults.PROFILE_ID, String.valueOf(profileId));
            editor.putString(BloodPressureResults.LOGIN_ID, String.valueOf(loginId));
            editor.apply();

        } catch (Exception e) {
            // Handle exceptions here
            e.printStackTrace(); // Log the exception for debugging
            // You can add specific error handling logic as needed
        }
    }

    private static double getNearestBPM(double bpm1, double bpm2) {
        double targetBPM = 70; // Set a default target BPM value
        double diff1 = Math.abs(bpm1 - targetBPM);
        double diff2 = Math.abs(bpm2 - targetBPM);

        // Return the nearest BPM value
        if (diff1 < diff2) {
            return bpm1;
        } else {
            return bpm2;
        }
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

    private void stop(){
        String data = "}";
        if (usbService != null) {
            // if UsbService was correctly binded, Send data
            usbService.write(data.getBytes());
        }
    }

    /////////////////////////// change status bar color according ui///////////////////////////////////////////////////////////
    private void setStatusBarColorLight(){
        StatusBarColor statusBarColor= new StatusBarColor(BloodPressure.this);
        statusBarColor.setColor(getResources().getColor(R.color.white));
    }
    private void setStatusBarColorDark(){
        StatusBarColor statusBarColor= new StatusBarColor(BloodPressure.this);
        statusBarColor.setDarkColor(getResources().getColor(R.color.black));
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////// USB Operations ///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////PERFORM DATA RECEIVE FROM THE DEVICE///////////////////////////////////
    private static class MyHandler extends Handler {

        private final WeakReference<BloodPressure> mActivity;

        public MyHandler(BloodPressure activity) {
            mActivity = new WeakReference<>(activity);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == UsbService.MESSAGE_FROM_SERIAL_PORT) {
                String data = (String) msg.obj;
                if (data.contains(",")){
                    performPulseReading(data);
                }
            }
        }



        @SuppressLint("SetTextI18n")
        private void performPulseReading(String data) {
            try {
                String[] dataArray = data.split("\\s*,\\s*");
                if (dataArray.length < 2) {
                    // Handle the case where dataArray doesn't contain enough elements
                    Log.d("UsbService", "Invalid data format: " + data);
                    return;
                }

                double green = Double.parseDouble(dataArray[0]);
                double red = Double.parseDouble(dataArray[1]);

                if (!mActivity.get().bpmCompletedFlag) {
                    ///////////////////////////////////////////////// when data threshold matches//////////////////////////////////////////////
                    if ((green >= 25000 && green < 70000) && (red >= 25000 && red < 70000)) {
                        mActivity.get().firstDataCounter++;

                        if (mActivity.get().firstDataCounter < 100) {
                            mActivity.get().txtWaitingForYourFinger.setText("Analysing your finger....");
                            mActivity.get().txtWaitingForYourFinger2.setText("Analysing your finger....");
                        }

                        if (mActivity.get().firstDataCounter > 100) {
                            mActivity.get().txtLog.append("-----------------" + "\n");

                            mActivity.get().layoutKeepYourFinger.setVisibility(View.GONE);
                            mActivity.get().layoutDoNotRemoveFinger.setVisibility(View.GONE);
                            mActivity.get().layoutBloodPressure.setVisibility(View.VISIBLE);

                            mActivity.get().setStatusBarColorLight();

                            mActivity.get().sumred = mActivity.get().sumred + red;
                            mActivity.get().sumblue = mActivity.get().sumblue + green;

                            mActivity.get().isFirstTime = true;

                            if (!mActivity.get().startTimeFlag) {
                                mActivity.get().startTime = System.currentTimeMillis() / 1000;
                                mActivity.get().startTimeFlag = true;
                            }

                            if (mActivity.get().isFirstTime2) {
                                if (!mActivity.get().endWrongTimeFlag) {
                                    mActivity.get().endWrongTime = System.currentTimeMillis() / 1000;
                                    mActivity.get().endWrongTimeFlag = true;
                                    double wrongTime = (mActivity.get().endWrongTime - mActivity.get().startWrongTime);
                                    mActivity.get().wrongTimeArray.add(wrongTime);
                                }
                            }

                            mActivity.get().txtLog.append("Green-->" + green + "\n");
                            mActivity.get().txtLog.append("Red-->" + red + "\n");

                            mActivity.get().GreenAvgList.add(green);
                            mActivity.get().RedAvgList.add(red);
                            ++mActivity.get().counter;

                            mActivity.get().startWrongTimeFlag = false;
                            mActivity.get().stopPPG();
                        } else if (mActivity.get().isFirstTime) {
                            if (!mActivity.get().startWrongTimeFlag) { // only one time startTime
                                mActivity.get().startWrongTime = System.currentTimeMillis() / 1000;
                                mActivity.get().startWrongTimeFlag = true;
                                mActivity.get().endWrongTimeFlag = false;
                            }
                            mActivity.get().isFirstTime2 = true;
                        }
                    } else {  // when finger remove which pulse reading
                        mActivity.get().txtLog.append(mActivity.get().isFirstTime + "\n");
                        mActivity.get().txtLog.append(mActivity.get().isFirstTime2 + "\n");

                        mActivity.get().firstDataCounter = 0;
                        if (mActivity.get().isFirstTime) { // not the first time
                            if (!mActivity.get().startWrongTimeFlag) { // only one time startTime
                                mActivity.get().startWrongTime = System.currentTimeMillis() / 1000;
                                mActivity.get().startWrongTimeFlag = true;
                                mActivity.get().endWrongTimeFlag = false;
                            }

                            /// do not remove your finger
                            mActivity.get().txtLog.append("DO NOT REMOVE-->" + "\n");
                            mActivity.get().layoutBloodPressure.setVisibility(View.GONE);
                            mActivity.get().layoutDoNotRemoveFinger.setVisibility(View.VISIBLE);
                            mActivity.get().txtWaitingForYourFinger2.setText("Waiting for your finger....");
                            mActivity.get().setStatusBarColorDark();

                            mActivity.get().isFirstTime2 = true;
                        }
                    }
                } else { /// when pulse reading is completed
                    if (mActivity.get().counter >= 1200) {
                        if ((green >= 3000) && (red >= 3000)) {
                            mActivity.get().layoutBloodPressure.setVisibility(View.GONE);
                            mActivity.get().layoutRemoveFinger.setVisibility(View.VISIBLE);

                            if (!mActivity.get().removeFinger) {
                                mActivity.get().removeFinger = true;
                                //// save the final data
                                mActivity.get().stop();

                            }

                            if (mActivity.get().removeFinger && !mActivity.get().PPGDATA) {
                                mActivity.get().fetchBloodPressureData();
                                mActivity.get().PPGDATA = true;
                            }


                        } else {

                        }
                    }
                }
            } catch (NumberFormatException e) {
                // Handle the NumberFormatException by logging the error
                Log.e("UsbService", "Error parsing data: " + data, e);
            }
        }




    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////// finally send raw data to the server///////////////////////////////////////////////////////


    private void highFlag1(){
        SharedPreferences sharedPreferences = getSharedPreferences("firstTime",MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putBoolean("bflag2", true);
        myEdit.apply();
    }

    public void addDataToServer() {



        SharedPreferences sharedPreferences = getSharedPreferences(BloodPressureResults.TITLE, Context.MODE_PRIVATE);
        String profileId = sharedPreferences.getString(BloodPressureResults.PROFILE_ID, "0");
        String loginId = sharedPreferences.getString(BloodPressureResults.LOGIN_ID, "0");
        String finalBPM = sharedPreferences.getString(BloodPressureResults.FINAL_BPM, "0");
        String spo2 = sharedPreferences.getString(BloodPressureResults.SPO2, "0");
        String systolicPressure = sharedPreferences.getString(BloodPressureResults.SYSTOLIC, "0");
        String diastolicPressure = sharedPreferences.getString(BloodPressureResults.DIASTOLIC, "0");
        String testdata = sharedPreferences.getString(BloodPressureResults.DEVICE_RAW_DATA, "0");


        txtLog.append(profileId+"\n");
        txtLog.append(loginId+"\n");
        txtLog.append(finalBPM+"\n");
        txtLog.append(systolicPressure+"\n");
        txtLog.append(diastolicPressure+"\n");
        txtLog.append(testdata+"\n");

        RequestParams params = new RequestParams();
        params.put("bflag", bFlag2);
        params.put("bpm", finalBPM);
        params.put("testdata", testdata);
        params.put("subid", loginId + "$" + profileId);
        params.put("sp", systolicPressure);
        params.put("dp", diastolicPressure);
        params.put("dp", diastolicPressure);
        params.put("spo2", spo2);


        AsyncHttpClient client = new AsyncHttpClient();
        client.get(HTTP_URLS.RAW_DATA_SAVE, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                // You can perform any setup or logging here if needed
                txtLog.setText(String.valueOf(params));
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {

                String response = new String(responseBody);

                System.out.println("response production "+ response);
                System.out.println("response production "+ HTTP_URLS.RAW_DATA_SAVE+String.valueOf(params));
                txtLog.setText(response);
                txtLog.setText( HTTP_URLS.RAW_DATA_SAVE+String.valueOf(params));


                if (response.contains("data")){
                    highFlag1();
                    Intent intent = new Intent(getApplicationContext(), GeneratingResults.class);
                    intent.putExtra("response", response);
                    startActivity(intent);
                    finish();


                }else{
                    handleException("An error was encountered during the report calculation process with the following error code: RESPRROR007.");
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                // Handle different types of errors
                if (statusCode == 0) {
                    // No network connection
                    // Handle accordingly


                    handleException("No Internet Connection");
                    addDataToServer();

                } else if (statusCode == 404) {
                    // Resource not found
                    // Handle accordingly
                    handleException(error.getMessage());

                    handleException("An error was encountered during the report calculation process with the following error code: RESPRROR008.");


                } else {
                    // Other errors
                    // Retry the request or show an error message
                    handleException(error.getMessage());
                }
            }
        });
    }



    ///////////////////////////// handle errors//////////////////////////////////////////
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
        builder.setPositiveButton("OK", (dialog, which) -> {
            // Perform an action when the "OK" button is clicked
            // For example, you can close the dialog or perform another task.
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Disable outside touch dismissal
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);

        alertDialog.show();
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////




    private void fetchBloodPressureData(){
        // Create and execute the AsyncTask with the string parameters
        MyAsyncTask myAsyncTask = new MyAsyncTask(redDataParam(), irDataParam(), BloodPressure.this, profileId, loginId);
        myAsyncTask.execute();
    }

    @Override
    public void onTaskComplete(String response) {
        bloodPressureData(response);
    }

    private void bloodPressureData(String jsonResponse){
        try {
            // Parse the JSON response
            JSONObject jsonObject = new JSONObject(jsonResponse);

            // Extract values from the JSON object
            double respirationRate = jsonObject.getDouble("Respiration Rate");
            double skinConductance = jsonObject.getDouble("Skin Conductance");
            double heartRate = jsonObject.getDouble("heart_rate");
            double lfHfRatio = jsonObject.getDouble("lf_hf_ratio");
            String message = jsonObject.getString("message");
            double sdnn = jsonObject.getDouble("sdnn");
            double spo2 = jsonObject.getDouble("spo2");
            String stressCategory = jsonObject.getString("stress_category");
            double stressScore = jsonObject.getDouble("stress_score");

            txtLog.append("heartRate : " + String.valueOf(roundOff(heartRate)) +  "\n");
            txtLog.append("sp02 : " + String.valueOf(spo2) +  "\n");
            txtLog.append("respirationRate : " + String.valueOf(roundOff(respirationRate)) +  "\n");
            txtLog.append("skinConductance : " + String.valueOf(roundOff(skinConductance)) +  "\n");
            txtLog.append("lfHfRatio : " + String.valueOf(roundOff(lfHfRatio)) +  "\n");
            txtLog.append("message : " + String.valueOf(message) +  "\n");
            txtLog.append("sdnn : " + String.valueOf(roundOff(sdnn)) +  "\n");
            txtLog.append("stressCategory : " + String.valueOf(stressCategory) +  "\n");
            txtLog.append("stressScore : " + String.valueOf(roundOff(stressScore)) +  "\n");


            SharedPreferences sharedPreferences = getSharedPreferences(BloodPressureResults.TITLE, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(BloodPressureResults.BPM1, String.valueOf((int)heartRate));
            editor.putString(BloodPressureResults.BPM2, String.valueOf((int)heartRate));
            editor.putString(BloodPressureResults.FINAL_BPM, String.valueOf((int)heartRate));
            editor.putString(BloodPressureResults.SPO2, String.valueOf((int)spo2));
            editor.putString(BloodPressureResults.BREATH_RATE, String.valueOf(roundOff(respirationRate)));
            editor.putString(BloodPressureResults.SYSTOLIC, String.valueOf("120"));
            editor.putString(BloodPressureResults.DIASTOLIC, String.valueOf("70"));
            editor.putString(BloodPressureResults.PROFILE_ID, String.valueOf(profileId));
            editor.putString(BloodPressureResults.LOGIN_ID, String.valueOf(loginId));
            editor.apply();


            addDataToServer();




        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String redDataParam(){

        // Convert ArrayList<Double> to comma-separated String
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < RedAvgList.size(); i++) {
            stringBuilder.append(RedAvgList.get(i));
            if (i < RedAvgList.size() - 1) {
                stringBuilder.append(",");
            }
        }

        return stringBuilder.toString();
    }

    private String roundOff(double value){
        return String.format("%.2f", value);
    }


    private String irDataParam(){
        // Convert ArrayList<Double> to comma-separated String
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < GreenAvgList.size(); i++) {
            stringBuilder.append(GreenAvgList.get(i));
            if (i < GreenAvgList.size() - 1) {
                stringBuilder.append(",");
            }
        }

        return stringBuilder.toString();
    }
}


