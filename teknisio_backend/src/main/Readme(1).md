# Roadmap Backend Teknisio — MVP Stable Release

Roadmap ini adalah versi yang dipangkas dan dirapikan untuk target **MVP stable release** aplikasi Teknisio. Fokusnya bukan semua fitur yang mungkin dibuat, tetapi fitur yang membuat flow utama aplikasi bisa dipakai end-to-end oleh customer dan technician.

Detail kontrak API yang lebih lengkap sebaiknya tetap dibuat terpisah di `API_CONTRACT.md`.

---

## Prinsip Utama

- Backend hanya satu untuk semua client.
- Client boleh berbeda: JavaFX Desktop, Android Java, atau client lain.
- Backend melayani semuanya melalui REST API + JSON.
- Data yang keluar/masuk API wajib menggunakan field English.
- Nama package, entity, tabel, dan function internal boleh tetap menggunakan bahasa Indonesia jika sudah ada.
- Jangan membuat endpoint khusus Android atau khusus Desktop.
- Jangan expose entity database langsung sebagai response API.
- DTO adalah kontrak antara backend dan frontend.
- Endpoint resmi menggunakan English.
- Endpoint Indonesia seperti `/api/kategori`, `/api/permintaan`, atau `/api/teknisi` tidak dipakai sebagai kontrak resmi.

---

## Flow Final MVP Stable

```text
Customer register / login
↓
Customer melihat daftar alat elektronik
↓
Customer memilih device category, misalnya Air Conditioner
↓
Backend menampilkan technician yang punya skill Air Conditioner
↓
Customer bisa filter technician berdasarkan availabilityStatus
↓
Customer bisa sort technician berdasarkan name, rating, atau totalJobs
↓
Customer memilih technician
↓
Customer melihat detail technician dan supportedDeviceCategories
↓
Customer memilih satu atau lebih device category yang dikuasai technician
↓
Customer mengisi alamat dan issueDescription
↓
Customer membuat service request
↓
Technician melihat request masuk
↓
Technician accept atau reject request
↓
Jika accepted, technician bisa start pengerjaan
↓
Technician complete pengerjaan
↓
Status history tercatat
↓
Customer memberi review setelah request completed
```

Untuk MVP stable, customer **tidak memilih detail jenis servis** seperti:

```text
AC Cleaning
AC Repair
Refrigerator Freon Refill
Washing Machine Cleaning
```

Customer hanya memilih `deviceCategoryIds` dan mengisi `issueDescription`.

---

## Istilah Resmi API

| Konsep | Nama API | Nama internal yang boleh tetap dipakai |
|---|---|---|
| Alat elektronik | `deviceCategory` | `KategoriLayanan` |
| Keahlian technician | `technicianDeviceCategory` | `TeknisiKategoriLayanan` |
| Permintaan layanan | `serviceRequest` | `PermintaanLayanan` |
| Kategori yang dipilih dalam order | `selectedDeviceCategories` | `PermintaanLayananKategori` |
| Deskripsi masalah | `issueDescription` | `deskripsiMasalah` |
| Technician | `technician` | `Teknisi` |
| Status request | `serviceRequestStatus` | `StatusPermintaan` |
| Riwayat status | `statusHistory` | `RiwayatStatus` |
| Notifikasi | `notification` | `Notifikasi` |
| Review | `review` | `Review` |

Catatan:

- `JenisLayanan` dan `TeknisiLayanan` boleh tetap ada sebagai legacy.
- Untuk MVP stable, flow tidak memakai `jenis_layanan`.
- Jika nanti fitur berkembang, `jenis_layanan` bisa dipakai lagi untuk detail layanan spesifik.

---

## Legend Status

- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Sudah selesai dan sudah dites manual.
- ![todo](https://img.shields.io/badge/%5Btodo%5D-lightgrey?style=flat-square) Belum dikerjakan.
- ![ongoing](https://img.shields.io/badge/%5Bongoing%5D-blue?style=flat-square) Sebagian sudah ada, tapi belum selesai penuh.
- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Ditunda setelah MVP stable.
- ![legacy](https://img.shields.io/badge/%5Blegacy%5D-lightgrey?style=flat-square) Masih ada di codebase, tapi tidak dipakai di MVP stable.

---

# 0. Fondasi Backend

Target: backend punya struktur rapi, response seragam, error handling, repository, dan siap dikembangkan per modul.

- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) **BE-00 [MVP] Rapikan struktur package**
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Package `config`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Package `model`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Package `repositories`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Package `dto`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Package `services`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Package `controllers`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Package `security`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Package `common.response`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Package `common.exception`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Package `common.util`

- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) **BE-01 [MVP] Global response format**
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Buat `ApiResponse<T>`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Response sukses punya `success`, `message`, `data`, `errors`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Response error punya `success`, `message`, `data`, `errors`

- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) **BE-02 [MVP] Global exception handler**
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Handle validation error
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Handle not found
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Handle bad request
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Handle unauthorized
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Handle forbidden
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Handle internal server error

- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) **BE-03 [MVP] DTO validation**
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Gunakan `@NotBlank`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Gunakan `@NotNull`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Gunakan `@Email`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Gunakan `@Size`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Gunakan `@Pattern` jika perlu

- ![ongoing](https://img.shields.io/badge/%5Bongoing%5D-blue?style=flat-square) **BE-04 [MVP] Repository entity inti**
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) `UserRepository`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) `UserSessionRepository`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) `TeknisiProfileRepository`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) `KategoriLayananRepository`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) `TeknisiKategoriLayananRepository`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) `PermintaanLayananRepository`
  - ![ongoing](https://img.shields.io/badge/%5Bongoing%5D-blue?style=flat-square) `PermintaanLayananKategoriRepository`, dipakai saat service request multi kategori
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) `RiwayatStatusRepository`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) `NotifikasiRepository`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) `ReviewRepository`
  - ![legacy](https://img.shields.io/badge/%5Blegacy%5D-lightgrey?style=flat-square) `JenisLayananRepository`
  - ![legacy](https://img.shields.io/badge/%5Blegacy%5D-lightgrey?style=flat-square) `TeknisiLayananRepository`

- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) **BE-05 [MVP] Validasi koneksi database dan Flyway**
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Build sukses
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Boot run sukses
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Tabel terbentuk otomatis
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Tidak ada error migration

---

# 1. Auth dan Role Access

Target: user bisa register, login, logout, refresh token, dan akses endpoint sesuai role.

- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) **BE-10 [MVP] Register customer**
  - Endpoint: `POST /api/auth/register/customer`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi `name`, `email`, `phoneNumber`, `password`, `address`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Email unik
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Phone number unik
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Password disimpan dalam bentuk hash
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Role otomatis `CUSTOMER`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Status akun otomatis `ACTIVE`

- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) **BE-11 [MVP] Register technician**
  - Endpoint: `POST /api/auth/register/technician`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Simpan user role `TECHNICIAN`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Buat otomatis `teknisi_profile`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Status ketersediaan default `OFFLINE`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Rating default 0
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Total pekerjaan default 0
  - ![todo](https://img.shields.io/badge/%5Btodo%5D-lightgrey?style=flat-square) Keahlian technician diatur setelah register lewat endpoint skill technician

- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) **BE-12 [MVP] Login**
  - Endpoint: `POST /api/auth/login`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Login pakai email dan password
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Cek password dengan BCrypt
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Cek akun `ACTIVE`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Generate access token dan refresh token

- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) **BE-13 [MVP] JWT authentication**
  - Endpoint profil: `GET /api/auth/profile`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi JWT
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) JWT filter aktif
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Ambil user dari token

- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) **BE-14 [MVP Support] Refresh token**
  - Endpoint: `POST /api/auth/refresh`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi refresh token
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi session belum expired
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi session belum revoked
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Generate access token baru

- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) **BE-15 [MVP] Logout**
  - Endpoint: `POST /api/auth/logout`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Revoke session aktif
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Isi `revokedAt`

- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) **BE-16 [MVP] Password hashing**
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Gunakan BCrypt
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Jangan simpan password asli
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Jangan tampilkan `passwordHash` di response

