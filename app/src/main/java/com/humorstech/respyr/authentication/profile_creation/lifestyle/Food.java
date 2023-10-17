package com.humorstech.respyr.authentication.profile_creation.lifestyle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.google.android.material.snackbar.Snackbar;
import com.humorstech.respyr.R;
import com.humorstech.respyr.StatusBarColor;
import com.humorstech.respyr.authentication.ProgressDots;
import com.humorstech.respyr.daily_routine.SearchMeal;
import com.humorstech.respyr.daily_routine.added.AddedFoodAdapter2;
import com.humorstech.respyr.daily_routine.added.AddedFoodData;
import com.humorstech.respyr.daily_routine.search.FoodDataBase;
import com.humorstech.respyr.utills.ProfileCreationData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Food extends AppCompatActivity {

    HashMap<String, String> foodItems = new HashMap<>();
    private final StringBuilder stringBuilder = new StringBuilder();
    private final StringBuilder stringBuilder2 = new StringBuilder();
    private int foodCount=0;

    private Button buttonNext;
    private Button buttonBack;


    private boolean isBreakfast=true;
    private boolean isLunch=true;
    private boolean isDinner=true;



    private final HashMap<String, String> foodQuantitiesList = new HashMap<>();
    private final HashMap<String, String> foodCatList = new HashMap<>();
    private final HashMap<String, String> foodNameList = new HashMap<>();
    private final HashMap<String, String> foodIdList = new HashMap<>();
    private final HashMap<String, String> foodImageList = new HashMap<>();


    private final HashMap<String, String> foodQuantities = new HashMap<>();
    private final HashMap<String, String> foodCategories = new HashMap<>();
    private final HashMap<String, String> foodIds = new HashMap<>();
    private final HashMap<String, String> foodImageLinks = new HashMap<>();


    private final StringBuilder foodNameBuffer1 = new StringBuilder();
    private final StringBuilder foodNameBuffer2 = new StringBuilder();
    private final StringBuilder foodQuantityBuffer1 = new StringBuilder();
    private final StringBuilder foodQuantityBuffer2 = new StringBuilder();

    private final StringBuilder foodIdBuffer = new StringBuilder();
    private final StringBuilder foodCatBuffer = new StringBuilder();
    private final StringBuilder foodImagesBuffer = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);
        StatusBarColor statusBarColor = new StatusBarColor(Food.this);
        statusBarColor.setColor(getResources().getColor(R.color.white));
        FoodDataBase dataBase=new FoodDataBase(getApplicationContext());
        dataBase.clearTable();

        init();
    }
    private void init(){
        setReferences();
        onClicks();
        performMeal();
    }

    @Override
    protected void onStart() {
        super.onStart();
        performAddedFood();
    }

    private void setReferences(){

        buttonNext =findViewById(R.id.button_next);
        buttonBack =findViewById(R.id.button_back);



        // dots
        LinearLayout linearLayout =findViewById(R.id.dotLayout);
        ProgressDots.generateDotIndicators(getApplicationContext(),linearLayout, 8,6);
    }
    private void onClicks(){
        Button buttonAddMeal =findViewById(R.id.button_add_meal);
        buttonAddMeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Food.this, SearchMeal.class);
                startActivity(intent);
            }
        });


        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               if (foodCount!=0){
                   try {



                       // store gender id for profile creation
                       SharedPreferences sharedPreferences = getSharedPreferences(ProfileCreationData.TITLE, Context.MODE_PRIVATE);
                       SharedPreferences.Editor editor = sharedPreferences.edit();
                       editor.putString(ProfileCreationData.FOOD_INTAKE, String.valueOf(foodCount));
                       editor.putString(ProfileCreationData.FOOD_NAME, String.valueOf(foodNameBuffer1));
                       editor.putString(ProfileCreationData.FOOD_QUANTITY, String.valueOf(foodQuantityBuffer1));
                       editor.putString(ProfileCreationData.BREAKFAST, String.valueOf(isBreakfast));
                       editor.putString(ProfileCreationData.LUNCH, String.valueOf(isDinner));
                       editor.putString(ProfileCreationData.DINNER, String.valueOf(isLunch));
                       editor.putString(ProfileCreationData.FOOD_CATEGORY, String.valueOf(foodCatBuffer));
                       editor.putString(ProfileCreationData.FOOD_IDS, String.valueOf(foodIdBuffer));
                       editor.putString(ProfileCreationData.FOOD_IMAGES_LINKS, String.valueOf(foodImagesBuffer));




                       editor.apply();


                       Intent intent = new Intent(Food.this, SleepHours.class);
                       startActivity(intent);


                   }catch (ActivityNotFoundException e){

                   }
               }else{
                   Snackbar.make(v, "Please fill out the complete form.", Snackbar.LENGTH_SHORT).show();
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
    private void performAddedFood(){


        foodNameBuffer1.setLength(0);
        foodNameBuffer2.setLength(0);
        foodQuantityBuffer1.setLength(0);
        foodQuantityBuffer2.setLength(0);
        foodIdBuffer.setLength(0);
        foodCatBuffer.setLength(0);
        foodImagesBuffer.setLength(0);


        RecyclerView recyclerView4 = findViewById(R.id.list_added_food_items);
        recyclerView4.setLayoutManager(new LinearLayoutManager(this));

        AddedFoodAdapter2 addedFoodAdapter;
        List<AddedFoodData> foodList;
        FoodDataBase database;

        // Initialize the FoodDataBase
        database = new FoodDataBase(getApplicationContext());

        foodList = database.getAllFood();



        // Create and set the adapter
        addedFoodAdapter = new AddedFoodAdapter2(foodList, getApplicationContext());
        recyclerView4.setAdapter(addedFoodAdapter);


        LinearLayout emptyListMessage =findViewById(R.id.empty_list_message);
        int dataCount = database.getDataCount();





        for (AddedFoodData foodData : foodList) {


            String foodName = foodData.getFoodName();
            String foodQuantity = foodData.getFoodQuantity();
            String cat = foodData.getFoodCategory();
            int id = foodData.getId();
            String images = foodData.getImageLink1();

            foodItems.put(foodName, foodQuantity);

            foodNameList.put(foodName, foodName);

            foodQuantitiesList.put(foodName, foodQuantity);
            foodCatList.put(foodName, cat);
            foodIdList.put(foodName, String.valueOf(id));
            foodImageList.put(foodName, images);
        }

        int index=0;
        for (Map.Entry<String, String> entry : foodItems.entrySet()) {
            String foodName = entry.getKey().trim();
            String quantity = entry.getValue().trim();
            foodNameBuffer1.append("food_name_").append(index).append("=").append(foodName).append("&");
            foodNameBuffer2.append("food_name_").append(index).append("=").append(foodName).append("!");
            foodQuantityBuffer1.append("food_quantity_").append(index).append("=").append(quantity).append("&");
            foodQuantityBuffer2.append("food_quantity_").append(index).append("=").append(quantity).append("!");
            index++;
        }

        foodCount = foodItems.size();


        int index2 =0;
        for (Map.Entry<String, String> entry : foodIdList.entrySet()) {
            String id = entry.getValue().trim();
            foodIdBuffer.append("food_id_").append(index2).append("=").append(id).append("!");
            index2++;
        }

        int index3 =0;
        for (Map.Entry<String, String> entry : foodCatList.entrySet()) {
            String foodCat = entry.getValue().trim();
            foodCatBuffer.append("food_cat_").append(index3).append("=").append(foodCat).append("!");
            index3++;
        }

        int index4 =0;
        for (Map.Entry<String, String> entry : foodImageList.entrySet()) {
            String foodImage = entry.getValue().trim();
            foodImagesBuffer.append("food_image_").append(index4).append("=").append(foodImage).append("!");
            index4++;
        }




        if (dataCount == 0){
            emptyListMessage.setVisibility(View.VISIBLE);
            recyclerView4.setVisibility(View.GONE);
        }else{
            emptyListMessage.setVisibility(View.GONE);
            recyclerView4.setVisibility(View.VISIBLE);
        }


    }

    private void performMeal(){
        CheckBox chkBreakfast=findViewById(R.id.chk_breakfast);
        CheckBox chkLunch=findViewById(R.id.chk_lunch);;
        CheckBox chkDinner=findViewById(R.id.chk_dinner);


        chkBreakfast.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Checkbox is checked
                    isBreakfast=false;
                } else {
                    // Checkbox is unchecked
                    isBreakfast=true;
                }
            }
        });

        chkLunch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Checkbox is checked
                    isLunch=false;
                } else {
                    // Checkbox is unchecked
                    isLunch=true;
                }
            }
        });

        chkDinner.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Checkbox is checked
                    isDinner=false;
                } else {
                    // Checkbox is unchecked
                    isDinner=true;
                }
            }
        });


    }
}