package com.teknisio.backend.entities;

import com.teknisio.backend.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

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
    @Column(name = "status_sebelum")
    private RequestStatus statusSebelum;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_sesudah", nullable = false)
    private RequestStatus statusSesudah;

    @Column(name = "catatan", columnDefinition = "TEXT")
    private String catatan;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}
