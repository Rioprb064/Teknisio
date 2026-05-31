package com.teknisio.app.ui.onboarding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.teknisio.app.R;

public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.SlideViewHolder> {

    private final int[] images;
    private int currentPage = 0;

    public OnboardingAdapter(int[] images) {
        this.images = images;
    }

    public void setCurrentPage(int page) {
        this.currentPage = page;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SlideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_onboarding_slide, parent, false);
        return new SlideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SlideViewHolder holder, int position) {
        holder.imageView.setImageResource(images[position]);
        
        // Build indicator dots
        holder.indicatorContainer.removeAllViews();
        for (int i = 0; i < images.length; i++) {
            View dot = new View(holder.itemView.getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(24, 24);
            params.setMargins(8, 0, 8, 0);
            dot.setLayoutParams(params);
            if (i == currentPage) {
                dot.setBackgroundResource(R.drawable.indicator_active);
            } else {
                dot.setBackgroundResource(R.drawable.indicator_inactive);
            }
            holder.indicatorContainer.addView(dot);
        }
    }

    @Override
    public int getItemCount() {
        return images.length;
    }

    static class SlideViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        LinearLayout indicatorContainer;

        SlideViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_slide_image);
            indicatorContainer = itemView.findViewById(R.id.indicator_container);
        }
    }
}
