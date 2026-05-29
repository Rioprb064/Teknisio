/* =====================================================
   API SERVICE — Teknisio Frontend
   ===================================================== */

const API_BASE = 'http://localhost:8080';
const TOKEN_KEY = 'teknisio_token';
const USER_KEY  = 'teknisio_user';

/* ── Token helpers ─────────────────────────────────── */
const Auth = {
  getToken() { return localStorage.getItem(TOKEN_KEY); },
  setToken(t) { localStorage.setItem(TOKEN_KEY, t); },
  removeToken() { localStorage.removeItem(TOKEN_KEY); },

  getUser() {
    const raw = localStorage.getItem(USER_KEY);
    return raw ? JSON.parse(raw) : null;
  },
  setUser(u) { localStorage.setItem(USER_KEY, JSON.stringify(u)); },
  removeUser() { localStorage.removeItem(USER_KEY); },

  isLoggedIn() { return !!this.getToken(); },

  logout() {
    this.removeToken();
    this.removeUser();
    window.location.href = 'login.html';
  },

  /** Redirect to login if not authenticated */
  requireAuth() {
    if (!this.isLoggedIn()) {
      window.location.href = 'login.html';
      return false;
    }
    return true;
  },

  /** Redirect to home if already authenticated */
  requireGuest() {
    if (this.isLoggedIn()) {
      window.location.href = 'home.html';
      return false;
    }
    return true;
  }
};

/* ── Fetch wrapper ─────────────────────────────────── */
async function apiFetch(path, options = {}) {
  const url = `${API_BASE}${path}`;
  const token = Auth.getToken();

  const headers = {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
    ...options.headers,
  };

  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  const res = await fetch(url, {
    ...options,
    headers,
  });

  const text = await res.text();
  let data;
  try {
    data = JSON.parse(text);
  } catch {
    data = { message: text };
  }

  if (!res.ok) {
    const err = new Error(data?.message || `HTTP ${res.status}`);
    err.status = res.status;
    err.data = data;
    throw err;
  }

  return data;
}

/* ── Auth API ──────────────────────────────────────── */
const AuthAPI = {
  async login(email, password) {
    try {
      const data = await apiFetch('/api/auth/login', {
        method: 'POST',
        body: JSON.stringify({ email, password }),
      });
      Auth.setToken(data.accessToken);
      Auth.setUser(data);
      return data;
    } catch (err) {
      console.warn('Backend tidak tersedia, menggunakan MOCK LOGIN');
      const mockData = {
        accessToken: 'mock-token-12345',
        nama: 'Pengguna Demo',
        email: email,
        role: 'CUSTOMER'
      };
      Auth.setToken(mockData.accessToken);
      Auth.setUser(mockData);
      return mockData;
    }
  },

  async registerCustomer(payload) {
    try {
      return await apiFetch('/api/auth/register/customer', {
        method: 'POST',
        body: JSON.stringify(payload),
      });
    } catch (err) {
      console.warn('Backend tidak tersedia, menggunakan MOCK REGISTER');
      return { message: 'Registrasi berhasil (Mock)' };
    }
  },

  async registerTeknisi(payload) {
    try {
      return await apiFetch('/api/auth/register/teknisi', {
        method: 'POST',
        body: JSON.stringify(payload),
      });
    } catch (err) {
      console.warn('Backend tidak tersedia, menggunakan MOCK REGISTER');
      return { message: 'Registrasi berhasil (Mock)' };
    }
  },

  async getProfile() {
    try {
      return await apiFetch('/api/auth/profile');
    } catch (err) {
      return Auth.getUser() || { nama: 'Pengguna Demo', email: 'demo@example.com' };
    }
  },
};

/* ── Service API (placeholder for future endpoints) ─ */
const ServiceAPI = {
  async getKategori() {
    try {
      return await apiFetch('/api/kategori-layanan');
    } catch {
      return getMockKategori();
    }
  },

  async getJenisLayanan() {
    try {
      return await apiFetch('/api/jenis-layanan');
    } catch {
      return getMockJenisLayanan();
    }
  },
};

/* ── Order API ─────────────────────────────────────── */
const OrderAPI = {
  async getMyOrders() {
    try {
      return await apiFetch('/api/permintaan/me');
    } catch {
      return getMockOrders();
    }
  },

  async createOrder(payload) {
    return await apiFetch('/api/permintaan', {
      method: 'POST',
      body: JSON.stringify(payload),
    });
  },
};

/* ── Notif API ─────────────────────────────────────── */
const NotifAPI = {
  async getNotifications() {
    try {
      return await apiFetch('/api/notifikasi');
    } catch {
      return [];
    }
  },
};

/* ══════════════════════════════════════════════════
   MOCK DATA — untuk endpoint yang belum ada
   ══════════════════════════════════════════════════ */

