package net.javaguides.springboot.web;

import net.javaguides.springboot.model.TemporaryUser;
import net.javaguides.springboot.model.User;
import net.javaguides.springboot.model.VerificationToken;
import net.javaguides.springboot.repository.TemporaryUserRepository;
import net.javaguides.springboot.repository.VerificationTokenRepository;
import net.javaguides.springboot.service.AddrService;
import net.javaguides.springboot.web.dto.SecretCode;
import net.javaguides.springboot.web.exceptions.UserAlreadyExistAuthenticationException;
import org.jboss.aerogear.security.otp.api.Base32;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.acls.model.AlreadyExistsException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import net.javaguides.springboot.service.UserService;
import net.javaguides.springboot.web.dto.UserRegistrationDto;
import org.springframework.web.context.request.WebRequest;

import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.text.AttributedString;
import java.time.Instant;
import java.util.Calendar;
import java.util.Locale;
import java.util.Optional;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.expression.Strings;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class UserRegistrationController {

    @Autowired
    ApplicationEventPublisher eventPublisher;

    private UserService userService;
    private TemporaryUserRepository temporaryUserRepository;
    private VerificationTokenRepository verificationTokenRepository;
    private UserRegistrationDto registrationDto;

    private final String secretCode = Base32.random();
    private String qrCode = "";


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
    public String registerUserAccount(@ModelAttribute("user") UserRegistrationDto registrationDto, RedirectAttributes redirectAttributes, Model model) throws UnsupportedEncodingException {

        User user = userService.findByEmail(registrationDto.getEmail());

        if (user != null) {
            model.addAttribute("error", "User already exists");
            return null;
        }

        if (registrationDto.isUsingfa()) {
            registrationDto.setSecret_code(secretCode);
            qrCode = userService.generateQRUrl(registrationDto);
            model.addAttribute("qr", qrCode);
            model.addAttribute("gaReg", "");
            this.registrationDto = registrationDto;
            return null;
        } else {
            if (userService.save(registrationDto) != null) {
                temporaryUserRepository.deleteTemporaryUserByEmail(registrationDto.getEmail());
                redirectAttributes.addFlashAttribute("success", "Registration was successful. You can log in!");
                return "redirect:/login";
            } else {
                model.addAttribute("reg", "");
                model.addAttribute("email", registrationDto.getEmail());
                return null;
            }
        }
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST, params = "registerGa")
    public String registerGaAccount(@ModelAttribute("user") UserRegistrationDto registrationDto, RedirectAttributes redirectAttributes, Model model) throws UnsupportedEncodingException {
        this.registrationDto.setCode(registrationDto.getCode());
        if (userService.checkcode(this.registrationDto.getSecret_code(), this.registrationDto.getCode())) {
            userService.save(this.registrationDto);
            temporaryUserRepository.deleteTemporaryUserByEmail(this.registrationDto.getEmail());
            redirectAttributes.addFlashAttribute("success", "Registration was successful. You can log in!");
            return "redirect:/login";
        } else {
            model.addAttribute("error", "Wrong code");
            model.addAttribute("qr", qrCode);
            model.addAttribute("token", "");
            model.addAttribute("gaReg", "");
            return null;
        }

    }
}