- ![ongoing](https://img.shields.io/badge/%5Bongoing%5D-blue?style=flat-square) **BE-17 [MVP] Role-based access**
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Endpoint public boleh diakses tanpa login
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Endpoint profile wajib login
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Endpoint `/api/customers/**` hanya bisa diakses customer, sudah terbukti pada BE-24 sampai BE-26
  - ![todo](https://img.shields.io/badge/%5Btodo%5D-lightgrey?style=flat-square) Endpoint `/api/technicians/**` hanya bisa diakses technician, dibuktikan saat BE-33 sampai BE-37
  - ![todo](https://img.shields.io/badge/%5Btodo%5D-lightgrey?style=flat-square) Pastikan endpoint customer dan technician tidak bentrok

---

# 2. Customer Lihat Device Category

Target: customer bisa melihat daftar alat elektronik yang tersedia.

- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) **BE-20 [MVP] Seed data device category**
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Seed Air Conditioner
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Seed Refrigerator
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Seed Washing Machine
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Seed Television
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Seed Fan
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Seed Rice Cooker
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Seed Oven
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Seed Mixer
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Seed tidak duplikat

- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) **BE-21 [MVP] List device category aktif**
  - Endpoint: `GET /api/device-categories`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Endpoint public
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Hanya tampilkan `aktif = true`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Jangan tampilkan soft deleted data
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Response berisi `deviceCategoryId`, `name`, `icon`

- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) **BE-22 [MVP] Detail device category**
  - Endpoint: `GET /api/device-categories/{deviceCategoryId}`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi category ditemukan
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi category aktif
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi category belum soft delete

---

# 3. Customer Cari Technician

Target: customer bisa memilih device category lalu melihat technician yang punya skill sesuai category tersebut.

- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) **BE-23 [MVP] Relasi skill technician dengan device category**
  - Tabel: `teknisi_kategori_layanan`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Satu technician bisa punya banyak device category
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Satu device category bisa dimiliki banyak technician
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Relasi tidak duplikat
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Default `aktif = true`

- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) **BE-24 [MVP] Search technician by device category**
  - Endpoint: `GET /api/customers/technicians?deviceCategoryId={deviceCategoryId}`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Hanya customer login
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi `deviceCategoryId`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Tampilkan technician yang punya skill category tersebut
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Hanya relasi skill aktif
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Response berisi `technicianProfileId`, `name`, `profilePhoto`, `availabilityStatus`, `averageRating`, `ratingCount`, `totalJobs`, `supportedDeviceCategories`

- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) **BE-26 [MVP] Filter dan sort technician**
  - Endpoint: `GET /api/customers/technicians?deviceCategoryId={deviceCategoryId}&availabilityStatus=ONLINE&sort=rating`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Filter berdasarkan `availabilityStatus`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Sort berdasarkan `name`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Sort berdasarkan `rating`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Sort berdasarkan `totalJobs`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi invalid `availabilityStatus`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi invalid `sort`
  - ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Sort harga ditunda sampai data harga tersedia
  - ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Sort jarak ditunda sampai data latitude/longitude technician tersedia

---

# 4. Customer Lihat Detail Technician

Target: setelah memilih technician, customer bisa melihat detail technician dan semua skill yang dikuasai.

- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) **BE-25 / BE-32 [MVP] Detail technician untuk customer**
  - Endpoint: `GET /api/customers/technicians/{technicianProfileId}`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Hanya customer login
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Tanpa token return `Unauthorized`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Token technician return `Forbidden`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi technician ditemukan
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi akun technician aktif
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Return `technicianProfileId`, `name`, `profilePhoto`, `availabilityStatus`, `averageRating`, `ratingCount`, `totalJobs`, `description`, `supportedDeviceCategories`

---

# 5. Technician Update Status

Target: technician bisa mengatur status ketersediaannya, dan status ini dipakai customer saat filter technician.

