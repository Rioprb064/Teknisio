package com.teknisio.backend.repositories;

import com.teknisio.backend.entities.TeknisiProfile;
import com.teknisio.backend.enums.TeknisiStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TeknisiProfileRepository extends JpaRepository<TeknisiProfile, UUID> {
    Optional<TeknisiProfile> findByUser_IdUser(UUID idUser);

    boolean existsByUser_IdUser(UUID idUser);

    List<TeknisiProfile> findByStatusKetersediaan(TeknisiStatus statusKetersediaan);
}
