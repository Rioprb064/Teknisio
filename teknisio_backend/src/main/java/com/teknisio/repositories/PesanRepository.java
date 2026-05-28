package com.teknisio.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.teknisio.model.entities.Pesan;

import java.util.List;
import java.util.UUID;

public interface PesanRepository extends JpaRepository<Pesan, UUID> {
    List<Pesan> findByPermintaan_IdPermintaanAndDeletedAtIsNullOrderByCreatedAtAsc(
        UUID idPermintaan
    );

    long countByPermintaan_IdPermintaanAndReadAtIsNullAndDeletedAtIsNull(UUID idPermintaan);
}