function getMockKategori() {
  return [
    { idKategori: '1', namaKategori: 'AC',           icon: '❄️' },
    { idKategori: '2', namaKategori: 'Kulkas',       icon: '🧊' },
    { idKategori: '3', namaKategori: 'Mesin Cuci',   icon: '🫧' },
    { idKategori: '4', namaKategori: 'TV',           icon: '📺' },
    { idKategori: '5', namaKategori: 'Kipas Angin',  icon: '💨' },
    { idKategori: '6', namaKategori: 'Rice Cooker',  icon: '🍚' },
    { idKategori: '7', namaKategori: 'Dispenser',    icon: '💧' },
    { idKategori: '8', namaKategori: 'Oven',         icon: '🔥' },
  ];
}

function getMockJenisLayanan() {
  return [
    { idLayanan: '1', idKategori: '1', namaLayanan: 'Cuci AC', hargaMin: 80000, hargaMax: 150000, estimasiMenit: 90 },
    { idLayanan: '2', idKategori: '1', namaLayanan: 'Service AC Tidak Dingin', hargaMin: 150000, hargaMax: 350000, estimasiMenit: 120 },
    { idLayanan: '3', idKategori: '2', namaLayanan: 'Perbaikan Kulkas', hargaMin: 100000, hargaMax: 400000, estimasiMenit: 90 },
    { idLayanan: '4', idKategori: '3', namaLayanan: 'Service Mesin Cuci', hargaMin: 120000, hargaMax: 350000, estimasiMenit: 120 },
  ];
}

function getMockTeknisi() {
  return [
    {
      id: '1',
      nama: 'Ahmed Rush',
      ratingAvg: 4.2,
      ratingCount: 38,
      jarak: '±2 Km',
      status: 'ONLINE',
      kategori: ['AC', 'Kulkas'],
      hargaMin: 50000,
      hargaMax: 200000,
      foto: null,
    },
    {
      id: '2',
      nama: 'Budi Santoso',
      ratingAvg: 4.7,
      ratingCount: 112,
      jarak: '±3 Km',
      status: 'ONLINE',
      kategori: ['Mesin Cuci', 'AC'],
      hargaMin: 75000,
      hargaMax: 300000,
      foto: null,
    },
    {
      id: '3',
      nama: 'Evan Bran',
      ratingAvg: 4.5,
      ratingCount: 64,
      jarak: '±5 Km',
      status: 'BUSY',
      kategori: ['TV', 'Kipas Angin'],
      hargaMin: 60000,
      hargaMax: 250000,
      foto: null,
    },
  ];
}

function getMockOrders() {
  return [
    {
      idPermintaan: '1',
      kodePermintaan: 'TK-20260511-AB12CD34',
      namaLayanan: 'Cuci AC',
      namaKategori: 'AC',
      status: 'COMPLETED',
      namaTeknisi: 'Ahmed Rush',
      waktuPermintaan: '2026-05-11T10:30:00Z',
      estimasiBiaya: 150000,
      biayaAkhir: 150000,
    },
    {
      idPermintaan: '2',
      kodePermintaan: 'TK-20260520-XY78EF90',
      namaLayanan: 'Perbaikan Kulkas',
      namaKategori: 'Kulkas',
      status: 'ON_PROGRESS',
      namaTeknisi: 'Budi Santoso',
      waktuPermintaan: '2026-05-20T14:00:00Z',
      estimasiBiaya: 200000,
      biayaAkhir: null,
    },
    {
      idPermintaan: '3',
      kodePermintaan: 'TK-20260525-GH45IJ67',
      namaLayanan: 'Service Mesin Cuci',
      namaKategori: 'Mesin Cuci',
      status: 'WAITING',
      namaTeknisi: null,
      waktuPermintaan: '2026-05-25T09:00:00Z',
      estimasiBiaya: null,
      biayaAkhir: null,
    },
    {
      idPermintaan: '4',
      kodePermintaan: 'TK-20260501-KL89MN12',
      namaLayanan: 'Cuci AC',
      namaKategori: 'AC',
      status: 'CANCELLED',
      namaTeknisi: null,
      waktuPermintaan: '2026-05-01T16:00:00Z',
      estimasiBiaya: null,
      biayaAkhir: null,
    },
  ];
}

function getMockChats() {
  return [
    {
      id: '1',
      nama: 'Ahmed Rush',
      lastMessage: 'Saya akan tiba sekitar 2 PM, mohon ada di rumah',
      lastTime: '08.43',
      unread: 2,
      foto: null,
    },
    {
      id: '2',
      nama: 'Evan Bran',
      lastMessage: 'Terima kasih sudah menghubungi saya',
      lastTime: '4/22/26',
      unread: 0,
      foto: null,
    },
    {
      id: '3',
      nama: 'Budi Santoso',
      lastMessage: 'Servis sudah selesai, terima kasih',
      lastTime: '4/15/26',
      unread: 0,
      foto: null,
    },
  ];
}

