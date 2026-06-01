package com.teknisio.app.ui.home;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.teknisio.app.R;

public class NewsDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        ImageButton btnBack = findViewById(R.id.btnBack);
        ImageView ivImage = findViewById(R.id.ivNewsDetailImage);
        TextView tvDate = findViewById(R.id.tvNewsDetailDate);
        TextView tvTitle = findViewById(R.id.tvNewsDetailTitle);
        TextView tvContent = findViewById(R.id.tvNewsDetailContent);

        btnBack.setOnClickListener(v -> finish());

        // Get data from intent
        String title = getIntent().getStringExtra("EXTRA_TITLE");
        String desc = getIntent().getStringExtra("EXTRA_DESC");
        String date = getIntent().getStringExtra("EXTRA_DATE");
        String imageUrl = getIntent().getStringExtra("EXTRA_IMAGE_URL");

        if (title != null) tvTitle.setText(title);
        if (date != null) tvDate.setText(date);
        
        // Provide a dummy long text based on description
        if (desc != null) {
            StringBuilder fullContent = new StringBuilder();
            fullContent.append(desc).append("\n\n");
            fullContent.append("Perawatan rutin sangat penting untuk memastikan peralatan rumah tangga Anda berfungsi dengan baik dan tahan lama. Dengan perawatan yang tepat, Anda dapat menghindari biaya perbaikan yang mahal dan memperpanjang umur pakai alat.\n\n");
            fullContent.append("Pastikan untuk selalu membersihkan komponen utama secara berkala dan memeriksa tanda-tanda kerusakan seperti kabel yang terkelupas atau suara mesin yang tidak wajar.\n\n");
            fullContent.append("Jika Anda menemukan masalah yang tidak dapat diatasi sendiri, segera hubungi teknisi profesional terdekat melalui aplikasi Teknisio untuk mendapatkan bantuan ahli.");
            tvContent.setText(fullContent.toString());
        }

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                 .load(imageUrl)
                 .transform(new CenterCrop())
                 .placeholder(R.drawable.bg_news_thumb)
                 .into(ivImage);
        } else {
            ivImage.setImageResource(R.drawable.bg_news_thumb);
        }
    }
}
