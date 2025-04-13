# Namaz Vakitleri API Araştırması

## Kullanılabilecek API Seçenekleri

### 1. Aladhan Prayer Times API (https://aladhan.com/prayer-times-api)
- RESTful API yapısı
- JSON formatında veri dönüşü
- Çeşitli endpoint'ler:
  - Belirli bir tarih için namaz vakitleri
  - Belirli bir adres için namaz vakitleri
  - Belirli bir şehir için namaz vakitleri
  - Sonraki namaz vakti bilgisi
  - Aylık ve yıllık namaz vakitleri takvimi
- Farklı hesaplama metotları desteği
- Ücretsiz kullanım

### 2. Muslim Pro API
- Doğru namaz vakitleri, ezan ve kıble yönü
- 170 milyondan fazla indirme ile popüler bir uygulama
- Mobil uygulamalar için API desteği

### 3. IslamicFinder API
- Dünya genelinde namaz vakitleri
- Farklı hesaplama metotları
- Konum bazlı namaz vakitleri

## Android Kütüphaneleri

### 1. Batoulapps/Adhan (https://github.com/batoulapps/Adhan)
- Yüksek hassasiyetli namaz vakti hesaplama kütüphanesi
- Çoklu platform desteği (Java/Android, Swift/iOS, JavaScript)
- İyi test edilmiş ve dokümante edilmiş
- Jean Meeus'un "Astronomical Algorithms" kitabındaki yüksek hassasiyetli denklemleri kullanır
- MIT lisansı altında açık kaynak
- Aktif geliştirme ve topluluk desteği

### 2. metinkale38/prayer-times-android (https://github.com/metinkale38/prayer-times-android)
- Her Müslümanın ihtiyaç duyduğu araçları içeren kapsamlı bir uygulama
- Namaz vakitleri için ezan okuma
- Namaz vakitleri için otomatik sessiz moda geçiş
- Namaz vakitleri için erken bildirim
- Mekruh namaz vakti göstergesi

### 3. ahmedeltaher/Prayer-Times-Android-Azan (https://github.com/ahmedeltaher/Prayer-Times-Android-Azan)
- Tek satır kod ile namaz vakti hesaplama kütüphanesi
- Android uygulamaları için özel olarak tasarlanmış

## Değerlendirme ve Öneriler

Android widget uygulaması için en uygun seçenekler:

1. **API: Aladhan Prayer Times API**
   - Kapsamlı endpoint'ler
   - Ücretsiz kullanım
   - JSON formatında veri dönüşü
   - "Sonraki namaz vakti" endpoint'i kalan süre hesaplaması için ideal

2. **Kütüphane: Batoulapps/Adhan**
   - Yüksek hassasiyetli hesaplamalar
   - İyi dokümante edilmiş
   - Android için Java implementasyonu
   - Açık kaynak ve aktif geliştirme
   - Kalan süre hesaplaması için uygun metotlar

Widget uygulaması için Aladhan API veya Batoulapps/Adhan kütüphanesi kullanılması önerilir. Aladhan API, sunucu tarafında hesaplama yaparak cihaz kaynaklarını korurken, Adhan kütüphanesi cihaz üzerinde hesaplama yaparak internet bağlantısı olmadan da çalışabilir.

## Kalan Süre Hesaplama Stratejisi

Namaz vakitleri arasında kalan süreyi hesaplamak için:

1. Mevcut zamanı al
2. Sonraki namaz vaktini belirle (API veya kütüphane ile)
3. İki zaman arasındaki farkı hesapla
4. Farkı saat, dakika ve saniye olarak formatla
5. Belirli aralıklarla (örneğin her dakika) güncelle

## Sonraki Adımlar
- Seçilen API veya kütüphanenin Android uygulamasına nasıl entegre edileceğini araştır
- Widget güncellemelerinin zamanlamasını planla
- Kalan süre hesaplama algoritmasını geliştir
- Farklı hesaplama metotları için kullanıcı tercihi ekle
