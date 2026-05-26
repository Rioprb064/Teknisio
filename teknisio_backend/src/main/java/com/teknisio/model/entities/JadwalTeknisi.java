package com.teknisio.model.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.teknisio.model.enums.HariEnum;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "jadwal_teknisi")
public class JadwalTeknisi {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_jadwal", nullable = false, updatable = false)
    private UUID idJadwal;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_teknisi_profile", nullable = false)
    private TeknisiProfile teknisiProfile;

    @Enumerated(EnumType.STRING)
    @Column(name = "hari", nullable = false)
    private HariEnum hari;

    @Column(name = "jam_mulai", nullable = false)
    private LocalTime jamMulai;

    @Column(name = "jam_selesai", nullable = false)
    private LocalTime jamSelesai;

    @Column(name = "aktif", nullable = false)
    @Builder.Default
    private Boolean aktif = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
