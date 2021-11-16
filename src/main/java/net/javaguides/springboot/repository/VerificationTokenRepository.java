package net.javaguides.springboot.repository;

import net.javaguides.springboot.model.User;
import net.javaguides.springboot.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository
        extends JpaRepository<VerificationToken, Long> {

    VerificationToken findByToken(String token);

    VerificationToken findByUser(User user);
}
