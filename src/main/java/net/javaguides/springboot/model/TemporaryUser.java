package net.javaguides.springboot.model;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name="temp_users", uniqueConstraints={@UniqueConstraint(columnNames={"email"})})
public class TemporaryUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email")
    private String email;

    @Column(name = "enabled")
    private boolean enabled;

    /*@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "temp_user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))*/
    //private Collection<Role> roles;

    public TemporaryUser() {

    }

    public TemporaryUser(Long id, String email, boolean enabled, Collection<Role> roles) {
        this.id = id;
        this.email = email;
        //this.roles = roles;
        this.enabled = false;
    }

    public TemporaryUser(String email, Collection<Role> roles) {
        super();
        this.email = email;
        //this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /*public Collection<Role> getRoles() {
        return roles;
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = roles;
    }*/
}
