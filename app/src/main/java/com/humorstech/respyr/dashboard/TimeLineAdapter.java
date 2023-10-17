package com.humorstech.respyr.dashboard;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.humorstech.respyr.R;
import com.humorstech.respyr.history.ViewResult;

import java.util.List;

public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineAdapter.ViewHolder>{
    private List<TimelineItem> healthDataList;

    public   int unHealthy = 60;
    public   int moderate = 80;
    public   int healthy = 100;


    private Context context;
    private Activity activity;
    private String dataKey;
    public TimeLineAdapter(List<TimelineItem> healthDataList, Activity activity, String dataKey) {
        this.healthDataList = healthDataList;
        this.activity = activity;
        this.dataKey = dataKey;
        this.context = activity.getApplicationContext();

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timeline, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TimelineItem healthData = healthDataList.get(position);


        holder.textViewDate.setText(healthData.getTime());
        double score = Double.parseDouble(healthData.getDataType(dataKey));


        holder.txtScore.setText(String.valueOf(roundOff(score)) + "%");

        score = roundOff(score);

        if (score <= 100 && score >= 80) {
            holder.txtScoreStatus.setTextColor(context.getResources().getColor(R.color.green));
            holder.txtScoreStatus.setText("Good!");
        } else if (score >= 71  && score < 80) {
            holder.txtScoreStatus.setTextColor(context.getResources().getColor(R.color.yellow));
            holder.txtScoreStatus.setText("Fair!");
        } else if (score >= 0 && score <= 70) {
           holder.txtScoreStatus.setTextColor(context.getResources().getColor(R.color.red));
            holder.txtScoreStatus.setText("Poor!");
        }

        holder.buttonViewTimeLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity.getApplicationContext(), ViewResult.class);
                intent.putExtra("id", healthData.getId());
                intent.putExtra("profileName", "");
                activity.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return healthDataList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewDate;
        public TextView txtScore;
        public TextView txtScoreStatus;
        // Declare other TextViews here
        ImageButton buttonViewTimeLine;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.eventNameTextView);

            
            txtScore = itemView.findViewById(R.id.txt_score_value);
            txtScoreStatus = itemView.findViewById(R.id.txt_score_status);
            buttonViewTimeLine = itemView.findViewById(R.id.button_view_score);

        }
    }


    private int roundOff(double healthScore){
        double roundedValue = Math.round(healthScore);

        if (healthScore - Math.floor(healthScore) >= 0.5) {
            roundedValue = Math.ceil(healthScore);
        }

        return  (int) roundedValue;
    }



}
