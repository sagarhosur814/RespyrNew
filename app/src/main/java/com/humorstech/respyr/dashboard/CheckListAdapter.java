package com.humorstech.respyr.dashboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.humorstech.respyr.R;

import java.util.List;

public class CheckListAdapter extends RecyclerView.Adapter<CheckListAdapter.ViewHolder> {

    private List<CheckListData> checklistItems;
    private Context context;

    public CheckListAdapter(List<CheckListData> checklistItems, Context context) {
        this.checklistItems = checklistItems;
        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.checklist_card, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CheckListData item = checklistItems.get(position);
      try {

          holder.checklistName.setText(item.getCheckListName());
          holder.checklistId.setText(item.getChecklistId() + "/6");
        //  holder.imageId.setBackgroundResource(item.getImageId());
      }catch (Resources.NotFoundException e){
         // holder.imageId.setBackgroundResource(R.drawable.ic_age1);
          System.out.println(e);
      }
    }

    @Override
    public int getItemCount() {
        return checklistItems.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView checklistName;
        public TextView checklistId;
        public ImageView imageId;

        public ViewHolder(View itemView) {
            super(itemView);
            checklistName = itemView.findViewById(R.id.txt_check_list_name);
            checklistId = itemView.findViewById(R.id.txt_check_list_id);
             imageId = itemView.findViewById(R.id.img_check_list);
        }
    }

}
