package main.controller;

import main.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/post")
public class ApiPostController {


    private final PostService postService;

    public ApiPostController(PostService postService) {
        this.postService = postService;
    }


    @GetMapping("")
    @PreAuthorize("hasAuthority('user:write')")
    private ResponseEntity getPosts(
            @RequestParam(required = false, defaultValue = "recent") String mode,
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "10") int limit) {

        return ResponseEntity.ok(postService.getPosts(mode, offset, limit));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('user:moderate')")
    private ResponseEntity getPostsByQuery(
            @RequestParam() String query,
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "10") int limit) {

        return new ResponseEntity(postService.getPostsByQuery(query, offset, limit), HttpStatus.OK);
    }

    @GetMapping("/byDate")
    private ResponseEntity getPostsByDate(
            @RequestParam("time") String date,
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "10") int limit) {

        return new ResponseEntity(postService.getPostsByDate(date, offset, limit), HttpStatus.OK);

    }

    @GetMapping("/byTag")
    private ResponseEntity getPostsByTag(
            @RequestParam("tag") String name,
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "10") int limit) {

        return new ResponseEntity(postService.getPostsByTag(name, offset, limit), HttpStatus.OK);

    }

    @GetMapping("/moderation")
    private ResponseEntity getPostsForModeration(
            @RequestParam() String status,
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "10") int limit) {


        return new ResponseEntity(postService.getPostsForModeration(status, offset, limit), HttpStatus.OK);

    }

    @GetMapping("/my")
    private ResponseEntity getMyPosts(
            @RequestParam() String status,
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "10") int limit) {

        return new ResponseEntity(postService.getMyPosts(status, offset, limit), HttpStatus.OK);
    }


    @GetMapping("/{id}")
    public ResponseEntity getPost(@PathVariable int id) {


        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

//    @PostMapping()
//    private Result addPost(
//            @RequestParam() long timestamp,
//            @RequestParam() int active,
//            @RequestParam() String title,
//            @RequestParam() String tags,
//            @RequestParam() String text) {
//
//        Post post = new Post();
//        Date date = null;
//        Result result = new Result(true);
//        if (Calendar.getInstance().getTime().getTime() / 1000 < timestamp) {
//            date = new Date(timestamp);
//        }
//        if (Calendar.getInstance().getTime().getTime() / 1000 > timestamp) {
//            date = Calendar.getInstance().getTime();
//        }
//        if ((title.length() == 0 || title.length() < 3) && (text.length() == 0 || text.length() < 50)) {
//            throw new RuntimeException("Некорректные данные");
//        }
//        post.setTime(date);
//        post.setIsActive(active);
//        post.setTitle(title);
//        post.setText(text);
//        post.setStatus(ModerationStatus.valueOf("NEW"));
//        postRepository.save(post);
//
//        return result;
//    }

}
