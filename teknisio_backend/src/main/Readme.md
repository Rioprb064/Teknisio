# Roadmap Backend Teknisio

Roadmap pengerjaan backend pbo
<!-- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) -->

## 0. Fondasi Backend

Target: backend punya struktur rapi, response seragam, error handling, repository, dan siap dikembangkan per modul.

- [ ] **BE-00 [MVP] Rapikan struktur package**
  - [ ] Buat package `common`
  - [ ] Buat package `auth`
  - [ ] Buat package `users`
  - [ ] Buat package `layanan`
  - [ ] Buat package `teknisi`
  - [ ] Buat package `jadwal`
  - [ ] Buat package `permintaan`
  - [ ] Buat package `chat`
  - [ ] Buat package `notifikasi`
  - [ ] Buat package `review`
  - [ ] Buat package `config`
  - [ ] Buat package `security`

- [ ] **BE-01 [MVP] Buat global response format**
  - [ ] Buat `ApiResponse<T>`
  - [ ] Response sukses punya `success`, `message`, `data`
  - [ ] Response error punya `success`, `message`, `errors`

- [ ] **BE-02 [MVP] Buat global exception handler**
  - [ ] Buat `GlobalExceptionHandler`
  - [ ] Handle validation error
  - [ ] Handle not found
  - [ ] Handle bad request
  - [ ] Handle unauthorized
  - [ ] Handle forbidden
  - [ ] Handle internal server error

- [ ] **BE-03 [MVP] Siapkan DTO validation**
  - [ ] Tambahkan dependency validation jika belum ada
  - [ ] Gunakan `@NotBlank`
  - [ ] Gunakan `@NotNull`
  - [ ] Gunakan `@Email`
  - [ ] Gunakan `@Size`
  - [ ] Gunakan `@Pattern` jika perlu

- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) **BE-04 [MVP] Buat repository untuk entity inti**
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) `UserRepository`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) `UserSessionRepository`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) `TeknisiProfileRepository`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) `KategoriLayananRepository`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) `JenisLayananRepository`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) `TeknisiLayananRepository`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) `JadwalTeknisiRepository`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) `PermintaanLayananRepository`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) `MediaPermintaanRepository`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) `PesanRepository`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) `NotifikasiRepository`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) `RiwayatStatusRepository`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) `ReviewRepository`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) `LogAktivitasRepository`

- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) **BE-05 [MVP] Validasi koneksi database dan Flyway**
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) `./gradlew clean build` sukses
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) `./gradlew bootRun` sukses
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Tabel terbentuk otomatis
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) `/actuator/health` status `UP`
  - ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Tidak ada error migration

---

## 1. Master Data Layanan

Target: mobile customer bisa menampilkan kategori dan jenis layanan.

- [ ] **BE-20 [MVP] List kategori layanan aktif**
  - Endpoint: `GET /api/kategori-layanan`
  - [ ] Hanya tampilkan data `aktif = true`
  - [ ] Jangan tampilkan data yang sudah soft delete
  - [ ] Response berisi `idKategori`, `namaKategori`, `icon`

- [ ] **BE-21 [MVP] Detail kategori layanan**
  - Endpoint: `GET /api/kategori-layanan/{id}`
  - [ ] Validasi kategori ditemukan
  - [ ] Validasi kategori aktif
  - [ ] Return detail kategori

- [ ] **BE-22 [MVP] List semua jenis layanan aktif**
  - Endpoint: `GET /api/jenis-layanan`
  - [ ] Hanya tampilkan layanan aktif
  - [ ] Sertakan info kategori
  - [ ] Sertakan estimasi menit
  - [ ] Sertakan rentang harga jika ada

- [ ] **BE-23 [MVP] List jenis layanan per kategori**
  - Endpoint: `GET /api/kategori-layanan/{id}/jenis-layanan`
  - [ ] Validasi kategori ditemukan
  - [ ] Filter berdasarkan kategori
  - [ ] Hanya tampilkan layanan aktif

