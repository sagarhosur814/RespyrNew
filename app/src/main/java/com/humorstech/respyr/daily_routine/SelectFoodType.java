package com.humorstech.respyr.daily_routine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.humorstech.respyr.Animatoo1;
import com.humorstech.respyr.R;
import com.humorstech.respyr.StatusBarColor;
import com.humorstech.respyr.daily_routine.added.AddedFoodAdapter2;
import com.humorstech.respyr.daily_routine.added.AddedFoodAdapter3;
import com.humorstech.respyr.daily_routine.added.AddedFoodData;
import com.humorstech.respyr.daily_routine.search.FoodDataBase;
import com.humorstech.respyr.reading.BlowActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectFoodType extends AppCompatActivity {








    private static final String TAG = "DailyRoutine";
    private String loginId, profileId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_food_type);

        StatusBarColor statusBarColor= new StatusBarColor(SelectFoodType.this);
        statusBarColor.setColor(getResources().getColor(R.color.white));


        FoodDataBase dataBase=new FoodDataBase(getApplicationContext());



        init();
    }

    private void init(){
        onClicks();
    }

    private void initVars(){

    }

    @Override
    protected void onStart() {
        super.onStart();
        performAddedFood();
    }

    private void onClicks(){
        ImageButton buttonAddBreakfast =findViewById(R.id.button_add_breakfast);
        buttonAddBreakfast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectFoodType.this, SearchMeal.class);
                intent.putExtra("foodType", "Breakfast");
                startActivity(intent);
                Animatoo1.animateSlideLeft(SelectFoodType.this);
            }
        });


        ImageButton buttonAddLunch=findViewById(R.id.button_add_lunch);
        buttonAddLunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectFoodType.this, SearchMeal.class);
                intent.putExtra("foodType", "Lunch");
                startActivity(intent);
                Animatoo1.animateSlideLeft(SelectFoodType.this);
            }
        });

        ImageButton buttonAddSnacks=findViewById(R.id.button_add_snacks);
        buttonAddSnacks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectFoodType.this, SearchMeal.class);
                intent.putExtra("foodType", "Snacks");
                startActivity(intent);
                Animatoo1.animateSlideLeft(SelectFoodType.this);
            }
        });
        ImageButton buttonAddDinner =findViewById(R.id.button_add_dinner);
        buttonAddDinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectFoodType.this, SearchMeal.class);
                intent.putExtra("foodType", "Dinner");
                startActivity(intent);
                Animatoo1.animateSlideLeft(SelectFoodType.this);
            }
        });
    }


    private void performAddedFood(){


        RecyclerView recyclerView1 = findViewById(R.id.list_added_breakfast_food_items);
        RecyclerView recyclerView2 = findViewById(R.id.list_added_lunch_food_items);
        RecyclerView recyclerView3 = findViewById(R.id.list_added_snaks_food_items);
        RecyclerView recyclerView4 = findViewById(R.id.list_added_dinner_food_items);


        recyclerView1.setLayoutManager(new LinearLayoutManager(this));

        AddedFoodAdapter3 addedFoodAdapter1;
        AddedFoodAdapter3 addedFoodAdapter2;
        AddedFoodAdapter3 addedFoodAdapter3;
        AddedFoodAdapter3 addedFoodAdapter4;


        // Initialize the FoodDataBase
        FoodDataBase database = new FoodDataBase(getApplicationContext());

        List<AddedFoodData>  foodList1 = database.getFoodByType("Breakfast");
        List<AddedFoodData>  foodList2 = database.getFoodByType("Lunch");
        List<AddedFoodData>  foodList3 = database.getFoodByType("Snacks");
        List<AddedFoodData>  foodList4 = database.getFoodByType("Dinner");



        // Create and set the adapter
        addedFoodAdapter1 = new AddedFoodAdapter3(foodList1, getApplicationContext());
        addedFoodAdapter2 = new AddedFoodAdapter3(foodList2, getApplicationContext());
        addedFoodAdapter3 = new AddedFoodAdapter3(foodList3, getApplicationContext());
        addedFoodAdapter4 = new AddedFoodAdapter3(foodList4, getApplicationContext());


       recyclerView1.setAdapter(addedFoodAdapter1);
        recyclerView2.setAdapter(addedFoodAdapter2);
//        recyclerView3.setAdapter(addedFoodAdapter3);
//        recyclerView4.setAdapter(addedFoodAdapter4);


    }

}