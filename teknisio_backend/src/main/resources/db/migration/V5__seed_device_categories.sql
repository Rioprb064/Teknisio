INSERT INTO kategori_layanan (nama_kategori, icon, aktif)
VALUES
  ('Air Conditioner', 'air-conditioner', true),
  ('Refrigerator', 'refrigerator', true),
  ('Washing Machine', 'washing-machine', true),
  ('Television', 'television', true),
  ('Fan', 'fan', true),
  ('Rice Cooker', 'rice-cooker', true),
  ('Oven', 'oven', true),
  ('Mixer', 'mixer', true)
ON CONFLICT (LOWER(nama_kategori))
WHERE deleted_at IS NULL
DO UPDATE SET
  icon = EXCLUDED.icon,
  aktif = true,
  updated_at = CURRENT_TIMESTAMP;
