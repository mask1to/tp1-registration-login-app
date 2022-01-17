package net.javaguides.springboot.model;

import eu.bitwalker.useragentutils.Version;

import java.util.Date;


public class Transaction {

    Date datum;
    String ipAdresa;
    String pozadovanaTransakcia;
    String krajina;
    String operatingSystem;
    String browser;
    Version browserVersion;
    String email;


    public Transaction(Date datum, String ipAdresa, String pozadovanaTransakcia, String krajina, String operatingSystem, String browser, Version browserVersion, String email) {
        this.datum = datum;
        this.ipAdresa = ipAdresa;
        this.pozadovanaTransakcia = pozadovanaTransakcia;
        this.krajina = krajina;
        this.operatingSystem = operatingSystem;
        this.browser = browser;
        this.browserVersion = browserVersion;
        this.email = email;
    }

    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }

    public String getIpAdresa() {
        return ipAdresa;
    }

    public void setIpAdresa(String ipAdresa) {
        this.ipAdresa = ipAdresa;
    }

    public String getPozadovanaTransakcia() {
        return pozadovanaTransakcia;
    }

    public void setPozadovanaTransakcia(String pozadovanaTransakcia) {
        this.pozadovanaTransakcia = pozadovanaTransakcia;
    }

    public String getKrajina() {
        return krajina;
    }

    public void setKrajina(String krajina) {
        this.krajina = krajina;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public Version getBrowserVersion() {
        return browserVersion;
    }

    public void setBrowserVersion(Version browserVersion) {
        this.browserVersion = browserVersion;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
