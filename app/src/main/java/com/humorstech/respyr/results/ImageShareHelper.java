package com.humorstech.respyr.results;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.view.View;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageShareHelper {

    // Method to share a captured image
    public static void shareImage(Bitmap imageBitmap, String title, String message, View view) {
        // Save the image to a temporary file
        File imagePath = new File(view.getContext().getExternalCacheDir(), "share_image.png");
        try {
            FileOutputStream fos = new FileOutputStream(imagePath);
            imageBitmap.compress(CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create an intent to share the image
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(view.getContext(), view.getContext().getApplicationContext().getPackageName() + ".fileprovider", imagePath));
        shareIntent.putExtra(Intent.EXTRA_TEXT, message);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, title);

        // Grant permission to read the temporary image file to other apps
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // Start the intent to share the image
        view.getContext().startActivity(Intent.createChooser(shareIntent, "Share Image"));
    }
}
