package com.teknisio.app.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.teknisio.app.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    public static class NewsItem {
        public String judul;
        public String deskripsi;
        public String tanggal;
        public String imageUrl;

        public NewsItem(String judul, String deskripsi, String tanggal, String imageUrl) {
            this.judul = judul;
            this.deskripsi = deskripsi;
            this.tanggal = tanggal;
            this.imageUrl = imageUrl;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(NewsItem item);
    }

    private final List<NewsItem> items;
    private final OnItemClickListener listener;

    public NewsAdapter(List<NewsItem> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
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
        
        if (item.imageUrl != null && !item.imageUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                 .load(item.imageUrl)
                 .apply(new RequestOptions().transform(new CenterCrop(), new RoundedCorners(16)))
                 .placeholder(R.drawable.bg_news_thumb)
                 .into(holder.ivNewsImage);
        } else {
            holder.ivNewsImage.setImageResource(R.drawable.ic_notification);
        }
        
        // Bottom divider: hide for last item
        holder.divider.setVisibility(position == items.size() - 1 ? View.GONE : View.VISIBLE);
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() { return items != null ? items.size() : 0; }

    static class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc, tvDate;
        android.widget.ImageView ivNewsImage;
        View divider;

        NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvNewsTitle);
            tvDesc = itemView.findViewById(R.id.tvNewsDesc);
            tvDate = itemView.findViewById(R.id.tvNewsDate);
            ivNewsImage = itemView.findViewById(R.id.ivNewsImage);
            divider = itemView.findViewById(R.id.vDivider);
        }
    }
}
