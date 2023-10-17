package com.humorstech.respyr.reading;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chinalwb.slidetoconfirmlib.ISlideListener;
import com.chinalwb.slidetoconfirmlib.SlideToConfirm;
import com.humorstech.respyr.R;
import com.humorstech.respyr.StatusBarColor;
import com.humorstech.respyr.dashboard.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class CheckList extends AppCompatActivity {

    CheckBox chkSelectAll, chk1, chk2, chk3, chk4, chk5,chk6;

    ArrayList<Integer> checkListUnCheckedList = new ArrayList<>();

    private LinearLayout llCheckListLayout, llCheckListWarningLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_list);
        StatusBarColor statusBarColor= new StatusBarColor(CheckList.this);
        statusBarColor.setColor(getResources().getColor(R.color.white));


        init();



    }

    private void init(){
        initVars();
        setTextCustom();
        performCheckBox();
        onClicks();


    }
    private void initVars(){
         chkSelectAll=findViewById(R.id.chk_select_all);
         chk1=findViewById(R.id.chk_1);
         chk2=findViewById(R.id.chk_2);
         chk3=findViewById(R.id.chk_3);
         chk4=findViewById(R.id.chk_4);
         chk5=findViewById(R.id.chk_5);
         chk6=findViewById(R.id.chk_6);

        llCheckListLayout=findViewById(R.id.checklist_layout);
        llCheckListWarningLayout=findViewById(R.id.ll_checklist_warning_layout);

    }

    private void onClicks(){
        ImageButton buttonCancel=findViewById(R.id.button_cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CancelTest.show(CheckList.this, CheckList.this);
            }
        });


        ////////////////////////// next


        final SlideToConfirm slideToConfirm = findViewById(R.id.slide_to_confirm_1);
        slideToConfirm.setSlideListener(new ISlideListener() {
            @Override
            public void onSlideStart() {
            }

            @Override
            public void onSlideMove(float percent) {
            }

            @Override
            public void onSlideCancel() {
            }

            @Override
            public void onSlideDone() {

                slideToConfirm.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        performCheckLists();
                    }
                }, 500);
            }
        });

        Button buttonContinueAnyWay=findViewById(R.id.button_continue_anyway);
        Button buttonILLTakeLater=findViewById(R.id.button_ill_take_later);
        buttonContinueAnyWay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DeviceReady.class);
                startActivity(intent);
                finish();
            }
        });
        buttonILLTakeLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void performCheckLists(){

        if(chkSelectAll.isChecked()){

            Intent intent = new Intent(getApplicationContext(), DeviceReady.class);
            startActivity(intent);
            finish();


        }else{
            CheckBox[] checkboxes = {chk1, chk2, chk3, chk4, chk5, chk6};
            ArrayList<Integer> checkListUnCheckedList = new ArrayList<>();

            for (int i = 0; i < checkboxes.length; i++) {
                if (!checkboxes[i].isChecked()) {
                    checkListUnCheckedList.add(i);
                }
            }




            int length = checkListUnCheckedList.size();


            if (length==0){
                Intent intent = new Intent(getApplicationContext(), DeviceReady.class);
                startActivity(intent);
                finish();
            }else{


                setWarningLists(checkListUnCheckedList);

                StatusBarColor statusBarColor= new StatusBarColor(CheckList.this);
                statusBarColor.setColor(getResources().getColor(R.color.warning_light));
                llCheckListLayout.setVisibility(View.GONE);
                llCheckListWarningLayout.setVisibility(View.VISIBLE);
            }
        }



    }

    private void setWarningLists(ArrayList<Integer> warningList){
        GridView gridView = findViewById(R.id.gridview);


        // Create a custom adapter and set it for the GridView
        CheckListAdapter adapter = new CheckListAdapter(this, warningList);
        gridView.setAdapter(adapter);
    }

    private void setTextCustom(){

        TextView txtCheckList1=findViewById(R.id.txt_checklist_1);
        TextView txtCheckList2=findViewById(R.id.txt_checklist_2);
        TextView txtCheckList3=findViewById(R.id.txt_checklist_3);
        TextView txtCheckList4=findViewById(R.id.txt_checklist_4);
        TextView txtCheckList5=findViewById(R.id.txt_checklist_5);
        TextView txtCheckList6=findViewById(R.id.txt_checklist_6);




        String checklist1 = "<b>I haven't eaten in the</b><font color=\"#308BF9\">" + " last four hours." + "</font> ";
        String checklist2 = "<b>I haven’t consumed water</b> or any other fluid in <font color=\"#308BF9\"> last one hour.</font>";
        String checklist3 = "<b>I haven't consumed any alcohol</b> in<font color=\"#308BF9\">" + " last 24 hours." + "</font>";
        String checklist4 = "<b>I haven't smoked </b> any type of tobacco in <font color=\"#308BF9\">" + " last four hours. " + "</font>";
        String checklist5 = "<b>I haven’t brushed</b> my teeth in<font color=\"#308BF9\">" + " last one hour." + "</font>";
        String checklist6 = " I am <b>sitting relaxed</b> to take the test";


        Spanned newCheckList1 = Html.fromHtml(checklist1);
        Spanned newCheckList2 = Html.fromHtml(checklist2);
        Spanned newCheckList3 = Html.fromHtml(checklist3);
        Spanned newCheckList4 = Html.fromHtml(checklist4);
        Spanned newCheckList5 = Html.fromHtml(checklist5);
        Spanned newCheckList6 = Html.fromHtml(checklist6);


        txtCheckList1.setText(newCheckList1);
        txtCheckList2.setText(newCheckList2);
        txtCheckList3.setText(newCheckList3);
        txtCheckList4.setText(newCheckList4);
        txtCheckList5.setText(newCheckList5);
        txtCheckList6.setText(newCheckList6);





    }
    private void performCheckBox(){
        chkSelectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    chk1.setChecked(true);
                    chk2.setChecked(true);
                    chk3.setChecked(true);
                    chk4.setChecked(true);
                    chk5.setChecked(true);
                    chk6.setChecked(true);
                }else{
                    chk1.setChecked(false);
                    chk2.setChecked(false);
                    chk3.setChecked(false);
                    chk4.setChecked(false);
                    chk5.setChecked(false);
                    chk6.setChecked(false);
                }
            }
        });

    }
}