- [ ] **BE-24 [MVP] Detail jenis layanan**
  - Endpoint: `GET /api/jenis-layanan/{id}`
  - [ ] Validasi layanan ditemukan
  - [ ] Validasi layanan aktif
  - [ ] Return detail layanan

- [ ] **BE-25 [MVP] Seed data kategori dan layanan**
  - [ ] Tambah kategori AC
  - [ ] Tambah kategori Kulkas
  - [ ] Tambah kategori Mesin Cuci
  - [ ] Tambah kategori TV
  - [ ] Tambah kategori Kipas Angin
  - [ ] Tambah kategori Rice Cooker
  - [ ] Tambah minimal 2 jenis layanan per kategori

---

## 2. Auth dan Session

Target: user bisa register, login, logout, refresh token, dan akses endpoint sesuai role.

- [ ] **BE-10 [MVP] Register customer**
  - Endpoint: `POST /api/auth/register/customer`
  - [ ] Validasi nama wajib
  - [ ] Validasi email wajib dan unik
  - [ ] Validasi nomor telepon wajib dan unik
  - [ ] Validasi password minimal
  - [ ] Simpan password dalam bentuk hash
  - [ ] Role otomatis `CUSTOMER`
  - [ ] Status akun otomatis `ACTIVE`

- [ ] **BE-11 [MVP] Register teknisi**
  - Endpoint: `POST /api/auth/register/teknisi`
  - [ ] Simpan data user dengan role `TEKNISI`
  - [ ] Buat otomatis data `teknisi_profile`
  - [ ] Status ketersediaan default `OFFLINE`
  - [ ] Rating default 0
  - [ ] Total pekerjaan default 0

- [ ] **BE-12 [MVP] Login**
  - Endpoint: `POST /api/auth/login`
  - [ ] Validasi email ada
  - [ ] Validasi password cocok
  - [ ] Validasi akun aktif
  - [ ] Generate access token
  - [ ] Generate refresh token
  - [ ] Simpan hash refresh token ke `user_session`
  - [ ] Update `last_login`

- [ ] **BE-13 [MVP] Get current user**
  - Endpoint: `GET /api/auth/me`
  - [ ] Ambil user dari token
  - [ ] Return data profil dasar
  - [ ] Return role user

- [ ] **BE-14 [NEXT] Refresh token**
  - Endpoint: `POST /api/auth/refresh`
  - [ ] Validasi refresh token
  - [ ] Validasi session belum expired
  - [ ] Validasi session belum revoked
  - [ ] Generate access token baru

- [ ] **BE-15 [MVP] Logout**
  - Endpoint: `POST /api/auth/logout`
  - [ ] Revoke session aktif
  - [ ] Isi `revoked_at`
  - [ ] Token lama tidak bisa dipakai refresh

- [ ] **BE-16 [MVP] Password hashing**
  - [ ] Gunakan BCrypt
  - [ ] Jangan pernah simpan password asli
  - [ ] Jangan tampilkan `password_hash` di response

- [ ] **BE-17 [MVP] Role-based access**
  - [ ] Endpoint customer hanya bisa diakses customer
  - [ ] Endpoint teknisi hanya bisa diakses teknisi
  - [ ] Endpoint umum bisa diakses tanpa login jika diperlukan
  - [ ] Endpoint profil sendiri wajib login

---

## 3. Profil User dan Teknisi

Target: customer dan teknisi bisa melihat serta mengubah profil dasar.

- [ ] **BE-30 [MVP] Lihat profil sendiri**
  - Endpoint: `GET /api/users/me`
  - [ ] Return nama
  - [ ] Return email
  - [ ] Return nomor telepon
  - [ ] Return foto profil
  - [ ] Return alamat
  - [ ] Return role

- [ ] **BE-31 [MVP] Update profil sendiri**
  - Endpoint: `PUT /api/users/me`
  - [ ] Bisa update nama
  - [ ] Bisa update nomor telepon
  - [ ] Bisa update foto profil
  - [ ] Bisa update alamat
  - [ ] Validasi nomor telepon jika berubah

