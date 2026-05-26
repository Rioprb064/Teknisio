package com.teknisio.backend.repositories;

import com.teknisio.backend.entities.KategoriLayanan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface KategoriLayananRepository extends JpaRepository<KategoriLayanan, UUID> {
    List<KategoriLayanan> findByAktifTrueAndDeletedAtIsNull();

    Optional<KategoriLayanan> findByIdKategoriAndAktifTrueAndDeletedAtIsNull(UUID idKategori);
}
