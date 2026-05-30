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
import com.teknisio.app.data.model.TeknisiModel;
import com.teknisio.app.ui.technician.TechnicianDetailActivity;

import java.util.List;

public class TechnicianCarouselAdapter extends RecyclerView.Adapter<TechnicianCarouselAdapter.TechViewHolder> {

    private final List<TeknisiModel> dataList;
    private int activeIndex = 1; // center card always starts active

    public TechnicianCarouselAdapter(List<TeknisiModel> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public TechViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_technician_card, parent, false);
        return new TechViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TechViewHolder holder, int position) {
        TeknisiModel teknisi = dataList.get(position);
        boolean isActive = (position == activeIndex);
        Context ctx = holder.itemView.getContext();

        // Set card size: active = 156dp x 156dp, inactive = 111dp x 126dp
        int widthDp = isActive ? 156 : 111;
        int heightDp = isActive ? 156 : 126;
        int width = dpToPx(ctx, widthDp);
        int height = dpToPx(ctx, heightDp);

        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp == null) lp = new RecyclerView.LayoutParams(width, height);
        lp.width = width;
        lp.height = height;
        // Overlap: negative margin to simulate overlap effect
        if (lp instanceof RecyclerView.LayoutParams) {
            int marginDp = isActive ? -8 : 0;
            ((RecyclerView.LayoutParams) lp).setMarginStart(dpToPx(ctx, marginDp));
            ((RecyclerView.LayoutParams) lp).setMarginEnd(dpToPx(ctx, marginDp));
        }
        holder.itemView.setLayoutParams(lp);

        // Load photo
        Glide.with(ctx)
                .load(teknisi.getFotoUrl())
                .placeholder(R.drawable.ic_profile_placeholder)
                .centerCrop()
                .into(holder.ivPhoto);

        // Name
        holder.tvName.setText(teknisi.getNama());
        int nameSizeSp = isActive ? 13 : 10;
        holder.tvName.setTextSize(TypedValue.COMPLEX_UNIT_SP, nameSizeSp);

        // Overlay
        if (isActive) {
            // Teal gradient-like overlay: use a semi-transparent teal
            holder.vOverlay.setBackgroundColor(Color.parseColor("#993899A3"));
        } else {
            holder.vOverlay.setBackgroundColor(Color.parseColor("#8094A5A6"));
        }

        // Active-only elements
        holder.layoutTop.setVisibility(isActive ? View.VISIBLE : View.GONE);
        holder.tvPrice.setVisibility(isActive ? View.VISIBLE : View.GONE);
        holder.tvMoreInfo.setVisibility(isActive ? View.VISIBLE : View.GONE);

        if (isActive) {
            holder.tvPrice.setText(teknisi.getHargaRange());
            // Stars
            holder.layoutStars.removeAllViews();
            int fullStars = (int) teknisi.getRating();
            for (int i = 0; i < 5; i++) {
                ImageView star = new ImageView(ctx);
                LinearLayout.LayoutParams slp = new LinearLayout.LayoutParams(dpToPx(ctx, 10), dpToPx(ctx, 10));
                slp.setMarginEnd(dpToPx(ctx, 1));
                star.setLayoutParams(slp);
                star.setImageResource(i < fullStars ? R.drawable.ic_star : R.drawable.ic_star_empty);
                holder.layoutStars.addView(star);
            }
            // Click More Info → TechnicianDetailActivity
            holder.tvMoreInfo.setOnClickListener(v -> {
                Intent intent = new Intent(ctx, TechnicianDetailActivity.class);
                intent.putExtra("nama", teknisi.getNama());
                intent.putExtra("fotoUrl", teknisi.getFotoUrl());
                intent.putExtra("rating", teknisi.getRating());
                intent.putExtra("jarak", teknisi.getJarak());
                intent.putExtra("hargaMin", teknisi.getHargaMin());
                intent.putExtra("hargaMax", teknisi.getHargaMax());
                intent.putExtra("deskripsi", teknisi.getDeskripsi());
                ctx.startActivity(intent);
            });
        }

        // Small category icon circles (small white circles)
        holder.layoutIcons.removeAllViews();
        List<String> spesialis = teknisi.getSpesialisasi();
        int maxIcons = isActive ? 3 : 2;
        int iconSizeDp = isActive ? 20 : 14;
        for (int i = 0; i < Math.min(spesialis.size(), maxIcons); i++) {
            View iconCircle = new View(ctx);
            int iconSize = dpToPx(ctx, iconSizeDp);
            LinearLayout.LayoutParams ilp = new LinearLayout.LayoutParams(iconSize, iconSize);
            ilp.setMarginEnd(dpToPx(ctx, 3));
            iconCircle.setLayoutParams(ilp);
            iconCircle.setBackgroundResource(R.drawable.bg_cat_icon_circle);
            holder.layoutIcons.addView(iconCircle);
        }

        // Click on inactive card → set as active
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

    private int dpToPx(Context ctx, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                ctx.getResources().getDisplayMetrics());
    }

    static class TechViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPhoto;
        View vOverlay;
        LinearLayout layoutTop, layoutStars, layoutIcons;
        TextView tvName, tvPrice, tvMoreInfo, tvDistance;

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
