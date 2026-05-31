package com.teknisio.controllers;

import com.teknisio.common.response.ApiResponse;
import com.teknisio.dto.requests.CreatePermintaanLayananRequest;
import com.teknisio.model.entities.PermintaanLayanan;
import com.teknisio.services.PermintaanLayananService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/permintaan")
public class PermintaanLayananController {

    private final PermintaanLayananService permintaanService;

    public PermintaanLayananController(PermintaanLayananService permintaanService) {
        this.permintaanService = permintaanService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PermintaanLayanan>> createPermintaan(
            // Asumsi UUID dari JWT token di controller sebenarnya, untuk testing kita gunakan request parameter atau mock
            @RequestParam UUID userId, 
            @Valid @RequestBody CreatePermintaanLayananRequest request) {
        
        PermintaanLayanan permintaan = permintaanService.createPermintaan(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Berhasil membuat permintaan layanan", permintaan));
    }
}
