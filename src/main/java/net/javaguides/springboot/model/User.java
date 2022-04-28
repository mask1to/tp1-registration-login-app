package net.javaguides.springboot.model;

import org.jboss.aerogear.security.otp.api.Base32;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name="users", uniqueConstraints={@UniqueConstraint(columnNames={"email"})})
public class User
{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Size(min=2, max=30, message="Reťazec musí obsahovať minimálne 2 znaky a maximálne 30 znakov")
	@Column(name = "first_name")
	private String firstName;

	@NotNull
	@Size(min=2, max=30, message="Reťazec musí obsahovať minimálne 2 znaky a maximálne 30 znakov")
	@Column(name = "last_name")
	private String lastName;


//	@Column(unique = true)
	@Email(message = "Email is not valid", regexp = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")
	@NotEmpty(message = "Email cannot be empty")
	private String email;

	@Column(name = "usingfa")
	private boolean Usingfa;

	@Column(name = "phone_code")
	private String phoneCode;

	@Size(min=10, max=10, message="Telefónne číslo musí obsahovať 10 znakov")
	@Column(name = "phone_number")
	private String phoneNumber;

	@Column(name = "authy_id")
	private String authyId;

	@NotNull
	private String password;

	@Column(name = "enabled")
	private boolean enabled;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
	private Collection<Role> roles;
	
	public User() {}
	
	public User(String firstName, String lastName, String email, String password, boolean Usingfa, Collection<Role> roles, String phoneCode, String phoneNumber, String authyId) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
		this.roles = roles;
		this.Usingfa=Usingfa;
		this.enabled = true;
		this.phoneCode = phoneCode;
		this.phoneNumber = phoneNumber;
		this.authyId = authyId;
	}


	public User(String email, Set<Role> roles) {
		super();
		this.email = email;
		this.roles = roles;
	}

	public User(String email){
		super();
		this.email=email;
	}
    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public void setUsingfa(boolean isusingfa) {
		this.Usingfa = isusingfa;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getAuthyId() {
		return authyId;
	}

	public void setAuthyId(String authyId) {
		this.authyId = authyId;
	}

	public String getPhoneCode() {
		return phoneCode;
	}

	public void setPhoneCode(String phoneCode) {
		this.phoneCode = phoneCode;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public boolean isUsingfa() {
		return Usingfa;
	}

	public Collection<Role> getRoles() {
		return roles;
	}

	public void setRoles(Collection<Role> roles) {
		this.roles = roles;
	}

	@Override
	public String toString() {
		return "User{" +
				"id=" + id +
				", firstName='" + firstName + '\'' +
				", lastName='" + lastName + '\'' +
				", email='" + email + '\'' +
				", Usingfa=" + Usingfa +
				", phoneCode='" + phoneCode + '\'' +
				", phoneNumber='" + phoneNumber + '\'' +
				", authyId='" + authyId + '\'' +
				", password='" + password + '\'' +
				", enabled=" + enabled +
				", roles=" + roles +
				'}';
	}
}
