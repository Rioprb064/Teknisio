package com.teknisio.controllers;

import com.teknisio.dto.requests.LoginRequest;
import com.teknisio.dto.requests.RefreshTokenRequest;
import com.teknisio.dto.requests.RegisterCustomerRequest;
import com.teknisio.dto.requests.RegisterTeknisiRequest;
import com.teknisio.dto.responses.AuthProfileResponse;
import com.teknisio.dto.responses.LoginResponse;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/register/customer")
  public ResponseEntity<RegisterCustomerResponse> registerCustomer(@Valid @RequestBody RegisterCustomerRequest request) {
    RegisterCustomerResponse response = authService.registerCustomer(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PostMapping("/register/teknisi")
  public ResponseEntity<RegisterTeknisiResponse> registerTeknisi(@Valid @RequestBody RegisterTeknisiRequest request) {
    RegisterTeknisiResponse response = authService.registerTeknisi(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
    LoginResponse response = authService.login(request);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/refresh")
  public ResponseEntity<RefreshTokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
    RefreshTokenResponse response = authService.refresh(request);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/profile")
  public ResponseEntity<AuthProfileResponse> profile(@AuthenticationPrincipal User user) {
    AuthProfileResponse response = authService.profile(user);
    return ResponseEntity.ok(response);
  }
}
