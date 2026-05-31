package com.teknisio.app.ui.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.teknisio.app.R;
import com.teknisio.app.data.model.CustomerTechnicianResponse;
import com.teknisio.app.data.model.DeviceCategoryResponse;
import com.teknisio.app.ui.technician.TechnicianDetailActivity;

import java.util.List;

public class TechnicianCarouselAdapter extends RecyclerView.Adapter<TechnicianCarouselAdapter.TechViewHolder> {

    private final List<CustomerTechnicianResponse> dataList;
    private final String selectedDeviceCategoryId;
    private final String selectedDeviceCategoryName;

    private int activeIndex = 0;

    public TechnicianCarouselAdapter(List<CustomerTechnicianResponse> dataList, String selectedDeviceCategoryId, String selectedDeviceCategoryName) {
        this.dataList = dataList;
        this.selectedDeviceCategoryId = selectedDeviceCategoryId;
        this.selectedDeviceCategoryName = selectedDeviceCategoryName;

        if (dataList != null && dataList.size() > 1) {
            activeIndex = 1;
        }
    }

    @NonNull
    @Override
    public TechViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_technician_card, parent, false);
        return new TechViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TechViewHolder holder, int position) {
        CustomerTechnicianResponse technician = dataList.get(position);

        if (activeIndex >= getItemCount()) {
            activeIndex = 0;
        }

        boolean isActive = position == activeIndex;
        Context ctx = holder.itemView.getContext();

        int widthDp = isActive ? 156 : 111;
        int heightDp = isActive ? 156 : 126;
        int width = dpToPx(ctx, widthDp);
        int height = dpToPx(ctx, heightDp);

        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp == null) {
            lp = new RecyclerView.LayoutParams(width, height);
        }

        lp.width = width;
        lp.height = height;

        if (lp instanceof RecyclerView.LayoutParams) {
            int marginDp = isActive ? -8 : 0;
            ((RecyclerView.LayoutParams) lp).setMarginStart(dpToPx(ctx, marginDp));
            ((RecyclerView.LayoutParams) lp).setMarginEnd(dpToPx(ctx, marginDp));
        }

        holder.itemView.setLayoutParams(lp);

        Glide.with(ctx)
            .load(technician.getProfilePhoto())
            .placeholder(R.drawable.ic_profile_placeholder)
            .centerCrop()
            .into(holder.ivPhoto);

        holder.tvName.setText(safe(technician.getName(), "Teknisi"));
        holder.tvName.setTextSize(TypedValue.COMPLEX_UNIT_SP, isActive ? 13 : 10);

        if (isActive) {
            holder.vOverlay.setBackgroundColor(Color.parseColor("#993899A3"));
        } else {
            holder.vOverlay.setBackgroundColor(Color.parseColor("#8094A5A6"));
        }

        holder.layoutTop.setVisibility(isActive ? View.VISIBLE : View.GONE);
        holder.tvPrice.setVisibility(isActive ? View.VISIBLE : View.GONE);
        holder.tvMoreInfo.setVisibility(isActive ? View.VISIBLE : View.GONE);

        String availability = safe(technician.getAvailabilityStatus(), "-");
        holder.tvDistance.setText(availability);

        if (isActive) {
            int totalJobs = technician.getTotalJobs() != null ? technician.getTotalJobs() : 0;
            int ratingCount = technician.getRatingCount() != null ? technician.getRatingCount() : 0;
            double rating = technician.getAverageRating() != null ? technician.getAverageRating() : 0.0;

            holder.tvPrice.setText("Rating " + String.format("%.1f", rating) + " • " + totalJobs + " jobs");

            holder.layoutStars.removeAllViews();

            int fullStars = (int) Math.round(rating);
            if (fullStars > 5) fullStars = 5;
            if (fullStars < 0) fullStars = 0;

            for (int i = 0; i < 5; i++) {
                ImageView star = new ImageView(ctx);
                LinearLayout.LayoutParams slp = new LinearLayout.LayoutParams(
                    dpToPx(ctx, 10),
                    dpToPx(ctx, 10)
                );
                slp.setMarginEnd(dpToPx(ctx, 1));
                star.setLayoutParams(slp);
                star.setImageResource(i < fullStars ? R.drawable.ic_star : R.drawable.ic_star_empty);
                holder.layoutStars.addView(star);
            }

            holder.tvMoreInfo.setText(ratingCount > 0 ? "More Info" : "Detail");

            holder.tvMoreInfo.setOnClickListener(v -> {
                Intent intent = new Intent(ctx, TechnicianDetailActivity.class);

                intent.putExtra("technicianProfileId", technician.getTechnicianProfileId());
                intent.putExtra("deviceCategoryId", selectedDeviceCategoryId);
                intent.putExtra("deviceCategoryName", selectedDeviceCategoryName);

                // Legacy extras supaya TechnicianDetailActivity lama tetap bisa jalan.
                intent.putExtra("nama", technician.getName());
                intent.putExtra("fotoUrl", technician.getProfilePhoto());
                intent.putExtra("rating", (float) rating);
                intent.putExtra("jarak", availability);
                intent.putExtra("deskripsi", technician.getDescription());

                ctx.startActivity(intent);
            });
        }

        holder.layoutIcons.removeAllViews();

        List<DeviceCategoryResponse> categories = technician.getSupportedDeviceCategories();
        int categoryCount = categories != null ? categories.size() : 0;

        int maxIcons = isActive ? 3 : 2;
        int iconSizeDp = isActive ? 20 : 14;
        int totalIcons = Math.min(categoryCount, maxIcons);

        for (int i = 0; i < totalIcons; i++) {
            View iconCircle = new View(ctx);
            int iconSize = dpToPx(ctx, iconSizeDp);

            LinearLayout.LayoutParams ilp = new LinearLayout.LayoutParams(iconSize, iconSize);
            ilp.setMarginEnd(dpToPx(ctx, 3));

            iconCircle.setLayoutParams(ilp);
            iconCircle.setBackgroundResource(R.drawable.bg_cat_icon_circle);

            holder.layoutIcons.addView(iconCircle);
        }

        holder.itemView.setOnClickListener(v -> {
            int oldActive = activeIndex;
            activeIndex = position;

            notifyItemChanged(oldActive);
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return dataList != null ? dataList.size() : 0;
    }

    private String safe(String value, String fallback) {
        return value != null && !value.trim().isEmpty() ? value : fallback;
    }

    private int dpToPx(Context ctx, int dp) {
        return (int) TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            ctx.getResources().getDisplayMetrics()
        );
    }

    static class TechViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPhoto;
        View vOverlay;
        LinearLayout layoutTop;
        LinearLayout layoutStars;
        LinearLayout layoutIcons;
        TextView tvName;
        TextView tvPrice;
        TextView tvMoreInfo;
        TextView tvDistance;

        TechViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.ivTechPhoto);
            vOverlay = itemView.findViewById(R.id.vOverlay);
            layoutTop = itemView.findViewById(R.id.layoutTop);
            layoutStars = itemView.findViewById(R.id.layoutStars);
            layoutIcons = itemView.findViewById(R.id.layoutIcons);
            tvName = itemView.findViewById(R.id.tvTechName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvMoreInfo = itemView.findViewById(R.id.tvMoreInfo);
            tvDistance = itemView.findViewById(R.id.tvDistance);
        }
    }
}