- [ ] **BE-32 [MVP] Lihat profil teknisi**
  - Endpoint: `GET /api/teknisi/{id}`
  - [ ] Return nama teknisi
  - [ ] Return status ketersediaan
  - [ ] Return rating rata-rata
  - [ ] Return jumlah rating
  - [ ] Return total pekerjaan
  - [ ] Return deskripsi

- [ ] **BE-33 [MVP] Teknisi update status ketersediaan**
  - Endpoint: `PATCH /api/teknisi/me/status`
  - [ ] Status bisa `ONLINE`
  - [ ] Status bisa `OFFLINE`
  - [ ] Status bisa `BUSY`
  - [ ] Status bisa `ON_LEAVE`
  - [ ] Hanya teknisi yang boleh akses

- [ ] **BE-34 [NEXT] Teknisi update deskripsi profil**
  - Endpoint: `PUT /api/teknisi/me/profile`
  - [ ] Bisa update deskripsi
  - [ ] Bisa update foto profil via user profile
  - [ ] Bisa update alamat

- [ ] **BE-35 [MVP] Teknisi tambah layanan yang dikuasai**
  - Endpoint: `POST /api/teknisi/me/layanan`
  - [ ] Validasi layanan ditemukan
  - [ ] Validasi layanan aktif
  - [ ] Cegah duplikasi layanan
  - [ ] Simpan ke `teknisi_layanan`

- [ ] **BE-36 [MVP] Teknisi hapus/nonaktifkan layanan yang dikuasai**
  - Endpoint: `DELETE /api/teknisi/me/layanan/{idLayanan}`
  - [ ] Validasi relasi ditemukan
  - [ ] Hapus atau set `aktif = false`
  - [ ] Hanya pemilik profil teknisi yang boleh hapus

---

## 4. Jadwal Teknisi

Target: teknisi bisa mengatur jam kerja.

- [ ] **BE-40 [MVP] Tambah jadwal kerja**
  - Endpoint: `POST /api/teknisi/me/jadwal`
  - [ ] Input hari
  - [ ] Input jam mulai
  - [ ] Input jam selesai
  - [ ] Validasi jam selesai lebih besar dari jam mulai
  - [ ] Cegah jadwal duplikat

- [ ] **BE-41 [MVP] Lihat jadwal sendiri**
  - Endpoint: `GET /api/teknisi/me/jadwal`
  - [ ] Return semua jadwal aktif
  - [ ] Urutkan berdasarkan hari dan jam mulai

- [ ] **BE-42 [NEXT] Update jadwal kerja**
  - Endpoint: `PUT /api/teknisi/me/jadwal/{id}`
  - [ ] Validasi jadwal milik teknisi tersebut
  - [ ] Bisa ubah hari
  - [ ] Bisa ubah jam mulai
  - [ ] Bisa ubah jam selesai
  - [ ] Bisa ubah aktif/nonaktif

- [ ] **BE-43 [MVP] Hapus/nonaktifkan jadwal kerja**
  - Endpoint: `DELETE /api/teknisi/me/jadwal/{id}`
  - [ ] Validasi jadwal milik teknisi tersebut
  - [ ] Soft delete/nonaktifkan jadwal

- [ ] **BE-44 [MVP] Validasi jadwal**
  - [ ] Jam selesai harus lebih besar dari jam mulai
  - [ ] Hari harus sesuai enum
  - [ ] Tidak boleh bentrok dengan jadwal yang sama

- [ ] **BE-45 [NEXT] Service cek teknisi tersedia**
  - [ ] Cek status teknisi `ONLINE`
  - [ ] Cek layanan yang dikuasai
  - [ ] Cek jadwal kerja aktif
  - [ ] Dipakai saat menampilkan order masuk

---

## 5. Permintaan Layanan — Customer

Target: customer bisa membuat, melihat, dan membatalkan permintaan layanan.

