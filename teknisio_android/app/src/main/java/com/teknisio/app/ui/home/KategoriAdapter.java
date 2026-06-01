package com.teknisio.app.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.teknisio.app.R;
import com.teknisio.app.data.model.DeviceCategoryResponse;

import java.util.List;

public class KategoriAdapter extends RecyclerView.Adapter<KategoriAdapter.KategoriViewHolder> {

    public interface OnCategoryClickListener {
        void onCategoryClick(DeviceCategoryResponse category);
    }

    private final List<DeviceCategoryResponse> categoryList;
    private final OnCategoryClickListener listener;

    public KategoriAdapter(
            List<DeviceCategoryResponse> categoryList,
            OnCategoryClickListener listener
    ) {
        this.categoryList = categoryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public KategoriViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new KategoriViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KategoriViewHolder holder, int position) {
        DeviceCategoryResponse category = categoryList.get(position);

        String name = category.getName();
        holder.tvCategoryName.setText(name != null ? name : "-");
        holder.ivCategoryIcon.setImageResource(getIconForCategory(name));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCategoryClick(category);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList != null ? categoryList.size() : 0;
    }

    private int getIconForCategory(String name) {
        if (name == null) return R.drawable.ic_cat_default;

        String lower = name.toLowerCase();

        if (lower.contains("tv") || lower.contains("televisi")) {
            return R.drawable.ic_cat_tv;
        }

        if (lower.contains("ac") || lower.contains("air condition")) {
            return R.drawable.ic_cat_ac;
        }

        if (lower.contains("kulkas") || lower.contains("fridge") || lower.contains("refriger")) {
            return R.drawable.ic_cat_fridge;
        }

        if (lower.contains("cuci") || lower.contains("washing")) {
            return R.drawable.ic_cat_washing_machine;
        }

        if (lower.contains("kipas") || lower.contains("fan")) {
            return R.drawable.ic_cat_electric_fan;
        }

        if (lower.contains("rice") || lower.contains("magic") || lower.contains("cooker")) {
            return R.drawable.ic_cat_rice_cooker;
        }

        return R.drawable.ic_cat_default;
    }

    static class KategoriViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryName;
        ImageView ivCategoryIcon;

        KategoriViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            ivCategoryIcon = itemView.findViewById(R.id.ivCategoryIcon);
        }
    }
}
