package com.teknisio.model.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import org.hibernate.annotations.UpdateTimestamp;

import com.teknisio.model.enums.TeknisiStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "teknisi_profile")
public class TeknisiProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_teknisi_profile", nullable = false, updatable = false)
    private UUID idTeknisiProfile;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_user", nullable = false, unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "status_ketersediaan", nullable = false, columnDefinition = "teknisi_status")
    @Builder.Default
    private TeknisiStatus statusKetersediaan = TeknisiStatus.OFFLINE;

    @Column(name = "rating_avg", nullable = false, precision = 3, scale = 2)
    @Builder.Default
    private BigDecimal ratingAvg = BigDecimal.ZERO;

    @Column(name = "rating_count", nullable = false)
    @Builder.Default
    private Integer ratingCount = 0;

    @Column(name = "total_pekerjaan", nullable = false)
    @Builder.Default
    private Integer totalPekerjaan = 0;

    @Column(name = "deskripsi", columnDefinition = "TEXT")
    private String deskripsi;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
