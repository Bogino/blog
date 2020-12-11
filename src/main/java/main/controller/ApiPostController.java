package main.controller;

import main.model.Post;
import main.model.PostRepository;
import main.model.PostVote;
import main.model.PostVoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/api/post")
public class ApiPostController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostVoteRepository postVoteRepository;

    private long count;
    private int offset = 0;
    private final int limit = 10;

    public ApiPostController(PostRepository postRepository, PostVoteRepository postVoteRepository) {
        this.postRepository = postRepository;
        this.postVoteRepository = postVoteRepository;

    }

    @RequestMapping(
            params = {"mode", "page"},
            method = GET)
    private ResponseEntity<ArrayList> getPosts(@RequestParam("mode") String mode, @RequestParam("page") int page) {

        if (mode.equals("recent")) {
            Pageable firstPageWithTenElements = PageRequest.of(page, limit, Sort.by("time").descending());
            Page<Post> posts = postRepository.findAll(firstPageWithTenElements);
            count = posts.getTotalElements();

            if (posts.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            ArrayList<Post> postList = (ArrayList<Post>) posts.stream().collect(Collectors.toList());


            return new ResponseEntity(postList.stream().filter(post -> post.getStatus().name().equals("ACCEPTED")).collect(Collectors.toList()), HttpStatus.OK);
        }

        if (mode.equals("early")) {
            Pageable firstPageWithTenElements = PageRequest.of(page, limit, Sort.by("time").ascending());
            Page<Post> posts = postRepository.findAll(firstPageWithTenElements);
            count = posts.getTotalElements();

            if (posts.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            ArrayList<Post> postList = (ArrayList<Post>) posts.stream().collect(Collectors.toList());

            return new ResponseEntity(postList.stream().filter(post -> post.getStatus().name().equals("ACCEPTED")).collect(Collectors.toList()), HttpStatus.OK);
        }

        if (mode.equals("best")) {


        }

        if (mode.equals("popular")) {

        }

        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
}
