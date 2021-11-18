package net.javaguides.springboot.web;

import net.javaguides.springboot.model.TemporaryUser;
import net.javaguides.springboot.model.User;
import net.javaguides.springboot.model.VerificationToken;
import net.javaguides.springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

import java.util.Calendar;
import java.util.Locale;
import java.util.Optional;

@Controller
@RequestMapping("/registrationConfirm")
public class RegistrationConfirmController {

    @Autowired
    private UserService service;

    @Qualifier("messageSource")
    @Autowired
    private MessageSource messages;

    @GetMapping
    public String confirmRegistration
            (WebRequest request, Model model, @RequestParam("token") Optional <String> token) {

        Locale locale = request.getLocale();

        VerificationToken verificationToken = service.getVerificationToken(token);
        if (verificationToken == null) {
            return "/badToken";
        }

        TemporaryUser temporaryUser = verificationToken.getTemporaryUser();
        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            return "/badToken";
        }

        temporaryUser.setEnabled(true);
        service.saveRegisteredUser(temporaryUser);
        return "redirect:/registration";
    }
}
