package com.teknisio.backend.repositories;

import com.teknisio.backend.entities.Pesan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PesanRepository extends JpaRepository<Pesan, UUID> {
    List<Pesan> findByPermintaan_IdPermintaanAndDeletedAtIsNullOrderByCreatedAtAsc(
            UUID idPermintaan
    );

    long countByPermintaan_IdPermintaanAndReadAtIsNullAndDeletedAtIsNull(UUID idPermintaan);
}
