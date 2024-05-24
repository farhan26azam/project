package com.university.treklogger.screens;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.university.treklogger.R;
import com.university.treklogger.entities.Point;

import java.util.List;

public class PointsAdapter extends RecyclerView.Adapter<PointsAdapter.PointViewHolder> {

    private List<Point> points;

    public PointsAdapter(List<Point> points) {
        this.points = points;
    }

    @NonNull
    @Override
    public PointViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_point, parent, false);
        return new PointViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PointViewHolder holder, int position) {
        Point point = points.get(position);
        holder.pointNameTextView.setText(point.getName());
    }

    @Override
    public int getItemCount() {
        return points.size();
    }

    static class PointViewHolder extends RecyclerView.ViewHolder {

        TextView pointNameTextView;

        public PointViewHolder(@NonNull View itemView) {
            super(itemView);
            pointNameTextView = itemView.findViewById(R.id.pointNameTextView);
        }
    }
}
