package com.humorstech.respyr.authentication.profile_creation.lifestyle;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.humorstech.respyr.Animatoo1;
import com.humorstech.respyr.R;
import com.humorstech.respyr.StatusBarColor;
import com.humorstech.respyr.authentication.ProgressDots;
import com.humorstech.respyr.utills.ProfileCreationData;
import java.util.ArrayList;
import java.util.List;

public class MentalConditions extends AppCompatActivity {

    private CheckBox chkNone, chk1, chk2, chk3, chk4, chk5, chk6, chk7, chk8;
    private List<String> conditions = new ArrayList<>();
    private Button buttonNext, buttonBack;
    private String mentalConditions;
    private boolean isSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mental_conditions);
        StatusBarColor statusBarColor = new StatusBarColor(this);
        statusBarColor.setColor(getResources().getColor(R.color.white));
        init();
    }

    private void init() {
        initVars();
        checkBoxes();
        onClicks();
    }

    private void initVars() {
        chkNone = findViewById(R.id.check_box_none);
        chk1 = findViewById(R.id.chk1);
        chk2 = findViewById(R.id.chk2);
        chk3 = findViewById(R.id.chk3);
        chk4 = findViewById(R.id.chk4);
        chk5 = findViewById(R.id.chk5);
        chk6 = findViewById(R.id.chk6);
        chk7 = findViewById(R.id.chk7);
        chk8 = findViewById(R.id.chk8);

        buttonNext = findViewById(R.id.button_next);
        buttonBack = findViewById(R.id.button_back);

        // Dots
        LinearLayout linearLayout = findViewById(R.id.dotLayout);
        ProgressDots.generateDotIndicators(getApplicationContext(), linearLayout, 8, 8);
    }

    private void onClicks() {
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder stringBuilder = new StringBuilder();

                for (int i = 0; i < conditions.size(); i++) {
                    stringBuilder.append(conditions.get(i));
                    if (i != conditions.size() - 1) {
                        stringBuilder.append(",");
                    }
                }

                if (stringBuilder.length() == 0) {
                    mentalConditions = "None";
                } else {
                    mentalConditions = stringBuilder.toString();
                }

                if (isSelected) {
                    // Store mental conditions for profile creation
                    SharedPreferences sharedPreferences = getSharedPreferences(ProfileCreationData.TITLE, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(ProfileCreationData.MENTAL_CONDITIONS, mentalConditions);
                    editor.apply();

                    Intent intent = new Intent(getApplicationContext(), LifeStyleScore.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MentalConditions.this, "Please select at least one condition", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo1.animateSlideRight(MentalConditions.this);
    }

    private void checkBoxes() {
        chkNone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    clearAllConditions();
                }
            }
        });

        chk1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                handleCheckBoxChange(isChecked, "Anxiety/Stress");
            }
        });

        chk2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                handleCheckBoxChange(isChecked, "Depression");
            }
        });

        chk3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                handleCheckBoxChange(isChecked, "Post-Traumatic Stress Disorder (PTSD)");
            }
        });

        chk4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                handleCheckBoxChange(isChecked, "Schizophrenia");
            }
        });

        chk5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                handleCheckBoxChange(isChecked, "Eating Disorders");
            }
        });

        chk6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                handleCheckBoxChange(isChecked, "Disruptive behaviour and dissocial disorders");
            }
        });

        chk7.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                handleCheckBoxChange(isChecked, "Autism spectrum");
            }
        });

        chk8.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                handleCheckBoxChange(isChecked, "Attention deficit hyperactivity disorder (ADHD)");
            }
        });
    }

    private void handleCheckBoxChange(boolean isChecked, String condition) {
        if (isChecked) {
            chkNone.setChecked(false);
            conditions.add(condition);
            isSelected = true;
        } else {
            conditions.remove(condition);
            if (conditions.isEmpty()) {
                isSelected = false;
            }
        }
    }

    private void clearAllConditions() {
        chk1.setChecked(false);
        chk2.setChecked(false);
        chk3.setChecked(false);
        chk4.setChecked(false);
        chk5.setChecked(false);
        chk6.setChecked(false);
        chk7.setChecked(false);
        chk8.setChecked(false);

        conditions.clear();
        isSelected = true;
    }
}