- [ ] **BE-50 [MVP] Customer membuat permintaan layanan**
  - Endpoint: `POST /api/permintaan`
  - [ ] Validasi user role `CUSTOMER`
  - [ ] Validasi layanan ditemukan
  - [ ] Validasi layanan aktif
  - [ ] Simpan latitude
  - [ ] Simpan longitude
  - [ ] Simpan alamat
  - [ ] Simpan detail alamat
  - [ ] Simpan deskripsi masalah
  - [ ] Status awal `WAITING`

- [ ] **BE-51 [MVP] Simpan detail lokasi dan masalah**
  - [ ] Latitude wajib
  - [ ] Longitude wajib
  - [ ] Alamat wajib
  - [ ] Deskripsi masalah wajib
  - [ ] Detail alamat opsional

- [ ] **BE-52 [NEXT] Simpan media permintaan**
  - Endpoint: `POST /api/permintaan/{id}/media`
  - [ ] Validasi permintaan milik customer
  - [ ] Simpan `url_file`
  - [ ] Simpan `tipe_file`
  - [ ] Simpan `mime_type`
  - [ ] Simpan `ukuran_file`

- [ ] **BE-53 [MVP] Customer lihat riwayat order**
  - Endpoint: `GET /api/permintaan/me`
  - [ ] Hanya tampilkan order milik user login
  - [ ] Bisa pagination
  - [ ] Urutkan dari terbaru

- [ ] **BE-54 [MVP] Customer lihat detail order**
  - Endpoint: `GET /api/permintaan/{id}`
  - [ ] Validasi order milik customer atau teknisi terkait
  - [ ] Return data layanan
  - [ ] Return data teknisi jika sudah ada
  - [ ] Return status
  - [ ] Return estimasi dan biaya akhir jika ada

- [ ] **BE-55 [MVP] Customer batalkan order**
  - Endpoint: `PATCH /api/permintaan/{id}/cancel`
  - [ ] Hanya customer pemilik order
  - [ ] Hanya boleh jika status masih `WAITING` atau `ACCEPTED`
  - [ ] Simpan alasan batal
  - [ ] Update status `CANCELLED`
  - [ ] Isi `waktu_dibatalkan`

- [ ] **BE-56 [NEXT] Filter order berdasarkan status**
  - Endpoint: `GET /api/permintaan/me?status=WAITING`
  - [ ] Filter status opsional
  - [ ] Validasi status sesuai enum
  - [ ] Tetap hanya data milik user login

---

## 6. Permintaan Layanan — Teknisi

Target: teknisi bisa melihat order masuk, menerima/menolak, memulai, dan menyelesaikan layanan.

- [ ] **BE-60 [MVP] Teknisi lihat order masuk**
  - Endpoint: `GET /api/teknisi/permintaan/masuk`
  - [ ] Hanya teknisi login
  - [ ] Tampilkan order `WAITING`
  - [ ] Cocokkan dengan layanan yang dikuasai teknisi
  - [ ] Bisa filter berdasarkan layanan
  - [ ] Bisa pagination

- [ ] **BE-61 [MVP] Teknisi lihat detail order**
  - Endpoint: `GET /api/teknisi/permintaan/{id}`
  - [ ] Validasi teknisi boleh melihat order
  - [ ] Return data customer
  - [ ] Return lokasi
  - [ ] Return layanan
  - [ ] Return deskripsi masalah
  - [ ] Return status

- [ ] **BE-62 [MVP] Teknisi accept order**
  - Endpoint: `PATCH /api/teknisi/permintaan/{id}/accept`
  - [ ] Hanya teknisi login
  - [ ] Order harus `WAITING`
  - [ ] Teknisi harus punya layanan terkait
  - [ ] Set `id_teknisi_profile`
  - [ ] Set status `ACCEPTED`
  - [ ] Isi `waktu_diterima`
  - [ ] Set status teknisi `BUSY` jika perlu

