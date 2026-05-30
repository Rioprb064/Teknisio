package com.teknisio.app.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        // For icon, we'll keep the placeholder ic_nav_home for now 
        // until we add image loading (like Glide) in the future if icons are URLs.
    }

    @Override
    public int getItemCount() {
        return kategoriList != null ? kategoriList.size() : 0;
    }

    static class KategoriViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryName;

        public KategoriViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
        }
    }
}
