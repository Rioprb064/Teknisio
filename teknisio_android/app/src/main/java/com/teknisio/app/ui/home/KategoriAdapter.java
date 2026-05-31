package com.teknisio.app.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.teknisio.app.R;
import com.teknisio.app.data.model.KategoriResponse;
import java.util.List;

public class KategoriAdapter extends RecyclerView.Adapter<KategoriAdapter.KategoriViewHolder> {

    private List<KategoriResponse> kategoriList;

    public KategoriAdapter(List<KategoriResponse> kategoriList) {
        this.kategoriList = kategoriList;
    }

    @NonNull
    @Override
    public KategoriViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new KategoriViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KategoriViewHolder holder, int position) {
        KategoriResponse kategori = kategoriList.get(position);
        holder.tvCategoryName.setText(kategori.getNamaKategori());
        holder.ivCategoryIcon.setImageResource(getIconForCategory(kategori.getNamaKategori()));
    }

    @Override
    public int getItemCount() {
        return kategoriList != null ? kategoriList.size() : 0;
    }

    /** Map category name to a local drawable icon resource */
    private int getIconForCategory(String name) {
        if (name == null) return R.drawable.ic_cat_default;
        String lower = name.toLowerCase();
        if (lower.contains("tv") || lower.contains("televisi")) return R.drawable.ic_cat_tv;
        if (lower.contains("ac") || lower.contains("air condition")) return R.drawable.ic_cat_ac;
        if (lower.contains("kulkas") || lower.contains("fridge") || lower.contains("refriger")) return R.drawable.ic_cat_ac;
        if (lower.contains("cuci") || lower.contains("washing")) return R.drawable.ic_cat_default;
        if (lower.contains("oven") || lower.contains("kompor")) return R.drawable.ic_cat_tv;
        if (lower.contains("laptop") || lower.contains("computer") || lower.contains("pc")) return R.drawable.ic_cat_tv;
        if (lower.contains("rice") || lower.contains("cooker")) return R.drawable.ic_cat_default;
        return R.drawable.ic_cat_default;
    }

    static class KategoriViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryName;
        ImageView ivCategoryIcon;

        public KategoriViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            ivCategoryIcon = itemView.findViewById(R.id.ivCategoryIcon);
        }
    }
}