- [ ] **BE-63 [MVP] Teknisi reject order**
  - Endpoint: `PATCH /api/teknisi/permintaan/{id}/reject`
  - [ ] Order harus `WAITING`
  - [ ] Simpan catatan/alasan jika ada
  - [ ] Status menjadi `REJECTED`
  - [ ] Tidak mengubah teknisi menjadi busy

- [ ] **BE-64 [MVP] Teknisi mulai pengerjaan**
  - Endpoint: `PATCH /api/teknisi/permintaan/{id}/start`
  - [ ] Order harus `ACCEPTED`
  - [ ] Hanya teknisi yang menerima order
  - [ ] Status menjadi `ON_PROGRESS`
  - [ ] Isi `waktu_diproses`

- [ ] **BE-65 [MVP] Teknisi selesaikan pengerjaan**
  - Endpoint: `PATCH /api/teknisi/permintaan/{id}/complete`
  - [ ] Order harus `ON_PROGRESS`
  - [ ] Hanya teknisi yang menangani order
  - [ ] Status menjadi `COMPLETED`
  - [ ] Isi `waktu_selesai`
  - [ ] Tambah `total_pekerjaan`
  - [ ] Status teknisi kembali `ONLINE` jika perlu

- [ ] **BE-66 [NEXT] Teknisi isi estimasi biaya**
  - Endpoint: `PATCH /api/teknisi/permintaan/{id}/estimasi`
  - [ ] Hanya teknisi terkait
  - [ ] Estimasi biaya tidak boleh negatif
  - [ ] Simpan `estimasi_biaya`

- [ ] **BE-67 [MVP] Teknisi isi biaya akhir dan catatan**
  - Endpoint: `PATCH /api/teknisi/permintaan/{id}/finalisasi`
  - [ ] Hanya teknisi terkait
  - [ ] Biaya akhir tidak boleh negatif
  - [ ] Simpan `biaya_akhir`
  - [ ] Simpan `catatan_teknisi`

---

## 7. Status dan Riwayat Status

Target: semua perubahan status tercatat dan bisa dilihat sebagai timeline.

- [ ] **BE-70 [MVP] Lihat timeline status order**
  - Endpoint: `GET /api/permintaan/{id}/riwayat-status`
  - [ ] Validasi user berhak melihat order
  - [ ] Return status sebelum
  - [ ] Return status sesudah
  - [ ] Return catatan
  - [ ] Return waktu perubahan

- [ ] **BE-71 [MVP] Catat siapa pengubah status**
  - [ ] Simpan `diubah_oleh_terakhir`
  - [ ] Simpan `diubah_oleh` di riwayat status
  - [ ] Customer tercatat saat cancel
  - [ ] Teknisi tercatat saat accept/start/complete

- [ ] **BE-72 [MVP] Validasi transisi status**
  - [ ] `WAITING` boleh ke `ACCEPTED`
  - [ ] `WAITING` boleh ke `REJECTED`
  - [ ] `WAITING` boleh ke `CANCELLED`
  - [ ] `ACCEPTED` boleh ke `ON_PROGRESS`
  - [ ] `ACCEPTED` boleh ke `CANCELLED`
  - [ ] `ON_PROGRESS` boleh ke `COMPLETED`
  - [ ] Status final tidak bisa diubah lagi

- [ ] **BE-73 [MVP] Simpan waktu status**
  - [ ] `waktu_diterima` saat accepted
  - [ ] `waktu_diproses` saat on progress
  - [ ] `waktu_selesai` saat completed
  - [ ] `waktu_dibatalkan` saat cancelled

- [ ] **BE-74 [MVP] Blok perubahan status final**
  - [ ] `COMPLETED` tidak bisa diubah
  - [ ] `CANCELLED` tidak bisa diubah
  - [ ] `REJECTED` tidak bisa diubah

---

## 8. Chat REST

Target: customer dan teknisi terkait order bisa berkomunikasi lewat REST API dulu sebelum WebSocket.

