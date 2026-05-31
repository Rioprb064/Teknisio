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

import com.teknisio.app.MainActivity;
import com.teknisio.app.R;
import com.teknisio.app.data.api.ApiClient;
import com.teknisio.app.data.api.ApiService;
import com.teknisio.app.data.model.ApiResponse;
import com.teknisio.app.data.model.AuthResponse;
import com.teknisio.app.data.model.AuthUserResponse;
import com.teknisio.app.data.model.RegisterCustomerRequest;
import com.teknisio.app.data.model.RegisterTeknisiRequest;
import com.teknisio.app.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private Button btnTabCustomer;
    private Button btnTabTeknisi;
    private Button btnRegister;

    private EditText etNama;
    private EditText etEmail;
    private EditText etTelepon;
    private EditText etPassword;
    private EditText etAlamat;
    private EditText etDeskripsi;

    private ImageButton btnTogglePassword;
    private TextView tvErrorMessage;
    private TextView tvLoginLink;
    private LinearLayout containerDeskripsi;

    private boolean isPasswordVisible = false;
    private String currentRole = "CUSTOMER";

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        sessionManager = new SessionManager(this);

        initViews();
        setupTabs();
        setupListeners();
        switchTab("CUSTOMER");
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
        btnTabTeknisi.setOnClickListener(v -> switchTab("TECHNICIAN"));
    }

    private void switchTab(String role) {
        currentRole = role;

        if ("CUSTOMER".equals(role)) {
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
        tvLoginLink.setOnClickListener(v -> finish());

        btnTogglePassword.setOnClickListener(v -> togglePasswordVisibility());

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

        if (!telepon.matches("^\\+?[0-9]{10,15}$")) {
            showError("Nomor telepon harus 10-15 digit dan boleh diawali +.");
            return;
        }

        if ("TECHNICIAN".equals(currentRole) && deskripsi.isEmpty()) {
            showError("Deskripsi keahlian harus diisi untuk Teknisi.");
            return;
        }

        performRegistration(nama, email, telepon, password, alamat, deskripsi);
    }

    private void performRegistration(String nama, String email, String telepon, String password, String alamat, String deskripsi) {
        hideError();
        setLoading(true);

        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);

        if ("CUSTOMER".equals(currentRole)) {
            RegisterCustomerRequest request = new RegisterCustomerRequest(
                nama,
                email,
                telepon,
                password,
                alamat
            );

            apiService.registerCustomer(request).enqueue(new Callback<ApiResponse<AuthResponse>>() {
                @Override
                public void onResponse(Call<ApiResponse<AuthResponse>> call, Response<ApiResponse<AuthResponse>> response ) {
                    handleAuthResponse(response);
                }

                @Override
                public void onFailure(Call<ApiResponse<AuthResponse>> call, Throwable t) {
                    handleFailure(t);
                }
            });
        }
        else {
            RegisterTeknisiRequest request = new RegisterTeknisiRequest(
                nama,
                email,
                telepon,
                password,
                alamat,
                deskripsi
            );

            apiService.registerTechnician(request).enqueue(new Callback<ApiResponse<AuthResponse>>() {
                @Override
                public void onResponse(Call<ApiResponse<AuthResponse>> call, Response<ApiResponse<AuthResponse>> response) {
                    handleAuthResponse(response);
                }

                @Override
                public void onFailure(Call<ApiResponse<AuthResponse>> call, Throwable t) {
                    handleFailure(t);
                }
            });
        }
    }

    private void handleAuthResponse(Response<ApiResponse<AuthResponse>> response) {
        setLoading(false);

        if (!response.isSuccessful() || response.body() == null) {
            showError("Registrasi gagal. Email mungkin sudah terdaftar atau data tidak valid.");
            return;
        }

        ApiResponse<AuthResponse> apiResponse = response.body();

        if (!apiResponse.isSuccess() || apiResponse.getData() == null) {
            String message = apiResponse.getMessage();
            showError(message != null ? message : "Registrasi gagal.");
            return;
        }

        AuthResponse authResponse = apiResponse.getData();
        AuthUserResponse user = authResponse.getUser();

        if (authResponse.getAccessToken() == null || user == null) {
            showError("Registrasi berhasil, tetapi data login tidak lengkap. Silakan login manual.");
            return;
        }

        if (!"CUSTOMER".equals(user.getRole())) {
            Toast.makeText(
                this,
                "Registrasi teknisi berhasil. Dashboard teknisi belum tersedia di aplikasi mobile ini.",
                Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        sessionManager.saveAuthSession(
            authResponse.getAccessToken(),
            authResponse.getTokenType(),
            authResponse.getExpiresInMs(),
            user
        );

        Toast.makeText(this, "Registrasi berhasil", Toast.LENGTH_SHORT).show();

        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
        finish();
    }

    private void handleFailure(Throwable t) {
        setLoading(false);
        showError("Gagal terhubung ke server: " + t.getMessage());
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            isPasswordVisible = false;
        }
        else {
            etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            isPasswordVisible = true;
        }

        etPassword.setSelection(etPassword.getText().length());
    }

    private void setLoading(boolean loading) {
        btnRegister.setEnabled(!loading);
        btnRegister.setText(loading ? "Mendaftar..." : "Confirm");
    }

    private void showError(String message) {
        tvErrorMessage.setText(message);
        tvErrorMessage.setVisibility(View.VISIBLE);
    }

    private void hideError() {
        tvErrorMessage.setVisibility(View.GONE);
    }
}
