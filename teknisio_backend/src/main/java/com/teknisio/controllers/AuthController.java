package com.teknisio.controllers;

import com.teknisio.dto.requests.RegisterCustomerRequest;
import com.teknisio.dto.responses.RegisterCustomerResponse;
import com.teknisio.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
}
