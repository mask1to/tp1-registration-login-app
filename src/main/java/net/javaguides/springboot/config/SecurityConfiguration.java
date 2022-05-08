package net.javaguides.springboot.config;

import eu.bitwalker.useragentutils.UserAgent;
import eu.bitwalker.useragentutils.Version;
import net.javaguides.springboot.model.GeoIp;
import net.javaguides.springboot.model.User;
import net.javaguides.springboot.service.AddrService;
import net.javaguides.springboot.service.UserService;
import net.javaguides.springboot.web.GetLocationContoller;
import net.javaguides.springboot.web.RiskServerController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
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
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Configuration
@EnableAsync
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserService userService;

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
                        "/welcome",
                        "/registration",
                        "/about-team",
                        "/project-presentation",
                        "/js/**",
                        "/css/**",
                        "/img/**",
                        "/404").permitAll()
                .antMatchers("/all-users/**").access("hasRole('ADMIN')")
                .antMatchers("/authyLogin", "/home", "/authyNotificationLogin").hasRole("PRE_USER")
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

                    RiskServerController riskServer = new RiskServerController();

                    int riskValue = 3; //riskServer.callRiskServer(date, ipAddress, country, operatingSystem, browser, browserVersion, authentication.getName(), "login");

                    User user = userService.findByEmail(authentication.getName());

                    if(riskValue == 4) {
                        SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler();
                        securityContextLogoutHandler.logout(httpServletRequest, httpServletResponse, null);
                        httpServletResponse.sendRedirect("/?blacklist");
                        return;
                    }
                    else if (riskValue == 3 && user.isFaceRecognition() && user.getUsingfa()) {
                        httpServletResponse.sendRedirect("/authyLogin?risk=3");
                    }
                    else if (user.getUsingfa() && riskValue == 2) {
                        httpServletResponse.sendRedirect("/authyLogin?risk=2");
                    }
                    else {
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
                })
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .maximumSessions(1)
                .expiredUrl("/login?error");
    }
}