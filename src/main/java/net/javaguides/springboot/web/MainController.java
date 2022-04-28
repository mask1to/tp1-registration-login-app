package net.javaguides.springboot.web;

import com.authy.AuthyException;
import com.twilio.Twilio;
import com.twilio.rest.verify.v2.Service;
//import jdk.internal.instrumentation.Logger;
import net.javaguides.springboot.model.Role;
import net.javaguides.springboot.model.User;
import net.javaguides.springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//import javax.xml.ws.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import com.authy.AuthyApiClient;
import com.authy.api.*;
import org.springframework.web.context.request.async.DeferredResult;

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
            return "redirect:/authyLogin";
        }

        return "login";
    }

    @GetMapping("/")
    public String index(HttpServletRequest httpServletRequest) throws AuthyException {

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

    @GetMapping("/all-users")
    public String GetAllUsers(Model model, HttpServletRequest httpServletRequest) {
        if (!httpServletRequest.isUserInRole("ROLE_ADMIN") && !httpServletRequest.isUserInRole("ROLE_USER")) {
            return "redirect:/home";
        }

        List<User> all_users = userService.allUsers();
        List<Role> all_roles = userService.allRoles();
        model.addAttribute("all_users", all_users);
        model.addAttribute("roles", all_roles);
        return "all-users";
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
