package net.javaguides.springboot.service;

import net.javaguides.springboot.model.TemporaryUser;
import net.javaguides.springboot.model.VerificationToken;
import net.javaguides.springboot.web.dto.UserEmailDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import net.javaguides.springboot.model.User;
import net.javaguides.springboot.web.dto.UserRegistrationDto;

public interface UserService extends UserDetailsService {

	User save(UserRegistrationDto registrationDto);
	TemporaryUser saveEmail(UserEmailDto userEmailDto);
	TemporaryUser getTemporaryUser(String verificationToken);
	VerificationToken getVerificationToken(String VerificationToken);
	void createVerificationToken(TemporaryUser temporaryUser, String token);
	void saveRegisteredUser(TemporaryUser temporaryUser);

}
