package net.javaguides.springboot.web.dto;

import org.jboss.aerogear.security.otp.api.Base32;

public class UserRegistrationDto {

	private String firstName;
	private String lastName;
	private String email;
	private String password;
	private boolean Usingfa;
	private String secret_code= Base32.random();;
	public UserRegistrationDto(){}

	public UserRegistrationDto(String firstName, String lastName, String email, String password, String secret_code,boolean Usingfa) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
		this.Usingfa = Usingfa;
		this.secret_code = secret_code;
	}
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	public boolean getUsingfa() {
		return Usingfa;
	}

	public void setUsingfa(boolean Usingfa) {
		this.Usingfa = Usingfa;
	}

	public String getSecret_code() {
		return secret_code;
	}

	public void setSecret_code(String secret_code) {
		this.secret_code = secret_code;
	}
}

