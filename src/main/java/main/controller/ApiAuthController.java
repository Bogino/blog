package main.controller;

import com.github.cage.Cage;
import com.github.cage.GCage;
import main.api.response.CaptchaImageResponse;
import main.api.response.RegistrationErrorResponse;
import main.api.response.Result;
import main.model.CaptchaCodes;
import main.model.repository.CaptchaRepository;
import main.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Date;
import java.util.Random;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {

    @Autowired
    private final CaptchaRepository captchaRepository;

    @Autowired
    private final UserRepository userRepository;

    public ApiAuthController(CaptchaRepository captchaRepository, UserRepository userRepository) {
        this.captchaRepository = captchaRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/check")
    private ResponseEntity isAuthorized() {
        return new ResponseEntity(false, HttpStatus.OK);
    }

//    @PostMapping("/register")
//    private ResponseEntity register(){
//
//    }

    @GetMapping("/captcha")
    private ResponseEntity getCaptcha() {
        Cage cage = new GCage();
        String code = cage.getTokenGenerator().next();
        String prefix = "data:image/png;base64, ";
        byte[] fileContent = cage.draw(code);
        String encodedString = Base64.getEncoder().encodeToString(fileContent);
        String image = prefix + encodedString;

        byte[] array = new byte[256];

        new Random().nextBytes(array);

        String randomString = new String(array, Charset.forName("UTF-8"));

        StringBuffer sb = new StringBuffer();

        String AlphaNumericString = randomString.replaceAll("[^A-Za-z0-9]", "");

        int n = 20;

        for (int k = 0; k < AlphaNumericString.length(); k++) {

            if (Character.isLetter(AlphaNumericString.charAt(k)) && (n > 0) || Character.isDigit(AlphaNumericString.charAt(k)) && (n > 0)) {

                sb.append(AlphaNumericString.charAt(k));
                n--;
            }
        }

        String secrete = sb.toString();

        CaptchaCodes captcha = new CaptchaCodes();
        captcha.setCode(code);
        captcha.setSecretCode(secrete);
        captcha.setTime(new Date());
        captchaRepository.save(captcha);
        captchaRepository.deleteOldCaptchas();

        CaptchaImageResponse imageResponse = new CaptchaImageResponse(secrete, image);

        return new ResponseEntity(imageResponse, HttpStatus.OK);

    }

    @PostMapping("/register")
    private ResponseEntity register(@RequestParam("e_mail") String eMail,
                                    @RequestParam() String password,
                                    @RequestParam() String name,
                                    @RequestParam() String captcha,
                                    @RequestParam("captcha_secrete") String captchaSecrete) {
        Pattern eMailPattern = Pattern.compile("[a-z0-9]+@[a-z]+\\.[a-z]+");
        Pattern passwordPattern = Pattern.compile(".{6,}");
        Pattern namePattern = Pattern.compile("[a-zA-Zа-яА-Я\\-\\s]+");
        RegistrationErrorResponse errorResponse = new RegistrationErrorResponse();
        Result result = new Result(true);

        if (!eMailPattern.matcher(eMail).matches()) {
            errorResponse.getErrors().put("email", "Некорректное имя e-mail");
        }
        if (userRepository.findByEmail(eMail).isPresent()) {
            errorResponse.getErrors().put("email", "Этот e-mail уже зарегистрирован");
        }
        if (!passwordPattern.matcher(password).matches()) {
            errorResponse.getErrors().put("password", "Пароль короче 6-ти символов");
        }
        if (!namePattern.matcher(name).matches()) {
            errorResponse.getErrors().put("name", "Имя содержит недопустимые символы");
        }

        getCaptcha();

        CaptchaCodes captchaCode = captchaRepository.findBySecretCode(captchaSecrete).get();

        if (!captchaCode.getCode().equals(captcha)) {
            errorResponse.getErrors().put("captcha", "Код с картинки введён неверно");
        }

        if (errorResponse.getErrors().size() > 0) {
            return new ResponseEntity(errorResponse, HttpStatus.OK);
        }

        return new ResponseEntity(result, HttpStatus.OK);


    }
}