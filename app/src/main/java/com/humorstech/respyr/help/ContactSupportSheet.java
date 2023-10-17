package com.humorstech.respyr.help;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.humorstech.respyr.R;
public class ContactSupportSheet {
    public static BottomSheetDialog bsDialog;

    @SuppressLint("SetTextI18n")
    public static void show(Activity activity) {

        try {
            if (activity == null || activity.isFinishing()) {
                // Handle the case when the activity is not valid or already finishing
                return;
            }

            Context context = activity.getApplicationContext();

            bsDialog = new BottomSheetDialog(activity, R.style.TransparentDialog);
            bsDialog.setContentView(R.layout.contact_support_sheet);
            bsDialog.getWindow().setNavigationBarColor(ContextCompat.getColor(context, R.color.white));
            bsDialog.show();


            LinearLayout btOpenWhatsapp = bsDialog.findViewById(R.id.button_open_whatsapp);
            btOpenWhatsapp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openWhatsAppWithMessage(v, activity);
                }
            });

            LinearLayout btOpenEmail = bsDialog.findViewById(R.id.button_open_email);
            btOpenEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openEmailClient(activity);
                }
            });


        } catch (WindowManager.BadTokenException e) {
            // Handle the BadTokenException
            e.printStackTrace();
            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    public static void openWhatsAppWithMessage(View view, Activity activity) {
        String phoneNumber = "+919902552385"; // Replace with the recipient's phone number
        String message = "Hello, this is a WhatsApp message!"; // Replace with your desired message

        try {
            // Create a Uri with the WhatsApp number in the format "whatsapp://send?phone=XXXXXXXXXXX"
            Uri uri = Uri.parse("whatsapp://send?phone=" + phoneNumber);

            // Create an Intent with the ACTION_SENDTO action and the WhatsApp Uri
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);

            // Add the message to the Intent as an extra text
            intent.putExtra(Intent.EXTRA_TEXT, message);

            // Start the Intent to open WhatsApp with the predefined message
            activity.startActivity(intent);
        } catch (Exception e) {
            // Handle any exceptions that may occur, such as WhatsApp not being installed

            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    public static void openEmailClient(Activity activity) {
        // Specify the recipient email address (you can leave it empty if you want to let the user choose)
        String recipientEmail = "info@humorstech.com";

        // Create an Intent with the ACTION_SENDTO action and the mailto: URI
        Uri uri = Uri.parse("mailto:" + recipientEmail);
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, uri);

        // Optionally, you can set the subject and body of the email
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject of the Email");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Hello, this is the body of the email.");

        // Start the Intent to open the email client
        activity.startActivity(Intent.createChooser(emailIntent, "Send Email"));
    }

}