- [ ] **BE-80 [MVP] Kirim pesan text**
  - Endpoint: `POST /api/permintaan/{id}/pesan`
  - [ ] Validasi user bagian dari order
  - [ ] Pesan text wajib punya `isi_pesan`
  - [ ] Tipe pesan `TEXT`
  - [ ] Simpan pengirim
  - [ ] Simpan waktu kirim

- [ ] **BE-81 [MVP] Ambil riwayat chat**
  - Endpoint: `GET /api/permintaan/{id}/pesan`
  - [ ] Validasi user bagian dari order
  - [ ] Urutkan dari pesan lama ke baru
  - [ ] Bisa pagination
  - [ ] Jangan tampilkan pesan yang soft deleted

- [ ] **BE-82 [NEXT] Kirim pesan gambar/file URL**
  - Endpoint: `POST /api/permintaan/{id}/pesan/media`
  - [ ] Validasi `file_url`
  - [ ] Tipe pesan `IMAGE`
  - [ ] Simpan pengirim
  - [ ] Simpan waktu kirim

- [ ] **BE-83 [NEXT] Tandai pesan sudah dibaca**
  - Endpoint: `PATCH /api/pesan/{id}/read`
  - [ ] Validasi penerima pesan
  - [ ] Isi `read_at`
  - [ ] Tidak perlu ubah kalau sudah dibaca

- [ ] **BE-84 [MVP] Validasi akses chat**
  - [ ] Customer hanya bisa chat pada order miliknya
  - [ ] Teknisi hanya bisa chat pada order yang dia tangani
  - [ ] User lain tidak boleh akses chat order tersebut

---

## 9. Notifikasi

Target: user bisa melihat notifikasi perubahan status dan pesan baru.

- [ ] **BE-90 [MVP] List notifikasi user**
  - Endpoint: `GET /api/notifikasi`
  - [ ] Hanya notifikasi milik user login
  - [ ] Urutkan terbaru
  - [ ] Bisa pagination

- [ ] **BE-91 [MVP] Hitung notifikasi belum dibaca**
  - Endpoint: `GET /api/notifikasi/unread-count`
  - [ ] Hitung `read_at IS NULL`
  - [ ] Hanya milik user login

- [ ] **BE-92 [MVP] Tandai satu notifikasi dibaca**
  - Endpoint: `PATCH /api/notifikasi/{id}/read`
  - [ ] Validasi notifikasi milik user
  - [ ] Isi `read_at`

- [ ] **BE-93 [NEXT] Tandai semua notifikasi dibaca**
  - Endpoint: `PATCH /api/notifikasi/read-all`
  - [ ] Update semua notifikasi user login
  - [ ] Hanya yang belum dibaca

- [ ] **BE-94 [MVP] Buat notifikasi saat status berubah**
  - [ ] Saat teknisi menerima order, customer dapat notifikasi
  - [ ] Saat teknisi mulai kerja, customer dapat notifikasi
  - [ ] Saat teknisi menyelesaikan order, customer dapat notifikasi
  - [ ] Saat customer membatalkan order, teknisi dapat notifikasi jika sudah assigned

- [ ] **BE-95 [NEXT] Buat notifikasi saat pesan baru**
  - [ ] Saat customer kirim pesan, teknisi dapat notifikasi
  - [ ] Saat teknisi kirim pesan, customer dapat notifikasi
  - [ ] Reference type `CHAT`

---

## 10. Review

Target: customer bisa memberi rating setelah layanan selesai.

- [ ] **BE-100 [MVP] Customer membuat review**
  - Endpoint: `POST /api/permintaan/{id}/review`
  - [ ] Order harus milik customer
  - [ ] Order harus `COMPLETED`
  - [ ] Rating wajib 1 sampai 5
  - [ ] Komentar opsional
  - [ ] Satu order hanya boleh satu review

- [ ] **BE-101 [MVP] Validasi order completed**
  - [ ] Kalau belum selesai, return error
  - [ ] Kalau cancelled/rejected, tidak boleh review

