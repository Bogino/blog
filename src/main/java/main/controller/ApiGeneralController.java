package main.controller;

import main.api.request.AddCommentRequest;
import main.api.request.PostModerationRequest;
import main.api.request.ProfileRequest;
import main.api.request.SettingsRequest;
import main.api.response.*;
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
        return new ResponseEntity<>(settingsService.getSettingsResponse(), HttpStatus.OK);
    }

    @PutMapping("/settings")
    @PreAuthorize("hasAuthority('user:moderate')")
    public void setSettings(@RequestBody SettingsRequest request) {

        settingsService.setGlobalSettings(request.isMultiuserMode(), request.isPostPremoderation(), request.isStatisticsIsPublic());

    }

    @GetMapping("/tag")
    public ResponseEntity<ApiTagResponse> getTags() {

        return new ResponseEntity(tagService.getTags(), HttpStatus.OK);
    }


    @RequestMapping(
            value = "/tag",
            params = "query",
            method = GET)
    public ResponseEntity<ApiTagResponse> getTagsByQuery(String query) {

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
    public ResponseEntity<ApiCalendarResponse> getCountPostsByYear(String year) {


        return new ResponseEntity(postService.getCountPostsByYear(year), HttpStatus.OK);
    }

    @GetMapping("/calendar")
    public ResponseEntity<ApiCalendarResponse> getCountPostsForCurrentYear() {

        return ResponseEntity.ok(postService.getCountPostsForCurrentYear());
    }

    @GetMapping("statistics/all")
    public ResponseEntity<ApiStatisticResponse> getStatistic() {

        if (!settingsService.getSettingsResponse().isStatisticsIsPublic()) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        } else {
            return new ResponseEntity(postService.getStatistic(), HttpStatus.OK);
        }
    }

    @GetMapping("statistics/my")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<ApiStatisticResponse> getMyStatistic() {

        return new ResponseEntity(postService.getMyStatistic(), HttpStatus.OK);
    }

    @PostMapping("/comment")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<ApiCommentResponse> addComment(@RequestBody AddCommentRequest request) {

        return ResponseEntity.ok(postService.addComment(request));

    }

    @PostMapping("/moderation")
    @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity<Result> moderate(@RequestBody PostModerationRequest request) {

        return ResponseEntity.ok(postService.moderate(request));

    }

    @PostMapping(value = "/profile/my", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<Result> updateProfile(@RequestBody ProfileRequest request) {
        return ResponseEntity.ok(userService.updateProfile(request.getEmail(), request.getName(),
                request.getPassword(), request.getRemovePhoto()));

    }

    @PostMapping(value = "/profile/my", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<Result> updateProfileWithPhoto(
            @RequestParam() MultipartFile photo,
            @RequestParam(required = false) int removePhoto,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String password
    ) {
        return ResponseEntity.ok(userService.getPostProfileMy(photo, email, name, password, removePhoto));
    }
}
