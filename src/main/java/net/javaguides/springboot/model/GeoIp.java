package net.javaguides.springboot.model;

public class GeoIp {
    private String ipAddress;
    private String country;

    public GeoIp() {
    }

    public GeoIp(String ipAddress, String country) {
        this.ipAddress = ipAddress;
        this.country = country;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
