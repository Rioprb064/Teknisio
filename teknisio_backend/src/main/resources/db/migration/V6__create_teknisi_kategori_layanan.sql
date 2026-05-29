-- ============================================================
-- Teknisio Migration V6: Technician Device Category Skills
-- ============================================================

CREATE TABLE teknisi_kategori_layanan (
  id_teknisi_profile UUID NOT NULL,
  id_kategori UUID NOT NULL,
  aktif BOOLEAN NOT NULL DEFAULT TRUE,

  created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

  PRIMARY KEY (id_teknisi_profile, id_kategori),

  CONSTRAINT fk_teknisi_kategori_teknisi
    FOREIGN KEY (id_teknisi_profile)
    REFERENCES teknisi_profile (id_teknisi_profile)
    ON DELETE CASCADE,

  CONSTRAINT fk_teknisi_kategori_kategori
    FOREIGN KEY (id_kategori)
    REFERENCES kategori_layanan (id_kategori)
    ON DELETE CASCADE
);

CREATE INDEX idx_teknisi_kategori_layanan_kategori
  ON teknisi_kategori_layanan (id_kategori);

CREATE INDEX idx_teknisi_kategori_layanan_teknisi
  ON teknisi_kategori_layanan (id_teknisi_profile);

CREATE TRIGGER trg_teknisi_kategori_layanan_updated_at
BEFORE UPDATE ON teknisi_kategori_layanan
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();
