package com.example.currencyandprayerwidget.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;

import com.example.currencyandprayerwidget.R;
import com.example.currencyandprayerwidget.api.AladhanApiService;
import com.example.currencyandprayerwidget.api.AladhanResponse;
import com.example.currencyandprayerwidget.api.ApiClient;
import com.example.currencyandprayerwidget.api.ExchangeRateApiService;
import com.example.currencyandprayerwidget.api.ExchangeRateResponse;
import com.example.currencyandprayerwidget.model.CurrencyRate;
import com.example.currencyandprayerwidget.model.PrayerTime;
import com.example.currencyandprayerwidget.widget.CurrencyPrayerWidget;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Widget verilerini güncellemek için arka plan servisi
 */
public class WidgetUpdateService extends Service {
    private static final String TAG = "WidgetUpdateService";
    
    // Takip edilecek döviz kurları
    private static final String[] CURRENCIES = {"USD", "EUR", "GBP", "XAU"};
    
    // Varsayılan konum (İstanbul)
    private static final double DEFAULT_LATITUDE = 41.0082;
    private static final double DEFAULT_LONGITUDE = 28.9784;
    
    // Namaz vakti hesaplama metodu (Diyanet İşleri Başkanlığı - Türkiye)
    private static final int PRAYER_CALCULATION_METHOD = 13;
    
    // Kalan süre güncelleme aralığı (saniye)
    private static final int REMAINING_TIME_UPDATE_INTERVAL = 60;
    
    // Mevcut sonraki namaz vakti
    private static PrayerTime currentNextPrayer = null;
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Widget güncelleme servisi başlatıldı");
        
        // Döviz kurlarını güncelle
        updateCurrencyRates();
        
        // Namaz vakitlerini güncelle
        updatePrayerTimes();
        
