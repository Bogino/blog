package main.config;

import main.model.repository.CaptchaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class SpringConfig {

    @Autowired
    CaptchaRepository captchaRepository;

    @Scheduled(fixedDelayString = "${blog.captchaLifeTimeInHours}")
    public void scheduleFixedDeleteCaptchas() {
        captchaRepository.deleteOldCaptchas();
    }


}
