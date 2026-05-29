package com.teknisio.controllers;

import com.teknisio.common.response.ApiResponse;
import com.teknisio.model.entities.KategoriLayanan;
import com.teknisio.services.KategoriLayananService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/kategori")
public class KategoriLayananController {

    private final KategoriLayananService kategoriLayananService;

    public KategoriLayananController(KategoriLayananService kategoriLayananService) {
        this.kategoriLayananService = kategoriLayananService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<KategoriLayanan>>> getAllKategori() {
        List<KategoriLayanan> kategoriList = kategoriLayananService.getAllKategoriAktif();
        return ResponseEntity.ok(ApiResponse.success("Berhasil mengambil data kategori", kategoriList));
    }
}
