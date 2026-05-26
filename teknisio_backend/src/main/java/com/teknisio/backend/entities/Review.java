package com.teknisio.backend.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "review")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_review", nullable = false, updatable = false)
    private UUID idReview;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_permintaan", nullable = false, unique = true)
    private PermintaanLayanan permintaan;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_pengguna", nullable = false)
    private User pengguna;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_teknisi_profile", nullable = false)
    private TeknisiProfile teknisiProfile;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "komentar", columnDefinition = "TEXT")
    private String komentar;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
