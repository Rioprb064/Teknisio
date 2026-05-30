package com.teknisio.app.ui.order;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.teknisio.app.R;

import java.util.Calendar;

public class OrderActivity extends AppCompatActivity {

    private String selectedSlot = "AFTERNOON"; // default active
    private LinearLayout slotMorning, slotAfternoon, slotEvening;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        // ── Receive technician data ────────────────────────────────────────
        String nama     = getIntent().getStringExtra("nama");
        String fotoUrl  = getIntent().getStringExtra("fotoUrl");
        float  rating   = getIntent().getFloatExtra("rating", 4f);
        String jarak    = getIntent().getStringExtra("jarak");
        String hargaMin = getIntent().getStringExtra("hargaMin");
        String hargaMax = getIntent().getStringExtra("hargaMax");

        // ── Bind views ─────────────────────────────────────────────────────
        LinearLayout btnBack         = findViewById(R.id.btnBack);
        de.hdodenhof.circleimageview.CircleImageView ivTech = findViewById(R.id.ivTechSmall);
        TextView tvName              = findViewById(R.id.tvTechName);
        LinearLayout layoutStarsSmall = findViewById(R.id.layoutStarsSmall);
        TextView tvJarak             = findViewById(R.id.tvJarak);
        TextView tvPrice             = findViewById(R.id.tvPriceSmall);
        LinearLayout rowDate         = findViewById(R.id.rowDate);
        TextView tvDate              = findViewById(R.id.tvSelectedDate);
        EditText etDesc              = findViewById(R.id.etDamageDesc);
        Button btnConfirm            = findViewById(R.id.btnConfirmOrder);

        slotMorning   = findViewById(R.id.slotMorning);
        slotAfternoon = findViewById(R.id.slotAfternoon);
        slotEvening   = findViewById(R.id.slotEvening);

        // ── Fill technician summary ────────────────────────────────────────
        Glide.with(this).load(fotoUrl)
                .placeholder(R.drawable.ic_profile_placeholder).into(ivTech);
        if (nama != null)   tvName.setText(nama);
        if (jarak != null)  tvJarak.setText(jarak);
        if (hargaMin != null && hargaMax != null)
            tvPrice.setText("Rp" + hargaMin + " - Rp" + hargaMax);

        // Stars
        int fullStars = (int) rating;
        for (int i = 0; i < 5; i++) {
            ImageView star = new ImageView(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dpToPx(12), dpToPx(12));
            lp.setMarginEnd(dpToPx(2));
            star.setLayoutParams(lp);
            star.setImageResource(i < fullStars ? R.drawable.ic_star : R.drawable.ic_star_empty);
            layoutStarsSmall.addView(star);
        }

        // ── Time slots ────────────────────────────────────────────────────
        setActiveSlot("AFTERNOON"); // Afternoon active by default

        slotMorning.setOnClickListener(v -> setActiveSlot("MORNING"));
        slotAfternoon.setOnClickListener(v -> setActiveSlot("AFTERNOON"));
        slotEvening.setOnClickListener(v -> setActiveSlot("EVENING"));

        // ── Date picker ───────────────────────────────────────────────────
        rowDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(this,
                    (dp, year, month, day) -> {
                        String dateStr = day + "/" + (month + 1) + "/" + year;
                        tvDate.setText(dateStr);
                        tvDate.setTextColor(getColor(android.R.color.black));
                    },
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        // ── Back button ───────────────────────────────────────────────────
        btnBack.setOnClickListener(v -> finish());

        // ── Confirm Order ─────────────────────────────────────────────────
        btnConfirm.setOnClickListener(v -> {
            String desc = etDesc.getText().toString().trim();
            if (desc.isEmpty()) {
                Toast.makeText(this, "Harap isi deskripsi kerusakan", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Pesanan berhasil dikirim!", Toast.LENGTH_LONG).show();
            finish();
        });
    }

    private void setActiveSlot(String slot) {
        selectedSlot = slot;

        // Reset all to inactive style
        applySlotStyle(slotMorning, false);
        applySlotStyle(slotAfternoon, false);
        applySlotStyle(slotEvening, false);

        // Set active
        switch (slot) {
            case "MORNING":   applySlotStyle(slotMorning, true);   break;
            case "AFTERNOON": applySlotStyle(slotAfternoon, true); break;
            case "EVENING":   applySlotStyle(slotEvening, true);   break;
        }
    }

    private void applySlotStyle(LinearLayout slot, boolean isActive) {
        slot.setBackgroundResource(isActive ? R.drawable.bg_time_slot_active : R.drawable.bg_time_slot);
        int color = isActive ? android.graphics.Color.WHITE : android.graphics.Color.parseColor("#1D1D1D");
        int subColor = isActive ? android.graphics.Color.WHITE : android.graphics.Color.parseColor("#6B7280");

        for (int i = 0; i < slot.getChildCount(); i++) {
            View child = slot.getChildAt(i);
            if (child instanceof TextView) {
                TextView tv = (TextView) child;
                // First text = bold title, second = small time
                tv.setTextColor(i == 0 ? color : subColor);
            } else if (child instanceof ImageView) {
                ((ImageView) child).setColorFilter(isActive ?
                        android.graphics.Color.WHITE :
                        android.graphics.Color.parseColor("#6B7280"));
            }
        }
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}