function getMockNews() {
  return [
    {
      id: '1',
      judul: 'Tips Merawat AC agar Tetap Dingin',
      ringkasan: 'AC yang rajin dirawat bisa bertahan lebih lama dan hemat listrik.',
      tanggal: '27 Mei 2026',
      kategori: 'AC',
    },
    {
      id: '2',
      judul: 'Penyebab Kulkas Tidak Dingin',
      ringkasan: 'Kenali tanda-tanda kulkas rusak sebelum terlambat.',
      tanggal: '25 Mei 2026',
      kategori: 'Kulkas',
    },
    {
      id: '3',
      judul: 'Cara Membersihkan Mesin Cuci Sendiri',
      ringkasan: 'Langkah mudah membersihkan drum mesin cuci di rumah.',
      tanggal: '20 Mei 2026',
      kategori: 'Mesin Cuci',
    },
  ];
}

/* ── Utilities ─────────────────────────────────────── */

/** Format currency to Indonesian Rupiah */
function formatRupiah(amount) {
  if (!amount) return '-';
  return new Intl.NumberFormat('id-ID', {
    style: 'currency', currency: 'IDR', maximumFractionDigits: 0
  }).format(amount);
}

/** Format date to Indonesian */
function formatDate(isoString) {
  if (!isoString) return '-';
  return new Date(isoString).toLocaleDateString('id-ID', {
    day: 'numeric', month: 'long', year: 'numeric'
  });
}

/** Format time */
function formatTime(isoString) {
  if (!isoString) return '';
  return new Date(isoString).toLocaleTimeString('id-ID', {
    hour: '2-digit', minute: '2-digit'
  });
}

/** Get status badge config */
function getStatusBadge(status) {
  const map = {
    WAITING:     { label: 'Menunggu',      cls: 'badge-warning' },
    ACCEPTED:    { label: 'Diterima',      cls: 'badge-info'    },
    ON_PROGRESS: { label: 'Diproses',      cls: 'badge-info'    },
    COMPLETED:   { label: 'Selesai',       cls: 'badge-success' },
    CANCELLED:   { label: 'Dibatalkan',    cls: 'badge-danger'  },
    REJECTED:    { label: 'Ditolak',       cls: 'badge-danger'  },
  };
  return map[status] || { label: status, cls: 'badge-default' };
}

/** Render star rating SVG */
function renderStars(rating, maxStars = 5) {
  const stars = [];
  for (let i = 1; i <= maxStars; i++) {
    const filled = i <= Math.round(rating);
    stars.push(`
      <svg class="star ${filled ? '' : 'empty'}" viewBox="0 0 24 24" fill="currentColor">
        <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
      </svg>
    `);
  }
  return `<div class="stars">${stars.join('')}</div>`;
}

/** Generate avatar placeholder with initials */
function avatarInitials(nama) {
  if (!nama) return '?';
  const parts = nama.trim().split(' ');
  return parts.length >= 2
    ? (parts[0][0] + parts[1][0]).toUpperCase()
    : parts[0][0].toUpperCase();
}

/** Avatar HTML element */
function avatarEl(nama, foto, sizeClass = 'avatar-md') {
  if (foto) {
    return `<img class="avatar ${sizeClass}" src="${foto}" alt="${nama}">`;
  }
  const initials = avatarInitials(nama);
  const colors = ['#2D3A8C', '#4F6EF7', '#0EA5E9', '#10B981', '#F59E0B', '#EF4444'];
  const colorIndex = (nama?.charCodeAt(0) || 0) % colors.length;
  const bg = colors[colorIndex];
  const fontSize = sizeClass === 'avatar-sm' ? '13px' : sizeClass === 'avatar-lg' ? '22px' : '16px';
  const size = sizeClass === 'avatar-sm' ? '36px' : sizeClass === 'avatar-lg' ? '64px' : '48px';
  return `
    <div class="avatar ${sizeClass}" style="background:${bg};color:white;display:flex;align-items:center;justify-content:center;font-size:${fontSize};font-weight:700;width:${size};height:${size}">
      ${initials}
    </div>
  `;
}

/* ── Toast Notification ────────────────────────────── */
function showToast(message, type = 'info') {
  let container = document.getElementById('toast-container');
  if (!container) {
    container = document.createElement('div');
    container.id = 'toast-container';
    document.body.appendChild(container);
  }

  const icons = {
    success: '✓',
    error:   '✕',
    warning: '⚠',
    info:    'ℹ',
  };

  const toast = document.createElement('div');
  toast.className = `toast toast-${type}`;
  toast.innerHTML = `<span>${icons[type] || 'ℹ'}</span><span>${message}</span>`;
  container.appendChild(toast);

  setTimeout(() => {
    toast.remove();
  }, 3000);
}

/* ── Loading helpers ───────────────────────────────── */
function setLoading(btn, loading) {
  if (!btn) return;
  if (loading) {
    btn._originalText = btn.innerHTML;
    btn.innerHTML = `<span class="spinner"></span>`;
    btn.disabled = true;
  } else {
    btn.innerHTML = btn._originalText || 'Submit';
    btn.disabled = false;
  }
}
