package net.javaguides.springboot.web;

import eu.bitwalker.useragentutils.Version;
import net.javaguides.springboot.config.SecurityConfiguration;
import net.javaguides.springboot.model.Transaction;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.simple.JSONObject;
import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class RiskServerController {
    public RiskServerController() {
    }

    Logger logger = LoggerFactory.getLogger(SecurityConfiguration.class);

    public int callRiskServer(String date, String ipAddress, String country, String operatingSystem, String browser, String browserVersion, String email, String transaction) {
        String url = "https://serene-refuge-96326.herokuapp.com/oauth/token?scope=write&grant_type=password&username=foo&password=foo";
//        String url = "http://localhost:8080/oauth/token?scope=write&grant_type=password&username=foo&password=foo";
        RestTemplate rt = new RestTemplate();

        String plainCreds = "clientId:abcd";
        byte[] plainCredsBytes = plainCreds.getBytes();
        byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
        String base64Creds = new String(base64CredsBytes);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Creds);

        HttpEntity<String> request = new HttpEntity<String>(headers);
        ResponseEntity<?> response = rt.exchange(url, HttpMethod.POST, request, JSONObject.class);
        JSONObject jsontoken = (JSONObject) response.getBody();
        String token = (String) jsontoken.get("access_token");
        logger.info(token);

//        url = "http://localhost:8080/evaluate";
        url = "https://serene-refuge-96326.herokuapp.com/evaluate";
        rt = new RestTemplate();
        headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        Transaction body = new Transaction(date, ipAddress, transaction, country, operatingSystem, browser, browserVersion, email);
        HttpEntity<Transaction> entity = new HttpEntity<Transaction>(body, headers);
        String risk_result = null;
        try {
            ResponseEntity<String> responseValue = rt.exchange(url, HttpMethod.POST, entity, String.class);
            risk_result = responseValue.getBody();
            logger.info(risk_result);
        } catch (HttpStatusCodeException e) {
            String errorpayload = e.getResponseBodyAsString();
            logger.info(String.valueOf(errorpayload));
            // ako riesit nedostupnost risk servera???
        }

        if (risk_result.equals("blacklist")) {
            return 4;
        } else if (risk_result.equals("high risk")) {
            return 3;
        } else if (risk_result.equals("medium risk")) {
            return 2;
        } else if (risk_result.equals("low risk")) {
            return 1;
        }

        return 0;
    }
}
