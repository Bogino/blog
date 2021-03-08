package main.controller;

import main.api.request.AddCommentRequest;
import main.api.request.EditProfileRequest;
import main.api.request.PostModerationRequest;
import main.api.request.SettingsRequest;
import main.api.response.*;
import main.exception.NotFoundParentCommentException;
import main.model.*;
import main.model.repository.*;
import main.service.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/api")
public class ApiGeneralController {


    public final SettingsService settingsService;

    private final InitResponse initResponse;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final TagRepository tagRepository;

    @Autowired
    private final PostRepository postRepository;

    @Autowired
    private final PostVoteRepository postVoteRepository;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final PostCommentRepository postCommentRepository;


    public ApiGeneralController(SettingsService settingsService, InitResponse initResponse, PasswordEncoder passwordEncoder, TagRepository tagRepository, PostRepository postRepository, PostVoteRepository postVoteRepository, UserRepository userRepository, PostCommentRepository postCommentRepository) {
        this.settingsService = settingsService;
        this.initResponse = initResponse;
        this.passwordEncoder = passwordEncoder;
        this.tagRepository = tagRepository;
        this.postRepository = postRepository;
        this.postVoteRepository = postVoteRepository;
        this.userRepository = userRepository;
        this.postCommentRepository = postCommentRepository;
    }

    @GetMapping("/init")
    public InitResponse init() {
        return initResponse;
    }

    @GetMapping("/settings")
    public ResponseEntity<SettingsResponse> settings() {
        return new ResponseEntity<>(settingsService.getGlobalSettings(), HttpStatus.OK);
    }

    @GetMapping("/tag")
    public ResponseEntity getTags() {

        List<Tag> tags = tagRepository.findAll();
        double totalPosts = postRepository.getAllAcceptedPosts().size();
        double countMaxPostsByTag = postRepository.getCountPostsByTagName(tagRepository.getTagWithMaxPostsCount().getName());
        double weight = countMaxPostsByTag / totalPosts;
        double factor = 1.0 / weight;

        ApiTagResponse apiTagResponse = new ApiTagResponse();

        for (Tag tag : tags) {

            apiTagResponse.addTag(tag.getName(), postRepository.getCountPostsByTagName(tag.getName()) / totalPosts * factor);

        }

        return new ResponseEntity(apiTagResponse, HttpStatus.OK);
    }

    @RequestMapping(
            value = "/tag",
            params = "query",
            method = GET)
    public ResponseEntity getTagsByQuery(String query) {

        List<Tag> tags = tagRepository.findByNameContaining(query);

        double totalPosts = postRepository.getAllAcceptedPosts().size();
        double countMaxPostsByTag = postRepository.getCountPostsByTagName(tagRepository.getTagWithMaxPostsCount().getName());
        double weight = countMaxPostsByTag / totalPosts;
        double factor = 1.0 / weight;

        ApiTagResponse apiTagResponse = new ApiTagResponse();

        for (Tag tag : tags) {

            apiTagResponse.addTag(tag.getName(), postRepository.getCountPostsByTagName(tag.getName()) / totalPosts * factor);

        }
        return new ResponseEntity(apiTagResponse, HttpStatus.OK);
    }

    @PostMapping(value = "/image")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<String> uploadFile(MultipartFile file) {

        String path = null;
        try {
            ImageIO.read(file.getResource().getFile());
        } catch (IOException e) {
            ErrorResponse imageUploadErrorResponse = new ErrorResponse(false, new HashMap<>());
            imageUploadErrorResponse.getErrors().put("image", "Неверный формат файла");
            return new ResponseEntity(imageUploadErrorResponse, HttpStatus.BAD_REQUEST);
        }

        InputStream in = null;
        try {
            in = file.getInputStream();

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
            path = currDir.getAbsolutePath();
            FileOutputStream f = new FileOutputStream(
                    path + "/" + file.getOriginalFilename());
            int ch;
            while ((ch = in.read()) != -1) {
                f.write(ch);
            }

            f.flush();
            f.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok(path);

    }

    @Bean
    public MultipartResolver multipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setMaxUploadSize(5242880);
        return multipartResolver;
    }


