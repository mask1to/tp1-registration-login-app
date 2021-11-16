package net.javaguides.springboot.web;

import net.javaguides.springboot.model.User;
import net.javaguides.springboot.model.VerificationToken;
import net.javaguides.springboot.web.exceptions.UserAlreadyExistAuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import net.javaguides.springboot.service.UserService;
import net.javaguides.springboot.web.dto.UserRegistrationDto;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Locale;

@Controller
@RequestMapping("/registration")
public class UserRegistrationController {

	@Autowired
	ApplicationEventPublisher eventPublisher;

	private UserService userService;
	
	public UserRegistrationController(UserService userService) {
		super();
		this.userService = userService;
	}

	@ModelAttribute("user")
	public UserRegistrationDto UserRegistrationDto()
	{
		return new UserRegistrationDto();
	}
	
	@GetMapping
	public String showRegistrationForm()
	{
		return "registration";
	}
	
	@PostMapping
	public String registerUserAccount(@ModelAttribute("user") UserRegistrationDto registrationDto, HttpServletRequest request)
	{
		try {
			User registered = userService.save(registrationDto);

			String appUrl = request.getContextPath();
			eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered,
					request.getLocale(), appUrl));
		}
		catch(UserAlreadyExistAuthenticationException e) {
			return "redirect:/registration?exist";
		}
		return "redirect:/registration?success";
	}
}
