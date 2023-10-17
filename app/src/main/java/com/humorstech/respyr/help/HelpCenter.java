package com.humorstech.respyr.help;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.window.SplashScreen;

import com.humorstech.respyr.R;
import com.humorstech.respyr.StatusBarColor;
import com.humorstech.respyr.dashboard.MainActivity;
import com.humorstech.respyr.profile.user.ProfileData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HelpCenter extends AppCompatActivity {

    ImageButton bt_toggle_text;
    LinearLayout lyt_expand_text;
    NestedScrollView nested_content;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_center);
        StatusBarColor statusBarColor= new StatusBarColor(  HelpCenter.this);
        statusBarColor.setColor(getResources().getColor(R.color.white));

        init();


//        bt_toggle_text = findViewById(R.id.bt_toggle_text);
//        lyt_expand_text = findViewById(R.id.lyt_expand_text);
//        nested_content = findViewById(R.id.nested_content);




        List<QAItem> qaItemList = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(HelpUtils.QA_LIST); // jsonData is your JSON string
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String question = jsonObject.getString("question");
                String answer = jsonObject.getString("answer");
                QAItem qaItem = new QAItem(question, answer);
                qaItemList.add(qaItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerView); // Make sure to have a RecyclerView in your layout XML
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        QAAdapter adapter = new QAAdapter(HelpCenter.this, qaItemList);
        recyclerView.setAdapter(adapter);



    }


    private void init(){
        initVars();
        onClicks();
    }
    private void initVars(){

    }
    private void onClicks(){
        Button buttonContactSupport = findViewById(R.id.button_contact_support);
        buttonContactSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactSupportSheet.show(HelpCenter.this);
            }
        });


        ImageButton buttonBack = findViewById(R.id.button_back);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}