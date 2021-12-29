package net.javaguides.springboot.config;


import net.javaguides.springboot.model.User;
import net.javaguides.springboot.service.UserService;
import org.apache.tomcat.util.codec.binary.Base64;
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
import org.springframework.web.client.RestTemplate;

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
                    callRiskServer();

                    httpServletRequest.getSession().setAttribute("principal_name", authentication.getName());
                    httpServletRequest.getSession().setMaxInactiveInterval(300);
                    User user = userService.findByEmail(authentication.getName());
                    if (user.getUsingfa()) {
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

    private void callRiskServer() {
        String url = "https://serene-refuge-96326.herokuapp.com/oauth/token?scope=write&grant_type=password&username=foo&password=foo";
        RestTemplate rt = new RestTemplate();

        String plainCreds = "clientId:abcd";
        byte[] plainCredsBytes = plainCreds.getBytes();
        byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
        String base64Creds = new String(base64CredsBytes);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Creds);

        HttpEntity<String> request = new HttpEntity<String>(headers);
        ResponseEntity<?> response = rt.exchange(url, HttpMethod.POST, request, JSONObject.class);
//        JSONObject account = response.getBody();
        JSONObject jsontoken = (JSONObject) response.getBody();
        String token = (String) jsontoken.get("access_token");
        logger.info(token);

        evaluateRiskServer(token);

    }

    private void evaluateRiskServer(String token) {
        String url = "https://serene-refuge-96326.herokuapp.com/evaluate";
        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + token);
//        JSONObject body = new JSONObject();
//        body.put("lastIP", "192.16");
//        body.put("lastTransaction", "create");
        //  workaround
        String body = "{\"lastIP\":\"129\",\"lastTransaction\":\"login\"}";
        HttpEntity<String> request = new HttpEntity<String>( body.toString() ,headers);
        ResponseEntity<?> response = rt.exchange(url, HttpMethod.GET, request, String.class);
        logger.info((String) response.getBody());


    }
}