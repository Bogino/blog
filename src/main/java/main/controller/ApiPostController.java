package main.controller;

import main.api.response.ApiPostResponse;
import main.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/post")
public class ApiPostController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostVoteRepository postVoteRepository;

    @Autowired
    private PostCommentRepository postCommentRepository;

    private ApiPostResponse apiPostResponse = new ApiPostResponse();

    private long count = 0;
    private int offset = 0;
    private final int limit = 10;


    public ApiPostController(PostRepository postRepository, PostVoteRepository postVoteRepository, PostCommentRepository postCommentRepository) {
        this.postRepository = postRepository;
        this.postVoteRepository = postVoteRepository;
        this.postCommentRepository = postCommentRepository;


    }

    @GetMapping("")
    private ResponseEntity<ApiPostResponse> getPosts(
            @RequestParam(required = false, defaultValue = "recent") String mode,
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "10") int limit) {

        int page = offset / limit;

        if (mode.equals("recent")) {
            Pageable pageWithTenElements = PageRequest.of(page, limit);
            Page<Post> posts = postRepository.getRecentPosts(pageWithTenElements);

            count = posts.getTotalElements();
            for (Post p : posts) {
                int likes = 0;
                int dislikes = 0;
                List<PostVote> postVotes = postVoteRepository.findByPost(p);
                int commentsCount = postCommentRepository.findByUserId(p.getId());
                for (PostVote pv : postVotes) {
                    if (pv.getValue() < 0) {
                        dislikes++;
                    } else likes++;
                }

                apiPostResponse.setPosts(count, p.getId(), p.getTime(), p.getUserId().getId(), p.getUserId().getName(), p.getTitle(), p.getText(), likes, dislikes, commentsCount, p.getViewCount());
            }


            if (posts.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }


            return new ResponseEntity(apiPostResponse, HttpStatus.OK);
        }

        if (mode.equals("early")) {
            Pageable pageWithTenElements = PageRequest.of(page, limit);
            Page<Post> posts = postRepository.getEarlyPosts(pageWithTenElements);

            count = posts.getTotalElements();
            for (Post p : posts) {
                int likes = 0;
                int dislikes = 0;
                List<PostVote> postVotes = postVoteRepository.findByPost(p);
                int commentsCount = postCommentRepository.findByUserId(p.getId());
                for (PostVote pv : postVotes) {
                    if (pv.getValue() < 0) {
                        dislikes++;
                    } else likes++;
                }

                apiPostResponse.setPosts(count, p.getId(), p.getTime(), p.getUserId().getId(), p.getUserId().getName(), p.getTitle(), p.getText(), likes, dislikes, commentsCount, p.getViewCount());
            }


            if (posts.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }


            return new ResponseEntity(apiPostResponse, HttpStatus.OK);
        }

        if (mode.equals("best")) {

            Pageable pageWithTenElements = PageRequest.of(page, limit);
            Page<Post> posts = postRepository.findAll(pageWithTenElements);

            count = posts.getTotalElements();
            for (Post p : posts) {
                int likes = 0;
                int dislikes = 0;
                List<PostVote> postVotes = postVoteRepository.findByPost(p);
                int commentsCount = postCommentRepository.findByUserId(p.getId());
                for (PostVote pv : postVotes) {
                    if (pv.getValue() < 0) {
                        dislikes++;
                    } else likes++;
                }

                apiPostResponse.setPosts(count, p.getId(), p.getTime(), p.getUserId().getId(), p.getUserId().getName(), p.getTitle(), p.getText(), likes, dislikes, commentsCount, p.getViewCount());

            }


            if (posts.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            apiPostResponse.sortPostsByLikes();

            return new ResponseEntity(apiPostResponse, HttpStatus.OK);

        }

        if (mode.equals("popular")) {
            Pageable pageWithTenElements = PageRequest.of(page, limit);
            Page<Post> posts = postRepository.findAll(pageWithTenElements);

            count = posts.getTotalElements();
            for (Post p : posts) {
                int likes = 0;
                int dislikes = 0;
                List<PostVote> postVotes = postVoteRepository.findByPost(p);
                int commentsCount = postCommentRepository.findByUserId(p.getId());
                for (PostVote pv : postVotes) {
                    if (pv.getValue() < 0) {
                        dislikes++;
                    } else likes++;
                }

                apiPostResponse.setPosts(count, p.getId(), p.getTime(), p.getUserId().getId(), p.getUserId().getName(), p.getTitle(), p.getText(), likes, dislikes, commentsCount, p.getViewCount());

            }


            if (posts.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            apiPostResponse.sortPostsByCommentCount();

            return new ResponseEntity(apiPostResponse, HttpStatus.OK);
        }

        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
}
