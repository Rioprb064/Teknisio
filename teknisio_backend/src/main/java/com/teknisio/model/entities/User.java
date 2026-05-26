package com.teknisio.model.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import org.hibernate.annotations.UpdateTimestamp;

import com.teknisio.model.enums.UserRole;
import com.teknisio.model.enums.UserStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_user", nullable = false, updatable = false)
    private UUID idUser;

    @Column(name = "nama", nullable = false, length = 100)
    private String nama;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "no_telepon", nullable = false, length = 20)
    private String noTelepon;

    @Column(name = "password_hash", nullable = false, columnDefinition = "TEXT")
    private String passwordHash;

    @Column(name = "foto_profil", columnDefinition = "TEXT")
    private String fotoProfil;

    @Column(name = "alamat", columnDefinition = "TEXT")
    private String alamat;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "role", nullable = false, columnDefinition = "user_role")
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "status_akun", nullable = false, columnDefinition = "user_status")
    @Builder.Default
    private UserStatus statusAkun = UserStatus.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Column(name = "last_login")
    private OffsetDateTime lastLogin;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;
}
