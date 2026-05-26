package com.teknisio.backend.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "teknisi_layanan")
public class TeknisiLayanan {

    @EmbeddedId
    private TeknisiLayananId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("idTeknisiProfile")
    @JoinColumn(name = "id_teknisi_profile", nullable = false)
    private TeknisiProfile teknisiProfile;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("idLayanan")
    @JoinColumn(name = "id_layanan", nullable = false)
    private JenisLayanan layanan;

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
