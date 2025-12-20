# 🎣 Fishing Planet Helper - Setup Guide

## 📁 Struktur Project untuk AIDE

```
FishingPlanetHelper/
├── app/
│   └── src/
│       └── main/
│           ├── java/com/fishinghelper/app/
│           │   ├── MainActivity.java
│           │   ├── AutoFishService.java
│           │   └── CoordinateSetupActivity.java
│           ├── res/
│           │   ├── layout/
│           │   │   ├── activity_main.xml
│           │   │   └── activity_coordinate_setup.xml
│           │   ├── values/
│           │   │   └── strings.xml
│           │   └── xml/
│           │       └── accessibility_service_config.xml
│           └── AndroidManifest.xml
└── README.md
```

## 🛠️ Setup di AIDE (HP Android)

### Step 1: Install AIDE
1. Download **AIDE - Android IDE** dari Play Store
2. Install dan buka aplikasi

### Step 2: Buat Project Baru
1. Buka AIDE
2. Pilih "New Project" → "App with Empty Activity"
3. Nama project: **FishingPlanetHelper**
4. Package name: **com.fishinghelper.app**

### Step 3: Copy Semua File
1. Buat file **strings.xml** di `res/values/`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">Fishing Helper</string>
    <string name="accessibility_service_description">Fishing Planet Helper - Auto fishing automation service</string>
</resources>
```

2. Buat folder **xml** di `res/` (jika belum ada)
3. Copy semua file dari artifacts ke lokasi yang sesuai

### Step 4: Build APK
1. Di AIDE, pilih menu → "Build & Run"
2. Tunggu proses compile selesai
3. APK akan terinstall otomatis

## 📱 Cara Menggunakan Aplikasi

### Pertama Kali Setup:

#### 1. Enable Permissions
- Buka aplikasi
- Akan muncul request permission:
  - **Overlay Permission** ✓
  - **Accessibility Service** ✓
- Enable semua permission

#### 2. Setup Koordinat
1. Buka **Fishing Planet** game
2. Masuk ke area fishing
3. Minimize game (jangan tutup!)
4. Buka **Fishing Helper** app
5. Tap "**Setup Coordinates**"
6. Ikuti instruksi:
   - Tap tombol **REEL MANUAL** (Kuning) di game
   - Tap tombol **REEL AUTO** (Hijau) di game
   - Tap tombol **ROD JERK** (Biru) di game
7. Tekan "**DONE**"

✅ Koordinat tersimpan! Setup hanya dilakukan 1x.

### Menggunakan Automation:

#### Mode 1: Stop & Go
**Untuk**: Teknik Stop & Go retrieval
- **Settings**:
  - Hold: 1-5 detik (berapa lama reel)
  - Pause: 0.5-3 detik (berapa lama berhenti)
- **Fungsi**: Otomatis tap & hold reel → lepas → repeat

#### Mode 2A: Auto Reel + Jerk
**Untuk**: Teknik dengan rod jerking
- **Settings**:
  - Jerk Interval: 0.5-3 detik
- **Fungsi**: Activate auto reel 1x → jerk rod berulang

#### Mode 2B: Manual Reel + Jerk
**Untuk**: Kombinasi manual reel & jerking
- **Settings**:
  - Hold: berapa lama reel
  - Pause: jeda sebelum jerk
- **Fungsi**: Reel → lepas → jerk → repeat

### Anti-Detection Settings:

**Random Coordinate**: ±5-20px
- Membuat tap tidak selalu di titik yang sama
- Rekomendasi: 10-15px

**Timing Variance**: ±5-30%
- Membuat durasi hold/pause bervariasi
- Rekomendasi: 15-20%

### Cara Pakai:
1. Setup koordinat (1x saja)
2. Pilih mode sesuai teknik fishing
3. Atur timing sesuai keinginan
4. Buka game Fishing Planet
5. Cast umpan seperti biasa
6. Kembali ke Fishing Helper
7. Tekan "**START**"
8. Minimize app
9. Bot akan bekerja otomatis!
10. Tekan "**STOP**" untuk berhenti

## ⚙️ Tips & Tricks

### Untuk Hasil Optimal:
- **Koordinat akurat**: Tap tepat di tengah button
- **Timing adjustment**: Test di game, sesuaikan duration
- **Anti-detection ON**: Aktifkan randomization
- **Jangan 24/7**: Main 30-60 menit, istirahat 5-10 menit

### Troubleshooting:

**Bot tidak jalan?**
- ✓ Pastikan Accessibility Service enabled
- ✓ Pastikan Overlay Permission enabled
- ✓ Koordinat sudah di-setup
- ✓ Game tidak di fullscreen mode (biar ada space untuk kontrol)

**Tap meleset?**
- Setup ulang koordinat dengan lebih akurat
- Kurangi random range di settings

**Timing tidak pas?**
- Sesuaikan slider Hold/Pause/Jerk
- Test beberapa kali untuk timing terbaik

## 🔒 Safety & Legal

**DISCLAIMER**: 
- Aplikasi ini untuk **edukasi** dan **personal use**
- Gunakan dengan **risiko sendiri**
- Beberapa game **melarang** penggunaan bot
- Bisa kena **ban** dari game

**Safety Tips**:
- Jangan gunakan 24/7
- Gunakan randomization maksimal
- Variasikan waktu bermain
- Jangan terlalu "perfect"

## 📝 Notes untuk Development

### Jika ingin modifikasi:
- Edit timing di **AutoFishService.java**
- Tambah mode baru di **MainActivity.java**
- Kustomisasi UI di **activity_main.xml**

### Untuk MODE 3 (Smart Fighting) - Coming Soon:
- Butuh screen capture & color detection
- Butuh library tambahan (OpenCV/TensorFlow)
- Lebih kompleks, perlu PC untuk development

## 🎯 Future Features

- [ ] Mode 3: Smart Fighting (auto tension management)
- [ ] Rod Stand detection
- [ ] Pattern recording (record & replay)
- [ ] Multiple preset profiles
- [ ] Statistics tracking
- [ ] Cloud sync settings

## 📞 Support

Jika ada bug atau pertanyaan:
- Test ulang setup koordinat
- Cek permission sudah enabled semua
- Restart app dan game
- Setup ulang dari awal jika perlu

---

**Happy Fishing! 🎣**

*Version 1.0 - Basic Automation*
