package net.javaguides.springboot.web;

import net.javaguides.springboot.service.UserService;
import net.javaguides.springboot.web.dto.UserEmailDto;
import net.javaguides.springboot.web.dto.UserRegistrationDto;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/welcome")
public class UserLandingController
{

    private UserService userService;

    public UserLandingController(UserService userService)
    {
        super();
        this.userService = userService;
    }

    @ModelAttribute("user")
    public UserRegistrationDto UserRegistrationDto()
    {
        return new UserRegistrationDto();
    }

    @GetMapping
    public String showRegistrationLanding()
    {
        return "welcome";
    }

    @PostMapping
    public String registerUserAccountTemp(@ModelAttribute("user") UserEmailDto userEmailDto)
    {
        userService.saveEmail(userEmailDto);
        return "redirect:/login?success";
    }
}
