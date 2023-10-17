package com.humorstech.respyr;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            String imageUrl = remoteMessage.getData().get("image_url"); // Get the image URL from data payload

            Log.d("FCM Message", "Title: " + title + ", Body: " + body);

            // Display the notification with image (if imageUrl is provided)
            if (imageUrl != null && !imageUrl.isEmpty()) {
                showNotificationWithImage(title, body, imageUrl);
            } else {
                // If imageUrl is not provided, show a notification without an image
                showNotification(title, body);
            }
        }
    }

    private void showNotification(String title, String message) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager);
        }

        int notificationId = (int) System.currentTimeMillis();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default")
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.respyr_logo)
                .setAutoCancel(true);

        Notification notification = builder.build();
        notificationManager.notify(notificationId, notification);
    }

    private void showNotificationWithImage(String title, String message, String imageUrl) {
        // Use Picasso to load and display the image
        Picasso.get().load(imageUrl).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    createNotificationChannel(notificationManager);
                }

                int notificationId = (int) System.currentTimeMillis();

                NotificationCompat.Builder builder = new NotificationCompat.Builder(MyFirebaseMessagingService.this, "default")
                        .setContentTitle(title)
                        .setContentText(message)
                        .setSmallIcon(R.drawable.respyr_logo)
                        .setLargeIcon(bitmap) // Set the image as the large icon
                        .setAutoCancel(true);

                Notification notification = builder.build();
                notificationManager.notify(notificationId, notification);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                // Handle the case where image loading fails
                showNotification(title, message); // Show a notification without the image
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                // Handle image loading preparation (if needed)
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel(NotificationManager notificationManager) {
        String channelId = "default";
        CharSequence channelName = "Default Channel";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        notificationManager.createNotificationChannel(channel);
    }
}
