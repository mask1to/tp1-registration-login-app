package net.javaguides.springboot.web;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CountryResponse;
import net.javaguides.springboot.model.GeoIp;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

public class GetLocationContoller {
    private DatabaseReader dbReader;

    public GetLocationContoller() throws IOException {
        File database = new File("src/main/resources/CountriesDB/GeoLite2-Country.mmdb");
        dbReader = new DatabaseReader.Builder(database).build();

        System.out.println(database);
    }

    public GeoIp getLocation(String ip) {

        try {
            InetAddress ipAddress = InetAddress.getByName(ip);
            CountryResponse response = dbReader.country(ipAddress);

            String country = response.getCountry().getName();
            return new GeoIp(ip, country);
        }
        catch(Exception e) {

        }

        return null;
    }
}
