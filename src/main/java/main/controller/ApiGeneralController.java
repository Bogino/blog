package main.controller;

import main.api.request.AddCommentRequest;
import main.api.request.EditProfileRequest;
import main.api.request.PostModerationRequest;
import main.api.response.ErrorResponse;
import main.api.response.InitResponse;
import main.api.response.SettingsResponse;
import main.exception.NotFoundParentCommentException;
import main.exception.NullPointerCommentTextException;
import main.service.PostService;
import main.service.SettingsService;
import main.service.TagService;
import org.springframework.context.annotation.Bean;
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
import java.util.HashMap;
import java.util.Random;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/api")
public class ApiGeneralController {


    private final SettingsService settingsService;

    private final TagService tagService;

    private final InitResponse initResponse;

    private final PasswordEncoder passwordEncoder;

    private final PostService postService;


    public ApiGeneralController(SettingsService settingsService, TagService tagService, InitResponse initResponse, PasswordEncoder passwordEncoder, PostService postService) {
        this.settingsService = settingsService;
        this.tagService = tagService;
        this.initResponse = initResponse;
        this.passwordEncoder = passwordEncoder;
        this.postService = postService;
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

        return new ResponseEntity(tagService.getTags(), HttpStatus.OK);
    }


    @RequestMapping(
            value = "/tag",
            params = "query",
            method = GET)
    public ResponseEntity getTagsByQuery(String query) {

        return new ResponseEntity(tagService.getTagsByQuery(query), HttpStatus.OK);
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


        return new ResponseEntity(postService.getCountPostsByYear(year), HttpStatus.OK);
    }

    @GetMapping("/calendar")
    public ResponseEntity getCountPostsForCurrentYear() {

        return ResponseEntity.ok(postService.getCountPostsForCurrentYear());
    }

    @GetMapping("statistics/all")
    public ResponseEntity getStatistic() {

        return new ResponseEntity(postService.getStatistic(), HttpStatus.OK);
    }

    @PostMapping("/comment")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity addComment(@RequestBody AddCommentRequest request) {

        int commentId;

        try {
            commentId = postService.addComment(request);
        } catch (NullPointerCommentTextException e) {

            ErrorResponse response = new ErrorResponse(false, new HashMap<>());
            response.getErrors().put("text", "Текст не задан");
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        } catch (NotFoundParentCommentException e) {

            return (ResponseEntity) ResponseEntity.badRequest();

        }


        return ResponseEntity.ok(commentId);
    }

    @PostMapping("/moderation")
    @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity moderate(@RequestBody PostModerationRequest request) {


        return ResponseEntity.ok(postService.moderate(request));

    }

//    @PostMapping("/profile/my")
//    @PreAuthorize("hasAuthority('user:write')")
//    public ResponseEntity editProfile(@RequestBody EditProfileRequest request) {
//
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
