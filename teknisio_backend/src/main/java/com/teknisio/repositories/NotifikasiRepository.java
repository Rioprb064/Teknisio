package com.teknisio.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.teknisio.model.entities.Notifikasi;

import java.util.List;
import java.util.UUID;

public interface NotifikasiRepository extends JpaRepository<Notifikasi, UUID> {
    List<Notifikasi> findByUser_IdUserOrderByCreatedAtDesc(UUID idUser);

    long countByUser_IdUserAndReadAtIsNull(UUID idUser);
}
