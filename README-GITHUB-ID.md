# Build Digital Book Kabilah tanpa Android Studio

Project ini adalah **Model A**: aplikasi Android membuka website live `https://digital-book.kabilahtour.com/`.
Perubahan isi website otomatis tampil di aplikasi, sehingga perubahan konten tidak memerlukan AAB baru.

## Pilihan paling sederhana: GitHub Actions

1. Buat repository GitHub baru, misalnya `digital-book-kabilah-android`.
2. Upload **isi folder `app_project`** ke repository tersebut.
3. Pastikan branch utamanya bernama `main`.
4. Buka tab **Actions** → workflow **Build Digital Book Android** → **Run workflow**.
5. Setelah selesai, buka hasil workflow → **Artifacts** → download `digital-book-kabilah-aab`.

## Untuk Google Play: gunakan release signing

AAB untuk Play Store harus ditandatangani. Jangan menyimpan keystore atau password ke dalam repository.

Buat upload keystore satu kali dengan `keytool`, kemudian simpan 4 nilai berikut sebagai **GitHub Actions Secrets**:

- `ANDROID_KEYSTORE_BASE64`
- `ANDROID_KEYSTORE_PASSWORD`
- `ANDROID_KEY_ALIAS`
- `ANDROID_KEY_PASSWORD`

Contoh encoding keystore:

```bash
base64 -w 0 release-upload-key.jks > release-upload-key.txt
```

Pada Windows PowerShell, bisa menggunakan:

```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("release-upload-key.jks"))
```

Setelah ke-4 secret tersedia, jalankan workflow lagi. Artifact yang sama akan berisi **signed release AAB**.

## Konfigurasi aplikasi

- Package ID: `com.kabilahtour.digitalbook`
- Version: `1.0.0` / versionCode `1`
- Target SDK: `36`
- Website: `https://digital-book.kabilahtour.com/`

Google Play mensyaratkan aplikasi baru dan update mulai 31 Agustus 2026 menargetkan Android 16 (API 36) atau lebih tinggi.
