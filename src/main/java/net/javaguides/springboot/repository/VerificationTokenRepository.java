package net.javaguides.springboot.repository;

import net.javaguides.springboot.model.TemporaryUser;
import net.javaguides.springboot.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

public interface VerificationTokenRepository  extends JpaRepository<VerificationToken, Long> {

    VerificationToken findByToken(Optional<String> token);


    @Modifying
    @Query("delete from VerificationToken t where t.expiryDate <= ?1")
    void deleteAllExpiredSince(Date now);

    //VerificationToken findByUser(TemporaryUser temporaryUser);
}
