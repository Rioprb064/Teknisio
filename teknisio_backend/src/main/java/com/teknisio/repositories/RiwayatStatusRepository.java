package com.teknisio.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.teknisio.model.entities.RiwayatStatus;

import java.util.List;
import java.util.UUID;

public interface RiwayatStatusRepository extends JpaRepository<RiwayatStatus, UUID> {
    List<RiwayatStatus> findByPermintaan_IdPermintaanOrderByCreatedAtAsc(UUID idPermintaan);
}
