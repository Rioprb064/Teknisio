package com.teknisio.backend.repositories;

import com.teknisio.backend.entities.RiwayatStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RiwayatStatusRepository extends JpaRepository<RiwayatStatus, UUID> {
    List<RiwayatStatus> findByPermintaan_IdPermintaanOrderByCreatedAtAsc(UUID idPermintaan);
}
