package com.example.currencyandprayerwidget.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.currencyandprayerwidget.R;
import com.example.currencyandprayerwidget.service.WidgetUpdateService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Widget sağlayıcı sınıfı.
 * Döviz kurları ve namaz vakitleri widget'ını yönetir.
 */
public class CurrencyPrayerWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Widget güncellendiğinde çalışacak kod
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        
        // Servis başlatarak verileri güncelle
        Intent intent = new Intent(context, WidgetUpdateService.class);
        context.startService(intent);
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        // Widget görünümünü oluştur
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.currency_prayer_widget);
        
        // Yükleniyor mesajını göster
        views.setTextViewText(R.id.tv_usd_try, "USD: " + context.getString(R.string.loading));
        views.setTextViewText(R.id.tv_eur_try, "EUR: " + context.getString(R.string.loading));
        views.setTextViewText(R.id.tv_gbp_try, "GBP: " + context.getString(R.string.loading));
        views.setTextViewText(R.id.tv_gold_try, "ALTIN: " + context.getString(R.string.loading));
        
        views.setTextViewText(R.id.tv_next_prayer_time, context.getString(R.string.loading));
        views.setTextViewText(R.id.tv_remaining_time, context.getString(R.string.loading));
        
        // Son güncelleme zamanını göster
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
        String lastUpdate = "Son güncelleme: " + sdf.format(new Date());
        views.setTextViewText(R.id.tv_last_update, lastUpdate);
        
        // Widget'ı güncelle
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onEnabled(Context context) {
        // Widget ilk kez eklendiğinde çalışacak kod
    }

    @Override
    public void onDisabled(Context context) {
        // Widget kaldırıldığında çalışacak kod
    }
}
