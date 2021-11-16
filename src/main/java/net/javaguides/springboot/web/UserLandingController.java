package net.javaguides.springboot.web;

import net.javaguides.springboot.model.TemporaryUser;
import net.javaguides.springboot.service.UserService;
import net.javaguides.springboot.web.dto.UserEmailDto;
import net.javaguides.springboot.web.dto.UserRegistrationDto;
import net.javaguides.springboot.web.exceptions.UserAlreadyExistAuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

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
    public String showRegistrationLanding()
    {
        return "welcome";
    }

    @PostMapping
    public String registerUserAccountTemp(@ModelAttribute("user") UserEmailDto userEmailDto, HttpServletRequest request)
    {
        try {
            TemporaryUser temporaryUser = userService.saveEmail(userEmailDto);

            String appUrl = request.getContextPath();
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(temporaryUser,
                    request.getLocale(), appUrl));
        }
        catch(UserAlreadyExistAuthenticationException e) {
            return "redirect:/welcome?exist";
        }
        return "redirect:/welcome?success";
    }
}
