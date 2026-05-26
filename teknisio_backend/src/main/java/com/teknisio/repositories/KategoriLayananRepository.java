package com.teknisio.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.teknisio.model.entities.KategoriLayanan;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface KategoriLayananRepository extends JpaRepository<KategoriLayanan, UUID> {
    List<KategoriLayanan> findByAktifTrueAndDeletedAtIsNull();

    Optional<KategoriLayanan> findByIdKategoriAndAktifTrueAndDeletedAtIsNull(UUID idKategori);
}
