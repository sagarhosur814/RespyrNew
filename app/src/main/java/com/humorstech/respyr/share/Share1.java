package com.humorstech.respyr.share;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.humorstech.respyr.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Share1 extends AppCompatActivity {

    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share1);

        // Get the reference to the LinearLayout
        LinearLayout linearLayout = findViewById(R.id.main_layout); // Replace with your LinearLayout ID

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
                    bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

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
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();

            // Create a ByteArrayInputStream from the byte array
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);

            // Get a ContentResolver instance from your activity or context
            ContentResolver contentResolver = getContentResolver();

            // Insert the ByteArrayInputStream into a ContentValues object
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "image.jpg");
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");

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
                String text = "Check out this amazing image!";
                String url = "https://www.example.com";
                shareIntent.putExtra(Intent.EXTRA_TEXT, text + "\n" + url);

                // Specify the MIME type for the image
                shareIntent.setType("image/jpeg");

                // Start the chooser dialog to select the sharing app
                startActivity(Intent.createChooser(shareIntent, "Select App"));
            }
        }
    }
}
