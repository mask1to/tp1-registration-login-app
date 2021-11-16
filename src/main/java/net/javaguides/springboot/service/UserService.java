package net.javaguides.springboot.service;

import net.javaguides.springboot.model.VerificationToken;
import net.javaguides.springboot.web.dto.UserEmailDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import net.javaguides.springboot.model.User;
import net.javaguides.springboot.web.dto.UserRegistrationDto;

public interface UserService extends UserDetailsService {

	User save(UserRegistrationDto registrationDto);
	User saveEmail(UserEmailDto userEmailDto);
	User getUser(String verificationToken);
	VerificationToken getVerificationToken(String VerificationToken);
	void createVerificationToken(User user, String token);
	void saveRegisteredUser(User user);

}
