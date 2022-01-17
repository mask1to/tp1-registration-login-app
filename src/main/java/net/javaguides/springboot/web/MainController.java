package net.javaguides.springboot.web;

import eu.bitwalker.useragentutils.UserAgent;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class MainController {

    @GetMapping("/login")
    public String login(HttpServletRequest httpServletRequest) {
        if (httpServletRequest.isUserInRole("ROLE_USER")) {
            return "redirect:/home";
        }
        else if (httpServletRequest.isUserInRole("ROLE_PRE_USER")) {
            return "redirect:/GAlogin";
        }

        return "login";
    }

    @GetMapping("/")
    public String index(HttpServletRequest httpServletRequest) {

        if (httpServletRequest.isUserInRole("ROLE_USER")) {
            return "redirect:/home";
        }

        return "index";
    }

    @GetMapping("/badToken")
    public String badToken() {
        return "badToken";
    }

    @GetMapping("/about-team")
    public String aboutTeam() {
        return "about-team";
    }

    @GetMapping("/home")
    public String home(HttpServletRequest httpServletRequest) {

        if (httpServletRequest.isUserInRole("ROLE_USER")) {
            return "home";
        }

        return "redirect:/";

    }
}
