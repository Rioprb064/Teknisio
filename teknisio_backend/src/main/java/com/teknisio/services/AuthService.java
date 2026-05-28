package com.teknisio.services;

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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
  private final AuthRegistrationService authRegistrationService;
  private final AuthSessionService authSessionService;
  private final AuthProfileService authProfileService;

  public RegisterCustomerResponse registerCustomer(RegisterCustomerRequest request) {
    return authRegistrationService.registerCustomer(request);
  }

  public RegisterTeknisiResponse registerTeknisi(RegisterTeknisiRequest request) {
    return authRegistrationService.registerTeknisi(request);
  }

  public LoginResponse login(LoginRequest request) {
    return authSessionService.login(request);
  }

  public RefreshTokenResponse refresh(RefreshTokenRequest request) {
    return authSessionService.refresh(request);
  }

  public LogoutResponse logout(LogoutRequest request) {
    return authSessionService.logout(request);
  }

  public AuthProfileResponse profile(User authenticatedUser) {
    return authProfileService.profile(authenticatedUser);
  }
}
