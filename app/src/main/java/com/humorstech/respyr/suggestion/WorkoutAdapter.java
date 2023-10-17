package com.humorstech.respyr.suggestion;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.humorstech.respyr.R;

import java.util.List;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.ViewHolder> {

    private List<WorkoutSuggestionDataModel> workoutList;

    public WorkoutAdapter(List<WorkoutSuggestionDataModel> workoutList) {
        this.workoutList = workoutList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.suggestion_page_workout_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WorkoutSuggestionDataModel workout = workoutList.get(position);
        holder.textViewName.setText(workout.getWorkoutName());
        holder.textViewDuration.setText(workout.getWorkoutTiming());
    }

    @Override
    public int getItemCount() {
        return workoutList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        TextView textViewDuration;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.txt_work_out_name);
            textViewDuration = itemView.findViewById(R.id.txt_work_out_time);
        }
    }
}
