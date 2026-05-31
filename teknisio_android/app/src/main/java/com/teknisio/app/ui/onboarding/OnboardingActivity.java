package com.teknisio.app.ui.onboarding;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.widget.Button;

import com.teknisio.app.R;
import com.teknisio.app.ui.auth.LoginActivity;
import com.teknisio.app.utils.SessionManager;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private OnboardingAdapter adapter;
    private Button btnStart;

    private final int[] slideImages = {
            R.drawable.start_1,
            R.drawable.start_2,
            R.drawable.start_3
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Skip onboarding if user has seen it already, or if already logged in
        SessionManager sessionManager = new SessionManager(this);
        if (sessionManager.hasSeenOnboarding()) {
            // Go to Login or Main directly
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_onboarding);

        viewPager = findViewById(R.id.viewPager);
        btnStart = findViewById(R.id.btn_start);

        adapter = new OnboardingAdapter(slideImages);
        viewPager.setAdapter(adapter);

        // Auto-slide every 3 seconds
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                adapter.setCurrentPage(position);
            }
        });

        // Update indicators initially
        adapter.setCurrentPage(0);

        // Start button click -> go to Login and mark onboarding as seen
        btnStart.setOnClickListener(v -> {
            sessionManager.setOnboardingSeen();
            Intent intent = new Intent(OnboardingActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
