package com.teknisio.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "teknisi_kategori_layanan")
public class TeknisiKategoriLayanan {
  @EmbeddedId
  private TeknisiKategoriLayananId id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @MapsId("idTeknisiProfile")
  @JoinColumn(name = "id_teknisi_profile", nullable = false)
  private TeknisiProfile teknisiProfile;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @MapsId("idKategori")
  @JoinColumn(name = "id_kategori", nullable = false)
  private KategoriLayanan kategori;

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
