package com.teknisio.backend.repositories;

import com.teknisio.backend.entities.LogAktivitas;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LogAktivitasRepository extends JpaRepository<LogAktivitas, UUID> {
    List<LogAktivitas> findByUser_IdUserOrderByCreatedAtDesc(UUID idUser);
}
