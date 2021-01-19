package main.model.repository;

import main.model.CaptchaCodes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CaptchaRepository extends JpaRepository<CaptchaCodes, Integer> {

    @Query(value = "DELETE FROM captcha_codes WHERE TIMESTAMPDIFF(HOUR, time, NOW()) > 1", nativeQuery = true)
    void deleteOldCaptchas();

    Optional<CaptchaCodes> findBySecretCode(String secreteCode);

}
