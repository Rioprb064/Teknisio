package com.teknisio.controllers;

import com.teknisio.common.response.ApiResponse;
import com.teknisio.dto.responses.TechnicianDetailResponse;
import com.teknisio.dto.responses.TechnicianSummaryResponse;
import com.teknisio.services.CustomerTechnicianService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customers/technicians")
public class CustomerTechnicianController {

  private final CustomerTechnicianService customerTechnicianService;

  @GetMapping
  public ResponseEntity<ApiResponse<List<TechnicianSummaryResponse>>> getTechniciansByDeviceCategory(
    @RequestParam UUID deviceCategoryId,
    @RequestParam(required = false) String availabilityStatus,
    @RequestParam(required = false, defaultValue = "name") String sort
  ) {
    List<TechnicianSummaryResponse> technicians =
      customerTechnicianService.getTechniciansByDeviceCategory(
        deviceCategoryId,
        availabilityStatus,
        sort
      );

    return ResponseEntity.ok(
      ApiResponse.success(
        "Technicians retrieved successfully",
        technicians
      )
    );
  }

  @GetMapping("/{technicianProfileId}")
  public ResponseEntity<ApiResponse<TechnicianDetailResponse>> getTechnicianDetail(
    @PathVariable UUID technicianProfileId
  ) {
    TechnicianDetailResponse technician =
      customerTechnicianService.getTechnicianDetail(technicianProfileId);

    return ResponseEntity.ok(
      ApiResponse.success(
        "Technician retrieved successfully",
        technician
      )
    );
  }
}
