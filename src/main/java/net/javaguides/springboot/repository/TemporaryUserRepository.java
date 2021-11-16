package net.javaguides.springboot.repository;

import net.javaguides.springboot.model.TemporaryUser;
import net.javaguides.springboot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface TemporaryUserRepository extends JpaRepository<TemporaryUser, Long> {

    TemporaryUser findByEmail(String email);

    @Transactional
    TemporaryUser deleteByEmail(String email);
}