        return START_STICKY;
    }
    
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    /**
     * Döviz kurlarını API'den alır ve widget'ı günceller
     */
    private void updateCurrencyRates() {
        ExchangeRateApiService service = ApiClient.getExchangeRateService();
        
        // İstenen para birimlerini belirt
        String symbols = String.join(",", CURRENCIES);
        
        // API çağrısını yap
        Call<ExchangeRateResponse> call = service.getSpecificRates(symbols);
        call.enqueue(new Callback<ExchangeRateResponse>() {
            @Override
            public void onResponse(Call<ExchangeRateResponse> call, Response<ExchangeRateResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ExchangeRateResponse data = response.body();
                    
                    // Kur verilerini işle
                    Map<String, CurrencyRate> currencyRates = processCurrencyData(data);
                    
                    // Widget'ı güncelle
                    updateWidgetWithCurrencyData(currencyRates, data.getDate());
                } else {
                    Log.e(TAG, "API yanıtı başarısız: " + response.message());
                    updateWidgetWithCurrencyError();
                }
            }
            
            @Override
            public void onFailure(Call<ExchangeRateResponse> call, Throwable t) {
                Log.e(TAG, "API çağrısı başarısız: " + t.getMessage());
                updateWidgetWithCurrencyError();
            }
        });
    }
    
    /**
     * API yanıtından döviz kuru verilerini işler
     */
    private Map<String, CurrencyRate> processCurrencyData(ExchangeRateResponse response) {
        Map<String, CurrencyRate> result = new HashMap<>();
        
        if (response.getRates() != null) {
            Map<String, Double> rates = response.getRates();
            
            // TRY baz para birimi olduğundan, diğer para birimlerinin TRY karşılığını hesapla
            for (String currency : CURRENCIES) {
                if (rates.containsKey(currency)) {
                    // API'den gelen değer TRY/Currency olduğundan, Currency/TRY için tersini alıyoruz
                    double rate = 1.0 / rates.get(currency);
                    result.put(currency, new CurrencyRate(currency, "TRY", rate, response.getDate()));
                }
            }
        }
        
        return result;
    }
    
    /**
     * Namaz vakitlerini API'den alır ve widget'ı günceller
     */
    private void updatePrayerTimes() {
        AladhanApiService service = ApiClient.getAladhanService();
        
        // Konum bilgisini al (varsayılan olarak İstanbul)
        double latitude = DEFAULT_LATITUDE;
        double longitude = DEFAULT_LONGITUDE;
        
        // Konum servisinden güncel konum almaya çalış
        try {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null) {
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastKnownLocation != null) {
                    latitude = lastKnownLocation.getLatitude();
                    longitude = lastKnownLocation.getLongitude();
                }
            }
        } catch (SecurityException e) {
            Log.e(TAG, "Konum izni yok: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Konum alınamadı: " + e.getMessage());
        }
        
        // API çağrısını yap
        Call<AladhanResponse> call = service.getPrayerTimesByCoordinates(
                latitude, longitude, PRAYER_CALCULATION_METHOD);
        
        call.enqueue(new Callback<AladhanResponse>() {
            @Override
            public void onResponse(Call<AladhanResponse> call, Response<AladhanResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AladhanResponse data = response.body();
                    
                    // Namaz vakitlerini işle
                    List<PrayerTime> prayerTimes = processPrayerTimesData(data);
                    
                    // Sonraki namaz vaktini bul
                    PrayerTime nextPrayer = findNextPrayer(prayerTimes);
                    
                    // Widget'ı güncelle
                    if (nextPrayer != null) {
                        currentNextPrayer = nextPrayer;
                        updateWidgetWithPrayerData(nextPrayer);
                        
                        // Kalan süre güncellemesini başlat
                        scheduleRemainingTimeUpdates();
                    } else {
                        updateWidgetWithPrayerError();
                    }
                } else {
                    Log.e(TAG, "API yanıtı başarısız: " + response.message());
                    updateWidgetWithPrayerError();
                }
            }
            
            @Override
            public void onFailure(Call<AladhanResponse> call, Throwable t) {
                Log.e(TAG, "API çağrısı başarısız: " + t.getMessage());
                updateWidgetWithPrayerError();
            }
        });
    }
    
    /**
     * API yanıtından namaz vakitleri verilerini işler
     */
    private List<PrayerTime> processPrayerTimesData(AladhanResponse response) {
        List<PrayerTime> prayerTimes = new ArrayList<>();
        
        if (response.getData() != null && response.getData().getTimings() != null) {
            Map<String, String> timings = response.getData().getTimings();
            
            // Namaz vakitlerini işle
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date currentDate = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentDate);
            
            for (Map.Entry<String, String> entry : timings.entrySet()) {
                String name = entry.getKey();
                String timeStr = entry.getValue();
                
                // Sadece ana namaz vakitlerini al
                if (isMainPrayer(name)) {
                    try {
                        // Saat ve dakikayı parse et
                        Date prayerTime = sdf.parse(timeStr);
                        if (prayerTime != null) {
                            // Bugünün tarihiyle birleştir
                            Calendar prayerCalendar = Calendar.getInstance();
                            prayerCalendar.setTime(currentDate);
                            Calendar tempCalendar = Calendar.getInstance();
                            tempCalendar.setTime(prayerTime);
                            
                            prayerCalendar.set(Calendar.HOUR_OF_DAY, tempCalendar.get(Calendar.HOUR_OF_DAY));
                            prayerCalendar.set(Calendar.MINUTE, tempCalendar.get(Calendar.MINUTE));
                            prayerCalendar.set(Calendar.SECOND, 0);
                            
                            // PrayerTime nesnesini oluştur
                            PrayerTime prayer = new PrayerTime(name, prayerCalendar.getTime(), false);
                            prayerTimes.add(prayer);
                        }
                    } catch (ParseException e) {
                        Log.e(TAG, "Namaz vakti parse edilemedi: " + e.getMessage());
                    }
                }
            }
        }
        
        return prayerTimes;
    }
    
    /**
     * Ana namaz vakitlerini kontrol eder
     */
    private boolean isMainPrayer(String name) {
        return name.equalsIgnoreCase("Fajr") ||
                name.equalsIgnoreCase("Sunrise") ||
                name.equalsIgnoreCase("Dhuhr") ||
                name.equalsIgnoreCase("Asr") ||
                name.equalsIgnoreCase("Maghrib") ||
                name.equalsIgnoreCase("Isha");
    }
    
    /**
     * Sonraki namaz vaktini bulur
     */
    private PrayerTime findNextPrayer(List<PrayerTime> prayerTimes) {
        if (prayerTimes.isEmpty()) {
            return null;
        }
        
        Date now = new Date();
        PrayerTime nextPrayer = null;
        long minDiff = Long.MAX_VALUE;
        
        for (PrayerTime prayer : prayerTimes) {
            long diff = prayer.getTime().getTime() - now.getTime();
            
            // Sadece gelecekteki namaz vakitlerini kontrol et
            if (diff > 0 && diff < minDiff) {
                minDiff = diff;
                nextPrayer = prayer;
            }
        }
        
        // Eğer bugün kalan namaz vakti yoksa, yarının ilk namaz vaktini kullan
        if (nextPrayer == null) {
            // Yarının ilk namaz vakti (Fajr) olarak varsay
            for (PrayerTime prayer : prayerTimes) {
                if (prayer.getName().equalsIgnoreCase("Fajr")) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(prayer.getTime());
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                    
                    nextPrayer = new PrayerTime(prayer.getName(), calendar.getTime(), true);
                    break;
                }
            }
        } else {
            nextPrayer.setNext(true);
        }
        
        return nextPrayer;
    }
    
    /**
     * Kalan süre güncellemelerini zamanlar
     */
    private void scheduleRemainingTimeUpdates() {
        // AlarmManager kullanarak periyodik güncelleme
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, RemainingTimeUpdateReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        
        // Periyodik alarm kur
        if (alarmManager != null) {
            alarmManager.setInexactRepeating(
                    AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime() + REMAINING_TIME_UPDATE_INTERVAL * 1000,
                    REMAINING_TIME_UPDATE_INTERVAL * 1000,
                    pendingIntent);
        }
    }
    
    /**
     * Widget'ı güncel döviz kuru verileriyle günceller
     */
    private void updateWidgetWithCurrencyData(Map<String, CurrencyRate> currencyRates, String date) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        ComponentName thisWidget = new ComponentName(this, CurrencyPrayerWidget.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(getPackageName(), R.layout.currency_prayer_widget);
            
            // Döviz kurlarını güncelle
            if (currencyRates.containsKey("USD")) {
                views.setTextViewText(R.id.tv_usd_try, "USD: " + String.format(Locale.getDefault(), "%.4f ₺", currencyRates.get("USD").getRate()));
            }
            
            if (currencyRates.containsKey("EUR")) {
                views.setTextViewText(R.id.tv_eur_try, "EUR: " + String.format(Locale.getDefault(), "%.4f ₺", currencyRates.get("EUR").getRate()));
            }
            
            if (currencyRates.containsKey("GBP")) {
                views.setTextViewText(R.id.tv_gbp_try, "GBP: " + String.format(Locale.getDefault(), "%.4f ₺", currencyRates.get("GBP").getRate()));
            }
            
            if (currencyRates.containsKey("XAU")) {
                views.setTextViewText(R.id.tv_gold_try, "ALTIN: " + String.format(Locale.getDefault(), "%.2f ₺", currencyRates.get("XAU").getRate()));
            }
            
            // Son güncelleme zamanını göster
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
            String lastUpdate = "Son güncelleme: " + sdf.format(new Date()) + " (Tarih: " + date + ")";
            views.setTextViewText(R.id.tv_last_update, lastUpdate);
            
            // Widget'ı güncelle
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
    
    /**
     * Widget'ı güncel namaz vakti verileriyle günceller
     */
    private void updateWidgetWithPrayerData(PrayerTime nextPrayer) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        ComponentName thisWidget = new ComponentName(this, CurrencyPrayerWidget.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(getPackageName(), R.layout.currency_prayer_widget);
            
            // Sonraki namaz vaktini göster
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String prayerTimeStr = sdf.format(nextPrayer.getTime());
            
            views.setTextViewText(R.id.tv_next_prayer, nextPrayer.getTurkishName());
            views.setTextViewText(R.id.tv_next_prayer_time, prayerTimeStr);
            
            // Kalan süreyi göster
            views.setTextViewText(R.id.tv_remaining_time, nextPrayer.getFormattedRemainingTime());
            
            // Widget'ı güncelle
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
    
    /**
     * Döviz kuru hatası durumunda widget'ı günceller
     */
    private void updateWidgetWithCurrencyError() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        ComponentName thisWidget = new ComponentName(this, CurrencyPrayerWidget.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(getPackageName(), R.layout.currency_prayer_widget);
            
            // Hata mesajını göster
            String errorMsg = getString(R.string.error_loading);
            views.setTextViewText(R.id.tv_usd_try, "USD: " + errorMsg);
            views.setTextViewText(R.id.tv_eur_try, "EUR: " + errorMsg);
            views.setTextViewText(R.id.tv_gbp_try, "GBP: " + errorMsg);
            views.setTextViewText(R.id.tv_gold_try, "ALTIN: " + errorMsg);
            
            // Son güncelleme zamanını göster
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
            String lastUpdate = "Son güncelleme: " + sdf.format(new Date()) + " (Hata)";
            views.setTextViewText(R.id.tv_last_update, lastUpdate);
            
            // Widget'ı güncelle
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
    
    /**
     * Namaz vakti hatası durumunda widget'ı günceller
     */
    private void updateWidgetWithPrayerError() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        ComponentName thisWidget = new ComponentName(this, CurrencyPrayerWidget.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(getPackageName(), R.layout.currency_prayer_widget);
            
            // Hata mesajını göster
            String errorMsg = getString(R.string.error_loading);
            views.setTextViewText(R.id.tv_next_prayer, getString(R.string.next_prayer));
            views.setTextViewText(R.id.tv_next_prayer_time, errorMsg);
            views.setTextViewText(R.id.tv_remaining_time, errorMsg);
            
            // Widget'ı güncelle
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
    
    /**
     * Kalan süreyi güncellemek için statik metot
     */
    public static void updateRemainingTime(Context context) {
        if (currentNextPrayer != null) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName thisWidget = new ComponentName(context, CurrencyPrayerWidget.class);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
            
            for (int appWidgetId : appWidgetIds) {
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.currency_prayer_widget);
                
                // Kalan süreyi güncelle
                views.setTextViewText(R.id.tv_remaining_time, currentNextPrayer.getFormattedRemainingTime());
                
                // Widget'ı güncelle
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
            
            // Eğer kalan süre 0 veya negatifse, namaz vakitlerini yeniden al
            if (currentNextPrayer.getRemainingTime() <= 0) {
                Intent intent = new Intent(context, WidgetUpdateService.class);
                context.startService(intent);
            }
        }
    }
}
