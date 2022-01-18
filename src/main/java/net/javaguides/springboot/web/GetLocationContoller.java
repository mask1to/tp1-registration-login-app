package net.javaguides.springboot.web;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CountryResponse;
import net.javaguides.springboot.model.GeoIp;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;

public class GetLocationContoller {
    private DatabaseReader dbReader;

    public GetLocationContoller() throws IOException {
        InputStream database = getClass().getResourceAsStream("/CountriesDB/GeoLite2-Country.mmdb");

        dbReader = new DatabaseReader.Builder(database).build();

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
