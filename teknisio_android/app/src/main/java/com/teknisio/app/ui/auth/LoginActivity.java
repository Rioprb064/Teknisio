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
import com.teknisio.app.data.model.LoginRequest;
import com.teknisio.app.data.model.LoginResponse;
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
        
        // Auto-login jika sudah ada token
        if (sessionManager.getToken() != null) {
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

        // Navigasi ke RegisterActivity
        tvRegisterLink.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        // Toggle Password
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

        // Proses Login
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
        tvErrorMessage.setVisibility(View.GONE);
        btnLogin.setEnabled(false);
        btnLogin.setText("Sedang memproses...");

        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        LoginRequest request = new LoginRequest(email, password);

        apiService.loginUser(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                btnLogin.setEnabled(true);
                btnLogin.setText("Confirm");

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    
                    // Simpan sesi
                    sessionManager.saveAuthSession(
                        loginResponse.getAccessToken(),
                        loginResponse.getIdUser(),
                        loginResponse.getNama()
                    );
                    // Simpan email dari response
                    sessionManager.saveUserProfile(
                        loginResponse.getEmail() != null ? loginResponse.getEmail() : "",
                        ""
                    );

                    Toast.makeText(LoginActivity.this, "Login Berhasil", Toast.LENGTH_SHORT).show();
                    
                    // Pindah ke MainActivity
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    showError("Login gagal. Periksa kembali kredensial Anda.");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                btnLogin.setEnabled(true);
                btnLogin.setText("Confirm");
                showError("Gagal terhubung ke server: " + t.getMessage());
            }
        });
    }

    private void showError(String message) {
        tvErrorMessage.setText(message);
        tvErrorMessage.setVisibility(View.VISIBLE);
    }
}
