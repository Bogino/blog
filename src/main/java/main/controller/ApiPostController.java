package main.controller;

import main.api.request.AddPostRequest;
import main.api.request.EditPostRequest;
import main.api.request.VoteRequest;
import main.api.response.ApiPostResponse;
import main.api.response.ApiPostResponseById;
import main.api.response.ErrorResponse;
import main.api.response.Result;
import main.exception.PostNotFoundException;
import main.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/api/post")
public class ApiPostController {


    private final PostService postService;


    public ApiPostController(PostService postService) {
        this.postService = postService;
    }


    @GetMapping("")
    public ResponseEntity getPosts(
            @RequestParam(required = false, defaultValue = "recent") String mode,
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "10") int limit) {

        return ResponseEntity.ok(postService.getPosts(mode, offset, limit));
    }

    @GetMapping("/search")
    public ResponseEntity getPostsByQuery(
            @RequestParam() String query,
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "10") int limit) {

        return new ResponseEntity(postService.getPostsByQuery(query, offset, limit), HttpStatus.OK);
    }

    @GetMapping("/byDate")
    public ResponseEntity getPostsByDate(
            @RequestParam("time") String date,
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "10") int limit) {

        return new ResponseEntity(postService.getPostsByDate(date, offset, limit), HttpStatus.OK);

    }

    @GetMapping("/byTag")
    public ResponseEntity getPostsByTag(
            @RequestParam("tag") String name,
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "10") int limit) {

        return new ResponseEntity(postService.getPostsByTag(name, offset, limit), HttpStatus.OK);

    }

    @GetMapping("/moderation")
    @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity getPostsForModeration(
            @RequestParam() String status,
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "10") int limit) {


        return new ResponseEntity(postService.getPostsForModeration(status, offset, limit), HttpStatus.OK);

    }

    @GetMapping("/my")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity getMyPosts(
            @RequestParam() String status,
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "10") int limit) {

        return new ResponseEntity(postService.getMyPosts(status, offset, limit), HttpStatus.OK);
    }


    @GetMapping("/{id}")
    public ResponseEntity getPost(@PathVariable int id) {

        return ResponseEntity.ok(postService.getPostsById(id));
    }


    @PostMapping()
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity addPost(@RequestBody AddPostRequest request) {


        return ResponseEntity.ok(postService.addPost(request.getTimestamp(),
                request.getActive(), request.getTitle(), request.getTags(), request.getText()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity editPost(
            @PathVariable int id,
            @RequestBody EditPostRequest request) {


        return ResponseEntity.ok(postService.editPost(id, request.getTimestamp(), request.getActive(),
                request.getTitle(), request.getTags(), request.getText()));

    }

    @PostMapping("/like")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity likePost(@RequestBody VoteRequest request) {

        return ResponseEntity.ok(postService.likePost(request.getPostId()));
    }

    @PostMapping("/dislike")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity dislikePost(@RequestBody VoteRequest request) {

        return ResponseEntity.ok(postService.dislikePost(request.getPostId()));
    }


}
