# Digital Book Kabilah — Android Model A

Model A ini adalah **online WebView wrapper**.

- Sumber konten: https://digital-book.kabilahtour.com/
- Perubahan website langsung tampil di aplikasi.
- Tidak ada salinan HTML lokal di dalam APK.
- Package ID: `com.kabilahtour.digitalbook`
- Version: `1.0.0` / versionCode `1`
- minSdk: 23
- targetSdk: 36
- compileSdk: 36
- Android Gradle Plugin: 9.4.0
- Gradle: 9.6.0

## Build di Android Studio

1. Extract ZIP project.
2. Buka folder `DigitalBookKabilah` di Android Studio.
3. Tunggu Gradle Sync selesai.
4. Untuk testing: `Run > Run 'app'`.
5. Untuk Google Play: `Build > Generate Signed App Bundle / APK > Android App Bundle`.
6. Pilih `release`, lalu buat/masukkan keystore release.

## Catatan

Aplikasi memerlukan internet untuk mengambil website live. Data yang Anda ubah di website tidak memerlukan release aplikasi baru.

Tautan non-web seperti `tel:`, `mailto:`, dan `whatsapp:` akan diteruskan ke aplikasi Android yang sesuai.
