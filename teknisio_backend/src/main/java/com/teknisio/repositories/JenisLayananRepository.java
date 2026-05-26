package com.teknisio.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.teknisio.model.entities.JenisLayanan;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JenisLayananRepository extends JpaRepository<JenisLayanan, UUID> {
    List<JenisLayanan> findByAktifTrueAndDeletedAtIsNull();
    List<JenisLayanan> findByKategori_IdKategoriAndAktifTrueAndDeletedAtIsNull(UUID idKategori);

    Optional<JenisLayanan> findByIdLayananAndAktifTrueAndDeletedAtIsNull(UUID idLayanan);
}
