package com.teknisio.backend.repositories;

import com.teknisio.backend.entities.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
    Optional<Review> findByPermintaan_IdPermintaan(UUID idPermintaan);

    boolean existsByPermintaan_IdPermintaan(UUID idPermintaan);

    List<Review> findByTeknisiProfile_IdTeknisiProfileOrderByCreatedAtDesc(UUID idTeknisiProfile);
}
