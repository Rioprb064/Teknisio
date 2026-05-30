-- ============================================================
-- Teknisio Migration V6: Seed Dummy Data
-- ============================================================

-- 1. Insert Kategori Layanan
INSERT INTO kategori_layanan (id_kategori, nama_kategori, icon, aktif) VALUES
('b3b3b3b3-b3b3-b3b3-b3b3-b3b3b3b3b3b1', 'Elektronik Rumah Tangga', 'ic_home_appliances', true),
('b3b3b3b3-b3b3-b3b3-b3b3-b3b3b3b3b3b2', 'Gadget & Komputer', 'ic_gadget', true)
ON CONFLICT (id_kategori) DO NOTHING;

-- 2. Insert Jenis Layanan
INSERT INTO jenis_layanan (id_layanan, id_kategori, nama_layanan, deskripsi, estimasi_menit, harga_min, harga_max, aktif) VALUES
('c3c3c3c3-c3c3-c3c3-c3c3-c3c3c3c3c3c1', 'b3b3b3b3-b3b3-b3b3-b3b3-b3b3b3b3b3b1', 'Service AC', 'Pengecekan dan perbaikan AC tidak dingin/bocor', 60, 150000, 500000, true),
('c3c3c3c3-c3c3-c3c3-c3c3-c3c3c3c3c3c2', 'b3b3b3b3-b3b3-b3b3-b3b3-b3b3b3b3b3b1', 'Service Kulkas', 'Perbaikan Kulkas tidak dingin', 90, 200000, 600000, true),
('c3c3c3c3-c3c3-c3c3-c3c3-c3c3c3c3c3c3', 'b3b3b3b3-b3b3-b3b3-b3b3-b3b3b3b3b3b2', 'Service Laptop', 'Perbaikan hardware/software laptop', 120, 100000, 1000000, true)
ON CONFLICT (id_layanan) DO NOTHING;

-- 3. Insert Users (password: password123)
-- Hash untuk password123: $2a$10$wE0pIn1F5/b9IHT2kG8tK.QxXyU3R6i1S/O3U7G3z02x.oM6Q1DGa

-- Customer
INSERT INTO users (id_user, nama, email, no_telepon, password_hash, role, status_akun, alamat) VALUES
('a1a1a1a1-a1a1-a1a1-a1a1-a1a1a1a1a1a1', 'Budi Customer', 'budi@example.com', '081234567890', '$2a$10$wE0pIn1F5/b9IHT2kG8tK.QxXyU3R6i1S/O3U7G3z02x.oM6Q1DGa', 'CUSTOMER', 'ACTIVE', 'Jl. Customer No. 1')
ON CONFLICT (id_user) DO NOTHING;

-- Teknisi
INSERT INTO users (id_user, nama, email, no_telepon, password_hash, role, status_akun, alamat) VALUES
('a2a2a2a2-a2a2-a2a2-a2a2-a2a2a2a2a2a2', 'Andi Teknisi', 'andi@example.com', '081298765432', '$2a$10$wE0pIn1F5/b9IHT2kG8tK.QxXyU3R6i1S/O3U7G3z02x.oM6Q1DGa', 'TEKNISI', 'ACTIVE', 'Jl. Teknisi No. 2')
ON CONFLICT (id_user) DO NOTHING;

-- 4. Insert Teknisi Profile
INSERT INTO teknisi_profile (id_teknisi_profile, id_user, status_ketersediaan, rating_avg, rating_count, total_pekerjaan, deskripsi) VALUES
('d4d4d4d4-d4d4-d4d4-d4d4-d4d4d4d4d4d4', 'a2a2a2a2-a2a2-a2a2-a2a2-a2a2a2a2a2a2', 'ONLINE', 4.8, 15, 20, 'Spesialis AC dan Kulkas berpengalaman lebih dari 5 tahun.')
ON CONFLICT (id_teknisi_profile) DO NOTHING;

-- 5. Insert Teknisi Layanan
INSERT INTO teknisi_layanan (id_teknisi_profile, id_layanan, aktif) VALUES
('d4d4d4d4-d4d4-d4d4-d4d4-d4d4d4d4d4d4', 'c3c3c3c3-c3c3-c3c3-c3c3-c3c3c3c3c3c1', true),
('d4d4d4d4-d4d4-d4d4-d4d4-d4d4d4d4d4d4', 'c3c3c3c3-c3c3-c3c3-c3c3-c3c3c3c3c3c2', true)
ON CONFLICT (id_teknisi_profile, id_layanan) DO NOTHING;