- ![todo](https://img.shields.io/badge/%5Btodo%5D-lightgrey?style=flat-square) **BE-33 [MVP] Technician update availability status**
  - Endpoint: `PATCH /api/technicians/me/status`
  - Request body: `availabilityStatus`
  - [ ] Status bisa `ONLINE`
  - [ ] Status bisa `OFFLINE`
  - [ ] Status bisa `BUSY`
  - [ ] Status bisa `ON_LEAVE`
  - [ ] Hanya technician yang boleh akses
  - [ ] Customer token return `Forbidden`
  - [ ] Tanpa token return `Unauthorized`
  - [ ] Validasi invalid status
  - [ ] Update `status_ketersediaan` di `teknisi_profile`
  - [ ] Response menggunakan field English

---

# 6. Technician Kelola Keahlian Alat Elektronik

Target: technician bisa melihat, menambah, dan menonaktifkan device category skill miliknya sendiri.

- ![todo](https://img.shields.io/badge/%5Btodo%5D-lightgrey?style=flat-square) **BE-37 [MVP] Technician lihat skill sendiri**
  - Endpoint: `GET /api/technicians/me/device-categories`
  - [ ] Hanya technician yang boleh akses
  - [ ] Return semua skill aktif milik technician login
  - [ ] Jangan tampilkan kategori nonaktif
  - [ ] Jangan tampilkan kategori soft delete
  - [ ] Response menggunakan `deviceCategoryId`, `name`, `icon`

- ![todo](https://img.shields.io/badge/%5Btodo%5D-lightgrey?style=flat-square) **BE-35 [MVP] Technician tambah skill device category**
  - Endpoint: `POST /api/technicians/me/device-categories`
  - Request body: `deviceCategoryId`
  - [ ] Hanya technician yang boleh akses
  - [ ] Validasi category ditemukan
  - [ ] Validasi category aktif
  - [ ] Validasi category belum soft delete
  - [ ] Cegah duplikasi skill aktif
  - [ ] Jika relasi lama nonaktif, aktifkan ulang
  - [ ] Simpan ke `teknisi_kategori_layanan`
  - [ ] Response menggunakan field English

- ![todo](https://img.shields.io/badge/%5Btodo%5D-lightgrey?style=flat-square) **BE-36 [MVP] Technician nonaktifkan skill device category**
  - Endpoint: `DELETE /api/technicians/me/device-categories/{deviceCategoryId}`
  - [ ] Hanya technician yang boleh akses
  - [ ] Validasi relasi ditemukan
  - [ ] Validasi relasi milik technician login
  - [ ] Set `aktif = false`
  - [ ] Response menggunakan field English

---

# 7. Customer Membuat Service Request

Target: customer bisa membuat service request setelah memilih technician dan satu atau lebih device category dari skill technician tersebut.

- ![todo](https://img.shields.io/badge/%5Btodo%5D-lightgrey?style=flat-square) **BE-50 [MVP] Customer create service request**
  - Endpoint: `POST /api/service-requests`
  - [ ] Hanya customer yang boleh akses
  - [ ] Request body berisi `technicianProfileId`
  - [ ] Request body berisi `deviceCategoryIds`
  - [ ] Request body berisi `address`
  - [ ] Request body berisi `addressDetail` opsional
  - [ ] Request body berisi `latitude` opsional
  - [ ] Request body berisi `longitude` opsional
  - [ ] Request body berisi `issueDescription`
  - [ ] Validasi technician ditemukan
  - [ ] Validasi akun technician aktif
  - [ ] Validasi `deviceCategoryIds` minimal 1
  - [ ] Validasi semua device category ditemukan
  - [ ] Validasi semua device category aktif dan belum soft delete
  - [ ] Validasi technician memiliki semua skill sesuai `deviceCategoryIds`
  - [ ] Generate `requestCode`, contoh `REQ-178...`
  - [ ] Status awal `WAITING`
  - [ ] Simpan ke `permintaan_layanan`
  - [ ] Simpan selected categories ke `permintaan_layanan_kategori`
  - [ ] Catat status history awal
  - [ ] Buat notifikasi ke technician

- ![todo](https://img.shields.io/badge/%5Btodo%5D-lightgrey?style=flat-square) **BE-51 [MVP] Validasi lokasi dan masalah**
  - [ ] `address` wajib
  - [ ] `issueDescription` wajib
  - [ ] `addressDetail` opsional
  - [ ] `latitude` opsional untuk MVP
  - [ ] `longitude` opsional untuk MVP
  - [ ] Jika latitude dikirim, validasi range -90 sampai 90
  - [ ] Jika longitude dikirim, validasi range -180 sampai 180

- ![todo](https://img.shields.io/badge/%5Btodo%5D-lightgrey?style=flat-square) **BE-52 [MVP] Selected device categories pada service request**
  - Tabel: `permintaan_layanan_kategori`
  - [ ] Satu service request bisa punya banyak selected device categories
  - [ ] Satu service request tetap hanya punya satu technician
  - [ ] Device category yang dipilih harus termasuk skill technician
  - [ ] Tidak boleh ada duplikasi category dalam satu request
  - [ ] Relasi menggunakan `id_permintaan`
  - [ ] Relasi menggunakan `id_kategori`

---

# 8. Customer Lihat Riwayat dan Detail Request

Target: customer bisa melihat semua request miliknya dan detail request tertentu.

- ![todo](https://img.shields.io/badge/%5Btodo%5D-lightgrey?style=flat-square) **BE-53 [MVP] Customer lihat riwayat service request**
  - Endpoint: `GET /api/customers/service-requests`
  - [ ] Hanya customer yang boleh akses
  - [ ] Hanya tampilkan request milik customer login
  - [ ] Bisa pagination
  - [ ] Bisa filter status opsional
  - [ ] Urutkan dari terbaru
  - [ ] Sertakan technician summary
  - [ ] Sertakan `selectedDeviceCategories`
  - [ ] Sertakan `requestCode`, `status`, `createdAt`

- ![todo](https://img.shields.io/badge/%5Btodo%5D-lightgrey?style=flat-square) **BE-54 [MVP] Customer lihat detail service request**
  - Endpoint: `GET /api/service-requests/{serviceRequestId}`
  - [ ] Customer pemilik request boleh akses
  - [ ] Technician terkait boleh akses
  - [ ] User lain tidak boleh akses
  - [ ] Return data customer
  - [ ] Return data technician
  - [ ] Return `selectedDeviceCategories`
  - [ ] Return `address`, `addressDetail`, `latitude`, `longitude`
  - [ ] Return `issueDescription`
  - [ ] Return `status`
  - [ ] Return status timestamps jika ada

---

# 9. Technician Lihat Request Masuk

Target: technician bisa melihat request `WAITING` yang memang ditujukan kepadanya.

- ![todo](https://img.shields.io/badge/%5Btodo%5D-lightgrey?style=flat-square) **BE-60 [MVP] Technician lihat request masuk**
  - Endpoint: `GET /api/technicians/service-requests/incoming`
  - [ ] Hanya technician yang boleh akses
  - [ ] Tampilkan request `WAITING` yang ditujukan ke technician login
  - [ ] Sertakan data customer
  - [ ] Sertakan `selectedDeviceCategories`
  - [ ] Sertakan `issueDescription`
  - [ ] Bisa pagination
  - [ ] Urutkan dari terbaru

- ![todo](https://img.shields.io/badge/%5Btodo%5D-lightgrey?style=flat-square) **BE-61 [MVP] Technician lihat detail request**
  - Endpoint: `GET /api/technicians/service-requests/{serviceRequestId}`
  - [ ] Hanya technician yang boleh akses
  - [ ] Validasi technician adalah technician yang dipilih pada request
  - [ ] Return data customer
  - [ ] Return lokasi
  - [ ] Return `selectedDeviceCategories`
  - [ ] Return `issueDescription`
  - [ ] Return status

---

# 10. Technician Accept / Reject / Start / Complete

Target: technician bisa memproses request yang masuk sampai selesai.

- ![todo](https://img.shields.io/badge/%5Btodo%5D-lightgrey?style=flat-square) **BE-62 [MVP] Technician accept request**
  - Endpoint: `PATCH /api/technicians/service-requests/{serviceRequestId}/accept`
  - [ ] Hanya technician terkait yang boleh akses
  - [ ] Request harus `WAITING`
  - [ ] Validasi technician masih memiliki semua skill untuk selected categories
  - [ ] Set status `ACCEPTED`
  - [ ] Isi `acceptedAt`
  - [ ] Set technician `BUSY` jika perlu
  - [ ] Catat status history
  - [ ] Buat notifikasi ke customer

- ![todo](https://img.shields.io/badge/%5Btodo%5D-lightgrey?style=flat-square) **BE-63 [MVP] Technician reject request**
  - Endpoint: `PATCH /api/technicians/service-requests/{serviceRequestId}/reject`
  - [ ] Hanya technician terkait yang boleh akses
  - [ ] Request harus `WAITING`
  - [ ] Simpan `rejectReason` jika ada
  - [ ] Set status `REJECTED`
  - [ ] Jangan ubah technician menjadi `BUSY`
  - [ ] Catat status history
  - [ ] Buat notifikasi ke customer

- ![todo](https://img.shields.io/badge/%5Btodo%5D-lightgrey?style=flat-square) **BE-64 [MVP] Technician start request**
  - Endpoint: `PATCH /api/technicians/service-requests/{serviceRequestId}/start`
  - [ ] Hanya technician terkait yang boleh akses
  - [ ] Request harus `ACCEPTED`
  - [ ] Set status `ON_PROGRESS`
  - [ ] Isi `processedAt`
  - [ ] Set technician `BUSY`
  - [ ] Catat status history
  - [ ] Buat notifikasi ke customer

- ![todo](https://img.shields.io/badge/%5Btodo%5D-lightgrey?style=flat-square) **BE-65 [MVP] Technician complete request**
  - Endpoint: `PATCH /api/technicians/service-requests/{serviceRequestId}/complete`
  - [ ] Hanya technician terkait yang boleh akses
  - [ ] Request harus `ON_PROGRESS`
  - [ ] Set status `COMPLETED`
  - [ ] Isi `completedAt`
  - [ ] Tambah `totalJobs`
  - [ ] Set technician kembali `ONLINE` jika perlu
  - [ ] Catat status history
  - [ ] Buat notifikasi ke customer

---

# 11. Customer Cancel Request

Target: customer bisa membatalkan request selama status masih memungkinkan.

- ![todo](https://img.shields.io/badge/%5Btodo%5D-lightgrey?style=flat-square) **BE-55 [MVP] Customer cancel service request**
  - Endpoint: `PATCH /api/service-requests/{serviceRequestId}/cancel`
  - [ ] Hanya customer pemilik request yang boleh akses
  - [ ] Hanya boleh jika status `WAITING` atau `ACCEPTED`
  - [ ] Simpan `cancelReason` jika dikirim
  - [ ] Set status `CANCELLED`
  - [ ] Isi `cancelledAt`
  - [ ] Catat status history
  - [ ] Buat notifikasi ke technician
  - [ ] Jika technician sudah `BUSY` karena request ini, status bisa dikembalikan sesuai aturan

---

# 12. Status History Tercatat

Target: semua perubahan status request tercatat dan bisa dilihat sebagai timeline.

- ![todo](https://img.shields.io/badge/%5Btodo%5D-lightgrey?style=flat-square) **BE-70 [MVP] Lihat timeline status request**
  - Endpoint: `GET /api/service-requests/{serviceRequestId}/status-history`
  - [ ] Validasi user berhak melihat request
  - [ ] Return `previousStatus`
  - [ ] Return `newStatus`
  - [ ] Return `note`
  - [ ] Return `changedAt`
  - [ ] Return `changedBy`

- ![todo](https://img.shields.io/badge/%5Btodo%5D-lightgrey?style=flat-square) **BE-71 [MVP] Catat siapa pengubah status**
  - [ ] Customer tercatat saat create/cancel
  - [ ] Technician tercatat saat accept/reject/start/complete
  - [ ] Simpan `changedBy`

- ![todo](https://img.shields.io/badge/%5Btodo%5D-lightgrey?style=flat-square) **BE-72 [MVP] Validasi transisi status**
  - [ ] `WAITING` boleh ke `ACCEPTED`
  - [ ] `WAITING` boleh ke `REJECTED`
  - [ ] `WAITING` boleh ke `CANCELLED`
  - [ ] `ACCEPTED` boleh ke `ON_PROGRESS`
  - [ ] `ACCEPTED` boleh ke `CANCELLED`
  - [ ] `ON_PROGRESS` boleh ke `COMPLETED`
  - [ ] Status final tidak bisa diubah lagi

- ![todo](https://img.shields.io/badge/%5Btodo%5D-lightgrey?style=flat-square) **BE-73 [MVP] Simpan waktu status**
  - [ ] `acceptedAt` saat accepted
  - [ ] `processedAt` saat on progress
  - [ ] `completedAt` saat completed
  - [ ] `cancelledAt` saat cancelled

- ![todo](https://img.shields.io/badge/%5Btodo%5D-lightgrey?style=flat-square) **BE-74 [MVP] Blok perubahan status final**
  - [ ] `COMPLETED` tidak bisa diubah
  - [ ] `CANCELLED` tidak bisa diubah
  - [ ] `REJECTED` tidak bisa diubah

---

# 13. Notifikasi Dasar Status Request

Target: user mendapat notifikasi ketika ada perubahan status request penting.

- ![todo](https://img.shields.io/badge/%5Btodo%5D-lightgrey?style=flat-square) **BE-90 [MVP] List notifikasi user**
  - Endpoint: `GET /api/notifications`
  - [ ] Hanya notifikasi milik user login
  - [ ] Urutkan terbaru
  - [ ] Bisa pagination

- ![todo](https://img.shields.io/badge/%5Btodo%5D-lightgrey?style=flat-square) **BE-91 [MVP] Hitung notifikasi belum dibaca**
  - Endpoint: `GET /api/notifications/unread-count`
  - [ ] Hitung `readAt IS NULL`
  - [ ] Hanya milik user login

- ![todo](https://img.shields.io/badge/%5Btodo%5D-lightgrey?style=flat-square) **BE-92 [MVP] Tandai satu notifikasi dibaca**
  - Endpoint: `PATCH /api/notifications/{notificationId}/read`
  - [ ] Validasi notifikasi milik user
  - [ ] Isi `readAt`

- ![todo](https://img.shields.io/badge/%5Btodo%5D-lightgrey?style=flat-square) **BE-94 [MVP] Buat notifikasi saat status berubah**
  - [ ] Saat customer membuat request, technician dapat notifikasi
  - [ ] Saat technician menerima request, customer dapat notifikasi
  - [ ] Saat technician menolak request, customer dapat notifikasi
  - [ ] Saat technician mulai kerja, customer dapat notifikasi
  - [ ] Saat technician menyelesaikan request, customer dapat notifikasi
  - [ ] Saat customer membatalkan request, technician dapat notifikasi

---

# 14. Review Setelah Completed

Target: customer bisa memberi rating setelah service request selesai, lalu rating technician ikut ter-update.

- ![todo](https://img.shields.io/badge/%5Btodo%5D-lightgrey?style=flat-square) **BE-100 [MVP] Customer membuat review**
  - Endpoint: `POST /api/service-requests/{serviceRequestId}/review`
  - [ ] Request harus milik customer
  - [ ] Request harus `COMPLETED`
  - [ ] Rating wajib 1 sampai 5
  - [ ] Comment opsional
  - [ ] Satu request hanya boleh satu review
  - [ ] Buat review untuk technician terkait

- ![todo](https://img.shields.io/badge/%5Btodo%5D-lightgrey?style=flat-square) **BE-101 [MVP] Validasi request completed sebelum review**
  - [ ] Kalau belum selesai, return error
  - [ ] Kalau cancelled/rejected, tidak boleh review

- ![todo](https://img.shields.io/badge/%5Btodo%5D-lightgrey?style=flat-square) **BE-102 [MVP] Validasi satu request satu review**
  - [ ] Cek review berdasarkan `serviceRequestId`
  - [ ] Jika sudah ada, return error

- ![todo](https://img.shields.io/badge/%5Btodo%5D-lightgrey?style=flat-square) **BE-104 [MVP] Update rating rata-rata technician**
  - [ ] Hitung ulang `averageRating`
  - [ ] Update `averageRating`
  - [ ] Update `ratingCount`

- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) **BE-105 [MVP] Tampilkan rating di profil technician**
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Include `averageRating` di list technician
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Include `ratingCount` di list technician
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Include `totalJobs` di list technician
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Include `averageRating`, `ratingCount`, `totalJobs` di detail technician

---

# Deferred Setelah MVP Stable

Fitur di bawah ini bukan target stable release pertama. Kerjakan setelah flow utama customer dan technician sudah stabil.

## Technician Schedule

- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) **BE-40 sampai BE-45** Jadwal technician dan cek availability berbasis jadwal.

Alasan ditunda:

- Flow final MVP tidak meminta customer memilih jadwal.
- Filter awal cukup memakai `availabilityStatus`.

## Media Request

- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) **BE-56** Upload/simpan media service request.

## Estimasi dan Biaya Final Detail

- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) **BE-66** Technician isi estimasi biaya.
- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) **BE-67** Technician isi final cost dan technician note.

## Chat

- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) **BE-80 sampai BE-84** Chat REST.
- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) **BE-85 sampai BE-86** WebSocket chat real-time.

## Notifikasi Lanjutan

- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) **BE-93** Tandai semua notifikasi dibaca.
- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) **BE-95** Notifikasi pesan baru.
- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) **BE-96** WebSocket notifikasi.

## Review Lanjutan

- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) **BE-103** Lihat daftar review technician dengan pagination.

## Log Aktivitas

- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) **BE-110 sampai BE-114** Log aktivitas dan endpoint admin log.

## Admin

- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) **BE-120 sampai BE-125** Admin dashboard dan admin management.

---

# Urutan Pengerjaan Berikutnya

Karena BE-20 sampai BE-26 sudah selesai, urutan paling masuk akal berikutnya:

```text
BE-33  Technician update status ketersediaan
BE-37  Technician lihat skill sendiri
BE-35  Technician tambah skill
BE-36  Technician nonaktifkan skill
BE-50  Customer create service request
BE-53  Customer lihat riwayat request
BE-54  Customer lihat detail request
BE-60  Technician lihat request masuk
BE-61  Technician lihat detail request
BE-62  Technician accept request
BE-63  Technician reject request
BE-64  Technician start request
BE-65  Technician complete request
BE-55  Customer cancel request
BE-70 sampai BE-74 Status history
BE-90 sampai BE-94 Notifikasi dasar
BE-100 sampai BE-105 Review
```

Catatan:

- BE-55 customer cancel bisa dikerjakan setelah BE-50, tapi lebih terasa utuh jika status transition BE-62 sampai BE-65 sudah mulai jelas.
- BE-90 sampai BE-94 bisa dibuat bersamaan dengan status flow, karena notifikasi dibuat saat status berubah.
- BE-104 update rating harus dikerjakan bareng BE-100 supaya rating technician langsung konsisten.

---

# Definition of Done per Fitur

Satu fitur dianggap selesai kalau:

- [ ] Endpoint bisa dipanggil via curl/Postman.
- [ ] Request DTO sudah divalidasi.
- [ ] Response sukses rapi dengan `ApiResponse`.
- [ ] Response error rapi dengan `ApiResponse`.
- [ ] Data tersimpan/terambil dari database dengan benar.
- [ ] Role access sudah sesuai.
- [ ] Data keluar/masuk API menggunakan field English.
- [ ] Tidak ada data sensitif bocor di response.
- [ ] Minimal ada satu script/manual test yang berhasil.
- [ ] Roadmap status di-update.
- [ ] Commit Git sudah dibuat.

---

# Catatan untuk API_CONTRACT.md

Dokumen API detail dibuat terpisah supaya roadmap tetap fokus.

`API_CONTRACT.md` nanti berisi:

- daftar endpoint resmi
- HTTP method
- auth requirement
- request body
- response body
- contoh response sukses
- contoh response error
- aturan validasi
- status code
