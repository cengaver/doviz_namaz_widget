package com.example.currencyandprayerwidget.api;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * Aladhan API yanıt modeli
 */
public class AladhanResponse {
    
    @SerializedName("code")
    private int code;
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("data")
    private PrayerData data;
    
    public int getCode() {
        return code;
    }
    
    public String getStatus() {
        return status;
    }
    
    public PrayerData getData() {
        return data;
    }
    
    /**
     * Namaz vakitleri verilerini içeren iç sınıf
     */
    public static class PrayerData {
        
        @SerializedName("timings")
        private Map<String, String> timings;
        
        @SerializedName("date")
        private DateInfo date;
        
        @SerializedName("meta")
        private Meta meta;
        
        public Map<String, String> getTimings() {
            return timings;
        }
        
        public DateInfo getDate() {
            return date;
        }
        
        public Meta getMeta() {
            return meta;
        }
    }
    
    /**
     * Tarih bilgilerini içeren iç sınıf
     */
    public static class DateInfo {
        
        @SerializedName("readable")
        private String readable;
        
        @SerializedName("timestamp")
        private String timestamp;
        
        @SerializedName("gregorian")
        private Gregorian gregorian;
        
        @SerializedName("hijri")
        private Hijri hijri;
        
        public String getReadable() {
            return readable;
        }
        
        public String getTimestamp() {
            return timestamp;
        }
        
        public Gregorian getGregorian() {
            return gregorian;
        }
        
        public Hijri getHijri() {
            return hijri;
        }
    }
    
    /**
     * Miladi takvim bilgilerini içeren iç sınıf
     */
    public static class Gregorian {
        
        @SerializedName("date")
        private String date;
        
        @SerializedName("format")
        private String format;
        
        @SerializedName("day")
        private String day;
        
        @SerializedName("month")
        private GregorianMonth month;
        
        @SerializedName("year")
        private String year;
        
        public String getDate() {
            return date;
        }
        
        public String getFormat() {
            return format;
        }
        
        public String getDay() {
            return day;
        }
        
        public GregorianMonth getMonth() {
            return month;
        }
        
        public String getYear() {
            return year;
        }
    }
    
    /**
     * Miladi ay bilgilerini içeren iç sınıf
     */
    public static class GregorianMonth {
        
        @SerializedName("number")
        private int number;
        
        @SerializedName("en")
        private String en;
        
        public int getNumber() {
            return number;
        }
        
        public String getEn() {
            return en;
        }
    }
    
    /**
     * Hicri takvim bilgilerini içeren iç sınıf
     */
    public static class Hijri {
        
        @SerializedName("date")
        private String date;
        
        @SerializedName("format")
        private String format;
        
        @SerializedName("day")
        private String day;
        
        @SerializedName("month")
        private HijriMonth month;
        
        @SerializedName("year")
        private String year;
        
        public String getDate() {
            return date;
        }
        
        public String getFormat() {
            return format;
        }
        
        public String getDay() {
            return day;
        }
        
        public HijriMonth getMonth() {
            return month;
        }
        
        public String getYear() {
            return year;
        }
    }
    
    /**
     * Hicri ay bilgilerini içeren iç sınıf
     */
    public static class HijriMonth {
        
        @SerializedName("number")
        private int number;
        
        @SerializedName("en")
        private String en;
        
        @SerializedName("ar")
        private String ar;
        
        public int getNumber() {
            return number;
        }
        
        public String getEn() {
            return en;
        }
        
        public String getAr() {
            return ar;
        }
    }
    
    /**
     * Meta bilgilerini içeren iç sınıf
     */
    public static class Meta {
        
        @SerializedName("latitude")
        private double latitude;
        
        @SerializedName("longitude")
        private double longitude;
        
        @SerializedName("timezone")
        private String timezone;
        
        @SerializedName("method")
        private Method method;
        
        public double getLatitude() {
            return latitude;
        }
        
        public double getLongitude() {
            return longitude;
        }
        
        public String getTimezone() {
            return timezone;
        }
        
        public Method getMethod() {
            return method;
        }
    }
    
    /**
     * Hesaplama metodu bilgilerini içeren iç sınıf
     */
    public static class Method {
        
        @SerializedName("id")
        private int id;
        
        @SerializedName("name")
        private String name;
        
        public int getId() {
            return id;
        }
        
        public String getName() {
            return name;
        }
    }
}
