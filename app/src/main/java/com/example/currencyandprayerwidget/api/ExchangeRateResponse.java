package com.example.currencyandprayerwidget.api;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * ExchangeRate-API yanıt modeli
 */
public class ExchangeRateResponse {
    
    @SerializedName("base")
    private String base;
    
    @SerializedName("date")
    private String date;
    
    @SerializedName("rates")
    private Map<String, Double> rates;
    
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("timestamp")
    private long timestamp;

    public String getBase() {
        return base;
    }

    public String getDate() {
        return date;
    }

    public Map<String, Double> getRates() {
        return rates;
    }

    public boolean isSuccess() {
        return success;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
