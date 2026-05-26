package com.teknisio.model.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "jenis_layanan")
public class JenisLayanan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_layanan", nullable = false, updatable = false)
    private UUID idLayanan;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_kategori", nullable = false)
    private KategoriLayanan kategori;

    @Column(name = "nama_layanan", nullable = false, length = 100)
    private String namaLayanan;

    @Column(name = "deskripsi", columnDefinition = "TEXT")
    private String deskripsi;

    @Column(name = "estimasi_menit", nullable = false)
    private Integer estimasiMenit;

    @Column(name = "harga_min", precision = 12, scale = 2)
    private BigDecimal hargaMin;

    @Column(name = "harga_max", precision = 12, scale = 2)
    private BigDecimal hargaMax;

    @Column(name = "aktif", nullable = false)
    @Builder.Default
    private Boolean aktif = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;
}
