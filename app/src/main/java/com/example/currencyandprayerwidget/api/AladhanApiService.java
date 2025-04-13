package com.example.currencyandprayerwidget.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Aladhan Prayer Times API için Retrofit servis arayüzü
 */
public interface AladhanApiService {
    
    // Belirli bir tarih ve konum için namaz vakitlerini getir
    @GET("timingsByCity")
    Call<AladhanResponse> getPrayerTimesByCity(
            @Query("city") String city,
            @Query("country") String country,
            @Query("method") int method);
    
    // Belirli bir tarih ve koordinat için namaz vakitlerini getir
    @GET("timings")
    Call<AladhanResponse> getPrayerTimesByCoordinates(
            @Query("latitude") double latitude,
            @Query("longitude") double longitude,
            @Query("method") int method);
}
