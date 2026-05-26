package com.teknisio.backend.repositories;

import com.teknisio.backend.entities.MediaPermintaan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MediaPermintaanRepository extends JpaRepository<MediaPermintaan, UUID> {
    List<MediaPermintaan> findByPermintaan_IdPermintaanAndDeletedAtIsNull(UUID idPermintaan);
}
