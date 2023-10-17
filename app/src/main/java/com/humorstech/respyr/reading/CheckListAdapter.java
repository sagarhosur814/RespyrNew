package com.humorstech.respyr.reading;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.humorstech.respyr.R;
import com.humorstech.respyr.utills.Stuffs;

import java.util.ArrayList;

public class CheckListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Integer> gridItems;


    public CheckListAdapter(Context context, ArrayList<Integer> gridItems) {
        this.context = context;
        this.gridItems = gridItems;
    }

    @Override
    public int getCount() {
        return gridItems.size();
    }

    @Override
    public Object getItem(int position) {
        return gridItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View gridView;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            gridView = new View(context);
            gridView = inflater.inflate(R.layout.checklist_item, null);

            ImageView imageView = gridView.findViewById(R.id.imageView);
            TextView textView = gridView.findViewById(R.id.textView);


            try {


                // Assuming getImageResource() and getCheckListName() are defined elsewhere
                int imageRes = getImageResource(gridItems.get(position));
                String checkListName = getCheckListName(gridItems.get(position));

                imageView.setImageResource(imageRes);
                textView.setText(checkListName);
            } catch (Exception e) {
                e.printStackTrace(); // Handle the exception as needed
                gridView = new View(context); // Create an empty view to avoid crashes
                textView.setText(e.getMessage());
            }
        } else {
            gridView = convertView;
        }

        return gridView;
    }


    private String getCheckListName(int i){

        switch (i){
            case 0: return Stuffs.checkListNames[0];
            case 1: return Stuffs.checkListNames[1];
            case 2: return Stuffs.checkListNames[2];
            case 3: return Stuffs.checkListNames[3];
            case 4: return Stuffs.checkListNames[4];
            case 5: return Stuffs.checkListNames[5];
            default:return Stuffs.checkListNames[0];
        }
    }

    private int getImageResource(int i){
        switch (i){
            case 0: return Stuffs.checkListImages[0];
            case 1: return Stuffs.checkListImages[1];
            case 2: return Stuffs.checkListImages[2];
            case 3: return Stuffs.checkListImages[3];
            case 4: return Stuffs.checkListImages[4];
            case 5: return Stuffs.checkListImages[5];
            default:return Stuffs.checkListImages[0];
        }
    }

}
