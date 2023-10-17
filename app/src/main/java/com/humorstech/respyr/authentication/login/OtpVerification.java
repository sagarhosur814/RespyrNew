package com.humorstech.respyr.authentication.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.telephony.SmsMessage;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gne.www.lib.PinView;
import com.humorstech.respyr.Animatoo1;
import com.humorstech.respyr.Dialogs;
import com.humorstech.respyr.HTTP_URLS;
import com.humorstech.respyr.R;
import com.humorstech.respyr.StatusBarColor;
import com.humorstech.respyr.utills.IntentUtils;
import com.humorstech.respyr.utills.LoginUtils;
import com.humorstech.respyr.utills.ProfileCreationData;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.w3c.dom.Text;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;
import cz.msebera.android.httpclient.Header;

public class OtpVerification extends AppCompatActivity {

    private String phoneNumber, countryCode, dialCode;
    private TextView txtMessage;
    private Button buttonNext;
    private static final String TAG = "OtpVerification";
    private TextView textOTP;
    private boolean isOTPSent;
    private PinView otpView;
    private Button resendOTP;

    private static final int SMS_PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        StatusBarColor statusBarColor = new StatusBarColor(OtpVerification.this);
        statusBarColor.setColor(getResources().getColor(R.color.white));

        SmsReceiver smsReceiver = new SmsReceiver();
        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(smsReceiver, intentFilter);

