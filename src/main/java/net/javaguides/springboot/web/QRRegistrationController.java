package net.javaguides.springboot.web;

import net.javaguides.springboot.model.User;
import net.javaguides.springboot.repository.VerificationTokenRepository;
import net.javaguides.springboot.service.UserService;
import net.javaguides.springboot.web.dto.UserRegistrationDto;
import net.javaguides.springboot.web.exceptions.UserAlreadyExistAuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.time.Instant;
import java.util.Objects;
import net.javaguides.springboot.repository.UserRepository;
@Controller
@RequestMapping("/registrationQR")
public class QRRegistrationController {
    @Autowired
    ApplicationEventPublisher eventPublisher;

    private UserService userService;
    private VerificationTokenRepository tokenRepository;


    public QRRegistrationController(UserService userService) {
        super();
        this.userService = userService;
    }

    @Scheduled(cron = "${purge.cron.expression}")
    public void purgeExpiredTokens()
    {
        Date now = (Date) Date.from(Instant.now());
        tokenRepository.deleteAllExpiredSince(now);
    }
    @GetMapping
    public String showRegistrationForm()
    {
        return "registrationQR";
    }
    @PostMapping
    public String registerUserAccount() throws UnsupportedEncodingException {

        return "redirect:/login";
    }
}
