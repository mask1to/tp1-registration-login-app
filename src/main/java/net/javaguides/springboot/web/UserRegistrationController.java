package net.javaguides.springboot.web;

import com.authy.AuthyApiClient;
import com.authy.AuthyException;
import com.authy.api.Users;
import eu.bitwalker.useragentutils.UserAgent;
import eu.bitwalker.useragentutils.Version;
import net.javaguides.springboot.model.GeoIp;
import net.javaguides.springboot.model.TemporaryUser;
import net.javaguides.springboot.model.User;
import net.javaguides.springboot.model.VerificationToken;
import net.javaguides.springboot.repository.TemporaryUserRepository;
import net.javaguides.springboot.repository.VerificationTokenRepository;
import net.javaguides.springboot.service.UserService;
import net.javaguides.springboot.web.dto.UserRegistrationDto;
import org.jboss.aerogear.security.otp.api.Base32;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.time.Instant;
import java.util.Calendar;
import java.util.Optional;


@Controller
public class UserRegistrationController {

    @Autowired
    ApplicationEventPublisher eventPublisher;

    private UserService userService;
    private TemporaryUserRepository temporaryUserRepository;
    private VerificationTokenRepository verificationTokenRepository;
    private UserRegistrationDto registrationDto;

    @Value( "${authy.api}" )
    private String API_KEY;

    public UserRegistrationController(UserService userService, TemporaryUserRepository temporaryUserRepository, VerificationTokenRepository verificationTokenRepository) {
        super();
        this.userService = userService;
        this.temporaryUserRepository = temporaryUserRepository;
        this.verificationTokenRepository = verificationTokenRepository;
    }

    @Scheduled(cron = "${purge.cron.expression}")
    public void purgeExpiredTokens() {
        Date now = (Date) Date.from(Instant.now());
        verificationTokenRepository.deleteAllExpiredSince(now);
    }

    @ModelAttribute("user")
    public UserRegistrationDto UserRegistrationDto() {
        return new UserRegistrationDto();
    }

    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    public String showRegistrationForm(HttpServletRequest httpServletRequest, @RequestParam("token") Optional<String> token, Model model) {
        VerificationToken verificationToken = userService.getVerificationToken(token);

        if (httpServletRequest.isUserInRole("ROLE_USER")) {
            return "redirect:/home";
        } else if (verificationToken == null) {
            return "/badToken";
        }

        TemporaryUser temporaryUser = verificationToken.getTemporaryUser();
        Calendar cal = Calendar.getInstance();
        if (temporaryUser.isEnabled() == false) {
            if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
                return "/badToken";
            }

            temporaryUser.setEnabled(true);
            userService.saveRegisteredUser(temporaryUser);
            model.addAttribute("reg", "");
            model.addAttribute("token", verificationToken.getToken());
            model.addAttribute("email", verificationToken.getTemporaryUser().getEmail());
            return null;
        }
        model.addAttribute("reg", "");
        model.addAttribute("token", verificationToken.getToken());
        model.addAttribute("email", verificationToken.getTemporaryUser().getEmail());
        return null;
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST, params = "register")
    public String registerUserAccount(@ModelAttribute("user") UserRegistrationDto registrationDto, RedirectAttributes redirectAttributes, Model model, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {

        User user = userService.findByEmail(registrationDto.getEmail());

        if (user != null) {
            model.addAttribute("error", "User already exists");
            model.addAttribute("token", registrationDto.getToken());
            model.addAttribute("email", registrationDto.getEmail());
            model.addAttribute("reg", "");
            return "/registration";
        }

        if (registrationDto.isUsingfa()) {
            model.addAttribute("gaReg", "");
            this.registrationDto = registrationDto;
            return "/registration";
        } else {
            if (userService.save(registrationDto, null) != null) {
                temporaryUserRepository.deleteTemporaryUserByEmail(registrationDto.getEmail());
                redirectAttributes.addFlashAttribute("success", "Registration was successful. You can log in!");

                java.util.Date date = java.util.Date.from(Instant.now());
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
                //int riskValue = riskServer.callRiskServer(date, ipAddress, country, operatingSystem, browser, browserVersion, registrationDto.getEmail(), "registration");

                /*if(riskValue == 4) {
                    SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler();
                    securityContextLogoutHandler.logout(httpServletRequest, httpServletResponse, null);
                    httpServletResponse.sendRedirect("/?blacklist");
                    return null;
                }*/

                return "redirect:/login";
            } else {
                model.addAttribute("reg", "");
                model.addAttribute("email", registrationDto.getEmail());
                return "/registration";
            }
        }
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST, params = "registerGa")
    public String registerGaAccount(@ModelAttribute("user") UserRegistrationDto registrationDto, RedirectAttributes redirectAttributes, Model model) throws UnsupportedEncodingException, AuthyException {

        AuthyApiClient client = new AuthyApiClient(API_KEY);

        Users users = client.getUsers();
        System.out.println(this.registrationDto.getEmail() + " " + this.registrationDto.getPhoneNumber_phoneCode() + " " + this.registrationDto.getPhoneNumber());
        com.authy.api.User user = users.createUser(this.registrationDto.getEmail(), this.registrationDto.getPhoneNumber(), this.registrationDto.getPhoneNumber_phoneCode());

        if (user.isOk()) {
            userService.save(this.registrationDto, String.valueOf(user.getId()));
            temporaryUserRepository.deleteTemporaryUserByEmail(this.registrationDto.getEmail());
            redirectAttributes.addFlashAttribute("success", "Registration was successful. You can log in!");
            return "redirect:/login";
        } else {
            model.addAttribute("error", user.getError().getMessage());
            model.addAttribute("gaReg", "");
            return "/registration";
        }

    }
}
