package com.humorstech.respyr.daily_routine;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.humorstech.respyr.R;
import com.humorstech.respyr.StatusBarColor;
import com.humorstech.respyr.daily_routine.added.AddedFoodAdapter;
import com.humorstech.respyr.daily_routine.added.AddedFoodData;
import com.humorstech.respyr.daily_routine.search.FrequentlyFoodAdapter;
import com.humorstech.respyr.daily_routine.search.FrequentlyFoodData;
import com.humorstech.respyr.daily_routine.search.FoodDataBase;
import com.humorstech.respyr.daily_routine.search.SearchFoodData;
import com.humorstech.respyr.daily_routine.search.SearchFoodDataAdapter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.techiness.progressdialoglibrary.ProgressDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class SearchMeal extends AppCompatActivity{

    private EditText searchEditText;
    private Button searchButton;
    private RecyclerView recyclerView;
    private SearchFoodDataAdapter foodAdapter;
    private List<SearchFoodData> foodList;
    private List<SearchFoodData> filteredFoodList;
    private LinearLayout searchedFoodItemLayout;
    private TextView txtSearchedFoodNotFound;
    private ImageButton buttonClearEditText;

    private LinearLayout llFoodNotFoundLayout;
    private TextView txtNotFoundFoodName;

    private ImageButton buttonBack;


    private String foodType;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_meal);
        StatusBarColor statusBarColor = new StatusBarColor(SearchMeal.this);
        statusBarColor.setColor(getResources().getColor(R.color.white));






        Intent intent = getIntent();
        foodType = intent.getStringExtra("foodType");
        TextView textView = findViewById(R.id.txt_appbar_title);
        textView.setText("Add "+foodType);


        init();

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void refreshLayout(){
        searchEditText.setText("");
        performFrequentlyAddedFood();
        performAddedFood();
        fetchFoodItemsFromServer();
    }


    private void init() {
        setVars();
        onClicks();
        refreshLayout();

        foodList = new ArrayList<>();
        filteredFoodList = new ArrayList<>();
        foodAdapter = new SearchFoodDataAdapter(filteredFoodList, getApplicationContext(), SearchMeal.this, foodType);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchMeal.this));
        recyclerView.setAdapter(foodAdapter);


        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.toString().toLowerCase().trim().length() >= 3){
                    performSearch(s.toString().toLowerCase().trim());
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (searchEditText.getText().toString().isEmpty()){
                    searchedFoodItemLayout.setVisibility(View.GONE);
                    buttonClearEditText.setVisibility(View.GONE);
                }else{
                    buttonClearEditText.setVisibility(View.VISIBLE);
                }
            }
        });

        buttonClearEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEditText.setText("");
            }
        });


    }

    private void onClicks(){
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setVars() {
        searchEditText = findViewById(R.id.search_food_edit_text);
        recyclerView = findViewById(R.id.list_search_food_items);
        searchedFoodItemLayout = findViewById(R.id.searched_food_item_layout);
        searchedFoodItemLayout.setVisibility(View.GONE);
        txtSearchedFoodNotFound = findViewById(R.id.txt_food_not_found);
        buttonClearEditText = findViewById(R.id.button_clear_edit_text);
        llFoodNotFoundLayout = findViewById(R.id.ll_food_not_found);
        txtNotFoundFoodName = findViewById(R.id.txt_not_found_food_name);
        buttonBack = findViewById(R.id.button_back);

        searchEditText.setText("");
    }






    private void fetchFoodItemsFromServer() {
        String url = "https://humorstech.com/humors_app/app_final/food/fetch_food2.php";

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                // You can show a loading indicator here if needed
                showLoading();

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                dismissLoadingProgress();
                if (foodList != null) {
                    List<SearchFoodData> foodItems = parseFoodItemsFromJson(response);

                    if (foodItems != null) {
                        foodList.clear(); // Clear the existing list
                        foodList.addAll(foodItems);
                        foodAdapter.notifyDataSetChanged();
                    } else {
                        Log.e("FoodItems", "Parsing JSON failed or resulted in null foodItems");
                        showError("Failed to parse data. Please try again later.");
                    }
                } else {
                    Log.e("FoodList", "foodList is null");
                    handleException("Internal error occurred. Please try again later.");
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                // Handle different types of errors and show appropriate messages to the user
                if (statusCode == 404) {
                    handleException("Resource not found. Please try again later.");
                } else if (statusCode == 500) {
                    handleException("Internal server error. Please try again later.");
                } else {
                    handleException("Failed to fetch data. Please check your internet connection and try again.");
                }
            }


            @Override
            public void onFinish() {
                super.onFinish();
                // Hide the loading indicator here if you showed it onStart()
            }
        });
    }

    private void showError(String message) {
        // Show an error message to the user using Toast or any other UI element
        Toast.makeText(SearchMeal.this, message, Toast.LENGTH_SHORT).show();
    }


    private void handleException(String s) {
        showErrorDialog("An error occurred: " + s);
    }
    private void showErrorDialog(String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage(errorMessage);
        // Set a positive button with a click listener
        builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                fetchFoodItemsFromServer();
            }
        });
        // Disable outside touch dismissal
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }


    private List<SearchFoodData> parseFoodItemsFromJson(JSONArray jsonArray) {
        List<SearchFoodData> foodItems = new ArrayList<>();

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String foodName = jsonObject.getString("food_name");

                int id = jsonObject.getInt("id");

                String foodCategory = jsonObject.getString("food_category");
                String imageLink = jsonObject.getString("image_link");

                String imagePath = "https://humorstech.com/humors_app/app_final/";
                imageLink = imagePath + imageLink;

                SearchFoodData foodItem = new SearchFoodData(id, foodName, foodCategory, imageLink);
                foodItems.add(foodItem);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return foodItems;
    }

    private void performSearch(String query) {
        filteredFoodList.clear(); // Clear the existing filtered list

        // Iterate through the foodList and add items that match the search query
        for (SearchFoodData foodItem : foodList) {
            String foodName = foodItem.getFoodName().toLowerCase();

            if (foodName.contains(query)) {
                filteredFoodList.add(foodItem);
            }
        }

        foodAdapter.notifyDataSetChanged(); // Notify the adapter of the data change

        // Show or hide the RecyclerView based on the filteredFoodList size
        if (filteredFoodList.isEmpty()) {
            searchedFoodItemLayout.setVisibility(View.GONE); // Hide the RecyclerView
            llFoodNotFoundLayout.setVisibility(View.VISIBLE); // Show the "Not found" TextView
        } else {
            searchedFoodItemLayout.setVisibility(View.VISIBLE); // Show the RecyclerView
            llFoodNotFoundLayout.setVisibility(View.GONE); // Hide the "Not found" TextView
        }
    }

    private void performFrequentlyAddedFood(){
        // Step 1: Create the item list
        List<FrequentlyFoodData> itemList2 = new ArrayList<>();
        itemList2.add(new FrequentlyFoodData(1, "Rice", "Grains", "100 g", ""));
        itemList2.add(new FrequentlyFoodData(31, "Dal Mixed", "Dal", "100 g", ""));
        itemList2.add(new FrequentlyFoodData(63, "Roti", "Breads", "100 g", ""));
        itemList2.add(new FrequentlyFoodData(36, "Idli", "Breakfast", "100 g", ""));

        // Step 2: Create the ItemAdapter
        FrequentlyFoodAdapter adapter = new FrequentlyFoodAdapter(SearchMeal.this, itemList2);

        // Step 3: Set the adapter on the RecyclerView
        RecyclerView recyclerView2 = findViewById(R.id.list_frequently_added_food_items);
        recyclerView2.setLayoutManager(new LinearLayoutManager(getApplicationContext())); // Set the layout manager
        recyclerView2.setAdapter(adapter);
    }

    @SuppressLint("SetTextI18n")
    private void performAddedFood(){

        RecyclerView recyclerView4 = findViewById(R.id.list_added_food_items1);
        recyclerView4.setLayoutManager(new LinearLayoutManager(this));

        AddedFoodAdapter addedFoodAdapter;
        List<AddedFoodData> foodList;
        FoodDataBase database;
        foodList = new ArrayList<>();

        // Initialize the FoodDataBase
        database = new FoodDataBase(getApplicationContext());

        foodList = database.getFoodByType(foodType);


        // Create and set the adapter
        addedFoodAdapter = new AddedFoodAdapter(foodList, getApplicationContext());
        recyclerView4.setAdapter(addedFoodAdapter);



        int dataCount = database.getDataCountByType(foodType);


        LinearLayout addedFoodLayout =findViewById(R.id.added_food_item_layout);
        LinearLayout buttonNext =findViewById(R.id.button_next);



        TextView foodDataCount =findViewById(R.id.txt_data_count);
        TextView txtSelectedMeals =findViewById(R.id.txt_selected_meals);

        if (dataCount >=1){
            addedFoodLayout.setVisibility(View.VISIBLE);
            buttonNext.setVisibility(View.VISIBLE);
            foodDataCount.setText("Add Meal ("+dataCount+")");
            txtSelectedMeals.setText("Selected ("+dataCount+")");
        }else{
            addedFoodLayout.setVisibility(View.GONE);
            buttonNext.setVisibility(View.GONE);
        }

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }


    private void showLoading(){
        progressDialog = new ProgressDialog(this);
        progressDialog.show();
    }


    private void dismissLoadingProgress(){
        progressDialog.dismiss();
    }



}
