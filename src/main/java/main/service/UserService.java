package main.service;

import com.github.cage.Cage;
import com.github.cage.GCage;
import main.api.request.ChangePasswordRequest;
import main.api.request.EmailRestoreRequest;
import main.api.request.RegisterRequest;
import main.api.response.*;
import main.model.CaptchaCodes;
import main.model.Post;
import main.model.User;
import main.model.repository.CaptchaRepository;
import main.model.repository.PostRepository;
import main.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class UserService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final CaptchaRepository captchaRepository;

    @Autowired
    private final PostRepository postRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public JavaMailSender emailSender;


    public UserService(UserRepository userRepository, CaptchaRepository captchaRepository,
                       PostRepository postRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.captchaRepository = captchaRepository;
        this.postRepository = postRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public LoginResponse getLoginResponse(String email) {
        main.model.User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));

        UserLoginResponse userResponse = new UserLoginResponse();
        userResponse.setEmail(currentUser.getEmail());
        userResponse.setName(currentUser.getName());
        userResponse.setModeration(currentUser.getIsModerator() == 1);
        if (currentUser.getIsModerator() == 1) {
            List<Post> newPosts = postRepository.getNewPosts();
            userResponse.setModerationCount(newPosts.size());
        }
        userResponse.setId(currentUser.getId());


        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setResult(true);
        loginResponse.setUserLoginResponse(userResponse);

        return loginResponse;
    }

    @Transactional
    public CaptchaImageResponse getCaptcha() {

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

        return imageResponse;


    }

    @Transactional
    public Result register(RegisterRequest request) {

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


        CaptchaCodes captchaCode = captchaRepository.findBySecretCode(request.getCaptchaSecret()).orElseThrow();

        if (!captchaCode.getCode().equals(request.getCaptcha())) {
            errorResponse.getErrors().put("captcha", "Код с картинки введён неверно");
        }

        if (errorResponse.getErrors().size() > 0) {
            return errorResponse;
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRegTime(new Date());
        userRepository.save(user);

        return result;

    }

    @Transactional
    public Result restore(EmailRestoreRequest request) {

        Result result = new Result(true);

        // Create a Simple MailMessage.
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(userRepository.findByEmail(request.getEmail()).orElseThrow().getEmail());
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

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        user.setCode(sb.toString());
        userRepository.save(user);
        message.setText("/login/change-password/" + sb.toString());

        // Send Message!
        this.emailSender.send(message);

        return result;

    }


    @Transactional
    public Result changePassword(ChangePasswordRequest request) {

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
            return response;
        }

        user.setPassword(request.getPassword());

        userRepository.save(user);

        return new Result(true);

    }


    @Transactional
    public Result updateProfile(String email,
                                String name,
                                String password,
                                int removePhoto){

        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();

        if (email != null) {
            user.setEmail(email);
        }
        if (name != null) {
            user.setName(name);
        }
        if (password != null) {
            user.setPassword(password);
        }
        if (removePhoto == 1) {
            user.setPhoto(null);
        }
        userRepository.save(user);

        return new Result(true);

    }


    @Transactional
    public Result getPostProfileMy(MultipartFile photo,
                                   String email,
                                   String name,
                                   String password,
                                   String removePhoto) {

        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();

        String path = null;
        try {
            ImageIO.read(photo.getInputStream());
        } catch (IOException e) {
            ErrorResponse imageUploadErrorResponse = new ErrorResponse(false, new HashMap<>());
            imageUploadErrorResponse.getErrors().put("image", "Неверный формат файла");
            return imageUploadErrorResponse;
        }

        InputStream in = null;
        try {
            in = photo.getInputStream();

            byte[] array = new byte[256];

            new Random().nextBytes(array);

            String randomString = new String(array, Charset.forName("UTF-8"));

            StringBuffer sb = new StringBuffer();

            String AlphaNumericString = randomString.replaceAll("[^A-Za-z0-9]", "");

            int n = 6;

            for (int k = 0; k < AlphaNumericString.length(); k++) {

                if (Character.isLetter(AlphaNumericString.charAt(k)) && (n > 0) || Character.isDigit(AlphaNumericString.charAt(k)) && (n > 0)) {

                    sb.append(AlphaNumericString.charAt(k));
                    n--;
                }
            }


            File currDir = new File("upload/" + sb.substring(0, 2) + "/" + sb.substring(2, 4) + "/" + sb.substring(4, 6));
            currDir.mkdirs();
            path = currDir.getPath();
            FileOutputStream f = new FileOutputStream(
                    path + "/" + photo.getOriginalFilename());
            int ch;
            while ((ch = in.read()) != -1) {
                f.write(ch);
            }

            f.flush();
            f.close();

            user.setPhoto(path + "\\" + photo.getOriginalFilename());

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (email != null) {
            user.setEmail(email);
        }
        if (name != null) {
            user.setName(name);
        }
        if (password != null) {
            user.setPassword(password);
        }
        if (removePhoto != null) {
            user.setPhoto(null);
        }
        userRepository.save(user);

        return new Result(true);
    }


//    public Result editProfile(EditProfileRequest request){
//
//        Pattern eMailPattern = Pattern.compile("[a-z0-9]+@[a-z]+\\.[a-z]+");
//        Pattern passwordPattern = Pattern.compile(".{6,}");
//        Pattern namePattern = Pattern.compile("[a-zA-Zа-яА-Я\\-\\s]+");
//        ErrorResponse errorResponse = new ErrorResponse(false, new HashMap<>());
//        Result result = new Result(true);
//
//        if (request.getEmail() != null && !eMailPattern.matcher(request.getEmail()).matches()) {
//            errorResponse.getErrors().put("email", "Некорректное имя e-mail");
//        }
//        if (request.getEmail() != null && userRepository.findByEmail(request.getEmail()).isPresent()) {
//            errorResponse.getErrors().put("email", "Этот e-mail уже зарегистрирован");
//        }
//        if (request.getPassword() != null && !passwordPattern.matcher(request.getPassword()).matches()) {
//            errorResponse.getErrors().put("password", "Пароль короче 6-ти символов");
//        }
//        if (request.getName() != null && !namePattern.matcher(request.getName()).matches()) {
//            errorResponse.getErrors().put("name", "Имя содержит недопустимые символы");
//        }
//
//        if (errorResponse.getErrors().size() > 0) {
//            return errorResponse;
//        }
//
//        if (request.getEmail() != null)
//            user.setEmail(request.getEmail());
//        if (request.getName() != null)
//            user.setName(request.getName());
//        if (request.getPassword() != null)
//            user.setPassword(passwordEncoder.encode(request.getPassword()));
//        if (request.getPhoto() != null)
//            user.setPhoto(uploadFile(request.getPhoto()).getBody());
//        if (request.getRemovePhoto() != 0)
//            user.setPhoto(null);
//
//        userRepository.save(user);
//
//        return result;
//
//    }
}
