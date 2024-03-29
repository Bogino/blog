package main.model.repository;

import main.model.CaptchaCodes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface CaptchaRepository extends JpaRepository<CaptchaCodes, Integer> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM captcha_codes where DATE_ADD(NOW(), INTERVAL -?1 HOUR) > captcha_time", nativeQuery = true)
    void deleteOldCaptchas(int captchaLifeTimeInHour);

    Optional<CaptchaCodes> findBySecretCode(String secretCode);

}
