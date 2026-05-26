package com.teknisio.backend.repositories;

import com.teknisio.backend.entities.PermintaanLayanan;
import com.teknisio.backend.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PermintaanLayananRepository extends JpaRepository<PermintaanLayanan, UUID> {
    Optional<PermintaanLayanan> findByKodePermintaan(String kodePermintaan);

    List<PermintaanLayanan> findByPengguna_IdUserOrderByCreatedAtDesc(UUID idUser);
    List<PermintaanLayanan> findByPengguna_IdUserAndStatusOrderByCreatedAtDesc(
        UUID idUser,
        RequestStatus status
    );

    List<PermintaanLayanan> findByTeknisiProfile_IdTeknisiProfileOrderByCreatedAtDesc(
        UUID idTeknisiProfile
    );

    List<PermintaanLayanan> findByTeknisiProfile_IdTeknisiProfileAndStatusOrderByCreatedAtDesc(
        UUID idTeknisiProfile,
        RequestStatus status
    );

    List<PermintaanLayanan> findByStatusOrderByCreatedAtDesc(RequestStatus status);
}
