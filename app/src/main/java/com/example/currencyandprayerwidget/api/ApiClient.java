package com.example.currencyandprayerwidget.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * API istemcisi oluşturmak için yardımcı sınıf
 */
public class ApiClient {
    
    private static final String EXCHANGE_RATE_BASE_URL = "https://open.er-api.com/v6/";
    private static final String ALADHAN_BASE_URL = "https://api.aladhan.com/v1/";
    
    private static Retrofit exchangeRateRetrofit = null;
    private static Retrofit aladhanRetrofit = null;
    
    /**
     * ExchangeRate API için Retrofit istemcisini oluşturur veya mevcut istemciyi döndürür
     * @return Retrofit istemcisi
     */
    public static Retrofit getExchangeRateClient() {
        if (exchangeRateRetrofit == null) {
            // HTTP isteklerini loglamak için interceptor oluştur
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            
            // OkHttpClient oluştur
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .build();
            
            // Retrofit istemcisini oluştur
            exchangeRateRetrofit = new Retrofit.Builder()
                    .baseUrl(EXCHANGE_RATE_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return exchangeRateRetrofit;
    }
    
    /**
     * Aladhan API için Retrofit istemcisini oluşturur veya mevcut istemciyi döndürür
     * @return Retrofit istemcisi
     */
    public static Retrofit getAladhanClient() {
        if (aladhanRetrofit == null) {
            // HTTP isteklerini loglamak için interceptor oluştur
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            
            // OkHttpClient oluştur
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .build();
            
            // Retrofit istemcisini oluştur
            aladhanRetrofit = new Retrofit.Builder()
                    .baseUrl(ALADHAN_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return aladhanRetrofit;
    }
    
    /**
     * ExchangeRate API servisi için bir örnek oluşturur
     * @return ExchangeRateApiService örneği
     */
    public static ExchangeRateApiService getExchangeRateService() {
        return getExchangeRateClient().create(ExchangeRateApiService.class);
    }
    
    /**
     * Aladhan API servisi için bir örnek oluşturur
     * @return AladhanApiService örneği
     */
    public static AladhanApiService getAladhanService() {
        return getAladhanClient().create(AladhanApiService.class);
    }
}
