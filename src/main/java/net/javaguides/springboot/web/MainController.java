package net.javaguides.springboot.web;

import eu.bitwalker.useragentutils.UserAgent;
import jdk.nashorn.internal.runtime.options.OptionTemplate;
import net.javaguides.springboot.model.User;
import net.javaguides.springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class MainController {

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    UserService userService;

    @GetMapping("/login")
    public String login(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByEmail(auth.getName());

        if (httpServletRequest.isUserInRole("ROLE_USER")) {
            return "redirect:/home";
        }
        else if (httpServletRequest.isUserInRole("ROLE_PRE_USER") && user.getUsingfa()) {
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

    @GetMapping("/project-presentation")
    public String projectPresentation() {
        return "project-presentation";
    }

    @GetMapping("/home")
    public String home(HttpServletRequest httpServletRequest) {

        if (httpServletRequest.isUserInRole("ROLE_USER")) {
            return "home";
        }

        return "redirect:/";

    }

    @RequestMapping(value="/download/text", method= RequestMethod.GET)
    public void downloadFile(HttpServletResponse httpServletResponse) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:text_files/tp1.pdf");
        if (resource.exists()) {
            httpServletResponse.setContentType("application/pdf");
            httpServletResponse.setHeader("Content-Disposition",
                    String.format("attachment; filename=" +
                            resource.getFilename()));
            httpServletResponse.setContentLength((int) resource.contentLength());
            InputStream inputStream = resource.getInputStream();
            FileCopyUtils.copy(inputStream, httpServletResponse.getOutputStream());
        }
    }

    @RequestMapping(value="/download/sprint", method= RequestMethod.GET)
    public void downloadSprint(HttpServletResponse httpServletResponse) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:text_files/sprint.pdf");
        if (resource.exists()) {
            httpServletResponse.setContentType("application/pdf");
            httpServletResponse.setHeader("Content-Disposition",
                    String.format("attachment; filename=" +
                            resource.getFilename()));
            httpServletResponse.setContentLength((int) resource.contentLength());
            InputStream inputStream = resource.getInputStream();
            FileCopyUtils.copy(inputStream, httpServletResponse.getOutputStream());
        }
    }
}
