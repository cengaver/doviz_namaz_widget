package com.example.currencyandprayerwidget.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Kalan süre güncellemelerini almak için BroadcastReceiver
 */
public class RemainingTimeUpdateReceiver extends BroadcastReceiver {
    private static final String TAG = "RemainingTimeReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Kalan süre güncelleme alındı");
        
        // WidgetUpdateService'in statik metodunu çağırarak kalan süreyi güncelle
        WidgetUpdateService.updateRemainingTime(context);
    }
}
