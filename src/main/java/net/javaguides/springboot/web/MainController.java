package net.javaguides.springboot.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class MainController {

    @GetMapping("/login")
    public String login(HttpSession session) {
        if (session.getAttribute("principal_name") != null) {
            return "redirect:/";
        }

        return "login";
    }

    @GetMapping("/")
    public String home(Model model, HttpSession session) {

        Date d = new Date(session.getCreationTime());

        model.addAttribute("sessionEx", d);

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
}
