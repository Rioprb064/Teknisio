package com.teknisio.backend.repositories;

import com.teknisio.backend.entities.TeknisiLayanan;
import com.teknisio.backend.entities.TeknisiLayananId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TeknisiLayananRepository extends JpaRepository<TeknisiLayanan, TeknisiLayananId> {
    List<TeknisiLayanan> findByTeknisiProfile_IdTeknisiProfileAndAktifTrue(UUID idTeknisiProfile);
    List<TeknisiLayanan> findByLayanan_IdLayananAndAktifTrue(UUID idLayanan);

    Optional<TeknisiLayanan> findById_IdTeknisiProfileAndId_IdLayanan(
        UUID idTeknisiProfile,
        UUID idLayanan
    );

    boolean existsById_IdTeknisiProfileAndId_IdLayanan(
        UUID idTeknisiProfile,
        UUID idLayanan
    );
}
