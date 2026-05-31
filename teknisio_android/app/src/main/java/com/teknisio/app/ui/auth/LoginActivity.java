package com.teknisio.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.teknisio.app.data.model.LoginRequest;
import com.teknisio.app.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private ImageButton btnTogglePassword;
    private TextView tvErrorMessage;

    private boolean isPasswordVisible = false;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);

        if (sessionManager.isLoggedIn()) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnTogglePassword = findViewById(R.id.btn_toggle_password);
        tvErrorMessage = findViewById(R.id.tv_error_message);

        TextView tvRegisterLink = findViewById(R.id.tv_register_link);

        tvRegisterLink.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        btnTogglePassword.setOnClickListener(v -> togglePasswordVisibility());

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                showError("Email dan password harus diisi.");
                return;
            }

            performLogin(email, password);
        });
    }

    private void performLogin(String email, String password) {
        hideError();
        setLoading(true);

        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        LoginRequest request = new LoginRequest(email, password);

        apiService.login(request).enqueue(new Callback<ApiResponse<AuthResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthResponse>> call, Response<ApiResponse<AuthResponse>> response) {
                setLoading(false);

                if (!response.isSuccessful() || response.body() == null) {
                    showError("Login gagal. Periksa kembali kredensial Anda.");
                    return;
                }

                ApiResponse<AuthResponse> apiResponse = response.body();

                if (!apiResponse.isSuccess() || apiResponse.getData() == null) {
                    String message = apiResponse.getMessage();
                    showError(message != null ? message : "Login gagal.");
                    return;
                }

                AuthResponse authResponse = apiResponse.getData();
                AuthUserResponse user = authResponse.getUser();

                if (authResponse.getAccessToken() == null || user == null) {
                    showError("Login gagal. Data login tidak lengkap.");
                    return;
                }

                if (!"CUSTOMER".equals(user.getRole())) {
                    showError("Login berhasil, tetapi dashboard untuk role " + user.getRole() + " belum tersedia di aplikasi mobile ini.");
                    sessionManager.clearSession();
                    return;
                }

                sessionManager.saveAuthSession(
                    authResponse.getAccessToken(),
                    authResponse.getTokenType(),
                    authResponse.getExpiresInMs(),
                    user
                );

                Toast.makeText(LoginActivity.this, "Login berhasil", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onFailure(Call<ApiResponse<AuthResponse>> call, Throwable t) {
                setLoading(false);
                showError("Gagal terhubung ke server: " + t.getMessage());
            }
        });
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
        btnLogin.setEnabled(!loading);
        btnLogin.setText(loading ? "Sedang memproses..." : "Confirm");
    }

    private void showError(String message) {
        tvErrorMessage.setText(message);
        tvErrorMessage.setVisibility(View.VISIBLE);
    }

    private void hideError() {
        tvErrorMessage.setVisibility(View.GONE);
    }
}
