package main.controller;

import main.api.request.AddCommentRequest;
import main.api.request.PostModerationRequest;
import main.api.request.ProfileRequest;
import main.api.response.ErrorResponse;
import main.api.response.InitResponse;
import main.api.response.Result;
import main.api.response.SettingsResponse;
import main.exception.NullPointerCommentTextException;
import main.service.PostService;
import main.service.SettingsService;
import main.service.TagService;
import main.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import java.util.HashMap;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/api")
public class ApiGeneralController {


    private final SettingsService settingsService;

    private final TagService tagService;

    private final InitResponse initResponse;

    private final PostService postService;

    private final UserService userService;


    public ApiGeneralController(SettingsService settingsService, TagService tagService, InitResponse initResponse, PostService postService, UserService userService) {
        this.settingsService = settingsService;
        this.tagService = tagService;
        this.initResponse = initResponse;
        this.postService = postService;
        this.userService = userService;
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
    public ResponseEntity<String> uploadImage(MultipartFile image) {


        return ResponseEntity.ok("\\" + userService.uploadImage(image));

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
        }


        return ResponseEntity.ok(commentId);
    }

    @PostMapping("/moderation")
    @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity moderate(@RequestBody PostModerationRequest request) {

        return ResponseEntity.ok(postService.moderate(request));

    }

    @PostMapping(value = "/profile/my", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity updateProfile(@RequestBody ProfileRequest request) {
        return ResponseEntity.ok(userService.updateProfile(request.getEmail(), request.getName(),
                request.getPassword(), request.getRemovePhoto()));

    }

    @PostMapping(value = "/profile/my", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<Result> updateProfileWithPhoto(
            @RequestParam() MultipartFile photo,
            @RequestParam(value = "removePhoto", required = false) int removePhoto,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(name = "password", required = false) String password
    ) {
        return ResponseEntity.ok(userService.getPostProfileMy(photo, email, name, password, removePhoto));
    }


//    @PutMapping("/settings")
//    @PreAuthorize("hasAuthority('user:moderate')")
//    public ResponseEntity saveSettings(@RequestBody SettingsRequest request){
//
//        GlobalSettings globalSettings = new GlobalSettings();
//
//
//
//    }


}
