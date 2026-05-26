package com.teknisio.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.teknisio.model.entities.LogAktivitas;

import java.util.List;
import java.util.UUID;

public interface LogAktivitasRepository extends JpaRepository<LogAktivitas, UUID> {
    List<LogAktivitas> findByUser_IdUserOrderByCreatedAtDesc(UUID idUser);
}
