package com.teknisio.app.ui.auth;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.teknisio.app.R;
import com.teknisio.app.data.api.ApiClient;
import com.teknisio.app.data.api.ApiService;
import com.teknisio.app.data.model.RegisterCustomerRequest;
import com.teknisio.app.data.model.RegisterResponse;
import com.teknisio.app.data.model.RegisterTeknisiRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private Button btnTabCustomer, btnTabTeknisi, btnRegister;
    private EditText etNama, etEmail, etTelepon, etPassword, etAlamat, etDeskripsi;
    private ImageButton btnTogglePassword;
    private TextView tvErrorMessage, tvLoginLink;
    private LinearLayout containerDeskripsi;

    private boolean isPasswordVisible = false;
    private String currentRole = "CUSTOMER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        setupTabs();
        setupListeners();
    }

    private void initViews() {
        btnTabCustomer = findViewById(R.id.btn_tab_customer);
        btnTabTeknisi = findViewById(R.id.btn_tab_teknisi);
        btnRegister = findViewById(R.id.btn_register);
        
        etNama = findViewById(R.id.et_nama);
        etEmail = findViewById(R.id.et_email);
        etTelepon = findViewById(R.id.et_telepon);
        etPassword = findViewById(R.id.et_password);
        etAlamat = findViewById(R.id.et_alamat);
        etDeskripsi = findViewById(R.id.et_deskripsi);
        
        btnTogglePassword = findViewById(R.id.btn_toggle_password);
        tvErrorMessage = findViewById(R.id.tv_error_message);
        tvLoginLink = findViewById(R.id.tv_login_link);
        containerDeskripsi = findViewById(R.id.container_deskripsi);
    }

    private void setupTabs() {
        btnTabCustomer.setOnClickListener(v -> switchTab("CUSTOMER"));
        btnTabTeknisi.setOnClickListener(v -> switchTab("TEKNISI"));
    }

    private void switchTab(String role) {
        currentRole = role;
        if (role.equals("CUSTOMER")) {
            btnTabCustomer.setBackgroundColor(Color.WHITE);
            btnTabCustomer.setTextColor(Color.parseColor("#384A7E"));
            
            btnTabTeknisi.setBackgroundColor(Color.TRANSPARENT);
            btnTabTeknisi.setTextColor(Color.parseColor("#555555"));
            
            containerDeskripsi.setVisibility(View.GONE);
        } else {
            btnTabTeknisi.setBackgroundColor(Color.WHITE);
            btnTabTeknisi.setTextColor(Color.parseColor("#384A7E"));
            
            btnTabCustomer.setBackgroundColor(Color.TRANSPARENT);
            btnTabCustomer.setTextColor(Color.parseColor("#555555"));
            
            containerDeskripsi.setVisibility(View.VISIBLE);
        }
    }

    private void setupListeners() {
        tvLoginLink.setOnClickListener(v -> {
            finish(); // Go back to login
        });

        btnTogglePassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                isPasswordVisible = false;
            } else {
                etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                isPasswordVisible = true;
            }
            etPassword.setSelection(etPassword.getText().length());
        });

        btnRegister.setOnClickListener(v -> validateAndRegister());
    }

    private void validateAndRegister() {
        String nama = etNama.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String telepon = etTelepon.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String alamat = etAlamat.getText().toString().trim();
        String deskripsi = etDeskripsi.getText().toString().trim();

        if (nama.isEmpty() || email.isEmpty() || telepon.isEmpty() || password.isEmpty() || alamat.isEmpty()) {
            showError("Semua field dasar harus diisi.");
            return;
        }

        if (password.length() < 8) {
            showError("Password minimal 8 karakter.");
            return;
        }

        if (currentRole.equals("TEKNISI") && deskripsi.isEmpty()) {
            showError("Deskripsi keahlian harus diisi untuk Teknisi.");
            return;
        }

        performRegistration(nama, email, telepon, password, alamat, deskripsi);
    }

    private void performRegistration(String nama, String email, String telepon, String password, String alamat, String deskripsi) {
        tvErrorMessage.setVisibility(View.GONE);
        btnRegister.setEnabled(false);
        btnRegister.setText("Mendaftar...");

        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);

        if (currentRole.equals("CUSTOMER")) {
            RegisterCustomerRequest req = new RegisterCustomerRequest(nama, email, telepon, password, alamat);
            apiService.registerCustomer(req).enqueue(new Callback<RegisterResponse>() {
                @Override
                public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                    handleResponse(response);
                }

                @Override
                public void onFailure(Call<RegisterResponse> call, Throwable t) {
                    handleFailure(t);
                }
            });
        } else {
            RegisterTeknisiRequest req = new RegisterTeknisiRequest(nama, email, telepon, password, alamat, deskripsi);
            apiService.registerTeknisi(req).enqueue(new Callback<RegisterResponse>() {
                @Override
                public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                    handleResponse(response);
                }

                @Override
                public void onFailure(Call<RegisterResponse> call, Throwable t) {
                    handleFailure(t);
                }
            });
        }
    }

    private void handleResponse(Response<RegisterResponse> response) {
        btnRegister.setEnabled(true);
        btnRegister.setText("Confirm");

        if (response.isSuccessful() && response.body() != null) {
            Toast.makeText(this, "Registrasi Berhasil! Silakan Login.", Toast.LENGTH_LONG).show();
            finish(); // Kembali ke LoginActivity
        } else {
            showError("Registrasi gagal. Email mungkin sudah terdaftar.");
        }
    }

    private void handleFailure(Throwable t) {
        btnRegister.setEnabled(true);
        btnRegister.setText("Confirm");
        showError("Gagal terhubung ke server: " + t.getMessage());
    }

    private void showError(String message) {
        tvErrorMessage.setText(message);
        tvErrorMessage.setVisibility(View.VISIBLE);
    }
}