        // Check and request SMS permission if needed
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_SMS},
                    SMS_PERMISSION_REQUEST_CODE);
        }

        init();
    }

    private void init() {
        initVars();
        onClick();
        Intent intent = getIntent();
        if (intent != null) {
            phoneNumber = intent.getStringExtra(IntentUtils.userPhoneNumber);
            countryCode = intent.getStringExtra(IntentUtils.userCountryCode);
            dialCode = intent.getStringExtra(IntentUtils.userDialCode);
            // set message
            sendOTP();
        }
    }

    private void initVars() {
        txtMessage = findViewById(R.id.txt_message);
        buttonNext = findViewById(R.id.button_next);
        textOTP = findViewById(R.id.otp);
        otpView = findViewById(R.id.otp_view);
        resendOTP = findViewById(R.id.button_resend_otp);
    }

    private void onClick() {
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOTPSent && !otpView.getText().toString().isEmpty()) {
                    verifyOTP(otpView.getText().toString());
                }
            }
        });
        resendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOTP();
            }
        });
    }

    private void sendOTP() {
        RequestParams params = new RequestParams();
        params.put("phone_no", phoneNumber);
        params.put("country_code", countryCode);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(HTTP_URLS.sendOTP, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                Dialogs.showLoadingDialog(OtpVerification.this, "Sending OTP");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response1) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.hideLoadingDialog();
                        String response = new String(response1).trim();
                        String[] parts = response.split("\\$");

                        if (parts.length >= 2 && Objects.equals(parts[0], "otp_sent")) {
                            Spanned message = Html.fromHtml("<font color='#535359'>An OTP has been sent to</font> <font color='#308BF9'>" + dialCode + phoneNumber + "</font>");
                            txtMessage.setText(message);
                            textOTP.setText(parts[1]);
                            isOTPSent = true;
                            startTimer();
                        } else {
                            Toast.makeText(OtpVerification.this, "Something went wrong while sending OTP\nplease try again later.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, 1500);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // Handle failure (e.g., show an error message)
                Dialogs.hideLoadingDialog();
                Toast.makeText(OtpVerification.this, "Failed to send OTP. Please try again later.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRetry(int retryNo) {
                // Handle retry if needed
            }
        });
    }

    private void verifyOTP(String OTP) {
        RequestParams params = new RequestParams();
        params.put("phone_no", phoneNumber);
        params.put("country_code", countryCode);
        params.put("otp", OTP);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(HTTP_URLS.checkOTP, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                Dialogs.showLoadingDialog(OtpVerification.this, "Verifying OTP....");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response2) {
                String response = new String(response2).trim();
                String[] parts = response.split("\\$");
                if (parts.length >= 2 && parts[0].equals("true")) {
                    checkProfileCount(parts[1]);
                } else {
                    Toast.makeText(OtpVerification.this, "OTP not matching", Toast.LENGTH_SHORT).show();
                    Dialogs.hideLoadingDialog();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // Handle failure (e.g., show an error message)
                Dialogs.hideLoadingDialog();
                Toast.makeText(OtpVerification.this, "Failed to verify OTP. Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startTimer() {
        CircularProgressIndicator progress = findViewById(R.id.progress_otp_time);
        TextView txtTime = findViewById(R.id.txts_otp_time);

        progress.setMaxProgress(30);
        progress.setCurrentProgress(0);
        disableOTPButton(true, resendOTP);
        txtTime.setText("30");

        CountDownTimer countDownTimer = new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                progress.setCurrentProgress(30 - (int) seconds);
                txtTime.setText(String.valueOf((int) seconds));
            }

            public void onFinish() {
                disableOTPButton(false, resendOTP);
            }
        };

        countDownTimer.start();
    }

    private void disableOTPButton(boolean disable, Button button) {
        if (disable) {
            button.setEnabled(false);
            button.setBackground(getResources().getDrawable(R.drawable.eclips1));
            button.setTextColor(getResources().getColor(R.color.grey));
            button.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.light));
        } else {
            button.setEnabled(true);
            button.setBackground(getResources().getDrawable(R.drawable.eclips1));
            button.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.black));
            button.setTextColor(getResources().getColor(R.color.white));
        }
    }

    private void checkProfileCount(String login_id) {
        RequestParams params = new RequestParams();
        params.put("login_id", login_id);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(HTTP_URLS.checkProfileCount, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                // Called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response3) {
                String response = new String(response3).trim();

                SharedPreferences sharedPreferences2 = getSharedPreferences(LoginUtils.LOGIN_TITLE, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor2 = sharedPreferences2.edit();
                editor2.putString(LoginUtils.LOGIN_ID, login_id);
                editor2.putString(LoginUtils.PHONE_NUMBER, phoneNumber);
                editor2.apply();

                Intent intent;
                // If response equals 0 means new user
                if (response.equals("0")) {
                    // Store login id for profile creation
                    SharedPreferences sharedPreferences = getSharedPreferences(ProfileCreationData.TITLE, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(ProfileCreationData.LOGIN_ID, login_id);
                    editor.apply();

                    intent = new Intent(getApplicationContext(), Name.class);
                } else {
                    intent = new Intent(getApplicationContext(), SelectProfile.class);
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // Handle failure (e.g., show an error message)
                Dialogs.hideLoadingDialog();
                Toast.makeText(OtpVerification.this, "Failed to check profile count. Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void displayIncomingSms(String sender, String message) {
        if (message.contains("Respyr")) {
            otpView.setText(extractOTP(message));
            if (isOTPSent && !otpView.getText().toString().isEmpty()) {
                verifyOTP(otpView.getText().toString());
            }
        }
    }

    public String extractOTP(String text) {
        // Define a regular expression pattern to match the OTP (a sequence of 4 digits)
        Pattern pattern = Pattern.compile("\\b\\d{4}\\b");

        // Create a Matcher to find the pattern in the text
        Matcher matcher = pattern.matcher(text);

        // Find the first occurrence of the OTP
        if (matcher.find()) {
            return matcher.group(0); // Return the matched OTP
        }

        return null; // Return null if OTP is not found
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), Login.class);
        startActivity(intent);
        Animatoo1.animateSlideRight(OtpVerification.this);
    }

    // BroadcastReceiver to handle incoming SMS messages
    private class SmsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    if (pdus != null) {
                        for (Object pdu : pdus) {
                            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                            String sender = smsMessage.getDisplayOriginatingAddress();
                            String message = smsMessage.getDisplayMessageBody();

                            // Call the displayIncomingSms method to handle the SMS
                            displayIncomingSms(sender, message);
                        }
                    }
                }
            }
        }
    }
}
