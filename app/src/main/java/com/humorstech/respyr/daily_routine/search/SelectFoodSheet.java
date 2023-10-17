package com.humorstech.respyr.daily_routine.search;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.humorstech.respyr.Dialogs;
import com.humorstech.respyr.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shawnlin.numberpicker.NumberPicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;


public class SelectFoodSheet {

    String foodName;
    String foodCategory;
    String imageLinkSmall;
    String foodImageLinkMain;

    Context context;
    Activity activity;

    BottomSheetDialog bsDialog;
    TextView txtSelectedFoodName;
    TextView txtGramsContains;
    LinearLayout selectedFoodBackground;
    ImageButton buttonBack;
    int foodAmount;
    NumberPicker foodAmountPicker;

    String[] stringArray;
    int stringArrayCount;
    int foodCount;
    String servingType;
    String foodType;

    private  boolean isRead=false;

    private RadioButton radioServingType;

    SelectFoodSheet(Context context, Activity activity, String foodType) {
        this.context = context;
        this.activity = activity;
        this.foodName = "-";
        this.foodCategory = "-";
        this.imageLinkSmall = "-";
        this.foodImageLinkMain = "-";
        this.foodCount=1;
        this.foodType=foodType;
    }


    int selectedFoodAmount=0;
    ArrayList<String> categoriesList = new ArrayList<>();

    @SuppressLint("SetTextI18n")
    public void show(int id) {
        bsDialog = new BottomSheetDialog(context, R.style.TransparentDialog);
        bsDialog.setContentView(R.layout.daily_routin_select_food);
        bsDialog.getWindow().setNavigationBarColor(context.getColor(R.color.white));

        txtSelectedFoodName = bsDialog.findViewById(R.id.txt_selected_food_name);
        selectedFoodBackground = bsDialog.findViewById(R.id.selected_food_bg);
        buttonBack = bsDialog.findViewById(R.id.button_back);
        txtGramsContains = bsDialog.findViewById(R.id.txt_grams_contains);
        radioServingType = bsDialog.findViewById(R.id.radio_serving_type);

        // Fetch data
        fetchFoodData(id);

        FoodDataBase database = new FoodDataBase(context);
        int dataCount = database.getDataCount();


        onClicks();
    }

    private void onClicks() {
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bsDialog.dismiss();
            }
        });
    }
    private void fetchFoodData(int id) {
        RequestParams params = new RequestParams();
        params.put("id", id);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get("https://humorstech.com/humors_app/app_final/food/fetch_food_data_by_id.php?", params,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        // Called before request is started
                        Dialogs.showLoadingDialog(context, "Please wait...");
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Dialogs.hideLoadingDialog();

                        String response = new String(responseBody);
                        try {
                            JSONArray jsonArray = new JSONArray(response);

                            // Iterate over each object in the array
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                // Extract values from the JSON object
                                int id = jsonObject.getInt("id");
                                foodName = jsonObject.getString("food_name");
                                foodCategory = jsonObject.getString("food_category");
                                String imageLinkSmall = jsonObject.getString("image_link");
                                String foodImageLinkMain = jsonObject.getString("image_link2");

                                // Set the data in the UI
                                setData(id, foodName, foodCategory, imageLinkSmall, foodImageLinkMain);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    }


                });
    }


    private void setData(int id, String foodName, String foodCategory, String imageLinkSmall, String foodImageLinkMain) {
        // Set TextView
        txtSelectedFoodName.setText(foodName);


        //
        fetchScaleTypes(id,foodCategory,foodName);

        // Set background image using Glide library
        String imageUrl = "https://humorstech.com/humors_app/app_final/" + foodImageLinkMain;
        RequestOptions requestOptions = new RequestOptions().centerCrop();
        Glide.with(context)
                .load(imageUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(requestOptions)
                .placeholder(R.drawable.food_bg_not_found)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        selectedFoodBackground.setBackground(resource);
                    }
                });

        bsDialog.show();

        LinearLayout addFoodToDB = bsDialog.findViewById(R.id.button_add_food_to_db);
        addFoodToDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                boolean isInsert = insertDataIntoSQLite(id, foodName, String.valueOf(selectedFoodAmount), foodCategory, imageLinkSmall);

                if (isInsert){
                    bsDialog.dismiss();
                    activity.recreate();
                }else{
                    Toast.makeText(context, "Food is already selected.", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }



    private boolean insertDataIntoSQLite(int id, String foodName, String foodAmount, String foodCategory, String imageLink) {
        // Insert the data into the SQLite database
        // Replace with your SQLite database insertion logic
        FoodDataBase dbHelper = new FoodDataBase(context);
        boolean isInserted = dbHelper.insertFoodItem(
                id,
                foodName,
                foodCategory,
                foodAmount,
                imageLink,
                "-",
                foodType);

        if (isInserted) {
            return true;
        } else {
            return false;
        }
    }


    private void fetchScaleTypes(int id, String foodCategory,String foodName){

        RequestParams params = new RequestParams();
        params.put("food_name", foodName);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get("https://humorstech.com/humors_app/app_final/food/food_amount2.php?", params,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        // called before request is started
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String responseNew = new String(responseBody);
                        try {
                            JSONArray jsonArray = new JSONArray(responseNew);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String servingTypeStr = jsonObject.getString("Serving");
                                String foodNameStr = jsonObject.getString("Food");
                                String servingQuantityInGramsStr = jsonObject.getString("Average");


                                if (servingTypeStr.contains("unit")){

                                    radioServingType.setText(foodNameStr);
                                    radioServingType.setChecked(true);
                                }else{
                                    radioServingType.setText(servingTypeStr);
                                    radioServingType.setChecked(true);
                                }

                                performFoodQuantity(id, foodNameStr, servingTypeStr, servingQuantityInGramsStr);

                            }



                        } catch (JSONException e) {

                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();

                            e.printStackTrace();


                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    }


                    @Override
                    public void onRetry(int retryNo) {
                        // called when request is retried
                    }
                });


    }

    private void performFoodQuantity(int foodId,String foodName, String servingTypeStr, String servingQuantityStr) {


        Typeface typeface = ResourcesCompat.getFont(context, R.font.roboto_bold);
        Typeface typeface2 = ResourcesCompat.getFont(context, R.font.roboto);


        foodAmountPicker = bsDialog.findViewById(R.id.food_amount_picker);
        assert foodAmountPicker != null;
        foodAmountPicker.setTypeface(typeface2);
        foodAmountPicker.setSelectedTypeface(typeface);
        foodAmountPicker.setMinValue(1);
        foodAmountPicker.setMaxValue(25);

        fetchAFoodAmount(foodId, foodName, servingTypeStr, servingQuantityStr, foodCount);




        foodAmountPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                foodCount = newVal;
               // servingType= stringArray[stringArrayCount];

                System.out.println(servingType);
                fetchAFoodAmount(foodId, foodName, servingTypeStr, servingQuantityStr, foodCount);
            }
        });



    }
    private void fetchAFoodAmount(int foodId,String foodName, String servingTypeStr, String servingQuantityStr, int selectedAmount){
        if (!isRead){

            int servingQuantityIn100Grams = Integer.parseInt(servingQuantityStr);
             selectedFoodAmount = servingQuantityIn100Grams * selectedAmount;
            txtGramsContains.setText(String.valueOf(selectedFoodAmount) + " Grams");



        }
    }
}
