package com.teknisio.services;

import com.teknisio.common.exception.ResourceNotFoundException;
import com.teknisio.dto.requests.CreatePermintaanLayananRequest;
import com.teknisio.model.entities.JenisLayanan;
import com.teknisio.model.entities.PermintaanLayanan;
import com.teknisio.model.entities.User;
import com.teknisio.model.enums.RequestStatus;
import com.teknisio.repositories.JenisLayananRepository;
import com.teknisio.repositories.PermintaanLayananRepository;
import com.teknisio.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class PermintaanLayananService {

    private final PermintaanLayananRepository permintaanRepository;
    private final UserRepository userRepository;
    private final JenisLayananRepository jenisLayananRepository;

    public PermintaanLayananService(PermintaanLayananRepository permintaanRepository,
                                    UserRepository userRepository,
                                    JenisLayananRepository jenisLayananRepository) {
        this.permintaanRepository = permintaanRepository;
        this.userRepository = userRepository;
        this.jenisLayananRepository = jenisLayananRepository;
    }

    @Transactional
    public PermintaanLayanan createPermintaan(UUID userId, CreatePermintaanLayananRequest request) {
        User pengguna = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User tidak ditemukan"));

        JenisLayanan layanan = jenisLayananRepository.findById(request.idLayanan())
                .orElseThrow(() -> new ResourceNotFoundException("Layanan tidak ditemukan"));

        // Generate kode permintaan sederhana
        String kodePermintaan = "REQ-" + System.currentTimeMillis();

        PermintaanLayanan permintaan = PermintaanLayanan.builder()
                .kodePermintaan(kodePermintaan)
                .pengguna(pengguna)
                .layanan(layanan)
                .latitude(request.latitude())
                .longitude(request.longitude())
                .alamat(request.alamat())
                .detailAlamat(request.detailAlamat())
                .deskripsiMasalah(request.deskripsiMasalah())
                .status(RequestStatus.WAITING)
                .waktuPermintaan(OffsetDateTime.now())
                .build();

        return permintaanRepository.save(permintaan);
    }
}
