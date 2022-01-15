package net.javaguides.springboot.web;

import net.javaguides.springboot.model.TemporaryUser;
import net.javaguides.springboot.model.User;
import net.javaguides.springboot.service.UserService;
import net.javaguides.springboot.web.dto.UserEmailDto;
import net.javaguides.springboot.web.dto.UserRegistrationDto;
import net.javaguides.springboot.web.exceptions.UserAlreadyExistAuthenticationException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.acls.model.AlreadyExistsException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/welcome")
public class UserLandingController
{

    private UserService userService;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    public UserLandingController(UserService userService)
    {
        super();
        this.userService = userService;
    }

    @ModelAttribute("user")
    public UserEmailDto userEmailDto()
    {
        return new UserEmailDto();
    }

    @GetMapping
    public String showRegistrationLanding(HttpSession session)
    {
        if(session.getAttribute("principal_name") != null) {
            return "redirect:/";
        }
        return "welcome";
    }

    @PostMapping
    public String registerUserAccountTemp(@ModelAttribute("user") UserEmailDto userEmailDto, HttpServletRequest request, RedirectAttributes redirectAttributes)
    {
        try {
            TemporaryUser temporaryUser = userService.saveEmail(userEmailDto, redirectAttributes);
            User user = userService.findByEmail(userEmailDto.getEmail());
            if(temporaryUser == null || user != null) {
                redirectAttributes.addFlashAttribute("error", "User already exists");
                return "redirect:/welcome";
            }
            else {
                String appUrl = request.getContextPath();
                eventPublisher.publishEvent(new OnRegistrationCompleteEvent(temporaryUser,
                        request.getLocale(), appUrl));
            }
        }
        catch(DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("error", "User already exists");
            return "redirect:/welcome";
        }
        redirectAttributes.addFlashAttribute("success", "Email has been sent.");
        return "redirect:/";
    }
}
