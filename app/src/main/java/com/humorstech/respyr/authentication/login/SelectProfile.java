package com.humorstech.respyr.authentication.login;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.Toast;

import com.humorstech.respyr.HTTP_URLS;
import com.humorstech.respyr.ProfileGridAdapter;
import com.humorstech.respyr.R;
import com.humorstech.respyr.dashboard.MainActivity;
import com.humorstech.respyr.utills.ActiveProfile;
import com.humorstech.respyr.utills.LoginUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.techiness.progressdialoglibrary.ProgressDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;

public class SelectProfile extends AppCompatActivity {

    private GridView gridView;
    private ProfileGridAdapter profileGridAdapter;
    private List<Map<String, Object>> profilesDataList;
    private String loginId;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_profile);
        init();
        SharedPreferences sharedPreferences3 = getSharedPreferences(LoginUtils.LOGIN_TITLE, Context.MODE_PRIVATE);
        loginId = sharedPreferences3.getString(LoginUtils.LOGIN_ID, "");
        getProfileDataById(loginId);
    }

    private void init() {
        initVars();
        setOnClickListeners();
    }

    private void initVars() {
        progressDialog = new ProgressDialog(SelectProfile.this);
        gridView = findViewById(R.id.profileGridView);
        profilesDataList = new ArrayList<>();
        profileGridAdapter = new ProfileGridAdapter(this, profilesDataList);
        gridView.setAdapter(profileGridAdapter);
    }

    private void setOnClickListeners() {
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            try {
                Map<String, Object> profile = profilesDataList.get(position);
                boolean profileHav = (boolean) profile.get("profile_hav");
                if (profileHav) {
                    handleProfileSelection(profile);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    private void handleProfileSelection(Map<String, Object> profile) {
        String name = (String) profile.get("name");
        String profile_id = (String) profile.get("profile_id");
        // Extract other profile information as needed

        // Clear and set the active profile
        setActiveProfile(profile_id, loginId, name);

        // Navigate to the main activity
        navigateToMainActivity();
    }

    private void setActiveProfile(String profile_id, String loginId, String name) {
        SharedPreferences preferences = getSharedPreferences(ActiveProfile.Title, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        SharedPreferences sharedPreferences3 = getSharedPreferences(ActiveProfile.Title, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor3 = sharedPreferences3.edit();
        editor3.putString(ActiveProfile.PROFILE_ID, profile_id);
        editor3.putString(ActiveProfile.LOGIN_ID, loginId);
        editor3.putString(ActiveProfile.NAME, name);
        // Set other profile information here
        editor3.apply();
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void getProfileDataById(String loginId) {
        RequestParams params = new RequestParams();
        params.put("login_id", loginId);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(HTTP_URLS.fetchProfilesOfLogin, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                showLoading();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                dismissLoadingProgress();
                String response = new String(responseBody);
                fetchProfileData(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                dismissLoadingProgress();
                handleFailure(statusCode, errorResponse, e);
            }
        });
    }

    private void handleFailure(int statusCode, byte[] errorResponse, Throwable e) {
        if (statusCode == 404) {
            handleException("Server not found. Please check your internet connection.");
        } else if (statusCode >= 500) {
            handleException("Server error. Please try again later.");
        } else if (errorResponse != null) {
            String errorMessage = new String(errorResponse);
            handleException(errorMessage);
            Toast.makeText(SelectProfile.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
        } else if (e != null) {
            handleException("Network error or other exception. Please try again.");
        } else {
            handleException("Something went wrong! Please try again.");
        }
    }

    private void handleException(String message) {
        showErrorDialog("An error occurred: " + message);
    }

    private void showErrorDialog(String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage(errorMessage);
        builder.setPositiveButton("OK", (dialog, which) -> getProfileDataById(loginId));
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private void fetchProfileData(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            profilesDataList.clear();

            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject profiles = jsonObject.getJSONObject(key);
                JSONArray profileKeys = profiles.names();

                for (int i = 0; i < (profileKeys != null ? Objects.requireNonNull(profileKeys).length() : 0); i++) {
                    String profileKey = profileKeys.getString(i);
                    JSONObject profileObject = profiles.getJSONObject(profileKey);

                    Map<String, Object> profileMap = new HashMap<>();
                    Iterator<String> profileDataKeys = profileObject.keys();
                    while (profileDataKeys.hasNext()) {
                        String profileDataKey = profileDataKeys.next();
                        profileMap.put(profileDataKey, profileObject.get(profileDataKey));
                    }

                    profilesDataList.add(profileMap);
                }
            }

            profileGridAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showLoading() {
        progressDialog.show();
    }

    private void dismissLoadingProgress() {
        progressDialog.dismiss();
    }
}
