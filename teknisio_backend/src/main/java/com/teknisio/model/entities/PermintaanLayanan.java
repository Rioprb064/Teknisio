package com.teknisio.model.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.teknisio.model.enums.RequestStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "permintaan_layanan")
public class PermintaanLayanan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_permintaan", nullable = false, updatable = false)
    private UUID idPermintaan;

    @Column(name = "kode_permintaan", nullable = false, unique = true, length = 50, insertable = false, updatable = false)
    private String kodePermintaan;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_pengguna", nullable = false)
    private User pengguna;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_teknisi_profile")
    private TeknisiProfile teknisiProfile;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_layanan", nullable = false)
    private JenisLayanan layanan;

    @Column(name = "latitude", precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(name = "alamat", nullable = false, columnDefinition = "TEXT")
    private String alamat;

    @Column(name = "detail_alamat", columnDefinition = "TEXT")
    private String detailAlamat;

    @Column(name = "deskripsi_masalah", nullable = false, columnDefinition = "TEXT")
    private String deskripsiMasalah;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private RequestStatus status = RequestStatus.WAITING;

    @Column(name = "estimasi_biaya", precision = 12, scale = 2)
    private BigDecimal estimasiBiaya;

    @Column(name = "biaya_akhir", precision = 12, scale = 2)
    private BigDecimal biayaAkhir;

    @Column(name = "catatan_teknisi", columnDefinition = "TEXT")
    private String catatanTeknisi;

    @Column(name = "alasan_batal", columnDefinition = "TEXT")
    private String alasanBatal;

    @Column(name = "waktu_permintaan", nullable = false)
    private OffsetDateTime waktuPermintaan;

    @Column(name = "waktu_diterima")
    private OffsetDateTime waktuDiterima;

    @Column(name = "waktu_diproses")
    private OffsetDateTime waktuDiproses;

    @Column(name = "waktu_selesai")
    private OffsetDateTime waktuSelesai;

    @Column(name = "waktu_dibatalkan")
    private OffsetDateTime waktuDibatalkan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diubah_oleh_terakhir")
    private User diubahOlehTerakhir;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (waktuPermintaan == null) {
            waktuPermintaan = OffsetDateTime.now();
        }
    }
}
