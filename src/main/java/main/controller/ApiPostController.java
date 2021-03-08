package main.controller;

import main.api.request.AddPostRequest;
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
import java.util.List;

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

        ApiPostResponseById postResponse;
        try {
            postResponse = postService.getPostsById(id);
        } catch (PostNotFoundException e) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(postResponse);
    }


    @PostMapping()
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity addPost(@RequestBody AddPostRequest request) {

        ErrorResponse response = new ErrorResponse(false, new HashMap<>());

        if (request.getTitle() == null) {
            response.getErrors().put("title", "Заголовок отсутствует");
        }
        if (request.getTitle().length() < 3) {
            response.getErrors().put("title", "Заголовок слишком короткий");
        }
        if (request.getText().length() < 50) {
            response.getErrors().put("text", "Текс публикации слишком короткий");
        }

        if(response.getErrors().size() > 0){
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.ok(postService.addPost(request.getTimestamp(),
                request.getActive(), request.getTitle(), request.getTags(), request.getText()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity editSomeData(
            @PathVariable int id,
            @RequestParam() long timestamp,
            @RequestParam() int active,
            @RequestParam() String title,
            @RequestParam() List<String> tags,
            @RequestParam() String text) {

        ErrorResponse response = new ErrorResponse(false, new HashMap<>());

        if (title == null) {
            response.getErrors().put("title", "Заголовок отсутствует");
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }
        if (title.length() < 3) {
            response.getErrors().put("title", "Заголовок слишком короткий");
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }
        if (text.length() < 50) {
            response.getErrors().put("text", "Текс публикации слишком короткий");
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(postService.editPost(id, timestamp, active, title, tags, text));

    }

    @PostMapping("/like")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity likePost(@RequestParam("post_id") int postId) {

        return ResponseEntity.ok(postService.likePost(postId));
    }

    @PostMapping("/dislike")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity dislikePost(@RequestParam("post_id") int postId) {

        return ResponseEntity.ok(new Result(true));
    }


}
