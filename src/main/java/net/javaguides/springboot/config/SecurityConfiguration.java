package net.javaguides.springboot.config;


import net.javaguides.springboot.model.Transaction;
import net.javaguides.springboot.model.User;
import net.javaguides.springboot.service.UserService;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.HTTP;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    Logger logger = LoggerFactory.getLogger(SecurityConfiguration.class);

    @Autowired
    private UserService userService;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userService);
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
                        "/GAlogin**",
                        "/js/**",
                        "/css/**",
                        "/img/**",
                        "/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .permitAll()
                .successHandler((httpServletRequest, httpServletResponse, authentication) -> {
                    // call na risk server
                    int riskValue = callRiskServer();

                    httpServletRequest.getSession().setAttribute("principal_name", authentication.getName());
                    httpServletRequest.getSession().setMaxInactiveInterval(300);
                    User user = userService.findByEmail(authentication.getName());
                    if (user.getUsingfa() ||  riskValue  >= 2 ) {
                        httpServletResponse.sendRedirect("/GAlogin");
                    }
                    else
                        httpServletResponse.sendRedirect("/");
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

    private int callRiskServer() {
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
        // do body sa budu pridavat parametre v dalsom riadku su dummy data
        String body = "{ " +
                "\"lastIP\":\"192.31\" ," +
                " \"lastTransaction\":\"pls funguj\"" +
                "}";
        HttpEntity<String> entity = new HttpEntity<>( body ,headers);
        String risk_result = null;
        try {
            ResponseEntity<String> responseValue = rt.exchange(url, HttpMethod.POST, entity, String.class);
            risk_result = responseValue.getBody();
            logger.info(risk_result);
        }catch (HttpStatusCodeException e){
            String errorpayload = e.getResponseBodyAsString();
            logger.info(String.valueOf(errorpayload));
            // ako riesit nedostupnost risk servera???
        }
        if (risk_result.equals("high risk")){
            return 3;
        }else if (risk_result.equals("medium risk")){
            return 2;
        }else if(risk_result.equals("low risk")){
            return 1;
        }

        return 0;




    }

    private void evaluateRiskServer(String token) {





    }
}