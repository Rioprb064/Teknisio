package com.teknisio.model.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import com.teknisio.model.enums.NotificationReferenceType;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "notifikasi")
public class Notifikasi {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_notifikasi", nullable = false, updatable = false)
    private UUID idNotifikasi;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    @Column(name = "reference_id")
    private UUID referenceId;

    @Column(name = "judul", nullable = false, length = 255)
    private String judul;

    @Column(name = "isi", nullable = false, columnDefinition = "TEXT")
    private String isi;

    @Column(name = "tipe", nullable = false, length = 100)
    private String tipe;

    @Enumerated(EnumType.STRING)
    @Column(name = "reference_type")
    private NotificationReferenceType referenceType;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "read_at")
    private OffsetDateTime readAt;
}
