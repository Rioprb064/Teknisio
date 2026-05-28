package com.teknisio.controllers;

import com.teknisio.common.response.ApiResponse;
import com.teknisio.dto.requests.LoginRequest;
import com.teknisio.dto.requests.LogoutRequest;
import com.teknisio.dto.requests.RefreshTokenRequest;
import com.teknisio.dto.requests.RegisterCustomerRequest;
import com.teknisio.dto.requests.RegisterTeknisiRequest;
import com.teknisio.dto.responses.AuthProfileResponse;
import com.teknisio.dto.responses.LoginResponse;
import com.teknisio.dto.responses.LogoutResponse;
import com.teknisio.dto.responses.RefreshTokenResponse;
import com.teknisio.dto.responses.RegisterCustomerResponse;
import com.teknisio.dto.responses.RegisterTeknisiResponse;
import com.teknisio.model.entities.User;
import com.teknisio.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;

  @PostMapping("/register/customer")
  public ResponseEntity<ApiResponse<RegisterCustomerResponse>> registerCustomer(@Valid @RequestBody RegisterCustomerRequest request) {
    RegisterCustomerResponse response = authService.registerCustomer(request);

    return ResponseEntity
      .status(HttpStatus.CREATED)
      .body(ApiResponse.success("Customer registered successfully", response))
    ;
  }

  @PostMapping("/register/technician")
  public ResponseEntity<ApiResponse<RegisterTeknisiResponse>> registerTeknisi(@Valid @RequestBody RegisterTeknisiRequest request) {
    RegisterTeknisiResponse response = authService.registerTeknisi(request);

    return ResponseEntity
      .status(HttpStatus.CREATED)
      .body(ApiResponse.success("Technician registered successfully", response))
    ;
  }

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
    LoginResponse response = authService.login(request);
    return ResponseEntity.ok(
      ApiResponse.success("Login successful", response)
    );
  }

  @PostMapping("/logout")
  public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody LogoutRequest request) {
    LogoutResponse response = authService.logout(request);
    return ResponseEntity.ok(ApiResponse.success(response.message()));
  }

  @PostMapping("/refresh")
  public ResponseEntity<ApiResponse<RefreshTokenResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
    RefreshTokenResponse response = authService.refresh(request);
    return ResponseEntity.ok(
      ApiResponse.success("Token refreshed successfully", response)
    );
  }

  @GetMapping("/profile")
  public ResponseEntity<ApiResponse<AuthProfileResponse>> profile(@AuthenticationPrincipal User user) {
    AuthProfileResponse response = authService.profile(user);
    return ResponseEntity.ok(
      ApiResponse.success("Profile retrieved successfully", response)
    );
  }
}
