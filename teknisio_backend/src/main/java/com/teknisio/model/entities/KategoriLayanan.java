package com.teknisio.model.entities;

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
@Table(name = "kategori_layanan")
public class KategoriLayanan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_kategori", nullable = false, updatable = false)
    private UUID idKategori;

    @Column(name = "nama_kategori", nullable = false, length = 100)
    private String namaKategori;

    @Column(name = "icon", columnDefinition = "TEXT")
    private String icon;

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
