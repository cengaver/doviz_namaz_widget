package com.example.currencyandprayerwidget.model;

import java.util.Date;

/**
 * Namaz vakti bilgilerini tutan model sınıfı
 */
public class PrayerTime {
    private String name;
    private Date time;
    private boolean isNext;

    public PrayerTime(String name, Date time, boolean isNext) {
        this.name = name;
        this.time = time;
        this.isNext = isNext;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public boolean isNext() {
        return isNext;
    }

    public void setNext(boolean next) {
        isNext = next;
    }

    /**
     * Şu anki zaman ile namaz vakti arasındaki farkı milisaniye cinsinden döndürür
     * @return Kalan süre (ms)
     */
    public long getRemainingTime() {
        return time.getTime() - new Date().getTime();
    }

    /**
     * Kalan süreyi saat:dakika:saniye formatında döndürür
     * @return Formatlanmış kalan süre
     */
    public String getFormattedRemainingTime() {
        long remainingMs = getRemainingTime();
        
        if (remainingMs <= 0) {
            return "00:00:00";
        }
        
        // Milisaniyeyi saat, dakika, saniyeye çevir
        long seconds = remainingMs / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        
        seconds = seconds % 60;
        minutes = minutes % 60;
        
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    /**
     * Türkçe namaz vakti adını döndürür
     * @return Türkçe namaz vakti adı
     */
    public String getTurkishName() {
        switch (name.toLowerCase()) {
            case "fajr":
                return "İmsak";
            case "sunrise":
                return "Güneş";
            case "dhuhr":
                return "Öğle";
            case "asr":
                return "İkindi";
            case "maghrib":
                return "Akşam";
            case "isha":
                return "Yatsı";
            default:
                return name;
        }
    }
}
