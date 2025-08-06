package com.app.pccooker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pccooker.R;
import com.app.pccooker.models.SavedBuild;

import java.util.List;

public class SavedBuildsAdapter extends RecyclerView.Adapter<SavedBuildsAdapter.SavedBuildViewHolder> {

    private Context context;
    private List<SavedBuild> builds;
    private OnBuildActionListener actionListener;

    public interface OnBuildActionListener {
        void onLoadBuild(SavedBuild build);
        void onEditBuild(SavedBuild build);
        void onDeleteBuild(SavedBuild build);
        void onDuplicateBuild(SavedBuild build);
        void onShareBuild(SavedBuild build);
    }

    public SavedBuildsAdapter(Context context, List<SavedBuild> builds, OnBuildActionListener actionListener) {
        this.context = context;
        this.builds = builds;
        this.actionListener = actionListener;
    }

    @NonNull
    @Override
    public SavedBuildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_saved_build, parent, false);
        return new SavedBuildViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedBuildViewHolder holder, int position) {
        SavedBuild build = builds.get(position);
        holder.bind(build);
    }

    @Override
    public int getItemCount() {
        return builds.size();
    }

    class SavedBuildViewHolder extends RecyclerView.ViewHolder {
        private TextView buildNameText, buildDescriptionText, totalCostText;
        private TextView componentCountText, buildTypeText, performanceScoreText;
        private TextView lastModifiedText, mainComponentText;
        private Button loadButton, editButton;
        private ImageButton deleteButton, duplicateButton, shareButton;

        public SavedBuildViewHolder(@NonNull View itemView) {
            super(itemView);
            
            buildNameText = itemView.findViewById(R.id.buildNameText);
            buildDescriptionText = itemView.findViewById(R.id.buildDescriptionText);
            totalCostText = itemView.findViewById(R.id.totalCostText);
            componentCountText = itemView.findViewById(R.id.componentCountText);
            buildTypeText = itemView.findViewById(R.id.buildTypeText);
            performanceScoreText = itemView.findViewById(R.id.performanceScoreText);
            lastModifiedText = itemView.findViewById(R.id.lastModifiedText);
            mainComponentText = itemView.findViewById(R.id.mainComponentText);
            
            loadButton = itemView.findViewById(R.id.loadButton);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            duplicateButton = itemView.findViewById(R.id.duplicateButton);
            shareButton = itemView.findViewById(R.id.shareButton);
        }

        public void bind(SavedBuild build) {
            // Basic info
            buildNameText.setText(build.getBuildName());
            
            String description = build.getDescription();
            if (description != null && !description.trim().isEmpty()) {
                buildDescriptionText.setText(description);
                buildDescriptionText.setVisibility(View.VISIBLE);
            } else {
                buildDescriptionText.setVisibility(View.GONE);
            }
            
            // Cost and components
            totalCostText.setText(build.getFormattedCost());
            componentCountText.setText(build.getComponentCount() + " components");
            
            // Build type and performance
            buildTypeText.setText(build.getBuildType());
            
            if (build.getPerformanceScore() > 0) {
                performanceScoreText.setText(build.getPerformanceGrade());
                performanceScoreText.setVisibility(View.VISIBLE);
            } else {
                performanceScoreText.setVisibility(View.GONE);
            }
            
            // Dates and main component
            lastModifiedText.setText(build.getFormattedDate());
            mainComponentText.setText(build.getMainComponent());
            
            // Action buttons
            loadButton.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onLoadBuild(build);
                }
            });
            
            editButton.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onEditBuild(build);
                }
            });
            
            deleteButton.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onDeleteBuild(build);
                }
            });
            
            duplicateButton.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onDuplicateBuild(build);
                }
            });
            
            shareButton.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onShareBuild(build);
                }
            });
        }
    }
}