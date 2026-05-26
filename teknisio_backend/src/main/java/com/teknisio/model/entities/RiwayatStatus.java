package com.teknisio.model.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import com.teknisio.model.enums.RequestStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "riwayat_status")
public class RiwayatStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_riwayat", nullable = false, updatable = false)
    private UUID idRiwayat;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_permintaan", nullable = false)
    private PermintaanLayanan permintaan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diubah_oleh")
    private User diubahOleh;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "status_sebelum", columnDefinition = "request_status")
    private RequestStatus statusSebelum;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "status_sesudah", nullable = false, columnDefinition = "request_status")
    private RequestStatus statusSesudah;

    @Column(name = "catatan", columnDefinition = "TEXT")
    private String catatan;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}
