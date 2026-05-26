package com.teknisio.repositories;

import com.teknisio.model.entities.JadwalTeknisi;
import com.teknisio.model.enums.HariEnum;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JadwalTeknisiRepository extends JpaRepository<JadwalTeknisi, UUID> {
    List<JadwalTeknisi> findByTeknisiProfile_IdTeknisiProfileAndAktifTrue(UUID idTeknisiProfile);
    List<JadwalTeknisi> findByTeknisiProfile_IdTeknisiProfileAndHariAndAktifTrue(
            UUID idTeknisiProfile,
            HariEnum hari
    );
}
