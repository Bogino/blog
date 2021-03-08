package main.controller;

import com.github.cage.Cage;
import com.github.cage.GCage;
import main.api.request.ChangePasswordRequest;
import main.api.request.LoginRequest;
import main.api.request.RegisterRequest;
import main.api.response.*;
import main.model.CaptchaCodes;
import main.model.User;
import main.model.repository.CaptchaRepository;
import main.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.Charset;
import java.security.Principal;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {


    private final AuthenticationManager authenticationManager;
    private final CaptchaRepository captchaRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public JavaMailSender emailSender;

    @Autowired
    public ApiAuthController(AuthenticationManager authenticationManager,
                             CaptchaRepository captchaRepository,
                             UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.captchaRepository = captchaRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(auth);

        org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) auth.getPrincipal();


        return ResponseEntity.ok(getLoginResponse(user.getUsername()));
    }


    @GetMapping("/check")
    public ResponseEntity<LoginResponse> check(Principal principal) {

        if (principal == null)
            return ResponseEntity.ok(new LoginResponse());

        return ResponseEntity.ok(getLoginResponse(principal.getName()));
    }

    public LoginResponse getLoginResponse(String email) {
        main.model.User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));

        UserLoginResponse userResponse = new UserLoginResponse();
        userResponse.setEmail(currentUser.getEmail());
        userResponse.setName(currentUser.getName());
        userResponse.setModeration(currentUser.getIsModerator() == 1);
        userResponse.setId(currentUser.getId());


        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setResult(true);
        loginResponse.setUserLoginResponse(userResponse);

        return loginResponse;
    }


    @GetMapping("/captcha")
    public ResponseEntity getCaptcha() {
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

        String secret = sb.toString();

        CaptchaCodes captcha = new CaptchaCodes();
        captcha.setCode(code);
        captcha.setSecretCode(secret);
        captcha.setTime(new Date());
        captchaRepository.save(captcha);

        //TODO: captchaRepository.deleteOldCaptchas();

        CaptchaImageResponse imageResponse = new CaptchaImageResponse(secret, image);

        return new ResponseEntity(imageResponse, HttpStatus.OK);

    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody RegisterRequest request) {

        Pattern eMailPattern = Pattern.compile("[a-z0-9]+@[a-z]+\\.[a-z]+");
        Pattern passwordPattern = Pattern.compile(".{6,}");
        Pattern namePattern = Pattern.compile("[a-zA-Zа-яА-Я\\-\\s]+");
        ErrorResponse errorResponse = new ErrorResponse(false, new HashMap<>());
        Result result = new Result(true);

        if (!eMailPattern.matcher(request.getEmail()).matches()) {
            errorResponse.getErrors().put("email", "Некорректное имя e-mail");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            errorResponse.getErrors().put("email", "Этот e-mail уже зарегистрирован");
        }
        if (!passwordPattern.matcher(request.getPassword()).matches()) {
            errorResponse.getErrors().put("password", "Пароль короче 6-ти символов");
        }
        if (!namePattern.matcher(request.getName()).matches()) {
            errorResponse.getErrors().put("name", "Имя содержит недопустимые символы");
        }

        if (captchaRepository.findBySecretCode(request.getCaptchaSecret()).isPresent()) {

            CaptchaCodes captchaCode = captchaRepository.findBySecretCode(request.getCaptchaSecret()).get();

            if (!captchaCode.getCode().equals(request.getCaptcha())) {
                errorResponse.getErrors().put("captcha", "Код с картинки введён неверно");
            }

            if (errorResponse.getErrors().size() > 0) {
                return new ResponseEntity(errorResponse, HttpStatus.OK);
            }

            User user = new User();
            user.setEmail(request.getEmail());
            user.setName(request.getName());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRegTime(new Date());
            userRepository.save(user);
            return new ResponseEntity(result, HttpStatus.OK);
        }

        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @ResponseBody
    @PostMapping("/restore")
    public ResponseEntity restore(@RequestParam String email) {

        Result result = new Result(true);

        // Create a Simple MailMessage.
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(userRepository.findByEmail(email).orElseThrow().getEmail());
        message.setSubject("Restoring password");

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

        User user = userRepository.findByEmail(email).orElseThrow();
        user.setCode(sb.toString());
        userRepository.save(user);
        message.setText("/login/change-password/" + sb.toString());

        // Send Message!
        this.emailSender.send(message);

        return ResponseEntity.ok(result);

    }

    @PostMapping("/password")
    public ResponseEntity changePassword(@RequestBody ChangePasswordRequest request) {

        User user = userRepository.findByCode(request.getCode()).orElseThrow();

        CaptchaCodes captcha = captchaRepository.findBySecretCode(request.getCaptchaSecret()).orElseThrow();

        ErrorResponse response = new ErrorResponse(false, new HashMap<>());

        if (!request.getCode().equals(user.getCode())) {

            response.getErrors().put("code", "Ссылка для восстановления пароля устарела.\n" +
                    "<a href=\n" +
                    "\\\"/auth/restore\\\">Запросить ссылку снова</a>");
        }

        if (!captcha.getCode().equals(request.getCode())) {
            response.getErrors().put("captcha", "Пароль с картинки введен неверно");
        }


        if (request.getPassword().length() < 6) {
            response.getErrors().put("password", "Пароль короче 6 символов");
        }

        if (response.getErrors().size() != 0) {
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }

        user.setPassword(request.getPassword());

        userRepository.save(user);

        return ResponseEntity.ok(new Result(true));
    }

}