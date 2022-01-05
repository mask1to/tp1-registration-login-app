package net.javaguides.springboot.model;

import java.util.Date;


public class Transaction {

    Date datum;
    String ipAdresa;
    String pozadovanaTransakcia;
    String krajina ;


    public Transaction(String ipAdresa, String pozadovanaTransakcia, Date datum, String krajina) {
        this.ipAdresa = ipAdresa;
        this.pozadovanaTransakcia = pozadovanaTransakcia;
        this.datum = datum;
        this.krajina = krajina;
    }

    public String getKrajina() {
        return krajina;
    }

    public void setKrajina(String krajina) {
        this.krajina = krajina;
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









}
