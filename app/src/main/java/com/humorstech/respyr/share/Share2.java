package com.humorstech.respyr.share;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.humorstech.respyr.R;
import com.humorstech.respyr.StatusBarColor;
import com.humorstech.respyr.profile.user.AddNewProfile;
import com.humorstech.respyr.reading.Stuffs;
import com.techiness.progressdialoglibrary.ProgressDialog;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Share2 extends AppCompatActivity {

    TextView txtScoreName, txtScoreValue, txtScoreStatus, txtScoreMessage, txtResultDate, txtResultTime;
    ImageView imgScore, imgScoreStatus;

    String scoreName,scoreValue, scoreStatus, scoreMessage;
    String resultTime, resultDate;

    private  LinearLayout shareLayout;
    private ProgressDialog progressDialog;


    int[] images = { R.drawable.pancreas, R.drawable.vital_new, R.drawable.blow_new, R.drawable.activity_new, R.drawable.nutrition_new,R.drawable.liver2};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share2);

        // set status bar color
        StatusBarColor statusBarColor= new StatusBarColor(Share2.this);
        statusBarColor.setColor(getResources().getColor(R.color.white));


        init();
    }
    private void init(){
        initVars();

        getIntentData();
    }

    private void initVars(){


        progressDialog =  new ProgressDialog(Share2.this);

        shareLayout=findViewById(R.id.main_layout);
        shareLayout.setVisibility(View.GONE);

        txtScoreName=findViewById(R.id.txt_name_score);
        txtScoreValue=findViewById(R.id.txt_value_score);
        txtScoreStatus=findViewById(R.id.txt_score_status);
        txtScoreMessage=findViewById(R.id.txt_score_message);

        txtResultDate=findViewById(R.id.txt_current_date);
        txtResultTime=findViewById(R.id.txt_current_time);

        imgScore=findViewById(R.id.image_score);
        imgScoreStatus=findViewById(R.id.img_score_status);
        imgScoreStatus=findViewById(R.id.img_score_status);
        imgScoreStatus=findViewById(R.id.img_score_status);



        Typeface poppinsSemibold = ResourcesCompat.getFont(this, R.font.poppins_semibold);
        Typeface robotoMedium = ResourcesCompat.getFont(this, R.font.roboto_medium);
        Typeface mulish = ResourcesCompat.getFont(this, R.font.mulish);

        txtScoreName.setTypeface(poppinsSemibold);
        txtScoreValue.setTypeface(robotoMedium);
        txtResultDate.setTypeface(robotoMedium);
        txtResultTime.setTypeface(robotoMedium);
        txtScoreStatus.setTypeface(poppinsSemibold);
        txtScoreMessage.setTypeface(mulish);


    }


    private void getIntentData(){
        Intent intent = getIntent();
        scoreName = intent.getStringExtra("score_name");
        scoreStatus = intent.getStringExtra("score_status");
        scoreMessage = intent.getStringExtra("score_message");
        scoreValue = intent.getStringExtra("score_value");
        resultDate = intent.getStringExtra("date");
        resultTime =intent.getStringExtra("time");


        showLoading();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                shareLayout.setVisibility(View.VISIBLE);
                dismissLoadingProgress();
                setTexts();
            }
        },1000);

    }

    private void setTexts(){
        txtScoreName.setText(scoreName);
        txtScoreValue.setText(scoreValue);
        txtScoreStatus.setText(scoreStatus);
        txtScoreMessage.setText(scoreMessage);

        txtResultDate.setText(resultDate);
        txtResultTime.setText(resultTime);


        int imageIdScoreName = getImageId(scoreName);
        if (imageIdScoreName!=0){
            imgScore.setImageResource(imageIdScoreName);
        }

        int imageIdStatus = getImageStatus(Double.parseDouble(scoreValue));
        imgScoreStatus.setImageResource(imageIdStatus);

        convertLayoutToBiMap(shareLayout);


    }

    private int getImageId(String scoreName){
        switch (scoreName){
            case "Diabetic Score" : return images[0];
            case "Vital Score" : return images[1];
            case "Respiratory Score" : return images[2];
            case "Activity Score" : return images[3];
            case "Nutrition Score" : return images[4];
            case "Liver Score" : return images[5];
            default: return 0;
        }
    }

    private int getImageStatus(double score){
        if (score <= 100 && score >= 80) {
            return R.drawable.diabetic_badge;
        } else if (score >= 71  && score < 80) {
            return R.drawable.diabetic_badge;
        } else  {
            return R.drawable.badge_poor;
        }
    }


    private void convertLayoutToBiMap(LinearLayout linearLayout){

        // Get the view tree observer of the LinearLayout
        ViewTreeObserver viewTreeObserver = linearLayout.getViewTreeObserver();
        viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                // Ensure the width and height are valid
                int width = linearLayout.getWidth();
                int height = linearLayout.getHeight();

                // Check if width and height are valid
                if (width > 0 && height > 0) {
                    // Create a Bitmap with the specified width and height
                    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

                    // Create a Canvas with the bitmap
                    Canvas canvas = new Canvas(bitmap);

                    // Draw the LinearLayout to the Canvas
                    linearLayout.draw(canvas);

                    // Now 'bitmap' contains the drawn content of the LinearLayout

                    // Share the bitmap
                    shareImage(bitmap);

                    // Remove the listener as we don't need it anymore
                    linearLayout.getViewTreeObserver().removeOnPreDrawListener(this);
                }

                // Return true to continue with the drawing pass
                return true;
            }
        });
    }


    private void shareImage(Bitmap bitmap) {
        if (bitmap != null) {
            // Convert the Bitmap to a byte array
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();

            // Create a ByteArrayInputStream from the byte array
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);

            // Get a ContentResolver instance from your activity or context
            ContentResolver contentResolver = getContentResolver();

            // Insert the ByteArrayInputStream into a ContentValues object
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, Stuffs.getCurrentTime()+".png");
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");

            // Insert the ByteArrayInputStream into the MediaStore and get a content URI
            Uri imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            if (imageUri != null) {
                try {
                    OutputStream outputStream = contentResolver.openOutputStream(imageUri);
                    if (outputStream != null) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = byteArrayInputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                        outputStream.close();
                    }
                    byteArrayInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Create an Intent to share the image
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);

                // Set the image URI
                shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);

                // Set the text content
                String text = "\uD83C\uDF1F Exciting News! \uD83C\uDF1F\n" +
                        "\n" +
                        "I've just started tracking my fitness journey with Respyr, and it's been absolutely transformative! \uD83C\uDFCB️\u200D♂️ If you're looking for high-quality health products to enhance your well-being, you should definitely check out what Respyr has to offer! \uD83C\uDF31\n" +
                        "\n" +
                        "Respyr provides top-notch health solutions designed to support your fitness goals and overall wellness. From fitness essentials to supplements, they have everything you need to lead a healthier lifestyle. \uD83C\uDFAF I can't recommend their products enough! \uD83D\uDCAA\n" +
                        "\n" +
                        "\uD83D\uDECD️ Explore Respyr's product range now and invest in your health and fitness journey! \uD83D\uDC49 Explore Respyr\n" +
                        "\n" +
                        "Let's prioritize our health together! \uD83C\uDF1F #FitnessGoals #HealthyLifestyle #RespyrWellness\n" +
                        "\n" +
                        "Take care and stay healthy! \uD83C\uDFC3\u200D♀️\uD83C\uDFC3\u200D♂️";
                String url = "https://www.respyr.in";
                shareIntent.putExtra(Intent.EXTRA_TEXT, text + "\n" + url);

                // Specify the MIME type for the image
                shareIntent.setType("image/png");

                // Start the chooser dialog to select the sharing app
                startActivity(Intent.createChooser(shareIntent, "Select App"));
            }
        }
    }

    private void showLoading(){
        progressDialog.show();
    }


    private void dismissLoadingProgress(){
        progressDialog.dismiss();
    }






}