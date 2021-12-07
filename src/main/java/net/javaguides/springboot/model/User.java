package net.javaguides.springboot.model;

import org.jboss.aerogear.security.otp.api.Base32;

import java.util.Collection;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name="users", uniqueConstraints={@UniqueConstraint(columnNames={"email"})})
public class User 
{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "first_name")
	private String firstName;
	
	@Column(name = "last_name")
	private String lastName;
	
//	@Column(unique = true)
	private String email;
	private String secret_code= Base32.random();
	@Column(name = "usingfa")
	private boolean Usingfa;
	private String password;

	@Column(name = "enabled")
	private boolean enabled;
	
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(
			name = "users_roles",
			joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
	private Collection<Role> roles;
	
	public User() {}
	
	public User(String firstName, String lastName, String email, String password,String secret_code,boolean Usingfa, Collection<Role> roles) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
		this.roles = roles;
		this.secret_code=secret_code;
		this.Usingfa=Usingfa;
		this.enabled = true;
	}

	public User(String email, Collection<Role> roles) {
		super();
		this.email = email;
		this.roles = roles;
	}

	public User(String email, String secret_code){
		super();
		this.email=email;
		this.secret_code=secret_code;
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

	public String getSecret_code() {
		return secret_code;
	}

	public void setSecret_code(String secret_code) {
		this.secret_code = secret_code;
	}

	public boolean getUsingfa() {
		return Usingfa;
	}

	public void setUsingfa(boolean isusingfa) {
		this.Usingfa = isusingfa;
	}

	public Collection<Role> getRoles() {
		return roles;
	}

	public void setRoles(Collection<Role> roles) {
		this.roles = roles;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email
				+ ", password=" + password + ", secret_code=" + secret_code + ", roles=" + roles + "]";
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
