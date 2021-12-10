package net.javaguides.springboot.service;

import net.javaguides.springboot.model.TemporaryUser;
import net.javaguides.springboot.model.VerificationToken;
import net.javaguides.springboot.web.dto.UserEmailDto;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.security.core.userdetails.UserDetailsService;

import net.javaguides.springboot.model.User;
import net.javaguides.springboot.web.dto.UserRegistrationDto;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

public interface UserService extends UserDetailsService {

	User save(UserRegistrationDto registrationDto);
	TemporaryUser saveEmail(UserEmailDto userEmailDto, RedirectAttributes redirectAttributes) throws ConstraintViolationException;
	TemporaryUser getTemporaryUser(String verificationToken);
	TemporaryUser getTemporaryUserByMail(String mail);

	VerificationToken getVerificationToken(Optional<String> VerificationToken);
	void createVerificationToken(TemporaryUser temporaryUser, String token);
	void saveRegisteredUser(TemporaryUser temporaryUser);
	void removeByMail(String mail);
	String generateQRUrl(UserRegistrationDto user) throws UnsupportedEncodingException;
}
