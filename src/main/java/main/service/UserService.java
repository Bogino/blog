package main.service;

import com.github.cage.Cage;
import com.github.cage.GCage;
import lombok.Data;
import main.api.mail.MyConstants;
import main.api.request.ChangePasswordRequest;
import main.api.request.EmailRestoreRequest;
import main.api.request.LoginRequest;
import main.api.request.RegisterRequest;
import main.api.response.*;
import main.model.CaptchaCodes;
import main.model.Post;
import main.model.User;
import main.model.repository.CaptchaRepository;
import main.model.repository.PostRepository;
import main.model.repository.UserRepository;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.*;
import java.util.regex.Pattern;

@Service
@Data
public class UserService {

    private final UserRepository userRepository;

    private final CaptchaRepository captchaRepository;

    private final PostRepository postRepository;

    private final PasswordEncoder passwordEncoder;

    public final JavaMailSender emailSender;

    private final AuthenticationManager authenticationManager;

    @Value("${blog.upload.folder}")
    private String path;

    @Autowired
    public UserService(UserRepository userRepository, CaptchaRepository captchaRepository,
                       PostRepository postRepository, PasswordEncoder passwordEncoder, JavaMailSender emailSender, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;

        this.captchaRepository = captchaRepository;

        this.postRepository = postRepository;

        this.passwordEncoder = passwordEncoder;

        this.emailSender = emailSender;

        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public LoginResponse getLoginResponse(String email) {
        main.model.User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));

        UserLoginResponse userResponse = new UserLoginResponse();

        userResponse.setEmail(currentUser.getEmail());

        userResponse.setPhoto(currentUser.getPhoto());

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

        String shortCode = code.substring(0,code.length()/2);

        String prefix = "data:image/png;base64, ";

        byte[] fileContent = cage.draw(shortCode);

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

        captcha.setCode(shortCode);

        captcha.setSecretCode(secret);

        captcha.setTime(new Date());

        captchaRepository.save(captcha);

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
    public LoginResponse login(LoginRequest loginRequest) {

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(auth);

        org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) auth.getPrincipal();

        return getLoginResponse(user.getUsername());


    }

    @Transactional
    public Result restore(EmailRestoreRequest request) {

        Result result = new Result(true);

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(MyConstants.MY_EMAIL);

        message.setTo(userRepository.findByEmail(request.getEmail()).orElseThrow(()->new UsernameNotFoundException("Пользователь не найден")).getEmail());

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

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

        return new Result(true);

    }


    @Transactional
    public Result updateProfile(String email,
                                String name,
                                String password,
                                int removePhoto) {

        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();

        if (email != null) {

            user.setEmail(email);

        }

        if (name != null) {

            user.setName(name);

        }

        if (password != null) {

            user.setPassword(passwordEncoder.encode(password));

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
                                   int removePhoto) {

        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();

        BufferedImage image;

        try {

            image = ImageIO.read(photo.getInputStream());

        } catch (IOException e) {

            ErrorResponse imageUploadErrorResponse = new ErrorResponse(false, new HashMap<>());

            imageUploadErrorResponse.getErrors().put("image", "Неверный формат файла");

            return imageUploadErrorResponse;

        }

        image = cropImage(image);

        BufferedImage scaledImage = Scalr.resize(image, 36, 36);

        user.setPhoto(uploadAvatar(scaledImage, photo.getOriginalFilename()));

        if (email != null) {

            user.setEmail(email);

        }

        if (name != null) {

            user.setName(name);

        }

        if (password != null) {

            user.setPassword(passwordEncoder.encode(password));

        }

        if (removePhoto != 0) {

            user.setPhoto(null);

        }

        userRepository.save(user);

        return new Result(true);

    }

    private String uploadAvatar(BufferedImage image, String fileName) {

        String path = null;

        try {


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

            path = "/" + this.path + sb.substring(0, 2) + "/" + sb.substring(2, 4) + "/" + sb.substring(4, 6) + "/" + fileName;

            File currDir = new File(this.path + sb.substring(0, 2) + "/" + sb.substring(2, 4) + "/" + sb.substring(4, 6) + "/" + fileName);

            currDir.mkdirs();

            ImageIO.write(image, "jpg", currDir);


        } catch (IOException e) {

            e.printStackTrace();

        }

        return path;
    }

    public BufferedImage cropImage(BufferedImage myImage) {

        int minSize = myImage.getHeight() > myImage.getWidth() ? myImage.getWidth() : myImage.getHeight();

        Rectangle rect = new Rectangle(minSize, minSize);

        BufferedImage newImage = myImage.getSubimage(0, 0, rect.height, rect.width);

        return newImage;
    }

    public String uploadImage(MultipartFile image) {

        String path = null;

        try {


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

            File currDir = new File(this.path + sb.substring(0, 2) + "/" + sb.substring(2, 4) + "/" + sb.substring(4, 6) + "/" + image.getOriginalFilename());

            currDir.mkdirs();

            ImageIO.write(ImageIO.read(image.getInputStream()), "jpg", currDir);

            path = currDir.getPath();


        } catch (IOException e) {
            e.printStackTrace();
        }

        return path;
    }
}
