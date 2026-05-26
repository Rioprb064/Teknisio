package com.teknisio.backend.entities;

import com.teknisio.backend.enums.PesanType;
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
@Table(name = "pesan")
public class Pesan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_pesan", nullable = false, updatable = false)
    private UUID idPesan;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_permintaan", nullable = false)
    private PermintaanLayanan permintaan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pengirim")
    private User pengirim;

    @Column(name = "isi_pesan", columnDefinition = "TEXT")
    private String isiPesan;

    @Column(name = "file_url", columnDefinition = "TEXT")
    private String fileUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipe_pesan", nullable = false)
    private PesanType tipePesan;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "read_at")
    private OffsetDateTime readAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;
}
