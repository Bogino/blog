package main.controller;

import main.model.Post;
import main.model.PostRepository;
import main.model.PostVoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

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
        count = postRepository.count();
    }

    @RequestMapping(
            params = "mode",
            method = GET)
    private ResponseEntity<List> getPosts(@RequestParam("mode") final String mode) {

        if (mode.equals("recent")) {
            Pageable firstPageWithTenElements = PageRequest.of(offset, limit, Sort.by("time").descending());

            ArrayList<Post> posts = new ArrayList<>();
            for (Post post : postRepository.findAll(firstPageWithTenElements)) {
                if (post.getIsActive() == 1 && post.getStatus().name().equals("ACCEPTED"))
                    posts.add(post);
                postVoteRepository.findByPostId(post).stream().forEach(postVote -> System.out.println(post.getTitle() + " - votes: " + postVote.getValue()));
            }
            if (posts.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(posts, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
