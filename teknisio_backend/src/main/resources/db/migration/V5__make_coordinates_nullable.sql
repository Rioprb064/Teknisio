-- Harus dibuat karena kita banting stir dari mobile ke desktop
ALTER TABLE permintaan_layanan
  ALTER COLUMN latitude DROP NOT NULL,
  ALTER COLUMN longitude DROP NOT NULL;

