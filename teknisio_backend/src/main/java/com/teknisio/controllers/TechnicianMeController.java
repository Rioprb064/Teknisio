package com.teknisio.controllers;

import com.teknisio.common.response.ApiResponse;
import com.teknisio.dto.requests.UpdateTechnicianAvailabilityStatusRequest;
import com.teknisio.dto.responses.TechnicianAvailabilityStatusResponse;
import com.teknisio.model.entities.User;
import com.teknisio.services.TechnicianMeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/technicians/me")
@RequiredArgsConstructor
public class TechnicianMeController {

  private final TechnicianMeService technicianMeService;

  @PatchMapping("/status")
  public ResponseEntity<ApiResponse<TechnicianAvailabilityStatusResponse>> updateAvailabilityStatus(@AuthenticationPrincipal User authenticatedUser,@Valid @RequestBody UpdateTechnicianAvailabilityStatusRequest request) {
    TechnicianAvailabilityStatusResponse response = technicianMeService.updateAvailabilityStatus(authenticatedUser, request);

    return ResponseEntity.ok(
      ApiResponse.success(
        "Technician availability status updated successfully",
        response
      )
    );
  }
}
