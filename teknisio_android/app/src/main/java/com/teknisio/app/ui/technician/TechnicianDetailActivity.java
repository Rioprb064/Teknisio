package com.teknisio.app.ui.technician;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.teknisio.app.R;
import com.teknisio.app.ui.order.OrderActivity;

public class TechnicianDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technician_detail);

        // Retrieve data from Intent
        String nama       = getIntent().getStringExtra("nama");
        String fotoUrl    = getIntent().getStringExtra("fotoUrl");
        float  rating     = getIntent().getFloatExtra("rating", 4f);
        String jarak      = getIntent().getStringExtra("jarak");
        String hargaMin   = getIntent().getStringExtra("hargaMin");
        String hargaMax   = getIntent().getStringExtra("hargaMax");
        String deskripsi  = getIntent().getStringExtra("deskripsi");

        // Bind views
        ImageView ivPhoto         = findViewById(R.id.ivTechPhoto);
        TextView tvName           = findViewById(R.id.tvName);
        LinearLayout layoutStars  = findViewById(R.id.layoutStars);
        TextView tvPrice          = findViewById(R.id.tvPrice);
        LinearLayout layoutSpec   = findViewById(R.id.layoutSpecialist);
        TextView tvDesc           = findViewById(R.id.tvDescription);
        LinearLayout btnBack      = findViewById(R.id.btnBack);
        Button btnMeet            = findViewById(R.id.btnMeetTechnician);

        // Load photo
        Glide.with(this)
                .load(fotoUrl)
                .placeholder(R.drawable.ic_profile_placeholder)
                .centerCrop()
                .into(ivPhoto);

        // Name
        if (nama != null) tvName.setText(nama);

        // Star rating
        int fullStars = (int) rating;
        for (int i = 0; i < 5; i++) {
            ImageView star = new ImageView(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    dpToPx(20), dpToPx(20));
            lp.setMarginEnd(dpToPx(3));
            star.setLayoutParams(lp);
            star.setImageResource(i < fullStars ? R.drawable.ic_star : R.drawable.ic_star_empty);
            layoutStars.addView(star);
        }

        // Price
        if (hargaMin != null && hargaMax != null) {
            tvPrice.setText("Rp" + hargaMin + " - Rp " + hargaMax);
        }

        // Specialist icons (2 circles as placeholders)
        for (int i = 0; i < 2; i++) {
            LinearLayout iconBox = new LinearLayout(this);
            int boxSize = dpToPx(52);
            LinearLayout.LayoutParams boxLp = new LinearLayout.LayoutParams(boxSize, dpToPx(68));
            boxLp.setMarginStart(dpToPx(8));
            iconBox.setLayoutParams(boxLp);
            iconBox.setOrientation(LinearLayout.VERTICAL);
            iconBox.setGravity(android.view.Gravity.CENTER);

            // Circle with icon
            LinearLayout circle = new LinearLayout(this);
            int circleSize = dpToPx(50);
            LinearLayout.LayoutParams circleLp = new LinearLayout.LayoutParams(circleSize, circleSize);
            circle.setLayoutParams(circleLp);
            circle.setBackgroundResource(R.drawable.bg_specialist_icon);
            circle.setGravity(android.view.Gravity.CENTER);

            ImageView icon = new ImageView(this);
            LinearLayout.LayoutParams iconLp = new LinearLayout.LayoutParams(dpToPx(24), dpToPx(24));
            icon.setLayoutParams(iconLp);
            icon.setImageResource(i == 0 ? R.drawable.ic_nav_home : R.drawable.ic_notification);
            icon.setColorFilter(getResources().getColor(android.R.color.holo_blue_dark, null));
            circle.addView(icon);
            iconBox.addView(circle);

            // Label (AC, Fridge, etc.)
            TextView label = new TextView(this);
            LinearLayout.LayoutParams labelLp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            label.setLayoutParams(labelLp);
            label.setTextSize(10f);
            label.setTextColor(0xFF1D1D1D);
            label.setText(i == 0 ? "AC" : "Fridge");
            label.setGravity(android.view.Gravity.CENTER);
            iconBox.addView(label);

            layoutSpec.addView(iconBox);
        }

        // Description
        if (deskripsi != null) tvDesc.setText(deskripsi);

        // Back
        btnBack.setOnClickListener(v -> finish());

        // Meet Technician → OrderActivity
        btnMeet.setOnClickListener(v -> {
            Intent intent = new Intent(this, OrderActivity.class);
            intent.putExtra("nama", nama);
            intent.putExtra("fotoUrl", fotoUrl);
            intent.putExtra("rating", rating);
            intent.putExtra("jarak", jarak);
            intent.putExtra("hargaMin", hargaMin);
            intent.putExtra("hargaMax", hargaMax);
            startActivity(intent);
        });
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}
