package com.example.currencyandprayerwidget.model;

/**
 * Döviz kuru bilgilerini tutan model sınıfı
 */
public class CurrencyRate {
    private String base;
    private String target;
    private double rate;
    private String lastUpdate;

    public CurrencyRate(String base, String target, double rate, String lastUpdate) {
        this.base = base;
        this.target = target;
        this.rate = rate;
        this.lastUpdate = lastUpdate;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Override
    public String toString() {
        return base + ": " + String.format("%.4f", rate) + " " + target;
    }
}
