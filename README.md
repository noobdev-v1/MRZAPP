# Karşılama MRZ Uygulaması

Otel resepsiyonu için Android MRZ okuyucu.  
Tamamen offline çalışır — internet, sunucu veya API ücreti yoktur.

## Özellikler
- Oda bazlı misafir gruplama
- CameraX + ML Kit ile cihazda MRZ okuma (internet gerekmez)
- TD1 (kimlik kartı) ve TD3 (pasaport) formatı desteği
- Gün sonu otomatik veri silme
- WhatsApp ile tek tuşta paylaşım

## Derleme (Android Studio ile)

1. **Android Studio** indirin: https://developer.android.com/studio (ücretsiz)
2. Bu klasörü açın: `File → Open → MrzApp klasörünü seçin`
3. Gradle sync otomatik başlar, tamamlanmasını bekleyin (~2-3 dk, ilk seferinde)
4. Telefonu USB ile bağlayın, USB hata ayıklama açın
5. Yeşil ▶ Run butonuna basın → APK telefona yüklenir

## APK çıkarmak (dağıtım için)

`Build → Build Bundle(s) / APK(s) → Build APK(s)`

APK dosyası: `app/build/outputs/apk/debug/app-debug.apk`

Bu dosyayı WhatsApp veya e-posta ile diğer telefonlara gönderin.  
Kurmak için: Ayarlar → Bilinmeyen kaynaklar → İzin ver

## Minimum Gereksinim
- Android 8.0 (API 26) ve üzeri
- Kamera
