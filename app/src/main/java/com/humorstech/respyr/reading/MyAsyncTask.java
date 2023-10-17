package com.humorstech.respyr.reading;


import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyAsyncTask extends AsyncTask<Void, Void, String> {

    private String redData;
    private String irData;

    private String profileId;
    private String loginId;
    private AsyncTaskResponseListener responseListener;



    public MyAsyncTask(String redData, String irData, AsyncTaskResponseListener listener, String profileId, String loginId) {
        this.redData = redData;
        this.irData = irData;
        this.responseListener = listener;
        this.profileId = profileId;
        this.loginId = loginId;
    }

    public interface AsyncTaskResponseListener {
        void onTaskComplete(String response);
    }

    @Override
    protected String doInBackground(Void... voids) {
        String response = null;
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            // Define the URL of your Flask API endpoint
            URL url = new URL("https://respyr.in/process_data");

            // Initialize HttpURLConnection
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Define the data to be sent
            String jsonInputString = "{\"RED\":\" " +redData+" \", \"IR\":\" "+irData+"\", \"login\":\"" +
                    loginId + "\", \"profile\":\"   "+profileId+"  \"}";

            // Write the JSON data to the output stream
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Get the response from the server
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line);
            }
            response = responseBuilder.toString();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close resources
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }

        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        // Handle the response here
        if (responseListener != null) {
            responseListener.onTaskComplete(result);
        }
    }
}
