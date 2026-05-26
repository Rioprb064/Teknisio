package com.teknisio.backend.entities;

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
@Table(name = "media_permintaan")
public class MediaPermintaan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_media", nullable = false, updatable = false)
    private UUID idMedia;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_permintaan", nullable = false)
    private PermintaanLayanan permintaan;

    @Column(name = "url_file", nullable = false, columnDefinition = "TEXT")
    private String urlFile;

    @Column(name = "tipe_file", length = 50)
    private String tipeFile;

    @Column(name = "mime_type", length = 100)
    private String mimeType;

    @Column(name = "ukuran_file")
    private Long ukuranFile;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;
}