    @RequestMapping(
            value = "/calendar",
            params = "year",
            method = GET)
    public ResponseEntity getCountPostsByYear(String year) {

        ApiCalendarResponse apiCalendarResponse = new ApiCalendarResponse();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (Integer y : postRepository.getYearsOfPosts()) {
            apiCalendarResponse.getYears().add(y);
        }

        for (Date date : postRepository.getDatesByYear(year)) {
            apiCalendarResponse.getPosts().put(dateFormat.format(date), postRepository.getCountPostsByDate(date));
        }

        return new ResponseEntity(apiCalendarResponse, HttpStatus.OK);
    }

    @GetMapping("/calendar")
    public ResponseEntity getCountPostsWithDates() {

        ApiCalendarResponse apiCalendarResponse = new ApiCalendarResponse();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (Integer y : postRepository.getYearsOfPosts()) {
            apiCalendarResponse.getYears().add(y);
        }

        for (Date date : postRepository.getDates()) {
            apiCalendarResponse.getPosts().put(dateFormat.format(date), postRepository.getCountPostsByDate(date));
        }

        return new ResponseEntity(apiCalendarResponse, HttpStatus.OK);
    }

    @GetMapping("statistics/all")
    public ResponseEntity getStatistic() {

        int postsCount = postRepository.getAllAcceptedPosts().size();

        ArrayList<Post> posts = postRepository.getAllAcceptedPosts();

        int likesCount = 0;
        int dislikesCount = 0;
        int viewsCount = 0;

        for (Post p : posts) {

            viewsCount += p.getViewCount();

            List<PostVote> postVotes = postVoteRepository.findByPost(p);

            for (PostVote pv : postVotes) {
                if (pv.getValue() < 0) {
                    dislikesCount++;
                } else likesCount++;
            }
        }

        Page<Post> earlyPosts = postRepository.getActivePosts("ACCEPTED", PageRequest.of(0, 1, Sort.by("time").ascending()));

        long firstPublication = earlyPosts.iterator().next().getTime().getTime() / 1000;

        ApiStatisticResponse apiStatisticResponse = new ApiStatisticResponse(postsCount, likesCount, dislikesCount, viewsCount, firstPublication);

        return new ResponseEntity(apiStatisticResponse, HttpStatus.OK);
    }

    @PostMapping("/comment")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity addComment(@RequestBody AddCommentRequest request) {

        if (request.getText() == null) {
            ErrorResponse response = new ErrorResponse(false, new HashMap<>());
            response.getErrors().put("text", "Текст не задан");
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }

        PostComment comment = new PostComment();

        if (request.getParentId() != 0) {
            try {
                int parentId = postRepository.findByIdAcceptedPost(request.getParentId()).orElseThrow(() -> new NotFoundParentCommentException()).getId();
                comment.setParentId(parentId);
            } catch (NotFoundParentCommentException e) {

                return (ResponseEntity) ResponseEntity.badRequest();
            }
        }

        comment.setTime(new Date());
        comment.setUserId(userRepository.findById(postRepository.findUserIdByPostId(request.getPostId())).orElseThrow());
        comment.setText(request.getText());
        comment.setPostId(postRepository.findByIdAcceptedPost(request.getPostId()).orElseThrow());
        postCommentRepository.save(comment);


        return ResponseEntity.ok(comment.getId());
    }

    @PostMapping("/moderation")
    @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity moderate(@RequestBody PostModerationRequest request) {


        Result result = new Result(true);
        Post post = postRepository.findById(request.getPostId()).orElseThrow();
        if (request.getDecision().equals("accept"))
            post.setStatus(ModerationStatus.ACCEPTED);
        else post.setStatus(ModerationStatus.DECLINED);

        postRepository.save(post);

        return ResponseEntity.ok(result);

    }

//    @PostMapping("/profile/my")
//    @PreAuthorize("hasAuthority('user:write')")
//    public ResponseEntity editProfile(@RequestBody EditProfileRequest request) {
//
//
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
//            return new ResponseEntity(errorResponse, HttpStatus.OK);
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
//        return new ResponseEntity(result, HttpStatus.OK);
//
//    }

//    @PutMapping("/settings")
//    @PreAuthorize("hasAuthority('user:moderate')")
//    public ResponseEntity saveSettings(@RequestBody SettingsRequest request){
//
//        GlobalSettings globalSettings = new GlobalSettings();
//        globalSettings.setCode();
//
//    }





}
