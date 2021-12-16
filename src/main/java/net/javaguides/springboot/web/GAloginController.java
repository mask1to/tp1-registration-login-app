package net.javaguides.springboot.web;

import net.javaguides.springboot.model.User;
import net.javaguides.springboot.repository.VerificationTokenRepository;
import net.javaguides.springboot.service.UserService;
import net.javaguides.springboot.web.dto.SecretCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.Date;
import java.time.Instant;
@Controller
@RequestMapping("/GAlogin")
public class GAloginController {
    @Autowired
    ApplicationEventPublisher eventPublisher;

    private UserService userService;
    private VerificationTokenRepository tokenRepository;


    public GAloginController(UserService userService) {
        super();
        this.userService = userService;
    }
    @ModelAttribute("user")
    public SecretCode SecretCode() {
        return new SecretCode();
    }

    @Scheduled(cron = "${purge.cron.expression}")
    public void purgeExpiredTokens()
    {
        Date now = (Date) Date.from(Instant.now());
        tokenRepository.deleteAllExpiredSince(now);
    }
    @GetMapping
    public String showLoginForm()
    {
        return "GAlogin";
    }
    @PostMapping
    public String loginUser(@ModelAttribute("user") SecretCode secretCode, HttpSession session){
        User user = userService.findByEmail(session.getAttribute("principal_name").toString());
        if (userService.checkcode(user,secretCode.getSecret_code()))
            return "redirect:/";
        else
            return "redirect:/GAlogin?bad";
    }
}