- [ ] **BE-102 [MVP] Validasi satu order satu review**
  - [ ] Cek review berdasarkan `id_permintaan`
  - [ ] Jika sudah ada, return error

- [ ] **BE-103 [NEXT] Lihat review teknisi**
  - Endpoint: `GET /api/teknisi/{id}/review`
  - [ ] Return daftar review
  - [ ] Return nama customer jika boleh
  - [ ] Return rating
  - [ ] Return komentar
  - [ ] Bisa pagination

- [ ] **BE-104 [MVP] Update rating rata-rata teknisi**
  - [ ] Hitung ulang rating average
  - [ ] Update `rating_avg`
  - [ ] Update `rating_count`

- [ ] **BE-105 [MVP] Tampilkan rating di profil teknisi**
  - [ ] Include rating di endpoint detail teknisi
  - [ ] Include rating count
  - [ ] Include total pekerjaan

---

## 11. Log Aktivitas

Target: aktivitas penting tersimpan untuk audit.

- [ ] **BE-110 [PLUS] Log saat login**
  - [ ] Simpan id user
  - [ ] Simpan aktivitas `LOGIN`
  - [ ] Simpan IP address jika tersedia
  - [ ] Simpan user agent jika tersedia

- [ ] **BE-111 [PLUS] Log saat buat order**
  - [ ] Simpan aktivitas `CREATE_PERMINTAAN`
  - [ ] Simpan metadata id permintaan

- [ ] **BE-112 [PLUS] Log saat update status**
  - [ ] Simpan aktivitas `UPDATE_STATUS_PERMINTAAN`
  - [ ] Simpan status sebelum
  - [ ] Simpan status sesudah

- [ ] **BE-113 [PLUS] Log saat logout**
  - [ ] Simpan aktivitas `LOGOUT`
  - [ ] Simpan id session jika perlu

- [ ] **BE-114 [LATER] Endpoint lihat log untuk admin**
  - Endpoint: `GET /api/admin/log-aktivitas`
  - [ ] Hanya admin
  - [ ] Bisa filter tanggal
  - [ ] Bisa filter user
  - [ ] Bisa filter aktivitas

---

## 12. WebSocket

Target: fitur real-time dikerjakan setelah REST API stabil.

- [ ] **BE-85 [LATER] WebSocket chat real-time**
  - Endpoint WS: `/ws/chat`
  - [ ] Setup WebSocket config
  - [ ] Setup STOMP jika digunakan
  - [ ] Buat room berdasarkan `id_permintaan`
  - [ ] Broadcast pesan baru ke room order

- [ ] **BE-86 [LATER] Broadcast pesan baru**
  - [ ] Saat pesan tersimpan, kirim event ke WebSocket
  - [ ] Customer menerima pesan teknisi
  - [ ] Teknisi menerima pesan customer

- [ ] **BE-96 [LATER] WebSocket notifikasi**
  - Endpoint WS: `/ws/notification`
  - [ ] Kirim notifikasi status order
  - [ ] Kirim notifikasi pesan baru
  - [ ] Kirim unread count update

---

## 13. Admin Opsional

Catatan: role `ADMIN` ada di backend, tapi untuk SRS saat ini fokus utama masih customer dan teknisi. Kerjakan admin hanya kalau MVP sudah aman.

- [ ] **BE-120 [LATER] Admin CRUD kategori layanan**
  - Endpoint: `/api/admin/kategori-layanan`
  - [ ] Create kategori
  - [ ] Update kategori
  - [ ] Nonaktifkan kategori
  - [ ] List semua kategori termasuk nonaktif

- [ ] **BE-121 [LATER] Admin CRUD jenis layanan**
  - Endpoint: `/api/admin/jenis-layanan`
  - [ ] Create jenis layanan
  - [ ] Update jenis layanan
  - [ ] Nonaktifkan jenis layanan
  - [ ] Atur harga min dan max

- [ ] **BE-122 [LATER] Admin lihat semua user**
  - Endpoint: `GET /api/admin/users`
  - [ ] Filter role
  - [ ] Filter status akun
  - [ ] Search nama/email

