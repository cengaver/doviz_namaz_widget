package com.example.currencyandprayerwidget;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;

import com.example.currencyandprayerwidget.service.WidgetUpdateService;
import com.example.currencyandprayerwidget.widget.CurrencyPrayerWidget;

/**
 * Ana aktivite sınıfı
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView titleTextView = findViewById(R.id.tv_title);
        TextView descriptionTextView = findViewById(R.id.tv_description);
        Button refreshButton = findViewById(R.id.btn_refresh);

        titleTextView.setText(R.string.app_name);
        descriptionTextView.setText(R.string.widget_description);

        // Yenile butonuna tıklandığında widget'ı güncelle
        refreshButton.setOnClickListener(v -> {
            // Widget güncelleme servisini başlat
            Intent intent = new Intent(this, WidgetUpdateService.class);
            startService(intent);

            // Widget'ı güncelle
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            ComponentName thisWidget = new ComponentName(this, CurrencyPrayerWidget.class);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.tv_last_update);
        });
    }
}
