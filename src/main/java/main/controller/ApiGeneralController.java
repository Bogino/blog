package main.controller;

import main.api.response.ApiCalendarResponse;
import main.api.response.ApiStatisticResponse;
import main.api.response.ApiTagResponse;
import main.api.response.InitResponse;
import main.model.Post;
import main.model.PostVote;
import main.model.Tag;
import main.model.repository.PostRepository;
import main.model.repository.PostVoteRepository;
import main.model.repository.TagRepository;
import main.service.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

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

    @Autowired
    private final PostVoteRepository postVoteRepository;

    public ApiGeneralController(SettingsService settingsService, InitResponse initResponse, TagRepository tagRepository, PostRepository postRepository, PostVoteRepository postVoteRepository) {
        this.settingsService = settingsService;
        this.initResponse = initResponse;
        this.tagRepository = tagRepository;
        this.postRepository = postRepository;
        this.postVoteRepository = postVoteRepository;
    }

    @GetMapping("/init")
    public InitResponse init() {
        return initResponse;
    }

    @GetMapping("/settings")
    private ResponseEntity settings() {
        return new ResponseEntity(settingsService.getGlobalSettings(), HttpStatus.OK);
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
    public ResponseEntity<String> uploadFile(MultipartFile file) throws IOException {


        InputStream in = file.getInputStream();
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


        File currDir = new File("upload/" + sb.substring(0,2) + "/" + sb.substring(2,4) + "/" + sb.substring(4,6));
        currDir.mkdirs();
        String path = currDir.getAbsolutePath();
        FileOutputStream f = new FileOutputStream(
                path + "/" + file.getOriginalFilename());
        System.out.println(f);
        int ch = 0;
        while ((ch = in.read()) != -1) {
            f.write(ch);
        }

        f.flush();
        f.close();

        return ResponseEntity.ok(path);
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

        return new ResponseEntity (apiStatisticResponse, HttpStatus.OK);
    }

    @Bean(name = "multipartResolver")
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setMaxUploadSize(100000);
        return multipartResolver;
    }
}
