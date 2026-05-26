package com.teknisio.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.teknisio.model.entities.MediaPermintaan;

import java.util.List;
import java.util.UUID;

public interface MediaPermintaanRepository extends JpaRepository<MediaPermintaan, UUID> {
    List<MediaPermintaan> findByPermintaan_IdPermintaanAndDeletedAtIsNull(UUID idPermintaan);
}
