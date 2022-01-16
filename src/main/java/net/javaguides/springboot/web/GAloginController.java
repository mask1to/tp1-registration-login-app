package net.javaguides.springboot.web;

import net.javaguides.springboot.model.User;
import net.javaguides.springboot.repository.VerificationTokenRepository;
import net.javaguides.springboot.service.UserService;
import net.javaguides.springboot.web.dto.SecretCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

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

    /*@Scheduled(cron = "${purge.cron.expression}")
    public void purgeExpiredTokens()
    {
        Date now = (Date) Date.from(Instant.now());
        tokenRepository.deleteAllExpiredSince(now);
    }*/
    @GetMapping
    public String showLoginForm() {
        return "GAlogin";
    }

    @PostMapping
    public String loginUser(@ModelAttribute("user") SecretCode secretCode, HttpSession session, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByEmail(auth.getName());

        if (userService.checkcode(user.getSecret_code(), secretCode.getSecret_code())) {

            List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(auth.getAuthorities());
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            Authentication newAuth = new UsernamePasswordAuthenticationToken(auth.getPrincipal(), auth.getCredentials(), authorities);
            SecurityContextHolder.getContext().setAuthentication(newAuth);

            return "redirect:/";
        }
        else{
            redirectAttributes.addFlashAttribute("error", "Wrong code");
            return "redirect:/GAlogin";
        }
    }
}
