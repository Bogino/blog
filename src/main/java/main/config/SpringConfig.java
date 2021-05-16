package main.config;

import main.model.repository.CaptchaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class SpringConfig {

    @Autowired
    CaptchaRepository captchaRepository;

    @Scheduled(initialDelay = 3_600_000, fixedDelay = 3_600_000)
    public void scheduleFixedDeleteCaptchas() {
        captchaRepository.deleteOldCaptchas();
    }


}
