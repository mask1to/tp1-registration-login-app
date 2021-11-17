package net.javaguides.springboot.service;
import java.sql.Date;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import net.javaguides.springboot.model.TemporaryUser;
import net.javaguides.springboot.model.VerificationToken;
import net.javaguides.springboot.repository.TemporaryUserRepository;
import net.javaguides.springboot.repository.VerificationTokenRepository;
import net.javaguides.springboot.web.dto.UserEmailDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import net.javaguides.springboot.model.Role;
import net.javaguides.springboot.model.User;
import net.javaguides.springboot.repository.UserRepository;
import net.javaguides.springboot.web.dto.UserRegistrationDto;

@Service
public class UserServiceImpl implements UserService {

	private UserRepository userRepository;
	private TemporaryUserRepository temporaryUserRepository;

	@Autowired
	private VerificationTokenRepository tokenRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	public UserServiceImpl(UserRepository userRepository, TemporaryUserRepository temporaryUserRepository) {
		super();
		this.userRepository = userRepository;
		this.temporaryUserRepository = temporaryUserRepository;
	}

	@Override
	public User save(UserRegistrationDto registrationDto) {
		User user=new User(registrationDto.getFirstName(),
							registrationDto.getLastName(), 
							registrationDto.getEmail(),
							passwordEncoder.encode(registrationDto.getPassword()), 
							Arrays.asList(new Role("USER")));
		
		return userRepository.save(user);
	}

	@Override
	public TemporaryUser saveEmail(UserEmailDto userEmailDto) {
		TemporaryUser temporaryUser = new TemporaryUser(userEmailDto.getEmail(), Arrays.asList(new Role("TEMP_USER")));

		return temporaryUserRepository.save(temporaryUser);
	}

	@Override
	public TemporaryUser getTemporaryUser(String verificationToken) {
		TemporaryUser temporaryUser = tokenRepository.findByToken(verificationToken).getTemporaryUser();
		return temporaryUser;
	}

	@Override
	public TemporaryUser getTemporaryUserByMail(String mail)
	{
		TemporaryUser temporaryUser = temporaryUserRepository.findByEmail(mail);
		return temporaryUser;
	}

	@Override
	public void removeByMail(String mail)
	{
		TemporaryUser temporaryUser = temporaryUserRepository.findByEmail(mail);
		temporaryUserRepository.delete(temporaryUser);
	}

	@Override
	public VerificationToken getVerificationToken(String VerificationToken) {
		return tokenRepository.findByToken(VerificationToken);
	}

	@Override
	public void createVerificationToken(TemporaryUser temporaryUser, String token) {
		VerificationToken myToken = new VerificationToken(token, temporaryUser);
		tokenRepository.save(myToken);
	}

	@Override
	public void saveRegisteredUser(TemporaryUser temporaryUser) {
		temporaryUserRepository.save(temporaryUser);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException 
	{
		User user=userRepository.findByEmail(username); 
		
		if(user==null)
		{
			throw new UsernameNotFoundException("Invalid Email or password");
		}
		return new org.springframework.security.core.userdetails.User(user.getEmail(),user.getPassword(),mapRolesToAuthorities(user.getRoles()));
	}

	private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles)
	{
		return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
	}

}
