package main.controller;

import main.api.response.ApiCalendarResponse;
import main.api.response.ApiTagResponse;
import main.api.response.InitResponse;
import main.api.response.SettingsResponse;
import main.model.PostRepository;
import main.model.Tag;
import main.model.TagRepository;
import main.service.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/api")
public class ApiGeneralController {

    private final SettingsService settingsService;
    private final InitResponse initResponse;

    @Autowired
    private final TagRepository tagRepository;

    @Autowired
    private final PostRepository postRepository;

    public ApiGeneralController(SettingsService settingsService, InitResponse initResponse, TagRepository tagRepository, PostRepository postRepository) {
        this.settingsService = settingsService;
        this.initResponse = initResponse;
        this.tagRepository = tagRepository;
        this.postRepository = postRepository;
    }

    @GetMapping("/init")
    private InitResponse init() {
        return initResponse;
    }

    @GetMapping("/settings")
    private ResponseEntity<SettingsResponse> settings() {
        return new ResponseEntity<>(settingsService.getGlobalSettings(), HttpStatus.OK);
    }

    @GetMapping("/tag")
    private ResponseEntity<List> tags() {

        List<Tag> tags = tagRepository.findAll();
        double totalPosts = postRepository.getAllAcceptedPosts().size();
        double countMaxPostsByTag = postRepository.getCountPostsByTagName(tagRepository.getTagWithMaxPostsCount().getName());
        double factor = 1.0 / countMaxPostsByTag / totalPosts;

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
    private ResponseEntity<List> list(String query) {

        List<Tag> tags = tagRepository.findByNameContaining(query);

        double totalPosts = postRepository.getAllAcceptedPosts().size();
        double countMaxPostsByTag = postRepository.getCountPostsByTagName(tagRepository.getTagWithMaxPostsCount().getName());
        double factor = 1.0 / countMaxPostsByTag / totalPosts;

        ApiTagResponse apiTagResponse = new ApiTagResponse();

        for (Tag tag : tags) {

            apiTagResponse.addTag(tag.getName(), postRepository.getCountPostsByTagName(tag.getName()) / totalPosts * factor);

        }
        return new ResponseEntity(apiTagResponse, HttpStatus.OK);
    }

    @PostMapping("/image")
    private ResponseEntity postImage(File file) {

        try {
            ImageIO.read(file);
            Path destination = Paths.get("upload/");
            Files.copy(file.toPath(), destination.resolve(file.toPath().getFileName()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ResponseEntity(file.getPath(), HttpStatus.OK);
    }

    @RequestMapping(
            value = "/calendar",
            params = "year",
            method = GET)
    private ResponseEntity getCountPostsByYear(String year) {

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
    private ResponseEntity getCountPostsWithDates() {

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


}