- [ ] **BE-123 [LATER] Admin nonaktifkan user**
  - Endpoint: `PATCH /api/admin/users/{id}/disable`
  - [ ] Set status akun
  - [ ] Cegah user login jika inactive/banned

- [ ] **BE-124 [LATER] Admin lihat semua permintaan**
  - Endpoint: `GET /api/admin/permintaan`
  - [ ] Filter status
  - [ ] Filter tanggal
  - [ ] Filter teknisi
  - [ ] Filter customer

- [ ] **BE-125 [LATER] Admin dashboard sederhana**
  - Endpoint: `GET /api/admin/dashboard`
  - [ ] Total user
  - [ ] Total teknisi
  - [ ] Total permintaan
  - [ ] Total order completed
  - [ ] Total order waiting
  - [ ] Rating rata-rata teknisi

---

## Urutan Pengerjaan yang Disarankan

Gunakan urutan ini agar tidak bingung.

- [ ] **Milestone 1 — Fondasi**
  - [ ] BE-00
  - [ ] BE-01
  - [ ] BE-02
  - [ ] BE-03
  - [ ] BE-04
  - [ ] BE-05

- [ ] **Milestone 2 — Master Data**
  - [ ] BE-20
  - [ ] BE-21
  - [ ] BE-22
  - [ ] BE-23
  - [ ] BE-24
  - [ ] BE-25

- [ ] **Milestone 3 — Auth**
  - [ ] BE-10
  - [ ] BE-11
  - [ ] BE-12
  - [ ] BE-13
  - [ ] BE-15
  - [ ] BE-16
  - [ ] BE-17

- [ ] **Milestone 4 — Profil dan Teknisi**
  - [ ] BE-30
  - [ ] BE-31
  - [ ] BE-32
  - [ ] BE-33
  - [ ] BE-35
  - [ ] BE-36

- [ ] **Milestone 5 — Jadwal Teknisi**
  - [ ] BE-40
  - [ ] BE-41
  - [ ] BE-43
  - [ ] BE-44

- [ ] **Milestone 6 — Order Customer**
  - [ ] BE-50
  - [ ] BE-51
  - [ ] BE-53
  - [ ] BE-54
  - [ ] BE-55

- [ ] **Milestone 7 — Order Teknisi**
  - [ ] BE-60
  - [ ] BE-61
  - [ ] BE-62
  - [ ] BE-63
  - [ ] BE-64
  - [ ] BE-65
  - [ ] BE-67

- [ ] **Milestone 8 — Status dan Riwayat**
  - [ ] BE-70
  - [ ] BE-71
  - [ ] BE-72
  - [ ] BE-73
  - [ ] BE-74

- [ ] **Milestone 9 — Chat REST**
  - [ ] BE-80
  - [ ] BE-81
  - [ ] BE-84

- [ ] **Milestone 10 — Notifikasi**
  - [ ] BE-90
  - [ ] BE-91
  - [ ] BE-92
  - [ ] BE-94

- [ ] **Milestone 11 — Review**
  - [ ] BE-100
  - [ ] BE-101
  - [ ] BE-102
  - [ ] BE-104
  - [ ] BE-105

- [ ] **Milestone 12 — Fitur Lanjutan**
  - [ ] Refresh token
  - [ ] Upload/media handling
  - [ ] WebSocket chat
  - [ ] WebSocket notifikasi
  - [ ] Admin dashboard

---

## Checklist Definisi Selesai per Fitur

Satu fitur dianggap selesai kalau:

- [ ] Endpoint bisa dipanggil via Postman/curl
- [ ] Request DTO sudah divalidasi
- [ ] Response sukses rapi
- [ ] Response error rapi
- [ ] Data tersimpan/terambil dari database dengan benar
- [ ] Role access sudah sesuai
- [ ] Tidak ada data sensitif bocor di response
- [ ] Minimal ada 1 test manual yang berhasil
- [ ] Commit Git sudah dibuat

---
