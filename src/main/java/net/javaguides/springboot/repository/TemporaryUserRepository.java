package net.javaguides.springboot.repository;

import net.javaguides.springboot.model.TemporaryUser;
import net.javaguides.springboot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemporaryUserRepository extends JpaRepository<TemporaryUser, Long> {

    TemporaryUser findByEmail(String email);
}
