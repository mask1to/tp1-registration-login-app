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
    public String login(HttpSession session) {
        if (session.getAttribute("principal_name") != null) {
            return "redirect:/";
        }

        return "login";
    }

    @GetMapping("/")
    public String home(Model model, HttpSession session, HttpServletRequest request) {

        Date d = new Date(session.getCreationTime());

        model.addAttribute("sessionEx", d);
        model.addAttribute("ipaddr", request.getRemoteAddr());
        UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));

        model.addAttribute("bname", userAgent.getBrowser().getName());
        model.addAttribute("bversion", userAgent.getBrowserVersion());

        String browserDetails = request.getHeader("User-Agent");
        String userAgent1 = browserDetails;
        String os = "";

        if (userAgent1.toLowerCase().indexOf("windows") >= 0) {
            os = "Windows";
        } else if (userAgent1.toLowerCase().indexOf("mac") >= 0) {
            os = "Mac";
        } else if (userAgent1.toLowerCase().indexOf("x11") >= 0) {
            os = "Unix";
        } else if (userAgent1.toLowerCase().indexOf("android") >= 0) {
            os = "Android";
        } else if (userAgent1.toLowerCase().indexOf("iphone") >= 0) {
            os = "IPhone";
        } else {
            os = "UnKnown, More-Info: " + userAgent;
        }


        model.addAttribute("os", os);


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
