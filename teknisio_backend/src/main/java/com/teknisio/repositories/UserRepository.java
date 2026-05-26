package com.teknisio.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.teknisio.model.entities.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByIdUserAndDeletedAtIsNull(UUID idUser);
    Optional<User> findByEmailIgnoreCaseAndDeletedAtIsNull(String email);
    Optional<User> findByNoTeleponAndDeletedAtIsNull(String noTelepon);

    boolean existsByEmailIgnoreCaseAndDeletedAtIsNull(String email);
    boolean existsByNoTeleponAndDeletedAtIsNull(String noTelepon);
    boolean existsByEmailIgnoreCaseAndIdUserNotAndDeletedAtIsNull(String email, UUID idUser);
    boolean existsByNoTeleponAndIdUserNotAndDeletedAtIsNull(String noTelepon, UUID idUser);
}
