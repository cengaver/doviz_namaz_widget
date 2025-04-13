package com.example.currencyandprayerwidget.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * ExchangeRate-API için Retrofit servis arayüzü
 */
public interface ExchangeRateApiService {
    
    // Açık erişim API endpoint'i
    @GET("latest/TRY")
    Call<ExchangeRateResponse> getLatestRates();
    
    // Belirli para birimleri için kur bilgisi
    @GET("latest/TRY")
    Call<ExchangeRateResponse> getSpecificRates(@Query("symbols") String symbols);
}
