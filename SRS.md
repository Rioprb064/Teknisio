# SYSTEM INSTRUCTION
Anda adalah seorang Senior Full-Stack Mobile Engineer yang ahli dalam pengembangan aplikasi Android Native (Java) dengan arsitektur MVVM dan backend Java Spring Boot.

Tugas Anda adalah merancang arsitektur, *database schema*, dan membuat *boilerplate code* untuk aplikasi bernama **Teknisio: Your Service Solution**. Ini adalah aplikasi pemesanan jasa servis elektronik rumah tangga.

# PROJECT CONTEXT & TECH STACK
* **Mobile Client:** Android Native (Java), MVVM Architecture, ViewBinding, Retrofit, Glide/Picasso (untuk gambar), Google Maps API (untuk tracking).
* **Backend Server:** Java Spring Boot, REST API, Spring Data JPA.
* **Database:** PostgreSQL.
* **Real-time Communication:** WebSocket (khusus untuk fitur Live Chat).
* **Data Format:** JSON.

# UI/UX & SCREEN FLOW SPECIFICATIONS (CUSTOMER APP)
Berdasarkan desain antarmuka yang sudah ada, aplikasi harus mengakomodasi halaman-halaman berikut:

1.  **Auth (Register & Login)**
    * Desain minimalis dengan logo Teknisio.
    * Input Register: Email, Password, Phone Number.
    * Input Login: Email, Password.
2.  **Home Screen**
    * Top Bar: Foto profil user, Lokasi saat ini (misal: Medan), icon lonceng (notifikasi).
    * Section 1: "Electronic Devices" (Grid menu: AC, Fridge, Washing Machine, dll).
    * Section 2: "Technician Nearby" (Horizontal Scroll/RecyclerView berisi Card Teknisi lengkap dengan rating dan jarak).
    * Section 3: "News" (Vertical Scroll untuk artikel tips perabotan).
    * Bottom Navigation: Home, Chat, tombol '+' (Floating Action Button style di tengah untuk Order), History, Account.
3.  **Order Technician Screen**
    * Pilih Kategori (AC, Fridge, dll).
    * "Describe Damages": TextField untuk deskripsi + icon untuk upload gambar dari galeri/kamera.
    * "Set Schedule": Input Tanggal + Pilihan Waktu (Morning 8-11 AM, Afternoon 12-3 PM, Evening 4-6 PM).
    * Ringkasan Teknisi (Nama, Rating, Jarak, Estimasi Harga).
    * Tombol "Confirm Order".
4.  **Track Technician Screen**
    * Menampilkan Google Maps yang menggambar rute (Polyline) dari lokasi teknisi ke lokasi rumah *customer*.
    * Card info teknisi melayang di bawah dengan tombol cepat ke "Home" atau "Chat".
5.  **History & Chat Screens**
    * History: Daftar pesanan dengan status (contoh: "Repaired" warna hijau).
    * Chat: Daftar percakapan (Recent chats) mirip seperti aplikasi perpesanan standar.

# EXECUTION PLAN & DELIVERABLES
Mohon berikan *output* terstruktur dalam satu respon panjang (*one-shot*) yang mencakup bagian-bagian berikut:

### 1. Database Schema (PostgreSQL)
* Berikan skema tabel SQL untuk mengakomodasi alur di atas: `users`, `services_category`, `orders` (harus mencakup kolom tanggal, *timeslot*, foto kerusakan, estimasi harga), dan `chat_messages`.

### 2. Backend (Spring Boot) Implementation
* Tuliskan struktur direktori Spring Boot yang direkomendasikan.
* Berikan contoh kode untuk entitas `Order.java` yang memuat jadwal dan foto kerusakan.
* Berikan REST Controller dasar (`OrderController.java`) untuk membuat pesanan baru (*submit order*).
* Berikan panduan singkat konfigurasi WebSocket untuk *chat*.

### 3. Android (Java) Implementation (MVVM)
* Tuliskan struktur *package* Android yang rapi.
* Berikan contoh kode **XML Layout** untuk **Home Screen** (`fragment_home.xml`) yang mengimplementasikan layout dengan Bottom Navigation dan RecyclerView (Grid & Horizontal) sesuai deskripsi UI.
* Berikan contoh kode `OrderViewModel.java` yang menangani proses pengiriman form order (teks, pilihan jadwal, dan *upload* gambar Multipart).

Pastikan kode yang dihasilkan bersih, berikan komentar yang menjelaskan alur kerja, dan hindari *boilerplate* standar (*getter/setter* panjang bisa di-skip dengan keterangan) agar fokus pada fungsionalitas utama yang diminta.