package com.humorstech.respyr;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.HashMap;
import java.util.Map;

public class JsonRequest {

    public interface JsonResponseListener {
        void onSuccess(String jsonResponse);
        void onError(String errorMessage);
    }

    public static void makeRequest(Context context, String url, final Map<String, String> params, final JsonResponseListener listener) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle the JSON response as a string
                        listener.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage;
                        if (error.networkResponse != null) {
                            // Volley provided an HTTP response code
                            errorMessage = "Volley error: HTTP " + error.networkResponse.statusCode;
                        } else {
                            // No HTTP response code, handle other errors
                            errorMessage = "Volley error: " + error.toString();
                        }
                        listener.onError(errorMessage);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Add your parameters here
                return params;
            }
        };

        // Handle potential network errors
        request.setRetryPolicy(new DefaultRetryPolicy(
                5000, // Timeout in milliseconds
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        // Add the request to the request queue
        requestQueue.add(request);
    }
}
