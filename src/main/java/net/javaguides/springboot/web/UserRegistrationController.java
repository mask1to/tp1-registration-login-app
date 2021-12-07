package net.javaguides.springboot.web;

import net.javaguides.springboot.model.TemporaryUser;
import net.javaguides.springboot.model.User;
import net.javaguides.springboot.model.VerificationToken;
import net.javaguides.springboot.repository.TemporaryUserRepository;
import net.javaguides.springboot.repository.VerificationTokenRepository;
import net.javaguides.springboot.web.exceptions.UserAlreadyExistAuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import net.javaguides.springboot.service.UserService;
import net.javaguides.springboot.web.dto.UserRegistrationDto;
import org.springframework.web.context.request.WebRequest;

import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.text.AttributedString;
import java.time.Instant;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.expression.Strings;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/registration")
public class UserRegistrationController {

	@Autowired
	ApplicationEventPublisher eventPublisher;

	private UserService userService;
	private VerificationTokenRepository tokenRepository;


	public UserRegistrationController(UserService userService) {
		super();
		this.userService = userService;
	}

	@Scheduled(cron = "${purge.cron.expression}")
	public void purgeExpiredTokens()
	{
		Date now = (Date) Date.from(Instant.now());
		tokenRepository.deleteAllExpiredSince(now);
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
	public String registerUserAccount(@ModelAttribute("user") UserRegistrationDto registrationDto, RedirectAttributes rm) throws UnsupportedEncodingException {
		try {
			userService.save(registrationDto);
		}
		catch(UserAlreadyExistAuthenticationException e) {
			return "redirect:/registration?exist";
		}
		if (registrationDto.getUsingfa()){
			String QR=userService.generateQRUrl(registrationDto);
			rm.addFlashAttribute("qr",QR);
			return "redirect:/registrationQR";
		}
		else
			return "redirect:/registration?success";
	}
}
