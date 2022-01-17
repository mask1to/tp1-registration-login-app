package net.javaguides.springboot.config;

import eu.bitwalker.useragentutils.UserAgent;
import eu.bitwalker.useragentutils.Version;
import net.javaguides.springboot.model.GeoIp;
import net.javaguides.springboot.model.Transaction;
import net.javaguides.springboot.model.User;
import net.javaguides.springboot.service.AddrService;
import net.javaguides.springboot.service.UserService;
import net.javaguides.springboot.web.GetLocationContoller;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    Logger logger = LoggerFactory.getLogger(SecurityConfiguration.class);

    @Autowired
    private UserService userService;

    @Autowired
    private AddrService addrService;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService((UserDetailsService) userService);
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    //.antMatchers("/registration**").access("hasRole('TEMP_USER')")
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers(
                        "/welcome**",
                        "/registration**",
                        "/registrationQR**",
                        "/about-team**",
                        "/js/**",
                        "/css/**",
                        "/img/**",
                        "/**").permitAll()
                .antMatchers("/GAlogin", "/home").hasRole("PRE_USER")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .permitAll()
                .successHandler((httpServletRequest, httpServletResponse, authentication) -> {
                    // call na risk server
                    Date date = Date.from(Instant.now());
                    String ipAddress = httpServletRequest.getRemoteAddr();
                    UserAgent userAgent = UserAgent.parseUserAgentString(httpServletRequest.getHeader("User-Agent"));
                    String browser = userAgent.getBrowser().getName();
                    Version browserVersion = userAgent.getBrowserVersion();
                    String browserDetails = httpServletRequest.getHeader("User-Agent");
                    String userAgent1 = browserDetails;
                    String operatingSystem;
                    if (userAgent1.toLowerCase().indexOf("windows") >= 0) {
                        operatingSystem = "Windows";
                    } else if (userAgent1.toLowerCase().indexOf("mac") >= 0) {
                        operatingSystem = "Mac";
                    } else if (userAgent1.toLowerCase().indexOf("x11") >= 0) {
                        operatingSystem = "Unix";
                    } else if (userAgent1.toLowerCase().indexOf("android") >= 0) {
                        operatingSystem = "Android";
                    } else if (userAgent1.toLowerCase().indexOf("iphone") >= 0) {
                        operatingSystem = "IPhone";
                    } else {
                        operatingSystem = "UnKnown, More-Info: " + userAgent;
                    }

                    GetLocationContoller locationContoller = new GetLocationContoller();
                    GeoIp geoIp = locationContoller.getLocation(ipAddress);
                    String country = "";

                    try {
                        country = geoIp.getCountry();
                    } catch (NullPointerException e) {
                        country = "No country";
                    }

                    int riskValue = callRiskServer(date, ipAddress, country, operatingSystem, browser, browserVersion, authentication.getName());

                    User user = userService.findByEmail(authentication.getName());
                    if (user.getUsingfa() || riskValue >= 2) {
                        httpServletResponse.sendRedirect("/GAlogin");
                    } else {
                        httpServletRequest.getSession().setAttribute("principal_name", authentication.getName());
                        httpServletRequest.getSession().setMaxInactiveInterval(300);
                        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(auth.getAuthorities());
                        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                        Authentication newAuth = new UsernamePasswordAuthenticationToken(auth.getPrincipal(), auth.getCredentials(), authorities);
                        SecurityContextHolder.getContext().setAuthentication(newAuth);
                        httpServletResponse.sendRedirect("/");
                    }
                })
                .and()
                .logout()
                .clearAuthentication(true)
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .permitAll()
                .logoutSuccessHandler((httpServletRequest, httpServletResponse, authentication) -> {
                    httpServletRequest.getSession().invalidate();
                    httpServletResponse.sendRedirect("/login?logout");
                }).and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .maximumSessions(1)
                .expiredUrl("/login?error");
    }

    private int callRiskServer(Date date, String ipAddress, String country, String operatingSystem, String browser, Version browserVersion, String email) {
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
        Transaction body = new Transaction(date, ipAddress, "login", country, operatingSystem, browser, browserVersion, email);
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
        if (risk_result.equals("high risk")) {
            return 3;
        } else if (risk_result.equals("medium risk")) {
            return 2;
        } else if (risk_result.equals("low risk")) {
            return 1;
        }

        return 0;
    }
}