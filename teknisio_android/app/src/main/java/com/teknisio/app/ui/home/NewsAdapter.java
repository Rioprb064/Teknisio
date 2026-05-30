package com.teknisio.app.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.teknisio.app.R;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    public static class NewsItem {
        public String judul;
        public String deskripsi;
        public String tanggal;

        public NewsItem(String judul, String deskripsi, String tanggal) {
            this.judul = judul;
            this.deskripsi = deskripsi;
            this.tanggal = tanggal;
        }
    }

    private final List<NewsItem> items;

    public NewsAdapter(List<NewsItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        NewsItem item = items.get(position);
        holder.tvTitle.setText(item.judul);
        holder.tvDesc.setText(item.deskripsi);
        holder.tvDate.setText(item.tanggal);
        // Bottom divider: hide for last item
        holder.divider.setVisibility(position == items.size() - 1 ? View.GONE : View.VISIBLE);
    }

    @Override
    public int getItemCount() { return items != null ? items.size() : 0; }

    static class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc, tvDate;
        View divider;

        NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvNewsTitle);
            tvDesc = itemView.findViewById(R.id.tvNewsDesc);
            tvDate = itemView.findViewById(R.id.tvNewsDate);
            divider = itemView.findViewById(R.id.vDivider);
        }
    }
}
