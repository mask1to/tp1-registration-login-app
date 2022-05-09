package net.javaguides.springboot.web;

import net.javaguides.springboot.model.User;
import net.javaguides.springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
@EnableAsync
@RequestMapping("/faceRecognition")
public class FaceRecognitionController {

    @Autowired
    ApplicationEventPublisher eventPublisher;

    private UserService userService;


    public FaceRecognitionController(UserService userService) {
        super();
        this.userService = userService;
    }

    @Value( "${faceRecognition.url}" )
    private String faceRecognitionUrl;

    @GetMapping
    public String showFaceRecognition(HttpServletRequest httpServletRequest) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByEmail(auth.getName());

        if (httpServletRequest.isUserInRole("ROLE_PRE_USER")) {
            return "faceRecognition";
        }
        else if (httpServletRequest.isUserInRole("ROLE_USER")) {
            return "redirect:/home";
        }

        return "redirect:/";
    }

    @PostMapping
    public String faceRecognition(RedirectAttributes redirectAttributes, Model model) throws InterruptedException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String uri = faceRecognitionUrl + "check_authentication?username=" + auth.getName();
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(uri, String.class);

        if(result.equals("1")) {
            List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(auth.getAuthorities());
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            Authentication newAuth = new UsernamePasswordAuthenticationToken(auth.getPrincipal(), auth.getCredentials(), authorities);
            SecurityContextHolder.getContext().setAuthentication(newAuth);
            return "redirect:/";
        }
        else {
            redirectAttributes.addFlashAttribute("error", "");
            redirectAttributes.addFlashAttribute("email", auth.getName());
            return "redirect:/faceRecognition";
        }
    }
}
