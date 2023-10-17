package com.humorstech.respyr;

import android.content.Context;
import android.util.Log;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

public class MyVolleyRequest {

    private static final String TAG = MyVolleyRequest.class.getSimpleName();
    private final Context context;
    private final RequestQueue requestQueue;

    public MyVolleyRequest(Context context) {
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context);
    }




    public void makeGetRequest(final String apiUrl, final VolleyResponseListener listener) {
        final int maxRetries = 3; // Number of retry attempts
        final int initialTimeoutMs = 5000; // Initial timeout in milliseconds
        final float backoffMultiplier = 1.0f; // Backoff multiplier (no backoff)

        RetryPolicy retryPolicy = new DefaultRetryPolicy(
                initialTimeoutMs,
                maxRetries,
                backoffMultiplier
        );

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET, apiUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle successful response
                        if (listener != null) {
                            listener.onResponse(response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle network errors
                        if (error.networkResponse != null) {
                            Log.e(TAG, "Error Response Code: " + error.networkResponse.statusCode);
                        }
                        if (error.getMessage() != null) {
                            Log.e(TAG, "Error Message: " + error.getMessage());
                        }

                        // Retry the request when there is no internet connection
                        if (listener != null && !NetworkUtils.isNetworkAvailable(context)) {
                            listener.onRetry();
                        } else {
                            // Handle other errors
                            if (listener != null) {
                                listener.onError(error);
                            }
                        }
                    }
                }
        );

        // Set the retry policy for the request
        stringRequest.setRetryPolicy(retryPolicy);

        // Add the request to the request queue
        requestQueue.add(stringRequest);
    }




    public interface VolleyResponseListener {
        void onResponse(String response); // Change the parameter type to String
        void onError(VolleyError error);
        void onRetry();
    }
}
